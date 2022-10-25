; ID: 1784
; Author: IKG
; Date: 2006-08-13 14:03:13
; Title: Draw Line With Mouse
; Description: Use your mouse to draw a line..

'Written by David Schwartz - http://www.devdave.net
Graphics 640,480,0

SetColor(255,255,255)

Global firstmousex = 0
Global firstmousey = 0
Global secondmousex = 0
Global secondmousey = 0
Global first = False
Global second = False

Repeat

If MouseHit(1) Then
	firstmousex = MouseX()
	firstmousey = MouseY()
	SetColor (255,0,0)
	DrawOval firstmousex,firstmousey,4,4
	SetColor(255,255,255)
	first = True
EndIf

If MouseHit(2) Then
	secondmousex = MouseX()
	secondmousey = MouseY()
	SetColor (255,0,0)
	DrawOval secondmousex,secondmousey,4,4
	SetColor(255,255,255)
	second = True
EndIf 

If first = True And second = True Then 
	DrawLine firstmousex,firstmousey,secondmousex,secondmousey
	first = False
	second = False
EndIf

Flip

Until KeyHit(key_escape)
