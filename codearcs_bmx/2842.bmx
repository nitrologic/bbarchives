; ID: 2842
; Author: Jesse
; Date: 2011-04-28 16:24:24
; Title: Vectors part 5
; Description: ball 2 ball collision part 1

'*************************************************************
'  Ball to roundWall Collision
'
'  author: Jesus Perez
' created: 10/11/2010
' based on tutorial Vectors for Flash by TonyPa
' tutorial located at: http://tonypa.pri.ee/vectors/tut09.html
'*************************************************************


SuperStrict


'Create game Object
Global game:Tgame = Tgame.Create(300, 180, 10)
'Function To draw the points And balls
Graphics game.stageW,game.stageH

Repeat
	Cls
	runMe()
	Flip()
Until KeyDown(key_escape)


Type Tpoint
	Field x:Float
	Field y:Float
End Type

Type Tvector
	Field p0:Tpoint
	Field p1:Tpoint
	Field vx:Float
	Field vy:Float
	Field dx:Float
	Field dy:Float
	Field length:Float
	Field rx:Float
	Field ry:Float
	Field lx:Float
	Field ly:Float
	
	Field b:Float
	Field f:Float
	
	Method New()
		p0 = New Tpoint
		p1 = New Tpoint
	End Method

End Type

Type TroundWall Extends Tvector
	Field r:Float
	
	Function Create:TroundWall(x:Float,y:Float,r:Float)
		Local w:TroundWall = New TroundWall
		w.p0.x = x
		w.p0.y = y
		w.r = r
		Return w
	End Function 
	
End Type

Type Tball Extends Tvector
	Field r:Float
	Field lastTime:Float
	Field timeFrame:Float
	
	Function Create:Tball(x:Float,y:Float,r:Float,vx:Float,vy:Float)
		Local b:Tball = New Tball
		b.p0.x = x
		b.p0.y = y
		b.r = r
		b.vx = vx
		b.vy = vy
		b.lastTime = MilliSecs()
		Return b
	End Function
End Type

Type Tgame
	Field stageW:Float
	Field stageH:Float
	Field maxV:Float
	Field ball:Tball
	Field wall:TroundWall[4]
	Field gravity:Float
	
	
	Function Create:Tgame(w:Float,h:Float,MaxV:Float)
		Local g:Tgame = New Tgame
		g.stageW = w
		g.stageH = h
		g.maxV = maxV
		'Create Object
		'point p0 is its starting point in the coordinates x/y
		g.ball = Tball.Create(150,30,20,5,5)
		g.ball.lastTime = MilliSecs()
		
		g.wall[0] = TroundWall.Create(100, 100, 30)
		g.wall[1] = TroundWall.Create(200, 100, 40)
		g.wall[2] = TroundWall.Create(250, 50, 20)
		g.wall[3] = TroundWall.Create(50, 100, 15)
		g.gravity = 0
		Return g		
	End Function
	
End Type


Function drawAll()
	Local b:Tball = game.ball
	For Local w:TroundWall = EachIn game.wall
		drawCircle(w.p0.x,w.p0.y,w.r)
	Next
	drawCircle(b.p0.x,b.p0.y,b.r)
