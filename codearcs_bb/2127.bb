; ID: 2127
; Author: Pongo
; Date: 2007-10-26 11:35:54
; Title: BlitzComp
; Description: Composite Blend modes

; Composite modes, by Pongo 10-26-07
; This code will let you composite multiple images together, using blending modes.
; There are different functions for the different modes, but they all work the same way

; Simply call the function with 2 images (front image first) or optionally use a 3rd image for an alpha mask.
; If you do not specify a mask image, the composite will work in "clip" mode, And cut out 0,0,0 black.

; Additionally, you can supply extra RGB values, and the top image will be tinted to that color. Run this example to see it all work.

; Note: This code requires all of the images to be the same pixel size.

; last updated 10-26 cleaned up a bit and fixed a problem with non-square images.

Graphics 640,480,0,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

;set the initial image
img_comp = compimage_mask("image1.png", "image2.png","mask.png")
info$ = "3) mask mode"

While Not KeyHit(1)

	If KeyHit (2) ; 1 key
		img_comp =  compimage_mask("image1.png", "image2.png")
		info$ = "1) clip mode"
	EndIf 

	If KeyHit (3) ; 2 key
		img_comp =  compimage_mask("image1.png", "image2.png",0,Rnd(255),Rnd(255),Rnd(255) )
		info$ = "2) clip mode tinted with random color"
	EndIf 

	If KeyHit (4) ; 3 key
		img_comp = compimage_mask("image1.png", "image2.png","mask.png")
		info$ = "3) mask mode"
	EndIf 

	If KeyHit (5) ;4 key
		img_comp =  compimage_mask("image1.png", "image2.png","mask.png",Rnd(255),Rnd(255),Rnd(255) )
		info$ = "4) mask mode tinted with random color"
	EndIf

	If KeyHit (6) ; 5 key
		img_comp = compimage_add("image1.png", "image2.png","mask.png")
		info$ = "5) add mode"
	EndIf 

	If KeyHit (7) ;6 key
		img_comp =  compimage_add("image1.png", "image2.png","mask.png",Rnd(255),Rnd(255),Rnd(255) )
		info$ = "6) add mode tinted with random color"
	EndIf 

	If KeyHit (8) ; 7 key
		img_comp = compimage_multiply("image1.png", "image2.png","mask.png")
		info$ = "7) multiply mode"
	EndIf 

	If KeyHit (9) ;8 key
		img_comp =  compimage_multiply("image1.png", "image2.png","mask.png",Rnd(255),Rnd(255),Rnd(255) )
		info$ = "8) multiply mode tinted with random color"
	EndIf 

	If KeyHit (10) ; 9 key
		img_comp = compimage_overlay("image1.png", "image2.png","mask.png")
		info$ = "9) overlay mode"
	EndIf 

	If KeyHit (11) ;0 key
		img_comp =  compimage_overlay("image1.png", "image2.png","mask.png",Rnd(255),Rnd(255),Rnd(255) )
		info$ = "0) overlay mode tinted with random color"
	EndIf 

	DrawImage img_comp,50,30

	Text 325,15,"Press 1-0 to change blend modes"
	Text  325,35,"1) Clip Mode"
	Text  325,50,"2) Clip Mode Tinted"
	Text  325,65,"3) Mask Mode"
	Text  325,80,"4) Mask Mode Tinted"
	Text  325,95,"5) Add Mode"
	Text  325,110,"6) Add Mode Tinted"
	Text  325,125,"7) Multiply Mode"
	Text  325,140,"8) Multiply Mode Tinted"
	Text  325,155,"9) Overlay Mode"
	Text  325,170,"0) Overlay Mode Tinted"

	Text 40,350,"current mode: " + info$
	Flip
	Cls 
Wend  ; end main loop

End ;end program

Function minmax#(value#,min#,max#)
	If value > max Return max
	If value < min Return min
	Return value
End Function

