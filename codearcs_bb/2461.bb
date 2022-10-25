; ID: 2461
; Author: blade007
; Date: 2009-04-14 23:32:33
; Title: Polygon Inscribed Inside Circle
; Description: A simple function that draws polygons inscribed inside a circle

Function DrawInscribedPolygon(Sides,Radius,CenterX,CenterY,Rotate#)
	PerAngle# = 360/Float(Sides)
	Angle# = Rotate#
	OriginPoint = True
	.BeginLoop ; I use a goto loop because the step value was not constant
		PrevX = cVertexX
		PrevY = cVertexY	
		cVertexX = (CenterX+Radius*Cos(Angle#))
		cVertexY = (CenterY+Radius*Sin(Angle#))
		If OriginPoint = False Then Line PrevX,PrevY,cVertexX,cVertexY Else OriginPoint = False
		Angle# = Angle# + PerAngle#
	If Angle# < 360.1+Rotate# Then Goto BeginLoop
End Function
