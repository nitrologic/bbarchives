; ID: 2350
; Author: Ryudin
; Date: 2008-10-30 22:31:21
; Title: ReplaceImage
; Description: Allows you to replace certain colors in an image with other colors.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; ReplaceImage() function by Kai Rosecrans ;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;To use this function, simply type in the following parameters:
;	r - the red value to replace in the image
;	g - the green value to replace in the image
;	b - the blue value to replace in the image
;	r_rep - the red value to replace r with in the image
;	g_rep - the green value to replace g with in the image
;	b_rep - the blue value to replace b with in the image
;	image - the image to do all this stuff to

;It could be very useful for the user to be able to create customized characters while you save space
;and time; you only need create one image with unique colors for each body region. You could also use it
;in a game where there are teams and each user can choose his/her own color for his units.

Function ReplaceImage(r,g,b,r_rep,g_rep,b_rep,image)
	Local ret_r = ColorRed()
	Local ret_g = ColorGreen()
	Local ret_b = ColorBlue()
	Local buffer = GraphicsBuffer()
	
	SetBuffer ImageBuffer(image)
	DrawImage image,0,0
	
	For x = 0 To ImageWidth(image)
		For y = 0 To ImageHeight(image)
			GetColor x,y
			
			If ColorRed() = r And ColorGreen() = g And ColorBlue() = b Then
				Color r_rep,g_rep,b_rep
				
				Plot x,y
			EndIf
		Next
	Next
	
	Color ret_r,ret_g,ret_b
	SetBuffer buffer
	
	Return image
End Function
