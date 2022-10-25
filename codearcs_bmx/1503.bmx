; ID: 1503
; Author: Filax
; Date: 2005-10-25 16:20:26
; Title: Resize image fast
; Description: How resize an image ?

' ------------
' Open graphic
' ------------
Graphics 1024,768,0

Global MyIMage01:Timage=LoadImage("colourblind.jpeg")

Global MyIMage02:Timage=ResizeImage(MyIMage01,256,256)


' -------------
' The main loop
' -------------
While Not KeyHit(KEY_ESCAPE)
	Cls

	DrawImage MyIMage02,0,0
	
	' ------------------------
	' Swap buffer and flushmem
	' ------------------------
	Flip
Wend 


Function ResizeImage:Timage(Image:TImage,Tx:Int,Ty:Int)
	Local x
	Local y
	Local c
	Local Pixmap:TPixmap
	Local Duplicate:TPixmap
		
	Local Output:Timage

	Duplicate=CreatePixmap(ImageWidth(Image),ImageHeight(Image),PF_RGB888)
	Pixmap = LockImage(Image)
	
	For x = 0 To ImageWidth(Image) - 1
		For y = 0 To ImageHeight(Image) - 1
			
			c=ReadPixel(Pixmap, x, y)
			WritePixel(Duplicate, x,y,c)
			
		Next			
	Next

	UnlockImage(Image)
	Pixmap= Null
	
	
	Resized=ResizePixmap(Duplicate,Tx,Ty)
	
	Output=CreateImage(Tx,Ty)
	Pixmap=LockImage(Output)
	
	For x = 0 To Tx-1
		For y = 0 To Ty-1 
			c=ReadPixel(Resized, x, y)
			WritePixel(Pixmap, x,y,c)
		Next			
	Next

	UnlockImage(Output) 
	
	Duplicate= Null	
	Pixmap= Null
	
	Return Output
End Function
