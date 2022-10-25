; ID: 744
; Author: Koriolis
; Date: 2003-07-10 20:40:12
; Title: Fast Fading
; Description: A fading method that is pretty fast comapred to alpha blended fading in 2D

; === Fast Fading by Koriolis ===

; Brute force alpha blended fading
; Could still be enhanced a bit by storing the image information in an array
Function DrawAlphaFading(alpha%, img1%, img2)
	; NB: img1 and img2 must have the same dimensions
	alpha_inv% = 255 - alpha
	SetBuffer(BackBuffer())
	w% = ImageWidth(img1)-1
	h% = ImageHeight(img1)-1
	img1Buf% = ImageBuffer(img1)
	img2Buf% = ImageBuffer(img2)
	LockBuffer(BackBuffer())
	LockBuffer(img1Buf)
	LockBuffer(img2Buf)
	For i = 0 To w
		For j = 0 To h
			c1% = ReadPixelFast(i, j, img1Buf)
			c2% = ReadPixelFast(i, j, img2Buf)
			r% = (((c1 And 255) * alpha) + ((c2 And 255) * alpha_inv)) Shr 8
			g% = ((((c1 Shr 8) And 255) * alpha) + (((c2 Shr 8) And 255) * alpha_inv)) Shr 8
			b% = ((((c1 Shr 16) And 255) * alpha) + (((c2 Shr 16) And 255) * alpha_inv)) Shr 8
			WritePixel(i, j, r Or (g Shl 8) Or (b Shl 16))
		Next
	Next
	UnlockBuffer(img1Buf)
	UnlockBuffer(img2Buf)
	UnlockBuffer(BackBuffer())
End Function

; brute force "new" fading
Function DrawSlowFading(grayLevel%, img%, mixMap%)
	SetBuffer(BackBuffer())
	w% = ImageWidth(img)-1
	h% = ImageHeight(img)-1
	imgBuf% = ImageBuffer(img)
	mixMapBuf% = ImageBuffer(mixMap)
	LockBuffer(BackBuffer())
	LockBuffer(imgBuf)
	LockBuffer(mixMapBuf)
	For i = 0 To w
		For j = 0 To h
			a% = ReadPixelFast(i, j, mixMapBuf) And 255
			If a = grayLevel Then				
				WritePixelFast(i, j, ReadPixelFast(i, j, imgBuf))
			EndIf
		Next
	Next
	UnlockBuffer(mixMapBuf)
	UnlockBuffer(imgBuf)
	UnlockBuffer(BackBuffer())
End Function


Dim CFFP_counts%(256)
Function CreateFastFadingPattern%(fadingImg%)
	w% = ImageWidth(fadingImg)
	h% = ImageHeight(fadingImg)
	bank% = CreateBank(w*h*4 + 1028)
	For layer = 0 To 256
		CFFP_counts(layer) = 0
	Next
	fadingImgBuf% = ImageBuffer(fadingImg)
	w = w - 1
	h = h - 1
	LockBuffer(fadingImgBuf)
	For i = 0 To w
		For j = 0 To h
			a% = (ReadPixelFast(i, j, fadingImgBuf) And 255) + 1
			CFFP_counts(a) = CFFP_counts(a) + 4
		Next
	Next
	PokeInt(bank, 0, CFFP_counts(0))
	For layer = 1 To 256
		CFFP_counts(layer) = CFFP_counts(layer) + CFFP_counts(layer-1)
		CFFP_counts(layer-1) = CFFP_counts(layer-1) + 1028
		PokeInt(bank, layer Shl 2, CFFP_counts(layer))
	Next
	CFFP_counts(256) = CFFP_counts(256) + 1028
	For i = 0 To w
		For j = 0 To h
			a% = ReadPixelFast(i, j, fadingImgBuf) And 255
			PokeShort(bank, CFFP_counts(a), i)
			PokeShort(bank, CFFP_counts(a)+2, j)
			CFFP_counts(a) = CFFP_counts(a) + 4
		Next
	Next
	UnlockBuffer(fadingImgBuf)
	Return bank
