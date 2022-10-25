; ID: 178
; Author: Kostik
; Date: 2002-01-03 08:22:12
; Title: Frames per second
; Description: shows the framecount every second

;Framecounter--------------------------------------------
Framecounter_counter=Framecounter_counter+1
If Framecounter_time=0 Then Framecounter_time=MilliSecs()
If Framecounter_time+1001 <MilliSecs() Then
	Framecounter_framerate=Framecounter_counter
	Framecounter_counter=0
	Framecounter_time=MilliSecs()
EndIf
Text 10,10,"fps: "+Framecounter_framerate
;--------------------------------------------------------
