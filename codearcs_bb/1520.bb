; ID: 1520
; Author: Jesse B Andersen
; Date: 2005-11-07 22:52:56
; Title: DarkMatter Models and B3D
; Description: How to use DarkMatter Models in your B3D Programs

;xmlspy
;Using Animated Dark Matter Models in Blitz3D
;March 20, 2004
;jesse_andersengt@yahoo.com
;http://www.alldevs.com



;graphics mode, select the one you want.
Graphics3D 640,480,32,2 ;windowed in 32 bit color
;Graphics3D 640,480,32,1 ;full screen in 32 bits.
;Graphics3D 640,480,16,2 ;windowed in 16 bit color
;Graphics32 640,480,16,1 ;full screen in 16 bit color
SetBuffer BackBuffer()
AppTitle "Using Animated Dark Matter Models in Blitz3D"



;camera and lights
cam=CreateCamera()
light=CreateLight(cam)
PositionEntity cam,-4,5,-10
LightColor light,192,192,192
AmbientLight 0,0,0
CameraRange cam,0.1,10000.0
CameraFogMode cam,True
CameraFogRange cam,0.0,2000.000000
CameraFogColor cam,96,192,255
CameraClsColor cam,96,192,255
RotateEntity cam,10,-45,0

;it sucks when you don't have all the animations in one file.
;however, you can use this code.
;load objects and animations, using Dark Matter models
;in this case the Ninja one.
obj=LoadAnimMesh("Ninja\H-Ninja-Move.3DS")
sec0=LoadAnimSeq(obj,"Ninja\H-Ninja-Static.3DS")
sec1=LoadAnimSeq(obj,"Ninja\H-Ninja-Attack1.3DS")
sec2=LoadAnimSeq(obj,"Ninja\H-Ninja-Die.3DS")
sec3=LoadAnimSeq(obj,"Ninja\H-Ninja-Fidget.3DS")
sec4=LoadAnimSeq(obj,"Ninja\H-Ninja-Idle.3DS")
sec5=LoadAnimSeq(obj,"Ninja\H-Ninja-Impact.3DS")
sec6=LoadAnimSeq(obj,"Ninja\H-Ninja-Move.3DS")

scale#=3
;main loop
Repeat

	If MouseDown(2) Then
		Scale#=Scale#+Float(MouseXSpeed()/200.351)
		MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
			If Scale#<0 Then Scale#=0
	EndIf
	ScaleEntity obj,Scale#,Scale#,Scale#


;if space bar then do dandom animation
If KeyHit(57)
	sec=Rnd(0,6)
	
		Select sec
			Case 0
				Animate obj,3,1,sec0,1
			Case 1
				Animate obj,3,1,sec1,1
			Case 2
				Animate obj,3,1,sec2,1
			Case 3
				Animate obj,3,1,sec3,1
			Case 4
				Animate obj,3,1,sec4,1
			Case 5
				Animate obj,3,1,sec5,1
			Case 6
				Animate obj,1,1,sec6,1
		End Select
EndIf

;attack sequence
If MouseHit(1)
	Animate obj,3,1,sec1
EndIf

;render, update
RenderWorld
UpdateWorld
Text 0,0,"Press Space to do Random Animation"
Text 0,20,"Press Mouse Left Click to Attack"
Text 0,40,"Press and Hold Mouse Right Click to Scale"
Text 0,60,"Animation Sequence: " +sec
Flip
Cls

Until KeyHit(1)
End
