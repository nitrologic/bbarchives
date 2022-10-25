; ID: 3201
; Author: RemiD
; Date: 2015-04-27 03:11:56
; Title: Light a scene with DirectX7 vertex lighting or with LightMesh vertex lighting
; Description: Demonstrate how to use LightMesh to have the same lighting than with a DirectX7 olight

;Light a scene with DirectX7 vertex lighting or with LightMesh vertex lighting
Graphics3D(800,600,32,2)

SeedRnd(MilliSecs())

Global Arial14Font = LoadFont("Arial",14,False,False,False)

Camera = CreateCamera() 
CameraRange(Camera,0.1,100)
CameraClsColor(Camera,000,000,000)

TTile = CreateMesh()
TSurface = CreateSurface(TTile)
AddVertex(TSurface, 0.0, 0.0, 1.0)
AddVertex(TSurface, 1.0, 0.0, 1.0)
AddVertex(TSurface, 0.0, 0.0, 0.0)
AddVertex(TSurface, 1.0, 0.0, 0.0)
AddTriangle(TSurface, 0, 1, 2)
AddTriangle(TSurface, 2, 1, 3)
UpdateNormals(TTile)
FloorMesh = CreateMesh()
For GX# = 0 To 10-1 Step 1
 For GZ# = 0 To 10-1 Step 1
  GY# = 0
  TPart = CopyMesh(TTile)
  PositionMesh(TPart,GX,GY,GZ)
  AddMesh(TPart,FloorMesh)
  FreeEntity(TPart)
 Next
Next
FreeEntity(TTile)

TTile = CreateMesh()
TSurface = CreateSurface(TTile)
AddVertex(TSurface, 0.0, 1.0, 0.0)
AddVertex(TSurface, 1.0, 1.0, 0.0)
AddVertex(TSurface, 0.0, 0.0, 0.0)
AddVertex(TSurface, 1.0, 0.0, 0.0)
AddTriangle(TSurface, 0, 1, 2)
AddTriangle(TSurface, 2, 1, 3)
UpdateNormals(TTile)
WallFrontMesh = CreateMesh()
For GX# = 0 To 10-1 Step 1
 For GY# = 0 To 3-1 Step 1
  GZ# = 10
  TPart = CopyMesh(TTile)
  PositionMesh(TPart,GX,GY,GZ)
  AddMesh(TPart,WallFrontMesh)
  FreeEntity(TPart)
 Next
Next
FreeEntity(TTile)

TTile = CreateMesh()
TSurface = CreateSurface(TTile)
AddVertex(TSurface, 1.0, 1.0, 0.0)
AddVertex(TSurface, 0.0, 1.0, 0.0)
AddVertex(TSurface, 1.0, 0.0, 0.0)
AddVertex(TSurface, 0.0, 0.0, 0.0)
AddTriangle(TSurface, 0, 1, 2)
AddTriangle(TSurface, 2, 1, 3)
UpdateNormals(TTile)
WallBackMesh = CreateMesh()
For GX# = 0 To 10-1 Step 1
 For GY# = 0 To 3-1 Step 1
  GZ# = 0
  TPart = CopyMesh(TTile)
  PositionMesh(TPart,GX,GY,GZ)
  AddMesh(TPart,WallBackMesh)
  FreeEntity(TPart)
 Next
Next
FreeEntity(TTile)

TTile = CreateMesh()
TSurface = CreateSurface(TTile)
AddVertex(TSurface, 0.0, 1.0, 0.0)
AddVertex(TSurface, 0.0, 1.0, 1.0)
AddVertex(TSurface, 0.0, 0.0, 0.0)
AddVertex(TSurface, 0.0, 0.0, 1.0)
AddTriangle(TSurface, 0, 1, 2)
AddTriangle(TSurface, 2, 1, 3)
UpdateNormals(TTile)
WallLeftMesh = CreateMesh()
GX# = 0
For GZ# = 0 To 10-1 Step 1
 For GY# = 0 To 3-1 Step 1
  TPart = CopyMesh(TTile)
  PositionMesh(TPart,GX,GY,GZ)
  AddMesh(TPart,WallLeftMesh)
  FreeEntity(TPart)
 Next
Next
FreeEntity(TTile)

TTile = CreateMesh()
TSurface = CreateSurface(TTile)
AddVertex(TSurface, 0.0, 1.0, 1.0)
AddVertex(TSurface, 0.0, 1.0, 0.0)
AddVertex(TSurface, 0.0, 0.0, 1.0)
AddVertex(TSurface, 0.0, 0.0, 0.0)
AddTriangle(TSurface, 0, 1, 2)
AddTriangle(TSurface, 2, 1, 3)
UpdateNormals(TTile)
WallRightMesh = CreateMesh()
GX# = 10
For GZ# = 0 To 10-1 Step 1
 For GY# = 0 To 3-1 Step 1
  TPart = CopyMesh(TTile)
  PositionMesh(TPart,GX,GY,GZ)
  AddMesh(TPart,WallRightMesh)
  FreeEntity(TPart)
 Next
