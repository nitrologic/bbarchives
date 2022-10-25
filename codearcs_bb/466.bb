; ID: 466
; Author: simonh
; Date: 2002-10-21 18:54:25
; Title: TriangleNX#(), TriangleNY#(), TriangleNZ#()
; Description: Returns the x, y, z normals of a particular triangle

Function TriangleNX#(surf,tri_no)

	v0=TriangleVertex(surf,tri_no,0)
	v1=TriangleVertex(surf,tri_no,1)
	v2=TriangleVertex(surf,tri_no,2)
	
	ax#=VertexX#(surf,v1)-VertexX#(surf,v0)
	ay#=VertexY#(surf,v1)-VertexY#(surf,v0)
	az#=VertexZ#(surf,v1)-VertexZ#(surf,v0)
	
	bx#=VertexX#(surf,v2)-VertexX#(surf,v1)
	by#=VertexY#(surf,v2)-VertexY#(surf,v1)
	bz#=VertexZ#(surf,v2)-VertexZ#(surf,v1)
	
	nx#=(ay#*bz#)-(az#*by#)
	
	Return nx#

End Function


Function TriangleNY#(surf,tri_no)

	v0=TriangleVertex(surf,tri_no,0)
	v1=TriangleVertex(surf,tri_no,1)
	v2=TriangleVertex(surf,tri_no,2)
	
	ax#=VertexX#(surf,v1)-VertexX#(surf,v0)
	ay#=VertexY#(surf,v1)-VertexY#(surf,v0)
	az#=VertexZ#(surf,v1)-VertexZ#(surf,v0)
	
	bx#=VertexX#(surf,v2)-VertexX#(surf,v1)
	by#=VertexY#(surf,v2)-VertexY#(surf,v1)
	bz#=VertexZ#(surf,v2)-VertexZ#(surf,v1)
	
	ny#=(az#*bx#)-(ax#*bz#)
	
	Return ny#

End Function


Function TriangleNZ#(surf,tri_no)

	v0=TriangleVertex(surf,tri_no,0)
	v1=TriangleVertex(surf,tri_no,1)
	v2=TriangleVertex(surf,tri_no,2)
	
	ax#=VertexX#(surf,v1)-VertexX#(surf,v0)
	ay#=VertexY#(surf,v1)-VertexY#(surf,v0)
	az#=VertexZ#(surf,v1)-VertexZ#(surf,v0)
	
	bx#=VertexX#(surf,v2)-VertexX#(surf,v1)
	by#=VertexY#(surf,v2)-VertexY#(surf,v1)
	bz#=VertexZ#(surf,v2)-VertexZ#(surf,v1)
	
	nz#=(ax#*by#)-(ay#*bx#)
	
	Return nz#

End Function
