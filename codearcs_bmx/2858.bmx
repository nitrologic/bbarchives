; ID: 2858
; Author: Jesse
; Date: 2011-06-08 13:32:05
; Title: circle to arc.
; Description: circle to arc interaction

SuperStrict
'Create game Object
Type Tgame
	Field stageW:Float
	Field stageH:Float
	Field maxV:Float
	Field gravity:Float
	Field LastTime:Float
	Field arc:TArc
	Field ball:Tball
	
	Method New()
		stageW = 480
		stageH = 400
		ball = Tball.Create(5,5,10,10,40)
		arc = Tarc.Create(170, 195.5, 60,-60,120)
	End Method
	
	Method update()
		Local length:Float = ball.length
		ball.ball2ArcCollision(arc,length)	
	End Method

	'Function To draw the points, lines And show text
	Method drawAll()
		'draw ball path
		'DrawLine ball.p2.x,ball.p2.y,ball.p1.x,ball.p1.y
		ball.display()	
		arc.display()
	End Method
	
End Type

Type Tpoint
	Field x:Float
	Field y:Float
End Type

Type Tvector
	Field p0:Tpoint
	Field p1:Tpoint
	Field length:Float
	Field dx:Float
	Field dy:Float
	Field lx:Float
	Field ly:Float
	Field vx:Float
	Field vy:Float
	
	Field b:Float
	Field f:Float
	Field m:Float
	Field airf:Float
	Field lastTime:Float
	Field timeFrame:Float
	
	Method New()
		p0 = New Tpoint
		p1 = New Tpoint
	End Method

	'Function To find all parameters For the vector
	Method update( frompoints:Int=False)
		'x And y components
		If(frompoints)
			vx=p1.x-p0.x
			vy=p1.y-p0.y
		Else
			p1.x=p0.x+vx
			p1.y=p0.y+vy
		EndIf
		'length of vector
		Length=Sqr(vx*vx+vy*vy)
		'normalized unit-sized components
		If(Length>0)
			dx=vx/Length
			dy=vy/Length
		Else
			dx=0
			dy=0
		EndIf
	End Method

	'find New vector bouncing from v2
	Method bounce:Tvector(v2:Tvector)
		'projection of v1 on v2
		Local dp:Float = vx*v2.dx+vy*v2.dy
		Local vx1:Float = dp*v2.dx
		Local vy1:Float = dp*v2.dy
		'projection of v1 on v2 normal
		dp = vx*v2.lx+vy*v2.ly
		Local vx2:Float = dp*v2.lx
		Local vy2:Float = dp*v2.ly 
		'reverse projection on v2 normal
		Local p2l:Float = Sqr(vx2*vx2+vy2*vy2)
		'add the projections
		vx=vx1+v2.lx*p2l
		vy=vy1+v2.ly*p2l
	End Method
	
	Method display()
		DrawLine p0.x,p0.y,p1.x,p1.y
	End Method
End Type

