; ID: 2633
; Author: Matty
; Date: 2009-12-23 17:11:34
; Title: Simple Image Encryption
; Description: A Very simple method for encrypting an image

Graphics 800,600,0,2
image=LoadImage("your image file goes here")
SetBuffer ImageBuffer(image)
iw=ImageWidth(image)-1
ih=ImageHeight(image)-1
SeedRnd 1
LockBuffer
For x=0 To iw
	For y=0 To ih
		WritePixelFast x,y,ReadPixelFast(x,y) Xor Rand(16777215)
	Next
Next
UnlockBuffer
SetBuffer BackBuffer()
DrawImage image,0,0
Flip
WaitKey
SeedRnd 1
For x=0 To iw
	LockBuffer
	For y=0 To ih
		Col=Rand(16777215)
		If y<GraphicsHeight() And x<GraphicsWidth() Then 
			WritePixelFast x,y,ReadPixelFast(x,y) Xor Col
		EndIf 
	Next
	UnlockBuffer
	Flip False
Next
End
