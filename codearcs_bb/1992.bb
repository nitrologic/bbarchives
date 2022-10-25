; ID: 1992
; Author: Rob Farley
; Date: 2007-04-17 16:56:53
; Title: Point in Triangle
; Description: Checks if a point in inside a triangle

Graphics 640,480,32,2
SeedRnd MilliSecs()

Repeat
Cls
	x1 = Rand(100,540)
	y1 = Rand(100,380)
	x2 = Rand(100,540)
	y2 = Rand(100,380)
	x3 = Rand(100,540)
	y3 = Rand(100,380)
	
	
	Color 255,0,0
	
	For x=0 To 639
	For y=0 To 479
		If intriangle (x,y,x1,y1,x2,y2,x3,y3) Then Plot x,y
	Next
	Next
	
	Color 255,255,255
	Line x1,y1,x2,y2
	Line x2,y2,x3,y3
	Line x3,y3,x1,y1
	
	WaitKey

Until KeyHit(1)


Function InTriangle(x0,y0,x1,y1,x2,y2,x3,y3)

	b0# =  (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)
	b1# = ((x2 - x0) * (y3 - y0) - (x3 - x0) * (y2 - y0)) / b0 
	b2# = ((x3 - x0) * (y1 - y0) - (x1 - x0) * (y3 - y0)) / b0
	b3# = ((x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0)) / b0 
	
	If b1>0 And b2>0 And b3>0 Then Return True Else Return False

End Function
