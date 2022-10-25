; ID: 382
; Author: Neo Genesis10
; Date: 2002-08-03 03:50:54
; Title: Mesh Plane
; Description: Creates a mesh plane of specified size

Dim vertex(99,99)

Function CreateMeshPlane(width, height, parent=0)
	width = width - 1
	height = height - 1

	mesh = CreateMesh()
	surface = CreateSurface(mesh)

	For x = 0 To width
		For z = 0 To height
			vertex(x,z) = AddVertex(surface,x,0,z)
		Next
	Next
	
	For x = 0 To width
		For z = 0 To height
			VertexTexCoords surface, vertex(x,z), 0, 1, 0
			VertexTexCoords surface, vertex(x,z+1), 0, 0, 0
			VertexTexCoords surface, vertex(x+1,z), 1, 1, 0
			VertexTexCoords surface, vertex(x+1,z+1), 1, 0, 0
			AddTriangle(surface, vertex(x,z), vertex(x,z+1), vertex(x+1,z+1) )
			AddTriangle(surface, vertex(x+1,z+1), vertex(x+1,z), vertex(x,z) )
		Next
	Next
	
	EntityParent mesh, parent
	Return mesh
	
End Function
