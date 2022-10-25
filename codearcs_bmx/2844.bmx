; ID: 2844
; Author: Jesse
; Date: 2011-04-28 17:00:40
; Title: vectors part 7
; Description: ball to arc collision

'*************************************************************
'  Ball to Arc Collision
'
'  author: Jesus Perez
' created: 10/11/2010
' based on tutorial Vectors for Flash by TonyPa
' tutorial located at: http://tonypa.pri.ee/vectors/tut12.html
'*************************************************************


SuperStrict
'Create game Object
Type Tgame
	Field stageW:Float
	Field stageH:Float
	Field maxV:Float
	Field gravity:Float
	Field LastTime:Float
	Field myOb:Tball
	Field vc:Tvector
	Field v3:Tvector
	Field vn:Tvector
	Field v4:Tvector
	Field v5:Tvector
	Field p2:tpoint
	Field p3:Tpoint
	Field totalRadius:Float
	Field arc:Tvector
	
	Method New()
		arc = New Tvector
		myOb = New Tball
		vc = New Tvector
		v3 = New Tvector
		vn = New Tvector
		v4 = New Tvector
		v5 = New Tvector
		p2 = New Tpoint
		p3 = New Tpoint
		stageW = 320
		stageH = 240
		'Create game Object
		'Create Object
		'point p0 is its starting point in the coordinates x/y
		myOb.r = 40
		myOb.p0.x=50
		myOb.p0.y=50
		myOb.p1.x=250
		myOb.p1.y=150
		myOb.arc = New Tvector
		myOb.update(True)
		'balls
		arc.p0.x = 160
		arc.p0.y = 80
		arc.r = 60
		
		arc.ang1=-60
		arc.ang2=80
		arc.arc= New Tvector
		Tarc.findArc(arc)
	arc.update(True)
	End Method

	'Function To draw the points, lines And show text
	Method drawAll(v:Tvector)
		'draw ob path
		DrawLine v.p0.x,v.p0.y,v.p1.x,v.p1.y
		'place ob mc
		Tball.drawCircle(v.p2.x,v.p2.y,v.r)
		v=game.arc
		'draw arc
		Tarc.drawArc(v.p0.x, v.p0.y, v.r, v.ang1, v.ang2)
		'line
		'DrawLine(v.arc.p0.x, v.arc.p0.y,v.arc.p1.x,v.arc.p1.y)
	End Method
	
End Type

Type Tpoint
	Field x:Float
	Field y:Float
End Type

Type Tvector
	Field p0:Tpoint
	Field p1:Tpoint
	Field p2:Tpoint
	Field length:Float
	Field dx:Float
	Field dy:Float
	Field vx:Float
	Field vy:Float
	Field vx1:Float
	Field vy1:Float
	Field vx2:Float
	Field vy2:Float
	Field ang1:Float
	Field ang2:Float
	
	Field rx:Float
	Field ry:Float
	Field lx:Float
	Field ly:Float
	
	Field b:Float
	Field f:Float
	Field r:Float
	Field m:Float
	Field airf:Float
	Field lastTime:Float
	Field timeFrame:Float
	Field arc:Tvector
	
	Method New()
		p0 = New Tpoint
		p1 = New Tpoint
		p2 = New Tpoint
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
		'Right hand normal
		rx = -dy
		ry = dx
		'Left hand normal
		lx = dy
		ly = -dx
	End Method

	'project vector v1 on unit-sized vector dx/dy
	Method projectVector:Tvector(dx:Float, dy:Float)
		'find dot product
		Local dp:Float = vx*dx + vy*dy
		Local proj:Tvector=New Tvector
		'projection components
		proj.vx=dp*dx
		proj.vy=dp*dy
		Return proj
	End Method
	
	'calculate dot product of 2 vectors
	Method dotP:Float(v2:Tvector)
		Return vx*v2.vx + vy*v2.vy
	End Method
	
End Type

Type Tball Extends Tvector

	Field vc:Tvector
	
	Method New()
		If vc=Null vc = New Tvector
		p2 =New Tpoint
	End Method
	Function drawcircle(x:Float,y:Float,r:Float)
		For Local a:Float = 0 Until 360
			Local vx:Float = Cos(a)*r
			Local vy:Float = Sin(a)*r
			Plot x+vx,y+vy
		Next
	End Function

	'find collision between balls
	Method ballvsBall:Tvector(p1:Tpoint, r:Float)
		'vector between center points of balls
		'game.vc={}
		vc.vx = p1.x-p0.x
		vc.vy = p1.y-p0.y
		'projection of vc on movement vector
		Local vp:Tvector=vc.projectVector(dx, dy)
		'vector To center of arc in direction of movement vectors normal
		'game.vn={}
		game.p2.x = p0.x+vp.vx
		game.p2.y = p0.y+vp.vy
		game.vn.p0=game.p2
		game.vn.p1=p1
		game.vn.update(True)
		'sum of radius
		game.totalRadius=Self.r+r
		'check If vn is shorter Then combined radiuses
		Local  diff:Float = game.totalRadius-game.vn.Length
		If (diff>0)
			'collision
			'amount To move back moving ball
			Local moveBack:Float=Sqr(game.totalRadius*game.totalRadius-game.vn.Length*game.vn.Length)
			game.p3.x = game.vn.p0.x-moveBack*dx
			game.p3.y = game.vn.p0.y-moveBack*dy
			game.v3.p0 = p0
			game.v3.p1 = game.p3
			game.v3.update(True)
			'check If p3 is on the movement vector
			If(game.v3.Length<Length And game.v3.dotP(Self)>0)
				Return game.v3
			EndIf
		EndIf
		Return Null
	End Method

