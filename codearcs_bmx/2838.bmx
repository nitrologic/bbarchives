; ID: 2838
; Author: Warpy
; Date: 2011-04-25 04:18:33
; Title: Fly by wire
; Description: Asteroids-style ships with a bit more control

Const maxspeed#=3
Type ship
	Field x#,y#,vx#,vy#
	Field angle#
	
	Method update()
		angle:+(KeyDown(KEY_RIGHT)-KeyDown(KEY_LEFT))*5
		thrust#=(KeyDown(KEY_UP)-KeyDown(KEY_DOWN))
		v#=Sqr(vx*vx+vy*vy)
		
		If thrust
			ax#=Cos(angle)*thrust*maxspeed-vx
			ay#=Sin(angle)*thrust*maxspeed-vy
		ElseIf v>maxspeed
			ax#=vx*(maxspeed/v-1)
			ay#=vy*(maxspeed/v-1)
		EndIf
		a#=Sqr(ax*ax+ay*ay)
		accelerate ax,ay
		
		If KeyDown(KEY_SPACE)
			vx:*5/v
			vy:*5/v
		EndIf

		x:+vx
		y:+vy
		
		If x<0 x:+600
		If x>600 x:-600
		If y<0 y:+600
		If y>600 y:-600
	End Method
	
	Method accelerate(ax#,ay#)
		'coefficients of quadratic equation
		a#=ax*ax+ay*ay
		b#=2*(ax*vx+ay*vy)
		c#=vx*vx+vy*vy-maxspeed*maxspeed
		f#=Sqr(b*b-4*a*c)
		
		'find two solutions of quadratic equation
		lambda1#=(-b+f)/(2*a)
		lambda2#=(-b-f)/(2*a)
		
		'pick either biggest value of lambda, or 1, whichever is smallest.
		'this is the same as trying to apply the biggest allowed thrust, but limiting it to 1 if the picked value is too much
		lambda#=Min(Max(lambda1,lambda2),1)

		'apply acceleration to velocity
		ax:*lambda
		ay:*lambda
		
		a#=Sqr(ax*ax+ay*ay)

		Const limit#=.01
		If a>limit
			ax:*limit/a
			ay:*limit/a
		EndIf
		vx:+ax
		vy:+ay
		
		SetColor 255,0,0
		DrawLine x,y,x+ax*500*lambda,y+ay*500*lambda
		
	End Method
	
	Method draw()
		SetColor 255,255,255
		ox#=x+Cos(angle-120)*5
		oy#=y+Sin(angle-120)*5
		For an=0 To 360 Step 120
			px#=x+Cos(angle+an)*5
			py#=y+Sin(angle+an)*5
			DrawLine ox,oy,px,py
			ox=px
			oy=py
		Next
		DrawLine x,y,x+Cos(angle)*10,y+Sin(angle)*10
		
		v#=Sqr(vx*vx+vy*vy)
		DrawText "v:  "+v,0,0

		dp1#=(Cos(angle)*vx+Sin(angle)*vy)/v
		DrawText "dp: "+Abs(dp1),0,15
	End Method
End Type

Graphics 600,600,0
s:ship=New ship
s.x=300
s.y=300
While Not (KeyHit(KEY_ESCAPE) Or AppTerminate())
	s.update
	s.draw
	Flip
	Cls
Wend
