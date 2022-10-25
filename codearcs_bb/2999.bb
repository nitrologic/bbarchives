; ID: 2999
; Author: Zethrax
; Date: 2012-11-05 03:27:34
; Title: Shows the ordering of pixel data in an integer value
; Description: Some code which demonstrates how pixel data is ordered within an integer used with the pixel read and write commands.

Global G_int_bank = CreateBank( 4 )

Graphics 800, 600, 0, 2


image = CreateImage( 64, 64 )
SetBuffer ImageBuffer( image )
For x = 0 To 63
	For y = 0 To 63
		PokeInt G_int_bank, 0, 0 ; Clear the bank.
		PokeByte G_int_bank, 0, 255 ; Store 255 in slot 0.
		WritePixel x, y, PeekInt( G_int_bank, 0 )
	Next
Next 
image1a = image


image = CreateImage( 64, 64 )
SetBuffer ImageBuffer( image )
For x = 0 To 63
	For y = 0 To 63
		PokeInt G_int_bank, 0, 0 ; Clear the bank.
		PokeByte G_int_bank, 1, 255 ; Store 255 in slot 1.
		WritePixel x, y, PeekInt( G_int_bank, 0 )
	Next
Next 
image2a = image


image = CreateImage( 64, 64 )
SetBuffer ImageBuffer( image )
For x = 0 To 63
	For y = 0 To 63
		PokeInt G_int_bank, 0, 0 ; Clear the bank.
		PokeByte G_int_bank, 2, 255 ; Store 255 in slot 2.
		WritePixel x, y, PeekInt( G_int_bank, 0 )
	Next
Next 
image3a = image


image = CreateImage( 64, 64 )
SetBuffer ImageBuffer( image )
For x = 0 To 63
	For y = 0 To 63
		PokeInt G_int_bank, 0, 0 ; Clear the bank.
		PokeByte G_int_bank, 3, 255 ; Store 255 in slot 3.
		WritePixel x, y, PeekInt( G_int_bank, 0 )
	Next
Next 
image4a = image

;---

image = CreateImage( 64, 64 )
SetBuffer ImageBuffer( image )
For x = 0 To 63
	For y = 0 To 63
		WritePixel x, y, 255 ; Set the least significant byte value to 255.
	Next
Next 
image1b = image


image = CreateImage( 64, 64 )
SetBuffer ImageBuffer( image )
For x = 0 To 63
	For y = 0 To 63
		WritePixel x, y, 255 * ( 2 ^ 8 ) ; Set the second byte value to 255.
	Next
Next 
image2b = image


image = CreateImage( 64, 64 )
SetBuffer ImageBuffer( image )
For x = 0 To 63
	For y = 0 To 63
		WritePixel x, y, 255 * ( 2 ^ 16 ) ; Set the third byte value to 255.
	Next
Next 
image3b = image


image = CreateImage( 64, 64 )
SetBuffer ImageBuffer( image )
For x = 0 To 63
	For y = 0 To 63
		WritePixel x, y, 255 * ( 2 ^ 24 ) ; Set the most significant byte value to 255.
	Next
Next 
image4b = image


SetBuffer BackBuffer()

y = o
Text 0, y, "This shows the arrangement of pixel bytes in a bank."
y = y + 20
Text 0, y, "Slot 0 = Blue. Slot 1 = Green. Slot 2 = Red. Slot 3 = Alpha"
y = y + 20
DrawImage image1a, 0, y
DrawImage image2a, 64, y
DrawImage image3a, 128, y
DrawImage image4a, 192, y
y = y + 64 + 20

Text 0, y, "This shows the arrangement of pixel data in an integer value."
y = y + 20
Text 0, y, "Left = Least significant byte (value: 255). Right = Most significant byte (value: 4278190080)."
y = y + 20
DrawImage image1b, 0, y
DrawImage image2b, 64, y
DrawImage image3b, 128, y
DrawImage image4b, 192, y

Flip 

WaitKey
End
