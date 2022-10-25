; ID: 373
; Author: BlitzSupport
; Date: 2002-07-31 00:03:13
; Title: Vertex projection
; Description: Some functions for getting world/2D co-ords of vertices


; -----------------------------------------------------------------------------
; Vertex projection functions...
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; WHAT'S THIS?
; -----------------------------------------------------------------------------
; 	Convert vertex co-ordinates within mesh into global co-ordinates.
; 	Convert vertex 3D co-ordinates to 2D co-ordinates.
; 	Pick nearest vertex when a mesh is clicked on.
; -----------------------------------------------------------------------------

; The main functions of interest here are:

; -----------------------------------------------------------------------------
; MakePickable		--	makes a mesh 'pickable' by PickNearestVertex (and the
; 						native Blitz 'pick' commands).
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; PickNearestVertex --  gets surface and index of nearest vertex to requested
; 						2D x/y position if within requested mesh. If this returns
; 						True, you can then use the global variables V_PickedMesh,
; 						V_PickedSurface and V_PickedIndex to get the picked mesh,
; 						surface and vertex index respectively. Remember UpdateWorld!
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; VertexProject		--  calculates 2D co-ords of given vertex. They are retrieved via
; 						VertexScreenX () and VertexScreenY () after calling this function.
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; GlobalizeVertex	--  gets global 3D co-ords of given vertex. Retrieved via VertexGlobalX (),
; 						VertexGlobalY () and VertexGlobalZ () after calling this function.
; -----------------------------------------------------------------------------

; NOTE: The 'usecam' parameter in these functions is the camera currently in use.











; -----------------------------------------------------------------------------
; *** START OF INCLUDES...
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; IMPORTANT: All globals are required!
; -----------------------------------------------------------------------------

Global V_PickedMesh							; Last mesh picked during PickNearestVertex ()
Global V_PickedSurface						; Last surface picked during PickNearestVertex ()
Global V_PickedIndex						; Last vertex index picked during PickNearestVertex ()

; -----------------------------------------------------------------------------
; IMPORTANT: All functions are required!
; -----------------------------------------------------------------------------

Function MakePickable (mesh, pickmode = 2, obscurer = True)
	EntityPickMode mesh, pickmode, obscurer
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

Function PickNearestVertex (usecam, x, y)
	picked = CameraPick (usecam, x, y)					; Find entity at pointer position
	If picked
		surf = PickedSurface ()							; Get selected surface
		tri = PickedTriangle ()							; Get selected triangle within surface
		t0 = TriangleVertex (surf, tri, 0)				; Find vertex 0 of triangle
		t1 = TriangleVertex (surf, tri, 1)				; Find vertex 1 of triangle
		t2 = TriangleVertex (surf, tri, 2)				; Find vertex 2 of triangle
		VertexProject (usecam, picked, surf, t0)		; Project vertex 0 screen position
		vx0 = VertexScreenX ()							; Get x position of vertex 0
		vy0 = VertexScreenY ()							; Get y position of vertex 0
		VertexProject (usecam, picked, surf, t1)		; Project vertex 1 screen position
		vx1 = VertexScreenX ()							; Get x position of vertex 1
		vy1 = VertexScreenY ()							; Get y position of vertex 1
		VertexProject (usecam, picked, surf, t2)		; Project vertex 2 screen position
		vx2 = VertexScreenX ()							; Get x position of vertex 2
		vy2 = VertexScreenY ()							; Get y position of vertex 2
		pd0# = Abs (Sqr ((x - vx0) ^ 2 + (y - vy0) ^ 2)) ; Distance from vertex 0 to pointer
		pd1# = Abs (Sqr ((x - vx1) ^ 2 + (y - vy1) ^ 2)) ; Distance from vertex 1 to pointer
		pd2# = Abs (Sqr ((x - vx2) ^ 2 + (y - vy2) ^ 2)) ; Distance from vertex 2 to pointer
		closest# = pd0: vindex = t0						; Assume vertex 0 is closest to pointer
		If pd1 < closest								; See if vertex 1 is closer...
			closest = pd1								; ... if so, that's now 'closest'...
			vindex = t1									; ... and the vertex index is #1
		EndIf
		If pd2 < closest								; See if vertex 2 is closer...
			vindex = t2									; ... if so, vertex index is #2
		EndIf
		V_PickedMesh	= picked
		V_PickedSurface = surf
		V_PickedIndex	= vindex
		Return True
	Else	
		V_PickedMesh	= 0
		V_PickedSurface = 0
		V_PickedIndex	= 0
		Return False
	EndIf
End Function

