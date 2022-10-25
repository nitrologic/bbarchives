; ID: 1712
; Author: IKG
; Date: 2006-05-13 13:13:48
; Title: Simple Rectangle Drawing
; Description: Little painting program.

Graphics 640,480,0

ShowMouse()

SeedRnd MilliSecs()

While Not (KeyHit(KEY_ESCAPE))
	
	'if right mouse button is down, draw rectangle
	If MouseDown(1) Then
		SetColor(Rnd(1,255),Rnd(1,255),Rnd(1,255))
		DrawRect(MouseX(),MouseY(),10,10)
	EndIf

	'press spacebar to clear screen
	If KeyHit(KEY_SPACE) Then Cls()

	Flip()
	
Wend
