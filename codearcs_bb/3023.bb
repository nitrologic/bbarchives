; ID: 3023
; Author: RemiD
; Date: 2013-02-03 05:23:43
; Title: 4 viewports example
; Description: An example to have 4 viewports of the same scene, with 4 different views : 3D, Front, Side, Top

GWidth% = 800
GHeight% = 600
GColors% = 32
GMode% = 2
Graphics3D(GWidth,GHeight,GColors,GMode)

SeedRnd(MilliSecs())
HidePointer()

OrigineMesh = CreateCube()
ScaleMesh(OrigineMesh,0.01/2,1000.0/2,0.01/2)
EntityColor(OrigineMesh,255,000,000)

Global CameraFront = CreateCamera()
CameraViewport(CameraFront,0,0,GWidth/2,GHeight/2)
CameraProjMode(CameraFront,2)
PositionEntity(CameraFront,0,0,30)
RotateEntity(CameraFront,0,180,0)
CameraZoom(CameraFront,0.03333)

Global CameraLeft = CreateCamera()
CameraViewport(CameraLeft,GWidth/2,0,GWidth/2,GHeight/2)
CameraProjMode(CameraLeft,2)
PositionEntity(CameraLeft,-30,0,0)
RotateEntity(CameraLeft,0,-90,0)
CameraZoom(CameraLeft,0.03333)

Global CameraTop = CreateCamera()
CameraViewport(CameraTop,0,GHeight/2,GWidth/2,GHeight/2)
CameraProjMode(CameraTop,2)
PositionEntity(CameraTop,0,30,0)
RotateEntity(CameraTop,90,0,0)
CameraZoom(CameraTop,0.03333)

Global Camera3D = CreateCamera()
CameraViewport(Camera3D,GWidth/2,GHeight/2,GWidth/2,GHeight/2)
PositionEntity(Camera3D,30,30,-30)
RotateEntity(Camera3D,45,45,0)

GroundMesh = CreateCube()
ScaleMesh(GroundMesh,32.0/2,0.1/2,32.0/2)
PositionMesh(GroundMesh,0,-0.1/2,0)
EntityColor(GroundMesh,125,125,125)

ObjectsMaxCount% = 10
Global ObjectsCount% = 0
Dim ObjectMesh(ObjectsMaxCount)

For i% = 1 To 10
 ObjectsCount = ObjectsCount + 1
 OId% = ObjectsCount
 ObjectMesh(OId) = CreateCube()
 ScaleMesh(ObjectMesh(OId),1.0/2,1.0/2,1.0/2)
 PositionMesh(ObjectMesh(OId),0,1.0/2,0)
 EntityColor(ObjectMesh(OId),Rand(000,255),Rand(000,255),Rand(000,255))
 PositionEntity(ObjectMesh(OId),Rnd(-10,10),0,Rnd(-10,10))
 RotateEntity(ObjectMesh(OId),0,Rnd(-180,180),0)
Next

Repeat

 LogicUpdate()
 
 If(KeyDown(2)>0)
  Wireframe(True)
 Else
  Wireframe(False)
 EndIf

 SetBuffer(BackBuffer())
 RenderWorld()
 
 Color(255,255,255)
 Text(0,0,"Front view")
 Text(GWidth/2,0,"Left view")
 Text(0,GHeight/2,"Top view")
 Text(GWidth/2,GHeight/2,"3D view")

 Color(200,200,200)
 Line(0,GHeight/2-1,GWidth-1,GHeight/2-1)
 Line(0,GHeight/2,GWidth-1,GHeight/2)
 Line(GWidth/2-1,0,GWidth/2-1,GHeight-1)
 Line(GWidth/2,0,GWidth/2,GHeight-1)

 Flip(True)

Until(KeyDown(1)>0)

End

Function LogicUpdate()
 For OId% = 1 To ObjectsCount
  TurnEntity(ObjectMesh(OId),0,1,0)
  MoveEntity(ObjectMesh(OId),0,0,0.1)
 Next
End Function
