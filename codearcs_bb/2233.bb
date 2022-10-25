; ID: 2233
; Author: Yo! Wazzup?
; Date: 2008-03-24 01:12:30
; Title: 2 Points into a rectangle
; Description: Turns 2 Points into a rectangle

Graphics 1280,1024,32,2
SetBuffer BackBuffer()

Print "Click anywhere on the screen."
Locate 0,0
WaitMouse()
point1x=MouseX()
point1y=MouseY()
Cls
Plot point1x, point1y
Locate 0,0
Print "Click somewhere else."
WaitMouse()
point2x=MouseX()
point2y=MouseY()
Cls
Plot point1x, point1y
Plot point2x, point2y
Locate 0,0
Print "Now press left shift to make a solid rectangle or right shift to make a hollow rectangle."
While KeyHit(42)=False
If KeyHit(54) Then
FlushKeys()
Cls
RectFrom2Points(point1x, point1y, point2x, point2y, 0)
Locate 0,0
Print "Press any key to quit."
WaitKey()
End
EndIf
Wend
FlushKeys()
Cls
RectFrom2Points(point1x, point1y, point2x, point2y)
Locate 0,0
Print "Press any key to quit."
WaitKey()
End
Function min(a,b)
If a>b Then
Return a 
Else
Return b
EndIf
End Function
Function RectFrom2Points(point1x, point1y, point2x, point2y, solid=1)

rectWidth = Abs(point1x-point2x)
rectHeight = Abs(point1y-point2y)
xstart = min(point1x, point2x)
ystart = min(point1y, point2y)



Rect xstart, ystart, rectwidth, rectheight, solid

Flip


End Function
