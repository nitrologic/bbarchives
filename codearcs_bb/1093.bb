; ID: 1093
; Author: Mr Snidesmin
; Date: 2004-06-21 22:43:39
; Title: Gradient Of a Triangle
; Description: Finds the maximum gradient of a triangle's defined plane

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



Function Test#(v0x#, v0y#, v0z#, v1x#, v1y#, v1z#, v2x#, v2y#, v2z#)
	m% = CreateMesh()
	s% = CreateSurface(m)
	AddVertex s, v0x, v0y, v0z
	AddVertex s, v1x, v1y, v1z
	AddVertex s, v2x, v2y, v2z
	AddTriangle s, 0,1,2
	Return TriGradient(s, 0)
End Function

Graphics3D 800, 600, 0, 2
Print Test(0,0,0,   10,1,0,   20,0,0)
WaitKey
End
