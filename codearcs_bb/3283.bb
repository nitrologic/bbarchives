; ID: 3283
; Author: RemiD
; Date: 2016-07-14 05:27:33
; Title: capsule collider using one pivot + several ellipsoids + collidables
; Description: for characters, for vehicles, using Blitz3d collision system

Graphics3D(800,600,32,2)

SeedRnd(MilliSecs())

Origine = CreateCube()
ScaleMesh(Origine,0.01/2,0.01/2,0.01/2)
EntityColor(Origine,255,000,000)

;Input
Const CUp% = 1
Const CToDown% = 2
Const CDown% = 3
Const CToUp% = 4

Global MPX% = 0
Global MPY% = 0
Global MXDiff# = 0
Global MYDiff# = 0

Global MZOld% = 0
Global MZCur% = 0
Global MWheel% = 0

Global Camera = CreateCamera()
CameraRange(Camera,0.1,100)

Global TerrainRenderer 
Global TerrainCollidable

BuildTerrain()

BuildingsMaxCount% = 100
Global BuildingsCount%
;Dim BuildingHeight#(BuildingsMaxCount)
Dim BuildingRenderer(BuildingsMaxCount)
Dim BuildingCollidable(BuildingsMaxCount)

BuildBuildings()

PlatformsMaxCount% = 100
Global PlatformsCount%
Dim PlatformRenderer(PlatformsMaxCount)
Dim PlatformCollidable(PlatformsMaxCount)

BuildPlatforms()

CharactersMaxCount% = 10
Global CharactersCount%
Dim CharacterFeet(CharactersMaxCount)
Dim CharacterEyes(CharactersMaxCount)
Dim CharacterRenderer(CharactersMaxCount)
Dim CharacterCollider(CharactersMaxCount,10)
Dim DebugCharacterCollider(CharactersMaxCount,10)
Dim CharacterState%(CharactersMaxCount)
Dim CharacterTarget(CharactersMaxCount)

BuildCharacters()

Global PlayerId%

PlayerId = Rand(1,10)
SetEntityAsFree(Camera)
PositionRotateEntityLikeOtherEntity(Camera,CharacterEyes(PlayerId))
MoveEntity(Camera,0,0,-3)
SetEntityAsChildOfOtherEntity(Camera,CharacterEyes(PlayerId))
RotateEntity(CharacterEyes(PlayerId),22.5,0,0)

;PositionEntity(Camera,0,0,-3)

DLight = CreateLight(1)
LightColor(DLight,200,200,200)
PositionEntity(DLight,-1000,1000,-1000)
RotateEntity(DLight,45,-45,0)

AmbientLight(050,050,050)

Global CollidedCollidable%
Global CollidedName$
Global CollidedKind$
Global CollidedId%

Const GroupTerrainsR% = 1
Const GroupBuildingsR% = 2
Const GroupPlatformsR% = 3
Const GroupCharactersER% = 100

Global UpdateCollidersTime%
Global UpdateCollisionsTime%

Main()

EndGraphics()

End()

Function PositionRotateEntityLikeOtherEntity(Entity,OtherEntity)
 PositionEntity(Entity,EntityX(OtherEntity,True),EntityY(OtherEntity,True),EntityZ(OtherEntity,True))
 RotateEntity(Entity,EntityPitch(OtherEntity,True),EntityYaw(OtherEntity,True),EntityRoll(OtherEntity,True))
End Function

Function PositionEntityLikeOtherEntity(Entity,OtherEntity)
 PositionEntity(Entity,EntityX(OtherEntity,True),EntityY(OtherEntity,True),EntityZ(OtherEntity,True))
End Function

Function RotateEntityLikeOtherEntity(Entity,OtherEntity)
 RotateEntity(Entity,EntityPitch(OtherEntity,True),EntityYaw(OtherEntity,True),EntityRoll(OtherEntity,True))
End Function

Function SetEntityAsChildOfOtherEntity(Entity,OtherEntity)
 EntityParent(Entity,OtherEntity,True)
