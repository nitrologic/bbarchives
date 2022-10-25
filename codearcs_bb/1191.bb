; ID: 1191
; Author: elias_t
; Date: 2004-11-12 06:18:04
; Title: triangle - box intersection
; Description: Checks intersection between a triangle and a box

;USE tribox2.cpp !!!!

;EXAMPLE

Graphics3D 640,480,32,2

cam=CreateCamera()
PositionEntity cam,0,5,-10
li=CreateLight()






;the bounding box
bx#=1 ; box x min
by#=2 ; box y min
bz#=3 ; box z min

bw#=3 ; box width
bh#=4 ; box height
bd#=5 ; box depth




box=CreateCube()
EntityColor box,0,255,100
EntityFX box,16
EntityAlpha box,.7

FitMesh box, bx,by,bz, bw,bh,bd




;tri visualization
p0x#=6:p0y#=10:p0z#=-10
p1x#=-8:p1y#=-5:p1z#=45;movable
p2x#=8:p2y#=10:p2z#=-10

m=CreateMesh()
s=CreateSurface(m)
v0=AddVertex(s,v0x,v0y,v0z)
v1=AddVertex(s,v1x,v1y,v1z)
v2=AddVertex(s,v2x,v2y,v2z)
AddTriangle (s,v0,v2,v1)
EntityColor m,255,255,0
EntityFX m,16
VertexCoords(s,0,p0x,p0y,p0z)
VertexCoords(s,1,p1x,p1y,p1z)
VertexCoords(s,2,p2x,p2y,p2z)
UpdateNormals m

PositionEntity cam,10,15,-10
PointEntity cam ,m



;the bank we need to pass to the dll
bank=CreateBank(15*4)

;box center
PokeFloat bank,0,bx+bw/2.0
PokeFloat bank,4,by+bh/2.0
PokeFloat bank,8,bz+bd/2.0
;box halfsize
PokeFloat bank,12,bw/2.0
PokeFloat bank,16,bh/2.0
PokeFloat bank,20,bd/2.0
;triangle vertices
;v0
PokeFloat bank,24,p0x
PokeFloat bank,28,p0y
PokeFloat bank,32,p0z
;v1
PokeFloat bank,36,p1x
PokeFloat bank,40,p1y
PokeFloat bank,44,p1z
;v2
PokeFloat bank,48,p2x
PokeFloat bank,52,p2y
PokeFloat bank,56,p2z


While Not KeyHit(1)

RenderWorld()

If KeyDown(200) VertexCoords(s,1,VertexX(s,1),VertexY(s,1)+.1,VertexZ(s,1))
If KeyDown(208) VertexCoords(s,1,VertexX(s,1),VertexY(s,1)-.1,VertexZ(s,1))

If KeyDown(203) VertexCoords(s,1,VertexX(s,1)-.1,VertexY(s,1),VertexZ(s,1))
If KeyDown(205) VertexCoords(s,1,VertexX(s,1)+.1,VertexY(s,1),VertexZ(s,1))


;update the bank for v1
PokeFloat bank,36,VertexX(s,1)
PokeFloat bank,40,VertexY(s,1)
PokeFloat bank,44,VertexZ(s,1)

res=triBox(bank)

Text 10,10,res

Flip

Wend

End
