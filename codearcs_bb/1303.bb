; ID: 1303
; Author: sswift
; Date: 2005-02-28 13:52:58
; Title: ScaleImageFast
; Description: This function scales an image an arbitrary amount on each axis, and is 80x faster than the default ScaleImage function in Blitz!

; -------------------------------------------------------------------------------------------------------------------------------------
; This function scales an image an arbitrary amount on the X and Y axis, and returns a pointer to the new image.
; The original image is not modified.
;
; This function is 80x faster than the ScaleImage function that comes with Blitz! 
; -------------------------------------------------------------------------------------------------------------------------------------
Function ScaleImageFast(SrcImage, ScaleX#, ScaleY#)

	Local SrcWidth,  SrcHeight
	Local DestWidth, DestHeight
	Local ScratchImage, DestImage
	Local SrcBuffer, ScratchBuffer, DestBuffer
	Local X1, Y1, X2, Y2

	; Get the width and height of the source image. 	
		SrcWidth  = ImageWidth(SrcImage)
		SrcHeight = ImageHeight(SrcImage)

	; Calculate the width and height of the dest image.
		DestWidth  = Floor(SrcWidth  * ScaleX#)
		DestHeight = Floor(SrcHeight * ScaleY#)

	; If the image does not need to be scaled, just copy the image and exit the function.
		If (SrcWidth = DestWidth) And (SrcHeight = DestHeight) Then Return CopyImage(SrcImage)

	; Create a scratch image that is as tall as the source image, and as wide as the destination image.
		ScratchImage = CreateImage(DestWidth, SrcHeight)
				
	; Create the destination image.
		DestImage = CreateImage(DestWidth, DestHeight) 

	; Get pointers to the image buffers.
		SrcBuffer     = ImageBuffer(SrcImage)
		ScratchBuffer = ImageBuffer(ScratchImage)
		DestBuffer    = ImageBuffer(DestImage)

	; Duplicate columns from source image to scratch image.
		For X2 = 0 To DestWidth-1
			X1 = Floor(X2 / ScaleX#)
			CopyRect X1, 0, 1, SrcHeight, X2, 0, SrcBuffer, ScratchBuffer
		Next
			
	; Duplicate rows from scratch image to destination image.
		For Y2 = 0 To DestHeight-1
			Y1 = Floor(Y2 / ScaleY#)
			CopyRect 0, Y1, DestWidth, 1, 0, Y2, ScratchBuffer, DestBuffer
		Next
						
	; Free the scratch image.
		FreeImage ScratchImage					
						
	; Return the new image.
		Return DestImage
					
End Function
