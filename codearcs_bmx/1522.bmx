; ID: 1522
; Author: Warpy
; Date: 2005-11-08 13:12:10
; Title: Catmull Rom splines
; Description: A short demo showing catmull rom splines

Rem
CATMULL ROM SPLINE

A Catmull Rom spline is a kind of curve. 
If you have a set of points , you can use Catmull Rom to construct
a nice curvy line joining them all up. It has the property of local
control , meaning that moving one point will only affect the piece of 
the line right next to it , and nothing further away than the next point.
It also doesn't require you to define a tangent at each point, unlike 
a bezier curve. Instead , the algorithm uses the points before and after
the segment it's working on to make a good curve.

THE ORIGINAL FUNCTION

Given 4 points on the spline P0 , P1 , P2 and P3 , q(t) gives you a point on
the bit of the curve between P1 and P2. q(0) is always equal to P1 and q(1)
is always equal to P2. It is not necessarily true that q(0.5) is halfway 
along the length of the curve, by distance.

q(t) = 0.5 *(
   				(2 * P1) +
  				(-P0 + P2) * t +
				(2*P0 - 5*P1 + 4*P2 - P3) * t2 +
				(-P0 + 3*P1- 3*P2 + P3) * t3
			)
			
Where P0 / 1 / 2 / 3 are the 4 control points on the spline , and q(t) is the point
on the curve at position t , where t = 0 is the start of the curve , and t = 1 is 
the end of the curve.

To actually use this, just split it into x and y parts, so:

qx(t) = 0.5 *(
   				(2 * P1x) +
  				(-P0x + P2x) * t +
				(2*P0x - 5*P1x + 4*P2x - P3x) * t2 +
				(-P0x + 3*P1x- 3*P2x + P3x) * t3
			)

qy(t) = 0.5 *(
   				(2 * P1y) +
  				(-P0y + P2y) * t +
				(2*P0y - 5*P1y + 4*P2y - P3y) * t2 +
				(-P0y + 3*P1y- 3*P2y + P3y) * t3
			)
			
Where each point PN has components PNx and PNy.

Clearly you can't compute every single point on the curve as there are infinitely many, so
what we do is compute the function for a certain number of values of t between 0 and 1, to
get a good enough approximation.


THE ALGORITHM

The way I've implemented the algorithm here is by defining two types: point and romspline.
A point is just an easy way of keeping track of points in 2d space - it has an x field
and a y field, that's all.
The romspline type holds all the information necessary to draw a spline. You create a romspline
object and then add points to it using the addpoint method. Calling the draw method draws the
spline on the screen.
What it does is start at the first 4 points in the spline. It then computes the catmull rom 
function at a number of values of t between 0 and 1, drawing a straight line between one and
the next. This gives a good approximation of the curve. Once that is done , it moves along one
point on the spline , so P0 becomes P1 , P1 becomes P2 , P2 becomes P3 , and P3 is the next point 
in the list. If there is no next point in the list , we're done and the function ends.
Because of the way the algorithm works , the part of the curve between the first and second 
points in the list and the penultimate and last points in the list won't be drawn. You can get 
around this by adding some more points to the start and end of the list.

EndRem


'This is just a little type to keep track of individual control points
Type point
	Field x#,y#
	
	Function create:point(x#,y#)
		p:point=New point
		p.x=x
		p.y=y
		Return p
	End Function
End Type

'Spline type
Type romspline

	Field points:TList 'the points list is a list of the control points to use in the spline
	
	Method New() '(This is called when the spline object is created, it just initialises the points list)
		points=New TList
	End Method
	
	Method addpoint(p:point) 'Call this to add a point to the end of the list
		points.addlast p
	End Method
	
	Method draw() 'Do the actual drawing!
	
		'Draw a rectangle at each control point so we can see them (not relevant to the algorithm)
		For p:point = EachIn points
			DrawRect p.x , p.y , 2 , 2
		Next
	
		num=points.count()
		If num<4 Then Return 'Check there are enough points to draw a spline

		'Get the first three  TLinks in the list of points. This algorithm is going to work by 
		'working out the first three points, then getting the last point at the start of the
		'while loop. After the curve section has been drawn, every point is moved along one,
		'and the TLink is moved to the next one so we can see if it's null, and then get the next
		'p3 from it if not.
		pl:TLink = points.firstlink()
		p0:point = point(pl.value() )
		pl = pl.nextlink()
		p1:point = point(pl.value() )
		pl = pl.nextlink()
		p2:point = point(pl.value() )
		pl = pl.nextlink()
		'we'll work out what p3 is inside the loop
		
		While pl <> Null 'pl3 will be null when we've reached the end of the list
			
			'get the point objects from the TLinks
			p3:point = point(pl.value() )
			
			ox# = p1.x
			oy# = p1.y
			For t#=0 To 1 Step .01 'THE MEAT And BONES! Oddly, there isn't much to explain here, just copy the code.
				x# = .5 * ( (2 * p1.x) + (p2.x - p0.x) * t + (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t * t + (3 * p1.x - p0.x - 3 * p2.x + p3.x) * t * t * t)
				y# = .5 * ( (2 * p1.y) + (p2.y - p0.y) * t + (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t * t + (3 * p1.y - p0.y - 3 * p2.y + p3.y) * t * t * t)
				DrawLine ox , oy , x , y
				
				ox = x
				oy = y
				
			Next
			
			'Move one place along the list
			p0 = p1
			p1 = p2
			p2 = p3
			pl=pl.nextlink()
		Wend
	End Method
End Type

'Demo - create a spline and add points when the mouse is clicked

Graphics 800 , 800 , 0

s:romspline = New romspline

While Not KeyHit(KEY_ESCAPE)

	If MouseHit(1)
		s.addpoint(point.create(MouseX(),MouseY()))
	EndIf
	
	s.draw()
	Flip
	Cls
Wend
