; ID: 1523
; Author: Warpy
; Date: 2005-11-08 16:51:31
; Title: Move at a constant speed along a bezier
; Description: Make an object move at a roughly constant speed along a bezier curve

Graphics 800,800,0

Local ax=50,ay=50,bx=400,by=50,cx=350,cy=750,dx=750,dy=750
t1#=0
t2#=0
speed#=0 'Speed of clever speed changing version
x1#=ax
y1#=ay
x2#=ax
y2#=ay
While Not KeyHit(KEY_ESCAPE)
	SetColor 100,100,100
	bezier(ax,ay,bx,by,cx,cy,dx,dy) 'draw the curve
	
	ox1#=x1
	oy1#=y1
	ox2#=x2
	oy2#=y2
	
	'CLEVER SPEED CHANGING VERSION
	t1:+speed
	If t1>1 Then t1=0
	a#=t1
	b#=1-t1
	x1#=ax*b*b*b + 3*bx*b*b*a + 3*cx*b*a*a + dx*a*a*a
	y1#=ay*b*b*b + 3*by*b*b*a + 3*cy*b*a*a + dy*a*a*a
	SetColor 255,255,0
	DrawRect x1-3,y1-3,7,7

	'Calculate 'speed' of curve at this point. 
	vx#=-3*ax*b*b + 3*bx*b*(b-2*a) + 3*cx*a*(2*b-a) + dx*3*a*a
	vy#=-3*ay*b*b + 3*by*b*(b-2*a) + 3*cy*a*(2*b-a) + dy*3*a*a
	d#=Sqr(vx*vx+vy*vy)

	'Watch out, magic ahead!

	speed#=1/d 'Make the ball moves less along the curve the 'faster' the curve is at this point, so the ball's *actual* cartesian velocity should stay roughly constant

	'You missed the magic! Go back!

	'Check what the actual speed is
	movedist1#=Sqr((ox1-x1)^2+(oy1-y1)^2)
	DrawText movedist1,0,0

	'Boring old version, t increments at a constant rate
	t2:+.001
	If t2>1 Then t2=0
	a#=t2
	b#=1-t2
	x2#=ax*b*b*b + 3*bx*b*b*a + 3*cx*b*a*a + dx*a*a*a
	y2#=ay*b*b*b + 3*by*b*b*a + 3*cy*b*a*a + dy*a*a*a
	SetColor 0,0,255
	DrawRect x2-3,y2-3,7,7

	'Check what the actual speed is
	movedist2#=Sqr((ox2-x2)^2+(oy2-y2)^2)
	DrawText movedist2,0,15

	Flip
	Cls
Wend

'DON'T BE DECEIVED! This is just a plain old bezier-drawing function, to show the curve the balls are moving along
Function bezier(ax#,ay#,bx#,by#,cx#,cy#,dx#,dy#)
	DrawLine ax,ay,bx,by
	DrawLine cx,cy,dx,dy
	ox#=ax
	oy#=ay
	For t#=0 To 1 Step .01
		a#=t
		b#=1-t
		x#=ax*b*b*b + 3*bx*b*b*a + 3*cx*b*a*a + dx*a*a*a
		y#=ay*b*b*b + 3*by*b*b*a + 3*cy*b*a*a + dy*a*a*a
		DrawRect x-1,y-1,3,3
		DrawLine ox,oy,x,y
		ox=x
		oy=y
	Next
End Function
