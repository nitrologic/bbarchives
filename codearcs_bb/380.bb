; ID: 380
; Author: BlitzSupport
; Date: 2002-08-20 23:19:29
; Title: TriMesh
; Description: Draws 2D version of mesh as overlay...


; -----------------------------------------------------------------------------
; TriMesh (camera, mesh)
; -----------------------------------------------------------------------------
; Inline version, uses Simon Armstrong's line function (which Blitz's line
; function is based on), because you can use it on locked buffers, which turns
; out to be quite a lot faster in this fairly demanding loop...

Function TriMesh (usecam, mesh, argb = $FFFFFFFF)

	; Save some hassle if the mesh can't be seen...
	
	If EntityInView (mesh, usecam)

		; Lock current buffer...
		
		LockBuffer

			; Parse each surface in mesh...
			
			For s = 1 To CountSurfaces (mesh)
	
				surf = GetSurface (mesh, s)
	
				; Parse each triangle in surface...
				
				For t = 0 To CountTriangles (surf) - 1
	
					; Get triangle's vertice positions... presumably these are
					; in anti-clockwise order (since it appears to work :)
	
					v0 = TriangleVertex (surf, t, 0)
					v1 = TriangleVertex (surf, t, 1)
					v2 = TriangleVertex (surf, t, 2)
	
					; ---------------------------------------------------------
					; Mark's code for backface culling...
					; ---------------------------------------------------------
					
				    ; Vertex positions...
				
				    x0# = VertexX (surf, v0)
					y0# = VertexY (surf, v0)
					z0# = VertexZ (surf, v0)
					
				    x1# = VertexX (surf, v1)
					y1# = VertexY (surf, v1)
					z1# = VertexZ (surf, v1)
					
				    x2# = VertexX (surf, v2)
					y2# = VertexY (surf, v2)
					z2# = VertexZ (surf, v2)
				    
				    ; Deltas...
				
				    dx1# = x1 - x0: dy1# = y1 - y0: dz1# = z1 - z0
				    dx2# = x2 - x0: dy2# = y2 - y0: dz2# = z2 - z0
				
				    ; Cross product...
				
				    cx# = dy1 * dz2 - dy2 * dz1
				    cy# = dz1 * dx2 - dz2 * dx1
				    cz# = dx1 * dy2 - dx2 * dy1
				    
				    ; Normalize...
				
				    sz# = Sqr (cx * cx + cy * cy + cz * cz)
				    cx = cx / sz: cy = cy / sz: cz = cz / sz
				    
				    ; Plane offset...
				
				    dt# = -(cx * x0 + cy * y0 + cz * z0)
				    
				    ; Transform eye to mesh coords...
				
				    TFormPoint 0, 0, 0, usecam, mesh
	
					; Super-complicated cull-decision stuff...

				    If TFormedX () * cx + TFormedY () * cy + TFormedZ () * cz + dt > 0

						; -----------------------------------------------------
						; Get 2D position for each point of triangle...
						; -----------------------------------------------------
	
						TFormPoint (x0, y0, z0, mesh, 0)
						CameraProject usecam, TFormedX (), TFormedY (), TFormedZ ()
						vx0 = ProjectedX ()
						vy0 = ProjectedY ()
	
						TFormPoint (x1, y1, z1, mesh, 0)
						CameraProject usecam, TFormedX (), TFormedY (), TFormedZ ()
						vx1 = ProjectedX ()
						vy1 = ProjectedY ()
	
						TFormPoint (x2, y2, z2, mesh, 0)
						CameraProject usecam, TFormedX (), TFormedY (), TFormedZ ()
						vx2 = ProjectedX ()
						vy2 = ProjectedY ()
	
						; -----------------------------------------------------
						; Draw triangle...
						; -----------------------------------------------------

						AcidLine (vx0, vy0, vx1, vy1, argb)
						AcidLine (vx1, vy1, vx2, vy2, argb)
						AcidLine (vx2, vy2, vx0, vy0, argb)
						
					EndIf
					
				Next
	
			Next

		; Unlock locked buffer...
		
		UnlockBuffer

	EndIf

End Function

; -----------------------------------------------------------------------------
; Simon Armstrong's Line function, slightly modified: I lock the buffers once
; per TriMesh call, so disabled that here -- be sure to re-enabled the commented
; out Lock/UnlockBuffer lines if you use this elsewhere!
; -----------------------------------------------------------------------------

