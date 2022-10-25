; ID: 3073
; Author: Pineapple
; Date: 2013-08-28 00:00:39
; Title: Windows clipboard - text and pixmap copy/paste
; Description: Read and write strings and pixmaps

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


' Thanks to Birdie whose post I ripped most of the string code from:
' http://www.blitzbasic.com/Community/post.php?topic=43255&post=483714

' Also thanks to Jim Brown:
' http://blitzbasic.com/codearcs/codearcs.php?code=699

' Pixmap/DIB code written with the help of Wikipedia:
' http://en.wikipedia.org/wiki/BMP_file_format

SuperStrict

Import brl.pixmap



' Example code

Rem

setclipboardtext("Pineapples are the best")
Print getclipboardtext()

EndRem



?Win32

Extern "Win32"
	Const GMEM_FIXED%=0
	Const CF_TEXT%=1
	Const CF_DIB%=8
	Function OpenClipboard(hwnd%)
	Function CloseClipboard()
	Function EmptyClipboard()
	Function SetClipboardData(format%,hMem@ Ptr)
	Function GetClipboardData@ Ptr(format%)
	Function GlobalAlloc@ Ptr(uflags%,bytes%)
	Function GlobalFree(buf@ Ptr)
	Function IsClipboardFormatAvailable%(format%)
End Extern  

Function GetClipboardPixmap:TPixmap()
	If OpenClipboard(0) And IsClipboardFormatAvailable(CF_DIB)
		Local data@ Ptr=GetClipboardData(CF_DIB)
		CloseClipboard
		Return PixmapFromDIB(data)
	Else
		Return Null
	EndIf
End Function

Function SetClipboardPixmap%(pix:TPixmap)
	If OpenClipboard(0)
		EmptyClipboard
		Local hbuf@ Ptr=PixmapToDIB(pix)
		SetClipboardData CF_DIB,hbuf
		CloseClipboard
		Return True
	Else
		Return False
	EndIf
End Function

Function PixmapToDIB@ Ptr(pix:TPixmap)
	Assert pix
	Local pixdatasize%=0,bpp%=0
	If pix.format=PF_RGB888 Or pix.format=PF_BGR888
		Local bytesw%=pix.width*3,pm4%=bytesw Mod 4
		If pm4>0 Then bytesw:+(4-pm4)
		pixdatasize=bytesw*pix.height
		bpp=24
	'ElseIf pix.format=PF_RGBA8888 Or pix.format=PF_BGRA8888
	'	pixdatasize=pix.width*4*pix.height
	'	bpp=32
	EndIf
	Assert bpp,"Can't copy pixmaps of that format"
	Const headersize%=40
	Local bdata@ Ptr=GlobalAlloc(GMEM_FIXED,headersize+pixdatasize)
	Local idata% Ptr=Int Ptr(bdata)
	Local sdata@@ Ptr=Short Ptr(bdata)
	idata[0]=headersize
	idata[1]=pix.width
	idata[2]=pix.height
	sdata[6]=1
	sdata[7]=bpp
	Print bpp
	idata[4]=0
	idata[5]=pixdatasize
	idata[6]=0
	idata[7]=0
	idata[8]=0
	idata[9]=0
	If bpp=24
		Local pdata@ Ptr=bdata+headersize
		For Local y%=pix.height-1 To 0 Step -1
			For Local x%=0 Until pix.width
				Local rgb%=pix.ReadPixel(x,y)
				pdata[0]=(rgb Shr 16)&$ff
				pdata[1]=(rgb Shr 8)&$ff
				pdata[2]=(rgb)&$ff
				pdata:+3
			Next
			Local wm4%=pix.width Mod 4
			If wm4>0 Then pdata:+(4-wm4)
		Next
		Return bdata
	'ElseIf bpp=32
	'	Local pdata@ Ptr=bdata+headersize
	'	For Local y%=pix.height-1 To 0 Step -1
	'		For Local x%=0 Until pix.width
	'			Local argb%=pix.ReadPixel(x,y)
	'			pdata[2]=(argb Shr 16)&$ff
	'			pdata[1]=(argb Shr 8)&$ff
	'			pdata[0]=(argb)&$ff
	'			pdata[3]=(argb Shr 24)&$ff
	'			pdata:+4
	'		Next
	'	Next
	'	Return bdata
	EndIf
	Return Null
End Function

Function PixmapFromDIB:TPixmap(bdata@ Ptr)
	Local idata% Ptr=Int Ptr(bdata)
	Local sdata@@ Ptr=Short Ptr(bdata)
	Local headersize%=idata[0]
	'If headersize<>40 Return Null ' indicates invalid bitmap
	Local width%=idata[1],height%=idata[2]
	Local planes@@=sdata[6],bpp@@=sdata[7]
	Assert bpp=24,bpp+"-bit DIBs not supported" ' Or bpp=32
	If planes<>1 Return Null ' bad bitmap
	Local compression%=idata[4]
	If compression>6 Or compression<0 Return Null ' bad bitmap
	Assert compression=0,"Compressed DIBs not supported"
	Local bitmapsize%=idata[5]
	Local xppm%=idata[6] ' horizontal resolution (pixels/meter)
	Local yppm%=idata[7] ' vertical resolution (pixels/meter)
	Local palexp%=idata[8]
	Local importantcols%=idata[9]
	If bpp=24
		Local pix:TPixmap=CreatePixmap(width,height,PF_RGB888)
		Local pdata@ Ptr=bdata+headersize
		For Local y%=height-1 To 0 Step -1
			For Local x%=0 Until width
				Local rgb%=(pdata[2] Shl 16) | (pdata[1] Shl 8) | pdata[0]
				pix.WritePixel x,y,rgb
				pdata:+3
			Next
			Local wm4%=width Mod 4
			If wm4>0 Then pdata:+(4-wm4)
		Next
		Return pix
	'ElseIf bpp=32
	'	Local pix:TPixmap=CreatePixmap(width,height,PF_RGBA8888)
	'	Local pdata@ Ptr=bdata+headersize
	'	For Local y%=height-1 To 0 Step -1
	'		For Local x%=0 Until width
	'			Local argb%=(pdata[3] Shl 24) | (pdata[0] Shl 16) | (pdata[1] Shl 8) | pdata[2]
	'			pix.WritePixel x,y,argb
	'			pdata:+4
	'		Next
	'	Next
	'	Return pix
	EndIf
	Return Null
End Function

Function GetClipboardText$()
	If OpenClipboard(0) And IsClipboardFormatAvailable(CF_TEXT)
		Local str$=String.FromCString(GetClipboardData(CF_TEXT))
		CloseClipboard
		Return str
	Else
		Return Null
	EndIf
End Function

Function SetClipboardText%(str$)
	If OpenClipboard(0)
		EmptyClipboard
		Local hbuf@ Ptr=GlobalAllocString(str)
		SetClipboardData CF_TEXT,hbuf
		CloseClipboard
		Return True
	Else
		Return False
	EndIf
End Function

Function GlobalAllocString@ Ptr(txt$)
	Local p@ Ptr=GlobalAlloc(GMEM_FIXED,txt.length+1)
	For Local i%=0 Until txt.length
		Assert txt[i]>0
		p[i]=txt[i]
	Next
	p[txt.length+1]=0
	Return p
End Function

?
