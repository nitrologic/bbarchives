; ID: 206
; Author: Chroma
; Date: 2002-05-05 15:03:46
; Title: Point an Entity at x,y,z 
; Description: Does exactly what it says.  Works perfect and FAST!!!  

;===========================
;Point Entity at x,y,z
;===========================
Function Point_Entity(entity,x#,y#,z#)
	xdiff# = EntityX(entity)-x#
	ydiff# = EntityY(entity)-y#
	zdiff# = EntityZ(entity)-z#
	dist#=Sqr#((xdiff#*xdiff#)+(zdiff#*zdiff#))
	pitch# = ATan2(ydiff#,dist#)
	yaw#   = ATan2(xdiff#,-zdiff#)
	RotateEntity entity,pitch#,yaw#,0
End Function
;===========================
