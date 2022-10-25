; ID: 1491
; Author: Andres
; Date: 2005-10-19 10:21:47
; Title: HTTP Poster
; Description: HTTP multi posting

Global MainWindow = CreateWindow("HTTP Poster", ClientWidth(Desktop()) / 2 - 250 / 2, ClientHeight(Desktop()) / 2 - 430 / 2, 250, 430, Desktop(), 1)
AppTitle "HTTP Poster"

Global Stream = False
Global Progress = False
Global Post$ = ""
Global tries = 0
Global VariableCount = 16
Dim PostData(VariableCount, 4)
Global Timer = CreateTimer(1000)

CreateLabel("Server:", 5, 8, 40, 15, MainWindow)
Global ServerField = CreateTextField(45, 5, 100, 20, MainWindow)
CreateLabel("Port:", 160, 8, 25, 15, MainWindow)
Global PortField = CreateTextField(185, 5, ClientWidth(MainWindow) - 190, 20, MainWindow)
CreateLabel("URL:", 5, 33, 30, 15, MainWindow)
Global URLField = CreateTextField(45, 30, ClientWidth(MainWindow) - 50, 20, MainWindow)
CreateLabel("Host:", 5, 58, 30, 15, MainWindow)
Global HostField = CreateTextField(45, 55, ClientWidth(MainWindow) - 50, 20, MainWindow)
CreateLabel("Header:", 5, 83, 38, 15, MainWindow)
Global HeaderField = CreateTextArea(45, 80, ClientWidth(MainWindow) - 50, 32, MainWindow, 1)
CreateLabel("Delay:", 5, 118, 30, 15, MainWindow)
Global DelayField = CreateTextField(45, 116, 50, 20, MainWindow)
CreateLabel("Count:", 110, 118, 30, 15, MainWindow)
Global CountField = CreateTextField(145, 116, 50, 20, MainWindow)
Global ProgressLabel = CreateLabel("Progress...", 5, ClientHeight(MainWindow) - 24, ClientWidth(MainWindow) - 65, 18, MainWindow, 3)

CreateLabel("Post data:", 5, 145, 50, 15, MainWindow)
PostPanel = CreatePanel(5, 160, ClientWidth(MainWindow) - 10, ClientHeight(MainWindow) - 190, MainWindow, 1)
Global Slider = CreateSlider(ClientWidth(PostPanel) - 18, 0, 18, ClientHeight(PostPanel), PostPanel, 2)

For i = 0 To VariableCount - 1
	PostData(i, 1) = CreateLabel("Var #" + (i + 1) + ":", 5, 8 + i * 62, 50, 15, PostPanel)
	PostData(i, 2) = CreateTextField(55, 5 + i * 62, 100, 20, PostPanel)
	PostData(i, 3) = CreateLabel("Content:", 5, 33 + i * 62, 50, 15, PostPanel)
	PostData(i, 4) = CreateTextArea(55, 30 + i * 62, 150, 32, PostPanel, 1)
Next

Global Button = CreateButton("Start", ClientWidth(MainWindow) - 55, ClientHeight(MainWindow) - 25, 50, 20, MainWindow)

SetSliderRange Slider, ClientHeight(PostPanel), VariableCount * 62 + 10

SetGadgetText PortField, "80"
SetGadgetText DelayField, "500"
SetGadgetText CountField, "100"

Repeat
	Events(50)
	
	If Progress
		If tries < Int(TextFieldText(CountField))
			If TimerTicks(Timer) => Int(TextFieldText(DelayField))
				If Not Eof(Stream)
					WriteLine Stream, Post$
					tries = tries + 1
					
					SetGadgetText ProgressLabel, "Try: " + tries + "/" + TextFieldText(CountField) + "  Sent: " + ConvertBytes(tries * (Len(Post$) + 2))
					
					;Clear buffer
					avail = ReadAvail(Stream)
					For i = 1 To avail:ReadByte(Stream):Next
				Else
					Progress = False
					StartProgress()
				EndIf
				ResetTimer Timer
			EndIf
		Else
			Progress = False
			StartProgress()
		EndIf
	EndIf
Forever

