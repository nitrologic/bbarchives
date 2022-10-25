; ID: 813
; Author: BlitzSupport
; Date: 2003-10-23 13:52:15
; Title: Chiaroscuro/greyscale buffer
; Description: It's slow and useless! Render in chiaroscuro and greyscale...

; chiatol parameter -- 0 for greyscale, 1-255 for chiaroscuro effect tolerance threshold...

Function GreyScale (buffer, width, height, chiatol = 0)
	tbuffer = GraphicsBuffer ()
	SetBuffer buffer
		LockBuffer buffer
		
			For x = 0 To width - 1
				For y = 0 To height -1
				
					rgb = ReadPixelFast (x, y)
					
					r = rgb Shr 16 And %11111111
					g = rgb Shr 8 And %11111111
					b = rgb And %11111111
	
					trgb = (r + g + b) / 3

					If Not chiatol
						r = trgb
						g = trgb
						b = trgb
					Else
						If trgb < chiatol
							r = 0
							g = 0
							b = 0
						Else
							r = 255
							g = 255
							b = 255
						EndIf
					EndIf
										
					rgb = ((r Shl 16) + (g Shl 8) + b)

					WritePixelFast x, y, rgb
				
				Next
			Next
		
		UnlockBuffer buffer
	SetBuffer tbuffer
End Function
