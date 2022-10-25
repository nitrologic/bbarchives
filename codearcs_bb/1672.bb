; ID: 1672
; Author: Grey Alien
; Date: 2006-04-14 09:25:19
; Title: Gradient Fill Image
; Description: Gradient Fill Image

; -----------------------------------------------------------------------------
; ccGradientFill
; -----------------------------------------------------------------------------
Function ccGradientFill(Temp%, SourceCol%, StartCol%, EndCol%)
	;temp is an image
	;-1 ($ffffffff) is white (good for sourcecol)
	SetBuffer ImageBuffer(Temp)
	LockBuffer ImageBuffer(Temp)

	Local x
	Local y#
	Local col = 0
	
	For y = 0 To ImageHeight(Temp)-1
		col = ccBlendColours(StartCol, EndCol, y/ImageHeight(Temp))
		For x = 0 To ImageWidth(Temp)-1
			r = ReadPixelFast(x, y)		
			If r = SourceCol Then				
				WritePixelFast(x, y, col)
			EndIf
		Next
	Next
	
	UnlockBuffer ImageBuffer(Temp)

	;don't reset buffer, calling proc must do it if it wants to.
End Function

; -----------------------------------------------------------------------------
; ccBlendColours
; -----------------------------------------------------------------------------
Function ccBlendColours%(st%, en%, ratio#)
	;use RGB values adjusted with ARGB_Black
	;ratio# must be 0 to 1
	Rst = (st Shr 16) And 255 
	Gst = (st Shr 8 ) And 255
	Bst = st And 255		

	Ren = (en Shr 16) And 255 
	Gen = (en Shr 8 ) And 255
	Ben = en And 255		
			
	Rdiff=  Rst-Ren
	Gdiff=  Gst-Gen
	Bdiff=  Bst-Ben

	Rdiff = Rdiff * ratio#
	Gdiff = Gdiff * ratio#
	Bdiff = Bdiff * ratio#
			
	Return (((Rst-Rdiff) Shl 16) Or ((Gst-Gdiff) Shl 8) Or (Bst-Bdiff))				
End Function
