; ID: 1544
; Author: KimoTech
; Date: 2005-11-27 06:53:49
; Title: PickedVertex()
; Description: Returns the nearest picked vertex

; I havent tested it much yet, so probertly some bugs :)

Function PickedVertex()
surf=PickedSurface()
tri=PickedTriangle()
v1=TriangleVertex(surf,tri,1)
v2=TriangleVertex(surf,tri,2)
v3=TriangleVertex(surf,tri,3)

Dist1X#=Abs(VertexX#(surf,v1)-PickedX#())
Dist1Y#=Abs(VertexY#(surf,v1)-PickedY#())
Dist1Z#=Abs(VertexZ#(surf,v1)-PickedZ#())
Dist1#=Dist1X#+Dist1Y#+Dist1Z#

Dist2X#=Abs(VertexX#(surf,v2)-PickedX#())
Dist2Y#=Abs(VertexY#(surf,v2)-PickedY#())
Dist2Z#=Abs(VertexZ#(surf,v2)-PickedZ#())
Dist2#=Dist2X#+Dist2Y#+Dist2Z#

Dist3X#=Abs(VertexX#(surf,v3)-PickedX#())
Dist3Y#=Abs(VertexY#(surf,v3)-PickedY#())
Dist3Z#=Abs(VertexZ#(surf,v3)-PickedZ#())
Dist3#=Dist3X#+Dist3Y#+Dist3Z#

If Dist1#<Dist2# And Dist1#<Dist3# Then Return v1
If Dist2#<Dist1# And Dist2#<Dist3# Then Return v2
If Dist3#<Dist2# And Dist3#<Dist1# Then Return v3
Return v1

End Function
