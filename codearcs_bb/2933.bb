; ID: 2933
; Author: RemiD
; Date: 2012-03-14 05:56:25
; Title: Basic character controls with FirstPerson view or ThirdPerson view
; Description: (forward, backward, left, right, jump, fall, land) + collisions Character->Environment

Graphics3D(800,600,32,2)  
HidePointer()  

SeedRnd(MilliSecs())  

Const CFirstPerson% = 1
Const CThirdPerson% = 2
Global ViewMode% = CFirstPerson

Const GroupEnvironment% = 1
Const GroupCharacters% = 2
 
Global Camera = CreateCamera()  
CameraRange(Camera,0.1,1000)
CameraClsColor(Camera,000,000,000)   

Origine = CreateCube()  
ScaleMesh(Origine,0.1/2,1000.0,0.1/2) 
EntityColor(Origine,255,000,000)
EntityFX(Origine,1) 

GroundMesh = CreateCube()
ScaleMesh(GroundMesh,100.0/2,0.1/2,100.0/2)
PositionMesh(GroundMesh,100.0/2,-0.1/2,100.0/2)
EntityColor(GroundMesh,000,125,000)
EntityType(GroundMesh,GroupEnvironment)

For i% = 1 To 100
 Width# = Rnd(0.5,5.0) 
 Depth# = Width*(Rnd(0.5,1.5))
 Height# = Width/2
 RockMesh = CreateSphere(8)
 ScaleMesh(RockMesh,Width/2,Height/2,Depth/2)
 RotateMesh(RockMesh,0,Rnd(-180,180),0)
 PositionMesh(RockMesh,Rnd(2.5,100.0-2.5),0,Rnd(2.5,100.0-2.5))
 EntityColor(RockMesh,050,050,050)
 EntityType(RockMesh,GroupEnvironment)
Next

For i% = 1 To 100
 CrateMesh = CreateCube()
 ScaleMesh(CrateMesh,1.0/2,1.0/2,1.0/2)
 PositionMesh(CrateMesh,0,1.0/2,0)
 RotateMesh(CrateMesh,0,Rnd(-180,180),0)
 PositionMesh(CrateMesh,Rnd(0.5,100.0-0.5),0,Rnd(0.5,100.0-0.5))
 EntityColor(CrateMesh,100,075,050)
 EntityType(CrateMesh,GroupEnvironment)
Next

PlayerCollider = CreatePivot()  
PositionEntity(PlayerCollider,0,0,0)  
EntityRadius(PlayerCollider,0.25)
EntityType(PlayerCollider,GroupCharacters)
  
PlayerColliderMarqueur = CreateSphere(16) ;for debug only
ScaleMesh(PlayerColliderMarqueur,0.5/2,0.5/2,0.5/2)
PositionEntity(PlayerColliderMarqueur,EntityX(PlayerCollider,True),EntityY(PlayerCollider,True),EntityZ(PlayerCollider,True))
EntityParent(PlayerColliderMarqueur,PlayerCollider,True)
EntityColor(PlayerColliderMarqueur,255,255,255)
EntityAlpha(PlayerColliderMarqueur,0.25)

PlayerEyes = CreatePivot()
PositionEntity(PlayerEyes,EntityX(PlayerCollider,True),EntityY(PlayerCollider,True)+1.6-0.25,EntityZ(PlayerCollider,True)+0.25)
EntityParent(PlayerEyes,PlayerCollider,True)

PlayerEyesMarqueur = CreateCube() ;for debug only
ScaleMesh(PlayerEyesMarqueur,0.025/2,0.025/2,0.025/2)
PositionEntity(PlayerEyesMarqueur,EntityX(PlayerEyes,True),EntityY(PlayerEyes,True),EntityZ(PlayerEyes,True))
EntityParent(PlayerEyesMarqueur,PlayerEyes,True)
EntityColor(PlayerEyesMarqueur,000,000,255)

PlayerMesh = CreateCube()  
ScaleMesh(PlayerMesh,0.5/2,1.7/2,0.25/2)  
PositionMesh(PlayerMesh,0,1.7/2,0)  
EntityColor(PlayerMesh,000,000,125)  
PositionEntity(PlayerMesh,EntityX(PlayerCollider,True),EntityY(PlayerCollider,True)-0.25,EntityZ(PlayerCollider,True))  
EntityParent(PlayerMesh,PlayerCollider,True)  
	
ViewMode = CFirstPerson

PositionEntity(PlayerCollider,5,0.25+0.01,5)

PlayerColliderYaw# = 0
PlayerEyesPitch# = 0
PlayerIsOnGround% = 0