End Type

Type Tarc Extends Tvector
	'find End points of the arc from angles
	Function findArc(v:Tvector)
		'vector between End points of the arc
		v.arc.p0.x = v.p0.x+v.r*Cos(v.ang1)
		v.arc.p0.y = v.p0.y+v.r*Sin(v.ang1)
		v.arc.p1.x = v.p0.x+v.r*Cos(v.ang2)
		v.arc.p1.y = v.p0.y+v.r*Sin(v.ang2)
		v.arc.update(True)
	End Function

	Function DrawArc(x:Float, y:Float, radius:Float, startAngle:Float, endAngle:Float)
		Local fx:Float,fy:Float 'first x,y
		Local lx:Float,ly:Float 'last x,y
		Const RATE:Float = Pi/180.0
		If startAngle = endAngle Then Return
		If startAngle > endAngle
			Local ta:Float = endAngle
			endAngle = startAngle
			startAngle = ta
		EndIf
		Local angle:Float = endAngle - StartAngle
		If angle > 360.0 angle = 360.0
		Local Stp:Float = 1/(RATE * radius)
		Local AccumAngle:Float = StartAngle
		While accumAngle < (StartAngle+Angle)
			lx:Float = Cos((accumAngle)) * radius
			ly:Float = Sin((accumAngle)) * radius
			Plot x + lx, y + ly
			AccumAngle :+ stp
		Wend
	End Function 

End Type


'Create game Object
Global game:Tgame = New Tgame

'collision
Function findCollision()
	'start To calculate movement
	Local ob:Tball=game.myOb
	Local ob1:Tvector=game.arc
	ob.update(True)
	ob1.update(True)
	Local collision:Tvector=ob.ballvsBall(ob1.p0, ob1.r)
	If(collision)
		'collision point found
		game.v5 = New Tvector
		game.v5.p0 = ob1.p0
		game.v5.p1 = game.p3
		game.v5.update(True)
		'check If the point is on the Right side of vector between arc points
		'vector between starting point of arc And collision point
		game.v4= New Tvector
		game.v4.p0 = ob1.arc.p0
		game.v4.p1.x = ob1.p0.x+game.v5.dx*ob1.r
		game.v4.p1.y = ob1.p0.y+game.v5.dy*ob1.r
		game.v4.update(True)
		Local arcNormal:Tvector = New Tvector
		arcNormal.vx = ob1.arc.lx
		arcNormal.vy = ob1.arc.ly
		If(game.v4.dotP(arcNormal)>=0)
			ob.p2.y=game.p3.y
			ob.p2.y=game.p3.y
		Else
			'Not on the arc
			collision = Null
		EndIf
	EndIf
	'need To check with other side of arc too
	If(collision= Null)
		If(game.vn.Length<ob1.r)
			'amount To move back moving ball
			Local r:Float=ob1.r-ob.r
			Local moveForward:Float=Sqr(r*r-game.vn.Length*game.vn.Length)
			game.p3.x = game.vn.p0.x+moveForward*ob.dx
			game.p3.y = game.vn.p0.y+moveForward*ob.dy
			game.v3.p0 = ob.p0
			game.v3.p1 = game.p3
			game.v3.update(True)
			'check If p3 is on the movement vector
			If(game.v3.Length<ob.Length And game.v3.dotP( ob)>0)
				'collision point found
				game.v5.p0 = ob1.p0
				game.v5.p1 = game.p3
				game.v5.update(True)
				'check If the point is on the Right side of vector between arc points
				'vector between starting point of arc And collision point
				game.v4.p0 = ob1.arc.p0
				game.v4.p1.x = ob1.p0.x+game.v5.dx*ob1.r
				game.v4.p1.y = ob1.p0.y+game.v5.dy*ob1.r
				game.v4.update(True)
				Local arcNormal:Tvector = New Tvector
				arcNormal.vx = ob1.arc.lx
				arcNormal.vy = ob1.arc.ly
				If(game.v4.dotP(arcNormal)>=0)
					ob.p2.x=game.p3.x
					ob.p2.y=game.p3.y
					collision=game.v3
				EndIf
			EndIf
		EndIf
		'now we need To check If endpoints of arc are being hit
		Local nextCollision:Tvector = ob.ballvsBall(ob1.arc.p0, 0)
		If(nextCollision)
			If(collision=Null Or nextCollision.Length<collision.Length)
				collision=nextCollision
				ob.p2.x = game.p3.x
				ob.p2.y = game.p3.y
			EndIf
		EndIf
		nextCollision=ob.ballvsBall(ob1.arc.p1, 0)
		If(nextCollision)
			If(collision=Null Or nextCollision.Length<collision.Length)
				collision=nextCollision
				ob.p2.x=game.p3.x
				ob.p2.y=game.p3.y
			EndIf
		EndIf
	EndIf
	If(collision=Null)
		'no collision
		ob.p2=ob.p1
	EndIf
	'draw it
	game.drawAll(ob)
End Function



Graphics 640,480
Cls
findCollision()
Flip()
WaitKey()
