; ID: 1242
; Author: Toony
; Date: 2004-12-20 16:31:06
; Title: 3D Line With Cylinders
; Description: How To Draw 3D Lines With Cylinders

Graphics3D 600,400,32,2
SetBuffer BackBuffer()

camera=CreateCamera()
PositionEntity camera,0,1,-10

SeedRnd MilliSecs()

Repeat
If KeyDown(205)=1 Then TurnEntity camera,0,-1,0
If KeyDown(203)=1 Then TurnEntity camera,0,1,0
If KeyDown(208)=1 Then MoveEntity camera,0,0,-0.05
If KeyDown(200)=1 Then MoveEntity camera,0,0,0.05

If KeyHit(28) Then
					nx=5-Rand(10)
					ny=5-Rand(10)
					nz=5-Rand(10)
					Line(ox,oy,oz,nx,ny,nz)
					ox=nx
					oy=ny
					oz=nz
					End If


UpdateWorld
RenderWorld

Text 0,0,"Press Enter to Create a 3D Line"
Text 0,10,"Use Arrows to move arround"

Flip
Until KeyDown(1)
End





Function Line(x,y,z,x2,y2,z2)

c1=CreateSphere()
PositionEntity c1,x,y,z
ScaleEntity c1,0.1,0.1,0.1
EntityColor c1,0,0,255

c2=CreateSphere()
PositionEntity c2,x2,y2,z2
ScaleEntity c2,0.1,0.1,0.1
EntityColor c2,0,0,255

p=CreatePivot()
PositionEntity p,x,y,z

le#=EntityDistance# (c1,c2)
le#=le#-(le#/2)

c3=CreateCylinder()
PositionEntity c3,x,le#+y,z
ScaleEntity c3,0.05,le#,0.05
EntityColor c3,0,255,0

EntityParent c3,p

PointEntity p,c2
TurnEntity p,90,0,0

End Function
