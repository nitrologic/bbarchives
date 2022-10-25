; ID: 1923
; Author: Zethrax
; Date: 2007-02-13 12:09:44
; Title: Snap to grid movement
; Description: Demonstrates camerapicking a point on a grid and moving an object so that movement is snapped to the size of the grid cell

Global G_grid_cell_size# = 1.0

Graphics3D 800, 600, 0, 2

Global G_camera = CreateCamera()
CameraZoom G_camera, 1.6
TurnEntity G_camera, 90.0, 0.0, 0.0
MoveEntity G_camera, 0.0, 0.0, -20.0

Global G_light = CreateLight()

Global G_cube = CreateCube()
ScaleMesh G_cube, 0.5, 0.5, 0.5
PositionMesh G_cube, 0.5, 0.5, 0.5
UpdateNormals G_cube

Global G_grid = CreateGrid()
EntityPickMode G_grid, 2

Global G_picked_entity

SetBuffer BackBuffer()

Repeat

	G_picked_entity = CameraPick( G_camera, MouseX(), MouseY() )
	
	If G_picked_entity
	
		x# = Floor( PickedX() / G_grid_cell_size# ) * G_grid_cell_size#
		z# = Floor( PickedZ() / G_grid_cell_size# ) * G_grid_cell_size#		
		PositionEntity G_cube, x#, PickedY(), z#
	
	EndIf
	
	UpdateWorld
	RenderWorld
	Flip
	Delay( 1 )

Until KeyHit( 1 )

End



Function CreateGrid()
	; -- Create grid texture.
	Local i, x, y
	Local grid_2d_tex = CreateTexture ( 256, 256, 11 )
	SetBuffer TextureBuffer ( grid_2d_tex )
	For i = 0 To 4
		Rect i, i, 256 - i - i, 256 - i - i, False
	Next
	For y = 5 To 250
		For x = 5 To 250
			WritePixel x, y, 0
		Next
	Next
	;^^^^^^
	; -- Create 2D grid.
	Local grid_2D = CreatePlane ()
	EntityTexture grid_2D, grid_2d_tex
	EntityFX grid_2D, 9
	;^^^^^^
	Return grid_2D
End Function