End Function

Function SetEntityAsFree(Entity)
 EntityParent(Entity,0)
End Function

Function Distance2D#(PAX#,PAZ#,PBX#,PBZ#)
 VX# = PBX - PAX
 VZ# = PBZ - PAZ
 Distance2D# = Sqr((VX*VX)+(VZ*VZ))
 Return Distance2D
End Function

Function Distance3D#(PAX#,PAY#,PAZ#,PBX#,PBY#,PBZ#)
 VX# = PBX - PAX
 VY# = PBY - PAY
 VZ# = PBZ - PAZ
 Distance3D# = Sqr((VX*VX)+(VY*VY)+(VZ*VZ))
 Return Distance3D
End Function

Function BuildTerrain()

 TerrainRenderer = CreateTerrain(128)
 For VX% = 0 To 128-1
  For VZ% = 0 To 128-1
   HeightRGB% = Rand(030,033)
   Height# = 1.0/255*HeightRGB
   ModifyTerrain(TerrainRenderer,VX,VZ,Height,False) 
  Next
 Next
 ScaleEntity(TerrainRenderer,1,25.5,1)
 TerrainDetail(TerrainRenderer,4000,True)
 TerrainShading(TerrainRenderer,True)
 PositionEntity(TerrainRenderer,0,0,0)
 EntityColor(TerrainRenderer,000,100,000)
 NameEntity(TerrainRenderer,"TER"+Str(1))

End Function

Function BuildBuildings()

 For i% = 1 To 100
  BuildingsCount = BuildingsCount + 1
  BId% = BuildingsCount
  TWidth# = Rnd(1,5)
  TDepth# = Rnd(1,5)
  THeight# = Rnd(0.25,15)
  BuildingRenderer(BId) = CreateMesh()
  TPart = CreateCube()
  ScaleMesh(TPart,TWidth/2,0.3/2,TDepth/2)
  PositionMesh(TPart,0,-0.15,0)
  AddMesh(TPart,BuildingRenderer(BId))
  FreeEntity(TPart)
  TPart = CreateCube()
  ScaleMesh(TPart,TWidth/2,THeight/2,TDepth/2)
  PositionMesh(TPart,0,THeight/2,0)
  AddMesh(TPart,BuildingRenderer(BId))
  FreeEntity(TPart)
  EntityColor(BuildingRenderer(BId),100,100,100)
  BuildingCollidable(BId) = CopyMesh(BuildingRenderer(BId))
  EntityAlpha(BuildingCollidable(BId),0.25)
  NameEntity(BuildingCollidable(BId),"BUI"+Str(BId))
  .LineChooseBuildingPosition
  TX# = Rnd(1+TWidth/2,100-1-TWidth/2)
  TY# = 3.3
  TZ# = Rnd(1+TDepth/2,100-1-TDepth/2)
  PositionEntity(BuildingCollidable(BId),TX,TY,TZ)
  For OBId% = 1 To BuildingsCount
   If(OBId <> BId)
    If(MeshesIntersect(BuildingCollidable(BId),BuildingCollidable(OBId))=True)
     Goto LineChooseBuildingPosition
    EndIf
   EndIf
  Next
  PositionEntity(BuildingRenderer(BId),TX,TY,TZ)
 Next
End Function

