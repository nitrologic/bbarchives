; ID: 3144
; Author: Pakz
; Date: 2014-10-12 18:35:06
; Title: Slopes Collision example
; Description: Collision sloped block with rectangle

Graphics 640,480,32,2
SetBuffer BackBuffer()

Global x1 = 50
Global y1 = 50
Global w1 = 100
Global h1 = 100
Global w2 = 50
Global h2 = 50

Global slopeim = CreateImage(64,64)
;make the slope image
makeslope()

While KeyDown(1) = False
	Cls
	DrawImage slopeim,50,50
	Color 255,255,0
	Rect MouseX(),MouseY(),w2,h2,True
	; Here the collision is done image with rectangle
	If ImageRectCollide(slopeim,50,50,0,MouseX(),MouseY(),w2,h2) = True
		Color 255,0,0
		Text 0,0,"Collision"
	End If
	Flip
Wend
End

Function makeslope()
	SetBuffer ImageBuffer(slopeim)
	Color 255,255,255
	x2 = 1
	For y=0 To 63
		For x=0 To x2
			Plot x,y
		Next
		x2=x2+1
	Next
	SetBuffer BackBuffer()
End Function
