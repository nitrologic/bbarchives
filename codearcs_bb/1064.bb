; ID: 1064
; Author: Neochrome
; Date: 2004-06-01 07:47:44
; Title: Crude Little coding Object Pushing!
; Description: this is a REALY simple pushing items example

Graphics3D 800,600,16,2

Const coll_world = 1
Const coll_object = 2
Const coll_player = 3

Global camera	= CreateCamera()
Global player 	= CreateCube()	: EntityColor player,255,0,0
Global level 	= CreateCube():ScaleEntity level, 170,10,170: FlipMesh level
Global light	= CreateLight(2)
Global blob		= CreateCube()	: EntityColor blob,255,255,0

Global boxed	= CreateCube()	: EntityColor boxed,0,0,255



Collisions coll_player, coll_object, 2, 2
Collisions coll_player, coll_world, 2,2
Collisions coll_object, coll_world, 2, 2

EntityType level, 	coll_world
EntityType player, 	coll_player
EntityType blob,	coll_object

PositionEntity boxed,22,-9,0
EntityType boxed, coll_world



While Not KeyHit(1)
	Cls


	
	If KeyDown(200) Then MoveEntity player,0,0,.2
	If KeyDown(208) Then MoveEntity player,0,0,-.2
	
	If KeyDown(203) Then TurnEntity player,0,4,0
	If KeyDown(205) Then TurnEntity player,0,-4,0

	PointEntity camera, player
	TranslateEntity player,0,-.1,0
	TranslateEntity blob,0,-.1,0



	z = EntityCollided(player, coll_object) 
	If z = blob
		i=1
		Nx# = CollisionNX(player%, i)
		Ny# = CollisionNY(player%, i)
		Nz# = CollisionNZ(player%, i)
					
		vy# = VectorYaw(-nx, -ny, -nz)

		tfm_z# = Cos(vy#)/5
		tfm_x# = -Sin(vy#)/5
		tfm_y# = VectorPitch(-nx, -ny, -nz)
		If Abs(tfm_y)<60 Then TranslateEntity blob,tfm_x,0,tfm_z
	End If
	
	UpdateWorld()
	RenderWorld()

	Flip
Wend
End
