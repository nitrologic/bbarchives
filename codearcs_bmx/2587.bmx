; ID: 2587
; Author: Matt McFarland
; Date: 2009-09-23 04:29:32
; Title: breakout
; Description: short game

SuperStrict
Graphics 640, 480
Global ObjectList:TList = CreateList()
Global BrickList:TList = CreateList()
Const COLOR_BLUE:Byte = 0
Const COLOR_GREEN:Byte = 1
Const COLOR_RED:Byte = 2


Function DrawGeom(x:Float, y:Float, sides:Float, length:Float, angle:Float, r:Double = 255, g:Double = 255, b:Double = 255, glow:Byte = True)

	Local aStep# = 360 / sides
	Length:Float = (length / 2) / Sin(aStep:Float / 2) 'Calculate the correct length of a side 			
	For Local a:Byte = 0 To sides - 1
		Local x1:Float = x:Float - (Sin(angle + (aStep * a)) * length)
		Local y1:Float = y:Float - (Cos(angle + (aStep * a)) * length)
		Local x2:Float = x:Float - (Sin(angle + (aStep * (a + 1))) * length)
		Local y2:Float = y:Float - (Cos(angle + (aStep * (a + 1))) * length)
		If glow = True
			SetColor r / 4, g / 4, b / 4
			DrawOval x1 - 5, y1 - 5, 11, 11							' Draw Circle at Vertex
		End If
		SetColor r, g, b
		DrawLine x1, y1, x2, y2									' Draw Connecting Lines
	Next
	
End Function



Type TgameObject
	Field x:Double, y:Double, Size:Float
	Field xSpeed:Float, ySpeed:Float
	
	Method updateself() Abstract
End Type

Function Spawn(Obj:TgameObject, xstart:Double, ystart:Double, size:Float)
	obj.x = xstart
	obj.y = ystart
	obj.Size = size
	ListAddLast ObjectList, obj
End Function

Type tBall Extends TgameObject
	Field rot:Float
	Function Create:tBall(xstart:Double, ystart:Double)
		Local newBall:tBall = New tBall
		Spawn (newball, xstart, ystart, 6)
		newball.ySpeed = 2.6
		newball.xSpeed = 2.6
	End Function
	
	Method updateself()
		SetBlend ALPHABLEND
		DrawGeom(x, y, 6, size, rot, 255, 255, 255, False)

		x:+xspeed
		y:+yspeed
		rot:-xspeed * 6
		If x > 640 Or x < 0 Then xspeed = -(xspeed)
		If y > 480 Or y < 0 Then yspeed = -(yspeed)
		SetBlend LIGHTBLEND
		For Local brick:tBrick = EachIn BrickList
			If brick.dead = False collide(brick:tBrick)
		Next

	End Method
	
	Method collide(Obj:TBrick)
		Local Distance:Float = Sqr((Obj.X - X) * (Obj.X - X) + (Obj.Y - Y) * (Obj.Y - Y))
		Local TouchDistance:Float = size + Obj.size
		If Distance < TouchDistance
			obj.dead = True
			'Detect angle of collision
			Local CollisionAngle:Float = ATan2(Obj.Y - Y, Obj.X - X) + 180
			'Move out of collision
			X :+ (TouchDistance - Distance) * Cos(CollisionAngle)
			Y :+ (TouchDistance - Distance) * Sin(CollisionAngle)
			'Sets new Speed
			Local Nx:Float = Cos(CollisionAngle)
			Local Ny:Float = Sin(CollisionAngle)
			'This section adjusted with code by Christian "Warpy" Perfect
			'p is the projection of V onto the normal
			Local Px:Float, Py:Float
			Local Dotproduct:Float = xspeed * Nx + yspeed * Ny
			Px = Dotproduct*Nx
			Py = Dotproduct*Ny
			'the velocity after hitting the wall is V - 2p, so just subtract 2*p from V
			xSpeed = xSpeed - 2 * Px
			ySpeed = ySpeed - 2 * Py +.01
		End If
	End Method


End Type

