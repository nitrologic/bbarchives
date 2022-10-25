; ID: 2400
; Author: BlitzSupport
; Date: 2009-01-25 15:14:31
; Title: DrawBank
; Description: Draw image directly from bank

f$ = "boing.png"

Graphics 1024, 768, 0, 2

; IMPORTANT! Required by DrawBank!

Global GW = GraphicsWidth ()
Global GH = GraphicsHeight ()
Global DB_RGB

SetDrawBankMask 255, 0, 255

Function SetDrawBankMask (r, g, b)

	tempbuffer = GraphicsBuffer ()
	temp = CreateImage (1, 1)

	SetBuffer ImageBuffer (temp)

	Plot 0, 0: GetColor 0, 0
	tr = ColorRed ()
	tg = ColorGreen ()
	tb = ColorBlue ()

	Color r, g, b: Plot 0, 0: GetColor 0, 0
	r = ColorRed ()
	g = ColorGreen ()
	b = ColorBlue ()

	FreeImage temp

	DB_RGB = ((r Shl 16) + (g Shl 8) + b) Or ~$00FFFFFF

	SetBuffer tempbuffer

	Color tr, tg, tb
	
End Function

Function DrawBank (bank, x, y, width, height, center = 0, masked = True)
	
	; Centre image on screen if required...
	
	If center
		x = x - width / 2
		y = y - height / 2
	EndIf
	
	; Alpha mask...
	
	mask = ((r Shl 16) + (g Shl 8) + b) Or ~$00FFFFFF

	; Scan across image...
	
	For w = 0 To width - 1

		; x-position plus current 'pixel' NOT off right of screen?

		If x + w < GW

			; Not off left of screen?
			
			If x + w > -1

				; Scan down image...
				
				For h = 0 To height - 1

					; Not off bottom of screen?
					
					If y + h < GH
	
						; Not off top of screen?
						
						If y + h > -1

							; Get current value in bank...
							
							argb = PeekInt (bank, pointer)
							
							; Assuming most cases use transparent mask...
							
							If argb <> DB_RGB
								WritePixelFast x + w, y + h, argb
							Else
								; User wants mask drawn...
								If Not masked
									WritePixelFast x + w, y + h, argb
								EndIf
							EndIf
							
							; Move to next value in bank (argb = 4 bytes = integer, hence PeekInt above)...
							
							pointer = pointer + 4

						Else
							
							; Off top of screen, so skip all pixels until on screen...
							
							h = h + (-y) - 1
							pointer = pointer + (h Shl 2) + 4
							
						EndIf	

					Else
						
						; Off bottom of screen, stop drawing this column...
						
						pointer = pointer + (height - h) Shl 2
						Exit
					
					EndIf
					
				Next

			Else
				
				; Off left of screen, so skip all pixels until on screen...
				
				pointer = pointer + (height - h) Shl 2
				
			EndIf

		Else
			
			; Off right of screen, so stop drawing...
			
			Exit
			
		EndIf
		
	Next
	
End Function

Function ImageToBank (bufferImage)

	; Slightly modified from http://www.blitzbasic.com/codearcs/codearcs.php?code=396
	; By Perturbatio
	
	bankImage = CreateBank ()
	
	bufOldBuffer = GraphicsBuffer ()
		
	SizeOfImage = ImageWidth (bufferImage) * ImageHeight (bufferImage)
	
	ResizeBank bankImage, (SizeOfImage * 4)
	
	SetBuffer ImageBuffer (bufferImage)
	LockBuffer ImageBuffer (bufferImage)

	For iLoopX = 0 To ImageWidth (bufferImage) - 1
		For iLoopY = 0 To ImageHeight (bufferImage) - 1
			PokeInt bankImage, ibankPointer, ReadPixelFast (iLoopX, iLoopY)
			iBankPointer = iBankPointer + 4
		Next
	Next

	UnlockBuffer ImageBuffer (bufferImage)
	SetBuffer bufOldBuffer
	
	Return bankImage
	
End Function

; Test...

ClsColor 64, 96, 128

SetBuffer BackBuffer ()

image = LoadImage (f$)

width = ImageWidth (image)
height = ImageHeight (image)

bank = ImageToBank (image)
FreeImage image

Repeat

	Cls
	
	mx = MouseX ()
	my = MouseY ()
	
	If KeyDown (203) Then xs# = xs - 0.1
	If KeyDown (205) Then xs = xs + 0.1
	If KeyDown (200) Then ys# = ys - 0.1
	If KeyDown (208) Then ys = ys + 0.1

	If mx <> lastx
		x# = mx
		xs = 0
	Else
		x = x + xs
	EndIf
	
	If my <> lasty
		y# = my
		ys = 0
	Else
		y = y + ys
	EndIf
	
	lastx = mx
	lasty = my
	
	LockBuffer BackBuffer ()
	
		ms = MilliSecs ()
		DrawBank bank, x, y, width, height, 1
		ticks = MilliSecs () - ms
	
	UnlockBuffer BackBuffer ()

	Text 20, 20, ticks

	Flip
	
Until KeyHit (1)

End