Function BuildPlatforms()

 For i% = 1 To 100
  PlatformsCount = PlatformsCount + 1
  PId % = PlatformsCount
  TWidth# = Rnd(0.5,3)
  TDepth# = Rnd(0.5,3)
  PlatformRenderer(PId) = CreateMesh()
  TPart = CreateCube()
  ScaleMesh(TPart,TWidth/2,0.1/2,TDepth/2)
  PositionMesh(TPart,0,0.05,0)
  AddMesh(TPart,PlatformRenderer(PId))
  FreeEntity(TPart)
  EntityColor(PlatformRenderer(PId),100,100,100)
  PlatformCollidable(PId) = CopyMesh(PlatformRenderer(PId))
  EntityAlpha(PlatformCollidable(PId),0.25)
  NameEntity(PlatformCollidable(PId),"PLA"+Str(PId))
  .LineChoosePlatformPosition
  TX# = Rand(1+TWidth/2,100-1-TWidth/2)
  TY# = Rnd(3.3,10)
  TZ# = Rand(1+TDepth/2,100-1-TDepth/2)
  PositionEntity(PlatformCollidable(PId),TX,TY,TZ)
  For BId% = 1 To BuildingsCount
   If(MeshesIntersect(PlatformCollidable(PId),BuildingCollidable(BId))=True)
    Goto LineChoosePlatformPosition
   EndIf
  Next
  For OPId% = 1 To PlatformsCount
   If(OPId <> PId)
    If(MeshesIntersect(PlatformCollidable(PId),PlatformCollidable(OPId))=True)
     Goto LineChoosePlatformPosition
    EndIf
   EndIf
  Next
  PositionEntity(PlatformRenderer(PId),TX,TY,TZ)
 Next

End Function

Function BuildCharacters()

 For i% = 1 To 10
  CharactersCount = CharactersCount + 1
  CId% = CharactersCount

  R% = Rand(050,250)
  G% = Rand(050,250)
  B% = Rand(050,250)

  ;Feet
  CharacterFeet(CId) = CreatePivot()
  PositionEntity(CharacterFeet(CId),0,0,0)

  ;Eyes
  CharacterEyes(CId) = CreatePivot()
  PositionRotateEntityLikeOtherEntity(CharacterEyes(CId),CharacterFeet(CId))
  SetEntityAsChildOfOtherEntity(CharacterEyes(CId),CharacterFeet(CId))
  MoveEntity(CharacterEyes(CId),0,1.6,0.15)
  DebugCharacterEyes = CreateCube()
  ScaleMesh(DebugCharacterEyes,0.14/2,0.04/2,0.08/2)
  PositionMesh(DebugCharacterEyes,0,0,0.04)
  PositionRotateEntityLikeOtherEntity(DebugCharacterEyes,CharacterEyes(CId))
  SetEntityAsChildOfOtherEntity(DebugCharacterEyes,CharacterEyes(CId))
  EntityColor(DebugCharacterEyes,R,G,B)

  ;Mesh
  CharacterRenderer(CId) = CreateCube()
  ScaleMesh(CharacterRenderer(CId),0.5/2,1.7/2,0.25/2)
  PositionMesh(CharacterRenderer(CId),0,1.7/2,0)
  PositionRotateEntityLikeOtherEntity(CharacterRenderer(CId),CharacterFeet(CId))
  SetEntityAsChildOfOtherEntity(CharacterRenderer(CId),CharacterFeet(CId))
  EntityColor(CharacterRenderer(CId),R,G,B)

  ;DebugColliderEs
  For ii% = 1 To 10
   DebugCharacterCollider(CId,ii%) = CreateSphere(8)
   ScaleMesh(DebugCharacterCollider(CId,ii%),0.25,0.25,0.25)
   EntityAlpha(DebugCharacterCollider(CId,ii%),0.5)
  Next

  ;ColliderEs
  For EId% = 1 To 6
   VY# = 0+(EId*0.25)
   CharacterCollider(CId,EId) = CreatePivot()  
   PositionRotateEntityLikeOtherEntity(CharacterCollider(CId,EId),CharacterFeet(CId))
   MoveEntity(CharacterCollider(CId,EId),0,VY,0)
   SetEntityAsChildOfOtherEntity(CharacterCollider(CId,EId),CharacterFeet(CId))
   PositionRotateEntityLikeOtherEntity(DebugCharacterCollider(CId,EId),CharacterFeet(CId))
   MoveEntity(DebugCharacterCollider(CId,EId),0,VY,0)
   SetEntityAsChildOfOtherEntity(DebugCharacterCollider(CId,EId),CharacterFeet(CId))
   NameEntity(CharacterCollider(CId,EId),"CHA"+Str(CId))  
  Next

  ;Target
  CharacterTarget(CId) = CreatePivot()
  DebugCharacterTarget = CreateCylinder(8)
  ScaleMesh(DebugCharacterTarget,0.5/2,0.001/2,0.5/2)
  PositionRotateEntityLikeOtherEntity(DebugCharacterTarget,CharacterTarget(CId))
  SetEntityAsChildOfOtherEntity(DebugCharacterTarget,CharacterTarget(CId))
  EntityColor(DebugCharacterTarget,R,G,B)

  PositionEntity(CharacterFeet(CId),32.0/(12)*i,3.3+0.001,0.5)
  PositionEntityLikeOtherEntity(CharacterTarget(CId),CharacterFeet(CId))

 Next

