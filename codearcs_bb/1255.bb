; ID: 1255
; Author: Jeppe Nielsen
; Date: 2005-01-06 15:13:43
; Title: Sinus scroller
; Description: A simple sinusscroller

Graphics 800,600
SetBuffer BackBuffer()
Repeat
Cls
x=MilliSecs()/10.0 Mod 800
SinScroll("A nice sinus scroller, just a small example how this can be done :)",800-x,300,MilliSecs()/5.0)
SinScroll("A nice sinus scroller, just a small example how this can be done :)",800-x-800,300,MilliSecs()/5.0)
Flip
Until KeyDown(1)
End
Function SinScroll(txt$,x,y,am,amp=25,per=20,d=10)
For n=1 To Len(txt$)
Text x+xx,y+Sin(am+n*per)*amp,Mid$(txt$,n,1)
xx=xx+d
Next
End Function
