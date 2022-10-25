; ID: 313
; Author: Giano
; Date: 2002-05-02 05:13:00
; Title: Lazer rays functions
; Description: Create laser rays stretching sprites (billboard)

;*****************************************************************
;*** Lazer rays functions
;*** 2002 by Gianluca Sclano aka BitmaniaK
;*** sclano@hotmail.com
;*****************************************************************
;***	
;***	check the functions...	
;***--------------------------------------------------------------	
;*** Point a sprite To an entity, without stretching it:
;***	
;***>	SpritePointedToEntity(sprite, entity , camera)
;***--------------------------------------------------------------		
;*** Point a sprite to an entity and stretch of the right size:
;***	
;***>	SpriteStretchedToEntity(sprite, entity ,camera, fat#=1.0)
;***	
;*** (where fat is the scale of width)
;***--------------------------------------------------------------
;***	
;*** Turn a free type sprite facing the camera, works on y axys:
;***	
;***>	SpriteFacedToCamera(entity,camera)
;***	
;*****************************************************************
;***	Remember to change the sprite filename!
;*****************************************************************

Graphics3D 640,480
;** Create the sprites
s1 = LoadSprite("particle.bmp",1) ;<--- Insert your sprite here, better particle sprite aspect
s2 = CopyEntity(s1)
SpriteViewMode s1,2 ;*** free mode 
PositionEntity s1,-50,0,80

ScaleSprite s1,10,15
SpriteViewMode s2,2 ;*** free mode
PositionEntity s2,50,0,80

cyl=CreateCylinder()
ScaleEntity cyl,1,50,1
PositionEntity cyl,-50,0,80


SetBuffer BackBuffer()

camera=CreateCamera()
light=CreateLight()

cone=CreateCube()
PositionEntity cone,10,-5,5
PositionEntity camera,0,0,-50

;*** Main loop
While Not KeyDown( 1 )
	RenderWorld

	Gosub MOUSECTRL	;*** controls the mouse movements
	
	SpriteFacedToCamera( s1,camera)
	SpriteFacedToCamera(cone,camera)
	SpriteStretchedToEntity( s2,cone,camera)
	SpritePointedToEntity( cyl,cone,camera)
	Flip
Wend

End




;*** Point a sprite To an entity, without stretching it
Function SpritePointedToEntity(s,e,camera)
	PointEntity s,e
	TurnEntity s,90,0,0
	SpriteFacedToCamera(s,camera)
End Function

;*** Point a sprite to an entity and stretch of the right size
Function SpriteStretchedToEntity(s,e,camera, fat#=1.0)
	SpritePointedToEntity(s,e,camera)
	HandleSprite s,0,-1
	ScaleSprite s,fat,EntityDistance(s,e)/2.0
End Function

;*** Turn a free type sprite facing the camera, works on y axys
Function SpriteFacedToCamera(entity,camera)
	piv = CreatePivot(entity)
	PointEntity piv,camera
	TurnEntity entity,0,180+EntityYaw(piv,False),0
	FreeEntity piv	
End Function

;*** Control mouse movements loop
.MOUSECTRL
  	mx#=mx#+MouseXSpeed()*.3
   	mz#=mz#-MouseYSpeed()*.3
   	my#=my#+MouseZSpeed()*5
   	MoveMouse 400,300
   	m= MouseDown(1)+2*MouseDown(2)
   	If m=3
		PositionEntity camera,EntityX(camera)+mx#,0,EntityZ(camera)+mz#
	Else If m=2
		MoveEntity camera,mx#,mz#,0
	Else If m=1
		PositionEntity cone,EntityX(cone)+mx#,EntityY(cone)+ mz#,0
	Else
		PositionEntity cone,EntityX(cone)+mx#,0,EntityZ(cone)+mz#
   	End If
   	
   	;reset mouse vars
   	mx#=0 : mz#=0 : my#=0
Return
