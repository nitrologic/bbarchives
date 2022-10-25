; ID: 1315
; Author: n8r2k
; Date: 2005-03-07 20:11:40
; Title: Dragable Image
; Description: Click and Drag An Image!

Graphics 800,600,16,2 
AutoMidHandle True
HidePointer()

Global mouse = CreateImage(10,10)
Global img = CreateImage(100,20)
Global imgx = 100
Global imgy = 100
Global distx = 0
Global disty = 0

SetBuffer ImageBuffer(mouse)
Color 100,100,100
Rect 0,0,10,10,1
SetBuffer ImageBuffer(img)
Color 100,0,0
Rect 0,0,100,20,1
SetBuffer BackBuffer()

While Not KeyHit(1)
Cls 
DrawImage img,imgx,imgy
DrawImage mouse,MouseX(),MouseY()
If ImagesCollide(mouse,MouseX(),MouseY(),0,img,imgx,imgy,0)
	distx = imgx - MouseX()
	disty = imgy - MouseY()
	DragImage()
EndIf
Flip
Wend 

Function DragImage()
While MouseDown(1)
Cls 
DrawImage img,imgx,imgy
DrawImage mouse,MouseX(),MouseY()

imgx = MouseX() + distx
imgy = MouseY() + disty
Flip 
Wend
End Function
