; ID: 3259
; Author: RemiD
; Date: 2016-02-17 05:22:09
; Title: rotatingdoor + trigger + animation (rotation)
; Description: This shows how to create a rotatingdoor and update its state and animation (rotation) depending on if a character is on the trigger or not

Graphics3D(1000,625,32,2)

SeedRnd(MilliSecs())

;PREMADE MESHES TEXTURES ANIMATIONS SOUNDS FONTS
Global XCharacter_Body = CreateCube()
ScaleMesh(XCharacter_Body,0.5/2,1.75/2,0.25/2)
PositionMesh(XCharacter_Body,0,1.75/2,0)
HideEntity(XCharacter_Body)

Global XCharacter_Collidable = CreateCylinder(16)
ScaleMesh(XCharacter_Collidable,0.5/2,1.75/2,0.5/2)
PositionMesh(XCharacter_Collidable,0,1.75/2,0)
HideEntity(XCharacter_Collidable)

Global XRotatingDoor_Wall = CreateMesh()
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,3.0/2,0.2/2)
PositionMesh(TPart,-1.0/2,3.0/2,0)
AddMesh(TPart,XRotatingDoor_Wall)
FreeEntity(TPart)
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,3.0/2,0.2/2)
PositionMesh(TPart,1.0/2+1.0,3.0/2,0)
AddMesh(TPart,XRotatingDoor_Wall)
FreeEntity(TPart)
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,1.0/2,0.2/2)
PositionMesh(TPart,1.0/2,1.0/2+2.0,0)
AddMesh(TPart,XRotatingDoor_Wall)
FreeEntity(TPart)
EntityColor(XRotatingDoor_Wall,200,200,200)
HideEntity(XRotatingDoor_Wall)

Global XRotatingDoor_Door = CreateMesh()
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,2.0/2,0.05/2)
PositionMesh(TPart,1.0/2,2.0/2.0,0)
AddMesh(TPart,XRotatingDoor_Door)
FreeEntity(TPart)
EntityColor(XRotatingDoor_Door,150,150,150)
HideEntity(XRotatingDoor_Door)

Global XRotatingDoor_TriggerA = CreateMesh()
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,0.1/2,1.0/2)
PositionMesh(TPart,1.0/2,0.1/2,-1.0/2)
AddMesh(TPart,XRotatingDoor_TriggerA)
FreeEntity(TPart)
EntityColor(XRotatingDoor_TriggerA,255,255,000)
HideEntity(XRotatingDoor_TriggerA)

Global XRotatingDoor_TriggerB = CreateMesh()
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,0.1/2,1.0/2)
PositionMesh(TPart,1.0/2,0.1/2,1.0/2)
AddMesh(TPart,XRotatingDoor_TriggerB)
FreeEntity(TPart)
EntityColor(XRotatingDoor_TriggerB,255,128,000)
HideEntity(XRotatingDoor_TriggerB)

Origine = CreateCube()
ScaleMesh(Origine,0.01/2,0.01/2,0.01/2)
EntityColor(Origine,255,000,255)
EntityFX(Origine,1)

Global Camera = CreateCamera()
CameraRange(Camera,0.1,100)
CameraClsColor(Camera,000,000,000)

SLight = CreateLight(1)
LightColor(SLight,224,224,224)
PositionEntity(SLight,50,1000,-1000,True)
RotateEntity(SLight,45,0,0,True)

AmbientLight(064,064,064)

Global TempsCount%
Dim TempI%(100)

Global Ground

BuildGround()

Global RotatingDoorsCount%
Dim RotatingDoor_Root(100)
Dim RotatingDoor_Wall(100)
Dim RotatingDoor_Door(100)
Dim RotatingDoor_TriggerA(100)
Dim RotatingDoor_TriggerB(100)
Dim RotatingDoor_TriggerN%(100)
Dim RotatingDoor_State%(100) ;IsOpening, IsOpened, IsClosing, IsClosed

Const CIsOpening% = 1
Const CIsOpen% = 2
Const CIsClosing% = 3
Const CIsClose% = 4

BuildRotatingDoors()

