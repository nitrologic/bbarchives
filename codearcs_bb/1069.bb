; ID: 1069
; Author: Rob
; Date: 2004-06-04 16:42:05
; Title: insect crawling demo!
; Description: ewwwww! they crawl everywhere!

;insect crawling demo
;rob@redflame.net

;definitions
Const c_insect=1,c_level=2
Const pedelength=20

 ; number of segments

Global camera,worldpivot
Global campitch#,camyaw#,mvx#,mvy#,mvz#,temp#,vx#,vy#,vz#

Global ball,pivot,level,txt$

Type pede
	Field ent[pedelength],pickpivot
	Field speed#,dist#
	Field misc
End Type


;setup graphics
Graphics3D 640,480,16,2
HidePointer

camera=CreateCamera()
PositionEntity camera,0,20,0

light=CreateLight(2)
PositionEntity light,0,20,0
LightRange light,20

;load level
;level=LoadMesh("level.b3d")
level=CreateLevel()

EntityAlpha level,0.4
EntityType level,c_level
EntityPickMode level,2

;make a few pedes
For n=0 To 5
	a.pede=New pede
	a\speed=.2
	a\dist=2.1
	
	;head
	a\ent[0]=CreateSphere(4)
	EntityColor a\ent[0],255,0,0
	EntityShininess a\ent[0],1
	ScaleEntity a\ent[0],.8,.8,1.2
	EntityType a\ent[0],c_insect
	EntityRadius a\ent[0],1
	PositionEntity a\ent[0],Rnd(-50,50),2,Rnd(-50,50)
	
	;body segs
	For i=1 To pedelength
		a\ent[i]=CreateSphere(4)
		EntityColor a\ent[i],255,0,0
		EntityShininess a\ent[i],1
		ScaleEntity a\ent[i],.8,.8,1.2
	Next
	
Next

Collisions c_insect,c_level,2,2

;mainloop
While Not KeyHit(1)
	UpdateWorld
	freelook()
	updategame()
	RenderWorld
	Flip 
Wend
End

;update game
Function updategame()
	Local nx#,ny#,nz#
	For a.pede=Each pede
	
		;calculate angle and stick to wall
		TurnEntity a\ent[0],5,0,0
		nx#=0:ny#=0:nz#=0	
		coll=EntityCollided(a\ent[0],c_level)
		If coll
			numcollisions = CountCollisions(a\ent[0])
			If numcollisions>0
				For i=1 To numcollisions
					nx=nx+CollisionNX(a\ent[0],i)
					ny=ny+CollisionNY(a\ent[0],i)
					nz=nz+CollisionNZ(a\ent[0],i)
				Next
				nx=nx/numcollisions
				ny=ny/numcollisions
				nz=nz/numcollisions
				AlignToVector a\ent[0],nx,ny,nz,2,.25
			EndIf ;numcollisions
		EndIf ;coll
		
		;the usual ai and stuff to do with speed and direction here
		If Rand(30)=1 TurnEntity a\ent[0],0,Rnd(-30,30),0
		MoveEntity a\ent[0],0,0,a\speed
	
		;update body segs
		For i=1 To pedelength	
			d#=EntityDistance(a\ent[i],a\ent[i-1])
			If d#>a\dist
				PointEntity a\ent[i],a\ent[i-1]
				MoveEntity a\ent[i],0,0,a\speed+d#-a\dist
			Else
				MoveEntity a\ent[i],0,0,a\speed+d#-a\dist
			EndIf
		Next

	Next
End Function

;camera freelook
Function freelook()
	mxspd#=MouseXSpeed()*0.25
	myspd#=MouseYSpeed()*0.25
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	campitch=campitch+myspd
	If campitch<-85 Then campitch=-85
	If campitch>85 Then campitch=85
	RotateEntity camera,campitch,EntityYaw(camera)-mxspd,0
	If KeyDown(203) Then mvx=mvx-.3
	If KeyDown(205) Then mvx=mvx+.3
	If KeyDown(200) Then mvz=mvz+.3
	If KeyDown(208) Then mvz=mvz-.3
	If KeyDown(30) Then mvx=mvx-.3
	If KeyDown(32) Then mvx=mvx+.3
	If KeyDown(17) Then mvz=mvz+.3
	If KeyDown(31) Then mvz=mvz-.3
	mvx#=mvx*.8
	mvy#=mvy*.8
	mvz#=mvz*.8
	MoveEntity camera,mvx,mvy,mvz
End Function

;create a test level
Function CreateLeveL()
	mesh=CreateMesh()
	
	box=CreateCube()
	FlipMesh box
	ScaleMesh box,40,23,32
	PositionMesh box,0,23,0
	AddMesh box,mesh
	
	box=CreateCube()
	ScaleMesh box,10,3,10
	PositionMesh box,22,3,-3
	AddMesh box,mesh

	box=CreateCube()
	ScaleMesh box,8,7,10
	PositionMesh box,-34,7,-22
	AddMesh box,mesh

	box=CreateCube()
	ScaleMesh box,8,6,13
	PositionMesh box,32,40,-10
	AddMesh box,mesh

	box=CreateCube()
	ScaleMesh box,16,6,4
	PositionMesh box,-12,40,4
	AddMesh box,mesh

	Return mesh
End Function
