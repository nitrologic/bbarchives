; ID: 1033
; Author: Bug Face
; Date: 2004-05-18 10:48:39
; Title: .ASC Mesh Exporter
; Description: Saves Multi-Surface Meshes to .ASC format

Function export_asc(mesh,file$)
	check=CountSurfaces(mesh) 
	If check=0 Then Return 
	
	totalVertices=0
	totalPolygons=0
	
	For surface=1 To CountSurfaces(mesh)
		surf=GetSurface(mesh,surface)
		totalVertices=totalVertices+CountVertices(surf)
		totalPolygons=totalPolygons+CountTriangles(surf)
	Next
	
	surf=GetSurface(mesh,1) 
	
	;start writing the file
	out=WriteFile(file$) 
	WriteLine out,"Ambient light color: Red=0.5 Green=0.5 Blue=0.5"
	WriteLine out,""
	WriteLine out,"Named object: "+Chr$(34)+"untitled"+Chr$(34)
	WriteLine out,"Tri-mesh, Vertices: "+totalVertices+" Faces: "+totalPolygons
	WriteLine out,"Vertex list:"
	
	;Vertices
	For surface=1 To CountSurfaces(mesh)
		surf=GetSurface(mesh,surface)
		For a=0 To CountVertices(surf)-1 
			WriteLine out,"Vertex "+a+": X:"+VertexX(surf,a)+" Y:"+VertexY(surf,a)+" Z:"+VertexZ(surf,a)+";," 
		Next 
	Next
	
	;Faces
	WriteLine out,"Face list:"
	baseVertex=0
	For surface=1 To CountSurfaces(mesh)
		surf=GetSurface(mesh,surface)
		For a=0 To CountTriangles(surf)-1
			WriteLine out,"Face "+a+": A:"+(TriangleVertex(surf,a,0)+baseVertex)+" B:"+(TriangleVertex(surf,a,1)+baseVertex)+" C:"+(TriangleVertex(surf,a,2)+baseVertex)+" AB:1 BC:1 CA:1"
			WriteLine out,"Smoothing: 1"
		Next
		baseVertex=baseVertex+CountVertices(surf)
	Next
	
	CloseFile out
End Function
