; ID: 375
; Author: BlitzSupport
; Date: 2002-07-31 00:05:11
; Title: DotMesh
; Description: Draws a dotted version of a mesh


; Parameters: camera used, mesh...

Function DotMesh (usecam, mesh)
	If EntityInView (mesh, usecam)
		gw = GraphicsWidth (): gh = GraphicsHeight ()
		LockBuffer
			For s = 1 To CountSurfaces (mesh)
				surf = GetSurface (mesh, s)				
				For vert = 0 To CountVertices (surf) - 1
					vx# = VertexX (surf, vert)
					vy# = VertexY (surf, vert)
					vz# = VertexZ (surf, vert)
					TFormPoint (vx, vy, vz, mesh, 0)	
					CameraProject usecam, TFormedX (), TFormedY (), TFormedZ ()
					vx = ProjectedX (): vy = ProjectedY ()
					If (vx > -1) And (vx < gw) And (vy > -1) And (vy < gh)
						WritePixelFast vx, vy, $00FFFFFF
					EndIf
				Next
			Next
		UnlockBuffer
	EndIf
End Function

Graphics3D 640, 480
cam = CreateCamera ()

ball = CreateSphere ()
MoveEntity ball, 0, 0, 2
EntityAlpha ball, 0

Repeat

	TurnEntity ball, 0.1, 0.2, 0.3
	RenderWorld
	
	DotMesh cam, ball
	
	Flip
	
Until KeyHit (1)

End
