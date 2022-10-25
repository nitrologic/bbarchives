; ID: 1079
; Author: Pete Rigz
; Date: 2004-06-07 17:54:57
; Title: Contrast Adjuster
; Description: Change the contrast of an image

;----------Quick test of adjust contrast function
Graphics3D 800,600,0,2

SetBuffer FrontBuffer()

test=LoadImage("grass.png")
test2=LoadImage("grass.png")

DrawImage test,5,5

DrawImage adjustcontrast(test,50,32),275,5
DrawImage adjustcontrast(test2,-50,64),5,275

WaitKey()

;-----------

Function AdjustContrast(image,level#,threshold#=64)

	;Contrast adjuster by Peter Rigby

	;set the level to a value between -100 to 100

	;threshold is a colour value between 0-255. Contrast works by exaggerating
	;the colour depending on which side of the threshold it is.

	Local pixel#
	Local red#,green#,blue#
	Local r,g,b
	Local pix

	LockBuffer ImageBuffer(image)

	Local contrast#=(100+level)/100	;set up the contrast to a value between 0-4
	contrast=contrast*contrast
	
	threshold=threshold/255.0		;normalise the threshold


	
	For x = 0 To ImageWidth(image)-1
		For y = 0 To ImageHeight(image)-1
		
			pix = ReadPixelFast(X,Y,ImageBuffer(image))	
			red#=(pix Shr 16) And $ff			;extract the colour values
			green#=(pix Shr 8) And $ff
			blue#=pix And $ff
			
			pixel = red/255.0					;for each colour, set to a value between 0-1
			pixel=pixel-threshold				;take away the threshold
			pixel=pixel*contrast				;if the pixel colour becomes negative then the colour will become darker, otherwise it'll lighten
			pixel=pixel+threshold				;add the threshold back again
			pixel=pixel*255						;convert back to noram colour value
			If pixel < 0 Then pixel = 0		;deal with abnormal colour values
			If pixel > 255 Then pixel = 255
			r=pixel
												;repeat for the other 2 colours
			pixel = green/255.0
			pixel=pixel-threshold
			pixel=pixel*contrast
			pixel=pixel+threshold
			pixel=pixel*255
			If pixel < 0 Then pixel = 0
			If pixel > 255 Then pixel = 255
			g=pixel
			
			pixel = blue/255.0
			pixel=pixel-threshold
			pixel=pixel*contrast
			pixel=pixel+threshold
			pixel=pixel*255
			If pixel < 0 Then pixel = 0
			If pixel > 255 Then pixel = 255
			b=pixel
			
			pix=(b Or (g Shl 8) Or (r Shl 16) Or ($ff000000))
			WritePixelFast x,y,pix,ImageBuffer(image)	;write pixel to image
	   Next
	Next
	UnlockBuffer ImageBuffer(image)
	Return image
End Function