Type tPaddle Extends TgameObject
	Field inertia:Float
	Function Create:tPaddle(xstart:Double, ystart:Double)
		Local newPaddle:tPaddle = New tPaddle
		MoveMouse(320, 200)
		
		Spawn (newPaddle, xstart, ystart, 64)
		
	
	End Function

	Method collide(obj:tgameobject)
		Local Distance:Float = Sqr((Obj.X - X) * (Obj.X - X) + (Obj.Y - Y) * (Obj.Y - Y))
		Local TouchDistance:Float = size + Obj.size
		If Distance < TouchDistance 
			'Detect angle of collision
			Local CollisionAngle:Float = ATan2(Obj.Y - Y, Obj.X - X) + 180
			'Move out of collision
			X :+ (TouchDistance - Distance) * Cos(CollisionAngle)
			Y :+ (TouchDistance - Distance) * Sin(CollisionAngle)
			'Sets new Speed
			Local Nx:Float = Cos(CollisionAngle)
			Local Ny:Float = Sin(CollisionAngle)
			'This section adjusted with code by Christian "Warpy" Perfect
			'p is the projection of V onto the normal
			Local Px:Float, Py:Float
			Local Dotproduct:Float = obj.xspeed * Nx + obj.yspeed * Ny
			Px = Dotproduct*Nx
			Py = Dotproduct*Ny
			'the velocity after hitting the wall is V - 2p, so just subtract 2*p from V
			obj.xSpeed = obj.xSpeed - 2 * Px
			obj.ySpeed = obj.ySpeed - 2 * Py +.01
			If obj.ySpeed > 0 obj.ySpeed = 1.6
		End If
	End Method
	
	Method updateself()
		DrawGeom(x, y, 6, size, 90, 100, 255, 255)
		inertia = Abs(x - MouseX())
		If x > MouseX() Then x:-inertia
		If x < MouseX() Then x:+inertia
		For Local o:TgameObject = EachIn ObjectList
			If o.Size < 40 collide(o:TgameObject)
		Next

	End Method
End Type

Type tBrick Extends TgameObject
	Field color:Byte
	Field r:Double, g:Double, b:Double
	Field ang:Float
	Field z:Float
	Field spinspeed:Float
	Field dead:Byte
	Method Die()
		ListRemove(BrickList, Self)
		ListRemove(ObjectList, Self)
	End Method
	Function Create:tBrick(xstart:Double, ystart:Double, color:Byte, spinspeed:Float)
		Local NewBrick:tBrick = New tBrick
		Spawn (newbrick, xstart, ystart, 25)
		newbrick.spinspeed = spinspeed
		Select color
			Case COLOR_BLUE
				NewBrick.r = 100
				NewBrick.g = 100
				NewBrick.b = 255
			Case COLOR_GREEN
				NewBrick.r = 100
				NewBrick.g = 255
				NewBrick.b = 100
			Case COLOR_RED
				NewBrick.r = 255
				NewBrick.g = 100
				NewBrick.b = 100
		End Select
		ListAddLast BrickList, newbrick
		Return newbrick
		
	End Function
	Method updateself()
		If dead = True
			size:+16
			spinspeed:+1
		End If
		If size > 900 die()
		ang:+spinspeed
		DrawGeom(x, y, 6, size, ang, r, g, b)
	End Method
End Type
HideMouse()
For Local n:Int = 1 To 10
	tBrick.Create(n * 58, 100, COLOR_BLUE, -.5)
Next

For Local n:Int = 1 To 10
	tBrick.Create(n * 58, 160, COLOR_GREEN, 6)
Next

For Local n:Int = 1 To 10
	tBrick.Create(n * 58, 220, COLOR_RED, .5)
Next

tPaddle.Create(320, 460)
tBall.Create(320, 330)
SetBlend LIGHTBLEND

Repeat
	Cls
	For Local o:TgameObject = EachIn ObjectList
		o.updateself()
	Next
	Flip
Until KeyHit(KEY_ESCAPE) Or AppTerminate()
