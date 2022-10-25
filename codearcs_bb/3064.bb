; ID: 3064
; Author: jfk EO-11110
; Date: 2013-08-18 20:32:54
; Title: Smooth Fullscreen win8
; Description: Workaround for win8 fullscreen issue

w=800:h=600
Graphics3D w,h,32,1
SetBuffer BackBuffer()
;SetBuffer FrontBuffer()

While KeyHit(1)<>1
 x=x+1
 If x >w Then x=0
 Color 0,0,0
 Rect 0,0,800,100,1
 Color 255,0,0
 Text 10,10,(MilliSecs()-t)
 ;Plot x,40
 ;Line x,0,x,100
 Rect x,0,w,100,1
 t=MilliSecs()
 Flip 1 
Wend
End

Function Flip(wait_sync)
 Local w=GraphicsWidth()
 Local h=GraphicsHeight()
 Local sc, old_sc
 If wait_sync<>0
  Repeat
   old_sc=sc
   Delay 1
   sc=ScanLine()
  Until (sc<old_sc) Or (KeyDown(1)=1)
 EndIf
 CopyRect 0,0,w,h,0,0,BackBuffer(),FrontBuffer()
End Function


Function VWait()
 Local sc, old_sc
 Repeat
  old_sc=sc
  Delay 1
  sc=ScanLine()
 Until (sc<old_sc) Or (KeyDown(1)=1)
End Function
