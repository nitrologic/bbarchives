; ID: 1930
; Author: Chroma
; Date: 2007-02-19 05:36:23
; Title: FindChild command replacement
; Description: Works will all model file formats including .b3d.

;sample use
truck = LoadAnimMesh("F100.b3d")
tire1 = FindChild(truck,"left_wheel")
HideEntity tire1

Function FindChild(mesh,mesh_name$)
	Local num% = CountChildren(mesh)
	For count = 1 To num
		tempmesh = GetChild(mesh,count)
		If EntityName(tempmesh) = mesh_name Return tempmesh
	Next
	Return 0
End Function
