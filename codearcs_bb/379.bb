; ID: 379
; Author: BlitzSupport
; Date: 2002-08-20 23:57:22
; Title: 2D-to-3D entity positioning (or something)
; Description: Positions entity 'at' 2D co-ords. (BTW: Bwu-ha-ha @ Rob! :)


; Most of this is unnecessary to use PositionEntityFrom2D () -- it's all just
; part of the spiffy demo :)

AppTitle "PositionEntityFrom2D..."
Graphics3D 640, 480

cam = CreateCamera ()
CameraRange cam, 0.1, 1000

light = CreateLight ()
MoveEntity light, -50, 25, -10
PointEntity light, cam

point = CreateSphere ()
EntityColor point, 255, 0, 0
PositionEntity point, 1, -4, 15
EntityPickMode point, 2

cube = CreateCube ()
ScaleMesh cube, 0.5, 0.5, 0.5
EntityColor cube, 255, 255, 0
PositionEntity cube, 5, 2, 5
EntityPickMode cube, 2

cone = CreateCone ()
ScaleMesh cone, 0.75, 0.75, 0.75
EntityColor cone, 0, 255, 0
PositionEntity cone, -5, -2, 10
EntityPickMode cone, 2

MoveMouse GraphicsWidth () / 2, GraphicsHeight () / 2
HidePointer

Repeat

	If picked = False
		If KeyDown (203) TurnEntity cam, 0, 1, 0, 1
		If KeyDown (205) TurnEntity cam, 0, -1, 0, 1
		If KeyDown (200) TurnEntity cam, 1, 0, 0
		If KeyDown (208) TurnEntity cam, -1, 0, 0
		If KeyDown (30) MoveEntity cam, 0, 0, 0.1
		If KeyDown (44) MoveEntity cam, 0, 0, -0.1
	Else
		If KeyDown (30) MoveEntity picked, 0, 0, 0.1
		If KeyDown (44) MoveEntity picked, 0, 0, -0.1
	EndIf
	
	mx = MouseX ()
	my = MouseY ()

	If MouseDown (1)
		If picked = False
			picked = CameraPick (cam, mx, my)
			If picked
				CameraProject cam, EntityX (picked, 1), EntityY (picked, 1), EntityZ (picked, 1)
				MoveMouse ProjectedX (), ProjectedY ()
			EndIf
		Else
			PositionEntityFrom2D (cam, picked, mx, my)
		EndIf
	Else
		picked = False
	EndIf
	
	UpdateWorld
	RenderWorld

	If picked
		If EntityVisible (picked, cam)
			Color 255, 255, 255
		Else
			Color 127, 127, 127
		EndIf
		TriMesh cam, picked
	EndIf
	
	; Draw crosshair...
	Color 0, 0, 0: Line mx - 4, my, mx + 4, my: Line mx, my - 4, mx, my + 4
	Color 255, 255, 255: Line mx - 5, my - 1, mx + 3, my - 1: Line mx - 1, my - 5, mx - 1, my + 3

	Text 20, 20, "Move using cursors and A/Z"
	Text 20, 40, "Click/drag objects with mouse plus A & Z to alter objects' z depth..."
	Flip
	
Until KeyHit (1)

End

; -----------------------------------------------------------------------------
; PositionEntityFrom2D ()
; -----------------------------------------------------------------------------
; Positions an entity at 3D x/y co-ords translated from given 2D co-ords, at
; specified z position. Useful for positioning an entity at mouse x/y position,
; at its current z depth...
; -----------------------------------------------------------------------------
; PARAMETERS...
; -----------------------------------------------------------------------------
; REQUIRED...
; -----------------------------------------------------------------------------
; entity is the entity to be positioned.
; x2d is the 2D x position you want translated to 3D.
; y2d is the 2D y position you want translated to 3D.
; z3d is the * 3D * z depth at which the translation should occur.
; -----------------------------------------------------------------------------
; OPTIONAL...
; -----------------------------------------------------------------------------
; positionGlobal defaults to false, as per PositionEntity.
; camZoom must be set manually if you've used CameraZoom to change it...
; -----------------------------------------------------------------------------

