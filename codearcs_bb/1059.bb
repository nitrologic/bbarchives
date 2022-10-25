; ID: 1059
; Author: Rob
; Date: 2004-05-30 08:41:52
; Title: Simple lensflare
; Description: Add lensflares easily!

;lensflare example for 2D and 3D
;rob@redflame.net

Graphics3D 640,480,16,2

;required
Global viewx=GraphicsWidth(),viewy=GraphicsHeight() 

camera=CreateCamera()
CameraRange camera,1,2000
HidePointer()

;make a sun
sun=CreateSphere()
EntityColor sun,255,255,0
EntityFX sun,1
ScaleEntity sun,100,100,100
PositionEntity sun,800,600,800

;level
p=CreatePlane()
PositionEntity p,0,-10,0

While Not KeyHit(1)

	;mouse
	mxspd#=MouseXSpeed()*0.25
	myspd#=MouseYSpeed()*0.25
	MoveMouse viewx/2,viewy/2

	;rotate view
	pitch#=pitch#+myspd#
	If pitch#<-90 Then pitch#=-90
	If pitch#>90 Then pitch#=90
	yaw#=yaw#-mxspd#
	RotateEntity camera,pitch#,yaw#,0

	
	RenderWorld
	
	;update flare (if using 3D, place before renderworld)
	updateflare(camera,sun)
	
	Flip		
Wend
End

Function updateflare(camera,source)
	CameraProject camera,EntityX(source,1),EntityY(source,1),EntityZ(source,1)
	x#=ProjectedX()/viewx
	y#=ProjectedY()/viewy

	;if on screen	
	If (x>0 And x<=1) And (y>0 And y<=1)
		
		;-1 to 1
		xoffset# = (x-0.5)*2
		yoffset# = (y-0.5)*2

		;notes:		
		
		;Flares: (use sprites and a sprite lib to translate from 3D to 2D.
		;For now, we use 2D to illustrate. Multiply offsets however you like.
		;Oval coords corrected for offset - not needed with 3D sprites.

		
		;flare 1
		flare1_x# = (viewx/2) - (xoffset*640)
		flare1_y# = (viewy/2) - (yoffset*320)
		Oval flare1_x-16,flare1_y-16,32,32,0
		
		;flare 2
		flare2_x# = (viewx/2) - (xoffset*64)
		flare2_y# = (viewy/2) - (yoffset*32)
		Oval flare2_x-100,flare2_y-100,200,200,0
		
		;flare 3
		flare3_x# = (viewx/2) - (xoffset*500)
		flare3_y# = (viewy/2) - (yoffset*250)
		Oval flare3_x-64,flare3_y-64,128,128,0
		
				
	EndIf
End Function