End Function
'Main Function
Function runMe()

	'start To calculate movement
	Local ob:Tball = game.ball
	'dont let it go over Max speed
	If (ob.vx>game.maxV)
		ob.vx = game.maxV
	Else If (ob.vx<-game.maxV)
		ob.vx = -game.maxV
	EndIf
	If (ob.vy>game.maxV)
		ob.vy = game.maxV
	Else If (ob.vy<-game.maxV)
		ob.vy = -game.maxV
	EndIf
	'update the vector parameters
	updateObject(ob)
	'check the balls For collisions
	For Local w:TroundWall = EachIn game.wall
	
		Local v:Tvector = ball2ball(ob, w)
		Local pen:Float = ob.r+w.r-v.length
		'If we have hit the ball
		If (pen>=0)
		
			'move Object away from the ball
			ob.p1.x :+ v.dx*pen
			ob.p1.y :+ v.dy*pen
			'change movement, bounce off from the normal of v
			Local vbounce:Tvector = New Tvector
			vbounce.dx =  v.dy
			vbounce.dy = -v.dx
			vbounce.lx = v.dx
			vbounce.ly = v.dy
			Local vb:Tvector = bounce(ob, vbounce)
			ob.vx = vb.vx
			ob.vy = vb.vy
		EndIf
	Next
	'reset Object To other side If gone out of stage
	If (ob.p1.x>game.stageW+ob.r)
		ob.p1.x = -ob.r
	Else If (ob.p1.x<-ob.r)
		ob.p1.x = game.stageW+ob.r
	EndIf
	If (ob.p1.y>game.stageH+ob.r)
		ob.p1.y = -ob.r
	Else If (ob.p1.y<-ob.r)
	
		ob.p1.y = game.stageH+ob.r
	EndIf
	'draw it
	drawAll()
	'make End point equal To starting point For Next cycle
	ob.p0 = ob.p1
	'save the movement without time
	ob.vx :/ ob.timeFrame
	ob.vy :/ ob.timeFrame
End Function

Function updateObject(v:Tball)

	'find time passed from last update
	Local thisTime:Float = MilliSecs()
	Local time:Float = (thisTime-v.lastTime)/100
	'we use time, Not frames To move so multiply movement vector with time passed
	v.vx :* time
	v.vy :* time
	'add gravity, also based on time
	v.vy = v.vy+time*game.gravity
	'find End point coordinates
	v.p1.x = v.p0.x+v.vx
	v.p1.y = v.p0.y+v.vy
	'length of vector
	v.Length = Sqr(v.vx*v.vx+v.vy*v.vy)
	'normalized unti-sized components
	v.dx = v.vx/v.length
	v.dy = v.vy/v.length
	'Right hand normal
	v.rx = -v.vy
	v.ry = v.vx
	'Left hand normal
	v.lx = v.vy
	v.ly = -v.vx
	'save the Current time
	v.lastTime = thisTime
	'save time passed
	v.timeFrame = time
End Function

'find collision of 2 balls
Function ball2ball:Tvector(b1:Tvector, b2:Tvector)

	'vector between centers of balls
	Local v3:Tvector = New Tvector
	v3.vx = b1.p1.x-b2.p0.x
	v3.vy = b1.p1.y-b2.p0.y
	v3.length = Sqr(v3.vx*v3.vx+v3.vy*v3.vy)
	v3.dx = v3.vx/v3.length
	v3.dy = v3.vy/v3.length
	Return v3
End Function

'find New vector bouncing from v2
Function bounce:Tvector(v1:Tvector, v2:Tvector)

	'projection of v1 on v2
	Local proj1:Tvector = projectVector(v1, v2.dx, v2.dy)
	'projection of v1 on v2 normal
	Local proj2:Tvector = projectVector(v1, v2.lx, v2.ly)
	Local proj:Tvector = New Tvector
	'reverse projection on v2 normal
	proj2.length = Sqr(proj2.vx*proj2.vx+proj2.vy*proj2.vy)
	proj2.vx = v2.lx*proj2.length
	proj2.vy = v2.ly*proj2.length
	'add the projections
	proj.vx = proj1.vx+proj2.vx
	proj.vy = proj1.vy+proj2.vy
	Return proj
End Function
'project vector v1 on unit-sized vector dx/dy
Function projectVector:Tvector(v1:Tvector, dx:Float, dy:Float)

	'find dot product
	Local dp:Float = v1.vx*dx+v1.vy*dy
	Local proj:Tvector = New Tvector
	'projection components
	proj.vx = dp*dx
	proj.vy = dp*dy
	Return proj
End Function

Function drawcircle(x:Float,y:Float,r:Float)
	For Local a:Float = 0 Until 360
		Local vx:Float = Cos(a)*r
		Local vy:Float = Sin(a)*r
		Plot x+vx,y+vy
	Next
End Function
