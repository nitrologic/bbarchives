; ID: 601
; Author: mrtricks
; Date: 2003-02-24 15:54:46
; Title: Terrain Colourmap Painter
; Description: Simple program to help paint a colourmap straight onto a terrain...

;------------------------------------------
;                             
;  LIGHTMAP PAINTING UTILITY  
;                             
;        by Robin King        
;    all seeing i software    
;   www.all-seeing-i.co.uk    
;                             
;------------------------------------------

Graphics3D 800,600,32,1

;SET UP LIGHTING
light=CreateLight()
RotateEntity light,45,45,0
LightColor light,192,192,192
AmbientLight 0,0,0

;LOAD TERRAIN
terrain=LoadTerrain( "terrain256.bmp" )
ScaleEntity terrain,4,120,4
;TerrainShading terrain,True
TerrainDetail terrain,8000,True
EntityType terrain,2
EntityPickMode terrain,2

;APPLY TEXTURES TO TERRAIN
;texture0=LoadTexture( "texture1.bmp",9 )
;ScaleTexture texture0,.25,.25
;EntityTexture terrain,texture0,0,0
;texture1=LoadTexture( "texture2.bmp",9 )
;ScaleTexture texture1,2,2
;EntityTexture terrain,texture1,0,2
lmap=CreateTexture(256,256)
For a=0 To 255
	For b=0 To 255
		WritePixel a,b,$ffffff,TextureBuffer(lmap)
	Next
Next

ScaleTexture lmap,TerrainSize(terrain),TerrainSize(terrain)
EntityTexture terrain,lmap,0,1

;CREATE SEA OBJECT
sea1=CreatePlane(8)
PositionEntity sea1,0,12.095050,0
EntityColor sea1,32,96,255
EntityAlpha sea1,0.6
sea2=CreatePlane(8)
PositionEntity sea2,0,12.095050,0
RotateEntity sea2,0,0,180
EntityColor sea2,32,96,255
EntityAlpha sea2,0.6
EntityParent sea2,sea1,True

;CREATE CAMERA
Global camera=CreateCamera()
CameraRange camera,0.1,10000
CameraFogMode camera,True
CameraFogRange camera,500,2000
CameraFogColor camera,96,192,255
CameraClsColor camera,96,192,255
PositionEntity camera,0,38.688118,0
RotateEntity camera,10,-45,0
CameraZoom camera,1
EntityType camera,1
EntityRadius camera,.5,.5
Global cam2=CreatePivot()
RotateEntity cam2,10,-45,0
Global cam3=CreatePivot(camera)




Collisions 1,2,2,2
Global sx[4],sy[4]
Global rd,gn,bl
Dim sp#(3) ;x,y,z
acc#=0.3






;MAIN LOOP
Repeat


	If picking=0

	;MOUSE LOOK
	mx=MouseXSpeed()
	my=MouseYSpeed()
	
	xc#=EntityPitch(cam2)+my
	If xc<-50 Then xc=-50
	If xc>80 Then xc=80

	yc#=EntityYaw(cam2)-mx
	
	RotateEntity cam2,xc,yc,0

	
	
	RotateEntity cam3,EntityPitch(cam2,True),EntityYaw(cam2,True),0,True
	
	cx=EntityPitch(cam3,False)
	cy=EntityYaw(cam3,False)
	
	
	TurnEntity camera,cx*.3,cy*.3,0
	
	RotateEntity camera,EntityPitch(camera),EntityYaw(camera),0
	MoveMouse 320,240

	;LEFT ARROW KEY
	If KeyDown(203) sp(1)=sp(1)-acc#
	;RIGHT ARROW KEY
	If KeyDown(205) Then sp(1)=sp(1)+acc#
	;UP ARROW KEY
	If KeyDown(200) sp(3)=sp(3)+acc#
	;DOWN ARROW KEY
	If KeyDown(208) sp(3)=sp(3)-acc#
	;NUM 0 KEY
	If KeyDown(82) sp(2)=sp(2)+acc#
	;R CTRL KEY
	If KeyDown(157) sp(2)=sp(2)-acc#
	
	
	sp(1)=sp(1)*.95
	sp(2)=sp(2)*.95
	sp(3)=sp(3)*.95
	

	MoveEntity camera,sp(1),sp(2),sp(3)
	
	EndIf
	
	
	If KeyHit(28) Then picking=1-picking: If picking=0 Then MoveMouse 320,240: a=MouseXSpeed(): a=MouseYSpeed()
	
	
	If KeyDown(71) ;7
		If rd<255 rd=rd+1
	EndIf
	If KeyDown(75) ;4
		If rd>0 rd=rd-1
	EndIf
	If KeyDown(72) ;8
		If gn<255 gn=gn+1
	EndIf
	If KeyDown(76) ;5
		If gn>0 gn=gn-1
	EndIf
	If KeyDown(73) ;9
		If bl<255 bl=bl+1
	EndIf
	If KeyDown(77) ;6
		If bl<255 bl=bl-1
	EndIf
	
	
	
	If picking
	
	CameraPick(camera,MouseX(),MouseY())
	
		x#=PickedX()
		y#=PickedY()
		z#=PickedZ()
		
		xsq=(x-2)/4
		zsq=(z-2)/4

		x#=xsq*4
		z#=zsq*4
		y#=TerrainHeight(terrain,xsq,zsq)*120
		CameraProject camera,x,y,z
		sx[1]=ProjectedX()
		sy[1]=ProjectedY()
		
		x#=(xsq+1)*4
		z#=zsq*4
		y#=TerrainHeight(terrain,xsq+1,zsq)*120	
		CameraProject camera,x,y,z
		sx[2]=ProjectedX()
		sy[2]=ProjectedY()
		
		x#=xsq*4
		z#=(zsq+1)*4
		y#=TerrainHeight(terrain,xsq,zsq+1)*120
		CameraProject camera,x,y,z
		sx[3]=ProjectedX()
		sy[3]=ProjectedY()
		
		x#=(xsq+1)*4
		z#=(zsq+1)*4
		y#=TerrainHeight(terrain,xsq+1,zsq+1)*120
		CameraProject camera,x,y,z
		sx[4]=ProjectedX()
		sy[4]=ProjectedY()
		
		
		If MouseDown(1)
			
			argb=(rd*256*256)+(gn*256)+bl
			
			
			WritePixel xsq,255-zsq,argb,TextureBuffer(lmap)
		EndIf
		
	
	EndIf
	
	
	
	
	UpdateWorld
	RenderWorld
	
		If picking
			Color 255,255,0
			Line sx[1],sy[1],sx[2],sy[2]			
			Line sx[2],sy[2],sx[4],sy[4]			
			Line sx[4],sy[4],sx[3],sy[3]			
			Line sx[3],sy[3],sx[1],sy[1]
			
			Color 255,255,255
			Oval MouseX()-2,MouseY()-2,5,5,0
		Else
	
	Color 255,255,255
	Oval 398,298,5,5,0
		EndIf			
			
	Color rd,gn,bl
	Rect 0,0,16,16
	
	
	Flip

Until KeyDown(1)

SaveBuffer(TextureBuffer(lmap),"lmap"+Str$(Rand(9999))+".bmp")

End
