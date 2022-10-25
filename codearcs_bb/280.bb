; ID: 280
; Author: Chroma
; Date: 2002-03-23 19:34:32
; Title: Save mesh to .asc file format
; Description: This saves a mesh to .asc file format.  

;.asc file exporter
;by Chroma

Function export_asc(mesh,file$)

check=CountSurfaces(mesh) 
If check=0 Then Return 

surf=GetSurface(mesh,1) 

;start writing the file
out=WriteFile(file$) 
WriteLine out,"Ambient light color: Red=0.5 Green=0.5 Blue=0.5"
WriteLine out,""
WriteLine out,"Named object: "+Chr$(34)+"untitled"+Chr$(34)
WriteLine out,"Tri-mesh, Vertices: "+CountVertices(surf)+" Faces: "+CountTriangles(surf)
WriteLine out,"Vertex list:"

;Vertices
For a=0 To CountVertices(surf)-1 
WriteLine out,"Vertex "+a+": X:"+VertexX(surf,a)+" Y:"+VertexY(surf,a)+" Z:"+VertexZ(surf,a)+";," 
Next 

;Faces
WriteLine out,"Face list:"
For a=0 To CountTriangles(surf)-1
WriteLine out,"Face "+a+": A:"+TriangleVertex(surf,a,0)+" B:"+TriangleVertex(surf,a,1)+" C:"+TriangleVertex(surf,a,2)+" AB:1 BC:1 CA:1"
WriteLine out,"Smoothing: 1"
Next

CloseFile out
End Function
