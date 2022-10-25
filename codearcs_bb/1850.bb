; ID: 1850
; Author: Devils Child
; Date: 2006-10-24 23:21:30
; Title: Model Viewer
; Description: Views b3d/x/3ds

file$ = CommandLine()
file$ = Replace(file$, Chr(34), "")
file$ = Trim(file$)
If file$ = "" RuntimeError "Failed to load Model " + Chr(34) + file$ + Chr(34) + "."

Graphics3D 1024, 768, 32, 2
SetBuffer BackBuffer()
AppTitle "Model View"

;Camera
Global Cam = CreateCamera()
CameraRange Cam, .01, 1000

;Light
RotateEntity CreateLight(), 45, 45, 0

;Model
ms = MilliSecs()
If Right(file$, 4) = ".md2" Then
	Model = LoadMD2(file$)
	ScaleEntity Model, .1, .1, .1
Else
	Model = LoadMesh(file$)
	FitMesh Model, -1, -1, -1, 2, 2, 2, True
EndIf
PositionEntity Model, 0, 0, 3
If Model = 0 RuntimeError "Failed to load Model " + Chr(34) + file$ + Chr(34) + "."
ModelLoadTime = MilliSecs() - ms

cnt_surf = CountSurfaces(Model)
For i = 1 To cnt_surf
	cnt_vert = cnt_vert + CountVertices(GetSurface(Model, i))
	cnt_tris = cnt_tris + CountTriangles(GetSurface(Model, i))
Next

wire = False
freelook = False
While Not KeyHit(1)
	If MouseDown(1) Then
		mys# = mys# - MouseYSpeed()
		mxs# = mxs# + MouseXSpeed()
	EndIf
	If MouseDown(2) Then mzs# = mzs# + MouseYSpeed() * .03
	TurnEntity Model, mys#, mxs#, 0, True
	TranslateEntity Model, 0, 0, mzs#
	mxs# = mxs# * .65
	mys# = mys# * .65
	mzs# = mzs# * .65
	If KeyHit(17) Then
		wire = 1 - wire
		WireFrame wire
	EndIf
	If KeyHit(57) Then
		freelook = 1 - freelook
	EndIf
	If freelook Then
		PositionEntity Model, 0, 0, 3
		RotateEntity Model, 0, 0, 0
		FreeLook(.003)
	Else
		PositionEntity Cam, 0, 0, 0
		RotateEntity Cam, 0, 0, 0
		MouseXSpeed()
		MouseYSpeed()
	EndIf
	RenderWorld
	xp = GraphicsWidth() / 2
	Text 0, 0, "Surfaces: " + cnt_surf
	Text 0, 20, "Vertices: " + cnt_vert
	Text 0, 40, "Triangles: " + cnt_tris
	Text 0, 60, "Load time: " + ModelLoadTime
	Text xp, 0, "Press 'W' for wireframe mode.", True
	Text xp, 20, "Press 'SPACE' for freelook mode.", True
	Flip
Wend
End

Global CamXS#, CamZS#, CamRotXS#, CamRotYS#
Function FreeLook(sp# = .1)
If sp# > 0 Then
	CamXS# = (CamXS# + ((KeyDown(32) Or KeyDown(205)) - (KeyDown(30) Or KeyDown(203))) * sp# * 2) * .75
	CamZS# = (CamZS# + ((KeyDown(17) Or KeyDown(200)) - (KeyDown(31) Or KeyDown(208))) * sp# * 2) * .75
	MoveEntity Cam, CamXS#, 0, CamZS#
EndIf
CamRotXS# = ((MouseXSpeed() - CamRotXS#) * .35 + CamRotXS#) * .75
CamRotYS# = ((MouseYSpeed() - CamRotYS#) * .35 + CamRotYS#) * .75
If EntityPitch(Cam) + CamRotYS# < -85 pitch# = -85 ElseIf EntityPitch(Cam) + CamRotYS# > 85 pitch# = 85 Else pitch# = EntityPitch(Cam) + CamRotYS#
yaw# = -CamRotXS# + EntityYaw(Cam)
RotateEntity Cam, pitch#, yaw#, 0
MoveMouse GraphicsWidth() / 2, GraphicsHeight() / 2
End Function
