; ID: 347
; Author: elias_t
; Date: 2002-06-17 20:52:20
; Title: Chrysanthemum &amp; Butterfly Curves
; Description: Chrysanthemum & Butterfly Curves drawed using triangles

.start
Cls

a$ = Input ("Select curve: 1=Chrysanthemum, 2=Butterfly : ")


If a$="1"
Goto chrysanthemum

Else If a$="2"
Goto butterfly

Else Goto start

EndIf


;----------------------
.chrysanthemum
Graphics3D 640,480,32,2

SetBuffer BackBuffer() 

cam=CreateCamera()
CameraClsColor cam,18,25,36
light=CreateLight()

;-------------------------------------------------------------------------

mesh=CreateMesh()
surf=CreateSurface(mesh)

EntityFX mesh,18

MoveEntity cam,8,8,8
PointEntity cam,mesh

n=10000 

For i=0 To n

u# = i * 21.0 * 180 / n
p4# = Sin(17.0 * u# / 3.0)
p8#= Sin(2.0 * Cos(3.0 * u#) - 28.0 * u#)
r#= 5.0*(1.0 + Sin(11.0*u#/5.0)) - 4.0*p4#^ 4* p8#^8

p_x# = r# * Cos(u#) 
p_y# = r# * Sin(u#)
p_z# = (r#/20.0+0.2)*Sin(r#*2*180/7.0)

v0=AddVertex (surf,p_x#,p_y#,p_z#)
v1=AddVertex (surf,p_x#+.02,p_y#+.02,p_z#+.02)
v2=AddVertex (surf,plast_x#,plast_y#,plast_z#)

AddTriangle (surf,v0,v1,v2)

r=u*155/(21*180)
g=(255-r)
b=r+128
If b>255 Then b=b-255

VertexColor surf,v0,r,g,b
VertexColor surf,v1,r,g,b
VertexColor surf,v2,r,g,b

plast_x# = p_x#
plast_y# = p_y#
plast_z# = p_z#
Next

While Not KeyHit(1)
UpdateWorld

RenderWorld

TurnEntity mesh,.2,.2,.2

Text 10,10,"tris:"+TrisRendered()
Flip

Wend

End



;-------------------------------------------------------------------------

.butterfly

Graphics3D 640,480,32,2

SetBuffer BackBuffer() 

cam=CreateCamera()
CameraClsColor cam,18,25,36
light=CreateLight()


mesh=CreateMesh()
surf=CreateSurface(mesh)

EntityFX mesh,18

MoveEntity cam,4,4,4
PointEntity cam,mesh

n=10000 

For i=0 To n

u# = i * 24.0 * 180 / n
 p_x# = Cos(u#) * (Exp(Cos(u#)) - 2.0 * Cos(4.0 * u#) - (Sin(u# / 12.0)^5.0));
 p_y# = Sin(u#) * (Exp(Cos(u#)) - 2.0 * Cos(4.0 * u#) - (Sin(u# / 12.0)^5.0));
 p_z# = Abs(p_y#) / 2.0 

v0=AddVertex (surf,p_x#,p_y#,p_z#)
v1=AddVertex (surf,p_x#+.02,p_y#+.02,p_z#+.02)
v2=AddVertex (surf,plast_x#,plast_y#,plast_z#)

AddTriangle (surf,v0,v1,v2)

VertexColor surf,v0,Rnd(255),Rnd(255),0
VertexColor surf,v1,Rnd(255),Rnd(255),0
VertexColor surf,v2,Rnd(255),Rnd(255),0

plast_x# = p_x#
plast_y# = p_y#
plast_z# = p_z#
Next

While Not KeyHit(1)
UpdateWorld

RenderWorld

TurnEntity mesh,.2,.2,.2

Text 10,10,TrisRendered()
Flip

Wend


End
