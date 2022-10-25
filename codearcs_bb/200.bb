; ID: 200
; Author: JoJo
; Date: 2002-01-22 03:32:40
; Title: Simple Blinking Technique
; Description: This code is if you ever want to have text or images blink on the screen. 

graphics 800,600,16,2
global looptime
SetBuffer BackBuffer()
looptime = Millisecs()
While Not KeyDown(1)

;places text on the screen
If MilliSecs() < (looptime+2000)
   Text 342,480,"Press Space"
Else
   ;takes the text off the screen and resets looptime to start it all over again
   If MilliSecs() > (looptime+2700) Then looptime = MilliSecs()
EndIf

Flip
Wend
