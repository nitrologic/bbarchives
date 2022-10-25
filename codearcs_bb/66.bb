; ID: 66
; Author: Richard Betson
; Date: 2001-10-02 05:16:01
; Title: Fast Copy/Flip Image
; Description: Fast way to Flip/Copy image using CopyRect. Faster then Scale Image!

;***********************************************************
;* The Image Flip Routine
;* Written By Richard Betson 9/1/2001
;* Copyright 2001, Richard Betson
;***********************************************************


;This shows that a copy/flip is faster using CopyRect then 
;ScaleImage.



Graphics 800,600,16

image=CreateImage(400,300)

SetBuffer FrontBuffer()

For i=0 To 20
	Color Rnd(255),Rnd(255),Rnd(255)
	
	Rect 400+Rnd(400),0,Rnd(400),Rnd(300)
Next

CopyRect 400,0,400,300,0,0,FrontBuffer(),ImageBuffer(image)



Color 255,255,255
Text 10,10,"Flip Image Using CopyRect"

Delay 3000

tm=MilliSecs()

For i=0 To 400
CopyRect 400+i,0,1,300,400-i,0,FrontBuffer()
Next


time1=(MilliSecs()-tm)


Delay 4000

Cls

Text 10,10,"Flip Image Using ScaleImage"

Delay 3000

DrawImage image,400,0

tm=MilliSecs()

ScaleImage image,-1,1
DrawImage image,400,0


time2=(MilliSecs()-tm)


Color 255,0,0
Text 10,450,"Total Time in Milisecs to flip 400 x 300 Image"
Color 255,128,32
Text 10,475,"CopyRect Time: "+Str$(time1)
Text 10,500,"ScaleImage Time: "+Str$(time2)

Text 10,550,"Hit any key to exit."

WaitKey ()
