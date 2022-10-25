; ID: 1675
; Author: drnmr
; Date: 2006-04-14 15:07:02
; Title: DrawLineTrapezoid()
; Description: Draws the outline of a trapezoid.

Function DrawLineTrapezoid(x,y,length,height)
	topleftx = length/4+x
	lowerlefty = y+height
	lowerrightx = x+length
	lowerrighty = y+height
	upperrightx = length/4*3+x
	DrawLine topleftx,y,x,lowerlefty
	DrawLine x,lowerlefty,lowerrightx,lowerrighty
	DrawLine lowerrightx,lowerrighty,upperrightx,y
	DrawLine upperrightx,y,topleftx,y
EndFunction
