; ID: 1725
; Author: TomToad
; Date: 2006-05-28 08:26:47
; Title: Screen Capture Mod
; Description: A mod for capturing the screen/desktop

Module toad.capture

Import BRL.Pixmap
Import pub.Win32
Import BRL.Basic

Extern "Win32"
	Function GetPixel:Int(hdc:Int,x:Int,y:Int)
End Extern

Private
Global Width:Int
Global Height:Int

Public
Function ScreenCapture:TPixmap()
	Local Pixmap:TPixmap
	Local Pixel:Int
	Local hdc:Int = GetDC(Null)
	
	Width:Int = GetDeviceCaps(hdc,HORZRES)
	Height:Int = getdevicecaps(hdc,VERTRES)
	Pixmap = CreatePixmap(Width,Height,PF_RGBA8888)
	For Local y = 0 To Height - 1
		For Local x = 0 To Width - 1
			Pixel = GetPixel(hdc,x,y)
			WritePixel(PixMap,x,y,$FF000000+(Pixel & $ff)Shl 16+(Pixel & $ff00)+(Pixel & $ff0000)Shr 16)
		Next
	Next
	
	Return PixMap

End Function

Function GetScreenCaptureWidth:Int()
	Return Width
End Function

Function GetScreenCaptureHeight:Int()
	Return Height
End Function
