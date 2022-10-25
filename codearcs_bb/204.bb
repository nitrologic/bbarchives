; ID: 204
; Author: BlitzSupport
; Date: 2002-08-20 23:13:50
; Title: LoadMaskedSprite
; Description: Loads a sprite or texture masked with your chosen RGB values

; -----------------------------------------------------------------------------
; LoadMaskedSprite () and LoadMaskedTexture ()...
; -----------------------------------------------------------------------------

; [THANKS: Skully, for the mind-boggling bit-shift thing!]

; Sprites and textures can only use a mask of 0, 0, 0, which means that you can't
; easily have sprites (or textures) with black in them -- ouch!

; These functions let you load a sprite or texture using whatever mask colour
; suits you, meaning you can now have nice black outlines on your cartoony
; sprites... :D

; LoadMaskedSprite is the same as LoadMaskedTexture, but returns a sprite
; automatically for convenience...

Graphics3D 640, 480
AppTitle "LoadMaskedSprite"

cam = CreateCamera ()
CameraClsColor cam, 64, 128, 180
CameraRange cam, 0.1, 100
PositionEntity cam, 0, 2, -5

plane = CreatePlane ()
EntityColor plane, 128, 180, 128

; -----------------------------------------------------------------------------
; Example usage: just include the LoadMaskedSprite function in your code, and
; call it like this (the 255, 0, 255 isn't actually needed in this example, as
; that's the default, but I've left it in so you can alter it to suit an image
; of your own)...
; -----------------------------------------------------------------------------
	sheep = LoadMaskedSprite ("sheep256.png", 255, 0, 255)
; -----------------------------------------------------------------------------

PositionEntity sheep, 0, 2, 0

Repeat
	MoveEntity sheep, ((-KeyDown (203)) Or (KeyDown (205))) / 10.0, ((-KeyDown (208)) Or (KeyDown (200))) / 10.0, ((-KeyDown (44)) Or (KeyDown (30))) / 10.0
	RenderWorld
	Text 20, 20, "Cursors plus A and Z..."
	Flip
Until KeyHit (1)
End

; -----------------------------------------------------------------------------
; LoadMaskedSprite (image$, r, g, b, flags)
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; REQUIRED PARAMETERS:
; -----------------------------------------------------------------------------
; image$  = image file to be used for sprite
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; OPTIONAL PARAMETERS:
; -----------------------------------------------------------------------------
; r, g, b = background colour to mask out (defaults to 255, 0, 255)
; flags   = defaults to 1 + 2 + 8 + 16 + 32 (best not to touch these!)
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; EXAMPLE
; -----------------------------------------------------------------------------
; LoadMaskedSprite ("ship.bmp", 0, 255, 0)
; ... would use a green background mask...
; -----------------------------------------------------------------------------

Function LoadMaskedSprite (f$, r = 255, g = 0, b = 255, flags = 1 + 2 + 8 + 16 + 32)

	temp = CreateTexture (1, 1, flags)
	WritePixel 0, 0, ((r Shl 16) + (g Shl 8) + b) And $00FFFFFF, TextureBuffer (temp)
	trgb = ReadPixel (0, 0, TextureBuffer (temp))
	r = trgb Shr 16 And %11111111
	g = trgb Shr 8 And %11111111
	b = trgb And %11111111
	FreeTexture temp

	texture = LoadTexture (f$, flags)						; Load texture
	tBuffer = GraphicsBuffer ()								; Store current buffer
		
	SetBuffer TextureBuffer (texture)						; Use texture's buffer
	LockBuffer GraphicsBuffer ()							; Lock it for 'fast' pixel access
	
	; Read each pixel of texture...
	
	For x = 0 To TextureWidth (texture) - 1
		For y = 0 To TextureHeight (texture) - 1

			rgb = ReadPixelFast (x, y) And $00FFFFFF		; Read pixel, strip alpha value			
			If rgb = ((r Shl 16) + (g Shl 8) + b)			; If pixel = rgb mask...
				WritePixelFast x, y, $00000000				; ... make transparent, else...
			Else											; Not rgb mask pixel, so...
				WritePixelFast x, y, rgb Or $FF000000		; ... make 'solid'
			EndIf

		Next
	Next

	UnlockBuffer GraphicsBuffer ()							; Unlock texture buffer

	sprite = CreateSprite ()								; Create blank sprite
	EntityTexture sprite, texture							; Apply masked texture
	
	SetBuffer tBuffer										; Reset graphics buffer as at start
	Return sprite											; Return masked sprite
	