Function Events(wait)
	Select WaitEvent(wait)
		Case $401
			Select EventSource()
				Case Slider
					For i = 0 To VariableCount - 1
						y = SliderValue(Slider)
						SetGadgetShape PostData(i, 1), 5, 8 + i * 62 - y, 50, 15
						SetGadgetShape PostData(i, 2), 55, 5 + i * 62 - y, 100, 20
						SetGadgetShape PostData(i, 3), 5, 33 + i * 62 - y, 50, 15
						SetGadgetShape PostData(i, 4), 55, 30 + i * 62 - y, 150, 32
					Next
				Case Button
					Progress = 1 - Progress
					StartProgress()
			End Select
		Case $803
			FreeGadget MainWindow
			End
	End Select
End Function

Function StartProgress()
	If progress
		Stream = OpenTCPStream(TextFieldText(ServerField), TextFieldText(PortField))
		If Stream
			DisableGadget ServerField
			DisableGadget PortField
			DisableGadget URLField
			DisableGadget HostField
			DisableGadget HeaderField
			DisableGadget DelayField
			DisableGadget CountField
			For i = 0 To VariableCount - 1
				DisableGadget PostData(i, 2)
				DisableGadget PostData(i, 4)
			Next
			SetGadgetText Button, "Stop"
			
			tries = 0
			sent = 0
			PostLine$ = ""
			For i = 0 To VariableCount - 1
				If Not TextFieldText(PostData(i, 2)) = ""
					If Len(PostLine$) > 0 Then PostLine$ = PostLine$ + "&"
					PostLine$ = PostLine$ + WebCompatible(TextFieldText(PostData(i, 2))) + "=" + WebCompatible(TextAreaText(PostData(i, 4)))
				EndIf
			Next
			
			Post$ = "POST " + TextFieldText(URLField) + " HTTP/1.1" + Chr(13) + Chr(10)
			Post$ = Post$ + "Host: " + TextFieldText(HostField) + Chr(13) + Chr(10)
			Post$ = Post$ + "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)" + Chr(13) + Chr(10)
			If Right$(TextAreaText(HeaderField), 2) = Chr(13) + Chr(10) Then Post$ = Post$ + TextAreaText(HeaderField) Else Post$ = Post$ + TextAreaText(HeaderField) + Chr(13) + Chr(10)
			Post$ = Post$ + "Content-Length: " + Len(PostLine$) + Chr(13) + Chr(10)
			Post$ = Post$ + "Content-Type: application/x-www-form-urlencoded" + Chr(13) + Chr(10)
			Post$ = Post$ + "Connection: Keep-Alive" + Chr(13) + Chr(10)
			Post$ = Post$ + Chr(13) + Chr(10)
			Post$ = Post$ + PostLine$
		Else
			Notify "Unable to connect to " + TextFieldText(ServerField) + ":" + TextFieldText(PortField)
			Progress = False
		EndIf
	Else
		EnableGadget ServerField
		EnableGadget PortField
		EnableGadget URLField
		EnableGadget HostField
		EnableGadget HeaderField
		EnableGadget DelayField
		EnableGadget CountField
		For i = 0 To VariableCount - 1
			EnableGadget PostData(i, 2)
			EnableGadget PostData(i, 4)
		Next
		SetGadgetText Button, "Start"
		If Stream CloseTCPStream Stream
		Stream = False
	EndIf
End Function

Function WebCompatible$(address$)
	For i = 1 To Len(address$)
		char = Asc(Mid$(address$, i, 1))
		
		If Encode(char)
			result$ = result$ + "%" + Right$(Hex(char), 2)
		Else
			result$ = result$ + Chr(char)
		EndIf
	Next
	Return result$
End Function

Function Encode(code)
	If code =< 31 Or code => 127 Then Return True
	Select code
		Case 36, 38, 43, 44, 47, 58, 59, 61, 63, 64, 32, 91, 93
			Return True
		Case 34, 60, 35, 37, 123, 125, 124, 92, 94, 126, 96
			Return True
		Default
			Return False
	End Select
End Function

Function ConvertBytes$(bytes)
	If bytes > 1024^2
		txt$ = Int(Float bytes / 1024^2) + " MB"
	ElseIf bytes > 1024
		txt$ = Int(Float bytes / 1024) + " KB"
	Else
		txt$ = bytes + " B"
	EndIf
	Return txt$
End Function
