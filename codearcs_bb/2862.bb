; ID: 2862
; Author: ClayPigeon
; Date: 2011-06-20 10:39:58
; Title: Realistically Modeled Motion Blur Effect
; Description: Simulates real motion blur

AppTitle "Motion Blur"
Graphics3D 640,480,0,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Const layers% = 8

Global buffer_size% = 256

Dim blurtex%(layers-1)
Dim blurquad%(layers-1)

For i = 0 To layers-1
	blurtex(i) = CreateTexture(buffer_size,buffer_size)
Next

Global objecttex = CreateTexture(128,128)
SetBuffer TextureBuffer(objecttex)
Color 255,255,0
Rect 0,0,128,128
Color 0,127,255
Rect 0,0,64,64,True
Rect 64,64,64,64,True
SetBuffer BackBuffer()

Global camera% = CreateCamera()
CameraRange camera,0.1,1000

Global cube% = CreateCube()
PositionEntity cube,0,0,3
EntityTexture cube,objecttex

Global clear% = CreateMesh()
Global surf% = CreateSurface(clear)
AddVertex(surf,-1,1,0)
AddVertex(surf,1,1,0)
AddVertex(surf,1,-1,0)
AddVertex(surf,-1,-1,0)
AddTriangle(surf,0,1,2)
AddTriangle(surf,0,2,3)
EntityColor clear,0,0,0
PositionEntity clear,0,0,1
EntityOrder clear,-1

For i = 0 To layers-1
	blurquad(i) = CreateMesh()
	surf = CreateSurface(blurquad(i))
	AddVertex(surf,-1,1,0,0,0)
	AddVertex(surf,1,1,0,1,0)
	AddVertex(surf,1,-1,0,1,1)
	AddVertex(surf,-1,-1,0,0,1)
	AddTriangle(surf,0,1,2)
	AddTriangle(surf,0,2,3)
	EntityTexture blurquad(i),blurtex(i)
	PositionEntity blurquad(i),0,0,1
	EntityFX blurquad(i),1
	EntityAlpha blurquad(i),0.125
	EntityOrder blurquad(i),-1-i
Next

While Not KeyHit(1)
	Cls
	
	CaptureWorld
	
	TurnEntity cube,4,8,12
	
	UpdateWorld
	CameraViewport camera,0,0,buffer_size,buffer_size
	HideEntity clear
	For i = 0 To layers-1
		HideEntity blurquad(i)
	Next
	CameraViewport camera,0,0,buffer_size,buffer_size
	For i = 0 To layers-1
		RenderWorld (i+1)/Float(layers)
		CopyRect 0,0,buffer_size,buffer_size,0,0,BackBuffer(),TextureBuffer(blurtex(i))
	Next
	CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()
	ShowEntity clear
	For i = 0 To layers-1
		ShowEntity blurquad(i)
	Next
	RenderWorld
	
	Flip True
Wend

End
