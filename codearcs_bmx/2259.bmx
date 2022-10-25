; ID: 2259
; Author: Warpy
; Date: 2008-05-31 16:50:41
; Title: Polygon triangulation - subtracting ears method
; Description: Cut a polygon into triangles by clipping off 'ears'

'TRIANGULATING A POLYGON BY SUBTRACTING EARS!

'Triangulation is the problem of how to cut up a polygon into triangles, so that all the triangles together make up the whole polygon.

'This method works by noticing that a polygon with no holes in it always has two ears.
'An 'ear' is a triangle consisting of two edges from the boundary of the polygon, and a third line on the inside of the polygon
'joining them up, but not crossing the boundary on the way.

'If you clip an ear off the polygon, you are left with a smaller polygon (or, more importantly, a polygon with fewer vertices)
'which you can perform the subtracting ears operation on again. You repeat the process until you are left with a 3-sided polygon,
'which of course is already triangulated!

'The set of all the ears you've clipped off forms a triangulation of the polygon.



Function triangulate:TList(points:TList) 
'this function takes a list of the vertices of a polygon (in order) as input, and returns a list of triangles.

	points = points.Copy()   'this algorithm works by removing points from the list, so we make a copy of it and leave the original intact
	
	c = points.count()  'we keep track of how many points are in our working polygon so that we know when to stop!
	If c < 3 Return New TList 'error-checking: fewer than 3 points doesn't make a polygon
	
	l:TList = New TList 'this list will store all our triangles
	While c>3	
		Local array:point[]
		array = point[] (points.toarray())  'make an array from the list of points, for easier referencing
	
		
		i = 0
		go = 0
		While Not go
			p1:point=array[i]
			p2:point=array[(i+1) Mod c]
			p3:point = array[(i + 2) Mod c] 
			
			'p1,p2,p3 are consecutive points on the boundary of the polygon.
			'consider the triangle p1->p2->p3
			
			midx:Float = (p1.x + p2.x + p3.x) / 3.0	'(midx,midy) is a point inside the candidate triangle
			midy:Float = (p1.y + p2.y + p3.y) / 3.0
			
			'here we check if (midx,midy) is inside the polygon. An 'S'-bend in the polygon can cause the candidate triangle
			'to actually be on the outside of the polygon, making it useless in a triangulation.
			'This check works by counting the number of times a horizontal ray originating from (midx,midy) crosses the boundary of the polygon
			'if hits is odd, then (midx,midy) is inside the polygon.
			hits=0
			For ii = 0 To c - 1
				x1#=array[ii].x
				y1#=array[ii].y
				x2#=array[(ii+1) Mod c].x
				y2#=array[(ii+1) Mod c].y
				If (y1-midy)*(y2-midy)<0
					ix#=x1+(x2-x1)*(midy-y1)/(y2-y1)
					If ix<midx hits:+1
				EndIf
			Next

			If (hits Mod 2) 'tri is inside polygon
			
				'We now know the triangle is inside the polygon, so the last thing we need to check is that the line p3->p1
				'doesn't cross the boundary at any point.
				
				x1#=p1.x
				y1#=p1.y
				x2#=p3.x
				y2#=p3.y
				dx1#=x2-x1
				dy1#=y2-y1
				
				go=1
				n=(i+3) Mod c
				While n<>i
					x3#=array[n].x
					y3#=array[n].y
					dx2#=x3-x2
					dy2#=y3-y2
					
					If dx1<>dx2 Or x1<>x2 Or dy1<>dy2 Or y1<>y2
						lambda#=(y2-y1+dy2*(x1-x2)/dx2)/(dy1-dx1*dy2/dx2)
						mu#=(x1-x2+lambda*dx1)/dx2
						If lambda>0 And lambda<1
							If mu>=0 And mu<=1
								go=0
							EndIf
						EndIf
					EndIf
					x2=x3
					y2=y3
					n=(n+1) Mod c
				Wend
			EndIf
			
			If Not go 'if go=0, then our line crossed the boundary at some point, so this triangle isn't an ear.
				i=(i+1) Mod c
				If i=0 Return Null
			EndIf
		Wend

		'by the time we get out of that while loop, we know that the triangle p1->p2->p3 is an ear, so can be clipped
		t:tri = tri.Create(p1, p2, p3) 
		
		'this is just some drawing code so you can see the algorithm working
		draweverything(points, l) 
		SetColor 255, 0, 0
		t.draw() 
		SetColor 255, 255, 255
				
		Flip
		Cls
		Delay 500

		
		'remove p2 from the list of points - this is the same as removing the whole ear from the polygon - now there is no way 
		'p1->p2->p3 will be considered again.
		points.remove p2
		
		l.addlast t	'add the triangle to our list of ears
		c:-1	'we've removed a point

	Wend
	
	'we're left with a single triangle, but it's not in our list of ears yet, so we need to add it
	array=point[](points.toarray())
	t:tri=tri.Create(array[0],array[1],array[2])
	l.addlast t
	
	'done! return the list of triangles
	Return l
	
End Function


Function draweverything(points:TList, triangles:TList) 
	For t:tri = EachIn triangles
		t.draw() 
	Next
	
	op:point = Null
	For p:point = EachIn points
		p.draw() 
		If op
			DrawLine op.x, op.y, p.x, p.y
		End If
		op = p
	Next
	If op
		p = point(points.First()) 
		DrawLine op.x, op.y, p.x, p.y
	EndIf
End Function

Type tri
	Field p1:point,p2:point,p3:point
	
	Function Create:tri(p1:point,p2:point,p3:point)
		t:tri=New tri
		t.p1=p1
		t.p2=p2
		t.p3=p3
		Return t
	End Function
	
	Method draw()
		Local poly:Float[] 
		SetAlpha.5
		poly =[p1.x, p1.y, p2.x, p2.y, p3.x, p3.y] 
		DrawPoly poly
		SetAlpha 1
		DrawLine p1.x, p1.y, p2.x, p2.y
		DrawLine p2.x, p2.y, p3.x, p3.y
		DrawLine p3.x, p3.y, p1.x, p1.y
	End Method
End Type

Type point
	Field x#,y#
	
	Function Create:point(x:Float, y:Float) 
		p:point=New point
		p.x=x
		p.y=y
		Return p
	End Function
	
	Method draw()
		DrawRect x-1,y-1,3,3
	End Method
End Type


'Demo - left click to place points, then right click when you have 3 or more to run the triangulation algorithm.

Graphics 600, 600, 0
SetBlend ALPHABLEND
points:TList = New TList
triangles:TList = New TList
While Not (KeyHit(KEY_ESCAPE) Or AppTerminate()) 

	If MouseHit(1) 
		points.AddLast point.Create(MouseX(), MouseY()) 
	End If
	
	If MouseHit(2) And points.Count() >= 3
		triangles = triangulate(points) 
	End If
	
	draweverything(points, triangles) 
		
	Flip
	Cls
WEnd
