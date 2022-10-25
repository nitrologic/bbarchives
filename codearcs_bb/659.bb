; ID: 659
; Author: Ross C
; Date: 2003-04-25 05:58:16
; Title: fade in/out image
; Description: Will fade in/out any image to any backround below, providing the backround below in static

SetBuffer BackBuffer()

x=100:y=100
fade=2; set fade to 2 so that nothing happens
alpha#=0; alpha to zero and make a float number
backround=LoadImage("final.jpg"); replace these images 
image=LoadImage("grass.jpg"); with two of your own
;these are the arrays to store image color information
Dim ir#(ImageWidth(image),ImageHeight(image));use arrays to store color info
Dim ig#(ImageWidth(image),ImageHeight(image));as it is much faster than constantly reading it
Dim ib#(ImageWidth(image),ImageHeight(image));from the screen

;these are the arrays to store backround color information
Dim r#(ImageWidth(image),ImageHeight(image))
Dim g#(ImageWidth(image),ImageHeight(image))
Dim b#(ImageWidth(image),ImageHeight(image))

tempr#=0;temp red, green and blue value
tempg#=0
tempb#=0

SetBuffer ImageBuffer(image); set the buffer to the image being faded in or out
For loop=1 To ImageWidth(image); set loop so that it is the same width as the image
	For loop1=1 To ImageHeight(image); set loop so that it is the same height as the image
		rgb=ReadPixel(loop,loop1); read the pixel color from the image
		ir(loop,loop1)=(rgb And $FF0000)/$10000; seperate the red
		ig(loop,loop1)=(rgb And $FF00)/$100;green
		ib(loop,loop1)=rgb And $FF;and blue color information
	Next
Next
SetBuffer BackBuffer(); set the buffer to the backbuffer

While Not KeyHit(1)
Cls

If KeyDown(200) Then y=y-1; to move image up
If KeyDown(208) Then y=y+1; and down
If KeyHit(3) Then fade=0:alpha=0; set fade to zero and alpha to zero then the image wil fade in
If KeyHit(4) Then fade=1:alpha=1;set fade to 1 and alpha to 1 and the image will fade out 
TileImage backround,0,0; the backround MUST be drawn before the color info is taken or it will
					   ; appear black.
If KeyHit(2) Then; this MUST be called prior to fading an image. grabs screen color information
				Gosub getbackroundinfo; from under the image
End If



If fade<>2 Then Gosub updatefade; if fade has been set then goto the fade in/out routine

Text 0,0,"PRESS 1 TO GRAB COLOR INFO. 2 TO FADE IN IMAGE. 3 TO FADE OUT IMAGE

Flip
Wend

.updatefade
;when alpha=1 then image is fully visible
If fade=0 Then alpha=alpha+0.1:If alpha>1 Then alpha=1:DrawImage image,x,y:Return
If fade=1 Then alpha=alpha-0.1:If alpha<-0 Then alpha=0:Return
LockBuffer BackBuffer()
For loop=1 To ImageWidth(image)-1

	For loop1=1 To ImageHeight(image)-1
		;fade the red part of the color into the backround red color
		tempr=Int(r(loop,loop1) + (  ( ir(loop,loop1)-r(loop,loop1) )*alpha  ))
		;fade the blue part of the color into the backround blue color
		tempg=Int(g(loop,loop1) + (  ( ig(loop,loop1)-g(loop,loop1) )*alpha  ))
		;fade the blue part of the color into the backround blue color
		tempb=Int(b(loop,loop1) + (  ( ib(loop,loop1)-b(loop,loop1) )*alpha  ))
		
		rgb=tempr*65536+tempg*256+tempb; combine the red,green and blue together
		WritePixelFast x+loop,y+loop1,rgb; write the color info to the screen
	Next
Next
UnlockBuffer BackBuffer()
Return

.getbackroundinfo; get the color info from below the image
SetBuffer FrontBuffer()
For loop=1 To ImageWidth(image)
	For loop1=1 To ImageHeight(image)
		rgb=ReadPixel(x+loop,y+loop1)
		r(loop,loop1)=(rgb And $FF0000)/$10000;separate out the red
		g(loop,loop1)=(rgb And $FF00)/$100;green
		b(loop,loop1)=rgb And $FF;and blue poarts of the color
	Next
Next
SetBuffer BackBuffer(); set buffer back to the back buffer
Return
