; ID: 2358
; Author: Jesse
; Date: 2008-11-10 01:58:55
; Title: Coral Snake
; Description: linklist and recursive  snake

SuperStrict
Type TSnake
	
	Field 	x		:Double
	Field 	y		:Double
	Field	dx		:Double
	Field	dy		:Double
	Field 	speed	:Double
	Field	spacing 	:Double
	Field	angle	:Double
	Field 	r		:Int
	Field 	g		:Int
	Field	b		:Int
	Field 	head		:TSnake
	Field 	tail		:TSnake
	Field 	last		:tsnake
'=============================================================================
'
'		a recursive function that links all of the parts together
'		the first time it is called must be called with the "first" 
'		flag set to true.
'
'==============================================================================
	
	Function Create(segment:Tsnake,x:Float,y:Float,parts:Int,first:Int = True)
		If parts = 0 
			segment = Null ' first segemnt must bee created outside with "new TSnake"
						' before calling create function the first time.
			Return
		EndIf
		If first = True
			segment = segment
			segment.x = x
			segment.y = y
			segment.dx = 1.0
			segment.dy = 0.0
			segment.speed = 15.0
			segment.spacing = 5.0
			segment.r = Rand(255)
			segment.g = Rand(255)
			segment.b = Rand(255)
		EndIf
		Local s:Tsnake = New Tsnake 'create the next segment 
		'assign values
		s.x = x 				
		s.y = y 
		s.dx = segment.dx
		s.dy = segment.dy
		s.spacing = segment.spacing
		s.speed = segment.speed
		s.r = Rand(255)
		s.g = Rand(255)
		s.b = Rand(255)
		segment.tail = s ' connect the new segment to the tail of previous segment.
		s.head = segment ' connect the previous segment  to the head of new segment.
		Create(s, s.x, s.y,parts-1, False) ' call it self(recurse) until all segments created.
									' False signifies it is not the first time called.
	End Function
	
	Method move(prev:TSnake) 'calculate new semgnet position.
		Local sx:Double = prev.x - x
		Local sy:Double = prev.y - y
		angle:Double = ATan2(sy,sx)
		Local dist:Double = Sqr(sx*sx+sy*sy)
		If  dist > spacing Then
			
			x :+Cos(angle)*speed
			y :+Sin(angle)*speed
			speed = prev.speed
		
		Else
			speed = prev.speed
		EndIf
	End Method
	
	Method update()
		
		Local n:tsnake = Self
		If last = Null 		' if tail (or last object not found)
			Repeat			' find it
				n = n.tail 	' get next segment(object)
				last = n  	' save it
			Until  n.tail = Null' if found the last segment exit. 
		EndIf
		n = last ' set n to last segment
		Repeat 'traverst through linklist n from last to first
			If n = Self 'if found the first object(head)
				'calculate movement of the head
				n.dx = Cos(n.angle) 
				n.dy = Sin(n.angle)
				'check for collition ugainst the walls
				If (n.x +n.dx*n.speed) < GraphicsWidth() And (n.x +n.dx*n.speed) > 0 
					n.x = n.x + n.dx * n.speed 
				EndIf
				If (n.y +n.dy*n.speed) < GraphicsHeight() And (n.y+n.dy*n.speed) > 0
					n.y = n.y + n.dy * n.speed
				EndIf 
			Else
				n.move(n.head) 'move previous segment 
				
			EndIf
			n = n.head 'jump to the previous object(segment)
		Until n = Null ' exit if found head(first object).  n has a null head pointer
		
	End Method
	
	Method draw()
		Local n:TSnake = last
		Repeat
			SetColor n.r,n.g,n.b
			DrawOval n.x-10,n.y-10,20,20
			n = n.head
		Until n.head = Null
	End Method
	Method control()
		Local dir:Int = KeyDown(key_Right)-KeyDown(key_left)
		Local spd:Int = KeyHit(key_up)-KeyHit(key_down)
		angle = (angle+dir*10)Mod 360
		speed :+ spd
		If speed > 5 Then speed = 5
		If speed < 0 Then speed = 0 
	End Method
			 

End Type


SetGraphicsDriver GLMax2DDriver()
Graphics 800,600
Local snake:tsnake = New tsnake
SetBlend alphablend

TSnake.Create(snake,300,300,1000)
snake.speed = 2
Repeat
	Cls
	snake.control()	
	snake.update()
	snake.draw()
	Flip()
Until KeyDown(key_escape)