Global CharactersCount%
Dim Character_Root(10)
Dim Character_Eyes(10)
Dim Character_Body(10)
Dim Character_Collidable(10)
Dim Character_State%(10)

BuildCharacters()

Global PlayerI% = 1 ;Rand(1,10)

Global D2DMsTime%
Global MIMsTime%

Main()

End()

Function Main()
 Repeat

  If( KeyHit(57)=1 )
   PlayerI% = Rand(1,10)
  EndIf

  UpdatePlayer()
  UpdateAI()
  UpdateRotatingDoors()

  I% = PlayerI
  PositionRotateEntityLikeOtherEntity(Camera,Character_Eyes(I))
  TurnEntity(Camera,22.5,0,0)
  MoveEntity(Camera,0,0,-3)

  Wireframe(False)
  If( KeyDown(2)=1 )
   Wireframe(True)
  EndIf

  SetBuffer(BackBuffer())
  RenderWorld()

  CText("D2DMsTime = "+Str(D2DMsTime),0,0)
  CText("MIMsTime = "+Str(MIMsTime),0,15)

  Flip(1)

 Until( KeyDown(1)=1 )
End Function

Function CText(TextStr$,PX%,PY%)
 Text(PX,PY,TextStr,False,False)
End Function

Function PositionEntityLikeOtherEntity(Entity,OEntity)
 PositionEntity(Entity,EntityX(OEntity,True),EntityY(OEntity,True),EntityZ(OEntity,True),True)
End Function

Function RotateEntityLikeOtherEntity(Entity,OEntity)
 RotateEntity(Entity,EntityPitch(OEntity,True),EntityYaw(OEntity,True),EntityRoll(OEntity,True),True)
End Function

Function PositionRotateEntityLikeOtherEntity(Entity,OEntity)
 PositionEntity(Entity,EntityX(OEntity,True),EntityY(OEntity,True),EntityZ(OEntity,True),True)
 RotateEntity(Entity,EntityPitch(OEntity,True),EntityYaw(OEntity,True),EntityRoll(OEntity,True),True)
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

Function Diag#(SideLength#)
 Diag# = Sqr( (SideLength * SideLength) + (SideLength * SideLength) )
 Return Diag
End Function

Function BuildGround()
 Ground = CreateMesh()
 Surface = CreateSurface(Ground)
 AddVertex( Surface, 0.0, 0.0, 100.0 )
 AddVertex( Surface, 100.0, 0.0, 100.0 )
 AddVertex( Surface, 0.0, 0.0, 0.0 )
 AddVertex( Surface, 100.0, 0.0, 0.0 )
 AddTriangle( Surface, 0, 1, 2 )
 AddTriangle( Surface, 2, 1, 3 )
 EntityColor(Ground,128,128,128)
End Function

