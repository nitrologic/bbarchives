; ID: 1849
; Author: Devils Child
; Date: 2006-10-24 23:17:51
; Title: ResizeImage(img,w,h,frame)
; Description: Resizes an image with frame!

Function ResizeImage(image, newwidth, newheight, frame = 0)
tbuffer = GraphicsBuffer()
oldwidth = ImageWidth(image)
oldheight = ImageHeight(image)
ni = CreateImage(newwidth + 1, oldheight)
dest = CreateImage(newwidth, newheight)
SetBuffer ImageBuffer(ni)
For x = 0 To newwidth
	DrawBlockRect image, x, 0, Floor(oldwidth * x / newwidth), 0, 1, oldheight, frame
Next
SetBuffer ImageBuffer(dest)
For y = 0 To newheight
	DrawBlockRect ni, 0, y, 0, Floor(oldheight * y / newheight), newwidth, 1
Next 
FreeImage ni
SetBuffer tbuffer
Return dest
End Function
