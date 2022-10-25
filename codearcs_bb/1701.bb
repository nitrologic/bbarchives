; ID: 1701
; Author: Jesse B Andersen
; Date: 2006-05-06 22:35:13
; Title: Input Key System for Games
; Description: Example of key input management for games

;xmlspy - jesse_andersengt@yahoo.com
;http://www.alldevs.com
;managing Input keys for games
;I decided to manage my key inputs for better control
;on character animations
;Now I can throw stuff in the Case statements and I will
;exactly know where to go if one of the actions is not
;working or not functioning correctly.
;pardon da french

Gosub example

Type ikey
	Field number, action$, style
	Field state
End Type

;number
;style - 0 = keydown(), 1 = keyhit()

Function NewIKey(number, action$, style=0)
	ik.ikey = New ikey
	ik\number = number
	ik\action = action
	ik\style = style
End Function

Function DeleteIKey(number)
	For ik.ikey = Each ikey
		If ik\number = number Then Delete ik.ikey : Exit
	Next
End Function

Function ChangeIKey(number, action$, style=0)
	For ik.ikey = Each ikey
		If ik\number = number Then
			ik\action = action
			ik\style = style
			Exit
		EndIf
	Next
End Function

Function InputKeys()
	For ik.ikey = Each ikey
		If ik\style Then
			ik\state = KeyHit(ik\number)
		Else
			ik\state = KeyDown(ik\number)
		EndIf
	Next
End Function

Function ManageKeys()
	For ik.ikey = Each ikey
		If ik\state Then
			Select ik\action
				Case "MoveForward"
					message$ = "Moving Forward"
					;Here I can add the code to move my mesh forward, or call a function
				Case "MoveBackward"
					message$ = "Move Backward"
				Case "MoveLeft"
					message$ = "Move Left"
				Case "MoveRight"
					message$ = "Move Right"
				Case "Jump"
					;message$ = "Jump" : way too fast for message$
					DebugLog "Jump"
				Case "Attack"
					message$ = "Attack"
				Case "Exit"
					End
			End Select
		EndIf
	Next
End Function

Function ClearKeys()
	For ik.ikey = Each ikey
		ik\state = 0
	Next
End Function

Function SaveIkeys(File$)
	fo = WriteFile(File$)
		For ik.ikey = Each ikey
			WriteInt fo, ik\number
			WriteString fo, ik\action
			WriteInt fo, ik\style
		Next
	CloseFile(fo)
End Function

Function LoadIkeys(File$)
	If FileType(File$) Then
		fi = ReadFile(File$)
			Repeat
				NewIKey(ReadInt(fi), ReadString(fi), ReadInt(fi))
			Until Eof(fi)
		CloseFile(fi)
	EndIf
End Function

.example
	NewIkey(57,"Jump",1)
	NewIkey(1,"Exit")
	NewIkey(2,"Attack")
	NewIkey(3,"MoveForward")
	NewIkey(4,"MoveBackward")
	Global message$
	
	Repeat
		Cls
		InputKeys()
		ManageKeys()
		Text 0, 0, "message: " + message$
		Text 0, 20, "123 and esc"
		Flip
		ClearKeys() : message$ = ""
	Forever
Return
