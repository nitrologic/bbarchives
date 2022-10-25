; ID: 540
; Author: Ken Lynch
; Date: 2003-01-07 05:31:30
; Title: No Reponse Collisions
; Description: No response collisions.

Graphics3D 800,600

light% = CreateLight()
camera% = CreateCamera()

pivot% = CreatePivot()
EntityType pivot, 1
EntityRadius pivot, 0.1

sphere% = CreateSphere()
ScaleEntity sphere, 0.1, 0.1, 0.1
EntityRadius sphere, 0.1
EntityType sphere, 2
EntityParent sphere, pivot

PositionEntity camera, 0, 2, -2
PointEntity camera, pivot

For i = 0 To 5

	cube = CreateCube()
	EntityType cube, 3
	PositionEntity cube, Rnd(-2, 2), 0, Rnd(-2, 2)
	ScaleEntity cube, 0.2, 0.2, 0.2
	
Next

For i = 0 To 5

	ball = CreateCylinder()
	EntityType ball, 4
	PositionEntity ball, Rnd(-2, 2), 0, Rnd(-2, 2)
	ScaleEntity ball, 0.2, 0.2, 0.2
	
Next

Collisions 2, 3, 2, 2
Collisions 1, 4, 2, 2

Repeat

	If KeyDown(200) Then TranslateEntity pivot, 0, 0, 0.1
	If KeyDown(208) Then TranslateEntity pivot, 0, 0, -0.1
	If KeyDown(203) Then TranslateEntity pivot, -0.1, 0, 0
	If KeyDown(205) Then TranslateEntity pivot, 0.1, 0, 0

	UpdateWorld

	PositionEntity sphere, 0, 0, 0
	
	cyl_col% = cyl_col + CountCollisions(pivot)
	cube_col% = cube_col + CountCollisions(sphere)

	ResetEntity sphere

	RenderWorld

	Text 0, 0, "Cylinder Collisions: " + cyl_col	
	Text 0, 16, "Cube Collisions: " + cube_col
	
	Flip

Until KeyHit(1)
