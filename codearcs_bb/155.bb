; ID: 155
; Author: Rob
; Date: 2001-12-04 17:40:48
; Title: Blitz3D speed timing done in 2D (castle demo stuff in 2D)
; Description: Automatic game timing for 2D

;logic framerate, not screen framerate.
Const FPS=60
Global x#,period#,time#,tween#,elapsed#

Graphics 640,480,16,2
SetBuffer BackBuffer()

;setup fps
period=1000/FPS
time=MilliSecs()-period

While Not KeyHit(1)

	Repeat
		elapsed=MilliSecs()-time
	Until elapsed
	tween=Float(elapsed)/Float(period)
	While tween>=1
		time=time+period
		tween=tween-1

		;update logic
		UpdateGame()
		
	Wend	

	;draw display
	DrawGame()

Wend
End

Function UpdateGame()
	x=x+1
	If x>640 Then x=0
End Function

Function DrawGame()
	Cls


	Oval x,32,16,16
	Oval x,64,16,16
	;etc


	Flip 0
End Function
