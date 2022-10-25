; ID: 429
; Author: Neochrome
; Date: 2002-09-16 12:53:35
; Title: Simple Laser Code
; Description: Laser from A to B

Graphics3D 640,480,16,2

SetBuffer BackBuffer()

laser = CreateCube()

PositionEntity laser,0,0,30

PointA = CreateCube()

PositionEntity PointA,-30,-30,30

PointB = CreateCube()

PositionEntity PointB,-60,60,30


lit = CreateLight()

cam = CreateCamera()

PositionEntity Cam,0,0,-50
ScaleEntity laser,1,1,1

While Not KeyDown(1)
	Cls
	
	MoveEntity  pointb,(MouseXSpeed()/5),-MouseYSpeed()/5,0
	
	t=EntityDistance(PointA,PointB)/2
	
	ScaleEntity laser,1,1,t
	PositionEntity laser,EntityX(pointa),EntityY(pointa),EntityZ(pointa)
	PointEntity laser,pointb
	MoveEntity laser,0,0,t
	
	
	
	
	
	UpdateWorld
	RenderWorld
	
	Flip
Wend
End
