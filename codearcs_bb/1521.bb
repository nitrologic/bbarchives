; ID: 1521
; Author: Ross C
; Date: 2005-11-08 11:19:43
; Title: Mirror Mesh
; Description: Takes a mesh, and mirrors it along an Axis X, Y or Z. Does NOT copy the mesh.

Graphics3D 800,600
SetBuffer BackBuffer()


Global cam = CreateCamera()
PositionEntity cam,0,0,-10

Global light = CreateLight()

Global cube = CreateCube()
PositionMesh cube,2,1,2


While Not KeyHit(1)


	If KeyHit(203) Then mirror_mesh(cube,0)
	If KeyHit(200) Then mirror_mesh(cube,1)
	If KeyHit(208) Then mirror_mesh(cube,2)


	UpdateWorld
	RenderWorld
	Flip
Wend
End



;plane to mirror along - 0 = x, 1 = y and 2 = z
Function mirror_mesh(mesh,plane,surface = 0)


	If plane < 0 Or plane > 2 Then Return 0

	If surface = 0 Then
		s_count = CountSurfaces(mesh)
		l_count = 1
	Else
		For loop = 0 To CountSurfaces(mesh)
			temp = GetSurface(mesh,loop)
			If surface = temp Then
				s_count = temp
				l_count = temp
			End If
		Next
	End If
	
	average_x# = EntityX(mesh) - (MeshWidth(mesh)/2.0)
	average_y# = EntityY(mesh) - (MeshHeight(mesh)/2.0)
	average_z# = EntityZ(mesh) - (MeshDepth(mesh)/2.0)


	If plane = 0 Then
		For sloop = l_count To s_count
			surface = GetSurface(mesh,sloop)
			For loop = 0 To CountVertices(surface) - 1

				ny# = VertexNY (surface,loop)
				nz# = VertexNZ (surface,loop)
				nx# = VertexNX (surface,loop)
				VertexCoords surface,loop, average_x - ( average_x + VertexX(surface,loop)), VertexY(surface,loop), VertexZ(surface,loop)
				VertexNormal surface,loop,nx,ny,-nz
			Next
		Next
	ElseIf plane = 1 Then
		For sloop = l_count To s_count
			surface = GetSurface(mesh,sloop)
			For loop = 0 To CountVertices(surface) - 1

				ny# = VertexNY (surface,loop)
				nz# = VertexNZ (surface,loop)
				nx# = VertexNX (surface,loop)

				VertexCoords surface,loop, VertexX(surface,loop), average_y - ( average_y + VertexY(surface,loop)), VertexZ(surface,loop)
				VertexNormal surface,loop,-nx,ny,-nz
			Next
		Next
	ElseIf plane = 2 Then
		For sloop = l_count To s_count
			surface = GetSurface(mesh,sloop)
			For loop = 0 To CountVertices(surface) - 1

				ny# = VertexNY (surface,loop)
				nz# = VertexNZ (surface,loop)
				nx# = VertexNX (surface,loop)

				VertexCoords surface,loop, VertexX(surface,loop), VertexY(surface,loop),  average_z - ( average_z + VertexZ(surface,loop))
				VertexNormal surface,loop,-nx,ny,nz
			Next
		Next
	End If

	FlipMesh mesh
	
End Function
