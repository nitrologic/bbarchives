; ID: 2180
; Author: Jasu
; Date: 2008-01-08 10:43:16
; Title: 2D math functions
; Description: Intersection, line to point distance and line to line distance functions

Global IntersectX#, IntersectY#


Function Intersect%(x1#, y1#, x2#, y2#, x3#, y3#, x4#, y4#)
	; This function returns True if lines x1,y1,x2,y2 and x3,y3,x4,y4 intersect at some point.
	Return (Orientation(x1, y1, x2, y2, x3, y3) <> Orientation(x1, y1, x2, y2, x4, y4)) And (Orientation(x3, y3, x4, y4, x1, y1) <> Orientation(x3, y3, x4, y4, x2, y2))

End Function


Function Orientation% ( x1#,y1#, x2#,y2#, Px#,Py# )
	; Linear determinant of the 3 points.
	; This function returns the orientation of px,py on line x1,y1,x2,y2.
	; Look from x2,y2 to the direction of x1,y1.
	; If px,py is on the right, function returns +1
	; If px,py is on the left, function returns -1
	; If px,py is directly ahead or behind, function returns 0
	Return Sgn((x2 - x1) * (Py - y1) - (Px - x1) * (y2 - y1))
End Function


Function IntersectPoint ( x1#,y1#, x2#,y2#, x3#,y3#, x4#,y4# )
	;Function returns the X,Y position of the two intersecting lines.
	;IntersectX and IntersectY are global variables of the main program.
	;The lines are infinite, is line1 goes through x1,y1,x2,y2 and line2 goes through x3,y3,x4,y4.
	;For line segments you must check if the lines truly intersect with the function Intersect% before you use this.
	
	Local dx1# = x2 - x1
	Local dx2# = x4 - x3
	Local dx3# = x1 - x3
	
	Local dy1# = y2 - y1
	Local dy2# = y1 - y3
	Local dy3# = y4 - y3
	
	Local R# = dx1 * dy3 - dy1 * dx2
	
	If R <> 0 Then
		R  = (dy2 * (x4 - x3) - dx3 * dy3) / R
		IntersectX = x1 + R * dx1
		IntersectY = y1 + R * dy1
	Else
		If (((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)) = 0) Then
			IntersectX = x3 
			IntersectY = y3
		Else
			IntersectX = x4
			IntersectY = y4
		EndIf
	EndIf
	
End Function

Function NormalDistance# ( lx1#,ly1#, lx2#,ly2#, x#,y# )
	; This function calculates the lenght of the normal from point to line.
	; The line is infinite, so the points lx1,ly1 and lx2,ly2 only determine that the line passes through them.
	; So the 'line' does not start from lx1,ly1 and end to lx2,ly2.
	; For a line segment, use function DistanceToLineSegment#

	Local dx#=lx2-lx1
	Local dy#=ly2-ly1
	
	Local d#=Sqr(dx*dx+dy*dy)
	
	;px#=(lx1-x)
	;py#=(ly1-y)
	
	;dist#=(dx*py-px*dy) / d
	
	Return Abs((dx*(ly1-y)-(lx1-x)*dy) / d)
	
End Function


Function DistanceToLineSegment# ( x1#,y1#, x2#,y2#, Px#,Py# )
	; This function calculates the distance between a line segment and a point.
	; So this function is useful to determine if line intersects a circle.
	; To also determine the point on the line x1,y1,x2,y2 which is the closest to px,py , use function NearestPointInLine#
	
	Local Dx#, Dy#, Ratio#
	
	If (x1 = x2) And (y1 = y2) Then
		Return Sqr( (Px-x1)*(Px-x1)+(Py-y1)*(Py-y1) )
	Else
		
		Dx#    = x2 - x1
		Dy#    = y2 - y1
		Ratio# = ((Px - x1) * Dx + (Py - y1) * Dy) / (Dx * Dx + Dy * Dy)
		
		If Ratio < 0 Then
			Return Sqr( (Px-x1)*(Px-x1)+(Py-y1)*(Py-y1) )
		ElseIf Ratio > 1 Then
			Return Sqr( (Px-x2)*(Px-x2)+(Py-y2)*(Py-y2) )
		Else
			Return Sqr ((Px - ((1 - Ratio) * x1 + Ratio * x2))*(Px - ((1 - Ratio) * x1 + Ratio * x2))+(Py - ((1 - Ratio) * y1 + Ratio * y2))*(Py - ((1 - Ratio) * y1 + Ratio * y2)))
		EndIf
		
	EndIf
	
End Function


Function NearestPointInLine# ( lx1#,ly1#, lx2#,ly2#, x#,y# )
	; This function calculates the point between lx1,ly1 and lx2,ly2 which is the nearest to x,y.
	; Result is put in global variables IntersectX,IntersectY
	; Function also returns the distance between x,y and the calculated point.
		
	Local dx#=lx2-lx1
	Local dy#=ly2-ly1
	;d# = Sqr(dx*dx+dy*dy)
	;ux# = dx/d
	;uy# = dy/d
	Local Ori1% = Orientation(lx1,ly1, (lx1+dy),(ly1-dx), x,y)
	Local Ori2% = Orientation(lx2,ly2, (lx2+dy),(ly2-dx), x,y)
	If (Ori1 = 1 And Ori2 = 1) Or Ori2 = 0 Then
		IntersectX = lx2
		IntersectY = ly2
	ElseIf (Ori1 = -1 And Ori2 = -1) Or Ori1 = 0 Then
		IntersectX = lx1
		IntersectY = ly1
	Else
		IntersectPoint( lx1,ly1, lx2,ly2, x,y, x+dy,y-dx )
	EndIf
	Return Sqr((x-IntersectX)*(x-IntersectX)+(y-IntersectY)*(y-IntersectY))
	
End Function

Function TwoLineDistance#( x1#,y1#, x2#,y2#, x3#,y3#, x4#,y4# )
	; This function returns the distance between two line segments
	
	Local Dt#
	Local sc#
	Local sN#
	Local sD#
	Local tc#
	Local tN#
	Local tD#
	Local dx#
	Local dy#
	
	Local ux# = x2 - x1
	Local uy# = y2 - y1
	Local vx# = x4 - x3
	Local vy# = y4 - y3
	Local wx# = x1 - x3
	Local wy# = y1 - y3
	
	Local a# = (ux * ux + uy * uy)
	Local b# = (ux * vx + uy * vy)
	Local c# = (vx * vx + vy * vy)
	Local d# = (ux * wx + uy * wy)
	Local e# = (vx * wx + vy * wy)
	
	Dt = a * c - b * b
	sD = Dt
	tD = Dt
	
	If Abs(Dt)<0.0001 Then
		sN = 0.0
		sD = 1.0
		tN = e
		tD = c
	Else
		sN = (b * e - c * d)
		tN = (a * e - b * d)
		If sN < 0.0 Then
			sN = 0.0
			tN = e
			tD = c
		ElseIf sN > sD Then
			sN = sD
			tN = e + b
			tD = c
		EndIf
	EndIf
	
	If tN < 0.0 Then
		tN = 0.0
		If -d < 0.0 Then
			sN = 0.0
		ElseIf -d > a Then
			sN = sD
		Else
			sN = -d
			sD = a
		EndIf
	ElseIf tN > tD Then
		tN = tD
		If (-d + b) < 0.0 Then
			sN = 0
		Else If (-d + b) > a Then
			sN = sD
		Else
			sN = (-d + b)
			sD = a
		EndIf
	EndIf
	
	If Abs(sN) < 0.0001 Then sc = 0.0 Else sc = sN / sD
	If Abs(tN) < 0.0001 Then tc = 0.0 Else tc = tN / tD
	
	dx = wx + (sc * ux) - (tc * vx)
	dy = wy + (sc * uy) - (tc * vy)
	
	Return Sqr(dx * dx + dy * dy)
	
End Function
