; ID: 1752
; Author: markcw
; Date: 2006-07-14 11:52:07
; Title: Capture Screen functions
; Description: Uses User32.dll and Gdi32.dll decls

;Capture Screen Area function, by markcw on 14 Jul 06

Graphics3D 320,240,0,2
SetBuffer BackBuffer()

initime=MilliSecs()

While Not KeyHit(1)

 If done=0 And MilliSecs()>initime+50 ;wait for text info
  done=1 : CaptureScreenArea(0,0,320,240) ;x,y,width,height
 EndIf

 Cls
 Text 0,0,"OK, paste the current clipboard data"
 Text 0,12,"to whatever program you use to see"
 Text 0,24,"the results of the screen capture."
 Text 0,36,"done="+done+" initime="+initime

Flip
Wend

Function CaptureScreenArea(sx,sy,swidth,sheight)
 ;Capture a given screen area to the clipboard
 ;From PureBasic CodeArchiv source, by wayne1 on 30/1/2002

 Local hdcSrc,hdcDst,hbmp,hold

 hdcSrc=Api_GetDC(0) ;hwnd
 hdcDst=Api_CreateCompatibleDC(hdcSrc) ;hdc
 hbmp=Api_CreateCompatibleBitmap(hdcSrc,swidth,sheight) ;hdc,width,height
 hold=Api_SelectObject(hdcDst,hbmp) ;hdc,hobject[bitmap]
 Api_BitBlt(hdcDst,0,0,swidth,sheight,hdcSrc,sx,sy,$00CC0020) ;SrcCopy
 Api_SelectObject(hdcDst,hold) ;restore old to dc
 If Api_OpenClipboard(0) ;hwnd
  Api_EmptyClipboard() ;free last data
  Api_SetClipboardData(2,hbmp) ;CF_Bitmap,hdata[bitmap]
  Api_CloseClipboard()
 EndIf
 Api_ReleaseDC(0,hdcSrc) ;hwnd,hdc
 Api_DeleteDC(hdcDst) ;hdc

End Function
