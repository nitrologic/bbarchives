; ID: 2932
; Author: col
; Date: 2012-03-13 08:14:48
; Title: BMax - Accurate 2D Line Circle Intersection
; Description: Accurate 2D Line to Circle Intersection with returned intersection points, parametric points of intersection on line and normal vectors at the intersection points.

Strict

Type TVector2
	Field X#,Y#
EndType

Global Origin:TVector2 = New TVector2
Global Dir:TVector2 = New TVector2
Global CircleCenter:TVector2 = New TVector2
Global CircleRadius#
Global PPoint#[2]		'Parametric point of intersction of the line
Global IPoint:TVector2[] = New TVector2[2] 'Intersection points
Global NPoint:TVector2[] = New TVector2[2] 'Normal at intersection points

IPoint[0] = New TVector2 ; IPoint[1] = New TVector2
NPoint[0] = New TVector2 ; NPoint[1] = New TVector2

CircleCenter.X = 400 ; CircleCenter.Y = 300
CircleRadius = 100

Origin.X = 400 ; Origin.Y = 40

Graphics 800,600
While Not KeyDown(KEY_ESCAPE) And Not AppTerminate()
	Cls
	
	Dir.X = MouseX() - Origin.X
	Dir.Y = MouseY() - Origin.Y
	
	If KeyDown(KEY_W) And Origin.Y > 0 Origin.Y :- 2
	If KeyDown(KEY_S) And Origin.Y < 800 Origin.Y :+ 2
	If KeyDown(KEY_A) And Origin.X > 0 Origin.X :- 2
	If KeyDown(KEY_D) And Origin.X < 600 Origin.X :+ 2
	
	SetColor 0,255,0
	DrawOval CircleCenter.X - CircleRadius,CircleCenter.Y - CircleRadius,CircleRadius*2,CircleRadius*2
	
	If IntersectLineCircle(Origin,Dir,CircleCenter,CircleRadius,PPoint,IPoint,NPoint)
		If PPoint[0] > 0.0 And PPoint[0] < 1.0
			SetColor 255,0,0
			DrawOval IPoint[0].X-4,IPoint[0].Y-4,8,8
			
			SetColor 255,255,0
			DrawLine IPoint[0].X,IPoint[0].Y,IPoint[0].X+(NPoint[0].X *40),IPoint[0].Y+(NPoint[0].Y *40)
		EndIf
		If PPoint[1] > 0.0 And PPoint[1] < 1.0
			SetColor 255,0,0
			DrawOval IPoint[1].X-4,IPoint[1].Y-4,8,8

			SetColor 255,255,0
			DrawLine IPoint[1].X,IPoint[1].Y,IPoint[1].X+(NPoint[1].X *40),IPoint[1].Y+(NPoint[1].Y *40)
		EndIf
	EndIf
	
	SetColor 255,255,255
	DrawOval Origin.X-5,Origin.Y-5,10,10
	DrawOval MouseX()-5,MouseY()-5,10,10
	DrawLine Origin.X,Origin.Y,MouseX(),MouseY()

	Flip
Wend
End

Function IntersectLineCircle(O:TVector2,D:TVector2,C:TVector2,Radius#,T#[] Var,Intersect:TVector2[] Var,Normal:TVector2[] Var)
	Local Diff:TVector2 = New TVector2
	Diff.X = O.X - C.X
	Diff.Y = O.Y - C.Y
	
	Local A# = (Dir.X * Dir.X) + (Dir.Y * Dir.Y)
	Local B# = (Diff.X * Dir.X) + (Diff.Y * Dir.Y)
	Local Coeff# = (Diff.X * Diff.X) + (Diff.Y * Diff.Y) - (Radius*Radius)
	
	Local Intersecting# = B * B - A * Coeff
	If Intersecting < 0.0 Return False
	
	Local sqrIntersecting = Sqr(Intersecting)
	Local InvA# = 1.0 / A
	T[0] = (-B - sqrIntersecting ) * InvA
	T[1] = (-B + sqrIntersecting ) * InvA
	
	Local invRadius# = 1.0 / Radius
	
	Intersect[0].X = O.X + T[0] * D.X
	Intersect[0].Y = O.Y + T[0] * D.Y
	Intersect[1].X = O.X + T[1] * D.X
	Intersect[1].Y = O.Y + T[1] * D.Y
	
	Normal[0].X = (Intersect[0].X - C.X) * invRadius
	Normal[0].Y = (Intersect[0].Y - C.Y) * invRadius
	Normal[1].X = (Intersect[1].X - C.X) * invRadius
	Normal[1].Y = (Intersect[1].Y - C.Y) * invRadius
	Return True
EndFunction
