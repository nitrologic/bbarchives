; ID: 627
; Author: Beaker
; Date: 2003-03-17 10:35:11
; Title: Furry ball effect
; Description: "MasterBeakers Hairy Balls" - the effect of fur on any mesh

;MasterBeakers Hairy Balls 2003
;credit is always nice. :)

Graphics3D 640,480
SetBuffer BackBuffer()

cam = CreateCamera ()
MoveEntity cam,0,0,-5

ball = CreateMesh()
EntityFX ball,32+2

ball2 = CreateSphere(5)
ScaleMesh ball2,3,3,3
EntityFX ball2,32+2
ClearTextureFilters 

tex = LoadTexture ("fur3.bmp",2+1)
EntityTexture ball,tex

surf = GetSurface(ball2,1)
For f# = 0 To -0.02 Step -0.001
	ScaleMesh ball2,1.008,1.008,1.008
	RotateMesh ball2,0,-(f*20.0),0
	n# = 1-(-(f)*100.0/2.0)
	For vert = 0 to CountVertices(surf)-1
		VertexColor surf,vert,255,255,255,n
	Next
	AddMesh ball2,ball
Next
FreeEntity ball2

While Not KeyDown(1)
	Flip False
	RenderWorld
	TurnEntity ball,0.03,0.12,0.03
Wend
End
