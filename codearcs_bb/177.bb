; ID: 177
; Author: BlitzSupport
; Date: 2001-12-31 23:24:49
; Title: Backdrops in 3D
; Description: Creates tiles from a backdrop image

; -----------------------------------------------------------------------------------
; Make3DBackdrop () demo...
; -----------------------------------------------------------------------------------

; 3D backdrop made up of sprites. Why?

; Since some cards will automatically rescale textures down to 256 x 256 or less,
; you shouldn't really try to use one big sprite -- it's also a struggle for many
; cards to deal with screen-sized textures!)...

; The default size used is 256 x 256, as this is what Voodoos seem to scale textures
; down to. Most cards should be OK with this, but you can always load a dummy
; texture of 256 x 256 and check the result with TextureWidth (), then pass
; that value to Make3DBackdrop (see function description).

; -----------------------------------------------------------------------------------
; Demo...
; -----------------------------------------------------------------------------------
; If you use an image larger than the screen, the cursors will scroll
; it around...
; -----------------------------------------------------------------------------------
; W for wireframe...
; -----------------------------------------------------------------------------------

Graphics3D 640, 480
AppTitle "W for wireframe..."

cam = CreateCamera ()

light = CreateLight ()
MoveEntity light, -10, 10, -10

cube = CreateCube ()
MoveEntity cube, 0, 0, 5
EntityColor cube, 128, 0, 0

; -----------------------------------------------------------------------------------
; Make a 3D backdrop from given image...
; -----------------------------------------------------------------------------------

backdrop = Make3DBackdrop (cam, "G:\My Documents\My Pictures\horsehead nebula.bmp")

Repeat

	TurnEntity cube, 1, 1, 1
	TranslateEntity cube, 0, 0, 0.1

	If KeyHit (17) w = 1 - w: WireFrame w

	If KeyDown (203)
		MoveEntity backdrop, 0.1, 0, 0
	Else
		If KeyDown (205)
			MoveEntity backdrop, -0.1, 0, 0
		EndIf
	EndIf
	
	If KeyDown (200)
		MoveEntity backdrop, 0, -0.1, 0
	Else
		If KeyDown (208)
			MoveEntity backdrop, 0, 0.1, 0
		EndIf
	EndIf

	RenderWorld

	; Impose your own movement restrictions based on these...!
	
	;Text 20, 20, EntityX (backdrop)
	;Text 20, 30, EntityY (backdrop)
	;Text 20, 40, EntityZ (backdrop)
	
	Flip

Until KeyHit (1)

End

; -----------------------------------------------------------------------------------
; FUNCTION: Make3DBackdrop (cam, image$, maxTexSize, eOrder)
; -----------------------------------------------------------------------------------
; cam			= existing 3D camera
; image$		= image filename
; maxTexSize	= max texture width & height (DEFAULT: 256, good for most gfx cards)
; eOrder		= EntityOrder value			 (DEFAULT: 128, adjust if necessary)
; -----------------------------------------------------------------------------------
; NOTE: Uses ClearTextureFilters, so TextureFilter is reset to default at the end...
; -----------------------------------------------------------------------------------
; Returns backdrop pivot...
; -----------------------------------------------------------------------------------

Function Make3DBackdrop (cam, image$, maxTexSize = 256, eOrder# = 128)

	; Image to use...

	bg = LoadImage (image$)

	; Display and image dimensions...
	
	gW = GraphicsWidth ()
	gH = GraphicsHeight ()
	
	iW = ImageWidth (bg)
	iH = ImageHeight (bg)
	
	; Handle to image's buffer...
	
	bgBuffer = ImageBuffer (bg)
	
	; Remainder width of right/bottom image pixels after full maxTexSize textures grabbed...

	rightSize = iW Mod maxTexSize
	bottomSize = iH Mod maxTexSize

	; How many times maxTexSize fits across the screen...
	
	scAcross = gW / maxTexSize
	scDown = gH / maxTexSize

	; Used to calculate full 3D dimensions of backdrop...
	
	across = iW / maxTexSize
	down = iH / maxTexSize
		
	gWidth# = (scAcross * 2) + (2.0 / (Float (maxTexSize) / Float (rightSize)))
	gHeight# = (scDown * 2) + (2.0 / (Float (maxTexSize) / Float (bottomSize)))
	
	; For best texture drawing...
	
	ClearTextureFilters
	
	; Backdrop pivot...
	
	bd = CreatePivot (cam)

	; Bah, trial and error produced this! Copies chunks of image onto separate sprites
	; and positions each accordingly. Also sets EntityOrder so they're drawn behind
	; everything else...
	
	For x = 0 To across
		For y = 0 To down 
			sprite = CreateSprite (bd)
			EntityOrder sprite, eOrder
			texture = CreateTexture (maxTexSize, maxTexSize, 8 + 16 + 32)
			If x = across Then uSize = rightSize Else uSize = maxTexSize
			If y = down Then vSize = bottomSize Else vSize = maxTexSize
			CopyRect (x * maxTexSize), (y * maxTexSize), uSize, vSize, 0, 0, bgBuffer, TextureBuffer (texture)
			EntityTexture sprite, texture
			PositionEntity sprite, (x * 2) + (1 - (gWidth / 2.0)), (-y * 2) + (gHeight / 2.0) - 1, gWidth / 2.0
		Next
	Next

	; Free the loaded image...

	FreeImage bg

	; Reset texture filter to default...
	
	TextureFilter "", 1 + 8

	; Return backdrop handle...
	
	Return bd
	
End Function
