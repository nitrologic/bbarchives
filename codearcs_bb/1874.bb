; ID: 1874
; Author: Mr Snidesmin
; Date: 2006-12-08 05:04:09
; Title: Simple Spline Interpolation
; Description: Simple Spline Function giving y values for x

;Simple spline interpolation - get Y from X
;(x0,y0),(x1,y1),(x2,y2),(x3,y3) =  4 known sequential points in increasing x order
;x = x coordinate we wish to interpolate for (must satisfy (x1 <= x < x2)
Function SplineY#(x0#, y0#, x1#, y1#, x2#, y2#, x3#, y3#, x#)    
	;known gradients:
	a1# = (y2-y0) / (x2-x0) 
	a2# = (y3-y1) / (x3-x1) 
	
    ;Linear equations for points 1 and 2:
    c1# = y1 - a1 * x1
	c2# = y2 - a2 * x2
	
	;Interpolate to get equation at point (x,y):
    t# = (x - x1) / (x2 - x1)
    t# = (Cos(180 * (1 - t)) + 1) / 2 ;(This smooths the transition using cos)
    c# = c1 * (1 - t) + c2 * t
    a# = a1 * (1 - t) + a2 * t
    
    Return a * x + c
End Function


Function SplineYGradient#(x0#, y0#, x1#, y1#, x2#, y2#, x3#, y3#, x#)    
	;known gradients:
	a1# = (y2-y0) / (x2-x0) 
	a2# = (y3-y1) / (x3-x1) 
	
	;Interpolate to get equation at point (x,y):
    t# = (x - x1) / (x2 - x1)
    t# = (Cos(180 * (1 - t)) + 1) / 2 ;(This smooths the transition using cos)
 
    a# = a1 * (1 - t) + a2 * t
    Return a
End Function


Example

Function Example()
	Local yvalues#[6]
	
	yvalues[0] = Rnd(600)
	yvalues[1] = Rnd(600)
	yvalues[2] = Rnd(600)
	yvalues[3] = Rnd(600)
	yvalues[4] = Rnd(600)
	yvalues[5] = Rnd(600)
	yvalues[6] = Rnd(600)
	
	Graphics 800, 600
	SetBuffer BackBuffer()
	Cls
	
	Color 255,0,0
	For x# = 0 To 5 Step 1
		Oval x*100+100-2, yvalues[x]-2, 4, 4, False
	Next
	
	Color 255,255,255
	For x# = 1.0 To 5 Step 0.01
		x0# = Floor(x) -1
		x1# = Floor(x) 
		x2# = Floor(x) +1
		x3# = Floor(x) +2
		
		y0# = yvalues[x0-1]
		y1# = yvalues[x0]
		y2# = yvalues[x0+1]
		y3# = yvalues[x0+2]
		
		y# = SplineY(x0, y0, x1, y1, x2, y2, x3, y3, x)
		Plot x*100, y 
	Next 
    
	Flip
    WaitKey
End Function
