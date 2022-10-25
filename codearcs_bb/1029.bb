; ID: 1029
; Author: elias_t
; Date: 2004-05-14 06:20:52
; Title: Ray Box Intersection
; Description: Ray Box Intersection function

;Ray box intersection function:
;
;rox,roy,roz = ray start point
;rdx,rdy,rdz = ray destination point
;
;bminx,bminy,bminz = box min corner
;bmaxx,bmaxy,bmaxz = box max corner

Function raybox(rox#,roy#,roz#, rdx#,rdy#,rdz#, bminx#,bminy#,bminz#, bmaxx#,bmaxy#,bmaxz#)

Local txmin#,txmax#,tymin#,tymax#

Local ddx#=1.0/(rox-rdx)
Local ddy#=1.0/(roy-rdy)

If ddx >= 0
txmin = (bminx - rox) * ddx
txmax = (bmaxx - rox) * ddx
Else
txmin = (bmaxx - rox) * ddx
txmax = (bminx - rox) * ddx
EndIf
 
If ddy >= 0
tymin = (bminy - roy) * ddy
tymax = (bmaxy - roy) * ddy
Else
tymin = (bmaxy - roy) * ddy
tymax = (bminy - roy) * ddy
EndIf

If ( (txmin > tymax) Or (tymin > txmax) ) Return 0

If (tymin > txmin) txmin = tymin
If (tymax < txmax) txmax = tymax

Local tzmin#,tzmax#
Local ddz#=1.0/(roz-rdz)

If ddz >= 0
tzmin = (bminz - roz) * ddz
tzmax = (bmaxz - roz) * ddz
Else
tzmin = (bmaxz - roz) * ddz
tzmax = (bminz - roz) * ddz
EndIf

If (txmin > tzmax) Or (tzmin > txmax) Return 0

Return 1

End Function












;EXAMPLE

Graphics3D 640,480,32,2

cam=CreateCamera()
PositionEntity cam,0,5,-10
li=CreateLight()

;start and end point of line segment
p1x#=8:p1y#=10:p1z#=-10
p2x#=-8:p2y#=-5:p2z#=45


;define the bounding box
bx#=1 ; box x min
by#=2 ; box y min
bz#=3 ; box z min

bw#=3 ; box width
bh#=4 ; box height
bd#=5 ; box depth

bxm#=bx+bw ; box x max
bym#=by+bh ; box y max
bzm#=bz+bd ; box z max


box=CreateCube()
EntityColor box,0,255,100
EntityFX box,16
EntityAlpha box,.7
FitMesh box, bx,by,bz, bw,bh,bd


;visual ray
m=CreateMesh()
s=CreateSurface(m)
v0=AddVertex(s,v0x,v0y,v0z)
v1=AddVertex(s,v1x,v1y,v1z)
v2=AddVertex(s,v2x,v2y,v2z)
AddTriangle (s,v0,v2,v1)
EntityColor m,255,255,0
EntityFX m,16
VertexCoords(s,0,p1x+.15,p1y,p1z)
VertexCoords(s,1,p2x,p2y,p2z)
VertexCoords(s,2,p1x-.15,p1y,p1z)
UpdateNormals m



PositionEntity cam,10,15,-10
PointEntity cam ,m


While Not KeyHit(1)

RenderWorld()

If KeyDown(200) Then VertexCoords(s,1,VertexX(s,1),VertexY(s,1)+.1,VertexZ(s,1))
If KeyDown(208) Then VertexCoords(s,1,VertexX(s,1),VertexY(s,1)-.1,VertexZ(s,1))

If KeyDown(203) Then VertexCoords(s,1,VertexX(s,1)-.1,VertexY(s,1),VertexZ(s,1))
If KeyDown(205) Then VertexCoords(s,1,VertexX(s,1)+.1,VertexY(s,1),VertexZ(s,1))

If KeyDown(201) Then VertexCoords(s,1,VertexX(s,1),VertexY(s,1),VertexZ(s,1)+.1)
If KeyDown(209) Then VertexCoords(s,1,VertexX(s,1),VertexY(s,1),VertexZ(s,1)-.1)

dx#=VertexX(s,1)
dy#=VertexY(s,1)
dz#=VertexZ(s,1)

res=raybox(p1x,p1y,p1z, dx,dy,dz, bx,by,bz, bxm,bym,bzm)

Text 0,0,"Arrow Keys = Move ray Destination"
Text 10,10,res

Flip

Wend


End
