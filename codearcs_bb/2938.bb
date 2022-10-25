; ID: 2938
; Author: GG
; Date: 2012-03-21 14:50:23
; Title: Mandelbrot in a nutshell
; Description: 273 chars in 4 lines.

w=800:h=600:Graphics w,h,16,2:LockBuffer For x=0 To w-1 For y=0 To h-1
WritePixel x,y,m((3.0*x/w)-2,(2.0*y/h)-1):Next:Next:UnlockBuffer:Flip:MouseWait
Function m(k#,l#,i=63):For c=0 To i:If (x#*x)+(y#*y)>4 Return c*263168+128
t#=(x*x)-(y*y)+k y=(2*x*y)+l x=t:Next:End Function
