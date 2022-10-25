; ID: 401
; Author: BlitzSupport
; Date: 2002-08-20 23:08:41
; Title: CopyTexture/LoadImageTexture
; Description: Used to make copies of textures


; -----------------------------------------------------------------------------
; LoadImageTexture / CopyTexture
; -----------------------------------------------------------------------------

; LoadImageTexture can be used in place of multiple calls to LoadTexture on the
; same file. Alternatively, load using the normal LoadTexture then use CopyTexture
; for copies you intend to modify (leaving the original intact)...

; Why? Using LoadTexture to try and load multiple copies of one texture, and
; then modifying one of them, results in ALL copies being modified (see the
; demo's comments)... they actually all refer to the same texture!

; LoadImageTexture loads a texture via an image,...

Function LoadImageTexture (f$, flags = 1)
	teximage = LoadImage (f$)
	iw = ImageWidth (teximage)
	ih = ImageHeight (teximage)
	If teximage
		tex = CreateTexture (iw, ih, flags)
		tw = TextureWidth (tex)
		th = TextureHeight (tex)
		ResizeImage teximage, tw, th
		ib = ImageBuffer (teximage)
		If tex
			tb = TextureBuffer (tex)
			CopyRect 0, 0, tw, th, 0, 0, ib, tb
			FreeImage teximage
			Return tex
		EndIf
	EndIf
End Function

; CopyTexture does... guess what?

Function CopyTexture (texture, flags = 1)
	tw = TextureWidth (texture): th = TextureHeight (texture)
	tex = CreateTexture (tw, th, flags)
	tb = TextureBuffer (texture)
	txb = TextureBuffer (tex)
	LockBuffer txb
	LockBuffer tb
	For x = 0 To tw - 1
		For y = 0 To th - 1
			WritePixelFast x, y, ReadPixelFast (x, y, tb), txb
		Next
	Next
	UnlockBuffer tb
	UnlockBuffer txb
	Return tex
End Function

; -----------------------------------------------------------------------------
; DEMO...
; -----------------------------------------------------------------------------

Graphics3D 640, 480

cam = CreateCamera ()
MoveEntity cam, 0, 0, -5

cube1 = CreateCube ()
cube2 = CreateCube ()

MoveEntity cube1, -2, 0, 0
MoveEntity cube2, 2, 0, 0

; -----------------------------------------------------------------------------
; THE WHOLE POINT OF THIS STUFF...!
; -----------------------------------------------------------------------------
; Try enabling these two lines (DISABLE THE NEXT TWO!) -- note that when the
; second texture is modified, the first is too! LoadImageTexture and CopyTexture
; get around this... you can either load all copies using LoadImageTexture, or
; load one copy, then make copies via CopyTexture -- whichever suits you.
; -----------------------------------------------------------------------------

; tex1 = LoadTexture ("grass.bmp")
; tex2 = LoadTexture ("grass.bmp")

tex1 = LoadImageTexture ("grass.bmp")
tex2 = LoadImageTexture ("grass.bmp") ; Or: tex2 = CopyTexture (tex1)

tw = TextureWidth (tex2)
th = TextureHeight (tex2)

txb = TextureBuffer (tex2)
LockBuffer txb
For x = 0 To tw - 1
	For y = 0 To th - 1
		WritePixelFast x, y, ReadPixelFast (x, y, txb) * 0.5, txb
	Next
Next
UnlockBuffer txb

EntityTexture cube1, tex1
EntityTexture cube2, tex2

Repeat

	TurnEntity cube1, 0.1, 0.2, 0.4
	TurnEntity cube2, -0.1, -0.2, -0.4
	
	RenderWorld
	Flip
	
Until KeyHit (1)

End
