; ID: 116
; Author: David Bird(Birdie)
; Date: 2001-10-28 15:14:07
; Title: CreateSegCube([segs],[parent])
; Description: Creates a segmented cube.

;	Six surfaced multi/segmented cube
;	David Bird
;	dave@birdie72.freeserve.co.uk
;
Graphics3D 640,480
SetBuffer BackBuffer()


cam=CreateCamera()
PositionEntity cam,0,0,-40
CameraRange cam,0.1,100

WireFrame True
cube=CreateSegCube(35)
ScaleMesh cube,30,20,20
texture=LoadTexture("test.jpg")

For a=1 To 6
	If a And 3 Then
		brush=CreateBrush(Rnd(255),Rnd(255),Rnd(255))
	Else
		brush=CreateBrush()
		BrushTexture brush,texture
	End If
	PaintSurface GetSurface(cube,a),brush
	FreeBrush brush
Next

light=CreateLight(3,cam)

While Not KeyDown(1)
	If KeyHit(17) Then w=1-w
	WireFrame w
	TurnEntity light,0,1,0
	TurnEntity cube,1,.1,1
	UpdateWorld
	RenderWorld
	Flip
Wend

FreeEntity cube
FreeEntity cam
EndGraphics
End

;
;	Create a segmented cube
;	six surfaces
;

Function CreateSegCube(segs=1,parent=0)
	mesh=CreateMesh( parent )
	For scnt=0 To 3
		surf=CreateSurface( mesh )
		stx#=-.5
		sty#=stx
		stp#=Float(1)/Float(segs)
		y#=sty
		For a=0 To segs
			x#=stx
			v#=a/Float(segs)
			For b=0 To segs
				u#=b/Float(segs)
				AddVertex(surf,x,y,0.5,u,v)
				x=x+stp
			Next
			y=y+stp
		Next
		For a=0 To segs-1
			For b=0 To segs-1
				v0=a*(segs+1)+b:v1=v0+1
				v2=(a+1)*(segs+1)+b+1:v3=v2-1
				AddTriangle( surf,v0,v1,v2 )
				AddTriangle( surf,v0,v2,v3 )
			Next
		Next
		RotateMesh mesh,0,90,0
	Next
	;top and bottom
	RotateMesh mesh,90,0,0
	For scnt=0 To 1
		surf=CreateSurface( mesh )
		stx#=-.5
		sty#=stx
		stp#=Float(1)/Float(segs)
		y#=sty
		For a=0 To segs
			x#=stx
			v#=a/Float(segs)
			For b=0 To segs
				u#=b/Float(segs)
				AddVertex(surf,x,y,0.5,u,v)
				x=x+stp
			Next
			y=y+stp
		Next
		For a=0 To segs-1
			For b=0 To segs-1
				v0=a*(segs+1)+b:v1=v0+1
				v2=(a+1)*(segs+1)+b+1:v3=v2-1
				AddTriangle( surf,v0,v1,v2 )
				AddTriangle( surf,v0,v2,v3 )
			Next
		Next
		RotateMesh mesh,180,0,0
	Next
	UpdateNormals mesh
	Return mesh
End Function
