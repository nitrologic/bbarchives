; ID: 1178
; Author: puki
; Date: 2004-10-24 16:00:53
; Title: Mesh builder
; Description: Builds a flat mesh plane

; mesh builder by "puki" - 24/10/04

Graphics3D 640,480,16,2 
SetBuffer BackBuffer() 

cam = CreateCamera() 
MoveEntity cam, 0,0,-5

WireFrame 1

mesh = CreateMesh()
surface=CreateSurface(mesh)

; here we set the size of the mesh (in squares)
; both values must each be at least 1 - they do not have to be matching values
mwidth=8
mheight=6

; there will only be 4 vertices per square - 0 to 3
; we re-use the array cells repeatedly to save memory
Dim vert_no(3)

; the following code draws each section of the mesh from left to right
; then it starts a new row below the first and again works along left to right
For height=1 To mheight
	For width=1 To mwidth
			vert_no(0)=AddVertex(surface,curx,cury-1,curz)
			vert_no(1)=AddVertex(surface,curx,cury,curz)
			vert_no(2)=AddVertex(surface,curx+1,cury,curz)
			vert_no(3)=AddVertex(surface,curx+1,cury-1,curz)
		
			tri1=AddTriangle(surface,vert_no(0),vert_no(1),vert_no(2))
			tri2=AddTriangle(surface,vert_no(0),vert_no(2),vert_no(3))
			curx=curx+1
	Next
	curx=0
	cury=cury-1
Next

UpdateNormals mesh
PositionMesh mesh,-4,3,0

While Not KeyHit(1)
RenderWorld
Flip 
Wend
End
