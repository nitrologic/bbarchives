; ID: 570
; Author: Giano
; Date: 2003-02-03 12:09:15
; Title: Track AI function
; Description: Track functions

;TRACK_SMOOTH TRACK_YR TRACK_SMOOTH_YR SAMPLE DEMO
;WRITTEN By Bitmaniak
;Tracking Functions idea and specs By DoctorZ
;Tracking Functions code by Bitmaniak
;*********************************************************************************
;*** Thi is a real shared pivot...
;*********************************************************************************
Global gPivot1,gPivot2,gCube

;*********************************************************************************
;*** Slow PointEntity on xz axe
;*********************************************************************************
Function  track_YR_Smooth(e1,e2,smothness#=1.0)
	PositionEntity gPivot1,EntityX(e1),EntityY(e1),EntityZ(e1)
	PointEntity gPivot1,e2	
	RotateEntity e1, 0,curveAngle#(EntityYaw(gPivot1),EntityYaw(e1),smothness#),0
End Function

;*********************************************************************************
;*** Fast PointEntity on xz axe
;*********************************************************************************
Function  track_YR(e1,e2)
	PositionEntity gPivot1,EntityX(e1),EntityY(e1),EntityZ(e1)
	PointEntity gPivot1,e2	
	RotateEntity e1, 0,EntityYaw(gPivot1),0
End Function

;*********************************************************************************
;*** Slow PointEntity on all axes
;*********************************************************************************
Function  track_Smooth(e1,e2,smothness#)
	PositionEntity gPivot1,EntityX(e1),EntityY(e1),EntityZ(e1)
	PointEntity gPivot1,e2	
	RotateEntity e1, curveAngle#(EntityPitch(gPivot1),EntityPitch(e1),smothness#),curveAngle#(EntityYaw(gPivot1),EntityYaw(e1),smothness#),0
End Function
;*********************************************************************************
;*** Fast PointEntity on all axes...so...pointEntity...just for fun!
;*********************************************************************************
Function  track(e1,e2)
	PointEntity e1,e2
End Function

;*********************************************************************************
;*** This calculate the 2d distance*distance on y axes 
;*** The exact distance is the sqr( EntityDistance_YR_Power2(e1,e2))
;*********************************************************************************
Function EntityDistance_YR_Power2(e1,e2)
	p#=EntityX(e2)-EntityX(e1)
	q#=EntityZ(e2)-EntityZ(e1)
	Return (p*p)+(q*q)
End Function

;*********************************************************************************
;*** Anyone knows this! Calculate the incremental steps
;*********************************************************************************
Function curveangle#( newangle#,oldangle#,increments#)
	If increments>1
		If (oldangle+360)-newangle<newangle-oldangle 
			oldangle=360+oldangle
		End If
		If (newangle+360)-oldangle<oldangle-newangle 
			newangle=360+newangle
		End If
		oldangle=oldangle-(oldangle-newangle)/increments
	End If
	If increments<=1 
		Return newangle
	End If
	Return oldangle

End Function

;*********************************************************************************
;*** Same of above but with points
;*********************************************************************************
;Global gPivot2
Function track_SmoothPoint(entity, x#,y#,z#, turnspeed#=1.0, useRoll%=0)
	PositionEntity gPivot2,x,y,z
	PositionEntity gPivot1,EntityX(entity),EntityY(entity),EntityZ(entity)
	PointEntity gPivot1,gPivot2	
	temp_pitch# = curveAngle#(EntityPitch(gPivot1),EntityPitch(entity),turnspeed#*2)
	temp_yaw# = curveAngle#(EntityYaw(gPivot1),EntityYaw(entity),turnspeed#)
	If useRoll
		temp_roll# = curveAngle#(EntityRoll(gPivot1),EntityRoll(entity),turnspeed#)
		RotateEntity entity, temp_pitch, temp_yaw, temp_roll
	Else
		RotateEntity entity, temp_pitch, temp_yaw, 0
	End If
End Function

;*********************************************************************************
;*** Create a squared texure
;*********************************************************************************
Function defaultTextureEntity(mesh, colr1=32,colg1=128,colb1=192, colr2=255,colg2=255,colb2=160)
	tex=CreateTexture( 64,64 )
	ScaleTexture tex,.125,.125
	SetBuffer TextureBuffer( tex )
	Color colr1,colg1,colb1:Rect 32,0,32,32:Rect 0,32,32,32
	Color colr2,colg2,colb2:Rect 0,0,32,32:Rect 32,32,32,32
	SetBuffer BackBuffer()
	Color 255,255,255

	brush=CreateBrush()
	BrushTexture brush,tex
	EntityTexture mesh,tex
	
	FreeBrush brush
	FreeTexture tex
End Function

;*********************************************************************************
;*** Draws Lines
;*********************************************************************************
Function drawpivot(e,camera,length)
	;Get initial position
	CameraProject camera,EntityX(e,1),EntityY(e,1),EntityZ(e,1)
	x=ProjectedX()
	y=ProjectedY()
	
	;Draw X axis
	Color 255,0,0
	MoveEntity e,length,0,0
	CameraProject camera,EntityX(e,1),EntityY(e,1),EntityZ(e,1)
	Line x,y,ProjectedX(),ProjectedY()
	MoveEntity e,-length,0,0
	
	;Draw Y axis
	Color 0,255,0
	MoveEntity e,0,length,0
	CameraProject camera,EntityX(e,1),EntityY(e,1),EntityZ(e,1)
	Line x,y,ProjectedX(),ProjectedY()
	MoveEntity e,0,-length,0
	
	;Draw Z axis
	Color 0,0,255
	MoveEntity e,0,0,length
	CameraProject camera,EntityX(e,1),EntityY(e,1),EntityZ(e,1)
	Line x,y,ProjectedX(),ProjectedY()
	MoveEntity e,0,0,-length
End Function


;*********************************************************************************
;*** The test!
;*********************************************************************************

Graphics3D 800,600
SetBuffer BackBuffer()

camera=CreateCamera()
light=CreateLight()

PositionEntity camera,0,0,-10

;*** create some entities
cone=CreateCone()
PositionEntity cone,-2,0,-5
RotateMesh cone,90,0,0
defaultTextureEntity(cone)


cone1=CopyEntity(cone)
PositionEntity cone1,-1,0,0
EntityColor cone1,255,0,0

cone2=CopyEntity(cone)
PositionEntity cone2,1,2,0
EntityColor cone2,0,255,0

cone3=CopyEntity(cone)
PositionEntity cone3,0,0,0
EntityColor cone3,0,255,255

cone4=CopyEntity(cone)
PositionEntity cone4,0,0,0
EntityColor cone4,255,255,0

cube=CreateCube()
PositionEntity cube,0,0,8
defaultTextureEntity(cube)


plane = CreatePlane()
defaultTextureEntity(plane)
PositionEntity plane,0,-.5,0
EntityAlpha plane,.5

gPivot1 = CreatePivot()
gPivot2 = CreatePivot()
gCube = CreatePivot()

gfxMiddleWidth = GraphicsWidth()/2
gfxMiddleHeight = GraphicsHeight()/2

;*********************************************************************************

While Not KeyDown( 1 )
;*** Cube rotation
	my = -MouseYSpeed()/5
	mx = MouseXSpeed()/5
	
	MoveMouse gfxMiddleWidth,gfxMiddleHeight
;	PositionEntity gPivot1,EntityX(cone),EntityY(cone),EntityZ(cone)
	PositionEntity gPivot1,0,0,0

	
    EntityParent cube,gCube
    TurnEntity gCube,my,mx,0
    EntityParent cube,0
	
;*** The routines
;*** Before you used to call a pointEntity(e1,e2)...now you'll call a track_Smooth(e1,e2,smooth)

	track_Smooth(cone,cube,20.0)
	track_Smooth(cone1,cube,100.0)	
	track(cone2,cube)	

	d#=EntityDistance_YR_Power2(cone3,cube)
	If d>1
		track_YR_Smooth(cone3,cube,10.0)	
		MoveEntity cone3,0,0,.1
	End If

	track_Smooth(cone4,cube,EntityDistance(cone4,cube)*2)	
	MoveEntity cone4,0,0,EntityDistance(cone4,cube)/200.0
	
	RenderWorld
	
	drawpivot(cone,camera,1)
	drawpivot(cone1,camera,1)
	drawpivot(cone2,camera,1)
	drawpivot(cone3,camera,1)
	drawpivot(cone4,camera,1)

	Text 0,0,d
	Flip
Wend

End
