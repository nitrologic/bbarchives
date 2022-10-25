; ID: 551
; Author: Rob Farley
; Date: 2003-01-14 13:52:51
; Title: Read and Write pixel functions
; Description: Takes the mystery out of the ARGB values

;ARGB Functions by Rob Farley 2003
;rob@mentalillusion.co.uk
;http://www.mentalillusion.co.uk

; RGB Functions
Global GotR=0
Global GotG=0
Global GotB=0


Function GetRGB(image_name,x,y)
; Gets the RGB components from an image.
; The imagebuffer needs to be locked as it does a read pixel fast.
; The components are put into the global varibles gotr, gotg and gotb
	argb=ReadPixelFast(x,y,ImageBuffer(image_name))
	gotr=(ARGB Shr 16) And $ff 
	gotg=(ARGB Shr 8) And $ff 
	gotb=ARGB And $ff
End Function

Function WriteRGB(image_name,x,y,red,green,blue)
; Writes a pixel to an image.
; The imagebuffer needs to be locked as it does a write pixel fast.
argb=(blue Or (green Shl 8) Or (red Shl 16) Or ($ff000000))
WritePixelFast x,y,argb,ImageBuffer(image_name)
End Function
