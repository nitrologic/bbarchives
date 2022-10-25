; ID: 1870
; Author: Danny
; Date: 2006-11-24 19:01:55
; Title: Point distance to a Line
; Description: Calculates the shortest distance between a point P and a line

Function PointDistanceToLine#( ax#,ay#,az#, bx#,by#,bz#, px#,py#,pz# )
;| Calculates the shortest distance between a point P(xyz) and a line segment defined by A(xyz) and B(xyz) - danny.

	;get the length of each side of the triangle ABP
	ab# = Sqr( (bx-ax)*(bx-ax) + (by-ay)*(by-ay) + (bz-az)*(bz-az) )
	bp# = Sqr( (px-bx)*(px-bx) + (py-by)*(py-by) + (pz-bz)*(pz-bz) )
	pa# = Sqr( (ax-px)*(ax-px) + (ay-py)*(ay-py) + (az-pz)*(az-pz) )

	;get the triangle's semiperimeter
	semi# = (ab+bp+pa) / 2.0
	
	;get the triangle's area
	area# = Sqr( semi * (semi-ab) * (semi-bp) * (semi-pa) )
	
	;return closest distance P to AB
	Return (2.0 * (area/ab))
	
End Function
