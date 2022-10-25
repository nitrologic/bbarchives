; ID: 1351
; Author: mag.
; Date: 2005-04-15 16:47:07
; Title: Glow Effect
; Description: Glow Effect

;Glow effect
;Code by Mag. Idea from Sswift.

Graphics3D 640,480,32,2
SetBuffer BackBuffer()

; Create camera
Global camera=CreateCamera()
MoveEntity camera,3,3,0
RotateEntity camera,30,30,0

light=CreateLight()

Global cube=CreateCube()
PositionEntity cube,0,0,5
tex0=CreateTexture(300,300)
SetBuffer TextureBuffer(tex0)
ClsColor 255,255,255
Cls
SeedRnd(MilliSecs())
For k=1 To 50
	Color Rand(256),Rand(256),Rand(256)
	Rect Rand(600),Rand(600),Rand(600),Rand(600)
Next
EntityTexture cube,tex0
SetBuffer BackBuffer()

;glow setup
s=1
Global sizex=640/s
Global sizey=480/s
Global glowtexture=CreateTexture (384,384,256)
Global sp=CreateSprite(camera)
MoveEntity sp,-.25,-0.06,1.18
EntityAlpha sp,.32
ScaleTexture glowtexture,GraphicsWidth()/sizex,GraphicsHeight()/sizey
EntityTexture sp,glowtexture
TextureBlend glowtexture, 5
While Not KeyDown( 1 )
	TurnEntity cube,0.5,0.5,0.5
	CameraViewport camera,0,0,sizex,sizey
	RenderWorld
	CopyRect 0,0,sizex,sizey,0,0,BackBuffer(),TextureBuffer(glowtexture)
	CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()
	
	RenderWorld
	Flip
Wend

End
