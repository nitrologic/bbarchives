; ID: 1094
; Author: Mr Snidesmin
; Date: 2004-06-21 23:16:53
; Title: Instant Cliffs
; Description: Applies textures based on gradient - EASY TO USE!

Function GradePaintTerrainMesh%(m%, g1#, g2#, g3#, g4#, bGrad0%, bGrad1%, bGrad2%, bGrad3%, bGrad4%)	
	m2% = CreateMesh()
	
	s_g0% = CreateSurface(m2)
	s_g1% = CreateSurface(m2)
	s_g2% = CreateSurface(m2)
	s_g3% = CreateSurface(m2)
	s_g4% = CreateSurface(m2)
	
	For i% = 1 To CountSurfaces(m)
		s% = GetSurface(m, i)
		For it% = 0 To CountTriangles(s)-1
			v0% = TriangleVertex(s, it, 0)
			v1% = TriangleVertex(s, it, 1)
			v2% = TriangleVertex(s, it, 2)
			
			v0x# = VertexX(s, v0)
			v0y# = VertexY(s, v0)
			v0z# = VertexZ(s, v0)
			v0u# = VertexU(s, v0)
			v0v# = VertexV(s, v0)
			v0w# = VertexW(s, v0)
			v0nx# = VertexNX(s, v0)
			v0ny# = VertexNY(s, v0)
			v0nz# = VertexNZ(s, v0)
			v0red# = VertexRed(s, v0)
			v0green# = VertexRed(s, v0)
			v0blue# = VertexRed(s, v0)
			v0alpha# = VertexAlpha(s, v0)
			
			v1x# = VertexX(s, v1)
			v1y# = VertexY(s, v1)
			v1z# = VertexZ(s, v1)
			v1u# = VertexU(s, v1)
			v1v# = VertexV(s, v1)
			v1w# = VertexW(s, v1)
			v1nx# = VertexNX(s, v1)
			v1ny# = VertexNY(s, v1)
			v1nz# = VertexNZ(s, v1)
			v1red# = VertexRed(s, v1)
			v1green# = VertexRed(s, v1)
			v1blue# = VertexRed(s, v1)
			v1alpha# = VertexAlpha(s, v1)
			
			v2x# = VertexX(s, v2)
			v2y# = VertexY(s, v2)
			v2z# = VertexZ(s, v2)
			v2u# = VertexU(s, v2)
			v2v# = VertexV(s, v2)
			v2w# = VertexW(s, v2)
			v2nx# = VertexNX(s, v2)
			v2ny# = VertexNY(s, v2)
			v2nz# = VertexNZ(s, v2)
			v2red# = VertexRed(s, v2)
			v2green# = VertexRed(s, v2)
			v2blue# = VertexRed(s, v2)
			v2alpha# = VertexAlpha(s, v2)
						
			
			grad# = TriGradient(s, it)
			If grad < g1 Then 
				s2 = s_g0
			ElseIf grad < g2 Then 
				s2 = s_g1
			ElseIf grad < g3 Then 
				s2 = s_g2
			ElseIf grad < g4 Then 
				s2 = s_g3				
			Else
				s2 = s_g4
			End If

			v0% = AddVertex(s2, v0x, v0y, v0z, v0u, v0v, v0w)
			v1% = AddVertex(s2, v1x, v1y, v1z, v1u, v1v, v1w)
			v2% = AddVertex(s2, v2x, v2y, v2z, v2u, v2v, v2w)
			VertexNormal s2, v0, v0nx, v0ny, v0nz
			VertexNormal s2, v1, v1nx, v1ny, v1nz
			VertexNormal s2, v2, v2nx, v2ny, v2nz
			VertexColor s2, v0, v0red, v0green, v0blue, v0alpha
			VertexColor s2, v1, v1red, v1green, v1blue, v1alpha
			VertexColor s2, v2, v2red, v2green, v2blue, v2alpha
			AddTriangle s2, v0, v1, v2
		Next
	Next

	PaintSurface s_g0, bGrad0
	PaintSurface s_g1, bGrad1
	PaintSurface s_g2, bGrad2
	PaintSurface s_g3, bGrad3
	PaintSurface s_g4, bGrad4
	
	FreeEntity m
	Return m2
End Function


Function TriGradient#(surface%, triangle%)
	Local v0%,v1%,v2%
	Local v0x#, v0y#, v0z#
	Local v1x#, v1y#, v1z#
	Local v2x#, v2y#, v2z#
	Local tx#, ty#, tz#
	Local m1#, m2#, c1#, c2#
	Local ax#, ay#, az#
	Local theta#, argument#, modulus#
	Local i%
	
	;Special case 1: Is the whole triangle parallel to xz-plane?
	v0x = VertexX(surface, TriangleVertex(surface, triangle, 0))
	v0y = VertexY(surface, TriangleVertex(surface, triangle, 0))
	v0z = VertexZ(surface, TriangleVertex(surface, triangle, 0))
	v1x = VertexX(surface, TriangleVertex(surface, triangle, 1))
	v1y = VertexY(surface, TriangleVertex(surface, triangle, 1))
	v1z = VertexZ(surface, TriangleVertex(surface, triangle, 1))
	v2x = VertexX(surface, TriangleVertex(surface, triangle, 2))
	v2y = VertexY(surface, TriangleVertex(surface, triangle, 2))
	v2z = VertexZ(surface, TriangleVertex(surface, triangle, 2))
	If v0y = v1y And v1y = v2y Then
		;Yes, so gradient is 0
		Return 0
	End If
	
	;Step 1:
	;Sort out vertices in order of height (y-axis), v0=bottom, v1=middle, v2=top
	For i=0 To 2
        v% = TriangleVertex(surface, triangle, i)
  		If v0=0 Then v0 = v
		If v2=0 Then v2 = v
		
		If VertexY(surface,v) > VertexY(surface,v2) Then v2 = v
		If VertexY(surface,v) < VertexY(surface,v0) Then v0 = v
    Next
	For i=0 To 2
        v% = TriangleVertex(surface, triangle, i)
  		If v0<>v And v2<>v Then v1 = v
    Next
	v0x = VertexX(surface, v0)
	v0y = VertexY(surface, v0)
	v0z = VertexZ(surface, v0)
	v1x = VertexX(surface, v1)
	v1y = VertexY(surface, v1)
	v1z = VertexZ(surface, v1)
	v2x = VertexX(surface, v2)
	v2y = VertexY(surface, v2)
	v2z = VertexZ(surface, v2)
		
	;Step 2: Translate lowest point To 0,0,0
	tx = v0x
	ty = v0y
	tz = v0z
	v0x = v0x - tx
	v0y = v0y - ty
	v0z = v0z - tz
	v1x = v1x - tx
	v1y = v1y - ty
	v1z = v1z - tz
	v2x = v2x - tx
	v2y = v2y - ty
	v2z = v2z - tz
	
	;Special case 2: Is the line (v1,v2) is parallel to xz-plane?
	If v1y = v2y Then
		;Yes, so invert the triangle in the y-axis
		v0y = v1y
		v1y = 0
		v2y = 0
		;Lowest point is now v2, highest is v0, so swap them:
		tx# = v2x
		ty# = v2y
		tz# = v2z
		v2x = v0x
		v2y = v0y
		v2z = v0z
		v0x = tx
		v0y = ty
		v0z = tz
		;Re-do translation:
		v0x = v0x - tx
		v0y = v0y - ty
		v0z = v0z - tz
		v1x = v1x - tx
		v1y = v1y - ty
		v1z = v1z - tz
		v2x = v2x - tx
		v2y = v2y - ty
		v2z = v2z - tz
	End If
	
	
	;Step 3: Find Point a, such that y(a) = 0 and y is on the line (v2, v1)
	;Line Equations:
	;y=m1x+c1
	;y=m2z+c2
	m1 = (v2y-v1y) / (v2x-v1x)
	m2 = (v2y-v1y) / (v2z-v1z)
	c1 = v1y - m1 * v1x
	c2 = v1y - m1 * v1z
	ay = 0
	ax = -c1/m1
	az = -c2/m2
	
	;Special case 3: is v1 at y=0
	If v1y = 0 Then
		;Yes, therefore a = v1
		ax = v1x
		ay = v1y
		az = v1z
	End If
	
	;Step 4. Rotate all points v0,v1,v2, a about y-axis so that point a is at x=0
	theta# = -ATan2(az, ax)	;Angle to rotate
	;NOTE: Actually only v2's z-component will be used, so let's be lazy and only calculate v2z.
	;;Rotate v0
	;argument# = ATan2(v0z, v0x)
	;modulus# = Sqr(v0x^2 + v0z^2)
	;v0x = modulus * Cos(theta+argument)
	;v0z = modulus * Sin(theta+argument)
	;;Rotate v1
	;argument# = ATan2(v1z, v1x)
	;modulus# = Sqr(v1x^2 + v1z^2)
	;v1x = modulus * Cos(theta+argument)
	;v1z = modulus * Sin(theta+argument)
	;;Rotate v2
	argument# = ATan2(v2z, v2x)
	modulus# = Sqr(v2x^2 + v2z^2)
	;v2x = modulus * Cos(theta+argument)
	v2z = modulus * Sin(theta+argument)
	;;Rotate a
	;argument# = ATan2(az, ax)
	;modulus# = Sqr(ax^2 + az^2)
	;ax = modulus * Cos(theta+argument)
	;az = modulus * Sin(theta+argument)
	
	
	;Step 5. Calculate the gradient, and return the value.
	Return Abs(v2y / v2z)
End Function


;Example:
Graphics3D 800, 600, 0, 2

b0 =  LoadBrush("grass_flat.bmp",1+8)
b1 =  LoadBrush("grass_slope.bmp",1+8)
b2 =  LoadBrush("grass+rocks.bmp",1+8)
b3 =  LoadBrush("rocks.bmp",1+8)
b4 =  LoadBrush("cliff.bmp",1+8)

terrain = LoadMesh("terrain.3ds")
terrain = GradePaintTerrainMesh(terrain, 0.1, 0.3, 0.8, 1.2, b0, b1, b2, b3, b4)

cam = CreateCamera()

PositionEntity cam, 100, 100, 100
PointEntity cam, terrain

AmbientLight 200, 200, 200

MoveMouse GraphicsWidth()/2, GraphicsHeight()/2
FlushMouse

While Not KeyHit(1)
	SetBuffer BackBuffer()
	UpdateWorld
	RenderWorld
	VWait
	Flip

	dY# = EntityPitch(Cam)+MouseYSpeed()/2*0.5
	If dY > 89 Then dY = 89
	If dY < -89 Then dY = -89
	RotateEntity Cam, dY, EntityYaw(Cam)-(MouseXSpeed()/2)*0.5, 0
	MoveEntity Cam, 0, 0, (MouseDown(1)-MouseDown(2))*3
	MoveMouse GraphicsWidth()/2, GraphicsHeight()/2
Wend
End