Next
FreeEntity(TTile)

TTile = CreateMesh()
TSurface = CreateSurface(TTile)
AddVertex(TSurface, 0.0, 0.0, 0.0)
AddVertex(TSurface, 1.0, 0.0, 0.0)
AddVertex(TSurface, 0.0, 0.0, 1.0)
AddVertex(TSurface, 1.0, 0.0, 1.0)
AddTriangle(TSurface, 0, 1, 2)
AddTriangle(TSurface, 2, 1, 3)
UpdateNormals(TTile)
CeilingMesh = CreateMesh()
For GX# = 0 To 10-1 Step 1
 For GZ# = 0 To 10-1 Step 1
  GY# = 3
  TPart = CopyMesh(TTile)
  PositionMesh(TPart,GX,GY,GZ)
  AddMesh(TPart,CeilingMesh)
  FreeEntity(TPart)
 Next
Next
FreeEntity(TTile)

Global ThingsCount% 
Dim ThingMesh(10)

For i% = 1 To 10 Step 1
 ThingsCount = ThingsCount + 1
 Id% = ThingsCount
 ThingMesh(Id) = CreateCube()
 ScaleMesh(ThingMesh(Id),1.0/2,1.0/2,1.0/2)
 PositionMesh(ThingMesh(Id),0,1.0/2,0)
 PositionEntity(ThingMesh(Id),Rnd(0+0.5,10-0.5),0,Rnd(0+0.5,10-0.5),True)
Next

LightIntensity# = 4.242
LightR% = Rand(025,255)
LightG% = Rand(025,255)
LightB% = Rand(025,255)
LightSource=CreateSphere(8)
ScaleMesh(LightSource,0.05/2,0.05/2,0.05/2)
EntityColor(LightSource,LightR,LightG,LightB)
EntityFX(LightSource,1)
PositionEntity(LightSource,Rnd(1,10-1),Rand(1.0,2.0),Rnd(1,10-1),True)

DX7Omni = CreateLight(2)
LightRange(DX7Omni,LightIntensity)
LightColor(DX7Omni,LightR,LightG,LightB) ;olight color
PositionEntity(DX7Omni,EntityX(LightSource,True),EntityY(LightSource,True),EntityZ(LightSource,True),True)
EntityParent(DX7Omni,LightSource,True)

Rotor = CreatePivot()
PositionEntity(Rotor,5,0,5,True)
EntityParent(LightSource,Rotor,True)

AmbientLight(015,015,015) ;ambientlight color

PositionEntity(Camera,5,1.65,-2.5)

Global LightingMode%
Const DX7VL% = 1
Const LMVL% = 2

