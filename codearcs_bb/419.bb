; ID: 419
; Author: EdzUp[GD]
; Date: 2002-09-07 09:29:25
; Title: 3d laser sight code
; Description: This code will create an entity that acts as a laser sight from a firer model to the target

;
;	LaserSight.bb - Copyright ©2002 EdzUp
;	Coded by Ed Upton
;

;Uses Zenith's CreateQuad function (modified by EdzUp ;) )

Graphics3D 640,480,16
SetBuffer BackBuffer()

Global Camera = CreateCamera()

Global Sight = CreateSight()
EntityFX Sight, 17				;make it double sided and unlit
EntityColor Sight,255,0,0
ScaleEntity Sight,1,.02,1

AmbientLight 255,255,255

MoveEntity camera,0,10,0		;move camera to above
PointEntity camera,sight		;point camera at laser sight

Global dummyfire = CreatePivot()		;this is the firing entity
Global Cube = CreateCube()				;this is the entity that would be like a wall etc
MoveEntity Cube,5,0,0

EntityPickMode Cube,2

While Not KeyDown(1)
	TurnEntity DummyFire,0,1,0			;keep rotating the firer
	PositionEntity Sight, EntityX#( DummyFire ), EntityY#( DummyFire ), EntityZ#( DummyFire ) ;position the laser sight at firer
	RotateEntity Sight, EntityPitch#( DummyFire )+90, EntityYaw#( DummyFire )+90, EntityRoll#( DummyFire );rotate it correct angle
	Picked = EntityPick( DummyFire, 50 )		;50 being max range of sight
	If Picked=0
		ScaleEntity Sight,50,.02,1				;scale it to max range
	Else
		ScaleEntity Sight,EntityDistance#( dummyfire, picked ),.02,1 ;otherwise scale to range of picking
	EndIf
	UpdateWorld
	RenderWorld
	Flip
Wend
End

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
	TurnEntity sprite,90,0,0
    Return sprite
End Function
