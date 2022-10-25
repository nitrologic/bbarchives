; ID: 1274
; Author: n8r2k
; Date: 2005-01-31 20:06:02
; Title: TextFader()
; Description: a simple textfader()

Graphics 800,600,16,2
Global g = 0
Global r = 155
Global bigfont = LoadFont("Arial",50,0,0,0)

While Not KeyHit(1)
ClsColor 155,0,0
Cls
textfader()
SetFont bigfont
Color r,g,0
Text 100,100,"Press space to fade in again"		;Edit this line
If KeyHit(57) resettext()
Flip
Wend

Function TextFader()
g = g + 1
r = r - 1
If r < 0
	r = 0
EndIf
If g > 128
	g = 128
EndIf
End Function

Function ResetText()
g = 0
r = 155
End Function
