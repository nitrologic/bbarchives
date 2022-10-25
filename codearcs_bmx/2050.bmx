; ID: 2050
; Author: Warpy
; Date: 2007-06-27 04:30:19
; Title: Convex Hull
; Description: Given a set of points, find smallest convex polygon containing them all.

Rem

HOW IT WORKS

Function Quickhull (takes a set of points, returns the points on the hull in clockwise order):
Pick a line AB which you know is a chord of the hull. The line between the leftmost and rightmost points is a suitable one.
Put everything to the left of AB in a set s1 , and everything to the right of AB in a set s2.
Then call findhull on s1 and s2, and join together the resulting lists.
A is the first point on the hull, then the results of findhull on s1, then B, then the results of findhull on s2.

Function Findhull (takes a set of points sk and a line AB, returns some of the points on the hull in order)
Find the point C which is furthest from AB.
Ignore the points inside ABC as they can't be in the hull.
Split sk into points which are on the left of AC (called s1) and those on the right (called s2)
Call findhull again on s1 and AC , then on s2 and CB. 
The list of points returned is the result of s1 plus C plus the result of s2.


EndRem


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

'Quickhull function - call this one with a set of points.
Function quickhull:TList(s:TList)
	If s.count()<=3 Return s
	l:point=Null
	r:point=Null
	For p:point=EachIn s
		If l=Null
			l=p
		ElseIf p.x<l.x
			l=p
		EndIf
		If r=Null
			r=p
		ElseIf p.x>r.x
			r=p
		EndIf
	Next
	
	an#=ATan2(r.y-l.y,r.x-l.x)
	rx#=Cos(an)
	ry#=Sin(an)
	sx#=Cos(an+90)
	sy#=Sin(an+90)
	
	s1:TList=New TList
	s2:TList=New TList
	For p:point=EachIn s
		If p<>l And p<>r
			mu#=(l.y-p.y+(ry/rx)*(p.x-l.x))/(sy-sx*ry/rx)
			If mu<0 
				s1.addlast p 
			ElseIf mu>0
				s2.addlast p
			EndIf
		EndIf
	Next
	
	out1:TList=findhull(s1,l,r)
	out2:TList=findhull(s2,r,l)
	out:TList = New TList
	out.addlast l
	If out1
		For o:Object=EachIn out1
			out.addlast o
		Next
	EndIf
	out.addlast r
	If out2
		For o:Object=EachIn out2
			out.addlast o
		Next
	EndIf
	
	Return out
End Function

'Findhull helper function - you never need to call this
Function findhull:TList(sk:TList,p:point,q:point)
	If Not sk.count() Return Null
	c:point=Null
	out:TList=New TList
	maxdist#=-1
	an#=ATan2(q.y-p.y,q.x-p.x)
	rx#=Cos(an)
	ry#=Sin(an)
	sx#=-ry
	sy#=rx
	For tp:point=EachIn sk
		If tp<>p And tp<>q
			mu#=(p.y-tp.y+(ry/rx)*(tp.x-p.x))/(sy-sx*ry/rx)
			If maxdist=-1 Or Abs(mu)>maxdist
				c=tp
				maxdist=Abs(mu)
			EndIf
		EndIf
	Next
	an#=ATan2(c.y-p.y,c.x-p.x)
	rx#=Cos(an)
	ry#=Sin(an)
	sx#=Cos(an+90)
	sy#=Sin(an+90)
	s1:TList=New TList
	s2:TList=New TList
	For tp:point=EachIn sk
		If tp<>c
			If Not pointintriangle(tp.x,tp.y,p.x,p.y,q.x,q.y,c.x,c.y)
				mu#=(p.y-tp.y+(ry/rx)*(tp.x-p.x))/(sy-sx*ry/rx)
				If mu<0 s1.addlast tp ElseIf mu>0 s2.addlast tp
			EndIf
		EndIf
	Next
	out1:TList=findhull(s1,p,c)
	out2:TList=findhull(s2,c,q)
	If out1
		For o:Object=EachIn out1
			out.addlast o
		Next
	EndIf
	out.addlast c
	If out2
		For o:Object=EachIn out2
			out.addlast o
		Next
	EndIf
	Return out
End Function


'''DEMO Left click to place points, right click to compute hull, left click again to clear screen

Graphics 800 , 800 , 0

points:tlist = New tlist
state=0
While Not KeyHit(KEY_ESCAPE)
	For p:point = EachIn points
		DrawRect p.x , p.y , 1 , 1
	Next
	
	Select state
	Case 0
		If MouseHit(1)
			p:point = New point
			p.x = MouseX()
			p.y = MouseY()
			points.addlast(p)
		EndIf
		If MouseHit(2)
			h:tlist = quickhull(points) 
			state = 1
		EndIf
	Case 1
		n = 0
		ox# = - 1
		oy#=-1
		For p:point = EachIn h
			If ox >= 0
				DrawLine ox , oy , p.x , p.y
			EndIf
			ox = p.x
			oy = p.y
			DrawText n , p.x , p.y
			n:+ 1
		Next
		If MouseHit(1)
			points = New tlist
			state = 0
		EndIf
	End Select
	Flip
	Cls
Wend
