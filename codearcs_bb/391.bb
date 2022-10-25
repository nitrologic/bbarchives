; ID: 391
; Author: Rob 
; Date: 2002-08-10 14:18:17
; Title: Geometric based per-entity glow
; Description: makes an entity of your choice glow with the shape of that entity. Makes your entity properly glow in the dark!

;GEOMETRIC GLOW TEST - Rob Cummings
;inspired by madjack!


;KEEPS TRACK OF ENTITIES
Type entlist
	Field ent
End Type


Graphics3D 1024,768,32,2
AmbientLight(10,10,20)
light=CreateLight()
PositionEntity light,-500,5000,2000
LightColor light,200,200,200
RotateEntity light,45,45,0
	
camera = CreateCamera()

ob1 = CreateCylinder()
PositionEntity ob1,0,1,7
EntityColor ob1,0,0,255
EntityFX ob1,1 
addent(ob1)
		
ob2 = CreateCone()
PositionEntity ob2,-4,1,7
EntityColor ob2,0,255,0
EntityFX ob2,1
addent(ob2)
	
ob3 = CreateSphere()
PositionEntity ob3,4,1,7
EntityColor ob3,255,255,255
addent(ob3)
	
column = CreateCylinder()
ScaleEntity column,2,100,2
PositionEntity column,50,0,100
EntityColor column,255,0,0
EntityFX column,1
addent(column)
	
	
While Not KeyHit(1)
	TurnEntity ob1,1,1,1
	TurnEntity ob2,1,1,1
	TurnEntity ob3,1,1,1
	TurnEntity column,1,1,1
	UpdateWorld
	RenderWorld 

	EntityGlow(camera,ob1,4,.2,50,50,255)
	EntityGlow(camera,ob2,3,.05,50,255,50)
	EntityGlow(camera,ob3,2,.05,255,255,0)

	
	Flip
Wend
End	
	
	
Function EntityGlow(eg_cam,eg_ent,eg_glowsteps=1,eg_glowsize#=.1,eg_r=-1,eg_g=-1,eg_b=-1)
	hideworld()
	eg_pivot=CreatePivot()
	For eg_i=eg_glowsteps To 1 Step -1
		eg_glowent=CopyMesh(eg_ent)
		ShowEntity eg_glowent
		PositionEntity eg_glowent,EntityX(eg_ent),EntityY(eg_ent),EntityZ(eg_ent)
		RotateEntity eg_glowent,EntityPitch(eg_ent),EntityYaw(eg_ent),EntityRoll(eg_ent)
		EntityFX eg_glowent,1
		EntityBlend eg_glowent,3 ;optional
		ScaleMesh eg_glowent,1+eg_glowsize*eg_i,1+eg_glowsize*eg_i,1+eg_glowsize*eg_i
		EntityAlpha eg_glowent,0.1
		EntityParent eg_glowent,eg_pivot
		If (eg_r+eg_g+eg_b)>0 EntityColor eg_glowent,eg_r,eg_g,eg_b
	Next
	CameraClsMode eg_cam,0,0:RenderWorld:CameraClsMode eg_cam,1,1
	For i=1 To CountChildren(eg_pivot):FreeEntity GetChild(eg_pivot,i):Next
	FreeEntity eg_pivot
	showworld()
End Function
	

;HOUSEKEEPING FUNCTIONS

Function AddEnt(ent)
	e.entlist=New entlist
	e\ent=ent
End Function
Function HideWorld()
	For e.entlist=Each entlist
		HideEntity e\ent
	Next
End Function
Function ShowWorld()
	For e.entlist=Each entlist
		ShowEntity e\ent
	Next
End Function
Function DeleteEnt(ent)
	For e.entlist=Each entlist
		If e\ent=ent
			Delete e
			Exit
		EndIf
	Next
End Function
Function ClearEntList()
		Delete Each entlist
End Function