Function compimage_mask(image1$, image2$, mask$=0, tint_R=255, tint_G=255, tint_B=255 )

	Local img1,img2,maskimg,r,g,b,r2,g2,b2,r3,g3,b3,m_rgb,m#

	;load the images and do error checks
	img1 = LoadImage (image1)
	If img1 = 0 Then RuntimeError "Image " + image1$ + " not found."	
	img2 = LoadImage (image2)
	If img2 = 0 Then RuntimeError "Image " + image2$ + " not found."
	If mask <> 0 Then
		maskimg = LoadImage (mask)
		If maskimg = 0 Then RuntimeError "Image " + mask$ + " not found."
	EndIf
	
	;lock the buffers for the pixel operations
	LockBuffer (ImageBuffer(img1))
	LockBuffer (ImageBuffer(img2))
	If mask <> 0 Then LockBuffer (ImageBuffer(maskimg)) ;only lock mask buffer if this exists

	;loop through the pixels
	For y = 0 To ImageHeight(img1) -1
		For x = 0 To ImageWidth(img1) -1
			;read the RGB value of image1 and split the RGB values
			rgb=ReadPixelFast(x,y,ImageBuffer(img1))
			r=((rgb Shr 16) And $ff )
			g=((rgb Shr 8) And $ff )
			b=(rgb And $ff)

			;read the RGB value of image2 and split the RGB values
			rgb2=ReadPixelFast(x,y,ImageBuffer(img2))
			r2=(rgb2 Shr 16) And $ff 
			g2=(rgb2 Shr 8) And $ff 
			b2=rgb2 And $ff		

			If mask = 0 ; no mask, so clip black values
				If r+g+b>0 ;if the pixel is not black then tint it and copy to img2
					r = minmax( r - (128 - tint_R Shr 1),0,255)
					g = minmax( g - (128 - tint_G Shr 1),0,255)
					b =minmax(  b - (128 - tint_B Shr 1),0,255)
					rgb=(b Or (g Shl 8) Or (r Shl 16) Or ($ff000000))
					WritePixelFast(x,y,rgb,ImageBuffer(img2))
				EndIf

			Else ; mask is being used,... tint image1 and fade it into image2 using the mask
				;first tint like before
				r = minmax( r - ( 128 - tint_R Shr 1),0,255)
				g = minmax( g - ( 128 - tint_G Shr 1),0,255)
				b =minmax(  b - ( 128 - tint_B Shr 1),0,255)

				;read the RGB value of the mask image
				m_rgb=ReadPixelFast(x,y,ImageBuffer(maskimg))
				m =(((m_rgb Shr 16) And $ff ) + ((m_rgb Shr 8) And $ff ) + (m_rgb And $ff) )/ 3	;get average of RGB values of matte
				m= m /256 ;convert matte value from 0-255 to 0-1

				;now blend img1 and img2 with the mask value
				r3 = minmax (		r2 + ((r - r2)*m)		,0,255)
				g3 = minmax (		g2 + ((g -g2)*m)	,0,255)
				b3 = minmax (		b2 + ((b -b2)*m)	,0,255)

				rgb=(b3 Or (g3 Shl 8) Or (r3 Shl 16) Or ($ff000000))
				WritePixelFast(x,y,rgb,ImageBuffer(img2))
			EndIf
		Next
	Next 

	UnlockBuffer (ImageBuffer(img1))
	UnlockBuffer (ImageBuffer(img2))
	If mask <> 0 Then UnlockBuffer (ImageBuffer(maskimg))

	Return img2

End Function

