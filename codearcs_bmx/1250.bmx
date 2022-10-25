; ID: 1250
; Author: dynaman
; Date: 2004-12-30 20:02:28
; Title: Setimagemask
; Description: Set the mask color of an image after it has been loaded

Function setimagemask(p_image:Timage, p_red,p_green,p_blue)
	Local l_maskrgb
	Local l_maskargb
	Local l_pixelrgb
	Local l_pixelraw
	Local l_x,l_y
	Local l_pix1:Tpixmap
	
	l_maskrgb = p_red * 65536 + p_green * 256 + p_blue
	l_pix1 = LockImage(p_image)
	For l_x = 0 To ImageWidth(p_image)
		For l_y = 0 To ImageHeight(p_image)
			l_pixelraw = ReadPixel(l_pix1,l_x,l_y)
			l_pixelrgb = l_pixelraw & 16777215
			If l_pixelrgb = l_maskrgb Then
				WritePixel(l_pix1,l_x,l_y,l_maskrgb)
			End if
		Next 
	next

	UnlockImage(p_image)
	Release l_pix1
End function
