; ID: 314
; Author: Curtastic
; Date: 2002-07-01 18:12:56
; Title: Small Fast Easy Flood Fill
; Description: fills over the color of the starting spot

;The function fills over the color of the pixel at the position passed.
;It fills with the current drawing color.
;You pass a buffer (screen or image), and its width and height.
;In this example, just click where you want to fill.


Graphics 640,480,0,2
AppTitle "Click where you want to fill."
SeedRnd MilliSecs()

;Make some background
Color 255,0,0
Rect 0,0,640,480
Color 0,0,0
For ovals=1 To 25
	Color Rnd(255),Rnd(255),Rnd(255)
	Oval Rnd(GraphicsWidth()),Rnd(GraphicsHeight()),Rand(80,180),Rand(80,180)
Next

;Main loop
Repeat
	If MouseHit(1) Then
		Color Rnd(255),Rnd(255),Rnd(255)
		fillfast(MouseX(),MouseY(),FrontBuffer(),GraphicsWidth(),GraphicsHeight())
		FlushMouse
	EndIf
Until KeyHit(1)
End




;This type and array are needed for the function.
Type Fillpixel
	Field x,y
End Type
Dim fillmap(0,0)


;The flood fill
;The function fills over the color of the pixel at the position passed.
;It fills with the current drawing color.
;You pass a buffer (screen or image), and its width and height.
Function FillFast(startx,starty,buffer,buffersizex,buffersizey)
	Local i.fillpixel,newi.fillpixel
	Local newx,newy,dir
	
	;Make sure the starting position is in the boundaries of the buffer being used.
	If startx<0 Or starty<0 Or startx>=buffersizex Or starty>=buffersizey Then RuntimeError "Fill starting point out of bounds."
	
	LockBuffer buffer
	
	;Convert the drawing color to a single RGB.
	fillcolor=(ColorRed() Shl 16)  Or  (ColorGreen() Shl 8)  Or  ColorBlue()

	;The color at the starting position is the color that will be filed over.
	fillover=ReadPixelFast(startx,starty)
	
	;Make the first fillpixel at the starting position.
	newi=New fillpixel
	newi\x=startx
	newi\y=starty
	WritePixelFast startx,starty,fillcolor
	
	;Convert the fillcolor to the readpixel-version of itself, so it works on all graphics depths.
	fillcolor=ReadPixelFast(startx,starty)
	
	;If the starting pixel is already the color we want it, then nothing needs to be done.
	If fillover=fillcolor Then UnlockBuffer buffer:Return
	
	;Reset the map.
	Dim fillmap(buffersizex,buffersizey)
	fillmap(startx,starty)=1
	
	For i=Each fillpixel
		;Try to make new pixels in all 4 directions.
		For dir=0 To 3
		
			;New pixel position based on the direction. 0=right 1=down 2=left 3=up
			newx=i\x+(dir=0)-(dir=2)
			newy=i\y+(dir=1)-(dir=3)
			
			;Make sure the new position is in the boundaries of the buffer.
			If newx>=0 And newy>=0 And newx<buffersizex And newy<buffersizey Then
				;Make sure this position hasn't already been checked.
				If fillmap(newx,newy)=0 Then
					;Flag this pixel as "already checked."
					fillmap(newx,newy)=1
					
					;Make sure this pixel is the color being filled over.
					If ReadPixelFast(newx,newy)=fillover Then
						;Fill and make a new pixel.
						WritePixelFast newx,newy,fillcolor
						newi=New fillpixel
						newi\x=newx
						newi\y=newy
					EndIf
					
				EndIf
			EndIf
			
		Next
		Delete i
	Next
	
	UnlockBuffer buffer
End Function