Type Tball Extends Tvector
	Field vn:Tvector
	Field v2:Tvector
	Field v3:Tvector
	Field v:Tvector
	Field vbounce:Tvector

	Field p2:Tpoint
	
	Field r:Float
	
	Method New()

		 v = New Tvector
		vn = New Tvector
		v2 = New Tvector
		v3 = New Tvector
		vbounce = New Tvector		

		p2 = New Tpoint

	End Method
	
	Function Create:Tball(x1:Float,y1:Float,x2:Float,y2:Float,r:Float)
		Local b:Tball = New Tball
		b.p0.x = x1
		b.p0.y = y1
		b.p1.x = x2
		b.p1.y = y2
		b.r = r
		b.update(True)
		Return b
	End Function
	
	Method display()
		For Local a:Float = 0 Until 360
			Local vx:Float = Cos(a)*r
			Local vy:Float = Sin(a)*r
			Plot p0.x+vx,p0.y+vy
		Next
	End Method

	'find collision between balls
	Method ballvsBall:Tvector(pa:Tpoint, r:Float,Tlen:Float Var)
		'dp for projection of vector between center of points of balls along movement vector	
		Local dp:Float = (pa.x-p0.x) * dx + (pa.y-p0.y) * dy
		'vector To center of arc in direction of movement vectors normal
		vn.p0.x = p0.x+dp*dx
		vn.p0.y = p0.y+dp*dy
		vn.p1.x=pa.x
		vn.p1.y=pa.y
		vn.update(True)
		'sum of radius
		Local totalRadius:Float=Self.r+r
		'check If vn is shorter Then combined radiuses
		Local  diff:Float = totalRadius-vn.Length
		If (diff>0)
			'collision
			'amount To move back moving ball
			Local moveBack:Float=Sqr(totalRadius*totalRadius-vn.Length*vn.Length)
			v2.p0.x = p0.x
			v2.p0.y = p0.y
			v2.p1.x = vn.p0.x-moveBack*dx
			v2.p1.y = vn.p0.y-moveBack*dy
			v2.update(True)
			'check If on the movement vector
			If(v2.Length<Tlen And (v2.vx*vx+v2.vy*vy)>0)
				Return v2
			EndIf
		EndIf
		Return Null
	End Method

	'collision
	Method ball2ArcCollision(arc:Tarc,Tlen:Float)
		'start To calculate movement
		Local vx4:Float,vy4:Float,vx5:Float,vy5:Float,len5:Float
		Local collision:Tvector=ballvsBall(arc.p0, arc.radius,Tlen)
		If collision
			'collision point found
			vx5 = collision.p1.x - arc.p0.x
			vy5 = collision.p1.y - arc.p0.y
			len5 = Sqr(vx5*vx5+vy5*vy5)
			'check If the point is on the Right side of vector between arc points
			'vector between starting point of arc And collision point
			If Len5 > 0
				vx4 = (arc.p0.x+vx5/len5*arc.radius)-arc.arc.p0.x
				vy4 = (arc.p0.y+vy5/len5*arc.radius)-arc.arc.p0.y
			Else
				vx4 = arc.p0.x - arc.arc.p0.x
				vy4 = arc.p0.y - arc.arc.p0.y
			EndIf
			If(vx4*arc.arc.dy-vy4*arc.arc.dx) >= 0
				p2.x=collision.p1.x
				p2.y=collision.p1.y
				v.vx = p2.x - arc.p0.x
				v.vy = p2.y - arc.p0.y
				v.update(False)
				vbounce.dx =  v.dy
				vbounce.dy = -v.dx
				vbounce.lx =  v.dx
				vbounce.ly =  v.dy
			Else
				'Not on the arc
				collision = Null
			EndIf
		EndIf
		'need To check with other side of arc too
		If collision = Null
			If(vn.Length < arc.radius)
				'amount To move back moving ball
				Local r:Float=arc.radius-r
				Local moveForward:Float=Sqr(r*r-vn.Length*vn.Length)
				v3.p0.x = p0.x
				v3.p0.y = p0.y
				v3.p1.x = vn.p0.x+moveForward*dx
				v3.p1.y = vn.p0.y+moveForward*dy
				v3.update(True)
				'check If on the movement vector
				If(v3.Length<=Tlen And (v3.vx*vx+v3.vy*vy)>0)
					'collision point found
					vx5 = v3.p1.x - arc.p0.x
					vy5 = v3.p1.y - arc.p0.y
					len5 = Sqr(vx5*vx5+vy5*vy5)
					'check If the point is on the Right side of vector between arc points
					'vector between starting point of arc And collision point
					If len5>0
						vx4 = (arc.p0.x+vx5/len5*arc.radius) - arc.arc.p0.x
						vy4 = (arc.p0.y+vy5/len5*arc.radius) - arc.arc.p0.y
					Else
						vx4 = arc.p0.x - arc.arc.p0.x
						vy4 = arc.p0.y - arc.arc.p0.y
					EndIf
					If (vx4*arc.arc.dy-vy4*arc.arc.dx) >= 0
						p2.x=v3.p1.x
						p2.y=v3.p1.y
						collision=v3
						v.vx = p2.x - arc.p0.x
						v.vy = p2.y - arc.p0.y
						v.update(False)
						vbounce.dx = -v.dy
						vbounce.dy =  v.dx
						vbounce.lx = -v.dx
						vbounce.ly = -v.dy
						
					EndIf
				EndIf
			EndIf
			
			'now we need To check If endpoints of arc are being hit
			Local nextCollision:Tvector = ballvsBall(arc.arc.p0, 0,Tlen)
			If(nextCollision)
				If(collision=Null Or nextCollision.Length<collision.Length)
					collision = nextCollision
					p2.x=nextCollision.p1.x
					p2.y=nextCollision.p1.y
					v.vx = p2.x - arc.arc.p0.x
					v.vy = p2.y - arc.arc.p0.y
					v.update(False)
					vbounce.dx =  v.dy
					vbounce.dy = -v.dx
					vbounce.lx =  v.dx
					vbounce.ly =  v.dy
				EndIf
			EndIf

			nextCollision=ballvsBall(arc.arc.p1, 0,Tlen)
			If(nextCollision)
				If(collision=Null Or nextCollision.Length<collision.Length)
					collision=nextCollision
					p2.x=nextCollision.p1.x
					p2.y=nextCollision.p1.y
					v.vx = p2.x - arc.arc.p1.x
					v.vy = p2.y - arc.arc.p1.y
					v.update(False)
					vbounce.dx =  v.dy
					vbounce.dy = -v.dx
					vbounce.lx =  v.dx
					vbounce.ly =  v.dy
				EndIf
			EndIf
		EndIf
		
		If collision
			Local vx1:Float = p2.x-p0.x
			Local vy1:Float = p2.y-p0.y
			Tlen :- Sqr(vx1*vx1+vy1*vy1)
			bounce(vbounce)
			p0.x = p2.x
			p0.y = p2.y
			update(False)
			p1.x = p2.x+dx*Tlen
			p1.y = p2.y+dy*Tlen
			ball2ArcCollision(arc,Tlen)
		Else
			If (p1.x>game.stageW+r)
				p1.x = -r
			Else If (p1.x<-r)
				p1.x = game.stageW+r
			EndIf
			If (p1.y>game.stageH+r)
				p1.y = -r
			Else If (p1.y<-r)
			
				p1.y = game.stageH+r
			EndIf
			p0.x = p1.x
			p0.y = p1.y
			update(False)
		EndIf
	End Method
