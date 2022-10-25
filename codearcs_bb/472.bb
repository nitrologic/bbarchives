; ID: 472
; Author: ShadowTurtle
; Date: 2002-10-28 07:16:44
; Title: Server Operating System
; Description: Operating System for 'Server Tool Programmer'

AppTitle "Server Operating System"
Graphics 800, 600, 16, 2

Global PortNum = 110

Print "Server Operating System"
.resetconnect:
Print "Press enter for exit!"
Serv$ = Input$("Connect to (Port " + Str$(PortNum) + ") ?")
If Serv$ = "" Then
	Print "Programm exit. Press a key!"
	WaitKey()
	End
Else
	Print "Connect to " + Serv$ + " (Port " + Str$(PortNum) + ") ..."
	TheServer = OpenTCPStream(Serv$, PortNum)
	If Not TheServer Then
		Print "Connection failed! Press a key... (Key U = Config Port!)"
		WaitKey()
		If KeyDown(22) Then
			NewPort = Input("Enter new Port number: ")
			If NewPort = 0 Then
				Print "Programm exit. Press a key!"
				WaitKey()
				End
			Else
				PortNum = NewPort
				Goto resetconnect
			End If
		Else
			End
		End If
	End If
End If

Print "Connected! Press 'end' and the Programm say: Bye, Bye! *g*"

While Not Lower$(A$) = "end"
	i$ = ReadLine(TheServer)
	Print Serv$ + " " + i$
	A$ = Input$(Serv$ + ">")
	WriteLine TheServer, A$
Wend
CloseTCPStream(TheServer)

Print "Programm exit. Press a key! (Bye, Bye)"
WaitKey()
End
