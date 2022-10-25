; ID: 650
; Author: elias_t
; Date: 2003-04-13 14:30:58
; Title: Create seamless texture function
; Description: It works pretty good [updated]

;=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
;
; Seamless texture generation Function
;
; by elias_t
;
; Created after a tutorial by Paul Bourke. 
;
; updated to make the linear methods work better.
;
;=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


;example
Graphics 640,480,32,2

img=LoadImage("your_image.bmp");your image here


;0=Radial   method 
;1=Linear 1 method 
;2=Linear 2 method 


seamless=make_seamless(img,0)


SaveImage (seamless,"seamless.bmp")

TileImage seamless,0,0

;DrawImage seamless,0,0

Flip

WaitKey()

End









;*************************************************************************

;needed arrays
Dim image(0,0,0) , diagonal(0,0,0) , tile(0,0,0) , mask(0,0)



;img=image handle
;masktype= [0=Radial , 1=Linear] method

Function make_seamless(img,masktype)


Local a1#,a2#,d#

;protect masktype
masktype=Abs(masktype)
If masktype>2 Then masktype=2

;find largest side of the image
x=ImageWidth(img)
y=ImageHeight(img)

;and resize the image to become square
If x<>y
If x>y Then N=x
If y>x Then N=y
ResizeImage img,N,N
EndIf

If x=y Then N=x

Dim image(N,N,2)
Dim diagonal(N,N,2)
Dim tile(N,N,2)
Dim mask(N,N)



LockBuffer (ImageBuffer(img))

	For j=0 To N-1
		
		For i=0 To N-1
		
		rgb=ReadPixelFast(j,i,ImageBuffer(img))

		image(i,j,0) = (rgb Shr 16 And $ff)
		image(i,j,1) = (rgb Shr 8 And $ff)
		image(i,j,2) = (rgb And $ff)

        diagonal ( (i+N/2) Mod N , (j+N/2) Mod N ,0) = image(i,j,0)
		diagonal ( (i+N/2) Mod N , (j+N/2) Mod N ,1) = image(i,j,1)
		diagonal ( (i+N/2) Mod N , (j+N/2) Mod N ,2) = image(i,j,2)

		Next

	Next


UnlockBuffer (ImageBuffer(img))


;try to make your own masktypes here

;Create the mask
   For i=0 To N/2-1

      For j=0 To N/2-1

		Select masktype

		Case 0;RADIAL
			d = Sqr((i-N/2)*(i-N/2) +  (j-N/2)*(j-N/2)) / (N/2)
           
		Case 1;LINEAR 1
			If (N/2-i)< (N/2-j)
			d=Sqr((j-N/2)*(j-N/2))/(N/2)
			EndIf
			
			If (N/2-i)>= (N/2-j)
			d=Sqr((i-N/2)*(i-N/2) ) /(N/2)
			EndIf


		Case 2;LINEAR 2
			If (N/2-i)<(N/2-j)
			d=Sqr((j-N)*(j-N) +  (i-N)*(i-N)) / (1.13*N)
			EndIf
			
			If (N/2-i)>=(N/2-j)
			d=Sqr((i-N)*(i-N) +  (j-N)*(j-N)) / (1.13*N)
			EndIf

			
		End Select
		

         ;Scale d To range from 1 To 255

         d = 255 - (255 * d)
         If (d < 1) Then d = 1
         If (d > 255) Then d = 255


         ;Form the mask in Each quadrant

         mask (i     , j    ) = d
         mask (i     , N-1-j) = d
         mask (N-1-i , j    ) = d
         mask (N-1-i , N-1-j) = d


		Next
		
	Next



;Create the tile
	For j=0 To N-1
	
		For i=0 To N-1

		a1 = mask(i,j)
		a2 = mask( (i+N/2) Mod N , (j+N/2) Mod N )
		tile(i,j,0) = a1*image(i,j,0)/(a1+a2) + a2*diagonal(i,j,0)/(a1+a2)
		tile(i,j,1) = a1*image(i,j,1)/(a1+a2) + a2*diagonal(i,j,1)/(a1+a2)
		tile(i,j,2) = a1*image(i,j,2)/(a1+a2) + a2*diagonal(i,j,2)/(a1+a2)
		
		Next
		
	Next


;create the new tileable image

img2=CreateImage(N,N)

LockBuffer (ImageBuffer(img2))

   For j=0 To N-1

      For i=0 To N-1
		
		rgb=(tile(i,j,0) Shl 16) + (tile(i,j,1) Shl 8) + tile(i,j,2)
		WritePixelFast j,i,rgb,ImageBuffer(img2)

		Next
		
	Next

UnlockBuffer (ImageBuffer(img2))

Dim image(0,0,0) , diagonal(0,0,0) , tile(0,0,0) , mask(0,0)

;if it wasn't a square image, resize it back to the original scale
If x<>y Then ResizeImage img2,x,y

Return img2

End Function
