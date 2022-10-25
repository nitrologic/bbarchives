; ID: 2491
; Author: superStruct
; Date: 2009-05-28 15:37:12
; Title: Sine Circle
; Description: A Drawing Method using the Sine Function

Graphics 800,600,0,2

Global originX% = 400
Global originY% = 300
Global radius% = 100
Global x1# = 0
Global x2# = 0
Global y1# = 0
Global y2# = 0

For i = 0 To 90
	y1 = (radius*Sin#(i))
	y2 = -(radius*Sin#(i))
	x1 = 400 + Sqr#(radius^2 - y1^2) 
	x2 = 400 - Sqr#(radius^2 - y1^2)
	Plot x1,y2 + 300
Next

For i = 0 To 90
	y1 = (radius*Sin#(i))
	y2 = -(radius*Sin#(i))
	x1 = 400 + Sqr#(radius^2 - y1^2) 
	x2 = 400 - Sqr#(radius^2 - y1^2)
	Plot x2,y2 + 300
Next

For i = 0 To 90
	y1 = (radius*Sin#(i))
	y2 = -(radius*Sin#(i))
	x1 = 400 + Sqr#(radius^2 - y1^2) 
	x2 = 400 - Sqr#(radius^2 - y1^2)
	Plot x2,y1 + 300
Next

For i = 0 To 90
	y1 = (radius*Sin#(i))
	y2 = -(radius*Sin#(i))
	x1 = 400 + Sqr#(radius^2 - y1^2) 
	x2 = 400 - Sqr#(radius^2 - y1^2)
	Plot x1,y1 + 300
Next

Flip

WaitKey
