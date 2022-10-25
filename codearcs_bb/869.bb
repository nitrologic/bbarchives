; ID: 869
; Author: aCiD2
; Date: 2003-12-31 05:46:55
; Title: Shadow from an image
; Description: Creates a shadow from an image

Function GenerateShadow(img, r = 255, g = 0, b = 255)
	setbuffer imagebuffer(img)
	lockbuffer
	for x = 0 to imagewidth(img) - 1
		for y = 0 to imageheight(img) - 1
			col = readpixelfast (x, y)
			color 0, 0, col
			if not (colorred() = r and colorgreen() = g and colorblue() = 255)
				color colorred(), colorred(), colorred()
				plot x, y
			endif
		next
	next
	unlockbuffer
	setbuffer backbuffer()
	
	return img
end function
