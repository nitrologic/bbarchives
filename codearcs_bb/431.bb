; ID: 431
; Author: Chroma
; Date: 2002-09-19 09:58:02
; Title: Steady Delta Time Routine
; Description: Stabilizes that jumpy delta time.

;Smooth Delta Time Routine
;by Chroma

;Set Graphics
Graphics3D 800,600,16,1
SetBuffer BackBuffer()

;Lights, Camera...Action!
camera = CreateCamera()
light = CreateLight()

;create test cube
cube = CreateCube()
MoveEntity cube,0,0,5


While Not KeyHit(1)
Cls

TurnEntity cube,0,1,0

UpdateWorld
RenderWorld


;Turn Steady Delta On and Off
If KeyHit(28)
	If deltamode=0 Then deltamode=1 Else deltamode=0
EndIf

;-------------------;
;-Steady Delta Time-;
;-------------------;

;-Get Per Frame Delta Time---------------------;
tempdelta#=(MilliSecs() - oldmillisecs#) / 1000
oldmillisecs#=MilliSecs()

;Check Current Delta Against Old Delta
;If Delta Changes by more than 0.004
;Reset the Delta
If tempdelta# > delta# + 0.004
	delta# = tempdelta#
Else
If tempdelta# < delta# - 0.004
	delta# = tempdelta#
EndIf
EndIf
;----------------------------------------------;

;Switch Delta Mode by Hitting Enter
If deltamode
	deltatime#=delta
Else
	deltatime#=tempdelta
EndIf


Text 5,5,"Press <Enter> for Steady Delta."
Text 5,25,"Steady Delta Mode:"+deltamode
;Show Steady Delta
Text 5,45,"Steady Delta Time: "+deltatime#


Flip
Wend
