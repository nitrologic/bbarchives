; ID: 197
; Author: Nigel Brown
; Date: 2002-01-29 02:01:38
; Title: Screen Fade
; Description: Screen fade to any RGB

Const FadeSteps#	= 64
Global	Sprite3D

Initalise()

Repeat

	For i=0 To 100
		Color Rnd(255),Rnd(255),Rnd(255)
		Rect Rnd(600),Rnd(400),Rnd(50),Rnd(40),1
		Flip
	Next

	Color 255,255,255
	Text 220,450, "PRESS A KEY TO FADE"
	Flip
	WaitKey()

	FadeScreen Rnd(255), Rnd(255), Rnd(255)
	
	Flip

Forever

;---------------------------------------------------------------------
Function Initalise()
;---------------------------------------------------------------------
Graphics3D	640,480,16,0

SetBuffer BackBuffer()

;3D once only Stuff
Sprite3D = CreateSprite()
MoveEntity Sprite3D,0,0,1

; Create a camera and tell it NOT to erase the 2D area when it clears.
Camera = CreateCamera()
CameraClsMode camera,False,True

; Set the viewport so the WHOLE screen is in camera view
CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()

End Function

;---------------------------------------------------------------------
Function FadeScreen(red,green,blue)
;---------------------------------------------------------------------


EntityColor Sprite3D,red,green,blue

alpha# = 0

For i=0 To FadeSteps

	alpha# = alpha# + 1.0/FadeSteps

	EntityAlpha Sprite3D,alpha#

	UpdateWorld

	RenderWorld

	Flip
		
Next

EntityAlpha Sprite3D,0

End Function
