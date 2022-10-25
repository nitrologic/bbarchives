; ID: 3095
; Author: RemiD
; Date: 2014-01-01 05:37:43
; Title: Retrieve the kind (listname), the id (handle), of a pickable or of a collidable after a linepick or after a collision (vTypes)
; Description: How to know in which list to search and which id (handle) to search after a linepick or after a collision (vTypes)

;Retrieve the kind (listname), the id (handle), of a pickable or of a collidable after a linepick or after a collision (vTypes)
;This is useful to know in which list to search and which id (handle) to search after a linepick or after a collision
;with different lists of different kinds
;with entities stored in Types

;Notes : 
;by "pickable" i mean a pivot (with EntityRadius Or EntityBox) or a mesh set as pickable (with EntityPickMode)
;by "collidable" i mean a pivot (with EntityRadius Or EntityBox) or a mesh set as collidable (with EntityType and Collisions)
;The Camera (start point of the linepick) must be near enough a pickable to be able to pick it (less than 10units)
;The ColliderE must be near enough a collidable to be able to provoke a collision (less than 10units)
;Hit the MouseKeyLeft to throw a linepick and maybe see the results of a pick
;Hit the MouseKeyRight to throw a colliderE and maybe see the results of a collision

Graphics3D(800,600,32,2)

SeedRnd(MilliSecs())

Origine = CreateCube()
ScaleMesh(Origine,0.01/2,0.01/2,0.01/2)
EntityColor(Origine,255,000,000)

Global Camera = CreateCamera()
CameraRange(Camera,0.1,100)
Global CameraPitch# = 0
Global CameraYaw# = 0

Const GroupColliderE% = 1
Const GroupTerrain% = 2
Const GroupBalls% = 3
Const GroupCrates% = 4
Const GroupBarrels% = 5

;Terrain
Global TerrainB3D = CreateTerrain(32)
For VX% = 0 To 31
 For VZ% = 0 To 31
  HeightRGB% = Rand(030,033)
  Height# = 1.0/255*HeightRGB
  ModifyTerrain(TerrainB3D,VX,VZ,Height,False) 
 Next
Next
ScaleEntity(TerrainB3D,1,25.5,1)
TerrainDetail(TerrainB3D,2048,True)
TerrainShading(TerrainB3D,True)
PositionEntity(TerrainB3D,0,0,0)
EntityColor(TerrainB3D,Rnd(125,255),Rnd(125,255),Rnd(125,255))
NameEntity(TerrainB3D,"TER"+Str(1))
EntityType(TerrainB3D,GroupTerrain)

;Balls
Global BallsCount% = 0
Type Ball
 Field Mesh
End Type

For i% = 1 To 10
 BallsCount = BallsCount + 1
 ball.Ball = New Ball
 ball\Mesh = CreateSphere(8)
 ScaleMesh(ball\Mesh,0.25/2,0.25/2,0.25/2)
 PositionMesh(ball\Mesh,0,0.25/2,0)
 .LineChooseBallPosition
 X# = Rnd(1,31)
 Z# = Rnd(1,31)
 Y# = TerrainY(TerrainB3D,X,0,Z)
 PositionEntity(ball\Mesh,X,Y,Z)
 If(BallsCount > 1)
  For oball.Ball = Each Ball
   If(oball <> ball)
    Distance# = EntityDistance(ball\Mesh,oball\Mesh)
    If(Distance <= 0.125+0.125)
     Goto LineChooseBallPosition
    EndIf
   EndIf
  Next
 EndIf
 EntityColor(ball\Mesh,Rnd(125,255),Rnd(125,255),Rnd(125,255))
 NameEntity(ball\Mesh,"BAL"+Str(Handle(ball)))
 EntityType(ball\Mesh,GroupBalls)
Next

;Crates
Global CratesCount% = 0
Type Crate
 Field Mesh
End Type

For i% = 1 To 10
 CratesCount = CratesCount + 1
 crate.Crate = New Crate
 crate\Mesh = CreateCube()
 ScaleMesh(crate\Mesh,0.5/2,0.5/2,0.5/2)
 PositionMesh(crate\Mesh,0,0.5/2,0)
 .LineChooseCratePosition
 X# = Rnd(1,31)
 Z# = Rnd(1,31)
 Y# = TerrainY(TerrainB3D,X,0,Z)
 PositionEntity(crate\Mesh,X,Y,Z)
 For ball.Ball = Each Ball
  Distance# = EntityDistance(crate\Mesh,ball\Mesh)
  If(Distance <= 0.25+0.125)
   Goto LineChooseCratePosition
  EndIf  
 Next
 If(CratesCount > 1)
  For ocrate.Crate = Each Crate
   If(ocrate <> crate)
    Distance# = EntityDistance(crate\Mesh,ocrate\Mesh)
    If(Distance <= 0.25+0.25)
     Goto LineChooseCratePosition
    EndIf
   EndIf
  Next
 EndIf
 EntityColor(crate\Mesh,Rnd(125,255),Rnd(125,255),Rnd(125,255))
 NameEntity(crate\Mesh,"CRA"+Str(Handle(crate)))
 EntityType(crate\Mesh,GroupCrates)
