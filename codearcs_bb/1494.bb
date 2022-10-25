; ID: 1494
; Author: Andres
; Date: 2005-10-19 16:53:06
; Title: Mailman
; Description: Any new email?

;Variables
w = 330
h = 120

Global Version$ = "1.0"
Global Command$ = Lower(CommandLine$())
Global Stream
Global Progress
Global ProgStep
Global MailCount
Global IntervalTimer = CreateTimer(1): PauseTimer IntervalTimer
;End of variables

Global MainWindow = CreateWindow("POP3 Mailman", ClientWidth(Desktop()) / 2 - w / 2, ClientHeight(Desktop()) / 2 - h / 2, w, h, Desktop(), 1)
AppTitle "POP3 Mailman " + Version$
If Instr(Command$, "/hide") HideGadget MainWindow

;Systemtray
ggTrayCreate(QueryObject(MainWindow, 1))
ggTraySetIconFromFile("Icon.ico")
ggTraySetToolTip("POP3 Mailman")
ggTrayShowIcon()
;End of systemtray

;Gadgets
CreateLabel("Server:", 5, 5, 50, 15, MainWindow)
Global ServerField = CreateTextField(5, 20, 100, 20, MainWindow)
CreateLabel("Port:", 110, 5, 50, 15, MainWindow)
Global PortField = CreateTextField(110, 20, 50, 20, MainWindow)
CreateLabel("Username:", 165, 5, 80, 15, MainWindow)
Global UserField = CreateTextField(165, 20, 80, 20, MainWindow)
CreateLabel("Password:", 250, 5, 70, 15, MainWindow)
Global PassField = CreateTextField(250, 20, 70, 20, MainWindow, 1)
CreateLabel("Interval (secs):", 5, 45, 100, 15, MainWindow)
Global IntervalField = CreateTextField(5, 60, 70, 20, MainWindow)
Global HideButton = CreateButton("Hide", ClientWidth(MainWindow) - 120, 60, 55, 20, MainWindow)
Global ProgButton = CreateButton("", ClientWidth(MainWindow) - 60, 60, 55, 20, MainWindow)

If Not FileType("data.dat")
	SetGadgetText PortField, "110"
	SetGadgetText IntervalField, "30"
Else
	rf = ReadFile("data.dat")
		If rf
			MailCount = ReadInt(rf)
			SetGadgetText ServerField, ReadString$(rf)
			SetGadgetText PortField, ReadString$(rf)
			SetGadgetText UserField, ReadString$(rf)
			SetGadgetText PassField, Crypt$(ReadString$(rf))
			SetGadgetText IntervalField, ReadString$(rf)
		EndIf
	CloseFile rf
EndIf
;End of gadgets

If Instr(Command$, "/connect") Progress = True
SetProgress(Progress)

Repeat
	Events()
	
	If Progress
		If TimerTicks(IntervalTimer) => Int(TextFieldText(IntervalField))
			ProgStep = ProgStep Mod 5
			ProgStep = ProgStep + 1
			Select ProgStep
				Case 1
					Connect(TextFieldText(ServerField), TextFieldText(PortField))
				Case 2
					Login(TextFieldText(UserField), TextFieldText(PassField))
				Case 3
					CountMails()
				Case 4
					Disconnect()
				Case 5
					ResetTimer IntervalTimer
			End Select
		EndIf
	EndIf
Forever

Function Events()
	Select WaitEvent(100)
		Case $401
			Select EventSource()
				Case HideButton
					HideGadget MainWindow
				Case ProgButton
					Progress = 1 - Progress
					SetProgress(Progress)
			End Select
		Case $803
			TheEnd()
	End Select
	
	If ggTrayPeekLeftDblClick()
		ShowGadget MainWindow
	EndIf
	ggTrayClearEvents()
End Function

Function Connect(server$, port)
	If Not Stream
		Stream = OpenTCPStream(server$, port)
		If Not Stream
			SetProgress(0)
			Notify "Unable to connect!"
			Return 0
		EndIf
		WaitResponse()
	EndIf
End Function

Function Login(username$, password$)
	If Stream
		WriteLine Stream, "USER " + username$
		WaitResponse()
		WriteLine Stream, "PASS " + password$
		WaitResponse()
	EndIf
End Function

Function CountMails()
	If Stream
		WriteLine Stream, "STAT"
		txt$ = ReadLine$(Stream)
		If Instr(txt$, "+OK")
			txt$ = Right$(txt$, Len(txt$) - 4)
			TempMailCount = Int(Mid$(txt, 0, Instr(txt$, " ") + 1))
			If TempMailCount > MailCount
				Notify "You have received an E-mail (" + (TempMailCount - MailCount) + ")!"
			EndIf
			MailCount = TempMailCount
		EndIf
	EndIf
End Function

Function Disconnect()
	If Stream
		If Not Eof(Stream)
			WriteLine Stream, "QUIT"
			WaitResponse()
		EndIf
		CloseTCPStream Stream
		Stream = 0
	EndIf
End Function

Function WaitResponse()
	If Stream
		If Not Eof(Stream)
			While Not ReadAvail(Stream)
				If Eof(Stream)
					Notify "Server has closed the connection!"
					Disconnect()
					Return False
				EndIf
			Wend
			
			txt$ = ReadLine$(Stream)
			DebugLog txt$
			
			If Left$(txt$, 3) = "+OK"
				Return True
			ElseIf Left$(txt$, 4) = "-ERR"
				Notify "ERROR '" + txt$ + "'"
				Return False
			Else
				Notify "UNKNOWN ERROR '" + txt$ + "'"
				Return False
			EndIf
		EndIf
	EndIf
End Function

Function SetProgress(pro)
	Progress = pro
	If Progress
		SetGadgetText ProgButton, "Finish"
		ResetTimer IntervalTimer
		ResumeTimer IntervalTimer
		DisableGadget ServerField
		DisableGadget PortField
		DisableGadget UserField
		DisableGadget PassField
		DisableGadget IntervalField
	Else
		SetGadgetText ProgButton, "Begin"
		PauseTimer IntervalTimer
		EnableGadget ServerField
		EnableGadget PortField
		EnableGadget UserField
		EnableGadget PassField
		EnableGadget IntervalField
	EndIf
End Function

Function TheEnd()
	
	wf = WriteFile("data.dat")
		If wf
			WriteInt wf, MailCount
			WriteString wf, TextFieldText(ServerField)
			WriteString wf, TextFieldText(PortField)
			WriteString wf, TextFieldText(UserField)
			WriteString wf, Crypt$(TextFieldText(PassField))
			WriteString wf, TextFieldText(IntervalField)
		EndIf
	CloseFile wf
	
	Disconnect()
	ggTrayDestroy()
	FreeGadget MainWindow
	End
End Function

Function Crypt$(pass$)
	For i = 1 To Len(pass$)
		temppass$ = temppass$ + Chr$(255 - Asc(Mid$(pass$, i)))
	Next
	Return temppass$
End Function
