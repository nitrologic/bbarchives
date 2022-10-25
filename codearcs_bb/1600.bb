; ID: 1600
; Author: Valorden
; Date: 2006-01-25 20:22:54
; Title: Image Zoom
; Description: Easy to adjust

Graphics 1024,768,32,1

Global pic=LoadImage("mona.JPG")
Const zoom=2

DrawImage pic,0,0 

Dim pix(GraphicsWidth(),GraphicsHeight())

For y=0 To GraphicsHeight()
For x=0 To GraphicsWidth()
pix(x,y)=ReadPixel(x/zoom,y/zoom)
Next
Next

Cls

For y=0 To GraphicsHeight()
For x=0 To GraphicsWidth()
WritePixel x,y,pix(x,y)
Next
Next

WaitKey()
