; ID: 140
; Author: Rob
; Date: 2001-11-16 17:14:24
; Title: 2D movement timing via deltatime
; Description: Move things smoothly no matter the framerate

;Robs simple movement timing in 2D
Global deltatime#,oldmillisecs#,thistime#,x#,xspd#

xspd=.5

;setup
Graphics 640,480,16,1
SetBuffer BackBuffer()


oldmillisecs=MilliSecs()
While Not KeyHit(1)
	;get time elapsed since last frame
	
	thistime# = MilliSecs()
	deltatime#=thistime#-oldmillisecs#
	oldmillisecs#=thistime#
	;graphics with timing
	Cls
	
	x=x + deltatime*xspd   ; this is movement by time elapsed.

	If x>640 Then x=0
	Rect x,y,16,16,1
	
	;uncomment for testing. movement will reach a to b in the same time despite how much is on screen
	;For i=0 To 1000
	;	Rect 100,100,100,100
	;Next 
	
	Flip
		
Wend
End
