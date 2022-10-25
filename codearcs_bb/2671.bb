; ID: 2671
; Author: stanrol
; Date: 2010-03-21 22:45:52
; Title: draw rectangle
; Description: draw rectangle with lines

Function rec(x,y,w,h)
Line x,y,x+w,y
Line x+w,y,x+w,y+h
Line x+w,y+h,x,y+h
Line x,y+h,x,y
Return Abs(w*h)
End Function
Graphics 800,600,0,2
Text 1,5,"Hi there this draws a rectangle"
Color 22,220,79
AppTitle "area is "+Rec(1,1,200,300)
WaitKey
