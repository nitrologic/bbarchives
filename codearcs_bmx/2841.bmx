; ID: 2841
; Author: Jesse
; Date: 2011-04-28 16:19:38
; Title: vectors part 4
; Description: ball to wall collision

'*************************************************************
'  author: Jesus Perez
' created: 10/11/2010
' based on tutorial Vectors for Flash by TonyPa
' tutorial located at: http://tonypa.pri.ee/vectors/tut08.html
'*************************************************************

SuperStrict
Type Tpoint
	Field x:Float
	Field y:Float
End Type

Type Tvector
	Field p0:Tpoint
	Field p1:Tpoint
	Field vx:Float
	Field vy:Float
	Field vx1:Float
	Field vy1:Float
	Field vx2:Float
	Field vy2:Float
	Field length:Float
	Field dx:Float
	Field dy:Float
	Field rx:Float
	Field ry:Float
	Field lx:Float
	Field ly:Float
	
	Field b:Float
	Field f:Float
	
	Method New()
		p0 = New Tpoint
		p1 = New TPoint
	End Method

	
End Type

Type Tball Extends Tvector
	Field r:Float
	Field m:Float
	
	Function Create:Tball(x:Float,y:Float,x1:Float,y1:Float,r:Float,m:Float,bo:Float,f:Float)
		Local b:Tball = New Tball
		b.p0.x = x
		b.p0.y = y
		b.r = r
		b.p1.x = x1
		b.p1.y = y1
		b.m = m
		b.b = bo
		b.f = f
		updateVector(b,True)
		Return b

	End Function

End Type

Type TflatWall Extends Tvector
	
	Function Create:TflatWall(x1:Float,y1:Float,x2:Float,y2:Float,b:Float,f:Float)
		Local w:TflatWall = New TflatWall

		w.p0.x = x1
		w.p0.y = y1
		w.p1.x = x2
		w.p1.y = y2
		w.b = b
		w.f = f
		updateVector(w,True)

		Return w
	End Function

End Type



Type Tgame
	Field stageW:Float
	Field stageH:Float
	Field maxV:Float
	Field myOb:Tball
	Field flatWall:TFlatWall[4]
	
	Field gravity:Float
	Field airf:Float
	Field lastTime:Float
	Field timeFrame:Float
	Field time:Float

	Function Create:Tgame(w:Float,h:Float,mV:Float,gr:Float)
		Local g:Tgame = New Tgame
		g.stageW = w
		g.stageH = h
		g.maxV = mV ' max velocity
		g.gravity = gr 
		g.airf = 1.0 ' value between 0 and 1. the smaller the more friction
		g.myOb = Tball.Create(150,30,150+5,30-5,20,1,1,1)
		g.flatWall[0] = TflatWall.Create(250, 100,  50, 100, 1, 1)
		g.flatwall[1] = TflatWall.Create(250, 150, 250, 100, 1, 1)
		g.flatwall[2] = TflatWall.Create( 50, 150, 250, 150, 1, 1)
		g.flatwall[3] = TflatWall.Create( 50, 100,  50, 150, 1, 1)
		'calculate all parameters For the wall vectors
		g.lastTime = MilliSecs()
		g.timeFrame = 1
		Return g
	End Function
		
End Type

Graphics 300,180
'Create game Object
Global game:Tgame = Tgame.Create(300, 180, 10,0)

Repeat
	Cls
	runme()
	Flip()
Until KeyDown(key_escape)

'point p0 is its starting point in the coordinates x/y
'Function To draw the points, lines And show text
Function drawAll()
	For Local w:TflatWall = EachIn game.flatwall
		DrawLine w.p0.x,w.p0.y,w.p1.x,w.p1.y
	Next
	drawCircle(game.myOb.p0.x,game.myOb.p0.y,game.myOb.r)
End Function
'Main Function
Function runMe()

	'find time passed from last update
	Local thisTime:Float = MilliSecs()
	game.time = (thisTime-game.lastTime)/20
	If game.time 
		'start To calculate movement
		Local ob:Tball = game.myOb
		updateObject(ob) 
		'add air resistance
		ob.vx :* game.airf
		ob.vy :* game.airf
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
		'check the walls For collisions
		For Local w:TflatWall = EachIn game.flatWall
			Local v:Tvector = findIntersection(ob, w)
			updateVector(v, False)
			Local pen:Float = ob.r-v.Length
			'If we have hit the wall
			If (pen>=0)
			
				'move Object away from the wall
				ob.p1.x :+ v.dx*pen
				ob.p1.y :+ v.dy*pen
				'change movement, bounce off from the normal of v
				Local vbounce:Tvector = New tvector
				vbounce.dx = v.lx
				vbounce.dy = v.ly
				vbounce.lx = v.dx
				vbounce.ly = v.dy
				vbounce.b = 1
				vbounce.f = 1
				Local vb:Tvector = bounce(ob, vbounce)
				ob.vx = vb.vx
				ob.vy = vb.vy
			EndIf
		Next
		'reset Object To other side If gone out of stage
		If (ob.p1.x>game.stageW+ob.r)
			ob.p1.x = -ob.r
		ElseIf (ob.p1.x<-ob.r)
			ob.p1.x = game.stageW+ob.r
		EndIf
		If (ob.p1.y>game.stageH+ob.r)
			ob.p1.y = -ob.r
		Else If (ob.p1.y<-ob.r)
			ob.p1.y = game.stageH+ob.r
		EndIf
		'draw it
		'make End point equal To starting point For Next cycle
		ob.p0 = ob.p1
		'save the movement without time
		ob.vx :/ game.timeFrame
		ob.vy :/ game.timeFrame
		game.lastTime = thisTime
		'save time passed
		game.timeFrame = game.time

	EndIf
	drawAll()
	
