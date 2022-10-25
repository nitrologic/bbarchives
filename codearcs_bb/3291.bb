; ID: 3291
; Author: RemiD
; Date: 2016-10-08 06:13:29
; Title: AddSurfaceToOtherSurface(Surface,Mesh,OSurface,OMesh)
; Description: merges a surface with another surface (alternative to addmesh)

;AddSurfaceToOtherSurface(Surface,Mesh,OSurface,OMesh)
;merges a surface with another surface (alternative to addmesh)
;
Graphics3D(1000,625,32,2)

SeedRnd(MilliSecs())

;Input
Global MX%
Global MY%
Global MXDiff%
Global MYDiff%

Global Camera = CreateCamera()
CameraViewport(Camera,0,0,GraphicsWidth(),GraphicsHeight())
CameraRange(Camera,0.1,100)
CameraClsColor(Camera,000,000,000)

Origine = CreateCube()
ScaleMesh(Origine,0.01,0.01,0.01)
EntityColor(Origine,255,000,255)
EntityFX(Origine,1)

;DLight
Global DLight = CreateLight(1)
LightColor(DLight,192,192,192)
PositionEntity(DLight,0,1000,-1000,True)
RotateEntity(DLight,45,0,0,True)

AmbientLight(019*2,019*2,019*2)

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

;XCube = LoadMesh("cube 201610011115.b3d")
;ScaleMesh(XCube,0.01,0.01,0.01)
XCube = CreateCube()
ScaleMesh(XCube,1.0/2,1.0/2,1.0/2)
HideEntity(XCube)
DebugLog("Cube has "+CountVertices(GetSurface(XCube,1))+"vertices and "+CountTriangles(GetSurface(XCube,1))+"triangles")

;XCylinder = LoadMesh("cylinder16 201610011126.b3d")
;ScaleMesh(XCylinder,0.01,0.01,0.01)
XCylinder = CreateCylinder(16)
ScaleMesh(XCylinder,1.0/2,1.0/2,1.0/2)
HideEntity(XCylinder)
DebugLog("Cylinder has "+CountVertices(GetSurface(XCylinder,1))+"vertices and "+CountTriangles(GetSurface(XCylinder,1))+"triangles")

;XSphere = LoadMesh("sphere16 201610011129.b3d")
;ScaleMesh(XSphere,0.01,0.01,0.01)
XSphere = CreateSphere(8)
ScaleMesh(XSphere,1.0/2,1.0/2,1.0/2)
HideEntity(XSphere)
DebugLog("Sphere has "+CountVertices(GetSurface(XSphere,1))+"vertices and "+CountTriangles(GetSurface(XSphere,1))+"triangles")

Global FinalShape = CreateMesh()
CreateSurface(FinalShape)
;add a cube to the finalshape
TPart = CopyMesh(XCube)
PositionMesh(TPart,0,0.5,0)
AddSurfaceToOtherSurface(GetSurface(TPart,1),TPart,GetSurface(FinalShape,1),FinalShape)
FreeEntity(TPart)
;add a cylinder to the finalshape
TPart = CopyMesh(XCylinder)
PositionMesh(TPart,0,1.0+0.5,0)
AddSurfaceToOtherSurface(GetSurface(TPart,1),TPart,GetSurface(FinalShape,1),FinalShape)
FreeEntity(TPart)
;add a sphere to the finalshape
TPart = CopyMesh(XSphere)
PositionMesh(TPart,0,1.0+1.0+0.5,0)
AddSurfaceToOtherSurface(GetSurface(TPart,1),TPart,GetSurface(FinalShape,1),FinalShape)
FreeEntity(TPart)
DebugLog("FinalShape has "+CountVertices(GetSurface(FinalShape,1))+"vertices and "+CountTriangles(GetSurface(FinalShape,1))+"triangles")

;ColorSurfaceWithBrush(GetSurface(FinalShape,1),Rand(025,255),Rand(025,255),Rand(025,255),1.0)
EntityColor(FinalShape,Rand(025,255),Rand(025,255),Rand(025,255))
EntityAlpha(FinalShape,1.0)
EntityFX(FinalShape,0)
EntityBlend(FinalShape,1)

PositionEntity(GhostRoot,0,1.65,-5,True)

Global MainLoopTimer = CreateTimer(30)

Main()

End()

