; ID: 550
; Author: EdzUp[GD]
; Date: 2003-01-14 08:04:11
; Title: Unreal style portal effect
; Description: This gives you a brief demo of how to do the Unreal portal effect in Blitz

;
;	PortalTest.bb - Copyright ©2002 EdzUp
;	Coded by Ed Upton
;

Graphics3D 640,480,16,2
SetBuffer BackBuffer()

Global camera = CreateCamera()		;standard camera
CameraRange Camera, .1, 9999
MoveEntity camera, 0, 0, -2.5


Global Portal = CreateCube()		;this is the portal mesh (could be a plane etc
Global PortalTexture = CreateTexture( 256, 256 )	;portal texture

Global PortalCamera = CreateCamera()				;this is the camera that is placed where the
CameraViewport PortalCamera, 0, 0, 256, 256			;portal is
PositionEntity PortalCamera, -100, -1000, -100

Global Room = CreateCube()							;this is a test room to put portal camera into
ScaleEntity Room, -14, -14, -14
PositionEntity Room, -100, -1000, -100

Global Tex = LoadTexture( "Images\advert1.png" )	;basic texture could be any image :)
EntityTexture Room, Tex

Global temp=CreateImage( 256, 256 )					;temp image (camera image)
Global Angle# = 0

AmbientLight 255,255,255			


;MAIN LOOP
While Not KeyDown(1)
	RenderPortal( Portal )							;special function to render the portal

	If KeyDown(203)=1 Then Angle# = Angle# -1		;left and right cursor to turn camera
	If KeyDown(205)=1 Then Angle# = Angle# +1
	
	PositionEntity Camera, 0, 0, 0
	RotateEntity Camera, 0, Angle#, 0
	MoveEntity Camera, 0, 0, -2.5

	RotateEntity PortalCamera, 0, Angle#, 180		;rotate portal camera to what player should see
	
	UpdateWorld
	RenderWorld
	Text 0, 0, "Portal demo - Copyright ©2002 EdzUp"
	Text 0,20, "Please include my name in credits if you use this effect in your game"
	Text 0,80, "Use left and right cursor to rotate camera"
	Flip
Wend
End

Function RenderPortal( Entity )
	;Render portal texture
	HideEntity Camera								;hide camera
	ShowEntity PortalCamera							;show portal camera
	UpdateWorld
	RenderWorld
	CopyRect 0, 0, 256, 256, 0, 0, BackBuffer(), TextureBuffer( PortalTexture )
	EntityTexture Entity, PortalTexture, 0, 0		;Retexture entity with new texture
	ShowEntity Camera								;show player camera
	HideEntity PortalCamera							;hide portal camera
	Cls												;clear back buffer
End Function
