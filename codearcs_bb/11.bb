; ID: 11
; Author: BlitzSupport
; Date: 2001-08-16 20:13:37
; Title: TexTile
; Description: Texture creator/viewer








; READ IMPORTANT INFORMATION FIRST!!!!!!







; ---------------------------------------------------------------
; Textile -- james@hi-toro.com -- Public Domain
; ---------------------------------------------------------------
; Crude little util for creating/editing B3D tileable textures.
; ---------------------------------------------------------------

; Load a texture to see it tiled, or load a large image and create
; a new texture, eg. from the bricks of a building...

; ***************************************************************

; IMPORTANT!!! Seems to be a BB bug that saves the image into
; the boot drive root folder (eg. "C:\"), instead of the
; current directory... so look there after quitting the program!

; IMPORTANT 2!!! Don't try to run it from here! CREATE AS AN
; EXECUTABLE. Drag the image you want to load onto the
; program's icon to load it.

; ***************************************************************

; ---------------------------------------------------------------
; Controls
; ---------------------------------------------------------------

; Use mouse to position box
; Use cursors to scroll around picture
; Use Ctrl + cursors to resize box
; Use < and > (actually , and .) to rotate (slowly!)
; Use < and > with Shift for larger rotation
; ESC quits and saves the texture

AppTitle "Textile"

; Set display mode (windowed) and double-buffering:
Graphics 640, 480, 0, 2
SetBuffer BackBuffer ()

; Source image position and limits to keep on screen:
Global sourceImage, sourceCopy, sx, sy, maxsx, maxsy
Global bw, resizing, rotation#

; Load source image:
a$ = CommandLine$ ()
LoadSource (a$)

; Resulting texture info:
Global textureImage = CopyImage (sourceImage);CreateImage (TextureWidth, TextureHeight)
Global texWidth = 64;ImageWidth (textureImage)
Global texHeight = 64;ImageHeight (textureImage)

TFormFilter False

Repeat

	RotateSource ()
	PositionSource ()	
	CopyToTexture ()
	ResizeTexture ()	
	PrintInfo ()
	
	Flip

Until KeyHit (1)

SaveTexture ()
End

; ---------------------------------------------------------------

Function LoadSource (a$)
	; Weird, Win2000 adds quotes round the command line that Blitz chokes on, whereas 98 doesn't!
	If Left (a$, 1) = Chr (34) And Right (a$, 1) = Chr (34)
		a$ = Mid (a$, 2, Len (a$) - 2)
	EndIf
	sourceImage = LoadImage (a$)
	If sourceImage = 0 Then RuntimeError "[Guru Meditation]" + Chr (10) + Chr (10) + "Cannot load image. Throwing dollies out of pram...": End
	sourceCopy = CopyImage (sourceImage)
	If ImageWidth (sourceImage) > GraphicsWidth ()
		maxsx = - (ImageWidth (sourceImage) - GraphicsWidth ())
	Else
		maxsx = 0
	EndIf
	If ImageHeight (sourceImage) > GraphicsHeight () / 2
		maxsy = - (ImageHeight (sourceImage) - (GraphicsHeight () / 2) + 1)
	Else
		maxsy = 0
	EndIf
	sx = 0
	sy = 0
End Function

Function PositionSource ()

	Viewport 0, 0, GraphicsWidth (), (GraphicsHeight () / 2) - 1
	Cls

	If Not resizing

		If (KeyDown (42)) Or (KeyDown (54))
			jump = 32
		Else
			jump = 1
		EndIf
	
		If maxsx <> 0
			If KeyDown (203)
				sx = sx + jump
			EndIf
			If KeyDown (205)
				sx = sx - jump
			EndIf
		EndIf
	
		If maxsy <> 0
			If KeyDown (208)
				sy = sy - jump
			EndIf
			If KeyDown (200)
				sy = sy + jump
			EndIf
		EndIf
		
		If sx > 0 Then sx = 0
		If sy > 0 Then sy = 0
	
		If sx < maxsx Then sx = maxsx
		If sy < maxsy Then sy = maxsy

	EndIf
	
	DrawImage sourceImage, sx, sy

End Function

Function DrawBox ()
	If bw Then c = 255 Else c = 0
	Color c, c, c
	Rect MouseX (), MouseY (), texWidth, texHeight, 0
End Function

Function CopyToTexture ()
	If MouseHit (1)
		FreeImage textureImage
		textureImage = CreateImage (texWidth, texHeight)
		GrabImage textureImage, MouseX (), MouseY ()
	EndIf
	DrawBox ()
	bw = 1 - bw
	Viewport 0, (GraphicsHeight () / 2) + 1, GraphicsWidth (), (GraphicsHeight () / 2) - 1
	Cls
	TileImage textureImage, 0, (GraphicsHeight () / 2) + 1
End Function

Function ResizeTexture ()

	resizing = False
	
	If (KeyDown (29)) Or (KeyDown (157))

		resizing = True
		changed = 0

		If (KeyDown (42)) Or (KeyDown (54))
			jump = 16
		Else
			jump = 1
		EndIf

		If KeyDown (203)
			texWidth = texWidth - jump
			changed = True
		EndIf

		If KeyDown (205)
			texWidth = texWidth + jump
			changed = True
		EndIf
	
		If KeyDown (200)
			texHeight = texHeight - jump
			changed = True
		EndIf

		If KeyDown (208)
			texHeight = texHeight + jump
			changed = True
		EndIf

		If texWidth < 2 Then texWidth = 2
		If texHeight < 2 Then texHeight = 2

;		If changed
;			FreeImage textureImage
;			textureImage = CreateImage (texWidth, texHeight)
;		EndIf

	EndIf

End Function

Function PrintInfo ()
	Color 0, 0, 0
	Text 20, (GraphicsHeight () / 2) + 20, "Texture width: " + texWidth
	Text 20, (GraphicsHeight () / 2) + 40, "Texture height: " + texHeight
	Color 255, 255, 255
	Text 21, (GraphicsHeight () / 2) + 21, "Texture width: " + texWidth
	Text 20, (GraphicsHeight () / 2) + 41, "Texture height: " + texHeight
End Function

Function SaveTexture ()
	texture$ = "Texture.bmp"	
	counter = 1
	Repeat
		counter = counter + 1
		texture$ = "Texture" + counter + ".bmp"
	Until FileType (texture$) = 0
	SaveImage textureImage, CurrentDir$ () + "\" + texture$
End Function

Function RotateSource ()

	changed = False
	
	If (KeyDown (42)) Or (KeyDown (54))
		jump# = 0.5
	Else
		jump# = 0.05
	EndIf

	If KeyDown (51)
		FreeImage sourceImage
		sourceImage = CopyImage (sourceCopy)
		MidHandle sourceImage
		rotation = rotation - jump
		changed = True
	EndIf

	If KeyDown (52)
		FreeImage sourceImage
		sourceImage = CopyImage (sourceCopy)
		MidHandle sourceImage
		rotation = rotation + jump
		changed = True
	EndIf

	If changed
		RotateImage sourceImage, rotation
		HandleImage sourceImage, 0, 0
		If ImageWidth (sourceImage) > GraphicsWidth ()
			maxsx = - (ImageWidth (sourceImage) - GraphicsWidth ())
		Else
			maxsx = 0
		EndIf
		If ImageHeight (sourceImage) > GraphicsHeight () / 2
			maxsy = - (ImageHeight (sourceImage) - (GraphicsHeight () / 2) + 1)
		Else
			maxsy = 0
		EndIf
	EndIf

End Function
