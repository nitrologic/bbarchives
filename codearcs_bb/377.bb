; ID: 377
; Author: MuffinRemnant
; Date: 2002-07-31 12:58:21
; Title: Negative
; Description: Create a negative of an image

Graphics 800,600,16,1
SetBuffer BackBuffer()
Global av#

; hardcoded for the named bitmap 64x64 pixels...
img=LoadImage("test.bmp")


For y=0 To 63
	For x=0 To 63


		SetBuffer(ImageBuffer(img))		
		GetColor(x,y)
		
		r=ColorRed()
		g=ColorGreen()
		b=ColorBlue()
		
		av=255-(r+g+b)/3
		
		Color av,av,av
		SetBuffer BackBuffer()

		Plot x,y
		
	Next
Next

Flip

WaitKey()

End 