Next

;Barrels
Global BarrelsCount% = 0
Type Barrel
 Field Mesh
End Type

For i% = 1 To 10
 BarrelsCount = BarrelsCount + 1
 barrel.Barrel = New Barrel
 barrel\Mesh = CreateCylinder(8)
 ScaleMesh(barrel\Mesh,0.5/2,0.75/2,0.5/2)
 PositionMesh(barrel\Mesh,0,0.75/2,0)
 .LineChooseBarrelPosition
 X# = Rnd(1,31)
 Z# = Rnd(1,31)
 Y# = TerrainY(TerrainB3D,X,0,Z)
 PositionEntity(barrel\Mesh,X,Y,Z)
 For ball.Ball = Each Ball
  Distance# = EntityDistance(barrel\Mesh,ball\Mesh)
  If(Distance <= 0.25+0.125)
   Goto LineChooseBarrelPosition
  EndIf  
 Next
 For crate.Crate = Each Crate
  Distance# = EntityDistance(barrel\Mesh,crate\Mesh)
  If(Distance <= 0.25+0.25)
   Goto LineChooseBarrelPosition
  EndIf  
 Next
 If(BarrelsCount > 1)
  For obarrel.Barrel = Each Barrel
   If(obarrel <> barrel)
    Distance# = EntityDistance(barrel\Mesh,obarrel\Mesh)
    If(Distance <= 0.25+0.25)
     Goto LineChooseBarrelPosition
    EndIf
   EndIf
  Next
 EndIf
 EntityColor(barrel\Mesh,Rnd(125,255),Rnd(125,255),Rnd(125,255))
 NameEntity(barrel\Mesh,"BAR"+Str(Handle(barrel)))
 EntityType(barrel\Mesh,GroupBarrels)
Next

Global PickedPickable%
Global PickedName$
Global PickedKind$
Global PickedId%

Global CollidedCollidable%
Global CollidedName$
Global CollidedKind$
Global CollidedId%

PositionEntity(Camera,16,3.3+1.6,0)

DLight = CreateLight(1)
LightColor(DLight,255,255,255)
PositionEntity(DLight,-1000,1000,-1000)
RotateEntity(DLight,45,-45,0)

AmbientLight(050,050,050)

Repeat

 UpdateCamera()

 If(KeyDown(2)=1)
  WireFrame(True)
 Else
  WireFrame(False)
 EndIf

 SetBuffer(BackBuffer())
 RenderWorld()

 Text(0,0,"PickedPickable = "+Str(PickedPickable))
 Text(0,20,"PickedName = "+PickedName)
 Text(0,40,"PickedKind = "+PickedKind)
 Text(0,60,"PickedId = "+Str(PickedId))

 Text(0,100,"CollidedCollidable = "+Str(CollidedCollidable))
 Text(0,120,"CollidedName = "+CollidedName)
 Text(0,140,"CollidedKind = "+CollidedKind)
 Text(0,160,"CollidedId = "+Str(CollidedId))

 Flip(1)

Until(KeyDown(1)=1)

EndGraphics()
End()

Function UpdateCamera()
 MXSpeed# = MouseXSpeed()  
 MYSpeed# = MouseYSpeed()
 MoveMouse(GraphicsWidth()/2,GraphicsHeight()/2)  
 CameraPitch = CameraPitch + MYSpeed*0.1 
 If(CameraPitch > 89)
  CameraPitch = 89
 EndIf
 If(CameraPitch < -89)
  CameraPitch = -89
 EndIf
 CameraYaw = CameraYaw - MXSpeed*0.1   
 RotateEntity(Camera,CameraPitch,CameraYaw,0)
 If(KeyDown(17)=1)
  MoveEntity(Camera,0,0,0.1)
 ElseIf(KeyDown(31)=1)
  MoveEntity(Camera,0,0,-0.1)
 EndIf
 If(MouseHit(1)>0)
  ThrowLinePick()
 ElseIf(MouseHit(2)>0)
  ThrowColliderE()
 EndIf
End Function

