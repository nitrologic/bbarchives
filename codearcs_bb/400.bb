; ID: 400
; Author: BlitzSupport
; Date: 2002-08-20 22:58:11
; Title: 3D tile system
; Description: Create 3D tiled game levels...


Type TilePlane
	Field mesh				; This plane's mesh
	Field surface.Surface	; List of surfaces in meshes
End Type

Type Surface
	Field surf				; Surface handle
	Field brush				; Brush used on this surface
	Field tiles.TilePlane	; Plane this surface belongs to
End Type

Graphics3D 640, 480

cam = CreatePivot ()
camera = CreateCamera (cam)
CameraRange camera, 0.1, 1000

; Load some BRUSHES (not textures)...

grass = LoadBrush ("grass.bmp")
stone = LoadBrush ("stone.bmp")
moss  = LoadBrush ("mossy.bmp")
water = LoadBrush ("water.bmp")

; Create a TilePlane (really just a mesh -- can have lots of 'y levels')...

level.TilePlane = CreateTilePlane ()

; Add each surface type to the TilePlane, using the appropriate brushes. NOTE
; that these surfaces are ONLY valid for this TilePlane. If you need the same
; image for a separate TilePlane, you need to create a new surface with the same
; brush...

grassy.Surface	= AddTilePlaneSurface (level, grass)
stony.Surface	= AddTilePlaneSurface (level, stone)
mossy.Surface	= AddTilePlaneSurface (level, moss)
watery.Surface	= AddTilePlaneSurface (level, water)

xsize = 32
zsize = 32

; -----------------------------------------------------------------------------
; Upper level...
; -----------------------------------------------------------------------------
	
	For x = 0 To xsize - 1
		For z = 0 To zsize - 1
			Select Rand (5)
				Case 1
					AddSurfaceTile (grassy, x, z)
				Case 2
					AddSurfaceTile (stony, x, z)
				Case 3
					AddSurfaceTile (mossy, x, z)
				Case 4
					AddSurfaceTile (watery, x, z)
			End Select
		Next
	Next
	
; -----------------------------------------------------------------------------
; Lower level...
; -----------------------------------------------------------------------------
		
	y = -5
	
	For x = 0 To xsize - 1
		For z = 0 To zsize - 1
			Select Rand (5)
				Case 1
					AddSurfaceTile (grassy, x, z, y)
				Case 2
					AddSurfaceTile (stony, x, z, y)
				Case 3
					AddSurfaceTile (mossy, x, z, y)
				Case 4
					AddSurfaceTile (watery, x, z, y)
			End Select
		Next
	Next
		
; -----------------------------------------------------------------------------

UpdateTilePlaneNormals (level)
PositionEntity level\mesh, 0, -2, 0
EntityFX level\mesh, 16

ScaleMesh level\mesh, 4, 1, 4

Repeat

	If KeyDown (203)
		TurnEntity cam, 0, 2, 0, 1
	Else
		If KeyDown (205)
			TurnEntity cam, 0, -2, 0, 1
		EndIf
	EndIf

	If KeyDown (200)
		TurnEntity cam, 1, 0, 0
	Else
		If KeyDown (208)
			TurnEntity cam, -1, 0, 0
		EndIf
	EndIf
		
	If KeyDown (30)
		speed# = speed + 0.001
	Else
		If KeyDown (44)
			speed# = speed - 0.001
		EndIf
	EndIf

	MoveEntity cam, 0, 0, speed
	
	If KeyHit (17) Then w = 1 - w: WireFrame w
	
	If KeyHit (57)
		If level <> Null Then FreeTilePlane (level)
	EndIf
	
	RenderWorld

	Text 20, 20, TrisRendered ()
	
	Flip
	
Until KeyHit (1)

End

Function CreateTilePlane.TilePlane ()
	tp.TilePlane = New TilePlane
	tp\mesh = CreateMesh ()
	Return tp
End Function

Function AddTilePlaneSurface.Surface (tp.TilePlane, brush)
	tp\surface = New Surface
	tp\surface\surf = CreateSurface (tp\mesh)
	tp\surface\tiles = tp
	tp\surface\brush = brush
	PaintSurface tp\surface\surf, tp\surface\brush
	Return tp\surface
End Function

Function AddSurfaceTile (surface.Surface, x#, z#, y# = 0, width# = 1, height# = 1)

	surf		= surface\surf
	topLeft		= AddVertex (surf, x + (-width / 2), y, z + (height / 2), 0, 0)
	topRight	= AddVertex (surf, x + (width / 2), y, z + (height / 2), 1, 0)
	bottomRight	= AddVertex (surf, x + (width / 2), y, z + (-height / 2), 1, 1)
	bottomLeft	= AddVertex (surf, x + (-width / 2), y, z + (-height / 2), 0, 1)

	AddTriangle (surf, topLeft, bottomRight, bottomLeft)
	AddTriangle (surf, topLeft, topRight, bottomRight)

End Function

Function UpdateTilePlaneNormals (tp.TilePlane)
	UpdateNormals tp\mesh
End Function

Function FreeTilePlane (tp.TilePlane)
	For s.Surface = Each Surface
		If s\tiles = tp
			If s\brush Then FreeBrush s\brush
			Delete s
		EndIf
	Next
	FreeEntity tp\mesh
	Delete tp
End Function