While( KeyDown(1) <> 1 )

 TurnEntity(Rotor,0,1,0)

 If( KeyDown(30)=1 )
  TurnEntity(Camera,0,1,0)
 Else If( KeyDown(32)=1)
  TurnEntity(Camera,0,-1,0)
 EndIf
 If( KeyDown(17)=1 )
  MoveEntity(Camera,0,0,0.1)
 Else If( KeyDown(31)=1 )
  MoveEntity(Camera,0,0,-0.1)
 EndIf

 LightingMode = 0
 LMmsTime% = 0

 ;reset vertex colors
 EntityFX(FloorMesh,0)
 For VId% = 0 To CountVertices(GetSurface(FloorMesh,1))-1 Step 1
  VertexColor(GetSurface(FloorMesh,1),VId,255,255,255,1.0)
 Next
 EntityFX(WallFrontMesh,0)
 For VId% = 0 To CountVertices(GetSurface(WallFrontMesh,1))-1 Step 1
  VertexColor(GetSurface(WallFrontMesh,1),VId,255,255,255,1.0)
 Next
 EntityFX(WallBackMesh,0)
 For VId% = 0 To CountVertices(GetSurface(WallBackMesh,1))-1 Step 1
  VertexColor(GetSurface(WallBackMesh,1),VId,255,255,255,1.0)
 Next
 EntityFX(WallLeftMesh,0)
 For VId% = 0 To CountVertices(GetSurface(WallLeftMesh,1))-1 Step 1
  VertexColor(GetSurface(WallLeftMesh,1),VId,255,255,255,1.0)
 Next
 EntityFX(WallRightMesh,0)
 For VId% = 0 To CountVertices(GetSurface(WallRightMesh,1))-1 Step 1
  VertexColor(GetSurface(WallRightMesh,1),VId,255,255,255,1.0)
 Next
 EntityFX(CeilingMesh,0)
 For VId% = 0 To CountVertices(GetSurface(CeilingMesh,1))-1 Step 1
  VertexColor(GetSurface(CeilingMesh,1),VId,255,255,255,1.0)
 Next
 For Id% = 1 To ThingsCount Step 1
  EntityFX(ThingMesh(Id),0)
  For VId% = 0 To CountVertices(GetSurface(ThingMesh(Id),1))-1 Step 1
   VertexColor(GetSurface(ThingMesh(Id),1),VId,255,255,255,1.0)
  Next
 Next

 HideEntity(DX7Omni)
 If( MouseDown(1)=1 )

  ;light with the DX7 vertex lighting
  LightingMode = DX7VL
  ShowEntity(DX7Omni)

 Else If(MouseDown(2)= 1 )

  ;light with lightmesh vertex lighting
  LightingMode = LMVL
  EntityFX(FloorMesh,1+2)
  EntityFX(WallFrontMesh,1+2)
  EntityFX(WallBackMesh,1+2)
  EntityFX(WallLeftMesh,1+2)
  EntityFX(WallRightMesh,1+2)
  EntityFX(CeilingMesh,1+2)
  For Id% = 1 To ThingsCount Step 1
   EntityFX(ThingMesh(Id),1+2)
  Next

  LMmsStart% = MilliSecs()

  LightMesh(FloorMesh,-255,-255,-255)
  LightMesh(FloorMesh,015,015,015) ;ambientlight color 
  TFormPoint(0,0,0,LightSource,FloorMesh)
  LightMesh(FloorMesh,LightR,LightG,LightB,LightIntensity,TFormedX(),TFormedY(),TFormedZ()) ;olight color
  
  LightMesh(WallFrontMesh,-255,-255,-255)
  LightMesh(WallFrontMesh,015,015,015) ;ambientlight color
  TFormPoint(0,0,0,LightSource,WallFrontMesh)
  LightMesh(WallFrontMesh,LightR,LightG,LightB,LightIntensity,TFormedX(),TFormedY(),TFormedZ()) ;olight color

  LightMesh(WallBackMesh,-255,-255,-255)
  LightMesh(WallBackMesh,015,015,015) ;ambientlight color
  TFormPoint(0,0,0,LightSource,WallBackMesh)
  LightMesh(WallBackMesh,LightR,LightG,LightB,LightIntensity,TFormedX(),TFormedY(),TFormedZ()) ;olight color

  LightMesh(WallLeftMesh,-255,-255,-255)
  LightMesh(WallLeftMesh,015,015,015) ;ambientlight color
  TFormPoint(0,0,0,LightSource,WallLeftMesh)
  LightMesh(WallLeftMesh,LightR,LightG,LightB,LightIntensity,TFormedX(),TFormedY(),TFormedZ()) ;olight color

  LightMesh(WallRightMesh,-255,-255,-255)
  LightMesh(WallRightMesh,015,015,015) ;ambientlight color
  TFormPoint(0,0,0,LightSource,WallRightMesh)
  LightMesh(WallRightMesh,LightR,LightG,LightB,LightIntensity,TFormedX(),TFormedY(),TFormedZ()) ;olight color

  LightMesh(CeilingMesh,-255,-255,-255)
  LightMesh(CeilingMesh,015,015,015) ;ambientlight color
  TFormPoint(0,0,0,LightSource,CeilingMesh)
  LightMesh(CeilingMesh,LightR,LightG,LightB,LightIntensity,TFormedX(),TFormedY(),TFormedZ()) ;olight color

  For Id% = 1 To ThingsCount Step 1
   LightMesh(ThingMesh(Id),-255,-255,-255) 
   LightMesh(ThingMesh(Id),015,015,015) ;ambientlight color
   TFormPoint(0,0,0,LightSource,ThingMesh(Id))
   LightMesh(ThingMesh(Id),LightR,LightG,LightB,LightIntensity,TFormedX(),TFormedY(),TFormedZ()) ;olight color
  Next

  LMmsTime% = MilliSecs() - LMmsStart

 EndIf

 If( KeyDown(2)=1 )
  WireFrame(True)
 Else
  WireFrame(False)
 EndIf

 SetBuffer(BackBuffer())
 RenderWorld()

 SetFont(Arial14Font)
 Color(255,255,255)
 Text(0,0,"Hold mouse1 to light the scene with DirectX7 vertex lighting, hold mouse2 to light the scene with LightMesh vertex lighting")
 Text(0,14,"LightingMode = "+LightingModeStr(LightingMode))
 If( LightingMode = LMVL )
  Text(0,28,"LMmsTime = "+LMmsTime)
 EndIf

 Flip(1)

Wend 

ClearWorld()

End()

Function LightingModeStr$(LightingMode%)
 If( LightingMode = DX7VL )
  Return "DirectX7 vertex lighting"
 Else If( LightingMode = LMVL )
  Return "LightMesh vertex lighting"
 EndIf
End Function
