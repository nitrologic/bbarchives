; ID: 437
; Author: skidracer
; Date: 2002-09-27 01:00:20
; Title: smoke
; Description: single surface particle system

; smoke.bb
; by simon@acid.co.nz

; 2003.7.15 fixed global cam variable and added camera rot test on mousex

; demonstrates particle system using single surface of dynamic quads 

; quads use position of particle and angle of camera to rebuild their
; billboard every update facing the camera with only 4 calls to vertexcoords

; commented code

; faster system may be possible placing textures in triangles instead of quads
; animation of particles is possible by scrolling through frames in texture 

; mousebutton to add smoke
; mousey to zoom in

Graphics3D 800,600

Global cam=CreateCamera()

PositionEntity cam,0,25,-40

d.smoke=CreateSmoke()

While Not KeyHit(1)

	rot#=rot#+MouseXSpeed()
	PositionEntity cam,0,25,0
	RotateEntity cam,10,rot,0
	MoveEntity cam,0,0,-MouseY()
	
;	If MouseDown(1) AddSmoke(d,5, 0,1,0,.01,Rnd(-.1,.1),Rnd(.2,.25),Rnd(-.1,.1), -1,600)
	If MouseDown(1) AddSmoke(d,1, 0,1,0,.04 ,Rnd(-.1,.1),Rnd(.2,.25),Rnd(-.1,.1), -1,600)

	UpdateWorld
	UpdateSmoke(d)
	RenderWorld
	Flip
Wend

End

Const MAXDIRT=4096		;4096			;512

Type vector
	Field	x#
	Field	y#
	Field	z#
End Type

Type smoke
;	Field	node.node
	Field	mesh
	Field	surf
	Field	cache.asmoke
	Field	pos.asmoke
	Field	texture
	Field	smokex#
End Type

Type asmoke
	Field	link.asmoke
	Field	index
	Field	time
	Field	rgb
	Field	pos.vector
	Field	vel.vector
	Field	size#,vsize#
End Type

Function CreateSmoke.smoke();parent.node)
	d.smoke=New smoke
	brush=CreateBrush()	
	dust=LoadTexture("smoke.bmp",2)
	BrushTexture brush,dust
	BrushFX brush,3
	BrushBlend brush,1
	d\texture=dust
	d\mesh=CreateMesh()
	d\surf=CreateSurface(d\mesh,brush)
	For i=1 To MAXDIRT
		MakeSmoke(d)
	Next
	FreeBrush brush
	Return d
End Function

Function DestroySmoke(d.smoke)
	FreeEntity d\mesh
	Delete d
End Function

Function UpdateSmoke(d.smoke)
;	PositionTexture d\texture,d\smokex,0	//cycle 32 frames 
;	d\smokex=d\smokex+1.0/32
	surf=d\surf
	a.asmoke=d\cache
	TFormVector -1,0,0,cam,0
	camxx#=TFormedX()
	camxy#=TFormedY()
	camxz#=TFormedZ()
	TFormVector 0,-1,0,cam,0
	camyx#=TFormedX()
	camyy#=TFormedY()
	camyz#=TFormedZ()
	While a<>Null
		If a\time>0
			a\time=a\time-1
			a\vel\y=a\vel\y-GRAVITY/2
;			AddVector a\pos,a\vel
			p.vector=a\pos
			p\x=p\x+a\vel\x
			p\y=p\y+a\vel\y
			p\z=p\z+a\vel\z
			a\size=a\size+a\vsize
			If a\time=0 Or a\pos\y<0
				HideSmoke(d,a)
			Else
				scale#=a\size
				cxx#=camxx*scale
				cxy#=camxy*scale
				cxz#=camxz*scale
				cyx#=camyx*scale
				cyy#=camyy*scale
				cyz#=camyz*scale
				i=a\index

				x#=p\x-cyx+cxx
				y#=p\y-cyy+cxy
				z#=p\z-cyz+cxz
				VertexCoords surf,i,x,y,z
				x#=p\x-cyx-cxx
				y#=p\y-cyy-cxy
				z#=p\z-cyz-cxz
				VertexCoords surf,i+1,x,y,z
				x#=p\x+cyx-cxx
				y#=p\y+cyy-cxy
				z#=p\z+cyz-cxz
				VertexCoords surf,i+2,x,y,z
				x#=p\x+cyx+cxx
				y#=p\y+cyy+cxy
				z#=p\z+cyz+cxz
				VertexCoords surf,i+3,x,y,z
				
				
;				x#=p\x-cyx
;				y#=p\y-cyy
;				z#=p\z-cyz
;				VertexCoords surf,i,x,y,z
;				x=x+cyx-cxx
;				y=y+cyy-cxy
;				z=z+cyz-cxz
;				VertexCoords surf,i+1,x,y,z
;				x=x+cxx+cxx
;				y=y+cxy+cxy
;				z=z+cxz+cxz
;				VertexCoords surf,i+2,x,y,z
			EndIf
		EndIf
		a=a\link
	Wend
End Function

Function HideSmoke(d.smoke,a.asmoke)
	surf=d\surf
	i=a\index
;	VertexCoords surf,i,0,-500000,0
;	VertexCoords surf,i+1,0,-500000,0
;	VertexCoords surf,i+2,0,-500000,0
	VertexCoords surf,i,0,-500000,0
	VertexCoords surf,i+1,0,-500000,0
	VertexCoords surf,i+2,0,-500000,0
	VertexCoords surf,i+3,0,-500000,0
	a\time=0
End Function

Function MakeSmoke(d.smoke)
	a.asmoke=New asmoke
	a\link=d\cache
	d\cache=a
	s=d\surf

	i=AddVertex(s,0,-50000,0,0,0)
	AddVertex(s,0,-50000,0,1,0)
	AddVertex(s,0,-50000,0,1,1)
	AddVertex(s,0,-50000,0,0,1)

	AddTriangle s,i,i+1,i+2
	AddTriangle s,i,i+2,i+3

;	i=AddVertex(s,0,-50000,0,0.5,-.4)
;	AddVertex(s,0,-50000,0,1.4,1)
;	AddVertex(s,0,-50000,0,-.4,1)
;	AddTriangle s,i,i+1,i+2

	a\index=i
	a\time=0
	a\pos=New vector
	a\vel=New vector
End Function

Function AddSmoke(s.smoke,size#,x#,y#,z#,vsize#,vx#,vy#,vz#,rgb,t)
	a.asmoke=s\pos
	If a=Null a=s\cache 
	s\pos=a\link
	a\pos\x=x
	a\pos\y=y
	a\pos\z=z
	a\vel\x=vx
	a\vel\y=vy
	a\vel\z=vz
	a\rgb=rgb
	r=(rgb Shr 16) And 255
	g=(rgb Shr 8) And 255
	b=rgb And 255
	surf=s\surf
	i=a\index
	VertexColor surf,i+0,r,g,b 
	VertexColor surf,i+1,r,g,b 
	VertexColor surf,i+2,r,g,b 
	a\time=t
	a\size=size
	a\vsize=vsize
End Function

Function CheckSmoke.asmoke(a.asmoke,cx#,cy#,cz#,rr#)
	While a<>Null
		If a\time>0
			p.vector=a\pos
			x#=a\pos\x-cx
			y#=a\pos\y-cy
			z#=a\pos\z-cz
			If x*x+y*y+z*z<rr Return a
		EndIf
		a=a\link
	Wend
End Function
