; ID: 1304
; Author: n8r2k
; Date: 2005-02-28 17:32:44
; Title: Blitz Scrollbar Example- Updated
; Description: demonstrates how to make scrollbars in blitz (commented yay! and updated double yay!)

;Blitz Scrollbar Demo
;By n8r2k
;Get the pictures for this demo at http://n8r2k.deviousbytes.com/

;Set the Graphics
Graphics 320,200,16,2

;Set the Title
AppTitle "Blitz Scroll Bar Example","C Ya"

;Load the pointer image
pointer = LoadImage("cursor.bmp")

;Load the up button image
scrollup = LoadImage("scroll up.bmp")

;Load the down button image
scrolldown = LoadImage("scroll down.bmp")

;Load the scrollbar track image
scrollertrack = LoadImage("scroller.bmp")

;Load the scrolling bar image
scrollbar = LoadImage("scroll bar.bmp")

;Load the image to scroll
scrollpic = LoadImage("scroll pic.bmp")

;Create the minimized scroll area
scrollimage = CreateImage(300,200)

;Create the text image
scrolltext = CreateImage(300,50)

;Create the text and graphics imag
textpic = CreateImage(300,450)

;Put the scrolled image into position
scrolly = 0

;Scrollbar Y-Pos equals 20 + (.775 times the opposite of the Scrolling image value) 
;(will need To change this For larger Or smaller images
scrollery = 20 + (.62 * -(scrolly))

;Set the scrolling text ImageBuffer
SetBuffer ImageBuffer(scrolltext)

;Set the color to red
Color 255,0,0

;Write stuff on the picture
Text 0,0,"Hi"
Text 0,11,"This is pretty neat"
Text 0,22,"And its fully commented"
Text 0,33,"n8r2k created it"

;Set the text + graphics ImageBuffer
SetBuffer ImageBuffer(textpic)

;Put the text picture at 0,0
DrawImage scrolltext,0,0

;Put the graphic at 0,50
DrawImage scrollpic,0,50

;Set the BackBuffer
SetBuffer BackBuffer()

;Start the Endless Loop
Repeat

;Clear the Screen
Cls

;Draw the images
DrawImage scrollimage,0,0
DrawImage scrollup,300,0
DrawImage scrolldown,300,180
DrawImage scrollertrack,300,20
DrawImage scrollbar,300,scrollery

;If the user clicks the up arrow, scroll up
If ImagesCollide(pointer,MouseX(),MouseY(),0,scrollup,300,0,0)
	If MouseDown(1)
		scrolly = scrolly + 5 
	EndIf 
EndIf 

;If user presses the up key, scroll up
If KeyDown(200)
	scrolly = scrolly + 5
EndIf

;If the user clicks the down arrow, scroll down
If ImagesCollide(pointer,MouseX(),MouseY(),0,scrolldown,300,180,0)
	If MouseDown(1)
		scrolly = scrolly - 5
	EndIf 
EndIf 

;If user presses the down key, scroll down
If KeyDown(208)
	scrolly = scrolly - 5
EndIf 

;If the user presses Page Up, move up a bunch
If KeyHit(201)
	scrolly = scrolly + 50
EndIf 

;If the user presses Page Down, move down a bunch
If KeyHit(209)
	scrolly = scrolly - 50
EndIf 

;If the user presses Home, go to the top
If KeyHit(199)
	scrolly = 0
EndIf 

;If the user presses End, go to the bottom
If KeyHit(207)
	scrolly = -250
EndIf

;Set the mousez2 var to 10 times the current MouseZSpeed
;(Tests to see if user scrolls with middle mouse wheel)
mousez2 = (MouseZSpeed() * 10) 

;Scroll the scrolling image according to the mousez2 var
scrolly = scrolly + mousez2

;Scrollbar Y-Pos equals 20 + (.775 times the opposite of the Scrolling image value) 
;(will need To change this For larger Or smaller images
scrollery = 20 + (.62 * -(scrolly))

;If scroller goes to high, keep it the same
If scrolly > 0
	scrolly = 0
EndIf 

;If scroller goes to low, keep it the same
If scrolly < -250
	scrolly = -250
EndIf

;If scroll bar goes to high, keep it the same
If scrollery < 20
	scrollery = 20
EndIf 

;If scroll bar goes to low, keep it the same
If scrollery > 175
	scrollery = 175
EndIf

;Set the scrolling image ImageBuffer
SetBuffer ImageBuffer(scrollimage)

;Clear the screen
Cls

;Draw the scrolling image @ 0,ScrollY
DrawImage textpic,0,scrolly

;Reset the current buffer to the BackBuffer()
SetBuffer BackBuffer()

;Flip the buffers
Flip

;Go to the start of loop
Forever
