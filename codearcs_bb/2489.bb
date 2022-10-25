; ID: 2489
; Author: Warner
; Date: 2009-05-25 17:48:58
; Title: transform point onto plane
; Description: transform point from plane to realworld coords

;input  = (X,Y) on plane
;output = Transformed point
Global resultx#, resulty#, resultz#
Function PointOntoPlane(x#, y#, z#, nx#, ny#, nz#, d#)

	a# = 0
	c# = ATan2(nx, ny)
	b# = ATan2(nz, Sqr(nx*nx+ny*ny))
		
	;apply yaw to point
	kx# = (Cos(-c) * z) - (Sin(-c) * x)
	ky# = y
	kz# = (Sin(-c) * z) + (Cos(-c) * x)
		
    ;apply pitch to point
	jx# = kx
	jy# = (Cos(-b) * ky) - (Sin(-b) * kz)
	jz# = (Sin(-b) * ky) + (Cos(-b) * kz)
			
    ;apply roll to point
	ix# = (Cos(-a) * jx) - (Sin(-a) * jy)
	iy# = (Sin(-a) * jx) + (Cos(-a) * jy)
	iz# = jz

    ;apply plane offset
	resultx# = ix# - nx*d
	resulty# = iy# - ny*d
	resultz# = iz# - nz*d

End Function
;Resources:
;http://www.geocities.com/siliconvalley/2151/math3d.html
;http://www.gamedev.net/community/forums/topic.asp?topic_id=399701
