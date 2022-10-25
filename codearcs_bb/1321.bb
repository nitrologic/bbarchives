; ID: 1321
; Author: indiepath
; Date: 2005-03-11 10:13:25
; Title: Multisided geometric shapes
; Description: Draw a geometric shape of nSides

; Written by Tim Fisher of Indiepath 2005. http://www.indiepath.com

; *******************************************************************************
; DrawGeom - Will draw a polygon of n Sides.
; x#,y# = Screen Co-ordinates
; Sides# = Number of sides on Polygon
; Length# = Length of Side
; Angle# = Angle of Rotation
; *******************************************************************************

Function DrawGeom(x#,y#,sides#,length#,angle#)

	Local aStep# = 360 / sides
	Length# = (length/2)/Sin(aStep#/2) ;Calculate the correct length of a side 			
	For a = 0 To sides-1
		x1# = x# - (Sin(angle + (aStep * a))*length)
		y1# = y# - (Cos(angle + (aStep * a))*length)
		x2# = x# - (Sin(angle + (aStep * (a+1)))*length)
		y2# = y# - (Cos(angle + (aStep * (a+1)))*length)
		Color 150,150,150
		Oval x1-5,y1-5,11,11,False							; Draw Circle at Vertex
		Color 255,255,255		
		Line x1,y1,x2,y2									; Draw Connecting Lines
	Next
	
End Function
