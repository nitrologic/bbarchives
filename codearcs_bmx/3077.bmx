; ID: 3077
; Author: Pineapple
; Date: 2013-09-22 21:30:13
; Title: Rotate and flip pixmaps
; Description: Rotates pixmaps CW, CCW, 180 degrees, and flips horizontally and vertically.

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


SuperStrict

Import brl.pixmap

' Example code

Rem

Graphics 280,398

' make the test pixmap
Local testpix:TPixmap=CreatePixmap(64,48,PF_RGB888)
ClearPixels testpix,~0
For Local i%=0 Until 16
	For Local j%=0 Until 16
		testpix.WritePixel i,j,$ff0000
		testpix.WritePixel testpix.width-i-1,j,$00ff00
		testpix.WritePixel i,testpix.height-j-1,$0000ff
	Next
Next

' draw the original and rotated/flipped pixmaps
Local x%=2,y%=2,yinc%=66
Local tx%=72,ty%=27
DrawPixmap testpix,x,y+yinc*0
DrawPixmap PixmapRotateCW(testpix),x,y+yinc*1
DrawPixmap PixmapRotateCCW(testpix),x,y+yinc*2
DrawPixmap PixmapRotate180(testpix),x,y+yinc*3
DrawPixmap PixmapFlipH(testpix),x,y+yinc*4
DrawPixmap PixmapFlipV(testpix),x,y+yinc*5
DrawText "Original",tx,ty+yinc*0
DrawText "Rotated Clockwise",tx,ty+yinc*1
DrawText "Rotated Counterclockwise",tx,ty+yinc*2
DrawText "Rotated 180",tx,ty+yinc*3
DrawText "Flipped Horizontally",tx,ty+yinc*4
DrawText "Flipped Vertically",tx,ty+yinc*5

' render it to the screen in a loop
Repeat
	Flip
	If KeyDown(27) Or AppTerminate() Then End
	Delay 100
Forever

EndRem

Function PixmapRotateCW:TPixmap(pix:TPixmap)
	Local ret:TPixmap=CreatePixmap(pix.height,pix.width,pix.format)
	For Local x%=0 Until ret.width
		Local gy%=pix.height-x-1
		For Local y%=0 Until ret.height
			ret.WritePixel x,y,pix.ReadPixel(y,gy)
		Next
	Next
	Return ret
End Function
Function PixmapRotateCCW:TPixmap(pix:TPixmap)
	Local ret:TPixmap=CreatePixmap(pix.height,pix.width,pix.format)
	For Local y%=0 Until ret.height
		Local gx%=pix.width-y-1
		For Local x%=0 Until ret.width
			ret.WritePixel x,y,pix.ReadPixel(gx,x)
		Next
	Next
	Return ret
End Function
Function PixmapRotate180:TPixmap(pix:TPixmap)
	Local ret:TPixmap=CreatePixmap(pix.width,pix.height,pix.format)
	For Local x%=0 Until ret.width
		Local gx%=pix.width-x-1
		For Local y%=0 Until ret.height
			Local gy%=pix.height-y-1
			ret.WritePixel x,y,pix.ReadPixel(gx,gy)
		Next
	Next
	Return ret
End Function
Function PixmapFlipH:TPixmap(pix:TPixmap)
	Local ret:TPixmap=CreatePixmap(pix.width,pix.height,pix.format)
	For Local x%=0 Until pix.width
		Local gx%=pix.width-x-1
		For Local y%=0 Until pix.height
			ret.WritePixel x,y,pix.ReadPixel(gx,y)
		Next
	Next
	Return ret
End Function
Function PixmapFlipV:TPixmap(pix:TPixmap)
	Local ret:TPixmap=CreatePixmap(pix.width,pix.height,pix.format)
	For Local y%=0 Until pix.height
		Local gy%=pix.height-y-1
		For Local x%=0 Until pix.width
			ret.WritePixel x,y,pix.ReadPixel(x,gy)
		Next
	Next
	Return ret
End Function
