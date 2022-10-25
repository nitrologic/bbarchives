; ID: 365
; Author: EdzUp[GD]
; Date: 2002-07-14 04:05:56
; Title: First Person Shooter Camera Control
; Description: This source code allows you to move and look around, jump, walk and fly.

;
;	CameraControl.bb - Copyright ©2002 EdzUp
;	Coded by Ed Upton
;

;	For this demonstration you will need a terrain called LandBlue.bmp
;	and a stone.bmp texture, the terrain image could be any heightmap
;	and the stone.bmp could be any texture you want.


Graphics3D 640,480,16,2								;Initialise graphics
SetBuffer BackBuffer()								;Initialise double buffering

Global CamPivot = CreatePivot( )					;create camera pivot
Global Camera = CreateCamera( )						;create camera with CamPivot as parent

AmbientLight 255,255,255							;the the global light level to fullbright

Global Terrain = LoadTerrain( "LandBlur.bmp" )		;Load landblue.bmp terrain (this could be any terrain)
ScaleEntity Terrain, 8, 32, 8

Global Stone = LoadTexture( "stone.bmp" )			;terrain texture, this too can be any texture
EntityTexture Terrain, Stone

MoveEntity CamPivot,256,250,256						;Move camera above terrain

Global Gravity# = 0.16								;Game gravity
Global PlayerGravity# = 0.0							;current player gravity

EntityType CamPivot, 1
EntityRadius CamPivot, 1.5
EntityType Camera, 1
EntityRadius Camera, 1.5
EntityType Terrain, 2

Collisions 1, 2, 2, 3
Collisions 2, 1, 2, 3

Global FlyMode=0									;if 1 then flymode is on
Global WalkSpeed#=.5								;this handled the walking motion
Global Jumped=0										;Jump check

While Not KeyDown(1)						;main loop
	TurnEntity CamPivot, 0, 0 -MouseXSpeed(), 0		;left/right rotation
	TurnEntity Camera, MouseYSpeed(), 0, 0			;up/down rotation
	RotateEntity CamPivot, EntityPitch#( CamPivot ), EntityYaw#( CamPivot ), 0	;z roll correction
	MoveMouse GraphicsWidth()/2, GraphicsHeight()/2	;move mouse pointer to center of screen

	RotateEntity Camera, EntityPitch#( Camera ), EntityYaw#( CamPivot ), 0		;Z roll correction

	If KeyDown( 54 ) =1 							;Walk key (Right Shift)
		WalkSpeed# = .05
	Else
		WalkSpeed# = .5
	EndIf

	If KeyDown( 157 ) =1							;Fly mode (Right CTRL)
		FlyMode=1
	Else
		FlyMode=0
	EndIf

	If KeyDown( 57 ) =1 And Jumped =0				;Jump check
		PlayerGravity# = 2.5
		Jumped =1
	EndIf
	
	;Cursor keys for movement
	If FlyMode=0
		If KeyDown( 200 ) =1 Then MoveEntity CamPivot, 0, 0, WalkSpeed#
		If KeyDown( 208 ) =1 Then MoveEntity CamPivot, 0, 0, 0-WalkSpeed#
		PositionEntity Camera, EntityX#( CamPivot ), EntityY#( CamPivot ), EntityZ#( CamPivot )
	Else
		If KeyDown( 200 ) =1 Then MoveEntity Camera, 0, 0, WalkSpeed#
		If KeyDown( 208 ) =1 Then MoveEntity Camera, 0, 0, 0-WalkSpeed#
		PositionEntity CamPivot, EntityX#( Camera ), EntityY#( Camera ), EntityZ#( Camera )
	EndIf
	If KeyDown( 203 ) =1 Then MoveEntity CamPivot, -1, 0, 0
	If KeyDown( 205 ) =1 Then MoveEntity CamPivot, 1, 0, 0

	; ************* Gravity check *************
	If FlyMode=0
		If EntityCollided( CamPivot, 2)
			;do nothing if player is in contact with terrain
			PlayerGravity# = 0.0
			Jumped =0
		Else
			PlayerGravity# = PlayerGravity# - Gravity#
		EndIf
	EndIf
	; ******* END OF GRAVITY CHECK ************
	TranslateEntity CamPivot, 0, PlayerGravity#, 0	;move camera pivot according to current gravity force
	
	UpdateWorld
	RenderWorld
	
	Flip
Wend
EndGraphics
End
