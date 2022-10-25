; ID: 2583
; Author: Warner
; Date: 2009-09-15 19:45:48
; Title: FPS like freelook camera
; Description: basic WASD+mouse control

Graphics3D 640, 480, 0, 2
SetBuffer BackBuffer()

CreateLight()

;create 3 copies of the same building
level1 = Building() ;LoadMesh("building.x")
level2 = Building()
level3 = Building()

;!!
;instead of using RemoveTris, create three separate meshes
;one with all floors
;one with all the walls
;one with all the ceilings
;RemoveTris doesn't remove redundant vertices, so it is unoptimised
;!!

RemoveTris(level1, False, False) ;floor
RemoveTris(level2, True, False)  ;wall
RemoveTris(level3, False, True)  ;ceiling
EntityType level1, 2 ;floor
EntityType level2, 3 ;wall
EntityType level3, 4 ;ceiling
EntityPickMode level1, 2 ;floor

Const sz = 10 ;player height

;create player
me = CreatePivot()
EntityType me, 1
EntityRadius me, 5, sz

;create camera
cam = CreateCamera()
EntityParent cam, me
PositionEntity cam, 0, 0, 0

;place player
PositionEntity me,0,50,200
ResetEntity me

Collisions 1, 2, 2, 3 ;floor
Collisions 1, 3, 2, 3 ;walls
Collisions 1, 4, 2, 3 ;ceiling

Local jsp# ;jump speed
Local xsp#, ysp# ;x/y speed

;reset mouse cursor
MoveMouse 400, 300
MouseXSpeed()
MouseYSpeed()

Repeat

	;space = jumpkey
	jkey = KeyHit(57)

	;lookaround with mouse	
	oldx# = EntityX(me)
	oldy# = EntityY(me)
	oldz# = EntityZ(me)
	MoveEntity me, xsp*2, 0, ysp*2
	If EntityCollided(me, 3) Then PositionEntity me, oldx, oldy, oldz ;bump into wall
	
	TurnEntity me, 0, -MouseXSpeed(), 0
	look# = look + MouseYSpeed()
	MoveMouse 400, 300
	
	If look < -80 Then look = -80
	If look > 80 Then look = 80
	RotateEntity cam, look, 0, 0

	;whobble	
	x# = EntityX(me)
	z# = EntityZ(me)
	hh# = Sin((x + z) * 8) * 0.1
	PositionEntity cam, 0, hh#, 0

	;jumping?		
	If jump > 0 Then

		xsp = xsp * 0.98 ;low friction
		ysp = ysp * 0.98

		TranslateEntity me, 0, jsp, 0 ;move up
		jsp = jsp * 0.9 ;jump friction
		If (EntityCollided(me, 2)) Then jump = 0 ;bump into floor
		If (EntityCollided(me, 4)) Then jump = 0 ;bump into ceiling
		jump = jump - 1
		
	;not jumping?
	Else		

		xsp = xsp * 0.9 ;high friction
		ysp = ysp * 0.9
	
		;jump key
		If jkey Then
			x# = EntityX(me)
			y# = EntityY(me)
			z# = EntityZ(me)
			;only allow when on floor
			If LinePick(x,y,z,0,-(sz+1),0) Then jump=45:jsp=3
		Else
			;fall down
			TranslateEntity me, 0, -gv#, 0
			If Not(EntityCollided(me, 2)) Then ;if not on floor
				gv = gv * 1.08 + 0.1 ;increase gravity
			Else
				gv = 0 ;reset gravity
				
				;cursor keys (WASD)
				If KeyDown(30) Then xsp = xsp * 1.01 - 0.1
				If KeyDown(32) Then xsp = xsp * 1.01 + 0.1
				If KeyDown(31) Then ysp = ysp * 1.01 - 0.1
				If KeyDown(17) Then ysp = ysp * 1.01 + 0.1
			End If
		End If
	End If
		
	UpdateWorld
	RenderWorld

	Flip
	
Until KeyHit(1)

End

;remove certain tris
Type TTriangle
	Field v0, v1, v2
End Type
Function RemoveTris(mesh, invert, cei)

	lt = 0
	If EntityClass(mesh) = "Mesh" Then
		For i = 1 To CountSurfaces(mesh)
			s = GetSurface(mesh, i)
			For t = 0 To CountTriangles(s) - 1
				v0 = TriangleVertex( s, t, 0 )
				v1 = TriangleVertex( s, t, 1 )
				v2 = TriangleVertex( s, t, 2 )
				ax# = VertexX( s, v1 ) - VertexX( s, v0 )
				ay# = VertexY( s, v1 ) - VertexY( s, v0 )	
				az# = VertexZ( s, v1 ) - VertexZ( s, v0 )	
				bx# = VertexX( s, v2 ) - VertexX( s, v1 )
				by# = VertexY( s, v2 ) - VertexY( s, v1 )	
				bz# = VertexZ( s, v2 ) - VertexZ( s, v1 )	
				Nx# = ( ay * bz ) - ( az * by )
				Ny# = ( az * bx ) - ( ax * bz )
				Nz# = ( ax * by ) - ( ay * bx )
				If cei Then test2 = (ny < 0) Else test2 = (ny > 0)
				test = (Abs(ny) > Abs(nx)) And (Abs(ny) > Abs(nz)) And test2
				If invert Then test = Not(test)
				
				If test Then
					tt.TTriangle = New TTriangle
					tt\v0 = v0
					tt\v1 = v1
					tt\v2 = v2
				End If
				
			Next
			ClearSurface s, False, True
			ti = 0
			For tt.TTriangle = Each TTriangle
				AddTriangle s, tt\v0, tt\v1, tt\v2
				ti=ti+1
			Next
			Delete Each TTriangle
		Next
	End If
	
	For i = 1 To CountChildren(mesh)
		RemoveTris(GetChild(mesh, i), invert, cei)
	Next
	
	UpdateNormals mesh
	
End Function

;create building
Function Building()

	tex = CreateTexture(16, 16, 1+8)
	Rect 0, 0, 16, 16, False
	CopyRect 0, 0, 16, 16, 0, 0, BackBuffer(), TextureBuffer(tex)
	mesh = CreateCube()
	ScaleMesh mesh, 300, 80, 300
	FlipMesh mesh
	EntityTexture mesh, tex
	ScaleTexture tex, 0.01, 0.03
	
	Return mesh
	
End Function
