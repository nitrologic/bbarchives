; ID: 695
; Author: Dabbede
; Date: 2003-05-17 16:01:39
; Title: Stereoscopic Camera
; Description: For Red\Cyan glasses

Global Screen_Width=800		
Global Screen_Height=600
Global StereoTex_Width=256		;The width of the StereoImage
Global StereoTex_Height=256		;The height of the StereoImage

Type StereoCamera
	Field Cam,Dx,Sx,ViewDx,ViewSx,PlaneDx,PlaneSx
End Type


;--Main----------------------------------------------------------------------------------------------
Graphics3D Screen_Width,Screen_Height
SetBuffer BackBuffer()

camera.StereoCamera=CreateStereoCamera()

box=CreateCube()
	FitMesh box,-1,-1.1,.5,2,2.2,1
	MoveEntity box,2,2,10
	If FileType("Bart.bmp")<>1 Then RuntimeError "Missing file 'Bart.bmp'"
	tex=LoadTexture("Bart.bmp") ;Any texture you want
	EntityTexture box,tex
	
While Not KeyHit(1)

	If MouseDown(1) MoveEntity camera\cam,0,0,.1
	If MouseDown(2) MoveEntity camera\cam,0,0,-.1
	
	x#=MouseXSpeed()
	y#=MouseYSpeed()
	MoveMouse Screen_Width/2,Screen_Height/2
	TurnEntity camera\cam,y,-x,0
	RotateEntity camera\cam,EntityPitch(camera\cam),EntityYaw(camera\cam),0
	
	ShowEntity box ;Remember to show all the objects of your world
	TakeStereoWorld()
	
	HideEntity box ;And now remember to hide all the objects of your world :)
	RenderStereoWorld()
	
	Flip
Wend
End



;--Functions-----------------------------------------------------------------------------------------
Function CreateStereoCamera.StereoCamera()
	a.StereoCamera=New StereoCamera
	
	a\cam=CreateCamera()
		ScaleEntity a\cam,.1,.1,.1
	a\dx=CreateCamera(a\cam)
		MoveEntity a\dx,+.3,0,0
		CameraViewport a\dx,0,0,StereoTex_Width,StereoTex_Height
	a\sx=CreateCamera(a\cam)
		MoveEntity a\sx,-.3,0,0
		CameraViewport a\sx,0,0,StereoTex_Width,StereoTex_Height
		
	a\viewdx=CreateTexture(StereoTex_Width,StereoTex_Height)
	a\viewsx=CreateTexture(StereoTex_Width,StereoTex_Height)
	
	a\planedx=CreateSprite(a\cam)
		SpriteViewMode a\planedx,1
		ScaleSprite a\planedx,1,.75
		MoveEntity a\planedx,0,0,1.001
		EntityOrder a\planedx,-1000
		EntityBlend a\planedx,3
		EntityTexture a\planedx,a\viewdx
		HideEntity a\planedx
	a\planesx=CreateSprite(a\cam)
		SpriteViewMode a\planesx,1
		ScaleSprite a\planesx,1,.75
		MoveEntity a\planesx,0,0,1.001
		EntityOrder a\planesx,-1000
		EntityBlend a\planesx,3
		EntityTexture a\planesx,a\viewsx
		HideEntity a\planesx

	Return a
End Function

Function TakeStereoWorld()	
	For a.StereoCamera=Each StereoCamera
		;--Dx--------------------
		CameraProjMode a\Dx,1
		CameraProjMode a\Sx,0
		CameraProjMode a\cam,0
		AmbientLight 255,0,0
		RenderWorld
		CopyRect 0,0,StereoTex_Width,StereoTex_Height,0,0,BackBuffer(),TextureBuffer(a\viewdx)
		;--Sx--------------------
		CameraProjMode a\Dx,0
		CameraProjMode a\Sx,1
		CameraProjMode a\cam,0
		AmbientLight 0,255,255
		RenderWorld
		CopyRect 0,0,StereoTex_Width,StereoTex_Height,0,0,BackBuffer(),TextureBuffer(a\viewsx)	
	Next
End Function

Function RenderStereoWorld()
	For a.StereoCamera=Each StereoCamera
		ShowEntity a\planedx
		ShowEntity a\planesx
		
		CameraProjMode a\Dx,0
		CameraProjMode a\Sx,0
		CameraProjMode a\cam,1
		
		RenderWorld
		
		HideEntity a\planedx
		HideEntity a\planesx
	Next
End Function
