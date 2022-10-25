; ID: 2353
; Author: Ryudin
; Date: 2008-11-02 04:51:30
; Title: 2D Alpha Blending
; Description: Blends images (as originally posted by WebDext, with a bug fixed).

;Note - I changed the function name and order of parameters a bit for easiness.

;AS ORIGINALLY POSTED:
; ID: 236
; Author: WebDext
; Date: 2002-02-13 01:17:50
; Title: AlphaBlit
; Description: Yet another way to alpha blit.

Dim pixelData(5000,5000)

Function DrawImageAlpha(Image, xOff, yOff, DestBuffer, Alpha#)
	;This function alpha blits any image smaller than 100,50 onto a desitantion
	;buffer of equal or lesser size, It has no error handling to speed up the 
	;function.
	
	;DestBuffer is the pointer to the desitnation image.
	;ImgBuffer is the pointer to the image to be Alpha Blited.
	;xOff, yOff are the ofsets of the image on the destination image.
	;Alpha ranges from 0 to 1 and is the opacity of the image.

	InvAlpha# 	= 1.0 - Alpha#
	ImgBuffer 	= ImageBuffer(Image)
	iWidth 		= ImageWidth(Image)
	iHeight 	= ImageHeight(Image)
	
	LockBuffer ImgBuffer
		For x = 0 To iWidth-1
			For y = 0 To iHeight-1
				If (x + xOff) < WIDTH And (y + yOff) < HEIGHT Then
					pixelData(x, y) = ReadPixelFast(x,y,ImgBuffer)
				EndIf
			Next
		Next
	UnlockBuffer ImgBuffer
	LockBuffer DestBuffer
		For x = 0 To iWidth-1
			For y = 0 To iHeight-1
				If (x + xOff) < WIDTH And (y + yOff) < HEIGHT Then
					PixA# = ReadPixelFast(x + xOff, y + yOff)
					PixB# = pixelData(x, y)
					
					;Calculate Alphas with the least number of computations
					aR = Int(PixA Sar 16)
					aRI = aR Shl 16
					aG = Int((PixA - aRI) Sar 8)
					aB = PixA - (aRI + (aG Shl 8))
	
					;Calculate Alphas with the least number of computations
					bR = Int(PixB Sar 16)
					bRI = bR Shl 16
					bG = Int((PixB - bRI) Sar 8)
					bB = PixB - (bRI + (bG Shl 8))
					
					;Apply Alphas
					If bR > 0 Or bG > 0 Or bB > 0
						nR = (aR * InvAlpha) + (bR * Alpha)
						nG = (aG * InvAlpha) + (bG * Alpha)
						nB = (aB * InvAlpha) + (bB * Alpha)
					
						;Write Pixel to Buffer
						WritePixelFast x + xOff, y + yOff, (nR Shl 16) + (nG Shl 8) + nB
					EndIf
				EndIf
			Next
		Next
	UnlockBuffer DestBuffer
End Function
