; ID: 2448
; Author: Warpy
; Date: 2009-03-30 05:21:19
; Title: Intersection of line and rectangle
; Description: Find the point of intersection of a line and a rectangle

Function boxintersect(last_x#, last_y#, x#, y#, bx1#, by1#, bx2#, by2#, ix# Var, iy# Var)
	'finds point of intersection of line from (last_x,last_y) to (x,y)
	'with the box (bx1,by1,bx2,by2). the ix and iy parameters are var pointers, which means
	'the function fills in the values of the point of intersection in the variables that you give.
	'the function also returns true if there is an intersection, and false if there is none.
	
	If Not (x>=bx1 And x<=bx2 And y>=by1 And y<=by2) Return False
	
	If last_x < bx1 And x >= bx1 		'does it cross left edge?
		iy# = last_y + (y - last_y) * (bx1-last_x)/(x-last_x)
		If iy>=by1 And iy<=by2			'is intersection point on left edge?
			ix# = bx1
			Return True
		EndIf
	ElseIf last_x > bx2 And x <= bx2	'does it cross right edge?
		iy# = last_y + (y - last_y) * (bx2 - last_x)/(x - last_x)
		If iy>=by1 And iy<=by2			'is intersection point on right edge?
			ix# = bx2
			Return True
		EndIf
	EndIf
	
	If last_y < by1 And y >= by1 		'does it cross top edge?
		ix# = last_x + (x - last_x) * (by1 - last_y)/(y - last_y)
		If ix>=bx1 And ix<=bx2			'is intersection point on top edge?
			iy# = by1
			Return True
		EndIf
	ElseIf last_y > by2 And y <= by2	'does it cross bottom edge?
		ix# = last_x + (x - last_x) * (by2 - last_y)/(y - last_y)
		If ix>=bx1 And ix<=bx2			'is intersection point on bottom edge?
			iy# = by2
			Return True
		EndIf
	EndIf
End Function





'example
Graphics 800,600,0

Global box1#,boy1#,box2#,boy2#
Global ox#,oy#,nx#,ny#

Function maketest()
	box1=Rnd(200,300)
	boy1=Rnd(100,200)
	box2=Rnd(500,600)
	boy2=Rnd(400,500)
	
	nx=Rnd(box1,box2)
	ny=Rnd(boy1,boy2)
	an#=Rnd(360)
	ox=300*Cos(an)+400
	oy=300*Sin(an)+300
	
End Function

maketest

While Not (KeyHit(KEY_ESCAPE) Or AppTerminate())

	If KeyHit(KEY_SPACE)
		'make a new box and line
		maketest
	EndIf
	
	DrawLine box1,boy1,box2,boy1
	DrawLine box1,boy1,box1,boy2
	DrawLine box1,boy2,box2,boy2
	DrawLine box2,boy1,box2,boy2
	
	DrawLine ox,oy,nx,ny
	
	Local ix#,iy#	'variables to store point of intersection in
	boxintersect( ox,oy,nx,ny, box1,boy1,box2,boy2, ix,iy )
	
	DrawOval ix-3,iy-3,6,6
	

	Flip
	Cls
Wend