PlayerVX# = 0
PlayerVZ# = 0
PlayerVY# = 0
PlayerOldX# = 0
PlayerOldZ# = 0
PlayerNewX# = 0
PlayerNewZ# = 0

DirectLight = CreateLight(1)  
LightColor(DirectLight,125,125,125)  
PositionEntity(DirectLight,0,128.0,-128.0)  
RotateEntity(DirectLight,45,0,0)  
AmbientLight(063,063,063)

Collisions(GroupCharacters,GroupEnvironment,2,3)

While( KeyDown(1)<>1 )   
 
 MXSpeed# = MouseXSpeed()  
 MYSpeed# = MouseYSpeed()   
 MoveMouse(GraphicsWidth()/2,GraphicsHeight()/2)  
 PlayerEyesPitch = PlayerEyesPitch+MYSpeed*0.1 
 If(PlayerEyesPitch > 89)
  PlayerEyesPitch = 89
 EndIf
 If(PlayerEyesPitch < -89)
  PlayerEyesPitch = -89
 EndIf
 PlayerColliderYaw = PlayerColliderYaw-MXSpeed*0.1   
 RotateEntity(PlayerEyes,PlayerEyesPitch,0,0)
 RotateEntity(PlayerCollider,0,PlayerColliderYaw,0)   
 
 CollidedCollidable% = EntityCollided(PlayerCollider,GroupEnvironment)
 If( CollidedCollidable = 0 )
  PlayerIsOnGround = False
 ElseIf( CollidedCollidable <> 0 )
  PlayerIsOnGround = True
 EndIf

 If( PlayerIsOnGround = False )
  PlayerVY = PlayerVY - 0.0025 
  TranslateEntity(PlayerCollider,PlayerVX,PlayerVY,PlayerVZ)  
  If(KeyDown(17)>0) 
   MoveEntity(PlayerCollider,0,0,0.01)  
  Else If(KeyDown(31)>0)  
   MoveEntity(PlayerCollider,0,0,-0.01)  
  EndIf  
  If(KeyDown(30)>0)  
   MoveEntity(PlayerCollider,-0.01,0,0)  
  Else If(KeyDown(32)>0)  
   MoveEntity(PlayerCollider,0.01,0,0)  
  EndIf
 ElseIf( PlayerIsOnGround = True )
  PlayerOldX = EntityX(PlayerCollider,True)
  PlayerOldZ = EntityZ(PlayerCollider,True)
  PlayerVY = 0  
  If(KeyDown(17)>0) 
   MoveEntity(PlayerCollider,0,0,0.1)  
  Else If(KeyDown(31)>0)  
   MoveEntity(PlayerCollider,0,0,-0.1)  
  EndIf  
  If(KeyDown(30)>0)  
   MoveEntity(PlayerCollider,-0.1,0,0)  
  Else If(KeyDown(32)>0)  
   MoveEntity(PlayerCollider,0.1,0,0)  
  EndIf  
  If(KeyDown(57)>0)
   PlayerVY# = 0.1
  EndIf 
  PlayerNewX = EntityX(PlayerCollider,True)
  PlayerNewZ = EntityZ(PlayerCollider,True)
  PlayerVX = PlayerNewX - PlayerOldX
  PlayerVZ = PlayerNewZ - PlayerOldZ  
 EndIf 

 UpdateWorld()

 If(KeyHit(15)>0)
  If( ViewMode = CFirstPerson )
   ViewMode = CThirdPerson
  ElseIf( ViewMode = CThirdPerson )
   ViewMode = CFirstPerson
  EndIf
 EndIf

 If( ViewMode = CFirstPerson )
  PositionEntity(Camera,EntityX(PlayerEyes,True),EntityY(PlayerEyes,True),EntityZ(PlayerEyes,True))
  RotateEntity(Camera,EntityPitch(PlayerEyes,True),EntityYaw(PlayerEyes,True),EntityRoll(PlayerEyes,True)) 
  MoveEntity(Camera,0,0,0.0)
 ElseIf( ViewMode = CThirdPerson )
  PositionEntity(Camera,EntityX(PlayerEyes,True),EntityY(PlayerEyes,True),EntityZ(PlayerEyes,True))
  RotateEntity(Camera,EntityPitch(PlayerEyes,True),EntityYaw(PlayerEyes,True),EntityRoll(PlayerEyes,True))
  MoveEntity(Camera,0,0,-3.0)
 EndIf

 If(KeyDown(2)>0)  
  WireFrame(True)  
 Else  
  WireFrame(False)  
 EndIf  

 SetBuffer(BackBuffer())
 RenderWorld()  
  
 Flip(1)  
  
Wend 

End()