Function ThrowLinePick()
 EntityPickMode(TerrainB3D,2)
 For ball.Ball = Each Ball
  EntityPickMode(ball\Mesh,2)
 Next
 For crate.Crate = Each Crate
  EntityPickMode(crate\Mesh,2)
 Next
 For barrel.Barrel = Each Barrel
  EntityPickMode(barrel\Mesh,2)
 Next
 TFormVector(0,0,10,Camera,0)
 LinePick(EntityX(Camera,True),EntityY(Camera,True),EntityZ(Camera,True),TFormedX(),TFormedY(),TFormedZ())
 PickedPickable = PickedEntity()
 If(PickedPickable <> 0)
  DebugPickedPoint = CreateCube()
  ScaleMesh(DebugPickedPoint,0.01/2,0.01/2,0.01/2)
  PositionEntity(DebugPickedPoint,PickedX(),PickedY(),PickedZ())
  EntityColor(DebugPickedPoint,255,000,000)
  PickedName$ = EntityName(PickedPickable)
  PickedKind$ = Left(PickedName,3)
  PickedId% = Right(PickedName,Len(PickedName)-3)
  If(PickedKind = "TER")
   EntityColor(TerrainB3D,Rnd(125,255),Rnd(125,255),Rnd(125,255))
  ElseIf(PickedKind = "BAL")
   ball.Ball = Object.Ball(PickedId)
   EntityColor(ball\Mesh,Rnd(125,255),Rnd(125,255),Rnd(125,255))
  ElseIf(PickedKind = "CRA")
   crate.Crate = Object.Crate(PickedId)
   EntityColor(crate\Mesh,Rnd(125,255),Rnd(125,255),Rnd(125,255))
  ElseIf(PickedKind = "BAR")
   barrel.Barrel = Object.Barrel(PickedId)
   EntityColor(barrel\Mesh,Rnd(125,255),Rnd(125,255),Rnd(125,255))
  EndIf
 EndIf
 EntityPickMode(TerrainB3D,0)
 For ball.Ball = Each Ball
  EntityPickMode(ball\Mesh,0)
 Next
 For crate.Crate = Each Crate
  EntityPickMode(crate\Mesh,0)
 Next
 For barrel.Barrel = Each Barrel
  EntityPickMode(barrel\Mesh,0)
 Next
End Function

Function ThrowColliderE()
 ColliderE = CreatePivot()
 EntityRadius(ColliderE,0.251)
 PositionEntity(ColliderE,EntityX(Camera,True),EntityY(Camera,True),EntityZ(Camera,True))
 RotateEntity(ColliderE,EntityPitch(Camera,True),EntityYaw(Camera,True),EntityRoll(Camera,True))
 EntityType(ColliderE,GroupColliderE)
 Collisions(GroupColliderE,GroupTerrain,2,1)
 Collisions(GroupColliderE,GroupBalls,2,1)
 Collisions(GroupColliderE,GroupCrates,2,1)
 Collisions(GroupColliderE,GroupBarrels,2,1)
 For i% = 1 To 10.0/0.25 Step 1
  MoveEntity(ColliderE,0,0,0.25)
  UpdateWorld()
  CC% = CountCollisions(ColliderE)
  If(CC% > 0)
   CollidedCollidable = CollisionEntity(ColliderE,1)
   If(CollidedCollidable <> 0)
    DebugCollidedPoint = CreateCube()
    ScaleMesh(DebugCollidedPoint,0.01/2,0.01/2,0.01/2)
    PositionEntity(DebugCollidedPoint,CollisionX(ColliderE,1),CollisionY(ColliderE,1),CollisionZ(ColliderE,1))
    EntityColor(DebugCollidedPoint,255,000,255)
    CollidedName$ = EntityName(CollidedCollidable)
    CollidedKind$ = Left(CollidedName,3)
    CollidedId% = Right(CollidedName,Len(CollidedName)-3)
    If(CollidedKind = "TER")
     EntityColor(TerrainB3D,Rnd(125,255),Rnd(125,255),Rnd(125,255))
    ElseIf(CollidedKind = "BAL")
     ball.Ball = Object.Ball(CollidedId)
     EntityColor(ball\Mesh,Rnd(125,255),Rnd(125,255),Rnd(125,255))
    ElseIf(CollidedKind = "CRA")
     crate.Crate = Object.Crate(CollidedId)
     EntityColor(crate\Mesh,Rnd(125,255),Rnd(125,255),Rnd(125,255))
    ElseIf(CollidedKind = "BAR")
     barrel.Barrel = Object.Barrel(CollidedId)
     EntityColor(barrel\Mesh,Rnd(125,255),Rnd(125,255),Rnd(125,255))
    EndIf
   EndIf
  EndIf
 Next
 DebugColliderE = CreateSphere(16)
 ScaleMesh(DebugColliderE,0.5/2,0.5/2,0.5/2)
 PositionEntity(DebugColliderE,EntityX(ColliderE,True),EntityY(ColliderE,True),EntityZ(ColliderE,True))
 RotateEntity(DebugColliderE,EntityPitch(ColliderE,True),EntityYaw(ColliderE,True),EntityRoll(ColliderE,True))
 EntityAlpha(DebugColliderE,0.5)
 ClearCollisions()
 FreeEntity(ColliderE)
End Function
