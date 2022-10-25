; ID: 30
; Author: BlitzSupport
; Date: 2001-08-30 09:49:55
; Title: Texture manipulation
; Description: Playing about with textures -- simple stuff


; ------------------------------------------------------------------
; 	Texture stuff
; ------------------------------------------------------------------

; ------------------------------------------------------------------
;	Open 3D display mode
; ------------------------------------------------------------------

Graphics3D 640, 480

Global cam = CreateCamera ()
CameraViewport cam, 0, 0, GraphicsWidth (), GraphicsHeight ()

; ------------------------------------------------------------------
; General setup
; ------------------------------------------------------------------

; Load and arrange objects, textures, etc here...
	
	; Create a texture, set the current buffer to draw to it, draw some stuff...
	
	tex		= CreateTexture (64, 64)
	SetBuffer TextureBuffer (tex)
		Color 255, 255, 255
		Rect 0, 0, 64, 64
		Color 0, 255, 0
		Rect 0, 0, 32, 32
		Rect 32, 32, 32, 32
		
	ground	= CreatePlane ()
	EntityTexture ground, tex	
	MoveEntity ground, 0, -1, 0

	btex	= CreateTexture (64, 64)
	SetBuffer TextureBuffer (btex)
		Color 255, 255, 255
		Rect 0, 0, 64, 64
		Color 255, 0, 0
		Rect 0, 0, 32, 32
		Rect 32, 32, 32, 32

	box = CreateCube ()
	EntityTexture box, btex
	MoveEntity box, 0, 0, 5
	
	; Set to BackBuffer () again!

	SetBuffer BackBuffer ()

	light = CreateLight ()
	MoveEntity light, 0, 2, 20
	PointEntity light, cam

Repeat
			
	u# = u + 0.001
	v# = v + 0.001

	; Rotate, move and scale texture on the box...
	
	RotateTexture btex, u * 100
	PositionTexture btex, u, v
	ScaleTexture btex, u , v

	TurnEntity box, 0, u, 0

	; Make 'em puke... :)
	
	TurnEntity ground, 0, (u / 10), 0
	
	; Keys...
	
	If KeyDown (203) TurnEntity cam, 0, 2, 0
	If KeyDown (205) TurnEntity cam, 0, -2, 0
	If KeyDown (200) MoveEntity cam, 0, 0, 0.1
	If KeyDown (208) MoveEntity cam, 0, 0, -0.1
		
	UpdateWorld
	RenderWorld

	Flip

Until KeyHit (1)

End