End Function

Function Main()
 
 While(KeyDown(1)<>1)

  GetInputvBasic()

  If(KeyHit(3)>0)
   PlayerId = PlayerId + 1
   If(PlayerId > 10)
    PlayerId = 1
   EndIf
   SetEntityAsFree(Camera)
   PositionRotateEntityLikeOtherEntity(Camera,CharacterEyes(PlayerId))
   SetEntityAsChildOfOtherEntity(Camera,CharacterEyes(PlayerId))
   MoveEntity(Camera,0,0,-3)
   RotateEntity(CharacterEyes(PlayerId),22.5,0,0)
  EndIf

  UpdateColliders()

  UpdatePlayerBC()

  UpdateBotsBC()
 
  UpdateCollisionsStart% = MilliSecs()
  UpdateWorld(0)
  UpdateCollisionsTime% = MilliSecs() - UpdateCollisionsStart

  UpdatePlayerAC()

  UpdateBotsAC()

  If(KeyDown(2)=1)
   WireFrame(True)
  Else
   WireFrame(False)
  EndIf

  SetBuffer(BackBuffer())
  RenderWorld()
 
  Text(0,0,"Triangles = "+TrisRendered())
  Text(0,20,"FPS = "+Str(FPS))
  Text(0,40,"CollidedCollidable = "+Str(CollidedCollidable))
  Text(0,60,"CollidedName = "+CollidedName)
  Text(0,80,"CollidedKind = "+CollidedKind)
  Text(0,100,"CollidedId = "+Str(CollidedId))
  Text(0,260,"UpdateCollidersTime = "+Str(UpdateCollidersTime))
  Text(0,280,"UpdateCollisionsTime = "+Str(UpdateCollisionsTime))

  Flip(1)

 Wend

End Function

Function GetInputvBasic()

 ;MouseWheel
 MWheel = 0
 MZCur = MouseZ()
 If(MZCur > MZOld)
  MWheel = + 1
  MZOld = MZCur
  ;DebugLog("MWheel = "+MWheel)
 ElseIf(MZCur < MZOld)
  MWheel = - 1
  MZOld = MZCur
  ;DebugLog("MWheel = "+MWheel)
 EndIf

 ;Mouse picker properties
 MXDiff = MouseXSpeed()  
 MYDiff = MouseYSpeed() 
 MPX = MouseX()
 MPY = MouseY()

End Function

