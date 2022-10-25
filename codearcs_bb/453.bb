; ID: 453
; Author: Rob
; Date: 2002-10-07 19:04:34
; Title: Planet halo/sunflare without clipping problems
; Description: Utilises a side effect of env mapping

;bugs: rob@redflame.net
Graphics3D 640,480,16,2
camera=CreateCamera()
planet=CreateSphere()
glow=CreateSphere()
ScaleEntity glow,1.6,1.6,1.6

light=CreateLight(2)
MoveEntity light,1000,1000,-1000
MoveEntity camera,0,0,-10

tex=CreateTexture(64,64,48+64+512+2)
SetBuffer TextureBuffer(tex)
For i=1 To 64
	Color i*3,i*3,i*4
	Oval i/2,i/2,64-i,64-i,1
Next
EntityTexture glow,tex
EntityBlend glow,3

While Not KeyHit(1)
	UpdateWorld
	RenderWorld
	Flip
Wend
End
