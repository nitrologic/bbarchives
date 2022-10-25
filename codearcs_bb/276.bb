; ID: 276
; Author: David Bird(Birdie)
; Date: 2002-03-21 17:51:42
; Title: Cylinderical Mapping
; Description: Creates uv coords for a mesh

;Cylinderical Mapping
;enquire@davebird.fsnet.co.uk
;www.davebird.fsnet.co.uk

Function Apply_CylWrap(Mesh,axis,scaleu#=1,scalev#=1,ou#=0,ov#=0)
For s=1 To CountSurfaces(mesh)
surf=GetSurface(mesh,s)
For a=0 To CountVertices(surf)-1
x#=VertexX(surf,a)
y#=VertexY(surf,a)
z#=VertexZ(surf,a)
Select axis
Case 1;X axis
u#=((scaleu/360.0)*ATan(y/z))+ou
v#=(scalev*x)+ov
Case 2;Y axis
u#=((scaleu/360.0)*ATan(x/z))+ou
v#=(scalev*y)+ov
Case 3;Z Axis
u#=((scaleu/360.0)*ATan(x/y))+ou
v#=(scalev*z)+ov
End Select
VertexTexCoords surf,a,u,v
Next
Next
End Function
