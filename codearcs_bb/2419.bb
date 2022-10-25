; ID: 2419
; Author: Nate the Great
; Date: 2009-02-22 21:15:47
; Title: tex face cam
; Description: faces textures to camera

Graphics3D 1024,768,0,2
SetBuffer BackBuffer()

Global cam1 = CreateCamera()
MoveEntity cam1,0,0,-8

lit = CreateLight()
TurnEntity lit,90,0,0

cub = CreateCube()
sph = CreateSphere(32,cub)
con = CreateCone(32,True,cub)

Tex = CreateTexture(256,256)

SetBuffer TextureBuffer(tex)

ClsColor 255,0,255
Cls
Color 0,255,0

For i = 1 To 100
	Line Rnd(256),Rnd(256),Rnd(256),Rnd(256)
Next

EntityTexture(cub,tex)
EntityTexture(con,tex)
EntityTexture(sph,tex)
MoveEntity con,-3,0,0
MoveEntity sph,3,0,0

SetBuffer BackBuffer()
ClsColor 0,0,0
tim = CreateTimer(60)
While Not KeyDown(1)
Cls

updateflattex(cub,256)
updateflattex(sph,256)
updateflattex(con,256)

RotateMesh cub,1,1,0

If KeyDown(203)
	MoveEntity cub,-.02,0,0
ElseIf KeyDown(205)
	MoveEntity cub,.02,0,0
EndIf
If KeyDown(208)
	MoveEntity cub,0,-.02,0
ElseIf KeyDown(200)
	MoveEntity cub,0,.02,0
EndIf

UpdateWorld()
RenderWorld()
WaitTimer(tim)

Flip
Wend

End

Function updateflattex(ent,num)

For s = 1 To CountSurfaces(ent)
	surf = GetSurface(ent,s)
	For v = 0 To CountVertices(surf)-1
		TFormPoint VertexX(surf,v), VertexY(surf, v),VertexZ(surf, v), ent, 0
		CameraProject cam1,TFormedX(),TFormedY(),TFormedZ()
		x# = ProjectedX()
		y# = ProjectedY()
		VertexTexCoords surf,v,x#/num,y#/num
	Next
Next

End Function
