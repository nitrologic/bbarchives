; ID: 809
; Author: Filax
; Date: 2003-10-14 05:40:50
; Title: Resize an object with mouse
; Description: Resize an object with mouse

Graphics3D 640,480,16,2
SetBuffer BackBuffer()

TmpCamera=CreateCamera()
PositionEntity  TmpCamera,0,0,-30

Cube=CreateCube()
PositionEntity Cube,0,0,0

; -------------------------
; Boucle de gestion
; -------------------------
While Not KeyDown( 1 )
	Scale#=Scale#+Float(MouseXSpeed()/200.351)
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	If Scale#<0 Then Scale#=0
	
	ScaleEntity Cube,Scale#,Scale#,Scale#
	
	UpdateWorld
	RenderWorld

	Flip
Wend
