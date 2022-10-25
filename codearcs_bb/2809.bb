; ID: 2809
; Author: furbrain
; Date: 2011-01-13 12:32:34
; Title: Circle plotter
; Description: Yet another circle routine (no division/multi)

Function Circle(xpos,ypos,radius,colour,buffer)
	Y = radius
	X = 0
	A = radius/2
	LockBuffer buffer
	WritePixelFast(xpos+x,ypos+y,colour)		; Draw the starting pixels
	WritePixelFast(xpos-x,ypos-y,colour)		
	WritePixelFast(xpos+x,ypos-y,colour)
	WritePixelFast(xpos-x,ypos+y,colour)
	WritePixelFast(xpos+y,ypos+x,colour)
	WritePixelFast(xpos-y,ypos-x,colour)
	WritePixelFast(xpos+y,ypos-x,colour)
	WritePixelFast(xpos-y,ypos+x,colour)
	While X < Y
		X = X + 1
		A = A - X
		If A<0 Then 
			A=A+Y 
			Y=Y-1
		EndIf
		WritePixelFast(xpos+x,ypos+y,colour) ; Draw 1/8 at a time 
		WritePixelFast(xpos+y,ypos+x,colour)
		WritePixelFast(xpos-x,ypos-y,colour)
		WritePixelFast(xpos-y,ypos-x,colour)
		WritePixelFast(xpos-x,ypos+y,colour)
		WritePixelFast(xpos-y,ypos+x,colour)
		WritePixelFast(xpos+x,ypos-y,colour)
		WritePixelFast(xpos+y,ypos-x,colour)
	Wend
	UnlockBuffer buffer
End Function
