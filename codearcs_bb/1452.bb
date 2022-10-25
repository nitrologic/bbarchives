; ID: 1452
; Author: Grey Alien
; Date: 2005-08-25 19:35:59
; Title: Mirror Image
; Description: Several horizontal image mirroring functions

; -----------------------------------------------------------------------------
; Mirror an Image horizontally
; -----------------------------------------------------------------------------
Function ccMirrorImageHoriz(TheImage%)
	;make a copy to work on
	TempImage% = CopyImage(TheImage)
	;flip it
	TFormImage TempImage, -1, 0, 0, 1	
	;now draw it back to the original image
	;note the corrected x coord
	SetBuffer ImageBuffer(TheImage)
	DrawBlock(TempImage, ImageWidth(TempImage), 0)
	FreeImage(TempImage)
	SetBuffer BackBuffer()
End Function

; -----------------------------------------------------------------------------
; Create an Image from another Mirrored horizontally
; -----------------------------------------------------------------------------
Function ccCopyMirrorImageHoriz%(TheImage%)
	;make a copy to work on
	TempImage% = CopyImage(TheImage)
	ccMirrorImageHoriz(TempImage)
	Return TempImage
	;Don't free temp image as it is going to be used by calling procedure.
End Function

; -----------------------------------------------------------------------------
; Mirror an Anim Image horizontally
; -----------------------------------------------------------------------------
Function ccMirrorAnimImageHoriz(TheImage%, Frames%)
	;make an image to work on
	Local w = ImageWidth(TheImage)
	Local h = ImageHeight(TheImage)
	TempImage% = CreateImage(w, h, 1, 1) ;need flag 1 to avoid mask problems in 16 bit colour
	Local T2% = CreateImage(w, h, 1, 1) ;same again
	
	For i = 0 To Frames-1	
		;make a copy of the current frame to work on
		SetBuffer ImageBuffer(TempImage)
		DrawBlock(TheImage, 0, 0, i)
		
		;flip it
		TFormImage TempImage, -1, 0, 0, 1	
		SetBuffer ImageBuffer(TempImage) ;for some reason it fails to mirror without this line!
		;now grab it back to the original image in the correct frame
		GrabImage(TheImage, 0, 0, i)		
	Next

	FreeImage(TempImage)
	FreeImage(T2)
	SetBuffer BackBuffer()
End Function

; -----------------------------------------------------------------------------
; Create an Anim Image from another Mirrored horizontally
; -----------------------------------------------------------------------------
Function ccCopyMirrorAnimImageHoriz%(TheImage%, Frames%)
	;make a copy to work on
	TempImage% = CopyImage(TheImage)
	ccMirrorAnimImageHoriz(TempImage, Frames)
	Return TempImage
	;Don't free temp image as it is going to be used by calling procedure.
End Function