End Type

Type Tarc Extends Tvector
	Field ang1:Float
	Field ang2:Float
	Field degrees:Float
	Field radius:Float
	Field stp:Float
	Field arc:Tvector
	
	Const RATE:Float = Pi/180.0

	Method New()
		arc = New Tvector
	End Method
	
	Function Create:Tarc(x:Float,y:Float,r:Float,a1:Float,a2:Float)
		Local a:Tarc = New Tarc
		a.p0.x = x
		a.p0.y = y
		a.radius = r
		If a1 > a2
			Local ta:Float = a2
			a2 = a1
			a1 = ta
		EndIf
		a.degrees = a2-a1
		If a.degrees > 360 
			a.degrees = 360
			a2 = a1+360
		EndIf
		a.ang1 = a1
		a.ang2 = a2
		a.stp = 1.0/(RATE*r)
		a.findArc()
		Return a
	End Function
	
	'find End points of the arc from angles
	Method findArc()
		'vector between End points of the arc
		arc.p0.x = p0.x+radius*Cos(ang1)
		arc.p0.y = p0.y+radius*Sin(ang1)
		arc.p1.x = p0.x+radius*Cos(ang2)
		arc.p1.y = p0.y+radius*Sin(ang2)
		arc.update(True)
	End Method

	Method Display()
		Local Angle:Float = ang1
		While Angle < (ang1+degrees)
			Plot p0.x + Cos(Angle) * radius, p0.y + Sin((Angle)) * radius
			Angle :+ stp
		Wend
		DrawOval p0.x-1,p0.y-1,2,2
	End Method

End Type

'Create game Object
Global game:Tgame = New Tgame

Graphics game.stageW,game.StageH
Repeat
Cls
game.update()
game.drawAll()
Flip()
Until KeyDown(key_escape)
