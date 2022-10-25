; ID: 369
; Author: sswift
; Date: 2002-07-19 17:03:51
; Title: Ray Intersect Triangle Test
; Description: This function tests if a ray intersects a triangle, and if so returns true.  It also calculates the UV cordinates of the collision.

; -------------------------------------------------------------------------------------------------------------------
; This function returns TRUE if a ray intersects a triangle.
;
; It also calculates the UV coordinates of said colision as part of the intersection test,
; but does not return them.
;
; V0xyz, V1xyz, and V2xyz are the locations of the three vertices of the triangle.
;
; V0 is location of UV(0,0).  V1 is the location of UV(1, 0).  V2 is the location of UV(0,1)
; These are important to know if you want to return the exact location in texture space of the collision, but
; you don't have to worry about them if you only want to find out if a collision occured.
;
; Pxyz is a point on the line.
;
; Dxyz is a vector providing the slope of the line.
; -------------------------------------------------------------------------------------------------------------------
Function Ray_Intersect_Triangle(Px#, Py#, Pz#, Dx#, Dy#, Dz#, V0x#, V0y#, V0z#, V1x#, V1y#, V1z#, V2x#, V2y#, V2z#)


	; A couple definitions for vector operations:

		;crossproduct(a,b,c) =
		;ax = (by * cz) - (cy * bz) 
		;ay = (bz * cx) - (cz * bx) 	
		;az = (bx * cy) - (cx * by)

		;dotproduct(v,q) = (vx * qx) + (vy * qy) + (vz * qz)	


	; vector(e1,v1,v0)
	E1x# = V1x# - V0x#
	E1y# = V1y# - V0y#
	E1z# = V1z# - V0z#


	; vector(e2,v2,v0)
	E2x# = V2x# - V0x#
	E2y# = V2y# - V0y#
	E2z# = V2z# - V0z#


	; crossproduct(h,d,e2)
	Hx# = (Dy# * E2z#) - (E2y# * Dz#)
	Hy# = (Dz# * E2x#) - (E2z# * Dx#)
	Hz# = (Dx# * E2y#) - (E2x# * Dy#)


	; a = dotproduct(e1,h)
	A# = (E1x# * Hx#) + (E1y# * Hy#) + (E1z# * Hz#)

	
	; If the ray is parallel to the plane then it does not intersect it.
	If (A# > -0.00001) And (A# < 0.00001) 
		Return False
	EndIf

	
	F# = 1.0 / A#
	

	; vector(s,p,v0)
	Sx# = Px# - V0x#
	Sy# = Py# - V0y#
	Sz# = Pz# - V0z#

		
	;u = f * (dotProduct(s,h))
	U# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))

	
	; If the value of the U coordinate is outside the range of values inside the triangle,
	; then the ray has intersected the plane outside the triangle.
	If (U# < 0.0) Or (U# > 1.0)
		Return False
	EndIf

	
	; crossProduct(q,s,e1)
	Qx# = (Sy# * E1z#) - (E1y# * Sz#)
	Qy# = (Sz# * E1x#) - (E1z# * Sx#)
	Qz# = (Sx# * E1y#) - (E1x# * Sy#)
	

	; v = f * dotProduct(d,q)
	V# = F# * ((Dx# * Qx#) + (Dy# * Qy#) + (Dz# * Qz#))

	
	; If the value of the V coordinate is outside the range of values inside the triangle,
	; then the ray has intersected the plane outside the triangle.
	;
	; U + V cannot exceed 1.0 or the point is not in the triangle. 
	;
	; If you imagine the triangle as half a square this makes sense.  U=1 V=1 would be  in the 
	; lower left hand corner which would be in the second triangle making up the square.
	If (V# < 0.0) Or ((U# + V#) > 1.0)
		Return(False)
	EndIf
	

	; The point was in the triangle.  Yay!		
	Return True

		
	; Note that you could also return the U and V coordinates calculated from this function
	; if you need those values!


End Function
