; ID: 766
; Author: Rob
; Date: 2003-08-18 14:13:52
; Title: SkyQuad
; Description: alternative to skybox

; rob@redflame.net
; Skyquad! like a skybox but using a quad. Easier to generate backdrops for 3d scenes.

Global campitch#,camyaw#,mvx#,mvz# ; for camera freelook
Global camera

Graphics3D 640,480,16,2
camera=CreateCamera()
CameraClsMode camera,0,1
light=CreateLight()

;create the skyquad. much like a skybox but only one side
skyquad = CreateQuad()
EntityFX skyquad,1 : EntityColor skyquad,255,255,255
ScaleEntity skyquad,2,2,2
PositionEntity skyquad,EntityX(camera),EntityY(camera),EntityZ(camera)+4
EntityParent skyquad,camera
EntityOrder skyquad,1

;texture the skyquad. Load yours here.
;we will make a texture to serve as an example
skytexture=CreateTexture(512,512)
SetBuffer TextureBuffer(skytexture)
For i=0 To 10000
	Color Rnd(255),Rnd(255),Rnd(255)
	Plot Rnd(512),Rnd(512)
Next
SetBuffer BackBuffer()

;texture it
EntityTexture skyquad,skytexture

;make some stuff in the world
For i=0 To 99
	temp = CreateSphere()
	ScaleEntity temp,5+Rnd(10),5+Rnd(10),5+Rnd(10)
	PositionEntity temp,Rnd(-500,500),Rnd(500),Rnd(-500,500)
	EntityColor temp,64+Rnd(128),64+Rnd(128),64+Rnd(128)
Next
ground=CreatePlane(4)
EntityColor ground,200,0,0


PositionEntity camera,0,10,0
RotateEntity camera,0,0,0
MoveMouse 0,0


;play the "game". use wsad/cursors and mouse.
While Not KeyHit(1)
	UpdateControls()
	UpdateSkyQuad(camera,skytexture)
	UpdateWorld
	RenderWorld
	Flip
Wend
End

Function UpdateControls()
	mxspd#=MouseXSpeed()*0.25
	myspd#=MouseYSpeed()*0.25
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	campitch=campitch+myspd
	camyaw=camyaw-mxspd
	If campitch<-85 Then campitch=-85
	If campitch>85 Then campitch=85
	RotateEntity camera,campitch,camyaw,0
	If KeyDown(203) Then mvx=mvx-.3
	If KeyDown(205) Then mvx=mvx+.3
	If KeyDown(200) Then mvz=mvz+.3
	If KeyDown(208) Then mvz=mvz-.3
	If KeyDown(30) Then mvx=mvx-.3
	If KeyDown(32) Then mvx=mvx+.3
	If KeyDown(17) Then mvz=mvz+.3
	If KeyDown(31) Then mvz=mvz-.3
	mvx=mvx/1.2
	mvz=mvz/1.2
	MoveEntity camera,mvx,0,mvz
	If EntityY(camera)<10 Then PositionEntity camera,EntityX(camera),10,EntityZ(camera)
End Function

Function UpdateSkyQuad(camera,skytex)
	;experiment with the *0.01 number so it lines up nicely with your texture etc...
	PositionTexture skytex,EntityYaw(camera)*0.01,-EntityPitch(camera)*0.01
End Function

Function CreateQuad()
	m=CreateMesh()
	s=CreateSurface( m )
	AddVertex s,-1,+1,-1,0,0
	AddVertex s,+1,+1,-1,1,0
	AddVertex s,+1,-1,-1,1,1
	AddVertex s,-1,-1,-1,0,1
	AddTriangle s,0,1,2
	AddTriangle s,0,2,3
	;FlipMesh m
	Return m
End Function
