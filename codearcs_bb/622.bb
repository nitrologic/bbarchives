; ID: 622
; Author: Beaker
; Date: 2003-03-15 16:00:22
; Title: Quad mapping function
; Description: Map any image to any quad shape

Const top = 0, bottom = 1

AppTitle "Quad function demo" 
Graphics 640,480

Dim xp#(1, bottom) , yp#(1,bottom)
Dim xdelta#(1), ydelta#(1)

image = LoadImage ("cat.png")
Global imagew = ImageWidth(image), imageh = ImageHeight(image)
Dim imageRGB(ImageW-1,ImageH-1)
Dim position(ImageW-1,ImageH-1)

LockBuffer (ImageBuffer(image))
For x = 0 To imagew-1
	For y = 0 To imageh-1
		imageRGB(x,y) = ReadPixelFast(x,y, ImageBuffer(image)) And $FFFFFF
		position(x,y) = 0
	Next
Next
UnlockBuffer(ImageBuffer(image))

		
Dim xp#(ImageW, bottom)
Dim yp#(imagew, bottom)
Dim xdelta#(imagew)	
Dim ydelta#(imagew)
	

SetBuffer BackBuffer()

angle = 0
MoveMouse width/2, height/2
Repeat
	angle = (angle + 3) Mod 360 
	cosang = Cos(angle)*100
	sinang = Sin(angle)*100
	quad (image, 130+sinang / 3,130+cosang / 3, 400+sinang / 2,90+cosang / 2, 460,260, MouseX(), MouseY()) 
	Color 255,255,255
	Text 5,460, "Quad Mapping by Beaker 2001"

	Flip:Cls
Until KeyHit(1)
End 



Function Quad (image, x0#,y0#, x1#,y1#, x2#,y2#, x3#,y3#)
	xpdtop# = (x1 - x0) / (imagew - 1)
	ypdtop# = (y1 - y0) / (imagew - 1)
	
	xpdbott# = (x2 - x3) / (imagew - 1)
	ypdbott# = (y2 - y3) / (imagew - 1)
	
 	For x = 0 To ImageW
		xp(x,top) = x0 + (x * xpdtop)
		yp(x,top) = y0 + (x * ypdtop)

		xp(x,bottom) = x3 + (x * xpdbott)
		yp(x,bottom) = y3 + (x * ypdbott)
	Next

	For x = 0 To imagew
		xdelta(x) = ((xp(x,bottom) - xp(x,top)) / (imageh - 1))
		ydelta(x) = ((yp(x,bottom) - yp(x,top)) / (imageh - 1))
	Next

		
	For x = 0 To imagew - 1
		For y = 0 To imageh - 1
			If imageRGB(x,y) > 0
				xadderTL = (xdelta(x) * y)

				position(x,y) = (xp(x,top) + (y * xdelta(x)) Shr 16) Or yp(x,top) + (y * ydelta(x))
				
				Color imageRGB(x,y) Shr 16 And $ff, imageRGB(x,y) Shr 8 And $ff, imageRGB(x,y) And $ff
				Oval xp(x,top) + (y * xdelta(x)), yp(x,top) + (y * ydelta(x)), 6,6
			EndIf
		Next
	Next
End Function
