; ID: 732
; Author: Bot Builder
; Date: 2003-07-02 02:58:47
; Title: Mid3dhandle() function
; Description: Center's an object's handle to a mesh (locational handle)

Function Mid3dHandle(mesh)
 ux#=-100000
 uy#=-100000
 uz#=-100000
 lx#=100000
 ly#=100000
 lz#=100000
 cs=CountSurfaces(mesh)
 For s=1 To cs
  surf=GetSurface(mesh,s)
  cv=CountVertices(surf)-1
  For v=0 To cv
   vx#=VertexX#(surf,v)
   vy#=VertexY#(surf,v)
   vz#=VertexZ#(surf,v)
   If vx#<lx# Then lx#=vx#
   If vx#>ux# Then ux#=vx#
   If vy#<ly# Then ly#=vy#
   If vy#>uy# Then uy#=vy#
   If vz#<lz# Then lz#=vz#
   If vz#>uz# Then uz#=vz#
  Next
 Next
 ax#=(ux#+lx#)/2
 ay#=(uy#+ly#)/2
 az#=(uz#+lz#)/2
 PositionMesh mesh,-ax#,-ay#,-az#
End Function