End Function

; optimized "new" fading
Function DrawFastFading(grayLevel%, img%, fadingPattern%)
	SetBuffer(BackBuffer())
	st% = PeekInt(fadingPattern, grayLevel Shl 2)
	en% = PeekInt(fadingPattern, (grayLevel+1) Shl 2) - 1
	imgBuf% = ImageBuffer(img)
	LockBuffer(BackBuffer())
	LockBuffer(imgBuf)
	For ofs = st To en Step 4
		i% = PeekShort(fadingPattern, ofs)
		j% = PeekShort(fadingPattern, ofs + 2)
		WritePixel(i, j, ReadPixelFast(i, j, imgBuf))
	Next
	UnlockBuffer(imgBuf)
	UnlockBuffer(BackBuffer())
End Function


;============================ little test ===============================

Graphics 256,256,16,2

; TODO: replace these images by two 256x256 images of your own
img1% = LoadImage("adobe.jpg")
img2% = LoadImage("MossyGround.bmp")

frame% = 0

vsync% = Lower(Input("VSync ? (y/n) "))="y"
Print("")
Print("1) Alpha blended fading, slooow")
Print("2) Slow fading (almost as slow)")
Print("3) Fast fading (really fast)")
Select Input("Select method ")
	Case 1
		Color 0,0,255
		While Not KeyHit(1)
			If (frame And 255) = 0 Then
				DrawImage(img1, 0, 0)
				startMS% = MilliSecs()
			EndIf
			DrawAlphaFading(frame And 255, img1, img2)
			Flip(vsync)
			frame = frame + 1
			If (frame And 255) = 255
				Cls
				Print "Duration : " + (MilliSecs() - startMS) + " ms"
			EndIf
			Wend

	Case 2
		Color 0,0,255
		While Not KeyHit(1)
			If (frame And 255) = 0 Then
				If mixMap <> 0 Then FreeImage(mixMap)
				mixMap% = CreateTestFadingImage(2*Rand(-3,3), Rnd(0, 20))
				DrawImage(mixMap, 0, 0)
				Text 128, 5, "Press a key", True
				Flip()
				WaitKey()
				startMS% = MilliSecs()
			EndIf
			DrawSlowFading(frame And 255, img2, mixMap)
			Flip(vsync)
			frame = frame + 1
			If (frame And 255) = 255
				Cls
				Print "Duration : " + (MilliSecs() - startMS) + " ms"
			EndIf
			Wend

	Case 3
		Color 0,0,255
		While Not KeyHit(1)
			If (frame And 255) = 0 Then
				If mixMap <> 0 Then FreeImage(mixMap)
				mixMap% = CreateTestFadingImage(2*Rand(-3,3), Rnd(0, 20))
				fadingPattern% = CreateFastFadingPattern(mixMap)
				DrawImage(mixMap, 0, 0)
				Text 128, 5, "Press a key", True
				Flip()
				WaitKey()
				DrawImage(img1, 0, 0)
				startMS% = MilliSecs()
			EndIf
			DrawFastFading(frame And 255, img2, fadingPattern)
			Flip(vsync)
			frame = frame + 1
			If (frame And 255) = 255
				Cls
				Print "Duration : " + (MilliSecs() - startMS) + " ms"
			EndIf
		Wend
		FreeBank(fadingPattern)

		Default Print "Invalid option, quiting..."
				Delay(2000)
				End
	End Select
End


Function CreateTestFadingImage%(coeff#, swirlStrength#)
	coeff = coeff*255.0/360.0
	img% = CreateImage(256,256)
	imgBuf% = ImageBuffer(img)
	LockBuffer(imgBuf)
	For i = 0 To 255
		For j = 0 To 255
			c% = ((ATan((i-128)/(j-128.5))*coeff) + swirlStrength*Sqr((i-128)*(i-128)+(j-128)*(j-128))) And 255
			WritePixelFast(i, j, c * $010101, imgBuf)
		Next
	Next
	UnlockBuffer(imgBuf)
	Return img
End Function
