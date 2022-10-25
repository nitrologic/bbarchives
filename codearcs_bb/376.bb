; ID: 376
; Author: BlitzSupport
; Date: 2002-07-31 00:06:29
; Title: BoxMesh
; Description: Draws a 2D rectangle to enclose a mesh


; Parameters: camera used, mesh...

Function BoxMesh (usecam, mesh)
	If EntityInView (mesh, usecam)
		largestx = 0: largesty = 0
		gw = GraphicsWidth (): gh = GraphicsHeight ()
		smallestx = gw: smallesty = gh
		For s = 1 To CountSurfaces (mesh)
			surf = GetSurface (mesh, s)
			For vert = 0 To CountVertices (surf) - 1
				vx# = VertexX (surf, vert)
				vy# = VertexY (surf, vert)
				vz# = VertexZ (surf, vert)
				TFormPoint (vx, vy, vz, mesh, 0)
				CameraProject usecam, TFormedX (), TFormedY (), TFormedZ ()
				vx2 = ProjectedX ()
				vy2 = ProjectedY ()
				If vx2 > largestx
					largestx = vx2
				Else
					If vx2 < smallestx
						smallestx = vx2
					EndIf
				EndIf
				If vy2 > largesty
					largesty = vy2
				Else
					If vy2 < smallesty
						smallesty = vy2
					EndIf
				EndIf
			Next
		Next
		If smallestx < 0 Then smallestx = 0
		If smallesty < 0 Then smallesty = 0
		If largestx > gw Then largestx = gw
		If largesty > gh Then largesty = gh
		Rect smallestx, smallesty, largestx - smallestx, largesty - smallesty, False
	EndIf
End Function

AppTitle "Cursors, A & Z..."
Graphics3D 640, 480, 0, 2
cam = CreateCamera ()

cube = CreateCube ()
MoveEntity cube, 0, 0, 5

light = CreateLight ()
MoveEntity light, -10, 2, 5
PointEntity light, cube

Color 0, 255, 0

Repeat

	If KeyDown (203) TranslateEntity cube, -0.1, 0, 0
	If KeyDown (205) TranslateEntity cube, 0.1, 0, 0
	If KeyDown (200) TranslateEntity cube, 0, 0.1, 0
	If KeyDown (208) TranslateEntity cube, 0, -0.1, 0
	If KeyDown (30) TranslateEntity cube, 0, 0, 0.1
	If KeyDown (44) TranslateEntity cube, 0, 0, -0.1

	TurnEntity cube, 0.1, 0.2, 0.3
	RenderWorld
	
	BoxMesh cam, cube
	
	Flip
	
Until KeyHit (1)

End
