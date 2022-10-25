; ID: 3141
; Author: RemiD
; Date: 2014-08-26 12:25:04
; Title: basic collisions example
; Description: turningmoving colliders (ellipsoid) -> static collidables (mesh) | turningmoving colliders (ellipsoid) <-> turningmoving colliders (ellipsoid)

;Here is an example of how to use the collision system and how to manage the turningmoving colliders and the static collidables
;collidables = static meshes
;colliders = turningmoving ellipsoids
;turningmoving colliders (ellipsoid) -> static collidables (mesh) | turningmoving colliders (ellipsoid) <-> turningmoving colliders (ellipsoid)
;Z,S,Q,D to turn move the first ellipsoid collider
;UP,DOWN,LEFT,RIGHT to turn move the second ellipsoid collider

Graphics3D(640,480,32,2)

SeedRnd(MilliSecs())

Camera = CreateCamera()
CameraRange(Camera,0.1,100)
CameraClsColor(Camera,000,000,000)

;create position rotate the statics collidables (meshes)
Global StaticsCount%
Dim Static(10)

For n% = 1 To 10
 StaticsCount = StaticsCount + 1
 I% = StaticsCount
 C% = Rand(1,2)
 If( C = 1 )
  Static(I) = CreateCube()
 Else If( C = 2 )
  Static(I) = CreateCylinder(8)
 EndIf
 ScaleMesh(Static(I),0.5/2,0.5/2,0.5/2)
 PositionMesh(Static(I),0,0.5/2,0)
 .LineChooseStaticPosition
 PositionEntity(Static(I),Rnd(-5,5),0,Rnd(-5,5))
 ;is there another static at this position ?
 For OI% = 1 To StaticsCount
  If(OI <> I)
   If(EntityDistance(Static(I),Static(OI)) < 0.36+0.36)
    Goto LineChooseStaticPosition
   EndIf
  EndIf
 Next
Next

;create position rotate the turningmovings colliders (ellipsoids)
Global TurningMovingsCount%
Dim TurningMoving(10)

For n% = 1 To 2
 TurningMovingsCount = TurningMovingsCount + 1
 I% = TurningMovingsCount
 TurningMoving(I) = CreateSphere(16)
 ScaleMesh(TurningMoving(I),0.5/2,0.5/2,0.5/2)
 DebugTurningMovingDirection = CreateCube()
 ScaleMesh(DebugTurningMovingDirection,0.01/2,0.01/2,1.0/2)
 PositionMesh(DebugTurningMovingDirection,0,0,1.0/2)
 PositionEntity(DebugTurningMovingDirection,EntityX(TurningMoving(I),True),EntityY(TurningMoving(I),True),EntityZ(TurningMoving(I),True))
 RotateEntity(DebugTurningMovingDirection,EntityPitch(TurningMoving(I),True),EntityYaw(TurningMoving(I),True),EntityRoll(TurningMoving(I),True))
 EntityParent(DebugTurningMovingDirection,TurningMoving(I),True)
 .LineChooseTurningMovingPosition
 PositionEntity(TurningMoving(I),Rnd(-5,5),0.25,Rnd(-5,5))
 ;is there a static at this position ?
 For OI% = 1 To StaticsCount
  If(EntityDistance(TurningMoving(I),Static(OI)) < 0.25+0.36)
   Goto LineChooseTurningMovingPosition
  EndIf
 Next
 ;is there another turningmoving at this position ?
 For OI% = 1 To StaticsCount
  If(OI <> I)
   If(EntityDistance(Static(I),Static(OI)) < 0.25+0.25)
    Goto LineChooseTurningMovingPosition
   EndIf
  EndIf
 Next
 RotateEntity(TurningMoving(I),0,Rnd(-180,180),0)
Next

PositionEntity(Camera,0,7.5,-7.5)
RotateEntity(Camera,45,0,0)

;put each static collidable in a collision group
For I% = 1 To StaticsCount
 EntityType(Static(I),1) ;group 1
Next

;put each turningmoving collider in a collision group
For I% = 1 To TurningMovingsCount
 EntityRadius(TurningMoving(I),0.25)
 EntityType(TurningMoving(I),2) ;group 2
Next

;configure the collision detection and response between the different collisions groups
Collisions(2,1,2,1) ;collision group 2 -> group 1, ellipsoid to mesh, stop
Collisions(2,2,1,1) ;collision group 2 -> group 2, ellipsoid to ellipsoid, stop

While(KeyDown(1)=0)

 ;turn move turningmoving 1
 If(KeyDown(30)=1)
  TurnEntity(TurningMoving(1),0,1.5,0)
 ElseIf(KeyDown(32)=1)
  TurnEntity(TurningMoving(1),0,-1.5,0)
 EndIf
 If(KeyDown(17)=1)
  MoveEntity(TurningMoving(1),0,0,0.05)
 ElseIf(KeyDown(31)=1)
  MoveEntity(TurningMoving(1),0,0,-0.05)
 EndIf
 
 ;turn move turningmoving 2
 If(KeyDown(203)=1)
  TurnEntity(TurningMoving(2),0,1.5,0)
 ElseIf(KeyDown(205)=1)
  TurnEntity(TurningMoving(2),0,-1.5,0)
 EndIf
 If(KeyDown(200)=1)
  MoveEntity(TurningMoving(2),0,0,0.05)
 ElseIf(KeyDown(208)=1)
  MoveEntity(TurningMoving(2),0,0,-0.05)
 EndIf
 
 ;detect collisions update colliders depending on the "response"
 UpdateWorld()

 For I% = 1 To StaticsCount
  EntityColor(Static(I),255,255,255)
 Next
 For I% = 1 To TurningMovingsCount
  EntityColor(TurningMoving(I),255,255,255)
 Next

 ;for each turningmoving collider
 For I% = 1 To TurningMovingsCount
  ;check if a collision happened between this turningmoving collider and a static collidable or another turningmoving collider
  CC% = CountCollisions(TurningMoving(I))
  ;if yes
  If(CC > 0)
   ;retrieve the collided with CollisionEntity(Collider,1) ;note : in most cases there is only one collision but if CountCollisions(Collider) is superior to 1, you can analyze the others collisions
   ;retrieve the collision point with CollisionX(Collider,1) CollisionY(Collider,1) CollisionZ(Collider,1) ;same note as above
   ;retrieve the collision normal with CollisionNX(Collider,1) CollisionNY(Collider,1) CollisionNZ(Collider,1) ;same note as above
   ;update the entities as you want...
   EntityColor(TurningMoving(I),255,000,000) ;turningmoving collider (ellipsoid)
   EntityColor(CollisionEntity(TurningMoving(I),1),125,000,000) ;static collidable (mesh) or another turningmoving collider (ellipsoid)
  EndIf
 Next

 SetBuffer(BackBuffer())
 RenderWorld()

 Flip(1)

Wend

End()