Function compimage_add(image1$, image2$, mask$=0, tint_R=255, tint_G=255, tint_B=255)

	Local img1,img2,maskimg,r,g,b,r2,g2,b2,r3,g3,b3,m_rgb,m#

	;load the images and do error checks
	img1 = LoadImage (image1)
	If img1 = 0 Then RuntimeError "Image " + image1$ + " not found."	
	img2 = LoadImage (image2)
	If img2 = 0 Then RuntimeError "Image " + image2$ + " not found."
	If mask <> 0 Then
		maskimg = LoadImage (mask)
		If maskimg = 0 Then RuntimeError "Image " + mask$ + " not found."
	EndIf
	
	;lock the buffers for the pixel operations
	LockBuffer (ImageBuffer(img1))
	LockBuffer (ImageBuffer(img2))
	If mask <> 0 Then LockBuffer (ImageBuffer(maskimg)) ;only lock mask buffer if this exists

	;loop through the pixels
	For y = 0 To ImageHeight(img1) -1
		For x = 0 To ImageWidth(img1) -1
			;read the RGB value of image1 and split the RGB values
			rgb=ReadPixelFast(x,y,ImageBuffer(img1))
			r=((rgb Shr 16) And $ff )
			g=((rgb Shr 8) And $ff )
			b=(rgb And $ff)

			;read the RGB value of image2 and split the RGB values
			rgb2=ReadPixelFast(x,y,ImageBuffer(img2))
			r2=(rgb2 Shr 16) And $ff 
			g2=(rgb2 Shr 8) And $ff 
			b2=rgb2 And $ff		

			If mask = 0 ; no mask, so clip black values
				If r+g+b>0 ;if the pixel is not black then tint it and copy to img2
					r = minmax( (r+r2) - (128 - tint_R Shr 1),0,255)
					g = minmax( (g+g2) - (128 - tint_G Shr 1),0,255)
					b =minmax(  (b+b2)- (128 - tint_B Shr 1),0,255)
					rgb=(b Or (g Shl 8) Or (r Shl 16) Or ($ff000000))
					WritePixelFast(x,y,rgb,ImageBuffer(img2))
				EndIf

			Else ; mask is being used,... tint image1 and fade it into image2 using the mask
				;first tint like before
				r = minmax( (r+r2 )- ( 128 - tint_R Shr 1),0,255)
				g = minmax( (g+g2) - ( 128 - tint_G Shr 1),0,255)
				b =minmax(  (b+b2) - ( 128 - tint_B Shr 1),0,255)

				;read the RGB value of the mask image
				m_rgb=ReadPixelFast(x,y,ImageBuffer(maskimg))
				m =(((m_rgb Shr 16) And $ff ) + ((m_rgb Shr 8) And $ff ) + (m_rgb And $ff) )/ 3	;get average of RGB values of matte
				m= m /256 ;convert matte value from 0-255 to 0-1

				;now blend img1 and img2 with the mask value
				r3 = minmax (		r2 + ((r - r2)*m)		,0,255)
				g3 = minmax (		g2 + ((g -g2)*m)	,0,255)
				b3 = minmax (		b2 + ((b -b2)*m)	,0,255)

				rgb=(b3 Or (g3 Shl 8) Or (r3 Shl 16) Or ($ff000000))
				WritePixelFast(x,y,rgb,ImageBuffer(img2))
			EndIf
		Next
	Next 

	UnlockBuffer (ImageBuffer(img1))
	UnlockBuffer (ImageBuffer(img2))
	If mask <> 0 Then UnlockBuffer (ImageBuffer(maskimg))

	Return img2

End Function 

Function compimage_multiply(image1$, image2$, mask$=0, tint_R=255, tint_G=255, tint_B=255)

	Local img1,img2,maskimg,r,g,b,r2,g2,b2,r3,g3,b3,m_rgb,m#

	;load the images and do error checks
	img1 = LoadImage (image1)
	If img1 = 0 Then RuntimeError "Image " + image1$ + " not found."	
	img2 = LoadImage (image2)
	If img2 = 0 Then RuntimeError "Image " + image2$ + " not found."
	If mask <> 0 Then
		maskimg = LoadImage (mask)
		If maskimg = 0 Then RuntimeError "Image " + mask$ + " not found."
	EndIf
	
	;lock the buffers for the pixel operations
	LockBuffer (ImageBuffer(img1))
	LockBuffer (ImageBuffer(img2))
	If mask <> 0 Then LockBuffer (ImageBuffer(maskimg)) ;only lock mask buffer if this exists

	;loop through the pixels
	For y = 0 To ImageHeight(img1) -1
		For x = 0 To ImageWidth(img1) -1
			;read the RGB value of image1 and split the RGB values
			rgb=ReadPixelFast(x,y,ImageBuffer(img1))
			r=((rgb Shr 16) And $ff )
			g=((rgb Shr 8) And $ff )
			b=(rgb And $ff)

			;read the RGB value of image2 and split the RGB values
			rgb2=ReadPixelFast(x,y,ImageBuffer(img2))
			r2=(rgb2 Shr 16) And $ff 
			g2=(rgb2 Shr 8) And $ff 
			b2=rgb2 And $ff		

			If mask = 0 ; no mask, so clip black values
				If r+g+b>0 ;if the pixel is not black then tint it and copy to img2
					r = minmax( (r*r2)Shr 8 - (128 - tint_R Shr 1),0,255)
					g = minmax( (g*g2)Shr 8 - (128 - tint_G Shr 1),0,255)
					b =minmax(  (b*b2)Shr 8 - (128 - tint_B Shr 1),0,255)
					rgb=(b Or (g Shl 8) Or (r Shl 16) Or ($ff000000))
					WritePixelFast(x,y,rgb,ImageBuffer(img2))
				EndIf

			Else ; mask is being used,... tint image1 and fade it into image2 using the mask
				;first tint like before
				r = minmax( (r*r2 )Shr 8- ( 128 - tint_R Shr 1),0,255)
				g = minmax( (g*g2)Shr 8 - ( 128 - tint_G Shr 1),0,255)
				b =minmax(  (b*b2)Shr 8 - ( 128 - tint_B Shr 1),0,255)

				;read the RGB value of the mask image
				m_rgb=ReadPixelFast(x,y,ImageBuffer(maskimg))
				m =(((m_rgb Shr 16) And $ff ) + ((m_rgb Shr 8) And $ff ) + (m_rgb And $ff) )/ 3	;get average of RGB values of matte
				m= m /256 ;convert matte value from 0-255 to 0-1

				;now blend img1 and img2 with the mask value
				r3 = minmax (		r2 + ((r - r2)*m)		,0,255)
				g3 = minmax (		g2 + ((g -g2)*m)	,0,255)
				b3 = minmax (		b2 + ((b -b2)*m)	,0,255)

				rgb=(b3 Or (g3 Shl 8) Or (r3 Shl 16) Or ($ff000000))
				WritePixelFast(x,y,rgb,ImageBuffer(img2))
			EndIf
		Next
	Next 

	UnlockBuffer (ImageBuffer(img1))
	UnlockBuffer (ImageBuffer(img2))
	If mask <> 0 Then UnlockBuffer (ImageBuffer(maskimg))

	Return img2

