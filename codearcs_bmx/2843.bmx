; ID: 2843
; Author: Jesse
; Date: 2011-04-28 16:32:10
; Title: vectors part 6
; Description: ball to ball part 2

'*************************************************************
'  Ball to Ball Collision and response
'
'  author: Jesus Perez
' created: 10/11/2010
' based on tutorial Vectors for Flash by TonyPa
' tutorial located at: http://tonypa.pri.ee/vectors/tut11.html
'*************************************************************

SuperStrict
'Create game Object
Type Tgame
	Field stageW:Double
	Field stageH:Double
	Field maxV:Double
	Field gravity:Double
	Field LastTime:Double
	Field myOb:Tvector
	Field ball:Tball[4]
	
	Method New()
	End Method

	Function Create:Tgame()
		Local g:Tgame = New Tgame
		g.stageW = 320
		g.stageH = 240
		
		'Create balls
		For Local b:Int = 0 Until 4
			Local ballOb:Tball = New Tball
			ballOb.r = 5.0+Rnd(3)*7.0
			ballOb.m = 1
			Local x:Double = b*45.0
			Local y:Double = 40.0
			ballOb.p0.x = x
			ballOb.p0.y = y
			x:+b
			y:+ Rnd(3)-1.0
			ballOb.p1.x = x
			ballOb.p1.y = y
			ballOb.update(True)
			g.ball[b] = ballOb
		Next
		Return g
	End Function
	
	'Function To hold balls inside stage
	Method holdVector(v:Tball)
		'reset Object To other side If gone out of stage
		If(v.p1.x>stageW-v.r)
			v.p1.x=stageW-v.r
			v.vx=-Abs(v.vx)
		ElseIf(v.p1.x<v.r)
			v.p1.x=v.r
			v.vx=Abs(v.vx)
		EndIf
		If(v.p1.y>stageH-v.r)
			v.p1.y=stageH-v.r
			v.vy=-Abs(v.vy)
		ElseIf(v.p1.y<v.r)
			v.p1.y=v.r
			v.vy=Abs(v.vy)
		EndIf
	End Method
	
	Method update()
		For Local ob:Tball = EachIn  ball
			ob.update()
			game.holdVector(ob)
			For Local ob2:Tball = EachIn ball
				If(ob=ob2) Continue
				ob.interact(ob2)			
				'vector between center points of ball
			Next
		Next
	End Method

	Method display()
		For Local ob:Tball = EachIn ball
			ob.display()
			ob.p0 = ob.p1
			ob.p0.x = ob.p1.x
			ob.p0.y = ob.p1.y
		Next
	End Method
End Type

Type Tpoint
	Field x:Double
	Field y:Double
End Type

Type Tvector
	Field p0:Tpoint
	Field p1:Tpoint
	Field length:Double
	Field dx:Double
	Field dy:Double
	Field vx:Double
	Field vy:Double
	
	Field rx:Double
	Field ry:Double
	Field lx:Double
	Field ly:Double
	Field m:Double
	
	Field f:Double
	Field b:Double

	Field proj11:Tvector
	Field proj21:Tvector
	Field proj12:Tvector
	Field proj22:Tvector
		
	Method New()
		p0 = New Tpoint
		p1 = New Tpoint
	End Method
	

	'Function To find all parameters For the vector
	Method update(frompoints:Int = False)
		'x And y components
		If frompoints = True
			vx=p1.x-p0.x
			vy=p1.y-p0.y
		Else
			p1.x=p0.x+vx
			p1.y=p0.y+vy
		EndIf
		'reset Object To other side If gone out of stage
		makeVector()
	End Method

	Method makeVector()
		'length of vector
		Length=Sqr(vx*vx+vy*vy)
		'normalized unti-sized components
		If Length>0
			dx=vx/Length
			dy=vy/Length
		Else
			dx=0
			dy=0
		EndIf
		'Right hand normal
		rx = -dy
		ry = dx
		'Left hand normal
		lx = dy
		ly = -dx
	End Method

	'calculate dot product of 2 vectors
	Method dotP:Double(v2:Tvector)
		Return vx*v2.vx + vy*v2.vy
	End Method
	
	'project vector v1 on unit-sized vector dx/dy
	Method project:Tvector(dx:Double, dy:Double)
		'find dot product
		Local dp:Double = vx*dx + vy*dy
		Local proj:Tvector = New Tvector
		'projection components
		proj.vx=dp*dx
		proj.vy=dp*dy
		Return proj
	End Method
	
	Function drawcircle(x:Double,y:Double,r:Double)
		For Local a:Double = 0 Until 360
			Local vx:Double = Cos(a)*r
			Local vy:Double = Sin(a)*r
			Plot x+vx,y+vy
		Next
	End Function
	
	'find intersection point of 2 vectors
	Method findIntersection:Tvector(v2:Tvector)
		Local v:tvector,v4:Tvector
		'vector between center of ball And starting point of wall
		Local v3:Tvector = New Tvector
		v3.vx = p1.x-v2.p0.x
		v3.vy = p1.y-v2.p0.y
		'check If we have hit starting point
		Local dp:Double = v3.vx*v2.dx+v3.vy*v2.dy
		If (dp<0)
			'hits starting point
			v = v3
		Else
			v4 = New Tvector
			v4.vx = p1.x-v2.p1.x
			v4.vy = p1.y-v2.p1.y
			'check If we have hit side Or endpoint
			dp = v4.vx*v2.dx+v4.vy*v2.dy
			If (dp>0)
				'hits ending point
				v = v4
			Else
				'it hits the wall
				'project this vector on the normal of the wall
				v = v3.project(v2.lx, v2.ly)
			End If
		EndIf
		Return v
	End Method

	'find New movement vector bouncing from v
	Method bounceb2b:Tvector(v2:Tvector, v:Tvector = Null)
		'projection of v1 on v
		proj11=project(v.dx, v.dy)
		
		'projection of v1 on v normal
		proj12=project(v.lx, v.ly)
		
		'projection of v2 on v
		proj21=v2.project(v.dx, v.dy)
		
		'projection of v2 on v normal
		proj22=v2.project(v.lx, v.ly)
	
		Local P:Double=m*proj11.vx+v2.m*proj21.vx
		Local Vn:Double=proj11.vx-proj21.vx
		Local v2fx:Double=(P+Vn*m)/(m+v2.m)
		Local v1fx:Double=v2fx-Vn
	
		P=m*proj11.vy+v2.m*proj21.vy
		Vn=proj11.vy-proj21.vy
		Local v2fy:Double=(P+Vn*m)/(m+v2.m)
		Local v1fy:Double=v2fy-Vn
	
		Local proj:Tvector = New Tvector
		'add the projections For v1
		vx = proj12.vx+v1fx
		vy = proj12.vy+v1fy
		'add the projections For v2
		v2.vx = proj22.vx+v2fx
		v2.vy = proj22.vy+v2fy
		
	'	Return proj
	End Method

	'find New vector bouncing from v2
	Method bounceb2w:Tvector(v2:Tvector,n:Tvector = Null)
		'projection of v1 on v2
		Local proj1:Tvector = project(v2.dx, v2.dy)
		'projection of v1 on v2 normal
		Local proj2:Tvector = project(v2.lx, v2.ly)
		Local proj:Tvector = New Tvector
		'reverse projection on v2 normal
		proj2.Length = Sqr(proj2.vx*proj2.vx+proj2.vy*proj2.vy)
		proj2.vx = v2.lx*proj2.Length
		proj2.vy = v2.ly*proj2.Length
		'add the projections
		proj.vx = f*v2.f*proj1.vx+b*v2.b*proj2.vx
		proj.vy = f*v2.f*proj1.vy+b*v2.b*proj2.vy
		Return proj
	End Method
	
	