Function Main()

 Repeat

  MainLoopMsStart% = MilliSecs()

  GetInput()

  UpdateInteractionMode()
  If( InteractionMode = C2D )
   ;
  Else If( InteractionMode = C3D )
   UpdateGhost() 	   
  EndIf

  TurnEntity(FinalShape,0,1.0,0)

  PositionRotateEntityLikeOtherEntity(Camera,GhostEyes)

  WireFrame(False)
  If( KeyDown(2)=1 )
   WireFrame(True)
  EndIf

  SetBuffer(BackBuffer())
  Render3dMsStart% = MilliSecs()
  RenderWorld()
  Render3dMsTime% = MilliSecs()- Render3dMsStart

  Color(255,255,255)
  CText("FPS = "+FPS,0,0)
  CText("Render3dMsTime = "+Render3dMsTime,0,15)
  CText("Tris = "+TrisRendered(),0,30) 

  ;Flip(1)
  WaitTimer(MainLoopTimer)
  VWait():Flip(False)

  MainLoopMsTime = MilliSecs() - MainLoopMsStart
  If( MainLoopMsTime <= 1 )
   MainLoopMsTime = 1
  EndIf

  FPS% = 1000.0/MainLoopMsTime

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

;vertices+triangles (with normals, with colors, with alphas, with uvs)
Function AddSurfaceToOtherSurface(Surface,Mesh,OSurface,OMesh)
 SurfaceVerticesCount% = CountVertices(Surface)
 ;DebugLog("SurfaceVerticesCount = "+SurfaceVerticesCount)
 OSurfaceVerticesCount% = CountVertices(OSurface)
 ;DebugLog("OSurfaceVerticesCount = "+OSurfaceVerticesCount)
 For VI% = 0 To CountVertices(Surface)-1 Step 1
  ;DebugLog("VI = "+VI)
  VLX# = VertexX(Surface,VI)
  VLY# = VertexY(Surface,VI)
  VLZ# = VertexZ(Surface,VI)
  TFormPoint(VLX,VLY,VLZ,Mesh,0)
  VGX# = TFormedX()
  VGY# = TFormedY()
  VGZ# = TFormedZ()
  VNX# = VertexNX(Surface,VI)
  VNY# = VertexNY(Surface,VI)
  VNZ# = VertexNZ(Surface,VI)
  VR% = VertexRed(Surface,VI)
  VG% = VertexGreen(Surface,VI)
  VB% = VertexBlue(Surface,VI)
  VA# = VertexAlpha(Surface,VI)
  VU# = VertexU(Surface,VI,0) 
  VV# = VertexV(Surface,VI,0)
  If( OSurfaceVerticesCount = 0 )
   NVI = VI
  Else If( OSurfaceVerticesCount > 0 )
   NVI% = OSurfaceVerticesCount+VI
  EndIf
  ;DebugLog("NVI = "+NVI)
  AddVertex(OSurface,VGX,VGY,VGZ)
  VertexNormal(OSurface,NVI,VNX,VNY,VNZ)
  VertexColor(OSurface,NVI,VR,VG,VB,VA)
  VertexTexCoords(OSurface,NVI,VU,VB)
  ;WaitKey()
 Next
 SurfaceTrianglesCount% = CountTriangles(Surface)
 ;DebugLog("SurfaceTrianglesCount = "+SurfaceTrianglesCount)
 OSurfaceTrianglesCount% = CountTriangles(OSurface)
 ;DebugLog("OSurfaceTrianglesCount = "+OSurfaceTrianglesCount)
 For TI% = 0 To CountTriangles(Surface)-1 Step 1
  V0I% = TriangleVertex(Surface,TI,0) ;vertex0
  V1I% = TriangleVertex(Surface,TI,1) ;vertex1
  V2I% = TriangleVertex(Surface,TI,2) ;vertex2
  ;DebugLog("oldtriangle"+TI+" "+V0I+","+V1I+","+V2I)
  If( OSurfaceVerticesCount = 0 ) 
   NV0I% = V0I
   NV1I% = V1I
   NV2I% = V2I
  Else If( OSurfaceVerticesCount > 0 )
   NV0I% = OSurfaceVerticesCount+V0I
   NV1I% = OSurfaceVerticesCount+V1I
   NV2I% = OSurfaceVerticesCount+V2I
  EndIf
  ;DebugLog("newtriangle"+TI+" "+NV0I+","+NV1I+","+NV2I)
  AddTriangle(OSurface,NV0I,NV1I,NV2I)
 Next
 ;WaitKey()
End Function

Function ColorSurfaceWithBrush(Surface,R%,G%,B%,A#=1.0,Fx%=0,BlendMode%=1)
 Brush = CreateBrush()
 BrushColor(Brush,R,G,B)
 BrushAlpha(Brush,A)
 BrushFX(Brush,Fx)
 BrushBlend(Brush,BlendMode)
 PaintSurface(Surface,Brush)
 FreeBrush(Brush)
End Function
