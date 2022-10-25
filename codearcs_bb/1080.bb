; ID: 1080
; Author: Mr Snidesmin
; Date: 2004-06-07 22:20:57
; Title: Removing Triangles and Vertices
; Description: Individually remove tris & verts from a surface.

Function VertexIsUsed(surface%, vi%)
	For i% = 0 To CountTriangles(surface) - 1
		If TriangleVertex(surface, i, 0) = vi Then Return True
		If TriangleVertex(surface, i, 1) = vi Then Return True
		If TriangleVertex(surface, i, 2) = vi Then Return True
	Next
	Return False
End Function

Function ClearUnusedVertices%(surface%)
	For i% = 0 To CountVertices(surface) - 1
		If Not VertexIsUsed(surface, i) Then
			RemoveVertex surface, i
		End If
	Next
End Function


Function RemoveTriangle(surface%, triangle_index%)
	s0% = surface
	m% = CreateMesh()
	s% = CreateSurface(m)
	
	;copy, excluding triangle:
	For i% = 0 To CountVertices(s0)-1
		vi% = AddVertex(s, VertexX(s0, i),VertexY(s0, i),VertexZ(s0, i),VertexU(s0, i),VertexV(s0, i),VertexW(s0, i))
		VertexNormal s, vi, VertexNX(s0, i), VertexNY(s0, i), VertexNZ(s0, i)
		VertexColor s, vi, VertexRed(s0, i), VertexGreen(s0, i), VertexBlue(s0, i), VertexAlpha(s0, i)
	Next
	For i% = 0 To CountTriangles(s0)-1
		If i <> triangle_index Then
			AddTriangle s, TriangleVertex(s0, i, 0), TriangleVertex(s0, i, 1), TriangleVertex(s0, i, 2)
		End If
	Next
	
	;copy back
	CopySurface s, s0
	FreeEntity m
End Function


Function RemoveVertex(surface%, vertex_index%)
	s0% = surface
	m% = CreateMesh()
	s% = CreateSurface(m)
	
	;copy, excluding vertex:
	For i% = 0 To CountVertices(s0)-1
		If i <> vertex_index Then
			vi% = AddVertex(s, VertexX(s0, i),VertexY(s0, i),VertexZ(s0, i),VertexU(s0, i),VertexV(s0, i),VertexW(s0, i))
			VertexNormal s, vi, VertexNX(s0, i), VertexNY(s0, i), VertexNZ(s0, i)
			VertexColor s, vi, VertexRed(s0, i), VertexGreen(s0, i), VertexBlue(s0, i), VertexAlpha(s0, i)
		End If
	Next
	For i% = 0 To CountTriangles(s0)-1
		v0% = TriangleVertex(s0, i, 0)
		v1% = TriangleVertex(s0, i, 1)
		v2% = TriangleVertex(s0, i, 2)
		
		If v0 > vertex_index Then v0 = v0 - 1
		If v1 > vertex_index Then v1 = v1 - 1
		If v2 > vertex_index Then v2 = v2 - 1
		AddTriangle s, v0, v1, v2
	Next
	
	;copy back
	CopySurface s, s0
	FreeEntity m
End Function



Function CopySurface(source%, dest%, clear_unused_vertices%=False)
	s0% = source
	s% = dest
	ClearSurface s
	
	For i% = 0 To CountVertices(s0)-1
		vi% = AddVertex(s, VertexX(s0, i),VertexY(s0, i),VertexZ(s0, i),VertexU(s0, i),VertexV(s0, i),VertexW(s0, i))
		VertexNormal s, vi, VertexNX(s0, i), VertexNY(s0, i), VertexNZ(s0, i)
		VertexColor s, vi, VertexRed(s0, i), VertexGreen(s0, i), VertexBlue(s0, i), VertexAlpha(s0, i)
	Next
	For i% = 0 To CountTriangles(s0)-1
		AddTriangle s, TriangleVertex(s0, i, 0), TriangleVertex(s0, i, 1), TriangleVertex(s0, i, 2)
	Next
End Function
