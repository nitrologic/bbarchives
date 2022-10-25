; ID: 439
; Author: EdzUp[GD]
; Date: 2002-09-28 08:10:22
; Title: 3d lightning
; Description: a way of generating a 3d lightning effect

;
;	Lightning.bb - Copyright ©2002 EdzUp
;	Coded by Ed Upton
;

;Uses Zenith's CreateQuad function (modified by EdzUp ;) )

Graphics3D 640,480,16,2
SetBuffer BackBuffer()

Global camera = CreateCamera()

AmbientLight 255,255,255

Global emitter = CreateSphere()
EntityColor Emitter, 255, 0, 0
MoveEntity Emitter, -3,0,0

Global target = CreateSphere()
EntityColor target, 0, 255, 0
MoveEntity target, 3,0,0

MoveEntity camera, 0, 0, -5

ScaleEntity emitter,.1,.1,.1
ScaleEntity Target,.1,.1,.1

Global Range# = 0
Global Division# = 0
Global DummyEntity = CreatePivot()

Type LightningType
	Field Entity
End Type

Global ang#=0

While Not KeyDown(1)
	ang# = ang# + 1
	
	PositionEntity camera, 0, 0, 0
	RotateEntity camera, 0, ang#, 0
	MoveEntity camera, 0,0,-5
	ResetLightning()
	CreateLightning( emitter, target, 10, .5 )
	UpdateWorld
	RenderWorld
	Flip
Wend
End

Function ResetLightning()
	For Lightning.LightningType = Each LightningType
		If Lightning<>Null
			If Lightning\Entity<>0 Then FreeEntity Lightning\Entity
			Delete Lightning
		EndIf
	Next
End Function

Function CreateLightning( FromEntity, ToEntity, Parts, Deviation# )
	Local DeviationEntity = CreatePivot()
	Local TargetEntity = CreatePivot()
	Local TE=CreatePivot()
	Local PartCount = 0
	
	PositionEntity DummyEntity, EntityX#( FromEntity ), EntityY#( FromEntity ), EntityZ#( FromEntity )
	PointEntity DummyEntity, ToEntity
	Range# = EntityDistance#( FromEntity, ToEntity )
	
	Division# = Range# / Parts
	
	PositionEntity DeviationEntity, EntityX#( FromEntity ), EntityY#( FromEntity ), EntityZ#( FromEntity )
	
	For PartCount = 0 To Parts-1
		PositionEntity TargetEntity, EntityX#( DummyEntity ), EntityY#( DummyEntity ), EntityZ#( DummyEntity )
		MoveEntity TargetEntity, 0, Rnd( Deviation# )-Rnd( Deviation# ), Rnd( Deviation# )-Rnd(Deviation#)

		Lightning.LightningType = New LightningType
		Lightning\Entity = CreateSight()

		PointEntity DeviationEntity, TargetEntity
		
		PositionEntity Lightning\Entity, EntityX#( Deviationentity ), EntityY#( Deviationentity ), EntityZ#( Deviationentity )
		RotateEntity Lightning\Entity, EntityPitch#( DeviationEntity ), EntityYaw#( DeviationEntity ), EntityRoll#( DeviationEntity )
		ScaleEntity Lightning\Entity, EntityDistance#( DeviationEntity, TargetEntity ), .02, 1
		TurnEntity Lightning\Entity, 0, 90, 0
		
		;move the deviationentity to the target
		PositionEntity DeviationEntity, EntityX#( TargetEntity, 1 ), EntityY#( TargetEntity, 1 ), EntityZ#( TargetEntity, 1 )
		MoveEntity DummyEntity, 0, 0, Division#

	Next

	PositionEntity TargetEntity, EntityX#( ToEntity ), EntityY#( ToEntity ), EntityZ#( ToEntity )

	Lightning.LightningType = New LightningType
	Lightning\Entity = CreateSight()

	PointEntity DeviationEntity, TargetEntity
		
	PositionEntity Lightning\Entity, EntityX#( Deviationentity ), EntityY#( Deviationentity ), EntityZ#( Deviationentity )
	RotateEntity Lightning\Entity, EntityPitch#( DeviationEntity ), EntityYaw#( DeviationEntity ), EntityRoll#( DeviationEntity )
	ScaleEntity Lightning\Entity, EntityDistance#( DeviationEntity, TargetEntity ), .02, 1
	TurnEntity Lightning\Entity, 0, 90, 0
	
	FreeEntity DeviationEntity
	FreeEntity TargetEntity
End Function

Function CreateSight()
	;by Zenith
    sprite=CreateMesh()
    he=CreateBrush(255,255,255)
    v=CreateSurface(sprite,he)
    FreeBrush he
    AddVertex ( v,0,1,0,1,0)  ; top left 0,1;1,0
    AddVertex ( v,1,1,0,0,0)   ; top right 1,1;1,1
    AddVertex ( v,0,-1,0,1,1) ; bottom left 0,0;,0,0
    AddVertex ( v,1,-1,0,0,1)  ; bottom right 1,0;0,1
    AddTriangle( v,0,1,2)
    AddTriangle( v,3,2,1)
	TurnEntity sprite,0,0,0
	EntityFX sprite,17
	
    Return sprite
End Function
