; ID: 2372
; Author: elcoo
; Date: 2008-12-14 12:25:34
; Title: Mesh-Morphing
; Description: This function morphs a mesh to the shape of another one

;___________________________
;Morphing example, by Elcoo
;14.12.08
;___________________________
Graphics3D 800,600,16,2
cam=CreateCamera()
PositionEntity cam,0,0,-10
cube=LoadMesh("cube.3ds")
sphere=LoadMesh("sphere.3ds")
HideEntity sphere
preparemorph(cube,sphere)
l=CreateLight()
PositionEntity l,100,100,100
Repeat
morph(cube,0.01)
RenderWorld()
Flip
Until KeyHit(1)

Type vertexto
Field mesh
Field vertexindex
Field vertextoindex
Field surface
End Type
;This function prepares the mesh1 to be morphed to mesh2.
;It checks which vertices in mesh2 is near to the vertices in mesh
;Only call it once per mesh!
Function preparemorph(mesh1,mesh2)
surf1=GetSurface(mesh1,1)
surf2=GetSurface(mesh2,1)
vertices1=CountVertices(surf1)
vertices2=CountVertices(surf2)
If Not vertices1=vertices2 RuntimeError("Meshes may not have different vertexamount")
For i=0 To vertices1-1
a.vertexto=New vertexto
a\mesh=mesh1
a\vertexindex=i
lastdistance#=-1
a\surface=surf2
For i2=0 To vertices1-1
newdistance#=vertexdistance(surf1,i,surf2,i2)
If newdistance<lastdistance Or lastdistance=-1
lastdistance=newdistance
a\vertextoindex=i2
EndIf
Next
Next

End Function
;This function does the actual morphing.
Function morph(mesh,speed#)
surf=GetSurface(mesh,1)

For a.vertexto=Each vertexto
If a\mesh=mesh
VertexCoords surf,a\vertexindex,(VertexX(surf,a\vertexindex)+VertexX(a\surface,a\vertextoindex)*speed)/(1+speed),(VertexY(surf,a\vertexindex)+VertexY(a\surface,a\vertextoindex)*speed)/(1+speed),(VertexZ(surf,a\vertexindex)+VertexZ(a\surface,a\vertextoindex)*speed)/(1+speed)
VertexNormal surf,a\vertexindex,(VertexNX(surf,a\vertexindex)+VertexNX(a\surface,a\vertextoindex)*speed)/(1+speed),(VertexNY(surf,a\vertexindex)+VertexNY(a\surface,a\vertextoindex)*speed)/(1+speed),(VertexNZ(surf,a\vertexindex)+VertexNZ(a\surface,a\vertextoindex)*speed)/(1+speed)
VertexTexCoords surf, a\vertexindex,(VertexU(surf,a\vertexindex)+VertexU(a\surface,a\vertextoindex)*speed)/(1+speed),(VertexV(surf,a\vertexindex)+VertexV(a\surface,a\vertextoindex)*speed)/(1+speed),(VertexW(surf,a\vertexindex)+VertexW(a\surface,a\vertextoindex)*speed)/(1+speed)
EndIf
Next
End Function


Function vertexdistance#(surf1,index1,surf2,index2)
pivot1=CreatePivot()
pivot2=CreatePivot()
PositionEntity pivot1,VertexX(surf1,index1),VertexY(surf1,index1),VertexZ(surf1,index1)
PositionEntity pivot2,VertexX(surf2,index2),VertexY(surf2,index2),VertexZ(surf2,index2)
distance#=EntityDistance(pivot1,pivot2)
FreeEntity pivot1
FreeEntity pivot2
Return distance
End Function
