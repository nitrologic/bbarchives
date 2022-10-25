; ID: 1845
; Author: Devils Child
; Date: 2006-10-24 22:59:59
; Title: CreateJointMesh()
; Description: Creates a joint pivot mesh

Function CreateJointMesh()
mesh = CreatePivot()
c = CreateSphere()
EntityColor c, 60, 60, 60
EntityParent c, mesh
c = CreateCube()
ScaleMesh c, 1.5, .15, .15
EntityColor c, 255, 0, 0
EntityParent c, mesh
c = CreateCube()
ScaleMesh c, .15, 1.5, .15
EntityColor c, 0, 255, 0
EntityParent c, mesh
c = CreateCube()
ScaleMesh c, .15, .15, 1.5
EntityColor c, 0, 0, 255
EntityParent c, mesh
Return mesh
End Function
