; ID: 756
; Author: Jeppe Nielsen
; Date: 2003-07-31 19:11:08
; Title: Circumcenter
; Description: Find a Triangle's circumcenter in 3D

;Circumcenter by Jeppe Nielsen 2003

; Find a triangle´s circumcenter;
; a center that is equally away from all of it´s vertices

Global circumcenterx#,circumcentery#,circumcenterz#


Graphics3D 800,600,16,2
HidePointer


SeedRnd MilliSecs()


pivot=CreatePivot()
cam=CreateCamera(pivot)

PositionEntity cam,0,0,-10

Dim entity(4)

findcircumcenter(Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4))



Repeat

If KeyHit(57)

findcircumcenter(Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4),Rnd(-4,4))

EndIf


TurnEntity pivot,MouseYSpeed()/2,-MouseXSpeed()/2,0
MoveMouse 400,300

If KeyDown(200) Then MoveEntity cam,0,0,.1
If KeyDown(208) Then MoveEntity cam,0,0,-.1

RenderWorld()




Color 255,255,0
Rect 10,50,20,20
Color 0,0,255
Rect 10,80,20,20

Color 255,255,255
Text 10,10,"Mouse to rotate view"
Text 10,30,"Space to create a new triangle"
Text 40,50,"Circumcenter"
Text 40,80,"Circumsphere"

Flip

Until KeyDown(1)
End



Function findcircumcenter(x1#,y1#,z1#,x2#,y2#,z2#,x3#,y3#,z3#)

For n=0 To 4
	If entity(n)<>0

		FreeEntity entity(n)

	EndIf
Next


dx1#=(x2#-x1#)
dy1#=(y2#-y1#)
dz1#=(z2#-z1#)

dx2#=(x3#-x1#)
dy2#=(y3#-y1#)
dz2#=(z3#-z1#)

dx3#=(x3#-x2#)
dy3#=(y3#-y2#)
dz3#=(z3#-z2#)


entity(0)=CreateCylinder(3,0)
PositionMesh entity(0),0,1,0
PositionEntity entity(0),x1#,y1#,z1#
l#=Sqr(dx1*dx1+dy1*dy1+dz1*dz1)/2.0
ScaleEntity entity(0),0.01,l#,0.01
AlignToVector entity(0),dx1,dy1,dz1,2

entity(1)=CreateCylinder(3,0)
PositionMesh entity(1),0,1,0
PositionEntity entity(1),x1#,y1#,z1#
l#=Sqr(dx2*dx2+dy2*dy2+dz2*dz2)/2.0
ScaleEntity entity(1),0.01,l#,0.01
AlignToVector entity(1),dx2,dy2,dz2,2

entity(2)=CreateCylinder(3,0)
PositionMesh entity(2),0,1,0
PositionEntity entity(2),x2#,y2#,z2#
l#=Sqr(dx3*dx3+dy3*dy3+dz3*dz3)/2.0
ScaleEntity entity(2),0.01,l#,0.01
AlignToVector entity(2),dx3,dy3,dz3,2

For n=0 To 2
EntityFX entity(n),1
Next

; Start math part :-)


dx1#=(x2#-x1#)
dy1#=(y2#-y1#)
dz1#=(z2#-z1#)

dx2#=(x3#-x1#)
dy2#=(y3#-y1#)
dz2#=(z3#-z1#)

tnx# = dy1 * dz2 - dy2 * dz1
tny# = dz1 * dx2 - dz2 * dx1
tnz# = dx1 * dy2 - dx2 * dy1


sx1#=(x1#+x3#)/2.0
sy1#=(y1#+y3#)/2.0
sz1#=(z1#+z3#)/2.0

dx1#=(sx1#-x1#)
dy1#=(sy1#-y1#)
dz1#=(sz1#-z1#)

dx2#=tnx#
dy2#=tny#
dz2#=tnz#

nx# = dy1 * dz2 - dy2 * dz1
ny# = dz1 * dx2 - dz2 * dx1
nz# = dx1 * dy2 - dx2 * dy1

sx2#=sx1#+nx#
sy2#=sy1#+ny#
sz2#=sz1#+nz#

sx3#=(x1#+x2#)/2.0
sy3#=(y1#+y2#)/2.0
sz3#=(z1#+z2#)/2.0

dx1#=(sx3#-x1#)
dy1#=(sy3#-y1#)
dz1#=(sz3#-z1#)

dx2#=tnx#
dy2#=tny#
dz2#=tnz#

nx# = dy1 * dz2 - dy2 * dz1
ny# = dz1 * dx2 - dz2 * dx1
nz# = dx1 * dy2 - dx2 * dy1

sx4#=sx3#+nx#
sy4#=sy3#+ny#
sz4#=sz3#+nz#


ax#=sx2#-sx1#
ay#=sy2#-sy1#
az#=sz2#-sz1#

bx#=sx4#-sx3#
by#=sy4#-sy3#
bz#=sz4#-sz3#

cx#=sx3#-sx1#
cy#=sy3#-sy1#
cz#=sz3#-sz1#

qx1# = cy * bz - by * cz
qy1# = cz * bx - bz * cx
qz1# = cx * by - bx * cy

qx2# = ay * bz - by * az
qy2# = az * bx - bz * ax
qz2# = ax * by - bx * ay

dot#=qx1*qx2+qy1*qy2+qz1*qz2

le#=Sqr(qx2*qx2+qy2*qy2+qz2*qz2)

si#=dot#/(le#*le#)

circumcenterx#=sx1#+ax#*si#
circumcentery#=sy1#+ay#*si#
circumcenterz#=sz1#+az#*si#

; End math part :-)


entity(3)=CreateSphere(16)

PositionEntity entity(3),circumcenterx#,circumcentery#,circumcenterz#
ScaleEntity entity(3),0.05,0.05,0.05
EntityColor entity(3),255,255,0
EntityFX entity(3),1



dx#=circumcenterx#-x1#
dy#=circumcentery#-y1#
dz#=circumcenterz#-z1#

rad#=Sqr(dx*dx+dy*dy+dz*dz)

entity(4)=CreateSphere(24)
PositionEntity entity(4),circumcenterx#,circumcentery#,circumcenterz#
ScaleEntity entity(4),rad,rad,rad
EntityAlpha entity(4),0.75
EntityColor entity(4),0,0,255

EntityOrder entity(4),1

End Function
