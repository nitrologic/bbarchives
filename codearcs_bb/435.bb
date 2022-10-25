; ID: 435
; Author: Litobyte
; Date: 2002-09-25 09:07:21
; Title: USB 12 buttons joypad
; Description: Digital USB 12 buttons gamepad check

; JoyType() example 
; Modified for 10 buttons joypad (Microsoft, Creative compatible)

; Check to see what stick is present - print the proper message 
Select JoyType() 
Case 0 
	Print "Sorry, no joystick attached to system!" 
Case 1 
	Print "Digital joystick is attached to system!" 
Case 2 
	Print "Analog joystick is attched to system!" 
End Select
Print

; Wait for user to hit ESC 
If JoyType()<>0 Then
	button=0
	Print "press a joypad button(1-12) to start"
	Print "press 'select' button to quit" 
	WaitJoy()
	While Not(JoyHit(10) Or KeyHit(1))
		.label
		Cls
		Locate 0,40
		Print "Joystick button n°"+button
		Print "joyX: "+JoyX()
		Print "joyY: "+JoyY()
		Print "joyXDir: "+JoyXDir()
		Print "joyYDir: "+JoyYDir()
		Print "joyZ: not supported"+JoyZ()
		Print:Print:Print "press Select Button to quit"
		FlushJoy
		.lab2
		If (JoyXDir()<>0 Or JoyYDir()<>0) Goto label
		button=GetJoy()
		If button=0 Then Goto lab2
	Wend
Else
	While Not KeyHit(1) 
	Wend
EndIf
