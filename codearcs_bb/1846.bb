; ID: 1846
; Author: Devils Child
; Date: 2006-10-24 23:01:32
; Title: CreatePillMesh()
; Description: Creates a capsule mesh.

Function CreatePillMesh(rad# = 1, height# = 2, seg = 8)
mesh = CreateCylinder(seg * 2)
ScaleMesh mesh, rad#, height# * .5, rad#
c = CreateSphere(seg)
ScaleMesh c, rad#, rad#, rad#
PositionMesh c, 0, height# * .5, 0
AddMesh c, mesh
FreeEntity c
c = CreateSphere(seg)
ScaleMesh c, rad#, rad#, rad#
PositionMesh c, 0, -height# * .5, 0
AddMesh c, mesh
FreeEntity c
Return mesh
End Function
