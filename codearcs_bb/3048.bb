; ID: 3048
; Author: _PJ_
; Date: 2013-03-30 23:19:34
; Title: Combine Surface Verts
; Description: Combines Surface Vertices & Triangles

Function CombineSurfaces(SourceSurface,DestinationSurface)
	Local Iter
	Local vX#
	Local vY#
	Local vZ#
	Local vU#
	Local vV#
	Local vW#
	Local vA#
	Local vR
	Local vG
	Local vB
	
	Local v
	Local v0
	Local v1
	Local v2
	Local vCount=CountVertices(DestinationSurface)
	
	Local Vert
	
	For Iter=1 To CountTriangles(SourceSurface)
		For Vert=0 To 2
			v=TriangleVertex(SourceSurface,Iter,Vert)
		
			vX=VertexX(SourceSurface,v)
			vY=VertexY(SourceSurface,v)
			vZ=VertexZ(SourceSurface,v)
		
			vU=VertexU(SourceSurface,v)
			vV=VertexV(SourceSurface,v)
			vW=VertexW(SourceSurface,v)
		
			vR=VertexRed(SourceSurface,v)
			vG=VertexGreen(SourceSurface,v)
			vB=VertexBlue(SourceSurface,v)
			vA=VertexAlpha(SourceSurface,v)
			
			Select (Vert)
				Case 1:
					v1=AddVertex(DestinationSurface,vX,vY,vZ,vU,vV,vW)
				Case 2:
					v2=AddVertex(DestinationSurface,vX,vY,vZ,vU,vV,vW)
				Default:
					v0=AddVertex(DestinationSurface,vX,vY,vZ,vU,vV,vW)
			End Select
			vCount=vCount+1
			VertexColor DestinationSurface,vCount,vR,vG,vB,vA			
		Next
		AddTriangle(DestinationSurface,v0,v1,v2)
	Next
End Function
