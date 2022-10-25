; ID: 2260
; Author: Yasha
; Date: 2008-05-31 17:07:38
; Title: DSA heightmap
; Description: Another random heightmap generator

;Another DSA heightmap alg.
;Seems to give nice results
;Tap space repeatedly to generate a tile
;LArrow and RArrow to turn camera

Graphics3D 800,600,32,2
SetBuffer BackBuffer()
WireFrame 1

centrecam=CreatePivot()
camera=CreateCamera(centrecam)


;Corner seed heights
y00=5
y0S=10
yS0=5
ySS=0

hconst#=5	;Height variance constant
size=32		;Grid size (should be a 2^n)
SeedRnd MilliSecs()


landscape=CreateMesh()
tile=CreateSurface(landscape)
EntityFX landscape,16		;so you can see it properly

iterations=Log(size)/Log(2)
iterated=0

Dim xpos(4)
Dim ypos#(4)
Dim zpos(4)
Dim dn(4)

PositionEntity centrecam,size/2,0,size/2
PositionEntity camera,16,15,-30
PointEntity camera,centrecam

Dim vert(size,size)
Dim poly(2*size,2*size)

cursor=CreateSphere()
EntityColor cursor,255,0,0
ScaleEntity cursor,0.5,0.5,0.5

For l=0 To size
	For r=0 To size
		vert(l,r)=AddVertex(tile,l,0,r)
	Next
Next

For l=1 To size
	For r=1 To size
		poly(l,r)=AddTriangle(tile,vert(l-1,r-1),vert(l-1,r),vert(l,r-1))
		poly(2*l,2*r)=AddTriangle(tile,vert(l-1,r),vert(l,r),vert(l,r-1))
	Next
Next

VertexCoords tile,vert(0,0),VertexX(tile,vert(0,0)),y00,VertexZ(tile,vert(0,0))
VertexCoords tile,vert(0,size),VertexX(tile,vert(0,size)),y0S,VertexZ(tile,vert(0,size))
VertexCoords tile,vert(size,0),VertexX(tile,vert(size,0)),yS0,VertexZ(tile,vert(size,0))
VertexCoords tile,vert(size,size),VertexX(tile,vert(size,size)),ySS,VertexZ(tile,vert(size,size))

While Not KeyDown(1)
	
	TurnEntity centrecam,0,KeyDown(205)-KeyDown(203),0
	;If KeyHit(28)=1 Then wired=1-wired
	;WireFrame wired
	
	If KeyHit(57)=1 And iterated<iterations
		For n=1 To 2^iterated
			For itn=1 To 2^iterated
				xpos(0)=(size/(2^(iterated+1)))+(n-1)*(size/(2^iterated))					;get location...
				zpos(0)=(size/(2^(iterated+1)))+(itn-1)*(size/(2^iterated))					;x1z1 etc give the square locations
				xpos(1)=xpos(0):xpos(3)=xpos(0)
				zpos(2)=zpos(0):zpos(4)=zpos(0)												;x4z1, x2z1, x2z3, x4z3 give the previous
				xpos(2)=n*(size/(2^iterated)):zpos(1)=itn*(size/(2^iterated))				;diamond of which x0z0 is centre
				xpos(4)=(n-1)*(size/(2^iterated)):zpos(3)=(itn-1)*(size/(2^iterated))
				
				ypos(0)=(VertexY(tile,vert(xpos(4),zpos(1)))+VertexY(tile,vert(xpos(2),zpos(1)))+VertexY(tile,vert(xpos(2),zpos(3)))+VertexY(tile,vert(xpos(4),zpos(3))))/4
				ypos(0)=ypos(0)+hvar(hconst,iterated)
				VertexCoords(tile,vert(xpos(0),zpos(0)),xpos(0),ypos(0),zpos(0))			;centres
				
				For q=1 To 4
					dn(q)=0
					ypos(q)=0
				Next
				
				
				If zpos(1)+(zpos(1)-zpos(0))<=size Then ypos(1)=ypos(1)+VertexY(tile,vert(xpos(1),zpos(1)+(zpos(1)-zpos(0)))):dn(1)=dn(1)+1
				ypos(1)=ypos(1) + VertexY(tile,vert(xpos(4),zpos(1))) + VertexY(tile,vert(xpos(2),zpos(1))) + ypos(0)  :dn(1)=dn(1)+3
				ypos(1)=ypos(1)/dn(1);:dn(1)=0
				ypos(1)=ypos(1)+hvar(hconst,iterated)
				
				If xpos(2)+(xpos(2)-xpos(0))<=size Then ypos(2)=ypos(2)+VertexY(tile,vert(xpos(2)+(xpos(2)-xpos(0)),zpos(2))):dn(2)=dn(2)+1
				ypos(2)=ypos(2)+VertexY(tile,vert(xpos(2),zpos(1)))+VertexY(tile,vert(xpos(2),zpos(3)))+ypos(0):dn(2)=dn(2)+3
				ypos(2)=ypos(2)/dn(2);:dn(2)=0
				ypos(2)=ypos(2)+hvar(hconst,iterated)
				
				If zpos(3)-(zpos(3)-zpos(0))>=0 Then ypos(3)=ypos(3)+VertexY(tile,vert(xpos(3),zpos(3)-(zpos(3)-zpos(0)))):dn(3)=dn(3)+1
				ypos(3)=ypos(3)+VertexY(tile,vert(xpos(4),zpos(3)))+VertexY(tile,vert(xpos(2),zpos(3)))+ypos(0):dn(3)=dn(3)+3
				ypos(3)=ypos(3)/dn(3);:dn(3)=0
				ypos(3)=ypos(3)+hvar(hconst,iterated)
				
				If xpos(4)-(xpos(4)-xpos(0))>=0 Then ypos(4)=ypos(4)+VertexY(tile,vert(xpos(4)-(xpos(4)-xpos(0)),zpos(4))):dn(4)=dn(4)+1
				ypos(4)=ypos(4)+VertexY(tile,vert(xpos(4),zpos(1)))+VertexY(tile,vert(xpos(4),zpos(3)))+ypos(0):dn(4)=dn(4)+3
				ypos(4)=ypos(4)/dn(4);:dn(4)=0
				ypos(4)=ypos(4)+hvar(hconst,iterated)
				
				VertexCoords(tile,vert(xpos(1),zpos(1)),xpos(1),ypos(1),zpos(1))			;squares
				VertexCoords(tile,vert(xpos(2),zpos(2)),xpos(2),ypos(2),zpos(2))
				VertexCoords(tile,vert(xpos(3),zpos(3)),xpos(3),ypos(3),zpos(3))
				VertexCoords(tile,vert(xpos(4),zpos(4)),xpos(4),ypos(4),zpos(4))
			Next
		Next
		iterated=iterated+1
	EndIf
	
	RenderWorld

	Text 0,0,iterated
	Text 0,20,2^iterated
	Text 0,40,n
	For q=0 To 4
		Text 50,20*q,ypos(q)
		Text 200,20*q,dn(q)
	Next

Flip
Wend
End

Function hvar#(k#,itn)

	range#=(k/2^itn)
	variance#=Rnd(range*-1,range)
	Return variance

End Function
