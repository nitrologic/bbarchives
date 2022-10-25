; ID: 3286
; Author: RemiD
; Date: 2016-09-04 07:48:45
; Title: color of shaded triangles depending on ambientlight color and thing color
; Description: this allows you to calculate the color of the shaded (not lighted) triangles depending on the color of the ambient light and the color of the thing

Graphics3D(1000,625,32,2)

SeedRnd(MilliSecs())

;Camera
Global Camera = CreateCamera()
CameraViewport(Camera,0,0,GraphicsWidth(),GraphicsHeight())
CameraRange(Camera,0.1,100)
CameraClsColor(Camera,000,000,000)

;Origine
Origine = CreateCube()
ScaleMesh(Origine,0.01,0.01,0.01)
EntityColor(Origine,255,000,255)
EntityFX(Origine,1)

;Center
Global Center = CreatePivot()
PositionEntity(Center,50,0,50,True)

;DLight
Global DLight = CreateLight(1)
LightColor(DLight,192,192,192)
PositionEntity(DLight,50,1000,-1000,True)
RotateEntity(DLight,45,0,0,True)

AmbientLight(038,038,038)

;Cube lighted/shaded normally (with directx7 lighting/shading)
Global CubeRenderer = CreateCube()
ScaleMesh(CubeRenderer,1.0/2,1.0/2,1.0/2)
PositionMesh(CubeRenderer,0,1.0/2,0)
R% = Rand(025,255) : G% = Rand(025,255) : B% = Rand(025,255)
EntityColor(CubeRenderer,R,G,B)
PositionEntity(CubeRenderer,0,0,0,True)

;Quad fullbright and has the same color than the shaded (not lighted) triangles of the cube
Global QuadRenderer = CreateMesh()
Surface = CreateSurface(QuadRenderer)
AddVertex(Surface, 0.5, 0.5, 0.0)
AddVertex(Surface, -0.5, 0.5, 0.0)
AddVertex(Surface, 0.5, -0.5, 0.0)
AddVertex(Surface, -0.5, -0.5, 0.0)
AddTriangle(Surface,0,1,2)
AddTriangle(Surface,2,1,3)
UpdateNormals(QuadRenderer)
EntityColor(QuadRenderer,038.0/255*R,038.0/255*G,038.0/255*B) ;this is the secret formula ! (AmbientR/255*ThingR,AmbientG/255*ThingG,AmbientB/255*ThingB)
EntityFX(QuadRenderer,1)
PositionEntity(QuadRenderer,0,1.5,0,True)

PositionEntity(Camera,3,1.65,3,True)
RotateEntity(Camera,0,135,0,True)

Global MainLoopTimer = CreateTimer(30)

Main()

End()

Function Main()

 Repeat

  If( KeyHit(57)=1 )
   R% = Rand(025,255) : G% = Rand(025,255) : B% = Rand(025,255)
   EntityColor(CubeRenderer,R,G,B)
   EntityColor(QuadRenderer,038.0/255*R,038.0/255*G,038.0/255*B)
  EndIf

  If( KeyHit(28)=1 )
   PositionEntity(DLight,Rnd(-1000,1000),Rnd(0,1000),-1000,True)
   PointEntity(DLight,Center)
  EndIf

  WireFrame(False)
  If( KeyDown(2)=1 )
   WireFrame(True)
  EndIf

  SetBuffer(BackBuffer())
  RenderWorld()

  Color(255,255,255)
  Text(0,0,"press space to change the color of the thing")
  Text(0,15,"press enter to change the light position")

  Text(0,30,"the cube is lighted/shaded normally (with directx7 lighting/shading)")
  Text(0,45,"the quad is fullbright and has the same color than the shaded (not lighted) triangles of the cube")

  ;Flip(1)
  WaitTimer(MainLoopTimer)
  VWait():Flip(False)

 Until( KeyDown(1)=1 )

End Function
