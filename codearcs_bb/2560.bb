; ID: 2560
; Author: chi
; Date: 2009-08-14 06:56:10
; Title: Correct monitor aspect ratio
; Description: no more stretching on different monitors/resolutions

; Correct monitor aspect ratio on all resolutions
; by chi

Const monratio#	= 16/10.		; Enter your monitors aspect ratio [ (4/3.), (16/10.), (16/9.), ...]
;Global monratio#  = api_GetSystemMetrics(0) / Float(api_GetSystemMetrics(1))  ; Desktopwidth / Desktopheight 

Const w_width	= 800
Const w_height	= 600
Const w_mode	= 1

Graphics3D w_width, w_height, 0, w_mode : WireFrame 1

cube = CreateCube()

cam = CreateCamera()
multiX# = 1.
multiY# = 1.
If w_mode=1
	multiX# = monratio# / (4/3.)
	multiY# = (Float(w_width)/Float(w_height)) / (4/3.)
EndIf
ScaleEntity cam,multiX,multiY,multiX
PositionEntity cam, 0, 0, -5


While Not KeyHit(1)
	mxs# = MouseXSpeed()
    mys# = MouseYSpeed()
	If MouseDown(1)
		RotateEntity cam, EntityPitch(cam) + (mys#/5), EntityYaw(cam) - (mxs#/5), 0
		MoveMouse GraphicsWidth() / 2,GraphicsHeight() / 2
	EndIf
	If KeyDown(200) Then MoveEntity cam, 0, 0, 0.1
    If KeyDown(208) Then MoveEntity cam, 0, 0, -0.1
    If KeyDown(205) Then MoveEntity cam, 0.1, 0, 0
    If KeyDown(203) Then MoveEntity cam, -0.1, 0, 0
	UpdateWorld
	RenderWorld
	Text 10,10,"no stretching on Text(x,y,txt$) anymore ;)"
	Flip
	Delay 1
Wend
End
