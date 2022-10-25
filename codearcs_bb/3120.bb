; ID: 3120
; Author: zoqfotpik
; Date: 2014-04-15 14:15:06
; Title: Fractal Triangles
; Description: Sierpinski fractal.

' Recursive subdivision of triangles

Global tri#[]
Global tmppoint = New point
Global seed:Int=0
Global SCREENWIDTH=1600
Global SCREENHEIGHT=1000
Global COLORXFACTOR = 255/SCREENWIDTH
Global COLORYFACTOR = 255/SCREENHEIGHT
Global MAXITER:Int = 7
Global MIDTRIANGLEITER:Int = 4
Global HOLDSTILL:Int = 0
Global TRI1:Int = 1
Global TRI2:Int = 1
Global TRI3:Int = 1

midpoint:point = New point
Global vertex1:point=New point
Global vertex2:point=New point
Global vertex3:point=New point

Graphics SCREENWIDTH,SCREENHEIGHT
While Not KeyHit(KEY_ESCAPE)
        Cls
		If HOLDSTILL = 0
			vertex1.update()
			vertex2.update()
			vertex3.update()
		Else
			vertex1.x = 0
			vertex1.y = SCREENHEIGHT
			vertex2.x = SCREENWIDTH/2
			vertex2.y = 0
			vertex3.x = SCREENWIDTH
			vertex3.y = SCREENHEIGHT
		EndIf
		
		seed = 0
		recursivesubdivide(vertex1,vertex2,vertex3,1)
		SetColor 255,255,255
		DrawText "Outer Triangle Iterations (change with q / a):"+maxiter, 10, 10
		DrawText "Inner Triangle Iterations (change with w / s):"+midtriangleiter , 10, 25
		DrawText "Toggle Hold Still with Spacebar", 10, 40
		DrawText "Toggle the Three Outer Triangles (1-3)",10, 55
       Flip
		If KeyHit(KEY_q) And maxiter < 10 maxiter = maxiter + 1
		If KeyHit(KEY_a) And maxiter > 0  maxiter = maxiter - 1
		If KeyHit(KEY_w) And midtriangleiter < 10 midtriangleiter = midtriangleiter + 1
		If KeyHit(KEY_s) And midtriangleiter > -1 midtriangleiter = midtriangleiter - 1
		If KeyHit(KEY_SPACE) HOLDSTILL = (HOLDSTILL = 0)
		If KeyHit(KEY_1) TRI1=(TRI1=0)
		If KeyHit(KEY_2) TRI2=(TRI2=0)
		If KeyHit(KEY_3) TRI3=(TRI3=0)
Wend

Function recursivesubdivide(p1:point,p2:point,p3:point, iter:Int)
	seed = seed + 1
	If iter > maxiter
		Local poly:Float[]=[p1.x,p1.y,p2.x,p2.y,p3.x,p3.y]
		SeedRnd(seed)
		SetColor(Rand(255),Rand(255),Rand(255))
		DrawPoly poly
		Return 1
	EndIf
	Local midpoint1:point = Fmidpoint(p1,p2)
	Local midpoint2:point = Fmidpoint(p2,p3)
	Local midpoint3:point = Fmidpoint(p3,p1)
	If TRI1 
		'SetColor 0,midpoint1.x*COLORXFACTOR,0
		recursivesubdivide(p1,midpoint1,midpoint3,iter+1)
	EndIf
	
	If TRI2
		'SetColor 0,255,0
	    recursivesubdivide(p2,midpoint2,midpoint1,iter+1)
	EndIf 
	
	If TRI3
		'SetColor 0,0,255
	    recursivesubdivide(p3,midpoint3,midpoint2,iter+1)
	EndIf
	If iter < midtriangleiter recursivesubdivide(midpoint1,midpoint2,midpoint3, iter+1)
End Function
	
Type point
	Field x#
	Field y#
	Field vx:Float
	Field vy:Float
	Field ox#,oy#
	Method New()
		x = Rand(SCREENWIDTH)
		y = Rand(SCREENHEIGHT)
		vx = (Rand(-8,7)+RndFloat()) /2
		vy = (Rand(-8,7)+RndFloat()) /2
	End Method
	Method update()
		ox = x
		oy = y
		If inlimits(x+vx,0,SCREENWIDTH)
			x = x + vx
		Else
			vx = vx * -1
		EndIf
		If inlimits(y+vy,0,SCREENHEIGHT)
			y = y + vy
		Else
			vy=vy*-1
		EndIf
	End Method
End Type

Function Fmidpoint:point(in1:point,in2:point)
	Local p:point = New point
	p.x = (in1.x+in2.x)/2
	p.y = (in1.y+in2.y)/2
	Return p
End Function

Function inlimits:Int(num:Float,lowerlimit:Float, upperlimit:Float)
	If num > lowerlimit And num < upperlimit Return 1
End Function
