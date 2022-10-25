; ID: 3288
; Author: RemiD
; Date: 2016-09-20 05:25:00
; Title: rainbow generator
; Description: generates a rainbow using a mesh (several arcs) and vertices colors

Graphics3D(1000,625,32,2)

SeedRnd(MilliSecs())

;Input
Global MX%
Global MY%
Global MXDiff%
Global MYDiff%

;Camera
Global Camera = CreateCamera()
CameraViewport(Camera,0,0,GraphicsWidth(),GraphicsHeight())
CameraRange(Camera,0.2,200)
CameraClsColor(Camera,000,000,000)

;Origine
Origine = CreateCube()
ScaleMesh(Origine,0.01,0.01,0.01)
EntityColor(Origine,255,000,255)
EntityFX(Origine,1)

;DLight
Global DLight = CreateLight(1)
LightColor(DLight,192,192,192)
PositionEntity(DLight,50,1000,-1000,True)
RotateEntity(DLight,45,0,0,True)

AmbientLight(032,032,032)

;InteractionMode
Global InteractionMode%
Const C2D% = 1
Const C3D% = 2
InitializeInteractionMode()

;Ghost
Global GhostRoot
Global GhostRootYaw#
Global GhostEyes
Global GhostEyesPitch#
BuildGhost()
InitializeGhost()

Terrain_ROAM = CreateTerrain(128)
ScaleEntity(Terrain_ROAM,1,25.5,1)
TerrainDetail(Terrain_ROAM,6553,True)
TerrainShading(Terrain_ROAM,True)
For VX% = 0 To 128-1
 For VZ% = 0 To 128-1
  HeightRGB% = Rand(030,033)
  Height# = 1.0/255*HeightRGB
  ModifyTerrain(Terrain_ROAM,VX,VZ,Height,False) 
 Next
Next
EntityColor(Terrain_ROAM,000,128,000)

Sky_Mesh = CreateSphere(8)
FlipMesh(Sky_Mesh)
ScaleMesh(Sky_Mesh,128.0,128.0,128.0)
;EntityColor(Sky_Mesh,092,092,255) ; blue sky
EntityColor(Sky_Mesh,128,128,128) ;grey sky
EntityFX(Sky_Mesh,1)
PositionEntity(Sky_Mesh,64,0,64)

;each section is made of 4 vertices 2 triangles
;each section is separated by 2 edges
;triangle A
;V0I = thisvertextop | V1I = nextvertextop | V2I = thisvertexbottom
;triangle B
;V0I = thisvertexbottom | V1I = nextvertextop | V2I = nextvertexbottom

;notes : 
;degrees = radians * 180 / PI
;radians = degrees * Pi / 180
;increment of 10degrees = 10.0*3.142/180.0 = 0.174radians
;increment of 0.1radians = 0.1*180.0/3.142 = 5.728degrees

Global ColorsCount%
Dim Color_R%(6)
Dim Color_G%(6)
Dim Color_B%(6)

;colors of a rainbow :
;magenta->red->yellow->green->cyan->blue->magenta
Color_R(1) = 000 : Color_G(1) = 000 : Color_B(1) = 255
Color_R(2) = 000 : Color_G(2) = 255 : Color_B(2) = 255
Color_R(3) = 000 : Color_G(3) = 255 : Color_B(3) = 000
Color_R(4) = 255 : Color_G(4) = 255 : Color_B(4) = 000
Color_R(5) = 255 : Color_G(5) = 000 : Color_B(5) = 000
Color_R(6) = 255 : Color_G(6) = 000 : Color_B(6) = 255

Global EdgesCCount%
Dim Edge_VertTopI%(19)
Dim Edge_VertBottomI%(19)

Rainbow_Mesh = CreateMesh()
CreateSurface(Rainbow_Mesh)