Function BuildRotatingDoors()
 TRoot = CreatePivot()
 LoopState = True
 Repeat
  ChooseRotatingDoorPositionTries% = 0
  .LineChooseRotatingDoorPosition
  ChooseRotatingDoorPositionTries = ChooseRotatingDoorPositionTries + 1
  ;choose the position of the temporary Rotatingdoor
  TX# = Rand(0+1.5,100.0-1.5)
  ;TY# = 0
  TZ# = Rand(0+1.5,100.0-1.5)
  ;.LineChooseRotatingDoorOrientation
  ;choose the orientation of a temporary Rotatingdoor
  ;TPitch# = 0
  TYaw# = Rnd(-180,180.0)
  ;TRoll# = 0
  ;check if there is enough empty space to create the temporary Rotatingdoor
  PositionEntity(TRoot,TX,0,TZ,True)
  IsThereEnoughEmptySpaceState% = True
  For OI% = 1 To RotatingDoorsCount Step 1
   D# = Distance2D(TX,TZ,EntityX(RotatingDoor_Root(OI),True),EntityZ(RotatingDoor_Root(OI),True))
   If( D >= 2+2+1.0 )
    ;
   Else
    IsThereEnoughEmptySpaceState = False
    Exit
   EndIf
  Next
  ;if yes
  If( IsThereEnoughEmptySpaceState = True )
   ;create the Rotatingdoor at this position with this orientation
   RotatingDoorsCount = RotatingDoorsCount + 1
   I% = RotatingDoorsCount
   RotatingDoor_Root(I) = CreatePivot()
   RotatingDoor_Wall(I) = CopyEntity(XRotatingDoor_Wall)
   EntityParent(RotatingDoor_Wall(I),RotatingDoor_Root(I),True)
   RotatingDoor_Door(I) = CopyEntity(XRotatingDoor_Door)
   MoveEntity(RotatingDoor_Door(I),0,0,0)
   EntityParent(RotatingDoor_Door(I),RotatingDoor_Root(I),True)
   RotatingDoor_TriggerA(I) = CopyEntity(XRotatingDoor_TriggerA)
   EntityParent(RotatingDoor_TriggerA(I),RotatingDoor_Root(I),True)
   EntityAlpha(RotatingDoor_TriggerA(I),0.25)
   RotatingDoor_TriggerB(I) = CopyEntity(XRotatingDoor_TriggerB)
   EntityParent(RotatingDoor_TriggerB(I),RotatingDoor_Root(I),True)
   EntityAlpha(RotatingDoor_TriggerB(I),0.25)
   RotatingDoor_State(I) = CIsClose
   PositionEntity(RotatingDoor_Root(I),TX,0,TZ,True)
   RotateEntity(RotatingDoor_Root(I),0,TYaw,0,True)
  ;else if no
  Else If( IsThereEnoughEmptySpaceState = False )
   ;if triescount inferior or equal to 100
   If( ChooseRotatingDoorPositionTries <= 100 )
    ;try to choose another position
    Goto LineChooseRotatingDoorPosition
   ;else if triescount superior to 100
   Else If( ChooseRotatingDoorPositionTries > 100 )
    ;exit
    LoopState = False
   EndIf
  EndIf 
 Until( RotatingDoorsCount% = 100 Or LoopState = False )
 FreeEntity(TRoot)
End Function

Function BuildCharacters()
 For n% = 1 To 10 Step 1
  CharactersCount = CharactersCount + 1
  I% = CharactersCount
  Character_Root(I) = CreatePivot()
  Character_Eyes(I) = CreatePivot()
  PositionEntity(Character_Eyes(I),0,1.65,0,True)
  EntityParent(Character_Eyes(I),Character_Root(I),True)
  Character_Body(I) = CopyEntity(XCharacter_Body)
  EntityColor(Character_Body(I),Rand(025,255),Rand(025,255),Rand(025,255))
  EntityParent(Character_Body(I),Character_Root(I),True)
  Character_Collidable(I) = CopyEntity(XCharacter_Collidable)
  EntityColor(Character_Collidable(I),255,255,255)
  EntityAlpha(Character_Collidable(I),0.5)
  EntityParent(Character_Collidable(I),Character_Root(I),True)
  PositionEntity(Character_Root(I),Rnd(0+0.25,100.0-0.25),0,Rnd(0+0.25,100.0-0.25),True)
  RotateEntity(Character_Root(I),0,0,0,True)
 Next
End Function

Function UpdatePlayer()
 I% = PlayerI
 If( KeyDown(30)=1 )
  TurnEntity(Character_Root(I),0,3,0)
 Else If( KeyDown(32)=1 )
  TurnEntity(Character_Root(I),0,-3,0)
 EndIf
 If( KeyDown(17)=1 )
  MoveEntity(Character_Root(I),0,0,0.05)
 Else If( KeyDown(31)=1 )
  MoveEntity(Character_Root(I),0,0,-0.05)
 EndIf
End Function

Function UpdateAI()
 For I% = 1 To CharactersCount Step 1
  If( I <> PlayerI )

  EndIf
 Next
End Function