End Type

Type Tball Extends Tvector
	Field vx1:Double
	Field vy1:Double
	Field vx2:Double
	Field vy2:Double
	
	Field r:Double
	Field airf:Double
	Field lastTime:Double
	Field timeFrame:Double
	Field vc:Tvector
	Field v3:Tvector
	Field vn:Tvector
	Field v4:Tvector
	Field p3:Tpoint
	Field totalRadius:Double
	
	Method New()
		vc = New Tvector
		v3 = New Tvector
		vn = New Tvector
		v4 = New Tvector
		p3 = New Tpoint
	End Method
	
	Method interAct(ob2:tball)
		vc.p0=p0
		vc.p1=ob2.p0
		vc.update(True)
		
		'sum of radius
		Local totalRadius:Double = r+ob2.r
		
		Local pen:Double=totalRadius-vc.Length
		'check If balls collide at start
		If pen >= 0
			'move Object away from the ball
			p1.x :- vc.dx*pen
			p1.y :- vc.dy*pen
			
			'change movement, bounce off from the normal of v
			bounceb2b(ob2, vc)
		Else
			'reduce movement vector from ball2 from movement vector of ball1
			v3.p0=p0
			v3.vx=vx - ob2.vx
			v3.vy=vy - ob2.vy
			v3.update()

			'use v3 as New movement vector For collision calculation
			'projection of vc on v3
			Local vp:Tvector = vc.project(v3.dx, v3.dy)
			'vector To center of ball2 in direction of movement vectors normal
			Local p2:Tpoint = New Tpoint
			p2.x = p0.x+vp.vx
			p2.y = p0.y+vp.vy
			vn.p0=p2
			vn.p1=ob2.p0
			vn.update(True)
			'check If vn is shorter Then combined radiuses
			Local diff:Double=totalRadius-vn.Length
			Local collision:Int=False
			If(diff>0)
				'collision
				'amount To move back moving ball
				Local moveBack:Double=Sqr(totalRadius*totalRadius-vn.Length*vn.Length)
				p3.x = vn.p0.x-moveBack*v3.dx
				p3.y = vn.p0.y-moveBack*v3.dy
				'vector from ball1 starting point To its coordinates when collision happens
				v4.p0 = p0
				v4.p1 = p3
				v4.update(True)
				
				'check If p3 is on the movement vector
				If(v4.Length<=v3.Length And v4.dotP(Self)>0)
					'collision
					Local t:Double=v4.Length/v3.Length
					collision=True
					p1.x = p0.x+t*vx
					p1.y = p0.y+t*vy
					ob2.p1.x = ob2.p0.x+t*ob2.vx
					ob2.p1.y = ob2.p0.y+t*ob2.vy
					'vector between centers of ball in the moment of collision
					Local vc:Tvector= New tvector
					vc.p0 = p1
					vc.p1 = ob2.p1
					vc.update(True)
					bounceb2b(ob2, vc)
					ob2.makeVector()
					makeVector()
				EndIf
			EndIf
		EndIf

	End Method
	
	
	Method display()
		DrawCircle p0.x,p0.y,r
	End Method

End Type


'Create game Object
Global game:Tgame = Tgame.Create()
Graphics game.stageW,game.stageH

Repeat
	Cls
	game.update()
	game.display()
	Flip()
Until KeyDown(key_escape)