; BONUS function :)

Function TriMesh (usecam, mesh)
	If EntityInView (mesh, usecam)
		For s = 1 To CountSurfaces (mesh)
			surf = GetSurface (mesh, s)
			For t = 0 To CountTriangles (surf) - 1
				v0 = TriangleVertex (surf, t, 0)
				VertexProject (usecam, mesh, surf, v0)
				vx0 = VertexScreenX ()
				vy0 = VertexScreenY ()
				v1 = TriangleVertex (surf, t, 1)
				VertexProject (usecam, mesh, surf, v1)
				vx1 = VertexScreenX ()
				vy1 = VertexScreenY ()
				v2 = TriangleVertex (surf, t, 2)
				VertexProject (usecam, mesh, surf, v2)
				vx2 = VertexScreenX ()
				vy2 = VertexScreenY ()
				Line vx0, vy0, vx1, vy1
				Line vx1, vy1, vx2, vy2
				Line vx2, vy2, vx0, vy0
			Next
		Next
	EndIf
End Function

; -----------------------------------------------------------------------------
; *** END OF INCLUDES...
; -----------------------------------------------------------------------------












; -----------------------------------------------------------------------------
; Demo...
; -----------------------------------------------------------------------------

AppTitle "MOUSE: get nearest vertex | CURSORS & A/Z: move | SPACE: overlay triangles"

Graphics3D 640, 480, 0, 2

; Our camera...

cam = CreateCamera ()
CameraClsColor cam, 32, 64, 96
MoveEntity cam, 0, 0, -5

; A cube, yesterday...

cube = CreateCube ()
EntityColor cube, 128, 128, 64

; Make cube 'pickable' -- IMPORTANT!

MakePickable (cube)

; A light...

light = CreateLight ()
PositionEntity light, -100, 20, -50
PointEntity light, cube

; The red 'cursor' ball...

piv = CreateSphere (8, cube)
ScaleEntity piv, 0.1, 0.1, 0.1
EntityColor piv, 255, 0, 0

HidePointer

Repeat
	
	If KeyDown (203) TranslateEntity cube, -0.1, 0, 0
	If KeyDown (205) TranslateEntity cube, 0.1, 0, 0
	If KeyDown (200) TranslateEntity cube, 0, 0.1, 0
	If KeyDown (208) TranslateEntity cube, 0, -0.1, 0
	If KeyDown (30) TranslateEntity cube, 0, 0, 0.1
	If KeyDown (44) TranslateEntity cube, 0, 0, -0.1

	If Not KeyDown (59) Then TurnEntity cube, 0.5, 0.25, 0.125 ; F1 pauses turning...
	
	mx = MouseX ()
	my = MouseY ()

	; If mouse button is clicked on a mesh, find the nearest vertex...
	
	If MouseHit (1)
	
		If PickNearestVertex (cam, mx, my)

			; -----------------------------------------------------------------
			; V_PickedMesh, V_PickedSurface and V_PickedIndex are now valid...
			; -----------------------------------------------------------------

			vx# = VertexX (V_PickedSurface, V_PickedIndex)
			vy# = VertexY (V_PickedSurface, V_PickedIndex)
			vz# = VertexZ (V_PickedSurface, V_PickedIndex)
			PositionEntity piv, vx, vy, vz

			; The 'piv' cursor is parented to the cube. VertexX/Y/Z return the
			; position of the vertex within its mesh, so the above line positions
			; the cursor at the picked vertex's position.

		EndIf
		
	EndIf
	
	UpdateWorld
	RenderWorld

	; Crosshair with shadow...
	
	Color 0, 0, 0: Line mx - 4, my, mx + 4, my: Line mx, my - 4, mx, my + 4
	Color 255, 255, 255: Line mx - 5, my - 1, mx + 3, my - 1: Line mx - 1, my - 5, mx - 1, my + 3

	; Plotting a crosshair over picked vertex to show off 2D vertex projection stuff...

	If V_PickedMesh ; Just checking a vertex has been picked...
	
		VertexProject (cam, V_PickedMesh, V_PickedSurface, V_PickedIndex)
		vx = VertexScreenX ()
		vy = VertexScreenY ()
		
		Color 0, 255, 0: Line vx - 4, vy, vx + 4, vy: Line vx, vy - 4, vx, vy + 4
		Color 255, 255, 255: Line vx - 5, vy - 1, vx + 3, vy - 1: Line vx - 1, vy - 5, vx - 1, vy + 3
		
	EndIf

	If KeyDown (57)
		TriMesh cam, cube
	EndIf
	
	Flip

Until KeyHit (1)

End
