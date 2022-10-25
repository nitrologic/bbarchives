; ID: 2039
; Author: HrdNutz
; Date: 2007-06-20 18:03:54
; Title: Fixed Step Logic &amp; Linear Interpolation
; Description: Low frequency update loop combined with motion tweening gives some smooth results

Graphics(640, 480, 32)

Global UPDATE_FREQUENCY = 10 ' times per second
Global update_time = 1000 / UPDATE_FREQUENCY
Global t , dt , execution_time = 0

' boucing ball
Global X# , oldX#
Global dirX# = 1

t = MilliSecs()
While Not KeyDown(KEY_ESCAPE)
	dt = MilliSecs() - t
	t = MilliSecs()

	execution_time:+ dt
 
	' fixed interval update loop    		
        While execution_time >= update_time		
		Update()
		execution_time:- update_time
	Wend
	
	' calculate the remainder for motion interpolation
	Local et# = execution_time
	Local ut# = update_time
	Local tween# = et / ut
	
	Render(tween)
Wend

Function Update()
	' time independent speed
	Local Speed# = 150.0 / (1000.0 / Float(update_time)) ' 150.0 pixels per second
	
	' record the old position for tweening
	oldX = X
	
	' move the ball
	X:+ (Speed * dirX)
	
	' reverse directions if ball is out of screen bounds
	If X < 0 Or X > 640 Then dirX = - dirX
End Function

Function Render(tween#)
	Cls
	' interpolate between old and actual positions
	Local tx# = X * tween + OldX * (1.0 - tween)	
		
	' draw bouncing ball with interpolated values
	DrawOval tx - 16 , 50 , 32 , 32
	
	' draw second bouncing ball WITHOUT tweening
	DrawOval X - 16 , 200 , 32 , 32
	
	Flip
End Function
