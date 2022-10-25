; ID: 1330
; Author: n8r2k
; Date: 2005-03-16 13:24:35
; Title: Background color chooser
; Description: Changes the background color with the click of an arrow. Great for user customized games.

;Set the Graphics
Graphics 300,200,16,2

;Seed the Random Generator
SeedRnd MilliSecs()

;Hide the mouse
HidePointer()

;Load all the picture files
RedUp = LoadAnimImage("up.bmp",22,15,0,2)
GreenUp = LoadAnimImage("up.bmp",22,15,0,2)
BlueUp = LoadAnimImage("up.bmp",22,15,0,2)
RedDown = LoadAnimImage("Down.bmp",22,15,0,2)
GreenDown = LoadAnimImage("Down.bmp",22,15,0,2)
BlueDown = LoadAnimImage("Down.bmp",22,15,0,2)
red = LoadImage("red.bmp")
green = LoadImage("green.bmp")
blue = LoadImage("blue.bmp")
;Create the mouse img
mouse = CreateImage(10,10)

;Set the Buffer to the mouse
SetBuffer ImageBuffer(mouse)

;Set the color
Color 100,100,100

;Draw a rectangle
Rect 0,0,10,10,1

;Set the color
Color 255,255,255

;Draw a rectangle
Rect 0,0,10,10,0

;Set the Backbuffer
SetBuffer BackBuffer()

;Set random values for the rgb
r = Rand(0,255)
g = Rand(0,255)
b = Rand(0,255)

;Start the loop
While Not KeyHit(1)

;If user hits 'r'
If KeyHit(19) Then 
	;Reset the colors to 0
	r = 0
	g = 0
	b = 0
	
EndIf 

;Make sure the rgb values arent out of range
If r < 0 Then r = 0
If r > 255 Then r = 255
If g < 0 Then g = 0
If g > 255 Then g = 255
If b < 0 Then b = 0
If b > 255 Then b = 255

;Clear the screen
Cls 

;Color the screen
ClsColor r,g,b

;Set the color
Color 255 - r,255 - g,255 - b

;Locate the 0,0 position
Locate 0,0

;Print the rgb values
Print r
Print g
Print b

;If the mouse is hit
If MouseDown(1) 
	;If the user clicked an arrow, increment the correct color the correct amount
	If ImagesCollide(mouse,MouseX(),MouseY(),0,RedUp,25,0,ruf)
		ruf = 1
		r = r + 1
		rdf = 0
		guf = 0
		gdf = 0
		buf = 0 
		bdf = 0
	ElseIf ImagesCollide(mouse,MouseX(),MouseY(),0,RedDown,25,30,rdf)
		rdf = 1
		r = r - 1
		ruf = 0
		guf = 0
		gdf = 0
		buf = 0 
		bdf = 0
	ElseIf ImagesCollide(mouse,MouseX(),MouseY(),0,GreenUp,48,0,guf)
		guf = 1
		g = g + 1
		ruf = 0
		rdf = 0
		gdf = 0
		buf = 0 
		bdf = 0
	ElseIf ImagesCollide(mouse,MouseX(),MouseY(),0,GreenDown,48,30,gdf)
		gdf = 1
		g = g - 1
		ruf = 0
		rdf = 0
		guf = 0
		buf = 0 
		bdf = 0
	ElseIf ImagesCollide(mouse,MouseX(),MouseY(),0,BlueUp,71,0,buf)
		buf = 1
		b = b + 1
		ruf = 0
		rdf = 0
		guf = 0
		gdf = 0
		bdf = 0
	ElseIf ImagesCollide(mouse,MouseX(),MouseY(),0,BlueDown,71,30,bdf)
		bdf = 1
		b = b - 1
		ruf = 0
		rdf = 0
		guf = 0
		gdf = 0
		buf = 0 
	EndIf
Else ;Reset the frames
	ruf = 0
	rdf = 0
	guf = 0
	gdf = 0
	buf = 0 
	bdf = 0
EndIf	

;Draw the images
DrawImage RedUp,25,0,ruf
DrawImage red,25,15
DrawImage RedDown,25,30,rdf
DrawImage GreenUp,48,0,guf
DrawImage Green,48,15
DrawImage GreenDown,48,30,gdf
DrawImage BlueUp,71,0,buf
DrawImage Blue,71,15
DrawImage BlueDown,71,30,bdf
DrawImage Mouse,MouseX(),MouseY()

;Flip the buffers
Flip

;loop to beginning
Wend
