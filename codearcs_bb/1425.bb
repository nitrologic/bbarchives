; ID: 1425
; Author: Andres
; Date: 2005-07-20 18:53:27
; Title: Bump mapping
; Description: A bit slow and buggy bit still nice

Function DrawBumpmapImage(image, map, dx, dy, lx, ly, lh, ld) ; image, bumpmap, x, y, LightX, LightY, LightHeight, LightDistance
	Local width = ImageWidth(Image), height = ImageHeight(Image)
	Local mapwidth = ImageWidth(map), mapheight = ImageHeight(map)
	Local imgr, imgg, imgb, mapr, mapg, mapb, mapcol, mapangle
	
	LockBuffer ImageBuffer(image)
	LockBuffer ImageBuffer(map)
	LockBuffer GraphicsBuffer()
	For y = 0 To height - 1
		For x = 0 To width - 1
			If BumpmapDistance(dx + x, dy + y, lx, ly) < ld
				argb = ReadPixelFast(x, y, ImageBuffer(image))
					imgr = (argb Shr 16) And $FF
					imgg = (argb Shr 8) And $FF
					imgb = argb And $FF
				argb = ReadPixelFast((Float x / width) * mapwidth, (Float y / height) * mapheight, ImageBuffer(map))
					mapcol = (((argb Shr 16) And $FF) + ((argb Shr 8) And $FF) + (argb And $FF)) / 3
				
				mapangle = BumpmapAngle(dx + x, dy + y, lx, ly)
				;argb = ReadPixel((Float x / width) * mapwidth + Cos(mapangle), (Float y / height) * mapheight + Sin(mapangle), ImageBuffer(map))
				argb = ReadBumpmapPixel((Float x / width) * mapwidth + Cos(mapangle) * 2, (Float y / height) * mapheight + Sin(mapangle) * 2, map)
					mapcol2 = (((argb Shr 16) And $FF) + ((argb Shr 8) And $FF) + (argb And $FF)) / 3
				
				mapangle = BumpmapAngle(0, mapcol, BumpmapDistance(dx, dy, lx, ly), lh) + BumpmapAngle(0, mapcol2, 2, mapcol)
				mapcol = mapcol * (Sin(mapangle) + 2) / 2 * (ld - BumpmapDistance(dx + x, dy + y, lx, ly)) / ld
				
				red = imgr * (Float mapcol / 255)
				green = imgg * (Float mapcol / 255)
				blue = imgb * (Float mapcol / 255)
				
				WritePixelFast dx + x, dy + y, (blue Or (green Shl 8) Or (red Shl 16) Or ($FF000000)), GraphicsBuffer()
			EndIf
		Next
	Next
	UnlockBuffer GraphicsBuffer()
	UnlockBuffer ImageBuffer(map)
	UnlockBuffer ImageBuffer(image)
End Function

Function ReadBumpmapPixel%(x#, y#, image)
	Local argb
	Local xf# = (x Mod 1)
	Local yf# = (y Mod 1)
	Local r1#, g1#, b1#, r2#, g2#, b2#, r3#, g3#, b3#, r4#, g4#, b4#
	
	argb = ReadPixelFast(x, y, ImageBuffer(image))
		r1# = ((argb Shr 16) And $FF) * xf * yf
		g1# = ((argb Shr 8) And $FF) * xf * yf
		b1# = (argb And $FF) * xf * yf
	argb = ReadPixelFast(x + 1, y, ImageBuffer(image))
		r2# = ((argb Shr 16) And $FF) * (1.0 - xf) * yf
		g2# = ((argb Shr 8) And $FF) * (1.0 - xf) * yf
		b2# = (argb And $FF) * (1.0 - xf) * yf
	argb = ReadPixelFast(x + 1, y + 1, ImageBuffer(image))
		r3# = ((argb Shr 16) And $FF) * (1.0 - xf) * (1.0 - yf)
		g3# = ((argb Shr 8) And $FF) * (1.0 - xf) * (1.0 - yf)
		b3# = (argb And $FF) * (1.0 - xf) * (1.0 - yf)
	argb = ReadPixelFast(x, y + 1, ImageBuffer(image))
		r4# = ((argb Shr 16) And $FF) * xf * (1.0 - yf)
		g4# = ((argb Shr 8) And $FF) * xf * (1.0 - yf)
		b4# = (argb And $FF) * xf * (1.0 - yf)
	
	red = r1 + r2 + r3 + r4
	green = g1 + g2 + g3 + g4
	blue = b1 + b2 + b3 + b4
	
	Return (blue Or (green Shl 8) Or (red Shl 16) Or ($FF000000))
End Function

Function BumpmapAngle(x, y, targetx, targety)
	If targety < y Then
		If targetx < x Then
			Return Abs(ATan2(targetx - x,targety - y)) + 90
		Else
			Return 270+(180-Abs(ATan2(targetx - x,targety - y)))
		EndIf
	Else If targety => y Then
		Return Abs(ATan2(targetx - x,targety - y) - 90)
	EndIf
End Function

Function BumpmapDistance#(x1#, y1#, x2#, y2#)
	Return Sqr((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
End Function