End Function
'Function To find all parameters For the vector
Function updateVector(v:Tvector, frompoints:Int)

	'x And y components
	If (frompoints)
		v.vx = v.p1.x-v.p0.x
		v.vy = v.p1.y-v.p0.y
	Else
		v.p1.x = v.p0.x+v.vx
		v.p1.y = v.p0.y+v.vy
	EndIf
	'length of vector
	v.Length = Sqr(v.vx*v.vx+v.vy*v.vy)
	'normalized unti-sized components
	If (v.Length>0)
		v.dx = v.vx/v.Length
		v.dy = v.vy/v.Length
	Else
		v.dx = 0
		v.dy = 0
	EndIf
	'Right hand normal
	v.rx = -v.dy
	v.ry = v.dx
	'Left hand normal
	v.lx = v.dy
	v.ly = -v.dx
End Function

Function updateObject:Int(v:Tball)
	'we use time, Not frames To move so multiply movement vector with time passed
	v.vx :* game.time
	v.vy :* game.time
	'Print game.time+" "+v.vx+" "+v.vy+" "+game.timeFrame
	'add gravity, also based on time
	v.vy = v.vy+game.time*game.gravity
	'find End point coordinates
	v.p1.x = v.p0.x+v.vx
	v.p1.y = v.p0.y+v.vy
	'length of vector
	v.Length = Sqr(v.vx*v.vx+v.vy*v.vy)
	If v.length > 0
	'normalized unti-sized components
		v.dx = v.vx/v.Length
		v.dy = v.vy/v.Length
	Else
		v.dx = 0
		v.dy = 0
	EndIf
	'Right hand normal
	v.rx = -v.vy
	v.ry = v.vx
	'Left hand normal
	v.lx = v.vy
	v.ly = -v.vx
End Function
'find intersection point of 2 vectors
Function findIntersection:Tvector(v1:Tvector, v2:Tvector)
	'vector between center of ball And starting point of wall
	Local v:Tvector
	Local v3:Tvector = New Tvector
	v3.vx = v1.p1.x-v2.p0.x
	v3.vy = v1.p1.y-v2.p0.y
	'check If we have hit starting point
	Local dp:Float = v3.vx*v2.dx+v3.vy*v2.dy
	If (dp<0)
		'hits starting point
		 v = v3
	Else
		Local v4:Tvector = New Tvector
		v4.vx = v1.p1.x-v2.p1.x
		v4.vy = v1.p1.y-v2.p1.y
		'check If we have hit side Or endpoint
		Local dp:Float = v4.vx*v2.dx+v4.vy*v2.dy
		If (dp>0)
			'hits ending point
			v = v4
		Else
			'it hits the wall
			'project this vector on the normal of the wall
			v = projectVector(v3, v2.lx, v2.ly)
		EndIf
	EndIf
	Return v
End Function
'find New vector bouncing from v2
Function bounce:Tvector(v1:Tvector, v2:Tvector)
	'projection of v1 on v2
	Local proj1:Tvector = projectVector(v1, v2.dx, v2.dy)
	'projection of v1 on v2 normal
	Local proj2:Tvector = projectVector(v1, v2.lx, v2.ly)
	Local proj:Tvector = New Tvector
	'reverse projection on v2 normal
	proj2.Length = Sqr(proj2.vx*proj2.vx+proj2.vy*proj2.vy)
	proj2.vx = v2.lx*proj2.Length
	proj2.vy = v2.ly*proj2.Length
	'add the projections
	proj.vx = v1.f*v2.f*proj1.vx+v1.b*v2.b*proj2.vx
	proj.vy = v1.f*v2.f*proj1.vy+v1.b*v2.b*proj2.vy
	Return proj
End Function
'project vector v1 on unit-sized vector dx/dy
Function projectVector:Tvector(v1:Tvector, dx:Float, dy:Float)
	'find dot product
	Local dp:Float = v1.vx*dx+v1.vy*dy
	Local proj:tvector = New Tvector
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
