; ID: 337
; Author: Rob 
; Date: 2002-06-05 18:52:03
; Title: 3d starfield/spacedust/snow/rain sim
; Description: efficient re-use of sprites without any worry and no need for variables...

;Quick Unlimited Starfield/Space debris/rain/snow example
;Rob Cummings

Global daddy
Global numstars = 200
Global fardistance = 1000

Graphics3D 640,480,16,2
camera=CreateCamera()
CameraRange camera,1,fardistance

;heirarchy to hold a starfield system
daddy=CreatePivot()
For i=1 To numstars
	temp=CreateSprite()
	EntityAutoFade temp,fardistance/4,fardistance/2
	PositionEntity temp,Rnd(-fardistance,fardistance),Rnd(-fardistance,fardistance),Rnd(-fardistance,fardistance)
	EntityParent temp,daddy
Next

While Not KeyHit(1)
	
	;rotations
	mxspd#=MouseXSpeed()
	myspd#=MouseYSpeed()
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	
	;movement
	If MouseDown(1)
		vel#=vel#+0.1
	EndIf
	vel=vel/1.05 ; dampen movement
	
	;apply to camera
	TurnEntity camera,myspd#,-mxspd#,0
	MoveEntity camera,0,0,vel#
	

	;calculate starfield/debris
	For i=1 To CountChildren(daddy)
		child=GetChild(daddy,i)
		If EntityDistance(child,camera)>fardistance
			PositionEntity child,EntityX(camera),EntityY(camera),EntityZ(camera)
			RotateEntity child,EntityPitch(camera),EntityYaw(camera),0
			MoveEntity child,Rnd(-500,500),Rnd(-500,500),fardistance/2
		EndIf
	Next
	
	UpdateWorld
	RenderWorld
	Flip

Wend
End