Function UpdateRotatingDoors()
 For I% = 1 To RotatingDoorsCount Step 1

  ;check if some characters are near enough to be considered
  TriggerState% = False
  D2DMsStart% = MilliSecs()
  TempsCount = 0
  For OI% = 1 To CharactersCount Step 1
   D# = Distance2D(EntityX(RotatingDoor_Root(I),True),EntityZ(RotatingDoor_Root(I),True),EntityX(Character_Root(OI),True),EntityZ(Character_Root(OI),True))
   If( D <= Diag(1.0) + 0.25 + 1.0 )
    TempsCount = TempsCount + 1
    TI% = TempsCount
    TempI(TI) = OI
   EndIf
  Next
  D2DMsTime% = MilliSecs() - D2DMsStart
  ;if yes
  MIMsStart% = MilliSecs()
  If( TempsCount > 0 )
   ;check if one of the considered characters (near enough) intersects with the trigger
   For TI% = 1 To TempsCount Step 1
    OI% = TempI(TI)
    ;if yes
    If( MeshesIntersect(Character_Collidable(OI),RotatingDoor_TriggerA(I))=True )
     TriggerState = True
     If( RotatingDoor_State(I) = CIsClosing And RotatingDoor_TriggerN(I) = 2 )
      TriggerState = False
     EndIf
     If( RotatingDoor_State(I) = CIsClose )
      RotatingDoor_TriggerN(I) = 1
     Else
      ;
     EndIf
     Exit
    Else If( MeshesIntersect(Character_Collidable(OI),RotatingDoor_TriggerB(I))=True )
     TriggerState = True
     If( RotatingDoor_State(I) = CIsClosing And RotatingDoor_TriggerN(I) = 1 )
      TriggerState = False
     EndIf
     If( RotatingDoor_State(I) = CIsClose )
      RotatingDoor_TriggerN(I) = 2
     Else
      ;
     EndIf
     Exit
    EndIf
   Next
  EndIf
  MIMsTime% = MilliSecs() - MIMsStart

  ;depending on Rotatingdoor state, turnmove the Rotatingdoor
  If( RotatingDoor_State(I) = CIsOpening )
   If( TriggerState = False )
    RotatingDoor_State(I) = CIsOpening
   Else If( TriggerState = True )
    RotatingDoor_State(I) = CIsOpening
   EndIf
   Yaw# = EntityYaw(RotatingDoor_Door(I),False)
   If( RotatingDoor_TriggerN(I) = 1 )
    If( Yaw < 90 )
     TurnEntity(RotatingDoor_Door(I),0,1,0)
    Else If( Yaw >= 90 )
     RotatingDoor_State(I) = CIsOpen
    EndIf
   Else If( RotatingDoor_TriggerN(I) = 2 )
    If( Yaw > -90 )
     TurnEntity(RotatingDoor_Door(I),0,-1,0)
    Else If( Yaw <= -90 )
     RotatingDoor_State(I) = CIsOpen
    EndIf
   EndIf
  Else If( RotatingDoor_State(I) = CIsOpen )
   If( TriggerState = False )
    RotatingDoor_State(I) = CIsClosing
   Else If( TriggerState = True )
    RotatingDoor_State(I) = CIsOpen
   EndIf
  Else If( RotatingDoor_State(I) = CIsClosing )
   If( TriggerState = False )
    RotatingDoor_State(I) = CIsClosing
   Else If( TriggerState = True )
    RotatingDoor_State(I) = CIsOpening
   EndIf
   Yaw# = EntityYaw(RotatingDoor_Door(I),False)
   If( RotatingDoor_TriggerN(I) = 1 )
    If( Yaw > 0 )
     TurnEntity(RotatingDoor_Door(I),0,-1,0)
    Else If( Yaw <= 0 )
     RotatingDoor_State(I) = CIsClose
    EndIf
   Else If( RotatingDoor_TriggerN(I) = 2 )
    If( Yaw < 0 )
     TurnEntity(RotatingDoor_Door(I),0,1,0)
    Else If( Yaw >= 0 )
     RotatingDoor_State(I) = CIsClose
    EndIf
   EndIf
  Else If( RotatingDoor_State(I) = CIsClose )
   If( TriggerState = False )
    RotatingDoor_State(I) = CIsClose
   Else If( TriggerState = True )
    RotatingDoor_State(I) = CIsOpening
   EndIf
  EndIf

 Next
End Function
