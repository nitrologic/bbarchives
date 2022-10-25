; ID: 2411
; Author: Medicine Storm
; Date: 2009-02-11 06:46:36
; Title: StackPixmapsPrecise &amp; StackPixmapsFast
; Description: Combine 2 pixmaps as if drawn one atop the other. Includes color, alpha, Xflip, Yflip, rotation.

'p_pixBottom: TPixmap imitating the pixmap drawn first, behind the top pixmap
'p_pixTop: TPixmap imitating the pixmap drawn second, on top of the first pixmap
'p_intAlpha: Optional. Value between 0 and 255 indicating transparency in addition to any transparency already in the image. Imitates SetAlpha(a/255) command. Only effects the top pixmap.
'p_intStainRed, p_intStainGreen, and p_intStainBlue: Optional. values between 0 and 255 indicating the colorization to be added to the image. Imitates SetColor(r, g, b) command. Only effects the top pixmap.
'p_intHorizontalOffset and p_intVerticalOffset: Optional. Shifts the top pixmap horizontally and vertically relative to the bottom pixmap. Negative values can be specified. Any areas of the top pixmap that are beyond the width or height of the bottom pixmap will be clipped off.
'p_blnFlipVertical and p_blnMirrorHorizontal: Optional. If "True", the image will be flipped about the X-axis and Y-axis, respectivly. Imitates XFlipPixmap() and YFlipPixmap() commands. only efects the top pixmap.
'p_intRotaiton: Optional. Values of 90, 180, or 270 will rotate the image by 90 degrees, 180 degrees, and 270 degrees, respectivly. Imitates SetRotation() command. Value must be a multiple of 90. Only effects the top pixmap.
Function StackPixmapsPrecise:TPixmap(p_pixBottom:TPixmap, p_pixTop:TPixmap, p_intAlpha:Int = 255, p_intStainRed:Int = 255, p_intStainGreen:Int = 255, p_intStainBlue:Int = 255, p_intHorizontalOffset:Int = 0, p_intVerticalOffset:Int = 0, p_blnFlipVertical:Byte = False, p_blnMirrorHorizontal:Byte = False, p_intRotation:Int = 0)
	'Initialize variables
	Local intLoopX:Int, intLoopY:Int 
	Local intOffsetX:Int, intOffsetY:Int, fltAlphaRatio:Float, fltInverseAlphaRatio:Float

	Local intTopPixel:Int, fltTopRed:Float,	fltTopGreen:Float, fltTopBlue:Float, fltTopAlpha:Float
	Local intBottomPixel:Int, fltBottomRed:Float, fltBottomGreen:Float,	fltBottomBlue:Float, fltBottomAlpha:Float
	Local intCompositePixel:Int, intCompositeRed:Int, intCompositeGreen:Int, intCompositeBlue:Int, intCompositeAlpha:Int

	Local intFMR:Int = FMRIndex(p_blnFlipVertical, p_blnMirrorHorizontal, p_intRotation)
	Local intMaxX:Int = p_pixTop.Width - 1
	Local intMaxY:Int = p_pixTop.Height - 1
	Local intBoundsWidth:Int = p_pixBottom.Width
	Local intBoundsHeight:Int = p_pixBottom.Height
	Local pixComposite:TPixmap = p_pixBottom.Copy()

	'Loop through each pixel
	For intLoopX = 0 To intMaxX
		For intLoopY = 0 To intMaxY

			'calculate offset location for later reference
			intOffsetX = intLoopX + p_intHorizontalOffset
			intOffsetY = intLoopY + p_intVerticalOffset

			'skip this pixel if it lies outside the boundaries of the bottom pixmap
			If intOffsetX < intBoundsWidth And intOffsetY < intBoundsHeight And intOffsetX >= 0 And intOffsetY >= 0

				'Get the top pixel at the current location, taking into account flip, mirror and rotation
				Select intFMR
					Case 0
						intTopPixel = p_pixTop.ReadPixel(intLoopX, intLoopY)

					Case 1
						intTopPixel = p_pixTop.ReadPixel(intMaxX - intLoopX, intLoopY)

					Case 2
						intTopPixel = p_pixTop.ReadPixel(intLoopX, intMaxY - intLoopY)

					Case 3
						intTopPixel = p_pixTop.ReadPixel(intMaxX - intLoopX, intMaxY - intLoopY)

					Case 4
						intTopPixel = p_pixTop.ReadPixel(intLoopY, intMaxX - intLoopX)

					Case 5
						intTopPixel = p_pixTop.ReadPixel(intMaxY - intLoopY, intLoopX)

					Case 6
						intTopPixel = p_pixTop.ReadPixel(intMaxY - intLoopY, intMaxX - intLoopX)

					Default '7
						intTopPixel = p_pixTop.ReadPixel(intLoopY, intLoopX)
				EndSelect 

				'Split pixel into Alpha channel
				fltTopAlpha = (intTopPixel Shr 24) & $FF

				'if no alpha or stain modifications are being done and the top pixel is totally opaque, there is no need to composite pixels
				If (Int(fltTopAlpha) & p_intStainRed & p_intStainGreen & p_intStainBlue & p_intAlpha) = 255
					intCompositePixel = intTopPixel
				Else
					'Split pixel into Red, Green, and Blue channels 
					fltTopRed = (intTopPixel Shr 16) & $FF
					fltTopGreen = (intTopPixel Shr 8) & $FF
					fltTopBlue = intTopPixel & $FF

					'add color stain and alpha modification
					If p_intAlpha < 255 Then fltTopAlpha = (fltTopAlpha * p_intAlpha) / 255
					If p_intStainRed < 255 Then fltTopRed = (fltTopRed * p_intStainRed) / 255
					If p_intStainGreen < 255 Then fltTopGreen = (fltTopGreen * p_intStainGreen) / 255
					If p_intStainBlue < 255 Then fltTopBlue = (fltTopBlue * p_intStainBlue) / 255

					'Get the bottom pixel at the offset location
					intBottomPixel = p_pixBottom.ReadPixel(intOffsetX, intOffsetY)

					'Split pixel into Alpha channel
					fltBottomAlpha = (intBottomPixel Shr 24) & $FF

					'if bottom pixel is totally transparent, there is no need to composite pixels
					If fltBottomAlpha = 0 
						intCompositePixel = (Int(fltTopAlpha) Shl 24) | (Int(fltTopRed) Shl 16) | (Int(fltTopGreen) Shl 8) | Int(fltTopBlue)
					Else
						'precalculate alpha ratios 
						fltAlphaRatio = fltTopAlpha / 255
						fltInverseAlphaRatio = 1 - fltAlphaRatio

						'Split pixel into Red, Green, and Blue channels
						fltBottomRed = (intBottomPixel Shr 16) & $FF
						fltBottomGreen = (intBottomPixel Shr 8) & $FF
						fltBottomBlue = intBottomPixel & $FF

						'Perform alpha-composite 
						intCompositeRed = (fltInverseAlphaRatio * fltBottomRed) + (fltAlphaRatio * fltTopRed)
						intCompositeGreen = (fltInverseAlphaRatio * fltBottomGreen) + (fltAlphaRatio * fltTopGreen )
						intCompositeBlue = (fltInverseAlphaRatio * fltBottomBlue) + (fltAlphaRatio * fltTopBlue)
						intCompositeAlpha = (fltInverseAlphaRatio * fltBottomAlpha) + fltTopAlpha

						'combine all channels back into a pixel
						intCompositePixel = (intCompositeAlpha Shl 24) | (intCompositeRed Shl 16) | (intCompositeGreen Shl 8) | intCompositeBlue
					EndIf
				EndIf
				
				'write the pixel to the pixmap at the offset location
				pixComposite.WritePixel(intOffsetX, intOffsetY, intCompositePixel)
			EndIf
		Next
	Next
	Return pixComposite 
