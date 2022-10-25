; ID: 2230
; Author: Emmett
; Date: 2008-03-13 05:43:21
; Title: Mouse Click Lines
; Description: Simple method for drawing lines with mouse clicks.

Global clicks
Graphics 800,600
Repeat

If MouseHit(1)				'Get a mouse click
	If clicks<1			'Clicks = 0 on first click
		x1=MouseX() y1=MouseY()	'Sets x1 and y1 to position of 1st mouse click
		DrawOval x1,y1,2,2	'Draws small oval at 1st mouse click position
		clicks:+1		'Add 1 to clicks
	Else				'If clicks is greater than 0 drop in here
		x2=MouseX() y2=MouseY()	'Sets x2 and y2 to position of 2nd mouse click
		DrawOval x2,y2,2,2
		clicks:+1
	EndIf
EndIf

If clicks=2	'Conditions have been met to draw a line
	SetColor Rand(0,255),Rand(0,255),Rand(0,255) 	'Set random color for fun
	DrawLine x1,y1,x2,y2				'Now draw the line
	clicks=0					'Reset clicks ready for next line
EndIf

Flip
Until KeyHit(key_escape)
