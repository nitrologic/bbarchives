; ID: 2472
; Author: superStruct
; Date: 2009-05-06 22:35:02
; Title: Mouse Mapping
; Description: Gives Information on Mouse

Graphics 800,600,0,2
SetBuffer BackBuffer()

Global line1
Global line2
Global changex
Global changey
Global mouse
Global update = CreateImage(350,200)
Global r
Global g
Global b
Global temp1
Global temp2
Global act%

Color 255,255,255
Rect 0,0,GraphicsWidth(),150,1
Rect 0,200,GraphicsWidth(),150,1
Rect 10,375,200,200,1
Rect 250,375,500,200,1

Text 0,150,"Mouse Y Acceleration"
Text 0,185,"Mouse X Acceleration"

While Not KeyDown(1)
	r = r + 9
	g = g + 7
	b = b + 3
	Color r,g,b
	line1 = line1 + 4
	line2 = line2 + 4
	MouseYAccel()
	MouseXAccel()
	CheckY()
	CheckX()
	PlotAccelData()
	PlotMouse()
	Color 255,255,255
	Text 10,360,"Mouse Plot"
	Flip 
Wend

Function MouseYAccel()
	MouseYSpeed()
	Delay(20)
	changey = MouseYSpeed()
End Function

Function MouseXAccel()
	MouseXSpeed()
	Delay(20)
	changex = -MouseXSpeed()
End Function 

Function CheckY()
	If line1 > GraphicsWidth()
		Cls
		line1 = 0
		Color 255,255,255
		Rect 0,0,GraphicsWidth(),150
		Rect 0,200,GraphicsWidth(),150
		Rect 10,375,200,200,1
		Rect 250,375,525,200,1
		Text 0,150,"Mouse Y Acceleration"
		Text 0,185,"Mouse X Acceleration"
	EndIf
	If changey < -50 
		changey = 0
	ElseIf changey > 50
		changey = 49
	EndIf 	
End Function 

Function CheckX()
	If line2 > GraphicsWidth()
		Cls
		line2 = 0
		Color 255,255,255
		Rect 0,0,GraphicsWidth(),150
		Rect 0,200,GraphicsWidth(),150
		Rect 10,375,200,200,1
		Rect 250,375,525,200,1
		Text 0,150,"Mouse Y Acceleration"
		Text 0,185,"Mouse X Acceleration"
	EndIf
	If changex < -50
		changex = -50
	ElseIf changex > 50
		changex = 49
	EndIf 
End Function 


Function PlotAccelData()
	Plot line1,changey + 75
	Plot line2,changex + 275
	Plot line1 - 1,changey + 75
	Plot line2 - 1,changex + 275
	Plot line1 - 2,changey + 75
	Plot line2 - 2,changex + 275
	Plot line1 - 3,changey + 75
	Plot line2 - 3,changex + 275
End Function 

Function PlotMouse()
	Color r,g,b
	Plot (MouseX()/4) + 10,(MouseY()/3) + 375
End Function