EndFunction

'StackPixmapsFast is much faster than StackPixmapsPrecise, but color values may be off by 0.2% at most.
'p_pixBottom: TPixmap imitating the pixmap drawn first, behind the top pixmap
'p_pixTop: TPixmap imitating the pixmap drawn second, on top of the first pixmap
'p_intAlpha: Optional. Value between 0 and 255 indicating transparency in addition to any transparency already in the image. Imitates SetAlpha(a/255) command. Only effects the top pixmap.
'p_intStainRed, p_intStainGreen, and p_intStainBlue: Optional. values between 0 and 255 indicating the colorization to be added to the image. Imitates SetColor(r, g, b) command. Only effects the top pixmap.
'p_intHorizontalOffset and p_intVerticalOffset: Optional. Shifts the top pixmap horizontally and vertically relative to the bottom pixmap. Negative values can be specified. Any areas of the top pixmap that are beyond the width or height of the bottom pixmap will be clipped off.
'p_blnFlipVertical and p_blnMirrorHorizontal: Optional. If "True", the image will be flipped about the X-axis and Y-axis, respectivly. Imitates XFlipPixmap() and YFlipPixmap() commands. only efects the top pixmap.
'p_intRotaiton: Optional. Values of 90, 180, or 270 will rotate the image by 90 degrees, 180 degrees, and 270 degrees, respectivly. Imitates SetRotation() command. Value must be a multiple of 90. Only effects the top pixmap.
Function StackPixmapsFast:TPixmap(p_pixBottom:TPixmap, p_pixTop:TPixmap, p_intAlpha:Int = 255, p_intStainRed:Int = 255, p_intStainGreen:Int = 255, p_intStainBlue:Int = 255, p_intHorizontalOffset:Int = 0, p_intVerticalOffset:Int = 0, p_blnFlipVertical:Byte = False, p_blnMirrorHorizontal:Byte = False, p_intRotation:Int = 0)
	'Initialize variables
	Local intLoopX:Int, intLoopY:Int 
	Local intOffsetX:Int, intOffsetY:Int, intInverseAlphaRatio:Int
	
	Local intTopPixel:Int, intTopRed:Int, intTopGreen:Int, intTopBlue:Int, intTopAlpha:Int
	Local intBottomPixel:Int, intBottomRed:Int, intBottomGreen:Int,	intBottomBlue:Int, intBottomAlpha:Int
	Local intCompositePixel:Int, intCompositeRed:Int, intCompositeGreen:Int, intCompositeBlue:Int, intCompositeAlpha:Int
	
	Local intFMR:Int = FMRIndex(p_blnFlipVertical, p_blnMirrorHorizontal, p_intRotation)
	Local intMaxX:Int = p_pixTop.Width - 1
	Local intMaxY:Int = p_pixTop.Height - 1
	Local intBoundsWidth:Int = p_pixBottom.Width
	Local intBoundsHeight:Int = p_pixBottom.Height
	Local pixComposite:TPixmap = p_pixBottom.Copy()

	'Loop through each pixel
	For intLoopX = 0 To intMaxX
		For intLoopY = 0 To intMaxY

			'calculate offset location for later reference
			intOffsetX = intLoopX + p_intHorizontalOffset
			intOffsetY = intLoopY + p_intVerticalOffset

			'skip this pixel if it lies outside the boundaries of the bottom pixmap
			If intOffsetX < intBoundsWidth And intOffsetY < intBoundsHeight And intOffsetX >= 0 And intOffsetY >= 0

				'Get the top pixel at the current location, taking into account flip, mirror and rotation
				Select intFMR
					Case 0
						intTopPixel = p_pixTop.ReadPixel(intLoopX, intLoopY)

					Case 1
						intTopPixel = p_pixTop.ReadPixel(intMaxX - intLoopX, intLoopY)

					Case 2
						intTopPixel = p_pixTop.ReadPixel(intLoopX, intMaxY - intLoopY)

					Case 3
						intTopPixel = p_pixTop.ReadPixel(intMaxX - intLoopX, intMaxY - intLoopY)

					Case 4
						intTopPixel = p_pixTop.ReadPixel(intLoopY, intMaxX - intLoopX)

					Case 5
						intTopPixel = p_pixTop.ReadPixel(intMaxY - intLoopY, intLoopX)

					Case 6
						intTopPixel = p_pixTop.ReadPixel(intMaxY - intLoopY, intMaxX - intLoopX)

					Default '7
						intTopPixel = p_pixTop.ReadPixel(intLoopY, intLoopX)
				EndSelect 

				'Split pixel into Alpha channel
				intTopAlpha = (intTopPixel Shr 24) & $FF

				'if no alpha or stain modifications are being done and the top pixel is totally opaque, there is no need to composite pixels
				If (intTopAlpha & p_intStainRed & p_intStainGreen & p_intStainBlue & p_intAlpha) = 255
					pixComposite.WritePixel(intOffsetX, intOffsetY, intTopPixel)
				Else
					'Split pixel into Red, Green, and Blue channels 
					intTopRed = ((intTopPixel Shr 16) & $FF)
					intTopGreen = ((intTopPixel Shr 8) & $FF)
					intTopBlue = (intTopPixel & $FF)

					'add color stain and alpha modification
					If p_intAlpha < 255 Then intTopAlpha = (intTopAlpha * (p_intAlpha + 1)) Shr 8
					If p_intStainRed < 255 Then intTopRed = (intTopRed * (p_intStainRed + 1)) Shr 8
					If p_intStainGreen < 255 Then intTopGreen = (intTopGreen * (p_intStainGreen + 1)) Shr 8
					If p_intStainBlue < 255 Then intTopBlue = (intTopBlue * (p_intStainBlue + 1)) Shr 8 

					'Get the bottom pixel at the offset location
					intBottomPixel = p_pixBottom.ReadPixel(intOffsetX, intOffsetY)

					'Split pixel into Alpha channel
					intBottomAlpha = (intBottomPixel Shr 24) & $FF

					'if bottom pixel is totally transparent, there is no need to composite pixels
					If intBottomAlpha = 0 
						pixComposite.WritePixel(intOffsetX, intOffsetY, (intTopAlpha Shl 24) | (intTopRed Shl 16) | (intTopGreen Shl 8) | intTopBlue)
					Else
						'precalculate alpha ratios 
						intInverseAlphaRatio = (intTopAlpha ~ $FF)

						'Split pixel into Red, Green, and Blue channels
						intBottomRed = ((intBottomPixel Shr 16) & $FF) + 1
						intBottomGreen = ((intBottomPixel Shr 8) & $FF) + 1
						intBottomBlue = (intBottomPixel & $FF) + 1

						'Perform alpha-composite 
						intCompositeRed = ((intInverseAlphaRatio * intBottomRed) + (intTopAlpha * intTopRed)) Shr 8
						intCompositeGreen = ((intInverseAlphaRatio * intBottomGreen) + (intTopAlpha * intTopGreen)) Shr 8
						intCompositeBlue = ((intInverseAlphaRatio * intBottomBlue) + (intTopAlpha * intTopBlue)) Shr 8
						intCompositeAlpha = (intInverseAlphaRatio * intBottomAlpha) + intTopAlpha

						'combine all channels back into a pixel and write it to the composite pixmap
						pixComposite.WritePixel(intOffsetX, intOffsetY, (intCompositeAlpha Shl 24) | (intCompositeRed Shl 16) | (intCompositeGreen Shl 8) | intCompositeBlue)
					EndIf
				EndIf
			EndIf
		Next
	Next
	Return pixComposite 