Function UpdateColliders()

 UpdateCollidersStart% = MilliSecs()

 ;Delete Colliders 
 For CId% = 1 To CharactersCount
  For EId% = 1 To 6
   SetEntityAsFree(CharacterCollider(CId,EId))
   SetEntityAsFree(DebugCharacterCollider(CId,EId))
   FreeEntity(CharacterCollider(CId,EId))
   CharacterCollider(CId,EId) = 0
  Next
 Next

 ;Recreate Colliders
 For CId% = 1 To CharactersCount
  ;ColliderEs
  For EId% = 1 To 6
   VY# = 0+(EId*0.25)
   CharacterCollider(CId,EId) = CreatePivot()  
   PositionRotateEntityLikeOtherEntity(CharacterCollider(CId,EId),CharacterFeet(CId))
   MoveEntity(CharacterCollider(CId,EId),0,VY,0)
   SetEntityAsChildOfOtherEntity(CharacterCollider(CId,EId),CharacterFeet(CId))
   PositionRotateEntityLikeOtherEntity(DebugCharacterCollider(CId,EId),CharacterFeet(CId))
   MoveEntity(DebugCharacterCollider(CId,EId),0,VY,0)
   SetEntityAsChildOfOtherEntity(DebugCharacterCollider(CId,EId),CharacterFeet(CId))
   NameEntity(CharacterCollider(CId,EId),"CHA"+Str(CId))  
  Next
 Next

 ;give each collider and each collidable a collision group
 EntityType(TerrainRenderer,GroupTerrainsR)
 For BId% = 1 To BuildingsCount
  EntityType(BuildingCollidable(BId),GroupBuildingsR)
 Next 
 For PId% = 1 To PlatformsCount
  EntityType(PlatformCollidable(PId),GroupPlatformsR)
 Next
 For CId% = 1 To CharactersCount
  For EId% = 1 To 6
   EntityRadius(CharacterCollider(CId,EId),0.25)
   EntityType(CharacterCollider(CId,EId),GroupCharactersER+CId)
   NameEntity(CharacterCollider(CId,EId),"CHA"+Str(CId))
  Next
 Next

 ClearCollisions()
 ;define collision detection and response between different collision groups
 For CId% = 1 To CharactersCount
  Collisions(GroupCharactersER+CId,GroupTerrainsR,2,1)
  Collisions(GroupCharactersER+CId,GroupBuildingsR,2,1)
  Collisions(GroupCharactersER+CId,GroupPlatformsR,2,1)
  For OCId% = 1 To CharactersCount
   If(OCId <> CId)
    Collisions(GroupCharactersER+CId,GroupCharactersER+OCId,1,1)
   EndIf
  Next
 Next

 UpdateCollidersTime = MilliSecs() - UpdateCollidersStart

End Function

Function UpdatePlayerBC()

 Id% = PlayerId
 If(KeyDown(30)=1)
  TurnEntity(CharacterFeet(Id),0,1.5,0)
 ElseIf(KeyDown(32)=1)
  TurnEntity(CharacterFeet(Id),0,-1.5,0)
 EndIf
 If(KeyDown(17)=1)
  MoveEntity(CharacterFeet(Id),0,0,0.1)
 ElseIf(KeyDown(31)=1)
  MoveEntity(CharacterFeet(Id),0,0,-0.1)
 EndIf
 If(KeyDown(16)=1)
  MoveEntity(CharacterFeet(Id),0,-0.1,0)
 ElseIf(KeyDown(18)=1)
  MoveEntity(CharacterFeet(Id),0,0.1,0)
 EndIf
 If(KeyDown(44)=1)
  MoveEntity(CharacterFeet(Id),-0.1,0,0)
 ElseIf(KeyDown(45)=1)
  MoveEntity(CharacterFeet(Id),0.1,0,0)
 EndIf

End Function

Function UpdatePlayerAC()

 Id% = PlayerId
 For EId% = 1 To 6
  CC% = CountCollisions(CharacterCollider(Id,EId))
  If(CC% > 0)
   CollidedCollidable = CollisionEntity(CharacterCollider(Id,EId),1)
   If(CollidedCollidable <> 0)
    TName$ = EntityName(CollidedCollidable)
    TKind$ = Left(TName,3)
    TId% = Right(TName,Len(TName)-3)
    VY# = 0+(EId*0.25)
    TFormPoint(0,-VY,0,CharacterCollider(Id,EId),0)
    PositionEntity(CharacterFeet(Id),TFormedX(),TFormedY(),TFormedZ())
    Goto LineEndCheckCollisions
   EndIf
  EndIf
 Next
 .LineEndCheckCollisions

End Function

Function UpdateBotsBC()

 For Id% = 1 To CharactersCount
  If(Id <> PlayerId)

  EndIf
 Next

End Function

Function UpdateBotsAC()

 For Id% = 1 To CharactersCount
  If(Id <> PlayerId)

  EndIf
 Next

End Function
