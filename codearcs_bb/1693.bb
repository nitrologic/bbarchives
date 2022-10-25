; ID: 1693
; Author: Devils Child
; Date: 2006-05-03 14:00:45
; Title: Under water distortion
; Description: a little demo that shows under water distorcion(very flexible and fast)!

Graphics3D 1024, 768, 32, 2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

;Globs(UDW = Under Water Distortion)
Global UWD_ProjPlane, UWD_Texture, UWD_Detail#, UWD_Sharpens#
Dim UWD_Vertex#(100, 100, 1)

;Camera
Cam = CreateCamera()
CameraClsColor Cam, 0, 0, 255
CameraFogMode Cam, True
CameraFogColor Cam, 0, 0, 255

;Light
RotateEntity CreateLight(), 45, 45, 0

;Some Cubes
For i = 1 To 100
	Cube = CreateCube()
	PositionEntity Cube, Rnd(-20, 20), Rnd(-20, 20), Rnd(10, 50)
	RotateEntity Cube, Rand(-180, 180), Rand(-180, 180), Rand(-180, 180)
	EntityColor Cube, Rand(0, 255), Rand(0, 255), Rand(0, 255)
Next

;Init
InitUnderWaterCam(1)

While Not KeyHit(1)
	UpdateUnderWaterCam(Cam)
	RenderWorld
	Flip
Wend
End

Function MipMapSize(x)
If x <= 16 Then Return 16
If x => 2048 Then Return 2048
If x => 1024 Then Return 1024
If x => 512 Then Return 512
If x => 256 Then Return 256
If x => 128 Then Return 128
If x => 64 Then Return 64
If x => 32 Then Return 32
If x => 16 Then Return 16
End Function

Function InitUnderWaterCam(Detail# = 1, sharpens# = 0)
If sharpens# = 0 Then sharpens# = MipMapSize(GraphicsHeight())
If Detail < 1 Or Detail > 20 Then RuntimeError "<Underwatercam> Detail must be between 1 and 20."
If sharpens# <> 16 And sharpens# <> 32 And sharpens# <> 64 And sharpens# <> 128 And sharpens# <> 256 And sharpens# <> 512 And sharpens# <> 1024 And sharpens# <> 2048 Then RuntimeError "<Underwatercam> Sharpens must be between 16 and 2048 and square(16, 32, 64, 128...)."
If sharpens# > GraphicsHeight() Then RuntimeError "<Underwatercam> Sharpens must not be bigger then the monitor height."
UWD_Sharpens# = sharpens#
UWD_Texture = CreateTexture(UWD_Sharpens#, UWD_Sharpens#)
UWD_ProjPlane = CreateMesh()
Surf = CreateSurface(UWD_ProjPlane)
For x = 0 To 4 * Detail
	For y = 0 To 3 * Detail
		UWD_Vertex(x, y, 0) = AddVertex(Surf, x, y, 0)
	Next
Next
For x = 0 To 4 * Detail
	For y = 0 To 3 * Detail
		AddTriangle Surf, UWD_Vertex(x, y, 0), UWD_Vertex(x, y + 1, 0), UWD_Vertex(x + 1, y, 0)
		AddTriangle Surf, UWD_Vertex(x + 1, y + 1, 0), UWD_Vertex(x + 1, y, 0), UWD_Vertex(x, y + 1, 0)
		UWD_Vertex(x, y, 1) = Rand(0, 360)
	Next
Next
PositionMesh UWD_ProjPlane, -2 * Detail, -1.5 * Detail, 0
ScaleMesh UWD_ProjPlane, 4.0 / Detail, -4.0 / Detail, 0
EntityTexture UWD_ProjPlane, UWD_Texture
EntityFX UWD_ProjPlane, 1
EntityOrder UWD_ProjPlane, -9999999999999999
UWD_Detail = Detail
End Function

Function UpdateUnderWaterCam(Cam)
For x = 0 To 4 * UWD_Detail
	For y = 0 To 3 * UWD_Detail
		UWD_Vertex(x, y, 1) = UWD_Vertex(x, y, 1) + 4
		VertexTexCoords GetSurface(UWD_ProjPlane, 1), UWD_Vertex(x, y, 0), x / 4.0 / UWD_Detail + Sin(UWD_Vertex(x, y, 1)) * .01 - .005, y / 4.0 / UWD_Detail + Cos(UWD_Vertex(x, y, 1)) * .01 - .005, 0
	Next
Next
PositionEntity UWD_ProjPlane, EntityX(Cam), EntityY(Cam), EntityZ(Cam)
RotateEntity UWD_ProjPlane, EntityPitch(Cam), EntityYaw(Cam), EntityRoll(Cam)
MoveEntity UWD_ProjPlane, 0, 0, 7.5
HideEntity UWD_ProjPlane
CameraViewport Cam, 0, 0, UWD_Sharpens#, UWD_Sharpens#
RenderWorld
CopyRect 0, 0, UWD_Sharpens#, UWD_Sharpens#, 0, 0, BackBuffer(), TextureBuffer(UWD_Texture)
ShowEntity UWD_ProjPlane
CameraViewport Cam, 0, 0, GraphicsWidth(), GraphicsHeight()
End Function
