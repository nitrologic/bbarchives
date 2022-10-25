; ID: 2498
; Author: Warner
; Date: 2009-06-05 11:49:46
; Title: Matrices in minib3d
; Description: use quaternions and matrices in b3d

Import sidesign.minib3d

Graphics3D 800, 600, 0, 2

CreateLight()

Local cam:TCamera = CreateCamera()
MoveEntity cam, 0, 0, -15

e1:TEntity = CreateCone()
PositionEntity2 e1, -8,  8, 0

e2:TEntity = CreateCube()
PositionEntity2 e2, -2, 8, 0

e3:TEntity = CreateCube()
PositionEntity2 e3,  3, 8, 0

e4:TEntity = CreateCube()
PositionEntity e4, -8,  2, 0

e5:TEntity = CreateCube()
e6:TEntity = CreateCube()
MoveEntity2 e6, 2, 2, 0
EntityParent2 e6, e5
PositionEntity e5, -2, 2, 4

e7:TEntity = CreateCube()
ScaleEntity2 e7, 1, 2, 1
PositionEntity e7, 4, 2, 0 

e8:TEntity  = CreateCube()
e9:TEntity = CreateCube()
ScaleMesh TMesh(e9), 0.1, 0.1, 0.1
EntityColor e9, 255, 0, 0
PositionEntity2 e8, -8, -4, 0

e10:TEntity = CreateCone()
PositionEntity2 e10, -2, -4, 0

e11:TEntity = CreateCone()
PositionEntity2 e11, 2, -4, 0

Repeat

	TurnEntity2 e2, 1, 2, 3
	
	t = t + 4
	MoveEntity2 e3, Sin(t)*0.1, 0, 0
	
	RotateEntity2 e4, 0, 0, t
	
	TurnEntity2 e5, 0, 1, 0
	
	TurnEntity2 e8, 1, 2, 3
	x#=0 y#=0 z#=3
	TFormPoint2 x#,y#,z#, e8, Null
	PositionEntity2 e9, x, y, z
	
	UpdateWorld
	RenderWorld
			
	BeginMax2D
	
	DrawText "PositionEntity2", 120, 140
	DrawText "TurnEntity2", 300, 140
	DrawText "MoveEntity2", 460, 140
	
	DrawText "RotateEntity2", 120, 300
	DrawText "EntityParent2", 300, 300
	DrawText "ScaleEntity2", 460, 300

	DrawText "TFormPoint2", 120, 460
	DrawText "TFormVector2", 120, 480

	TurnEntity2 e10, 0, 0, 1
	
	pt# = EntityPitch2(e10)
	yw# = EntityYaw2(e10)
	rl# = EntityRoll2(e10)
	
	RotateEntity2 e11, pt, yw, rl
		
	DrawText "EntityPitch2:" + pt, 300, 460
	DrawText "EntityYaw2:  " + yw, 300, 480
	DrawText "EntityRoll2: " + rl, 300, 500

	EndMax2D

	Flip
	
Until KeyHit(key_escape)

End
