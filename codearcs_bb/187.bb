; ID: 187
; Author: ascii
; Date: 2002-01-14 15:39:26
; Title: alpha_engine v.6
; Description: Uses banks to precalc an image for alphablending

Function alpha_precalc_image(img)

	imgwidth	= ImageWidth(img) - 1
	imgheight	= ImageHeight(img) - 1

	framesize = (imgwidth + 1) * (imgheight + 1) * 4		; no of bytes pr. 'frame'

	bsize = (framesize * 101) + 8					; banksize + little extra (until i bother debugging)
			
	bank = CreateBank(bsize)
		
	PokeShort bank,0,imgwidth
	PokeShort bank,2,imgheight
	
	pointer = 4	
	
	SetBuffer ImageBuffer(img)

	LockBuffer ImageBuffer(img)

	For z# = 0 To 1 Step 0.01

		For y = 0 To (imgheight)
	
			For x = 0 To (imgwidth)

				col		= (ReadPixelFast(x,y) And $FFFFFF)
				r		= Int(((col And $FF0000) Shr 16) * z#)
				g		= Int(((col And $FF00) Shr 8) * z#)
				b		= Int((col And $FF) * z#)

				lum		= r
				
				If g > lum
				
					lum = g
					
				ElseIf b > lum
				
					lum = b
					
				EndIf

				PokeByte bank,pointer,r
				PokeByte bank,pointer + 1,g
				PokeByte bank,pointer + 2,b
				PokeByte bank,pointer + 3,lum 

				pointer = pointer + 4
	
			Next
	
		Next

	Next

	UnlockBuffer ImageBuffer(img)

	FreeImage(img)

	SetBuffer BackBuffer()

	Return bank

End Function

Function alpha_render_image(bank,scrx,scry,alpha#)

	imgwidth	= PeekShort(bank,0)
	imgheight	= PeekShort(bank,2)

	scrx = scrx - (imgwidth / 2)
	scry = scry - (imgheight / 2)

	framesize	= (imgwidth + 1) * (imgheight + 1) * 4

	frameno	= Int(alpha# * 100)

	pointer = (framesize * frameno) + 4

	destalpha# = 1 - alpha#

	If alpha# > 1 Then alpha# = 1
	If alpha# < 0 Then alpha# = 0

	LockBuffer BackBuffer()

	For y = 0 To (imgheight)

		For x = 0 To (imgwidth)

			sx = scrx + x
			sy = scry + y
			
			If sx > 0 And sx < (scrw - 1) And sy > 0 And sy < (scrh - 1)

				dcol = (ReadPixelFast(sx,sy) And $FFFFFF)

				If dcol < $FFFFFF

					lum	= PeekByte(bank,pointer + 3)
		
					fr	= ((dcol And $FF0000) Shr 16) + PeekByte(bank,pointer)
					fg	= ((dcol And $FF00) Shr 8) + PeekByte(bank,pointer + 1)
					fb	= (dcol And $FF) + PeekByte(bank,pointer + 2)
					
					If fr > 255 Then fr = 255
					If fg > 255 Then fg = 255
					If fb > 255 Then fb = 255
	
					dlum = fr
					
					If fg > dlum
					
						dlum = fg
						
					ElseIf fb > dlum
					
						dlum = fb
						
					EndIf
	
					If dlum >= lum
	
						WritePixelFast sx,sy,(fr Shl 16) Or (fg Shl 8) Or fb
	
					EndIf

				EndIf

			EndIf 

			pointer = pointer + 4
		
		Next

	Next

	UnlockBuffer BackBuffer()

End Function
