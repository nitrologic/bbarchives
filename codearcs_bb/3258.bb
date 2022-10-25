; ID: 3258
; Author: RemiD
; Date: 2016-02-17 05:19:26
; Title: slidingdoor + trigger + animation (movement)
; Description: This shows how to create a slidingdoor and update its state and animation (movement) depending on if a character is on the trigger or not

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

Global XSlidingDoor_Wall = CreateMesh()
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,3.0/2,0.01/2)
PositionMesh(TPart,-1.0/2,3.0/2,0)
AddMesh(TPart,XSlidingDoor_Wall)
FreeEntity(TPart)
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,3.0/2,0.01/2)
PositionMesh(TPart,1.0/2+1.0,3.0/2,0)
AddMesh(TPart,XSlidingDoor_Wall)
FreeEntity(TPart)
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,1.0/2,0.01/2)
PositionMesh(TPart,1.0/2,1.0/2+2.0,0)
AddMesh(TPart,XSlidingDoor_Wall)
FreeEntity(TPart)
EntityColor(XSlidingDoor_Wall,200,200,200)
HideEntity(XSlidingDoor_Wall)

Global XSlidingDoor_Door = CreateMesh()
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,2.0/2,0.01/2)
PositionMesh(TPart,1.0/2,2.0/2.0,0)
AddMesh(TPart,XSlidingDoor_Door)
FreeEntity(TPart)
EntityColor(XSlidingDoor_Door,150,150,150)
HideEntity(XSlidingDoor_Door)

Global XSlidingDoor_Trigger = CreateMesh()
TPart = CreateCube()
ScaleMesh(TPart,1.0/2,0.1/2,2.0/2)
PositionMesh(TPart,1.0/2,0.1/2,0)
AddMesh(TPart,XSlidingDoor_Trigger)
FreeEntity(TPart)
EntityColor(XSlidingDoor_Trigger,255,255,000)
HideEntity(XSlidingDoor_Trigger)

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

Global SlidingDoorsCount%
Dim SlidingDoor_Root(100)
Dim SlidingDoor_Wall(100)
Dim SlidingDoor_Door(100)
Dim SlidingDoor_Trigger(100)
Dim SlidingDoor_State%(100) ;IsOpening, IsOpened, IsClosing, IsClosed

Const CIsOpening% = 1
Const CIsOpen% = 2
Const CIsClosing% = 3
Const CIsClose% = 4

BuildSlidingDoors()

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
  UpdateSlidingDoors()

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

Function BuildSlidingDoors()
 TRoot = CreatePivot()
 LoopState = True
 Repeat
  ChooseSlidingDoorPositionTries% = 0
  .LineChooseSlidingDoorPosition
  ChooseSlidingDoorPositionTries = ChooseSlidingDoorPositionTries + 1
  ;choose the position of the temporary slidingdoor
  TX# = Rand(0+1.5,100.0-1.5)
  ;TY# = 0
  TZ# = Rand(0+1.5,100.0-1.5)
  ;.LineChooseSlidingDoorOrientation
  ;choose the orientation of a temporary slidingdoor
  ;TPitch# = 0
  TYaw# = Rnd(-180,180.0)
  ;TRoll# = 0
  ;check if there is enough empty space to create the temporary slidingdoor
  PositionEntity(TRoot,TX,0,TZ,True)
  IsThereEnoughEmptySpaceState% = True
  For OI% = 1 To SlidingDoorsCount Step 1
   D# = Distance2D(TX,TZ,EntityX(SlidingDoor_Root(OI),True),EntityZ(SlidingDoor_Root(OI),True))
   If( D >= 2+2+1.0 )
    ;
   Else
    IsThereEnoughEmptySpaceState = False
    Exit
   EndIf
  Next
  ;if yes
  If( IsThereEnoughEmptySpaceState = True )
   ;create the slidingdoor at this position with this orientation
   SlidingDoorsCount = SlidingDoorsCount + 1
   I% = SlidingDoorsCount
   SlidingDoor_Root(I) = CreatePivot()
   SlidingDoor_Wall(I) = CopyEntity(XSlidingDoor_Wall)
   EntityParent(SlidingDoor_Wall(I),SlidingDoor_Root(I),True)
   SlidingDoor_Door(I) = CopyEntity(XSlidingDoor_Door)
   MoveEntity(SlidingDoor_Door(I),0,0,-0.005-0.001)
   EntityParent(SlidingDoor_Door(I),SlidingDoor_Root(I),True)
   SlidingDoor_Trigger(I) = CopyEntity(XSlidingDoor_Trigger)
   EntityParent(SlidingDoor_Trigger(I),SlidingDoor_Root(I),True)
   EntityAlpha(SlidingDoor_Trigger(I),0.25)
   SlidingDoor_State(I) = CIsClose
   PositionEntity(SlidingDoor_Root(I),TX,0,TZ,True)
   RotateEntity(SlidingDoor_Root(I),0,TYaw,0,True)
  ;else if no
  Else If( IsThereEnoughEmptySpaceState = False )
   ;if triescount inferior or equal to 100
   If( ChooseSlidingDoorPositionTries <= 100 )
    ;try to choose another position
    Goto LineChooseSlidingDoorPosition
   ;else if triescount superior to 100
   Else If( ChooseSlidingDoorPositionTries > 100 )
    ;exit
    LoopState = False
   EndIf
  EndIf 
 Until( SlidingDoorsCount% = 100 Or LoopState = False )
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

