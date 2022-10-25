; ID: 1857
; Author: impixi
; Date: 2006-11-09 08:30:47
; Title: Seamless Tiles
; Description: Create a seamless tile pixmap from a source pixmap

Rem
	Seamless Texture Tiling Example
	
	ORIGINAL AUTHOR (Article and C Code):
		Paul Bourke
		January 2000
	http://local.wasp.uwa.edu.au/~pbourke/texture/tiling/
			
	REQUIREMENTS:
		BlitzMax 1.22
		Windows XP (Linux and MacOS untested)
		
	PURPOSE:
		Create a seamless 'tile' pixmap from a source pixmap
		
EndRem

SuperStrict

AppTitle = "Seamless Texture Tiling Example"

Graphics 1024, 768
SetBlend SOLIDBLEND

Local tex:TPixmap = LoadPixmap("tex.png")	'<--- Provide your own texture for this example. <---

If (tex.format <> PF_RGBA8888) Then tex = ConvertPixmap(tex, PF_RGBA8888)

Local tile:TPixmap = createSeamlessTile(tex, MASK_RADIAL)

Local img:TImage = LoadImage(tile)

SetClsColor 255, 255, 255

While Not KeyHit(KEY_ESCAPE)

	Cls

	SetViewport 0, 0, GraphicsWidth(), GraphicsHeight()

	DrawText "Source:", 2, 2
	DrawPixmap tex, 2, 22
	
	DrawText "Result:", 2, tex.height + 42
	DrawPixmap tile, 2, tex.height + 62

	DrawText "Tiled Result:", tex.width + 20, 2
	SetViewport tex.width + 20, 22, GraphicsWidth() - tex.width - 22, GraphicsHeight() - 26
	TileImage img
			
	Flip 1

Wend

End


'*************** SEAMLESS TILE FUNCTION AND RELATED CONSTANTS *************************************


Const MASK_LINEAR:Int = 0
Const MASK_RADIAL:Int = 1

Function createSeamlessTile:TPixmap(src:TPixmap, masktype:Int = MASK_LINEAR)

	'src: Source pixmap texture. Format should be PF_RGBA8888.
	'masktype: Mask type. MASK_LINEAR or MASK_RADIAL. Some textures tile better using a different mask.
	'Returns a new 'tileable' pixmap.

	Local outp:TPixmap = CreatePixmap(src.width, src.height, src.format)
	Local diag:TPixmap = CreatePixmap(src.width, src.height, src.format)

	Local temp:TPixmap = PixmapWindow(src, 0, 0, src.width / 2, src.height / 2)

	For Local x:Int = 0 To temp.width - 1
		For Local z:Int = 0 To temp.height - 1
			WritePixel diag, (src.width / 2) + x, (src.height / 2) + z, ReadPixel(temp, x, z)
		Next
	Next
	
	temp = PixmapWindow(src, src.width / 2, src.height / 2, src.width / 2, src.height / 2)
	
	For Local x:Int = 0 To temp.width - 1
		For Local z:Int = 0 To temp.height - 1
			WritePixel diag, x, z, ReadPixel(temp, x, z)
		Next
	Next

	temp = PixmapWindow(src, src.width / 2, 0, src.width / 2, src.height / 2)
	
	For Local x:Int = 0 To temp.width - 1
		For Local z:Int = 0 To temp.height - 1
			WritePixel diag, x, (src.height / 2) + z, ReadPixel(temp, x, z)
		Next
	Next

	temp = PixmapWindow(src, 0, src.height / 2, src.width / 2, src.height / 2)
	
	For Local x:Int = 0 To temp.width - 1
		For Local z:Int = 0 To temp.height - 1
			WritePixel diag, (src.width / 2) + x, z, ReadPixel(temp, x, z)
		Next
	Next

	Local masksize:Int
	If (src.width > src.height) Then masksize = src.width Else masksize = src.height 
		
	Local mask:TPixmap = CreatePixmap(masksize, masksize, PF_RGB888)

	For Local x:Int = 0 To masksize / 2
		For Local z:Int = 0 To masksize / 2

			Local d:Float = 0.0
			
			If masktype = MASK_RADIAL
				d = Sqr((x - (masksize / 2)) * (x - (masksize / 2)) + (z - (masksize / 2)) * (z - (masksize / 2))) / (masksize / 2)
			Else 
				If masktype = MASK_LINEAR
					d = Max(Float((masksize / 2) - x), Float((masksize / 2) - z)) / Float(masksize / 2)
				EndIf
			EndIf
			
			d = 255 - (255 * d)
			
			If d < 1 Then d = 1
			If d > 255 Then d = 255
			
			WritePixel mask, x, z, Int(Byte(d) Shl 16 | Byte(d) Shl 8 | Byte(d))
			WritePixel mask, x, (masksize - 1 - z), Int(Byte(d) Shl 16 | Byte(d) Shl 8 | Byte(d))
			WritePixel mask, (masksize - 1 - x), z, Int(Byte(d) Shl 16 | Byte(d) Shl 8 | Byte(d))
			WritePixel mask, (masksize - 1 - x), (masksize - 1 - z), Int(Byte(d) Shl 16 | Byte(d) Shl 8 | Byte(d))

		Next
	Next

	mask = ResizePixmap(mask, src.width, src.height)

	For Local z:Int = 0 To src.height - 1
		For Local x:Int = 0 To src.width - 1
		
			Local a1:Float = Float(Byte(ReadPixel(mask, x, z) Shr 16))
			Local a2:Float = Float(Byte(ReadPixel(mask, ((x + src.width / 2) Mod src.width), ((z + src.height / 2) Mod src.height)) Shr 16))
			
			Local px1:Int = ReadPixel(src, x, z)
			Local px1a:Byte = px1 Shr 24
			Local px1b:Byte = px1 Shr 16
			Local px1g:Byte = px1 Shr 8
			Local px1r:Byte = px1
			
			Local px2:Int = ReadPixel(diag, x, z)
			Local px2a:Byte = px2 Shr 24
			Local px2b:Byte = px2 Shr 16
			Local px2g:Byte = px2 Shr 8
			Local px2r:Byte = px2
			
			Local pxRa:Byte = px1a
			Local pxRb:Byte = (a1 * (px1b / (a1 + a2))) + (a2 * (px2b / (a1 + a2)))
			Local pxRg:Byte = (a1 * (px1g / (a1 + a2))) + (a2 * (px2g / (a1 + a2)))
			Local pxRr:Byte = (a1 * (px1r / (a1 + a2))) + (a2 * (px2r / (a1 + a2)))
			Local pxR:Int = Int(pxRa Shl 24 | pxRb Shl 16 | pxRg Shl 8 | pxRr)
						
			WritePixel outp, x, z, pxR
	
		Next
	Next

	Return outp

EndFunction
