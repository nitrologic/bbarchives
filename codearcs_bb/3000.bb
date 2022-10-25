; ID: 3000
; Author: Zethrax
; Date: 2012-11-05 22:41:55
; Title: ResizeImageToFitBox() function
; Description: Resizes an image to fit within a specified width and height with the aspect ratio preserved.

Function ResizeImageToFitBox( image, box_width#, box_height#, force_resize = False )
	; This function will resize an image to fit into the specified 'box_width' and 'box_height'.
	; The aspect ratio of the image is preserved.

	; If 'force_resize' is set to false (the default) then the image will only be resized if it
	; is bigger than the box size. If it is set to true then the image will be resized even if
	; isn't larger than the box size.
	
	; Nothing is returned by this function. The image handle still points to the image, although
	; the image's buffer and dimensions may have changed.

	; Note that the resize operation resets the current drawing buffer back to the backbuffer if it was
	; set to the image's buffer, so you will need to re-open the image's image
	; buffer if you wish to keep drawing to it.

	width = ImageWidth( image )
	height = ImageHeight( image )

	If ( width > box_width# ) Or ( height > box_height# ) Or force_resize
		width_margin = width - box_width#
		height_margin = height - box_height#
	
		If width_margin > height_margin
			multiplier# = box_width# / width
		Else
			multiplier# = box_height# / height
		EndIf
		ResizeImage image, width * multiplier#, height * multiplier#
	EndIf 
End Function


; *** DEMO ***


Graphics 800, 600, 0, 2
image = CreateImage( 300, 200 )
SetBuffer ImageBuffer( image )
ClsColor 0, 0, 200
Cls
Text 150, 100, "Just testing.", True, True
SetBuffer BackBuffer()

SaveImage( image, "original_image.bmp" )
ResizeImageToFitBox( image, 256, 128 )
SaveImage( image, "resized_image.bmp" )

Print "OK: The image was resized and saved to the folder this code file is in."

WaitKey
End
