; ID: 357
; Author: Rob 
; Date: 2002-06-29 05:54:19
; Title: 60's PONG-3D!
; Description: I wrote this 3d pong game with fun in mind - you have just got to see it!

;60's PONG-3D! By Rob. 100 line pong game entry! Use mouse up and down! This is a true 100 line prog, no cheating.
Graphics3D 640,480,16
font=LoadFont("Arial",32,True)
SetFont font
SetBuffer BackBuffer()
blurtex=CreateTexture(256,256,256)
camera=CreateCamera()
CameraClsMode camera,0,1
sprite=CreateSprite(camera)
MoveEntity sprite,0,0,640
ScaleSprite sprite,640,640
EntityOrder sprite,-1
EntityTexture sprite,blurtex
EntityAlpha sprite,0.75
EntityBlend sprite,3
p1=CreateCube()
ScaleEntity p1,2,2,8
EntityColor p1,255,0,0
p2=CreateCube()
ScaleEntity p2,2,2,8
EntityColor p2,0,255,0
ball=CreateSphere(12)
ScaleEntity ball,3,3,3
EntityColor ball,100,100,255
EntityShininess ball,.2
light=CreateLight(2,ball)
LightRange light,20
MoveEntity light,5,5,5
ground=CreateSphere(32)
ScaleEntity ground,160,0.5,160
MoveEntity ground,0,-5,20
light=CreateLight(2,p1)
LightRange light,20
light=CreateLight(2,p2)
LightRange light,20
Collisions 1,2,2,2
EntityType ball,1
EntityType p1,2
EntityType p2,2
While Not KeyHit(1)
	If count=3
		CameraViewport camera,0,0,256,256
		RenderWorld
		CopyRect 0,0,256,256,0,0,BackBuffer(),TextureBuffer(blurtex)	
		CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()
		count=0
	EndIf
	count=count+1
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	PositionEntity camera,0,100,1
	RotateEntity camera,80,0,0
	If y1#<-48 Then y1#=-48
	If y1#>104 Then y1#=104
	If y2#<-48 Then y2#=-48
	If y2#>104 Then y2#=104
	PositionEntity p1,-86,0,y1#
	PositionEntity p2,86,0,y2#
	MoveEntity ball,bx#,0,bz#
	If EntityCollided(ball,2)
		If Sgn(bx#)=Sgn(EntityX(ball)) Then bx=-1.1*bx
		MoveEntity ball,bx#,0,0
		bz#=( EntityZ(ball)-EntityZ(EntityCollided(ball,2)))*0.1
	EndIf
	If bx#=0 Then bx#=Rnd(-2,2)*4
	If bz#=0 Then bz#=Rnd(-2,2)
	If EntityZ(ball)<-48 Then bz#=-bz#
	If EntityZ(ball)>104 Then bz#=-bz#
	If EntityX(ball)<-90
		p2score=p2score+1
		PositionEntity ball,0,0,0
		bx#=0
		bz#=0
	EndIf
	If EntityX(ball)>90
		p1score=p1score+1
		PositionEntity ball,0,0,0
		bx#=0
		bz#=0
	EndIf
	If aicount>seed And bx>0
		If EntityZ(ball)>EntityZ(p2) Then impulse#=1 Else impulse=-1
		seed=EntityDistance(p2,ball)*(Rnd(0.6))
		aicount=0
		speed#=(bz*Rnd(2.3))+3
	EndIf
	aicount=aicount+1
	If EntityZ(p2)<-48 Or  EntityZ(p2)>104 Then impulse=-impulse
	y2=y2+impulse*speed
	speed=speed*0.95	
	EntityColor ground,Abs(EntityX(ball))*1.2,Abs(EntityZ(ball))*1.2,Abs( EntityX(ball)-EntityZ(ball) )*1.2
	TurnEntity ground,0,1,0
	UpdateWorld
	RenderWorld
	Color 0,0,0
	Text (GraphicsWidth()/2)-EntityX(ball)*0.04,(EntityZ(ball)*0.03)+8,p1score+"  -VS-  "+p2score,1,0
	Color 255-Abs(EntityX(ball)*2.2), 255-Abs(EntityX(ball)*2.2), 255-Abs(EntityX(ball)*2.2)
	Text GraphicsWidth()/2,8,p1score+"  -VS-  "+p2score,1,0
	Flip
	y1#=y1#-MouseYSpeed()
Wend
End