Function PositionEntityFrom2D (usecam, entity, x2d#, y2d#, positionGlobal = 0, camZoom# = 1)
	gw = GraphicsWidth ()
	gh = GraphicsHeight ()
	x# = -((gw / 2) - x2d)
	y# = (gh / 2) - y2d
	parent = GetParent (entity)
	EntityParent entity, usecam
	z3d# = Abs (EntityZ (entity))
	div# = (gw / (2 / camzoom)) / z3d
	PositionEntity entity, x / div, y / div, z3d, positionGlobal
	EntityParent entity, parent
End Function


Function GlobalizeVertex (mesh, surface, index)
	vx# = VertexX (surface, index)
	vy# = VertexY (surface, index)
	vz# = VertexZ (surface, index)
	TFormPoint (vx, vy, vz, mesh, 0)
End Function

Function VertexGlobalX# ()
	Return TFormedX ()
End Function

Function VertexGlobalY# ()
	Return TFormedY ()
End Function

Function VertexGlobalZ# ()
	Return TFormedZ ()
End Function

Function VertexProject (usecam, mesh, surface, index)
	GlobalizeVertex (mesh, surface, index)
	CameraProject usecam, VertexGlobalX (), VertexGlobalY (), VertexGlobalZ ()
End Function

Function VertexScreenX ()
	Return ProjectedX ()
End Function

Function VertexScreenY ()
	Return ProjectedY ()
End Function

; Made possible thanks to SSwift and http://www.sbdev.pwp.blueyonder.co.uk/tut4.htm !

Function TriMesh (usecam, mesh)

	If EntityInView (mesh, usecam)

		For s = 1 To CountSurfaces (mesh)

			surf = GetSurface (mesh, s)

			; Parse each triangle in mesh...
			
			For t = 0 To CountTriangles (surf) - 1

				; Get triangle's vertice positions... presumably these are
				; in anti-clockwise order (since it appears to work :)

				v0 = TriangleVertex (surf, t, 0)
				v1 = TriangleVertex (surf, t, 1)
				v2 = TriangleVertex (surf, t, 2)
				
				If BackFacing (surf, v0, v1, v2, mesh, usecam)

					; Get 2D position for each point of triangle...

					VertexProject (usecam, mesh, surf, v0)
					vx0 = VertexScreenX ()
					vy0 = VertexScreenY ()

					VertexProject (usecam, mesh, surf, v1)
					vx1 = VertexScreenX ()
					vy1 = VertexScreenY ()

					VertexProject (usecam, mesh, surf, v2)
					vx2 = VertexScreenX ()
					vy2 = VertexScreenY ()

					; Draw triangle...

					Line vx0, vy0, vx1, vy1
					Line vx1, vy1, vx2, vy2
					Line vx2, vy2, vx0, vy0	

				EndIf
				
			Next

		Next

	EndIf

End Function

Function BackFacing (surf, v0, v1, v2, mesh, camera)

    ;vertex position
    x0# = VertexX (surf, v0): y0# = VertexY (surf, v0): z0# = VertexZ (surf, v0)
    x1# = VertexX (surf, v1): y1# = VertexY (surf, v1): z1# = VertexZ (surf, v1)
    x2# = VertexX (surf, v2): y2# = VertexY (surf, v2): z2# = VertexZ (surf, v2)
    
    ;deltas
    dx1# = x1 - x0: dy1# = y1 - y0: dz1# = z1 - z0
    dx2# = x2 - x0: dy2# = y2 - y0: dz2# = z2 - z0

    ;cross product
    cx# = dy1 * dz2 - dy2 * dz1
    cy# = dz1 * dx2 - dz2 * dx1
    cz# = dx1 * dy2 - dx2 * dy1
    
    ;normalize...
    sz# = Sqr (cx * cx + cy * cy + cz * cz)
    cx = cx / sz: cy = cy / sz: cz = cz / sz
    
    ;plane offset
    dt# = -(cx * x0 + cy * y0 + cz * z0)
    
    ;tranform eye to mesh coords
    TFormPoint 0, 0, 0, camera, mesh
    
    Return TFormedX () * cx + TFormedY () * cy + TFormedZ () * cz + dt > 0
    
End Function
