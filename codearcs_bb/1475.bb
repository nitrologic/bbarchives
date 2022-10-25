; ID: 1475
; Author: Grey Alien
; Date: 2005-10-05 18:53:52
; Title: Anti-alias logo onto background
; Description: Anti-alias logo onto background

Global ScreenWidth% = 800
Global ScreenHeight% = 600
Dim ScreenPixels%(ScreenWidth*ScreenHeight)
Dim ImagePixels%(ScreenWidth*ScreenHeight)
Dim amPixels#(ScreenWidth*ScreenHeight)

Function ccDrawImageAlpha(image%, bx, by, alphamap%, background%)
	;Use an Alphamap image to blend an image onto the background.
	;Alphamap MUST be same size as image.
	;Background MUST be same size or larger
	;bx and by are the coords of where the image should be drawn on the background
	Local w = ImageWidth(image)
	Local h = ImageHeight(image)
		
	ccAlphaDrawSetup(image, bx, by, alphamap, background)
	ccAlphaDraw(bx, by, w, h)
End Function

Function ccAlphaDrawSetup(image%, bx, by, alphamap%, background%)
	Local w = ImageWidth(image)
	Local h = ImageHeight(image)
	Local ami = 0	
	;Read the alphamap into an array
	SetBuffer ImageBuffer(alphamap)
	LockBuffer ImageBuffer(alphamap)
	For x = 0 To w-1
		For y = 0 To h-1
			;make it into a number in the range 0 to 1
			amPixels(ami) = (ReadPixelFast(x,y) And 255)/255.0 ;we only care about greyscale!
			ami = ami + 1
		Next
	Next		
	UnlockBuffer ImageBuffer(alphamap)	
	
	;Read the image pixels into an array
	ccReadImagePixelsVert(image%)
	
	;Read the background into an array	
	SetBuffer ImageBuffer(background)
	LockBuffer ImageBuffer(background)
	Local counter = 0
	For x = 0 To w-1
		For y = 0 To h-1
			ScreenPixels(counter) = ReadPixelFast(bx+x, by+y)
			Counter = Counter + 1
		Next
	Next
	UnlockBuffer ImageBuffer(background)	
	SetBuffer BackBuffer() ;important
End Function

Function ccAlphaDraw(bx, by, w, h)
	;Draws on the BackBuffer
	;(could be modified to draw onto an image for storage and use later)
	LockBuffer BackBuffer()	
	Local n = 0
	For x = bx To bx+w-1
		For y = by To by+h-1
			;Check alpha map
			;If 0, don't draw
			Local amRGB# = amPixels(n)
			If amRGB > 0 Then
				;If 255, then draw normally
				If amRGB = 1 Then
					Local RGB = ImagePixels(n)
					WritePixelFast x,y, RGB 
				Else
					;alpha blend!
					Local im = ImagePixels(n)
					Local back = ScreenPixels(n)
					Local am# = amPixels(n)	
					;get background colours
					bR = (back Shr 16) And 255 
					bG = (back Shr 8 ) And 255
					bB = (back       ) And 255			
					;work out difference between colours	
					;then multiply difference by alphamap
					dR = (((im Shr 16) And 255) - bR)*am  
					dG = (((im Shr 8 ) And 255) - bG)*am
					dB = (((im       ) And 255) - bB)*am
					;work out and draw final colour in the right place
					WritePixelFast x,y, ((bR+dR) Shl 16) Or ((bG+dG) Shl 8) Or (bB+dB)
				EndIf
			EndIf
			n = n +1
		Next
	Next	
	UnlockBuffer BackBuffer()	
End Function

; -----------------------------------------------------------------------------
; ccReadImagePixelsVert
; -----------------------------------------------------------------------------
Function ccReadImagePixelsVert(image%)
	;Read an image into an array of pixels vertically for faster processing.
	Local counter = 0
	Local x,y
	;lock the buffer
	LockBuffer ImageBuffer(image)
	SetBuffer ImageBuffer(image)
	;read into an array of pixels
	For x = 0 To ImageWidth(image)-1
		For y = 0 To ImageHeight(image)-1
			ImagePixels(counter) = ReadPixelFast(x, y)
			Counter = Counter + 1
		Next
	Next
	;unlock the buffer
	UnlockBuffer ImageBuffer(image)
End Function
