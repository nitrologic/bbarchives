; ID: 2543
; Author: Warner
; Date: 2009-07-23 18:27:18
; Title: minib3d platform collisions
; Description: a minib3d example of using platform collisions

Import sidesign.minib3d

Graphics3D 800, 600, 0, 2

CreateLight()

Local piv:TPivot, cube:TMesh

'everything is a child of this pivot
piv = CreatePivot()

'create camera
cam = CreateCamera(piv)
MoveEntity cam, 0, 5, -15
CameraClsColor cam, 0, 0, 255

'create cube (=platform)
cube = CreateCube(piv)
ScaleEntity cube, 4, 4, 4
PositionMesh cube, 0, -1.35, 0
EntityType cube, 2
MoveEntity cube, 0, 0.4, 0
'MoveEntity cube, 0, 2, 0 '<--rotation

'create ground
ground = createplane2(piv)
EntityColor ground, 0, 255, 0
MoveEntity ground, 0, -1, 0
EntityType ground, 2

'create sphere (=player)
sphere = CreateSphere(8, piv)
EntityColor sphere, 255, 0, 0
PositionEntity sphere, -10, 1, 0
EntityType sphere, 1

'set collisions
Collisions 1, 2, 2, 3

Local p#
Repeat

	'move sphere
	If KeyDown(37) MoveEntity sphere, -0.2, 0, 0 
	If KeyDown(39) MoveEntity sphere, 0.2, 0, 0
	If KeyDown(38) MoveEntity sphere, 0,0, 0.2 
	If KeyDown(40) MoveEntity sphere, 0, 0,-0.2
	MoveEntity sphere, 0, -1, 0
	UpdateWorld 0 'normal collisions
	
	p :+ 2
	hh# = Sin(p) * 0.1
	
'	piv.px = cube.px '<-rotation
'	piv.py = cube.py
'	piv.pz = cube.pz

	EntityParent cube, 0	

'	TurnEntity piv, 0, 1, 0 '<-rotation

	'move everything, except the cube
	MoveEntity piv, 0, -hh, 0 '<-disable for rotation
	EntityParent cube, piv

	UpdateWorld 'platform collisions
	
	RenderWorld
	Flip

Until KeyHit(27)
End

'--------------------------------------------------------------------------------------------------------------------------------------
'												CreatePlane()
'--------------------------------------------------------------------------------------------------------------------------------------
Function CreatePlane2:TEntity(parent:TEntity=Null)

	Local mesh:TMesh = CreateMesh(parent)
	Local surf:TSurface = CreateSurface(mesh)

	Local s# = 900.0
	AddVertex surf, -s, 0,  s, -s,  s
	AddVertex surf,  s, 0,  s,  s,  s
	AddVertex surf,  s, 0, -s,  s, -s
	AddVertex surf, -s, 0, -s, -s, -s
	AddTriangle surf, 0, 1, 2
	AddTriangle surf, 0, 2, 3
	VertexNormal surf, 0, 0, 1, 0
	VertexNormal surf, 1, 0, 1, 0
	VertexNormal surf, 2, 0, 1, 0
	VertexNormal surf, 3, 0, 1, 0

	Return mesh

End Function
