; ID: 452
; Author: Rob
; Date: 2002-10-06 20:33:40
; Title: quick planet glow without entityorder
; Description: planet gradient fx

;bugs: rob@redflame.net
Graphics3D 640,480,16,2
camera=CreateCamera()
planet=CreateSphere()
glow=CreateSprite()
ScaleSprite glow,2,2

light=CreateLight(2)
MoveEntity light,1000,1000,-1000
MoveEntity camera,0,0,-10

tex=CreateTexture(64,64,48+2+512)
SetBuffer TextureBuffer(tex)
For i=1 To 64
	Color i*3,i*3,i*4
	Oval i/2,i/2,64-i,64-i,1
Next
EntityTexture glow,tex
EntityBlend glow,3

While Not KeyHit(1)

	PositionEntity glow,EntityX(planet),EntityY(planet),EntityZ(planet)
	PointEntity glow,camera
	MoveEntity glow,0,0,-1


	UpdateWorld
	RenderWorld
	Flip
Wend
End
