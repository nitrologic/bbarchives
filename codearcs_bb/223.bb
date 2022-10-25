; ID: 223
; Author: Rob
; Date: 2002-02-04 15:29:32
; Title: quick deathstar effect (tm)
; Description: hey why not?

;instant space glowing windows & lots of bits effect
; rob cummings (rob@redflame.net)

Graphics3D 640,480,16,2

camera=CreateCamera()
CameraRange camera,1,2000
MoveEntity camera,0,0,-1000
deathstar=CreateSphere(16)
ScaleEntity deathstar,500,500,500
light=CreateLight(2)
MoveEntity light,1000,1000,-1000
AmbientLight 32,32,32

;create crap chunky metal texture
basetex=CreateTexture(256,256,8)
SetBuffer TextureBuffer(basetex)
For i=0 To 511
	c=Rnd(64)+128
	Color c/2,c/2,c/2
	x=Rnd(255):y=Rnd(255)
	w=Rnd(64):h=Rnd(64)
	Rect x,y,2+w,2+h,1
	Color c,c,c
	Rect x+2,y+2,w,h,1
Next 

;create bright windows texture
windows=CreateTexture(256,256)
SetBuffer TextureBuffer(windows)
For i=0 To 100
	Color 255,255,255
	Rect Rnd(255),Rnd(255),1+Rnd(4),1+Rnd(2),1
Next 


;map the deathstar with these textures
ScaleTexture basetex,0.1,0.2
EntityTexture deathstar,basetex,0,0

ScaleTexture windows,0.5,0.2
TextureBlend windows,3
EntityTexture deathstar,windows,0,1

SetBuffer BackBuffer()
While Not KeyHit(1)
	TurnEntity deathstar,0,-.05,0
	RenderWorld
	Flip
Wend
End
