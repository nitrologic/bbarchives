; ID: 1120
; Author: Rob Farley
; Date: 2004-07-30 07:58:14
; Title: Asteroids
; Description: Make random Asteroids

; Rob Farley 2004
; rob@mentalillusion.co.uk

Graphics3D 640,480

; create asteroid sphere
asteroid=CreateSphere(8)
;EntityTexture asteroid(ast),astex

; monkey with the verts
as1=GetSurface(asteroid,1)

; record the locations of the verts
Dim vpos#(CountVertices(as1)-1,3)
For n=0 To CountVertices(as1)-1
	vpos(n,0)=VertexX(as1,n)
	vpos(n,1)=VertexY(as1,n)
	vpos(n,2)=VertexZ(as1,n)
	vpos(n,3)=0
Next

For n=0 To CountVertices(as1)-1

; change these to make it more or less messy
xm#=Rnd(-.1,.1)
ym#=Rnd(-.1,.1)
zm#=Rnd(-.1,.1)

	For nn=0 To CountVertices(as1)-1
	
	; if the vert has not been monkeyed with monkey away
	If vpos(nn,3)=0
		If vpos(n,0)=vpos(nn,0) And vpos(n,1)=vpos(nn,1) And vpos(n,2)=vpos(nn,2)
			VertexCoords as1,nn,vpos(nn,0)+xm,vpos(nn,1)+ym,vpos(nn,2)+zm
			vpos(nn,3)=1
			EndIf
		EndIf
		
	Next
Next


; draw it
camera = CreateCamera():PositionEntity camera,0,0,-5
light = CreateLight(2):PositionEntity light,1000,1000,-500

Repeat
	TurnEntity asteroid,0,1,2
	RenderWorld
	Flip
Until KeyHit(1)
