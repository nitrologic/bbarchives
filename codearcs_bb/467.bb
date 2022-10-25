; ID: 467
; Author: simonh
; Date: 2002-10-21 18:56:33
; Title: NormaliseNormals()
; Description: Normalises all normals on a particular mesh

Function NormaliseNormals(mesh)

	Local s
	
	For s=1 To CountSurfaces(mesh)
	
		surf=GetSurface(mesh,s)
	
		For v=0 To CountVertices(surf)-1
	
			nx#=VertexNX#(surf,v)
			ny#=VertexNY#(surf,v)
			nz#=VertexNZ#(surf,v)
			
			uv#=Sqr(nx#^2+ny#^2+nz#^2)
	
			nx#=nx#/uv#
			ny#=ny#/uv#
			nz#=nz#/uv#
		
			VertexNormal surf,v,nx#,ny#,nz#
	
		Next
	
	Next

End Function
