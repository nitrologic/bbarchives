; ID: 1294
; Author: sswift
; Date: 2005-02-17 15:35:27
; Title: 2D Point in Triangle
; Description: Thsi function tells you if a 2D point is in a 2D triangle

; -------------------------------------------------------------------------------------------------------------------
; This function tells you if a point is inside a triangle, in 2D.
; It also calculates the UV coordinates of said point as part of the intersection test, but does not return them.
;
; Pxy is a point.
;
; V0xy, V1xy, and V2xy, are the locations of the three vertices of the triangle.
;
; For these vertices, V0 is location of UV(0,0), V1 is the location of UV(1, 0), and V2 is the location of UV(0,1)
;
; These are important to know if you want to return the exact location in texture space of the collision, but
; you don't have to worry about them if you only want to find out if a collision occured.
; -------------------------------------------------------------------------------------------------------------------
Function PointInTri(Px#, Py#, V0x#, V0y#, V1x#, V1y#, V2x#, V2y#)

	; vector(e1,v1,v0)
	E1x# = V1x# - V0x#
	E1y# = V1y# - V0y#

	; vector(e2,v2,v0)
	E2x# = V2x# - V0x#
	E2y# = V2y# - V0y#

	; crossproduct(h,d,e2)
	Hx# = -E2y#
	Hy# =  E2x# 

	; a = dotproduct(e1,h)
	A# = (E1x# * Hx#) + (E1y# * Hy#) 
	
	F# = 1.0 / A#
	
	; vector(s,p,v0)
	Sx# = Px# - V0x#
	Sy# = Py# - V0y#
		
	;u = f * (dotProduct(s,h))
	U# = F# * ((Sx# * Hx#) + (Sy# * Hy#))
	
	; If the value of the U coordinate is outside the range of values inside the triangle,
	; then the ray has intersected the plane outside the triangle.
	If (U# < 0) Or (U# > 1)
		Return False
	EndIf
	
	; crossProduct(q,s,e1)
	Qz# = (Sx# * E1y#) - (E1x# * Sy#)

	; v = f * dotProduct(d,q)
	V# = F# * Qz#
	
	; If the value of the V coordinate is outside the range of values inside the triangle,
	; then the ray has intersected the plane outside the triangle.
	If (V# < 0) Or (V# > 1) Then Return False

	; U + V together cannot exceed 1.0 or the point is not in the triangle. 
	; If you imagine the triangle as half a square this makes sense.  U=1 V=1 would be in the 
	; lower left hand corner which would be in the second triangle making up the square.
	If (U# + V#) > 1 Then Return False

	; The point was in the triangle. Yay!		
	Return True
		
	; Note that you could also return the U and V coordinates calculated in this function
	; if you need those values!

End Function
