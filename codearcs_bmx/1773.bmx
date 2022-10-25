; ID: 1773
; Author: ralphy
; Date: 2006-08-02 11:30:29
; Title: TMouseStick
; Description: Emulate mouse as a digital joystick with smoothmovement

'BlitzMax - MouseStick Class Module - Parablax 2006
'Emulates the mouse as a digital joystick
'Class: TMouseStick
'Methods: Init(smoothmove), Refresh
'Properties: Up, Down, Lft, Rght (all readonly)
'(increasing the smoothmove parameter delays the change of a direction from 1 To 0 when no movement is detected)

Strict

Local MouseStick:TMouseStick
MouseStick = New TMouseStick
MouseStick.Init(20)
Graphics 320,240,0
HideMouse()
While Not MouseHit(1)
	Cls
	SetColor 200,200,0
	DrawText "Wiggle the mouse - Click to exit",0,0
	SetColor 200,200,200
	If mousestick.up Then DrawText "up",140,60
	If mousestick.Down Then DrawText "down",140,180
	If mousestick.Lft Then DrawText "left",80,120
	If mousestick.Rght Then DrawText "right",200,120
	MouseStick.Refresh	
	Flip
Wend
End

'Types
Type TMouseStick
	Field Up,Down, Lft, Rght
	Field xspeed[], yspeed[]
	
	Method Init(smoothmove)
		xspeed = New Int[smoothmove]
		yspeed = New Int[smoothmove]
	End Method
	
	Method Refresh()
		Local i, j, xspeed_chk, yspeed_chk
		
		For i = xspeed.length-1 To 1 Step -1
			xspeed[i] = xspeed[i-1]
			yspeed[i] = yspeed[i-1]
		Next
	
		xspeed[0] =MouseX() -100
		yspeed[0] =MouseY() -100
		MoveMouse(100,100)
	
		For j = 0 To xspeed.length-1
			xspeed_chk :+ xspeed[j]
			yspeed_chk :+ yspeed[j]
		Next

		If xspeed_chk = 0 Then Rght = 0; Lft = 0
		If yspeed_chk = 0 Then Up = 0; Down = 0

		If xspeed[0]<0 Then 	Lft = 1; Rght = 0; 
		If xspeed[0]>0 Then 	Lft = 0; Rght = 1; 
		If yspeed[0]<0 Then 	Up = 1; Down = 0
		If yspeed[0]>0 Then 	Up = 0; Down = 1
	End Method
	
EndType
