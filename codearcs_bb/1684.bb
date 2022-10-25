; ID: 1684
; Author: Jesse B Andersen
; Date: 2006-04-22 17:18:10
; Title: Object Movement Relative to Camera
; Description: It moves an object relative to the camera's yaw.

;xmlspy
;http://www.alldevs.com
;April 22, 2006
Gosub setup

;Main Loop
Repeat
	Gosub GetMKeys
	Gosub MoveCube
	
	TurnEntity cam_pivot, 0, .1, 0
	
	If KeyHit(57) Then
		If relative = 1 Then relative = 0 Else relative = 1
	EndIf
	
	RenderWorld()
	Text 0, 0, "AWSD, Right Mouse, Spacebar"
	Text 0, 20, "Relative: " + relative
	Flip
	Gosub FlushMKeys
Until KeyHit(1)
End

.setup
	Graphics3D 640, 480, 0, 2
	AppTitle "Object Movement Relative to Camera"
	cam_pivot = CreatePivot()
	cam = CreateCamera(cam_pivot)
	MoveEntity cam, 0, 20, -20
	
	cube = CreateCube()
	MoveEntity cube, 0, 0, 0
	
	p = CreatePlane()
	Gosub maketexture
	MoveEntity p, 0, -1, 0
	
	center = CreatePivot()
	PointEntity cam,cube
	
	nc = CreateCube()
	EntityColor nc, 0, 0, 255
	MoveEntity nc, 0, 0, 20
	sc = CreateCube()
	EntityColor sc, 0, 255, 0
	MoveEntity sc, 0, 0, -20
	relative = 1
Return

.maketexture
	Tex=CreateTexture( 256,256 )  : SetBuffer TextureBuffer( Tex ) 
	Color 255, 0, 0 : Rect 0, 0, 256, 256
	Color 0, 255, 0 : Rect 0, 0, 256, 256, 0
	ScaleTexture Tex, 128,128 : EntityTexture p,Tex
	SetBuffer BackBuffer() : Color 255, 255, 255
Return

.GetMKeys
	MOVELEFT = KeyDown(203) Or KeyDown(30)
	MOVERIGHT = KeyDown(205) Or KeyDown(32)
	MOVEUP = KeyDown(200) Or KeyDown(17)
	MOVEDOWN = KeyDown(208) Or KeyDown(31)
Return

.FlushMKeys
	MOVELEFT = 0 : MOVERIGHT = 0
	MOVEUP = 0 : MOVEDOWN = 0
	If EntityDistance(cube,center) > 40 PositionEntity cube, 0, 0, 0
Return 

.MoveCube
	If MOVELEFT a=90
	If MOVERIGHT a=-90
	If MOVEUP a=0
	If MOVEDOWN a=180
	
	If MOVELEFT And MOVEUP Then
		a=45
	ElseIf MOVELEFT And MOVEDOWN
		a=135
	EndIf
	
	If MOVERIGHT And MOVEUP Then
		a=-45
	ElseIf MOVERIGHT And MOVEDOWN
		a=-135
	EndIf
	
	;Here is the trick of the whole movement of object relative to camera
	;it is actually not with camera, but it's pivot
	If relative
		RotateEntity cube, 0, a + EntityYaw(cam_pivot), 0
	Else
		RotateEntity cube, 0, a, 0
	EndIf
	If MOVELEFT Or MOVERIGHT Or MOVEUP Or MOVEDOWN Then
		MoveEntity cube, 0, 0, 1
	EndIf
Return
