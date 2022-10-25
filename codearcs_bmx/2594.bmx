; ID: 2594
; Author: Warpy
; Date: 2009-10-10 07:40:18
; Title: Aim a projectile at an elevated target
; Description: Shows how to find the angle to fire a projectile so it hits a target which may not be at the same height as you

Graphics 600,600,0

Global bullets:TList=New TList
Type bullet
	Field x#,y#
	Field vx#,vy#
	Field path#[]
	
	Function Create:bullet(x#,y#,vx#,vy#)
		b:bullet=New bullet
		bullets.addlast b
		b.x=x
		b.y=y
		b.vx=vx
		b.vy=vy
		Return b
	End Function
	
	Method update()
		path:+[x,y]
		
		x:+vx
		y:+vy
		vy:+g
		DrawRect x-5,y-5,10,10
		
		For i=0 To Len(path)-1 Step 2
			DrawRect path[i],path[i+1],1,1
		Next
		
		If x>600 Or y>600
			bullets.remove Self
		EndIf
	End Method
End Type

'the setup is that you've got a cannon at (0,300), trying to fire at the mouse cursor.
'gravity is directed down the screen and the velocity of the projectiles is fixed at 20 px per frame.
'there will be some inaccuracy in the projectiles drawn on the screen because I'm using a discrete timestep model, due to I'm lazy.

Const g#=1,v#=20

While Not (KeyHit(KEY_ESCAPE) Or AppTerminate())
	sx#=MouseX()
	sy#=MouseY()-300
	
	a#=g*sx*sx/(2*v*v)	'coefficients of the quadratic equation
	b#=sx
	c#=a-sy
	
	If b*b>4*a*c	'if solution exists
		
		t#=(-b+Sqr(b*b-4*a*c))/(2*a)		'this is tan(phi)
		
		an#=ATan(t)
		DrawLine 0,300,sx,sy+300
		
		If MouseHit(1)
			bullet.Create 0,300,v*Cos(an),v*Sin(an)
		EndIf
		
	Else
		DrawText "no solution!",0,0
	EndIf
		
	For bu:bullet=EachIn bullets
		bu.update
	Next


	Flip
	Cls
Wend
