; ID: 2109
; Author: TomToad
; Date: 2007-09-19 21:22:57
; Title: CopyImageRect()
; Description: Copy a rectangular section of one TImage to another

Strict
Const Filename:String = "169a.jpg" 'name of image to load

Graphics 800,600

Local Image1:TImage = LoadImage(Filename) 'load the images into pixmaps
Local Image2:TImage = LoadImage(YFlipPixmap(LockImage(Image1))) 'flip second copy
UnlockImage(Image1)

Local Image1Width:Int = ImageWidth(Image1)
Local Image1Height:Int = ImageHeight(Image1)
Local Image2Width:Int = ImageWidth(Image2)
Local Image2Height:Int = ImageHeight(Image2)

While Not KeyHit(KEY_ESCAPE) And Not AppTerminate()
	Local MX:Int = MouseX()
	Local MY:Int = MouseY()
	Cls
	SetColor 255,255,255
	
	DrawImage Image1,0,0
	DrawImage Image2,400,0
	
	SetColor 255,0,0
	DrawLine MX,MY,MX+64,MY
	DrawLine MX,MY,MX,MY+64
	DrawLine MX+64,MY,MX+64,MY+64
	DrawLine MX,MY+64,MX+64,MY+64
	
	Flip
	If MouseHit(1)
		If MX >= 0 And MX < Image1Width And MY >= 0 And MY < Image1Height
			CopyImageRect(Image1,MX,MY,64,64,Image2,MX,MY)
		Else If MX - 400 >= 0 And MX - 400 < Image2Width And MY >= 0 And MY < Image2Height
			CopyImageRect(Image2,MX-400,MY,64,64,Image1,MX-400,MY)
		End If
	End If
Wend


Function CopyImageRect(Source:TImage,SX:Int,SY:Int,SWidth:Int,SHeight:Int,Dest:TImage,DX:Int,DY:Int)
	'get the pixmap for the images
	Local SourcePix:TPixmap = LockImage(Source)
	Local DestPix:TPixmap = LockImage(Dest)
	
	'find the dimentions
	Local SourceWidth:Int = PixmapWidth(SourcePix)
	Local SourceHeight:Int = PixmapHeight(SourcePix)
	Local DestWidth:Int = PixmapWidth(DestPix)
	Local DestHeight:Int = PixmapHeight(DestPix)
	
	If SX < SourceWidth And SY < SourceHeight And DX < DestWidth And DY < DestHeight 'make sure rects are on image
		If SX+SWidth > SourceWidth Then SWidth = SourceWidth - SX 'bound the coordinates to the image area
		If SY+SHeight > SourceHeight Then SHeight = SourceHeight - SY
		If DX+SWidth > DestWidth Then SWidth = DestWidth - DX 'Make sure coordinates will fit into the destination
		If DY+SHeight > DestHeight Then SHeight = DestHeight - DY
		
		'find the pitch
		Local SourcePitch:Int = PixmapPitch(SourcePix)
		Local DestPitch:Int = PixmapPitch(DestPix)
	
		'pointers To the first pixel of pixmaps
		Local SourcePtr:Byte Ptr = PixmapPixelPtr(SourcePix) + SY * SourcePitch + SX * 4
		Local DestPtr:Byte Ptr = PixmapPixelPtr(DestPix) + DY * DestPitch + DX * 4
		
		'copy pixels over one line at a time
		For Local i:Int = 1 To SHeight
			MemCopy(DestPtr,SourcePtr,SWidth*4)
			SourcePtr :+ SourcePitch
			DestPtr :+ DestPitch
		Next
	End If
	
	'unlock the buffers
	UnlockImage(Source)
	UnlockImage(Dest)
End Function
