; ID: 838
; Author: patisawesome
; Date: 2003-11-23 18:50:37
; Title: AI code UPDATED
; Description: another AI ball

Graphics3D 640,480,16,1

Collisions 1,2,2,2
Collisions 1,3,2,2
Collisions 4,2,2,2
Collisions 4,3,2,2

cam=CreateCamera()
CameraClsColor cam,100,250,255
PositionEntity cam,0,100,0
CameraRange cam,0.1,45000
CameraViewport cam,320,0,320,240

enemy=CreateCube()
PositionEntity enemy,0,5,0
ScaleEntity enemy,1,1,1
EntityType enemy,3
EntityColor enemy,0,0,0
EntityRadius enemy,1

p=CreateSphere()
PositionEntity p,0,-1,4
PointEntity cam,p
EntityType p,1
PointEntity enemy,p
EntityColor p,0,0,255

walls=LoadMesh("aimaze1.x")
PositionEntity walls,0,-3,0
EntityType walls,2
ScaleEntity walls,2,2,2

l=CreateLight()
TurnEntity l,45,45,0

c=CreateCamera()
PositionEntity c,EntityX(p),EntityY(p),EntityZ(p)
CameraRange c,0.01,45000
CameraViewport c,0,240,320,240

While Not KeyHit(1)

PositionEntity c,EntityX(p),EntityY(p),EntityZ(p)

PointEntity enemy,p
MoveEntity p,0,0,0.5
MoveEntity enemy,0,0,0.1

If EntityCollided(p,2)
TurnEntity p,0,98,0
PointEntity enemy,p
TurnEntity c,0,98,0
EndIf

If EntityCollided(p,3)
EntityColor p,250,10,10
MoveEntity p,0,0,1
EndIf

If Not EntityCollided(p,3)
EntityColor p,0,0,255
EndIf

UpdateWorld
RenderWorld
Flip
Wend

End
