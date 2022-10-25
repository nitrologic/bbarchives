; ID: 2954
; Author: Pineapple
; Date: 2012-06-27 15:48:02
; Title: Rectangle
; Description: find intersections and minimum bounding boxes of rectangles

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict

'example code
Rem
Graphics 512,512
SetBlend alphablend

Global r:rectangle[8],ms%=0

	r[0]=rectangle.Create(100,40,150,310)
	r[1]=rectangle.Create(50,30,160,190)
	r[2]=rectangle.Create(20,90,300,150)

Repeat
	Cls
	drs
	drr
	If MouseHit(1) Then ms:+1;ms=ms Mod 3
	Flip
Until AppTerminate() Or KeyDown(27)

Function drs()
	r[ms].x=MouseX()
	r[ms].y=MouseY()
	r[3]=r[0].intersection(r[1])
	r[4]=r[1].intersection(r[2])
	r[5]=r[2].intersection(r[0])
	r[6]=r[3].intersection(r[4])
	r[7]=r[0].container(r[1])
End Function
Function drr()
	SetAlpha 1
	For Local x%=0 To 6
		SetColor 255*(x=0 Or x=3 Or x=5),255*(x=1 Or x=3 Or x=4),255*(x=2 Or x=4 Or x=5)
		If x=6 SetColor 255,255,255
		dr r[x]
	Next
	SetColor 192,192,192
	SetAlpha 0.5
	dr r[7]
End Function
Function dr(r:rectangle)
	If Not(r And r.exists()) Then Return
	DrawRect r.x,r.y,r.w,r.h
End Function
EndRem 

Type rectangle
	Field x%,y%,w%,h%
	' return a new rectangle with the specified dimensions
	Function Create:rectangle(x%,y%,w%,h%)
		Local n:rectangle=New rectangle
		n.x=x
		n.y=y
		n.w=w
		n.h=h
		Return n
	End Function
	' return a new rectangle with the same dimensions as this one
	Method copy:rectangle()
		Return rectangle.Create(x,y,w,h)
	End Method
	' return the rectangle that is the intersection of this and another
	Method intersection:rectangle(r2:rectangle)
		Local g:rectangle=r2.copy()
		If g.x<x Then g.x=x;g.w:-(x-r2.x)
		If g.y<y Then g.y=y;g.h:-(y-r2.y)
		If g.x>x+w Then g.x=x+w
		If g.y>y+h Then g.y=y+h
		If g.x+g.w>x+w Then g.w:-(g.x+g.w)-(x+w)
		If g.y+g.h>y+h Then g.h:-(g.y+g.h)-(y+h)
		If g.x+g.w<x Then g.w=0
		If g.y+g.h<y Then g.h=0
		Return g
	End Method
	' return the smallest rectangle that contains both this and another (aka minimum bounding box)
	Method container:rectangle(r2:rectangle)
		Local g:rectangle=r2.copy()
		If g.x>x Then g.x=x
		If g.y>y Then g.y=y
		If g.x+g.w<x+w Then g.w=w+(x-g.x)
		If g.y+g.h<y+h Then g.h=h+(y-g.y)
		If g.x+g.w<r2.x+r2.w Then g.w=r2.w+(r2.x-g.x)
		If g.y+g.h<r2.y+r2.h Then g.h=r2.h+(r2.y-g.y)
		Return g
	End Method
	' does the rectangle exist?
	Method exists%()
		Return w>0 And h>0
	End Method
	' return the area of the rectangle
	Method area%()
		Return w*h
	End Method
	' return the perimeter of the rectangle
	Method perimeter%()
		Return w+w+h+h
	End Method
	' return the length of the line from one corner of the rectangle to its opposite
	Method diagonal#()
		Return ((w*w)+(h*h))^.5
	End Method
	' returns 1 if a rectangle is entirely inside this one, 0 otherwise
	Method containsrect%(r:rectangle)
		If intersection(r).equals(r) Then Return 1
		Return 0
	End Method
	' returns 1 if a point is inside this rectangle, 0 otherwise
	Method containspoint%(px%,py%)
		If (px>=x And py>=y And px<x+w And py<y+h) Then Return 1
		Return 0
	End Method
	' returns 1 if this rectangle overlaps another, 0 otherwise
	Method overlaps%(o:rectangle)
		If x+w>o.x And x<o.x+o.w And y+h>o.y And y<o.y+o.h Then Return 1
		Return 0
	End Method
	' returns 1 if this rectangle has the same dimensions as another, 0 otherwise
	Method equals%(o:rectangle)
		Return x=o.x And y=o.y And w=o.w And h=o.h
	End Method
End Type
