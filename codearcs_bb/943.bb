; ID: 943
; Author: Ross C
; Date: 2004-02-24 05:47:23
; Title: Push a row of cubes!
; Description: Pushes a row of cubes, uses blitz collisions system

Graphics3D 640,480,16
SetBuffer BackBuffer()

Const cube_col=1
Const sphere_col=2


Global light= CreateLight()


Global camera=CreateCamera()
PositionEntity camera,0,40,0
RotateEntity camera,90,0,0

Global sphere=CreateSphere()
EntityType sphere,sphere_col

Global level=CreatePivot()

Dim cube(10)
For loop=0 To 10
	cube(loop)=CreateCube()
	PositionEntity cube(loop),-10,0,-10+loop*2
	EntityType cube(loop),cube_col
	EntityParent cube(loop),level
Next



Collisions 1,2,2,2
Collisions 1,1,2,2
Collisions 1,3,2,2

While Not KeyHit(1)
	
	If KeyDown(30) MoveEntity level,0.1,0,0
	If KeyDown(32) MoveEntity level,-0.1,0,0
	If KeyDown(17) MoveEntity level,0,0,-0.1
	If KeyDown(31) MoveEntity level,0,0,0.1

	UpdateWorld
	updatecubes() ; make sure collisions doesn't push the cubes downwards or upwards
	RenderWorld
	Flip
Wend
End

Function updatecubes()
	For loop=0 To 10
		PositionEntity cube(loop),EntityX(cube(loop)),0,EntityZ(cube(loop))
	Next
End Function
