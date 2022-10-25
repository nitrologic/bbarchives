; ID: 1748
; Author: Byteemoz
; Date: 2006-07-10 16:09:29
; Title: Circle through three points
; Description: Finds the circle that goes through three given points.

SuperStrict

Function CircleFromPoints:Int(p1x#, p1y#, p2x#, p2y#, p3x#, p3y#, cx# Var, cy# Var, rad# Var)
	Local A#, B#, C#, D#, E#, F#, G#
	A = p2x - p1x
	B = p2y - p1y
	C = p3x - p1x
	D = p3y - p1y

	E = A * (p1x + p2x) + B * (p1y + p2y)
	F = C * (p1x + p3x) + D * (p1y + p3y)	
	G = 2 * (A * (p3y - p2y) - B * (p3x - p2x))
	
	If G = 0 Then
		cx = 0.0
		cy = 0.0
		rad = 0.0
		Return False
	Else
		cx = (D * E - B * F) / G
		cy = (A * F - C * E) / G
		rad = Sqr((p1x - cx) ^ 2 + (p1y - cy) ^ 2)
		Return True
	EndIf
EndFunction

' a test
AppTitle = "LMB = point1 ; MMB = point2 ; RMB = point3"
Graphics 800, 600

Local p1x# = 400, p1y# = 250, p2x# = 350, p2y# = 350, p3x# = 450, p3y# = 350, cx#, cy#, rad#

Repeat
	WaitEvent
	
	If MouseDown(MOUSE_LEFT) Then p1x = MouseX() ; p1y = MouseY()
	If MouseDown(MOUSE_MIDDLE) Then p2x = MouseX() ; p2y = MouseY()
	If MouseDown(MOUSE_RIGHT) Then p3x = MouseX() ; p3y = MouseY()
	
	CircleFromPoints p1x, p1y, p2x, p2y, p3x, p3y, cx, cy, rad
	
	Cls
	SetColor 255, 255, 255
	DrawOval cx - rad, cy - rad, rad * 2, rad * 2
	SetColor 0, 64, 0
	DrawOval cx - 1, cy - 1, 3, 3
	SetColor 255, 128, 128
	DrawRect p1x - 2, p1y - 2, 5, 5
	DrawRect p2x - 2, p2y - 2, 5, 5
	DrawRect p3x - 2, p3y - 2, 5, 5
	Flip
Until KeyHit(KEY_ESCAPE) Or AppTerminate()