Function UpdateSlidingDoors()
 For I% = 1 To SlidingDoorsCount Step 1

  ;check if some characters are near enough to be considered
  TriggerState% = False
  D2DMsStart% = MilliSecs()
  TempsCount = 0
  For OI% = 1 To CharactersCount Step 1
   D# = Distance2D(EntityX(SlidingDoor_Root(I),True),EntityZ(SlidingDoor_Root(I),True),EntityX(Character_Root(OI),True),EntityZ(Character_Root(OI),True))
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
    If( MeshesIntersect(Character_Collidable(OI),SlidingDoor_Trigger(I))=True )
     TriggerState = True
     Exit
    EndIf
   Next
  EndIf
  MIMsTime% = MilliSecs() - MIMsStart

   ;depending on slidingdoor state, turnmove the slidingdoor
  If( SlidingDoor_State(I) = CIsOpening )
   If( TriggerState = False )
    SlidingDoor_State(I) = CIsOpening
   Else If( TriggerState = True )
    SlidingDoor_State(I) = CIsOpening
   EndIf
   D# = Distance2D(EntityX(SlidingDoor_Door(I),True),EntityZ(SlidingDoor_Door(I),True),EntityX(SlidingDoor_Root(I),True),EntityZ(SlidingDoor_Root(I),True))
   If( D < 0.9 )
    MoveEntity(SlidingDoor_Door(I),-0.01,0,0)
   Else If( D >= 0.9 )
    SlidingDoor_State(I) = CIsOpen
   EndIf
  Else If( SlidingDoor_State(I) = CIsOpen )
   If( TriggerState = False )
    SlidingDoor_State(I) = CIsClosing
   Else If( TriggerState = True )
    SlidingDoor_State(I) = CIsOpen
   EndIf
  Else If( SlidingDoor_State(I) = CIsClosing )
   If( TriggerState = False )
    SlidingDoor_State(I) = CIsClosing
   Else If( TriggerState = True )
    SlidingDoor_State(I) = CIsOpening
   EndIf
   D# = Distance2D(EntityX(SlidingDoor_Door(I),True),EntityZ(SlidingDoor_Door(I),True),EntityX(SlidingDoor_Root(I),True),EntityZ(SlidingDoor_Root(I),True))
   If( D < 0.01 )
    SlidingDoor_State(I) = CIsClose
   Else If( D >= 0.01 )
    MoveEntity(SlidingDoor_Door(I),0.01,0,0)
   EndIf
  Else If( SlidingDoor_State(I) = CIsClose )
   If( TriggerState = False )
    SlidingDoor_State(I) = CIsClose
   Else If( TriggerState = True )
    SlidingDoor_State(I) = CIsOpening
   EndIf
  EndIf

 Next
End Function
