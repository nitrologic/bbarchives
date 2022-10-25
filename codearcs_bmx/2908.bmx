; ID: 2908
; Author: SystemError51
; Date: 2012-01-10 05:26:47
; Title: Small animator
; Description: A small piece of code I hacked together to animate a starfield

' ----------------------------------------------------
' ORION'S GATE
' A sci-fi MMORTS game.
'
' depot/Tools/CoreAnimation.bmx
' A poor man's attempt at implementing a smooth
' animation framework as seen on OS X, for BlitzMax
' ----------------------------------------------------

Type CoreAnimation
	
	' What should be animated
	Field _ca_imageToAnimate		:TImage
	
	' From where
	Field _ca_startX				:Float
	Field _ca_startY				:Float
	' To where
	Field _ca_endX				:Float
	Field _ca_endY				:Float
	
	' Where are we now?
	Field _ca_currentX			:Float
	Field _ca_currentY			:Float
	
	' Timer, and speed
	Field _ca_timer				:TTimer
	Field _ca_animspeed			:Float
	
	' Directions to animate
	Field _ca_horizontalDirection	:Int
	Field _ca_verticalDirection		:Int
	
	
	' Create a new animation sequence
	Method CA_CreateAnimation(image:TImage , startx:Float , starty:Float , endx:Float , endy:Float , timingStep:Int , animSpeed:Float, xAxis:Int, yAxis:Int) 
		
		_ca_imageToAnimate = image
		_ca_startX = startx
		_ca_startY = starty
		_ca_endX = endx
		_ca_endY = endy
		
		_ca_timer = CreateTimer(timingStep) 
		_ca_animspeed = animSpeed
		
		_ca_currentX = startx
		_ca_currentY = starty
		
		_ca_horizontalDirection = xAxis
		_ca_verticalDirection = yAxis
		
	EndMethod
	
	
	' Draw the animation. This will not clear the screen and assumes to be called in your draw loop
	Method CA_DrawAnimation()
		If _ca_currentX <> _ca_endX Or _ca_currentY <> _ca_endY
			If WaitTimer
				If _ca_horizontalDirection = 1
					_ca_currentX = _ca_currentX - _ca_animspeed
				EndIf
			
				If _ca_horizontalDirection = 2
					_ca_currentX = _ca_currentX + _ca_animspeed
				EndIf
			
				If _ca_verticalDirection = 3
					_ca_currentY = _ca_currentY - _ca_animspeed
				EndIf
			
				If _ca_verticalDirection = 4
					_ca_currentY = _ca_currentY + _ca_animspeed
				EndIf
			EndIf
		EndIf
		
		DrawImage _ca_imageToAnimate , _ca_currentX , _ca_currentY
		
	EndMethod
	
	
	' Adjust parameters
	Method CA_SetStartX(newstartx:Int) 
		_ca_startX = newstartx
	EndMethod
	
	Method CA_SetStartY(newstarty:Int) 
		_ca_startY = newstarty
	EndMethod
	
	Method CA_SetEndX(newendx:Int) 
		_ca_EndX = newendx
	EndMethod
	
	Method CA_SetEndY(newendy:Int) 
		_ca_EndY = newendy
	EndMethod
	
	Method CA_SetHorizontalDirection(dir:Int) 
		_ca_horizontalDirection = dir
	EndMethod
	
	Method CA_SetVerticalDirection(dir:Int) 
		_ca_verticalDirection = dir
	EndMethod
	
EndType
