; ID: 2711
; Author: GIB3D
; Date: 2010-05-08 19:27:06
; Title: ColorMesh and ColorSurface
; Description: Works like EntityColor and EntityAlpha except on the actual mesh or a single surface on the mesh.

Function ColorMesh(mesh,red#,green#,blue#,alpha#=1)
	Local Surface,Surfaces,Vertices
	
	Surfaces = CountSurfaces(mesh)
	
	For s = 1 To Surfaces
		Surface = GetSurface(mesh,s)
		ColorSurface(Surface,red,green,blue,alpha)
	Next
End Function

Function ColorSurface(surface,red#,green#,blue#,alpha#=1)
	Local Surfaces,Vertices
	
	Vertices = CountVertices(surface)
	
	For v = 0 To Vertices - 1
		VertexColor surface,v,red,green,blue,alpha
	Next
End Function
