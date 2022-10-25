; ID: 2148
; Author: JoshK
; Date: 2007-11-07 11:02:08
; Title: Clipboard Module
; Description: Copy and paste text or pixmaps

' Clipboard Text - Copy/Paste functions
Strict

Module leadwerks.clipboard
ModuleInfo "Version: 1.0.0"
ModuleInfo "Author: Joshua Klint"
ModuleInfo "www.leadwerks.com"

Import brl.pixmap
Import pub.win32

Extern "Win32"
	Function OpenClipboard%(hwnd%)
	Function CloseClipboard%()
	Function EmptyClipboard%()
	Function IsClipboardFormatAvailable%(format%)
	Function GetClipboardData:Byte Ptr(Format:Int)
	Function SetClipboardData(format%, hMem:Byte Ptr)
	Function GlobalAlloc(Flags:Int, Bytes:Int)
	Function GlobalFree(Mem:Int)
	Function GlobalLock:Byte Ptr(Mem:Int)
	Function GlobalUnlock(Mem:Int)
	Function CreateBitmap:Byte Ptr(width:Int,height:Int,colorplanes:Int,bpp:Int,data:Byte Ptr)
End Extern 

Const CF_TEXT%=$1
Const CF_BITMAP%=2
Const GMEM_MOVEABLE%=$2
Const GMEM_DDESHARE%=$2000

' -----------------------------------------------

Function ClipboardText:String()
	If Not OpenClipboard(0)	Return ""
	Local TextBuf:Byte Ptr = GetClipboardData(CF_TEXT)
	CloseClipboard()
	Return String.FromCString(TextBuf)
End Function 
	
Function SetClipboardText:Int(txt:String)
	Local result:Int=False
	If txt$="" Return
	Local TextBuf:Byte Ptr = Txt.ToCString()
	Local Memblock:Int = GlobalAlloc(GMEM_MOVEABLE|GMEM_DDESHARE, txt.Length+1)
	Local DataBuf:Byte Ptr = GlobalLock(Memblock)
	MemCopy DataBuf, TextBuf, Txt.length
	If OpenClipboard(0)
		EmptyClipboard
		SetClipboardData CF_TEXT, DataBuf
		CloseClipboard
		result=True
	EndIf
	GlobalUnlock Memblock
	GlobalFree Memblock
	Return result
End Function

Function SetClipboardPixmap:Int(pixmap:TPixmap)
	If Not pixmap Return
	Local result:Int=False
	Local hbitmap:Byte Ptr
	If pixmap.format<>PF_BGRA8888 pixmap=ConvertPixmap(pixmap,PF_BGRA8888)
	If Not pixmap Return
	hbitmap=CreateBitmap(pixmap.width,pixmap.height,1,32,pixmap.pixels)
	If Not hbitmap Return
	If OpenClipboard(0)
		EmptyClipboard
		SetClipboardData CF_BITMAP,hbitmap
		CloseClipboard
		result=True
	EndIf
	DeleteObject Int(hbitmap)
	Return result
EndFunction
