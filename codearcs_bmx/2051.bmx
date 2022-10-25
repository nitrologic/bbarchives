; ID: 2051
; Author: Warpy
; Date: 2007-06-27 04:31:01
; Title: Point inside convex polygon
; Description: For the special case when a polygon is convex, it's easy to see if a point is inside it.

'little type for keeping track of 2d points
Type point
	Field x#,y#
	
	Function create:point(x#,y#)
		p:point=New point
		p.x=x
		p.y=y
		Return p
	End Function
End Type

'returns True if p1 and p2 are on the same side of the line a->b
Function sameside(p1x#,p1y#,p2x#,p2y#,ax#,ay#,bx#,by#)
	cp1# = (bx-ax)*(p1y-ay)-(p1x-ax)*(by-ay)
	cp2# = (bx-ax)*(p2y-ay)-(p2x-ax)*(by-ay)
	If cp1*cp2 >= 0 Then Return True
End Function	
	
'Clever little trick for telling if a point is inside a given triangle
'If for each pair of points AB in the triangle, P is on the same side of AB as 
'the other point in the triangle, then P is in the triangle. 
Function pointintriangle(px#,py#,ax#,ay#,bx#,by#,cx#,cy#)
	If sameside(px,py,ax,ay,bx,by,cx,cy) And sameside(px,py,bx,by,ax,ay,cx,cy) And sameside(px,py,cx,cy,ax,ay,bx,by)
		Return True
	Else
		Return False
	EndIf
End Function

'returns True if (x,y) is inside convex polygon defined by list of points
'points must be in clockwise (or anticlockwise) order.
'won't work for just any polygon!
'Works by splitting polygon into triangles, and checking each of those
Function pointinside(x# , y# , points:tlist)
	p1:point = Null
	p2:point = Null
	For p:point = EachIn points
		If p1
			If p2
				If pointintriangle(x , y , p1.x , p1.y , p2.x , p2.y , p.x , p.y)
					Return True
				EndIf
			EndIf
			p2=p
		Else
			p1 = p
		EndIf
	Next
	Return False
End Function
