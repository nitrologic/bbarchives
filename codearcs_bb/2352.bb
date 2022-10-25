; ID: 2352
; Author: TomToad
; Date: 2008-11-01 15:59:19
; Title: DrawPixmapRect()
; Description: draws a rectangular portion of a pixmap.  DX only

SuperStrict

'This function will draw a rectangular portion of a pixmap to the screen
'pixmap = source pixmap
'x, y, Width = Screen coordinates of where to draw the pixmap
'SrcX, SrcY = the pixmap coordinates of what to draw
'Width, Height = the width and height of the rectangular section to draw

Function DrawPixmapRect( pixmap:TPixmap,x:Int,y:Int,SrcX:Int,SrcY:Int,Width:Int,Height:Int )
	'make sure parameters are valid and clip to the screen
	If SrcX >= Pixmap.Width Or SrcY >= Pixmap.Height Then Return 'rect right or bottom of pixmap
	If SrcX+Width < 0 Or SrcY+Height < 0 Then Return 'rect left or top of pixmap
	If SrcX < 0 Then Width :+ SrcX; SrcX = 0 'clip To Left of pixmap
	If SrcY < 0 Then Height :+ SrcY; SrcY = 0 'Clip to top of pixmap
	If SrcX + Width >= pixmap.Width Then Width :- (SrcX + Width) - pixmap.Width 'clip to the right of pixmap
	If SrcY + Height >= pixmap.Height Then Height :- (SrcY + Height) - pixmap.Height 'clip to the bottom of pixmap
	Local ScreenWidth:Int = GraphicsWidth()
	Local ScreenHeight:Int = GraphicsHeight()
	If x >= ScreenWidth Or y >= ScreenHeight Then Return 'off screen at right or bottom
	If x+Width < 0 Or y+Height < 0 Then Return 'off screen at left or top
	If x < 0 Then Width :+ x; x = 0 'clip to left edge
	If y < 0 Then Height :+ y; y = 0 'clip to top edge
	If x + Width >= ScreenWidth Then Width :- (x + Width) - ScreenWidth 'clip to right edge
	If y + Height >= ScreenHeight Then Height :- (y + Height) - ScreenHeight 'Clip to bottom

	'much of this is the same as DrawPixmap()
	Local srcdc:Int,destdc:Int
	Local surf:IDirectDrawSurface7
	Local renderSurf:IDirectDrawSurface7

	D3D7GraphicsDriver().EndScene

	TD3D7Max2DDriver(_max2dDriver).device.GetRenderTarget Varptr renderSurf
	
	renderSurf.GetDC Varptr destdc
	surf=TD3D7Max2DDriver(_max2dDriver).surffrompixmap( pixmap )
	surf.GetDC Varptr srcdc
	BitBlt destdc,x,y,Width,Height,srcdc,SrcX,SrcY,ROP_SRCCOPY 'this has changed to reflect the new parameters
	surf.ReleaseDC srcdc
	renderSurf.ReleaseDC destdc
	surf.Release_

	D3D7GraphicsDriver().BeginScene
End Function

'Here is an example of the function in use
Graphics 800,600

For Local t:Int = 1 To 100
	SetColor Rand(0,255),Rand(0,255),Rand(0,255)
	
	DrawOval Rand(-50,799),Rand(-50,599),Rand(10,200),Rand(10,200)
Next
SetColor 255,255,255

Local pixmap:TPixmap = GrabPixmap(0,0,800,600)

While Not KeyHit(KEY_ESCAPE) And Not AppTerminate()
	Local MX:Int = MouseX()
	Local MY:Int = MouseY()
	
	Cls
	DrawPixmapRect(pixmap,MX-50,MY-50,MX-50,MY-50,100,100)
	Flip
Wend
