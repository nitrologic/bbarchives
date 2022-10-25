; ID: 1271
; Author: flying willy
; Date: 2005-01-29 13:39:34
; Title: No clipping for FPS weapons!
; Description: This code shows a way to painlessly have guns that don't get stuck inside walls!

;--------------------------------------------------------------------------------
;avoiding camera clipping with weapons (fps example) - see MAKE GUN, below...
;by rob
;--------------------------------------------------------------------------------

;collision vars
Const col_player=1,col_level=2

;--------------------------------------------------------------------------------

;setup display
Graphics3D 800,600,0,2
HidePointer

;--------------------------------------------------------------------------------

;camera
camera = CreateCamera() ;camera
CameraClsColor camera,200,200,255

;--------------------------------------------------------------------------------

;player
player = CreatePivot() ;player
EntityType player,col_player
EntityRadius player,2
EntityParent camera,player ;attach main camera to player
MoveEntity camera,0,6,0 ;move camera to eye height

;--------------------------------------------------------------------------------

;MAKE GUN!

GUNFIX=True ;ENABLE THIS TO SEE THE GUN CLIPPING FIX, false for old method!

If GUNFIX = True

	guncamera = CreateCamera() ;for the gun
	CameraClsMode guncamera,0,1
	PositionEntity guncamera,0,65535+6,0
	
	;same code as below, however, offset and NOT parented!
	gun=CreateCylinder()
	ScaleEntity gun,1,3,1
	RotateEntity gun,90,0,0
	TranslateEntity gun,0,65535+4,4
	EntityColor gun,0,0,255
	
Else

	make usual fps gun that's rubbish!
	gun=CreateCylinder()
	ScaleEntity gun,1,3,1
	RotateEntity gun,90,0,0
	TranslateEntity gun,0,4,4
	EntityParent gun,player
	EntityColor gun,0,0,255

EndIf


;--------------------------------------------------------------------------------
;dummy texure
tex = CreateTexture(32,32,9)
SetBuffer TextureBuffer(tex)
ClsColor 255, 255, 255 : Cls
Color 128, 128, 128
Rect 0, 0, 16, 16
Rect 16, 16, 16, 16
ScaleTexture tex,0.2,0.2
SetBuffer BackBuffer()

;make level
temp=CreatePlane(8)
EntityColor temp,100,200,100
For i=0 To 100
	temp=CreateCube()
	PositionEntity temp,Rnd(-1000,1000),0,Rnd(-1000,1000)
	ScaleEntity temp,1+Rnd(50),10,1+Rnd(50)
	RotateEntity temp,0,Rnd(360),0
	EntityType temp,col_level
	EntityTexture temp,tex
Next

;--------------------------------------------------------------------------------

;activate collisions
Collisions col_player,col_level,2,2

;--------------------------------------------------------------------------------

;mainloop
While Not KeyHit(1)
	
	Cls
	
	;get mouse
	mxspd#=MouseXSpeed()
	myspd#=MouseYSpeed()
	MoveMouse 400,300
	
	;turn camera
	;note, we change only camera pitch as it's attached to the pivot of the player which
	;only changes it's yaw. This for various reasons makes it easy to control.
	
	pitch#=pitch#+myspd*0.1
	yaw#=yaw#-mxspd*0.1
	RotateEntity camera,pitch,0,0
	RotateEntity player,0,yaw,0
	
	;move player
	spd#=0.8
	If KeyDown(200) MoveEntity player,0,0,spd
	If KeyDown(208) MoveEntity player,0,0,-spd
	If KeyDown(205) MoveEntity player,spd,0,0
	If KeyDown(203) MoveEntity player,-spd,0,0
	
	
	UpdateWorld
	RenderWorld
	Flip
Wend
End

;--------------------------------------------------------------------------------
