; ID: 193
; Author: Kostik
; Date: 2002-01-16 08:15:06
; Title: time dependant movements and rotations
; Description: This code allows you to run your game the same speed on various PCs, fps independent




	Graphics 640,480,16

	;Set t and DeltaT global as float, this MUST be done, trust me!
	Global t#,DeltaT#

	;Create an image so that we have something on the screen
	image1=CreateImage(30,30)
	
	;Draw a green circle on the ImageBuffer 
	SetBuffer ImageBuffer(image1)
	Color 0,160,0
	Oval 0,0,30,30,1
	
	
	;Set the Buffer back to the whole screen
	SetBuffer BackBuffer()



	Repeat
	
	
		;This is the "time" trick---------------------------------------------
		If T=0 Then T=MilliSecs()
		DeltaT=(MilliSecs()-T)/1000
		T=MilliSecs()
		
		
		;The Speed of the circle is calculated with DeltaT,
		;it's the passed time since the Last frame
		;Let's say, the circle should move 640 pixel per second
		;besides, the coords variables MUST be flaot!
		x# = x# + (640 * DeltaT)
		y=200
		
		;Put the circle back
		If x>GraphicsWidth() Then x=0
		;Draw our circle
		DrawImage image1,x,y 
		;---------------------------------------------------------------------




		;This code slows down the framerate
		;you have to play around with this to
		;achieve the slow down, this is for an
		;Athlon 1.2 Ghz, GeForce2MX
 		If KeyDown(200) Then brake=brake+1
		If KeyDown(208)	Then brake=brake-1
		If brake<1 Then brake=1
		For i=1 To brake*10000
		Next

		;Framecounter
		Framecounter_counter=Framecounter_counter+1
		If Framecounter_time=0 Then Framecounter_time=MilliSecs()
		If Framecounter_time+1001 <MilliSecs() Then
			Framecounter_framerate=Framecounter_counter
			Framecounter_counter=0
			Framecounter_time=MilliSecs()
		EndIf
		
		;Some text
		Text 400,300,"fps: "+Framecounter_framerate
		Text 400,310,"brake: "+brake
		Text 0,0,"Use Arrow Up/Down to increase/decrease FPS"
		Text 0,20,"DeltaT: "+DeltaT
		Text 500,400,"Time: "+CurrentTime$()
		;--------------------------------------------------------
	Flip
	Cls 
Until KeyHit(1)
End