End Function

; -----------------------------------------------------------------------------
; LoadMaskedTexture () -- usage as per LoadMaskedSprite () -- see above...
; -----------------------------------------------------------------------------

Function LoadMaskedTexture (f$, r = 255, g = 0, b = 255, flags = 1 + 2 + 8 + 16 + 32)

	texture = LoadTexture (f$, flags)						; Load texture
	tBuffer = GraphicsBuffer ()								; Store current buffer
		
	SetBuffer TextureBuffer (texture)						; Use texture's buffer
	LockBuffer GraphicsBuffer ()							; Lock it for 'fast' pixel access
	
	; Read each pixel of texture...
	
	For x = 0 To TextureWidth (texture) - 1
		For y = 0 To TextureHeight (texture) - 1

			rgb = ReadPixelFast (x, y) And $00FFFFFF		; Read pixel, strip alpha value
			If rgb = ((r Shl 16) + (g Shl 8) + b)			; If pixel = rgb mask...
				WritePixelFast x, y, $00000000				; ... make transparent, else...
			Else											; Not rgb mask pixel, so...
				WritePixelFast x, y, rgb Or $FF000000		; ... make 'solid'
			EndIf

		Next
	Next

	UnlockBuffer GraphicsBuffer ()							; Unlock texture buffer

	SetBuffer tBuffer										; Reset graphics buffer as at start
	Return texture											; Return masked texture
	
End Function


Function LoadMaskedAnimTexture (f$, width, height, frames, r = 255, g = 0, b = 255, startframe = 0, flags = 1 + 2 + 8 + 16 + 32)

	; Get correct RGB value for 16-bit modes by writing to,
	; and reading from, a temporary texture...
	
	temp = CreateTexture (1, 1, flags)
	WritePixel 0, 0, ((r Shl 16) + (g Shl 8) + b) And $00FFFFFF, TextureBuffer (temp)
	trgb = ReadPixel (0, 0, TextureBuffer (temp))
	r = trgb Shr 16 And %11111111
	g = trgb Shr 8 And %11111111
	b = trgb And %11111111
	FreeTexture temp

	; Load the animated texture...
	
	texture = LoadAnimTexture (f$, flags, width, height, startframe, frames)
	
	If texture
	
		tBuffer = GraphicsBuffer ()									; Store current graphics buffer
	
		For loop = 0 To frames - 1									; Do each frame in turn
		
			SetBuffer TextureBuffer (texture, loop)					; Use texture frame's buffer
			LockBuffer GraphicsBuffer ()							; Lock it for 'fast' pixel access
			
			; Read each pixel of texture frame...
			
			For x = 0 To width - 1
				For y = 0 To height - 1
		
					rgb = ReadPixelFast (x, y) And $00FFFFFF		; Read pixel, strip alpha value			
					If rgb = ((r Shl 16) + (g Shl 8) + b)			; If pixel = rgb mask...
						WritePixelFast x, y, $00000000				; ... make transparent, else...
					Else											; Not rgb mask pixel, so...
						WritePixelFast x, y, rgb Or $FF000000		; ... make 'solid'
					EndIf
		
				Next
			Next
		
			UnlockBuffer GraphicsBuffer ()							; Unlock texture buffer
	
		Next
			
		SetBuffer tBuffer											; Restore graphics buffer
		Return texture												; Hand back the AnimTexture...
	
	EndIf
	
End Function

; EXAMPLE: Loading a 10-frame animation sequence. (This one consists
; of the numbers 0-9, with a frame size of 64 x 64 and
; an RGB mask of 0, 0, 0.)

; tex = LoadMaskedAnimTexture (f$, 64, 64, 10, 0, 0, 0)
