; ID: 2265
; Author: computercoder
; Date: 2008-06-11 04:30:37
; Title: Modified Spline Interpolation
; Description: X and Y spline interpolation

Function SplineY:Int(x0:Int, y0:Int, x1:Int, y1:Int, x2:Int, y2:Int, x3:Int, y3:Int, x:Float)
	' known gradients:
	a1:Float = (y2-y0) / (x2-x0)
	a2:Float = (y3-y1) / (x3-x1)

	' Linear equations For points 1 And 2:
	c1:Float = y1 - a1 * x1
	c2:Float = y2 - a2 * x2

	' Interpolate To get equation at point (x,y):
	t:Float = (x - x1) / (x2 - x1)
	t = (Cos(180 * (1 - t)) + 1) / 2 '(This smooths the transition using Cos)
	c:Float = c1 * (1 - t) + c2 * t
	a:Float = a1 * (1 - t) + a2 * t

	Return Int(a * x + c)
End Function

Function SplineX:Int(x0:Int, y0:Int, x1:Int, y1:Int, x2:Int, y2:Int, x3:Int, y3:Int, y:Float)
	' known gradients:
	a1:Float = (y2-y0) / (x2-x0)
	a2:Float = (y3-y1) / (x3-x1)

	' Linear equations For points 1 And 2:
	c1:Float = x1 - a1 * y1
	c2:Float = x2 - a2 * y2

	' Interpolate To get equation at point (x,y):
	t:Float = (y - y1) / (y2 - y1)
	t = (Cos(180 * (1 - t)) + 1) / 2 '(This smooths the transition using Cos)
	c:Float = c1 * (1 - t) + c2 * t
	a:Float = a1 * (1 - t) + a2 * t

	Return Int(a * y + c)
End Function


Function Example()

	Local xvalues:Int[8]
	Local yvalues:Int[8]

	Local x:Float = 0.0
	Local x0:Int  = 0
	Local x1:Int  = 0
	Local x2:Int  = 0
	Local x3:Int  = 0
	
	Local y:Float = 0.0
	Local y0:Int  = 0
	Local y1:Int  = 0
	Local y2:Int  = 0
	Local y3:Int  = 0

	xvalues[0] = Rand(400,800)
	xvalues[1] = Rand(400,800)
	xvalues[2] = Rand(400,800)
	xvalues[3] = Rand(400,800)
	xvalues[4] = Rand(400,800)
	xvalues[5] = Rand(400,800)
	xvalues[6] = Rand(400,800)
	xvalues[7] = Rand(400,800)
	
	yvalues[0] = Rand(300)
	yvalues[1] = Rand(300)
	yvalues[2] = Rand(300)
	yvalues[3] = Rand(300)
	yvalues[4] = Rand(300)
	yvalues[5] = Rand(300)
	yvalues[6] = Rand(300)
	yvalues[7] = Rand(300)
	
	Graphics 800, 600
	
	Cls
	
	SetColor 255,0,0
	
	For xx:Int = 0 To 5 Step 1
		DrawOval xx*100+100-2, yvalues[xx]-2, 4, 4
	Next
	
	SetColor 255,255,0
	
	For yy:Int = 0 To 5 Step 1
		DrawOval xvalues[yy]-2, yy*100+100-2, 4, 4
	Next
	
	SetColor 0,255,0
	
	For x = 1.0 To 5.0 Step 0.01
		x0 = Floor(x) - 1
		x1 = Floor(x) 
		x2 = Floor(x) + 1
		x3 = Floor(x) + 2
		
		y0 = yvalues[x1-1]
		y1 = yvalues[x1]
		y2 = yvalues[x1+1]
		y3 = yvalues[x1+2]
		
		y = SplineY(x0, y0, x1, y1, x2, y2, x3, y3, x)
		
		Plot (x*100), y
	Next
	
	SetColor 0,0,255
	
	For y = 1.0 To 5.0 Step 0.01
		y0 = Floor(y) - 1
		y1 = Floor(y) 
		y2 = Floor(y) + 1
		y3 = Floor(y) + 2
		
		x0 = xvalues[y1-1]
		x1 = xvalues[y1]
		x2 = xvalues[y1+1]
		x3 = xvalues[y1+2]
		
		x = SplineX(x0, y0, x1, y1, x2, y2, x3, y3, y)
		
		Plot x, (y*100)
	Next
	
	Flip
	WaitKey()
End Function

Example()
End
