; ID: 2286
; Author: Jesse
; Date: 2008-07-12 17:21:21
; Title: mouse handling
; Description: mouse control (type)

Const 	BUTTONLOCKED		:Int =  2
Const	BUTTONPRESSED		:Int =  1
Const	BUTTONFREE		:Int =  0

Const	REPEATTIME	:Int =250 'in milliseconds
Global	mouse	:tmouse = New Tmouse
Function MyHook:Object( id:Int,data:Object,context:Object )
	mouse.Load()
End Function

'Add our hook to the system
AddHook FlipHook,MyHook

Type Tbutton
	
	Field	activated		:Int
	Field	inuse		:Int
	Field 	free			:Int
End Type

Type Tmouse


	Field 	x					:Int,..
			y					:Int,..
			Pressed				:Int[3],..
			DoublePressed			:Int[3],..
			mousewheel			:Int	
	
	Field	oldx					:Int,..
			oldy					:Int,..
			oldpressed			:Int[3],..
			oldmousewheel			:Int

	Field	turnDifference			:Int,..
			selected				:Int
	
	Field	state				:Int[3],..
			ClickCount			:Int[3],..
			endtime				:Long[3]

	' loads all of the mouse processes such as 
	' mouse x, y, z, buttons and click, double, double click.
	' mouse pressed, used and released.
	' needs be called at the beginning of each frame.

	Method Load() 
		oldx = x
		oldy = y
		oldmousewheel = mousewheel
		
		x = MouseX()
		y = MouseY() 
		mousewheel = MouseZ() 
		
		turnDifference = mouseWheel - oldMouseWheel		

		For Local b:Int = 0 To 2
			
			oldpressed[b] = pressed[b]
			pressed[b] = MouseDown(b+1)  
			
			If oldpressed[b]
			
				If pressed[b]
					state[b] = BUTTONLOCKED
				Else
					state[b] = BUTTONFREE
				EndIf
			
			ElseIf pressed[b]
			
				state[b] = BUTTONPRESSED
			
			EndIf
		
			DoublePressed[b] = False
		
			If state[b] = BUTTONPRESSED
			
				If ClickCount[b] = 0
			
					ClickCount[b] = 1 'first click
					endtime[b] = MilliSecs() + REPEATTIME
			
				Else'second click
			
					If MilliSecs() < endtime[b] 
						DoublePressed[b] = True 
					EndIf
					ClickCount[b] = 0
			
				EndIf
			ElseIf MilliSecs() > Endtime[b]
			
				ClickCount[b] = 0
			
			EndIf
		Next
	End Method
	' checks if mouse cursor is with in a specified area
	' h = start x
	' v = start y
	' width of area
	' height of area
	' returns true if in area false if not	

	Method InArea:Int(h:Int, v:Int, width:Int, height:Int)	

		If x < h Return False
		If x > (h+Width) Return False
		If y < v Return False
		If y > (v + Height) Return False
		Return True

	End Method
	
	' returns mouse x and y position
	' through input variables a and d.

	Method GetXY(a:Int Var, d:Int Var)

		a=Self.x
		d=Self.y

	End Method
	
	'returns true if a mouse "double click" was completed with in current cycle frame. 	

	Method doubleclick:Int(selected:Int = 1)
		selected :- 1
		selected :& 3
		
		Return DoublePressed[selected]

	End Method
	
	' returns true if a specified mouse button is still pressed based on previos frame.
	
	Method ButtonInUse:Int(selected:Int = 1)
	 	selected :- 1
		selected :& 3

		If state[selected] = BUTTONLOCKED 
			Return True
		EndIf
		Return False

	End Method
	
	' returns true if the pre-specified mouse button was pressed on current frame.
	
	Method ButtonActivated:Int(selected:Int = 1)
		selected :- 1
		selected :& 3 

		If state[selected] = BUTTONPRESSED
			Return True
		EndIf
		Return False

	End Method
	
	'returns true if a pre-specified buttone is currently released
	
	Method ButtonReleased:Int(selected:Int = 1)
		selected :- 1
		selected :& 3
		
		If state[selected] = BUTTONFREE
			Return True
		EndIf
		Return False

	End Method
	
	'return true if the mouse button was moved based on previos frame and current frame.
	
	Method moved:Int()

		If oldx <> x Return True
		If oldy <> y Return True
		Return False

	End Method
	
	' same as above but for x only
	
	Method MovedX:Int()

		If oldx <> x Return True
		Return False

	End Method 
	
	' same as above but for y only
	
	Method movedy:Int()

		If oldy <> y Return True
		Return False

	End Method
	
	' returns mouse speed(difference between current frame x and previous frame x
	
	Method Thrustx:Int()

		Return x - oldx

	End Method
	
	' returns mouse speed(difference between current frame y and previous frame y
	
	Method Thrusty:Int()

		Return y - oldy

	End Method
	
	' returns mouse wheel speed based on current frame and prvious frame position.
	
	Method wheelThrust:Int() 

		Return TurnDifference

	End Method

End Type


rem
Local boxx:Int 	= 100
Local boxy:Int 	= 100
Local boxwidth:Int	= 100
Local boxheight:Int = 100
Local displaytime:Int = 1000 'millisecs 
Local display:Int 	= MilliSecs()
Local display2:Int	= MilliSecs()
Local red%,green%,blue%
Graphics 800,600
SetBlend ALPHABLEND
Repeat
	Cls()
	SetAlpha .3
	DrawRect boxx,boxy,boxwidth,boxheight
	DrawText "double click", 105,130
	DrawText "   in here",105,150
	SetAlpha 1.0

	If mouse.InArea(boxx,boxy,boxwidth,boxheight)
		DrawText "insidebox",MouseX(),MouseY()
		If Mouse.doubleclick() 
			red = Rnd(255)
			green = Rnd(255)
			blue = Rnd(255)
			display = displaytime+MilliSecs()
		EndIf
	EndIf
	If display > MilliSecs()
		SetColor red,green,blue
		DrawText "double clicked",100,80
	EndIf
	SetColor 255,255,255
	DrawText mouse.Thrustx()+" xspeed",100,200
	DrawText mouse.Thrusty()+" yspeed",100,220
	If mouse.Thrustx()
		If mouse.thrusty()
			DrawText "moving Diagonally",100,240
		Else
			DrawText "moving Horizontally",100,240
		EndIf
	Else
		If mouse.Thrusty()
			DrawText "moving Vertically",100,240
		EndIf
	EndIf
	DrawText "checking button "+(mouse.selected+1),100,320
	If mouse.buttoninUse()DrawText "button in use",100,260
	If mouse.buttonReleased() DrawText "button is free",100,260
	If mouse.buttonActivated() display2 = displaytime+MilliSecs()
	If display2> MilliSecs() DrawText "button was activated",100,280
	DrawText "mouse wheel speed = "+mouse.wheelThrust(),100,300
	Flip()
Until KeyDown(key_escape)
endrem