EndFunction

'Since some combinations of XFlip, YFlip and Rotation are equivalent, this function returns the index for the simplest combination of the three for faster processing.
'p_blnFlipVertical: If "True", indicates a flip about the X-axis.
'p_blnMirrorHorizontal: If "True", indicates a flip about the Y-axis.
'p_intRotation: Indicates a rotation by the specified amount in degrees. Angle must be 0, 90, 180 or 270. Any other angles default to 270 degrees.
Function FMRIndex:Int(p_blnFlipVertical:Byte = False, p_blnMirrorHorizontal:Byte = False, p_intRotation:Int = 0)
	If p_blnFlipVertical
		If p_blnMirrorHorizontal
			Select p_intRotation
				Case 0
					'Flip + Mirror = Rotate 180 Degrees
					Return 3 

				Case 90
					'Flip + Mirror + Rotate 90 degrees = Rotate 270 degrees
					Return 5

				Case 180
					'Flip + Mirror + Rotate 180 degrees = Nothing
					Return 0

				Default '270
					'Flip + Mirror + Rotate 270 degrees = Rotate 90 degrees
					Return 4
			EndSelect
		Else
			Select p_intRotation
				Case 0
					'Flip = Mirror + Rotate 180 Degrees
					Return 2

				Case 90
					'Flip + Rotate 90 degrees = Mirror + Rotate 270 degrees
					Return 7

				Case 180
					'Flip + Rotate 180 degrees = Mirror
					Return 1

				Default '270
					'Flip + Rotate 270 degrees = Mirror + Rotate 90 degrees 
					Return 6
			EndSelect
		EndIf
	Else
		If p_blnMirrorHorizontal
			Select p_intRotation
				Case 0
					'Mirror = Flip + Rotate 180 degrees
					Return 1

				Case 90
					'Mirror + Rotate 90 Degrees = Flip + Rotate 270 degrees
					Return 6

				Case 180
					'Mirror + Rotate 180 Degrees = Flip
					Return 2

				Default '270
					'Mirror + Rotate 270 Degrees = Flip + Rotate 90 degrees
					Return 7
			EndSelect
		Else
			Select p_intRotation
				Case 0
					'Nothing = Flip + Mirror + Rotate 180 degrees
					Return 0

				Case 90
					'Rotate 90 Degrees = Flip + Mirror + Rotate 270 degrees
					Return 4

				Case 180
					'Rotate 180 Degrees = Flip + Mirror
					Return 3

				Default '270
					'Rotate 270 Degrees = Flip + Mirror + Rotate 90 degrees
					Return 5
			EndSelect
		EndIf
	EndIf
EndFunction
