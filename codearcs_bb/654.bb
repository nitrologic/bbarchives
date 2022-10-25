; ID: 654
; Author: Ziltch
; Date: 2003-04-15 08:38:52
; Title: GetDesktop
; Description: Make a snapshot of users desktop

Const SRCCOPY = $CC0020
Const CF_BITMAP = 2
Const SW_HIDE = 0
Const SW_SHOW = 5

Function GetDesktop(flag=0,update=0)

; ADAmor ZILTCH 2003
;
; This command must come after your GRAPHICS(3D) x,y command.
;
;  flag 0 = create texture
;       1 = create image
;
; if update is not 0 then it is the tex/image to update.

   DeskHwnd = GetDesktopWindow()

   ; Get screen coordinates
   fwidth  = GetSystemMetrics%(0)  ;RectWin\rightR  - RectWin\leftR
   fheight = GetSystemMetrics%(1)  ;RectWin\bottomR - RectWin\topR

   BlitzHwnd = GetActiveWindow()
   ShowWindow(BlitzHwnd,SW_HIDE)

   ; Get the device context of Desktop and allocate memory
   hdc = GetDC(DeskHwnd)
   Blitzhdc = GetDC(BlitzHwnd)

   ; Copy data
   BitBlt(Blitzhdc, 0, 0, fwidth, fheight, hdc, 0,0, SRCCOPY)

   ; Clean up handles
   ReleaseDC(DeskHwnd, hdc)
   ReleaseDC(BlitzHwnd, Blitzhdc)
   ShowWindow(BlitzHwnd,SW_SHOW)

   ; Create/update texture or image
   Select flag
     Case 0
       If update = 0 Then
         tex=CreateTexture(fwidth,fheight)
       Else
         tex=update
       End If
       CopyRect 0,0,fwidth,fheight,0,0,FrontBuffer(),TextureBuffer(tex)
       Return tex
     Case 1
       If update = 0 Then
         image=CreateImage(fwidth,fheight)
       Else
         image=update
       End If
       CopyRect 0,0,fwidth,fheight,0,0,FrontBuffer(),ImageBuffer(image)
       Return image
   End Select

End Function
;---------------- end code

Lines needed in Userlib: User32.decls

FindWindow%( class$,Text$ ):"FindWindowA"
ShowWindow(hwnd%,nCmdShow%)
GetActiveWindow%()
GetDC%(hWnd% )
ReleaseDC%(hWnd%,hDC%)
GetDesktopWindow%()
GetSystemMetrics%(nIndext%)

Lines needed in Userlib: Gdi32.decls
BitBlt%(hDestDC%,X%,Y%,nWidth%,nHeight%,hSrcDC,XSrc,YSrc,dwRop)
