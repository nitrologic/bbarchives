; ID: 1591
; Author: Subirenihil
; Date: 2006-01-05 16:09:05
; Title: 2D Mountain Generation
; Description: A 2D Mountain maker

Global w=1024,h=768,d=32;Modify for your system.
Graphics w,h,d,1
SetBuffer BackBuffer()
Cls

SeedRnd MilliSecs()

y=Rnd(100,h-1)
sy=Rnd(-2,2)
For x=0 to w-1
    py=y
    y=py+Rnd(-1,1)+sy
    If y<100 Then y=100
    If y>h-1 Then y=h-1
    sy=y-py
    If Abs(sy)>2 Then sy=Sgn(sy)*2
    Line x,y,x,h-1
Next

Flip
WaitKey()
End