Function AcidLine (x0, y0, x1, y1, argb)

	cx0 = 0
	cy0 = 0

	cx1 = GraphicsWidth () - 1
	cy1 = GraphicsHeight () - 1

	While True

		clip0 = 0
		clip1 = 0
		
		If y0 > cy1 clip0 = clip0 Or 1 Else If y0 < cy0 clip0 = clip0 Or 2 
		If x0 > cx1 clip0 = clip0 Or 4 Else If x0 < cx0 clip0 = clip0 Or 8 
		If y1 > cy1 clip1 = clip1 Or 1 Else If y1 < cy0 clip1 = clip1 Or 2
		If x1 > cx1 clip1 = clip1 Or 4 Else If x1 < cx0 clip1 = clip1 Or 8 

		If (clip0 Or clip1) = 0 Exit ;draw Line
		If (clip0 And clip1) Return ;outside

		If (clip0 And 1) = 1 x0 = x0 + ((x1 - x0) * (cy1 - y0)) / (y1 - y0) y0 = cy1 Goto continue 
		If (clip0 And 2) = 2 x0 = x0 + ((x1 - x0) * (cy0 - y0)) / (y1 - y0) y0 = cy0 Goto continue 
		If (clip0 And 4) = 4 y0 = y0 + ((y1 - y0) * (cx1 - x0)) / (x1 - x0) x0 = cx1 Goto continue 
		If (clip0 And 8) = 8 y0 = y0 + ((y1 - y0) * (cx0 - x0)) / (x1 - x0) x0 = cx0 Goto continue 
		If (clip1 And 1) = 1 x1 = x0 + ((x1 - x0) * (cy1 - y0)) / (y1 - y0) y1 = cy1 Goto continue 
		If (clip1 And 2) = 2 x1 = x0 + ((x1 - x0) * (cy0 - y0)) / (y1 - y0) y1 = cy0 Goto continue 
		If (clip1 And 4) = 4 y1 = y0 + ((y1 - y0) * (cx1 - x0)) / (x1 - x0) x1 = cx1 Goto continue 
		If (clip1 And 8) = 8 y1 = y0 + ((y1 - y0) * (cx0 - x0)) / (x1 - x0) x1 = cx0 Goto continue 

		.continue

	Wend
	
	dx = x1 - x0
	dy = y1 - y0

	;RuntimeError dx
		
	If (dx Or dy) = 0 
		WritePixel x0, y0, argb
		Return
	EndIf
	
	If (dx >= 0) sx = 1: ax = dx Else sx =-1: ax = -dx 
	If (dy >= 0) sy = 1: ay = dy Else sy =-1: ay = -dy 
	
;	LockBuffer GraphicsBuffer ()

	If (ax > ay)
	
		ddf	 = ay + ay - ax
		sadj = ax + ax
		padj = ay + ay
		
		While (ax > -1);0)
			WritePixelFast x0, y0, argb
			x0 = x0 + sx
			ddf = ddf + padj
			If (ddf > 0) y0 = y0 + sy: ddf = ddf - sadj
			ax = ax - 1
		Wend
		
	Else
	
		ddf  = ax + ax - ay
		sadj = ay + ay
		padj = ax + ax
		
		While (ay > -1);0)
			WritePixelFast x0, y0, argb
			y0  = y0 + sy
			ddf = ddf + padj
			If (ddf > 0) x0 = x0 + sx: ddf = ddf - sadj
			ay = ay - 1
		Wend
		
	EndIf
	
;	UnlockBuffer GraphicsBuffer ()
	
End Function



; -----------------------------------------------------------------------------
; Demo...
; -----------------------------------------------------------------------------

AppTitle "TriMesh: Use cursors, plus A & Z... SPACE draws lines"

Graphics3D 640, 480, 0, 2

; Our camera...

cam = CreateCamera ()
CameraClsColor cam, 32, 64, 96
MoveEntity cam, 0, 0, -5

; A cube, yesterday...

cube = CreateCube ()
EntityColor cube, 128, 128, 64

; A light...

light = CreateLight ()
PositionEntity light, -100, 20, -50
PointEntity light, cube

Repeat

	TurnEntity cube, 0.1, 0.2, 0.4
		
	If KeyDown (203) TranslateEntity cube, -0.1, 0, 0
	If KeyDown (205) TranslateEntity cube, 0.1, 0, 0
	If KeyDown (200) TranslateEntity cube, 0, 0.1, 0
	If KeyDown (208) TranslateEntity cube, 0, -0.1, 0
	If KeyDown (30) TranslateEntity cube, 0, 0, 0.1
	If KeyDown (44) TranslateEntity cube, 0, 0, -0.1

	RenderWorld

	If KeyDown (57)
		TriMesh cam, cube
	Else
		EntityAlpha cube, 1
	EndIf
	
	Flip

Until KeyHit (1)

End
