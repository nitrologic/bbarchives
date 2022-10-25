; ID: 2219
; Author: ringwraith
; Date: 2008-02-19 04:16:47
; Title: TriSteepness
; Description: calculates the steepness of a triangle

Function TriSteepness#(surface,index)

	vert0 = TriangleVertex(surface,index,0)
	vert1 = TriangleVertex(surface,index,1)
	vert2 = TriangleVertex(surface,index,2)
	vert0to1# = VertSteepness#(surface,TriangleVertex(surface,index,0),surface,TriangleVertex(surface,index,1))
	vert0to2# = VertSteepness(surface,TriangleVertex(surface,index,0),surface,TriangleVertex(surface,index,2))
	vert1to2# = VertSteepness#(surface,TriangleVertex(surface,index,1),surface,TriangleVertex(surface,index,2))
	If vert0to1# => vert0to2# And vert0to1# => vert1to2# Then Return vert0to1#
	If vert0to2# => vert0to1# And vert0to2# => vert1to2# Then Return vert0to2#
	If vert1to2# => vert0to2# And vert1to2# => vert0to1# Then Return vert1to2#
	
End Function 

Function VertDist#(surface,index,x#,Z#)

	xdist# = VertexX(surface,index) - x
	Zdist# = VertexZ(surface,index) - Z
	
	Dist# = Sqr(Xdist^2 + Zdist^2)
	
	Return Dist#
	
End Function

Function VertSteepness#(surface,index,surface2,index2)

	vertgroundist# = VertDist#(surface,index,VertexX(surface2,index2),VertexZ(surface2,index2))
	vertupdist# = Abs(VertexY(surface2,index2) - VertexY(surface,index))
	slope# = vertupdist#/vertgroundist#
 
	Return slope#
	
End Function