End Function 

Function compimage_overlay(image1$, image2$, mask$=0, tint_R=255, tint_G=255, tint_B=255 )

	Local img1,img2,r,g,b,r2,g2,b2,r3,g3,b3,tmp,m#

	img1 = LoadImage (image1)
	img2 = LoadImage (image2)
	If mask <> 0 Then maskimg = LoadImage (mask)

	LockBuffer (ImageBuffer(img1))
	LockBuffer (ImageBuffer(img2))
	If mask <> 0 Then LockBuffer (ImageBuffer(maskimg)) ;only lock mask buffer if this exists
	
	For y = 0 To ImageHeight(img1) -1
		For x = 0 To ImageWidth(img1) -1
			rgb=ReadPixelFast(x,y,ImageBuffer(img1))

			r = minmax( ((rgb Shr 16) And $ff) - (255 - tint_R),0,255)
			g = minmax( ((rgb Shr 8) And $ff ) - (255 - tint_G),0,255)
			b =minmax(  (rgb And $ff) - (255 - tint_B),0,255)
			
			rgb2=ReadPixelFast(x,y,ImageBuffer(img2))
			r2=(rgb2 Shr 16) And $ff 
			g2=(rgb2 Shr 8) And $ff 
			b2=rgb2 And $ff

			If mask = 0 ; no mask, so clip black values
				If r+g+b>0 ;if the pixel is not black then tint it and copy to img2
					r3 =minmax(255 - ((255-r) * (255 - r2) Shr 7),0,255)
					g3 =minmax(255 - ((255-g) * (255 - g2) Shr 7),0,255)
					b3 =minmax(255 - ((255-b) * (255 - b2) Shr 7),0,255)
					rgb=(b3 Or (g3 Shl 8) Or (r3 Shl 16) Or ($ff000000))
					WritePixelFast(x,y,rgb,ImageBuffer(img2))
				EndIf

			Else ; mask is being used,... tint image1 and fade it into image2 using the mask

				r3 =minmax(255 - ((255-r) * (255 - r2) Shr 7),0,255)
				g3 =minmax(255 - ((255-g) * (255 - g2) Shr 7),0,255)
				b3 =minmax(255 - ((255-b) * (255 - b2) Shr 7),0,255)

				;read the RGB value of the mask image
				m_rgb=ReadPixelFast(x,y,ImageBuffer(maskimg))
				m =(((m_rgb Shr 16) And $ff ) + ((m_rgb Shr 8) And $ff ) + (m_rgb And $ff) )/ 3	;get average of RGB values of matte
				m= m /256 ;convert matte value from 0-255 to 0-1

				r = minmax (	r2 + ((r3 - r2)*m)		,0,255)
				g = minmax (	g2 + ((g3 -g2)*m)		,0,255)
				b = minmax (	b2 + ((b3 -b2)*m)		,0,255)

				rgb=(b Or (g Shl 8) Or (r Shl 16) Or ($ff000000))
				WritePixelFast(x,y,rgb,ImageBuffer(img2))
			EndIf

		Next
	Next
		
	UnlockBuffer (ImageBuffer(img1))
	UnlockBuffer (ImageBuffer(img2))
	If mask <> 0 Then UnlockBuffer (ImageBuffer(maskimg))
	
	Return img2

End Function
