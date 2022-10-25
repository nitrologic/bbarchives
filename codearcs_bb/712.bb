; ID: 712
; Author: Rob
; Date: 2003-06-05 03:48:27
; Title: New Get Entity Box / Entity 2D Size
; Description: find the outer bounds of an entity (enclose it in a 2D rect or 3D box)

; Get 2D entity size! Rob Cummings (rob@redflame.net)

; perfect bounding box detection for entities - ideal for
; huds, cameras, and more! targetting squares... endless uses!

; use cursors and mouse to fly around.

Global mxspd#,myspd#,campitch#,vx#,vz#,temp#
Global camera

Graphics3D 800,600,16,2
SetBuffer BackBuffer()

camera=CreateCamera()
PositionEntity camera,0,500,0
light=CreateLight()
RotateEntity light,45,45,0

; setup a small world to play within
plane=CreatePlane()
EntityColor plane,100,100,200
For i=0 To 10
	temp=CreateSphere(8)
	ScaleEntity temp,Rnd(-100,100),Rnd(-100,100),Rnd(-100,100)
	PositionEntity temp,Rnd(-500,500),Rnd(500),Rnd(-500,500)
	EntityPickMode temp,2
Next	

Color 0,255,0

;small test app
While Not KeyHit(1)
	picked=CameraPick(camera,GraphicsWidth()/2,GraphicsHeight()/2)
	freelook
	UpdateWorld
	RenderWorld
	targetbox(picked)
	Flip
Wend
End


;-------------------------------------------------------

Function targetbox(ent)
	If ent=0 Return 0
	If EntityInView(ent,camera)=0 Return 0
	;CameraProject camera,EntityX(ent,True),EntityY(ent,True),EntityZ(ent,True)
	leftmost#=10000;ProjectedX()
	rightmost#=-10000;ProjectedX()
	topmost#=10000;ProjectedY()
	bottommost#=-10000;ProjectedY()
	For i=1 To CountSurfaces(ent)
		s=GetSurface(ent,1)
		For v=0 To CountVertices(s)-1
			TFormPoint VertexX(s,v),VertexY(s,v),VertexZ(s,v),ent,0
			CameraProject camera,TFormedX(),TFormedY(),TFormedZ()
			If ProjectedX()<leftmost leftmost=ProjectedX()
			If ProjectedX()>rightmost rightmost=ProjectedX()
			If ProjectedY()<topmost topmost=ProjectedY()
			If ProjectedY()>bottommost bottommost=ProjectedY()
		Next
	Next
	Rect leftmost,topmost,rightmost-leftmost,bottommost-topmost,0
End Function

;-------------------------------------------------------

Function freelook()
	mxspd#=MouseXSpeed()*0.4
	myspd#=MouseYSpeed()*0.4
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	campitch=campitch+myspd
	If campitch<-85 Then campitch=-85
	If campitch>85 Then campitch=85
	RotateEntity camera,campitch,EntityYaw(camera)-mxspd,0
	
	If KeyDown(203) Then vx=vx-.1
	If KeyDown(205) Then vx=vx+.1	
	If KeyDown(200) Then vz=vz+.1
	If KeyDown(208) Then vz=vz-.1
	vx=vx/1.05
	vz=vz/1.05
	MoveEntity camera,vx,vy,vz
End Function