BottomR% = Color_R(6) : BottomG% = Color_G(6) : BottomB% = Color_B(6)
For n% = 1 To 6 Step 1

 TPart = CreateMesh()
 Surface = CreateSurface(TPart)

 For Angle# = -90.0 To +90.0 Step +10.0

  VertUpMarker = CreateCube()
  ScaleMesh(VertUpMarker,0.02/2,0.02/2,0.02/2)
  EntityColor(VertUpMarker,Rand(025,255),Rand(025,255),Rand(025,255))
  RotateEntity(VertUpMarker,0,0,Angle,True)
  MoveEntity(VertUpMarker,0,+30+n+0.5,0)
  AddVertex(Surface,EntityX(VertUpMarker,True),EntityY(VertUpMarker,True),EntityZ(VertUpMarker,True))
  FreeEntity(VertUpMarker)

  VertDownMarker = CreateCube()
  ScaleMesh(VertDownMarker,0.02/2,0.02/2,0.02/2)
  EntityColor(VertDownMarker,Rand(025,255),Rand(025,255),Rand(025,255))
  RotateEntity(VertDownMarker,0,0,Angle,True)
  MoveEntity(VertDownMarker,0,+30+n-0.5,0)
  AddVertex(Surface,EntityX(VertDownMarker,True),EntityY(VertDownMarker,True),EntityZ(VertDownMarker,True))
  FreeEntity(VertDownMarker)

 Next
 
 VI% = -1
 For EI% = 1 To 19 Step 1
  VI = VI + 1
  Edge_VertTopI(EI) = VI
  VI = VI + 1
  Edge_VertBottomI(EI) = VI
 Next

 For SI% = 1 To 18 Step 1
  VAI% = Edge_VertTopI(SI)
  VBI% = Edge_VertTopI(SI+1)
  VCI% = Edge_VertBottomI(SI)
  VDI% = Edge_VertBottomI(SI+1)
  AddTriangle(Surface,VAI,VBI,VCI)
  AddTriangle(Surface,VCI,VBI,VDI)
 Next

 TopR% = Color_R(n) : TopG% = Color_G(n) : TopB% = Color_B(n)
 If( n = 1 )
  For EI% = 1 To 19 Step 1
   VertexColor(Surface,Edge_VertTopI(EI),TopR,TopG,TopB,0.2)
   VertexColor(Surface,Edge_VertBottomI(EI),BottomR,BottomG,BottomB,0.0)
  Next
 Else If( n > 1 And n < 6 )
  For EI% = 1 To 19 Step 1
   VertexColor(Surface,Edge_VertTopI(EI),TopR,TopG,TopB,0.2)
   VertexColor(Surface,Edge_VertBottomI(EI),BottomR,BottomG,BottomB,0.2)
  Next
 Else If( n = 6 )
  For EI% = 1 To 19 Step 1
   VertexColor(Surface,Edge_VertTopI(EI),TopR,TopG,TopB,0.0)
   VertexColor(Surface,Edge_VertBottomI(EI),BottomR,BottomG,BottomB,0.2)
  Next
 EndIf
 BottomR% = TopR : BottomG% = TopG : BottomB% = TopB

 AddMesh(TPart,Rainbow_Mesh)
 FreeEntity(TPart)

Next

UpdateNormals(Rainbow_Mesh)

EntityFX(Rainbow_Mesh,1+2+16+32)
EntityBlend(Rainbow_Mesh,3) ;1 or 3

PositionEntity(Rainbow_Mesh,64,3.0,64,True)
RotateEntity(Rainbow_Mesh,0,180,0,True)

PositionEntity(GhostRoot,64,3.0+1.65,10,True)
GhostRootYaw = 0

Global MainLoopTimer = CreateTimer(30)

Main()

End()

Function Main()

 Repeat

  GetInput()

  UpdateInteractionMode()
  If( InteractionMode = C2D )
   ;
  Else If( InteractionMode = C3D )
   UpdateGhost() 	   
  EndIf

  PositionRotateEntityLikeOtherEntity(Camera,GhostEyes)

  WireFrame(False)
  If( KeyDown(2)=1 )
   WireFrame(True)
  EndIf

  SetBuffer(BackBuffer())
  RenderWorld()

  ;Flip(1)
  WaitTimer(MainLoopTimer)
  VWait():Flip(False)

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

Function InitializeInteractionMode()
 InteractionMode = C3D
 HidePointer()
End Function

Function UpdateInteractionMode() 

 If( KeyHit(15)=1 )
  If( InteractionMode = C2D )
   InteractionMode = C3D
   HidePointer()
  Else If( InteractionMode = C3D )
   InteractionMode = C2D
   ShowPointer()
  EndIf
 EndIf

End Function

Function BuildGhost()
 
 GhostRoot = CreatePivot()

 GhostEyes = CreatePivot()
 PositionRotateEntityLikeOtherEntity(GhostEyes,GhostRoot)
 EntityParent(GhostEyes,GhostRoot,True)

End Function

Function InitializeGhost()
 PositionEntity(GhostRoot,0,0,-3,True)
End Function

Function UpdateGhost()

 MoveMouse(GraphicsWidth()/2,GraphicsHeight()/2)
 GhostRootYaw = GhostRootYaw - Float(MXDiff)/10
 RotateEntity(GhostRoot,0,GhostRootYaw,0,False)
 GhostEyesPitch = GhostEyesPitch + Float(MYDiff)/10
 If( GhostEyesPitch < -89 )
  GhostEyesPitch = -89
 Else If( GhostEyesPitch > 89 )
  GhostEyesPitch = 89
 EndIf
 RotateEntity(GhostEyes,GhostEyesPitch,0,0,False)

 If( KeyDown(42) = 0 And KeyDown(29) = 0 )
  Speed# = 0.1
 Else If( KeyDown(42) = 1 And KeyDown(29) = 0 )
  Speed# = 1
 Else If( KeyDown(42) = 0 And KeyDown(29) = 1 )
  Speed# = 0.01
 EndIf

 If( KeyDown(17)=1 Or MouseDown(1)=1 )
  MoveEntity(GhostRoot,0,0,Speed)
 Else If( KeyDown(31)=1 Or MouseDown(2)=1 )
  MoveEntity(GhostRoot,0,0,-Speed)
 EndIf
 If( KeyDown(30)=1 )
  MoveEntity(GhostRoot,-Speed,0,0)
 Else If( KeyDown(32)=1 )
  MoveEntity(GhostRoot,Speed,0,0)
 EndIf
 If( KeyDown(16)=1 )
  MoveEntity(GhostRoot,0,-Speed,0)
 Else If( KeyDown(18)=1 )
  MoveEntity(GhostRoot,0,Speed,0)
 EndIf

End Function

Function GetInput()

 MX = MouseX()
 MY = MouseY()

 MXDiff = MouseXSpeed()
 MYDiff = MouseYSpeed()

End Function
