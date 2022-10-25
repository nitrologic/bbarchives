; ID: 136
; Author: skidracer
; Date: 2002-05-05 19:21:56
; Title: Polygon
; Description: Draws Triangles and Quads

; poly.bb
; by simon@acid.co.nz

Dim xval(20)
Dim yval(20)

Graphics 640,480
Color 255,0,255

Triangle 50,0,80,20,30,20

WaitKey
End

Function Triangle(x0,y0,x1,y1,x2,y2)
	xval(0)=x0
	yval(0)=y0
	xval(1)=x1
	yval(1)=y1
	xval(2)=x2
	yval(2)=y2
	poly(3)
End Function

Function Quad(x0,y0,x1,y1,x2,y2,x3,y3)
	xval(0)=x0
	yval(0)=y0
	xval(1)=x1
	yval(1)=y1
	xval(2)=x2
	yval(2)=y2
	xval(3)=x3
	yval(3)=y3
	poly(4)
End Function

Function poly(vcount)
; get clipping region
	width=GraphicsWidth()
	height=GraphicsHeight()
; find top verticy
	b=vcount-1
	y=yval(0)
	While c<>b
		c=c+1
		yy=yval(c)
		If yy<y y=yy d=c
	Wend
	c=d 
	t=c
; draw top to bottom
	While y<height
; get left gradient
		If y=yval(c)
			While y=yval(c)
				x0=xval(c) Shl 16
				c=c+1
				If c>b c=a
				If c=t Return
				If y>yval(c) Return
			Wend
			h=yval(c)-y
			g0=((xval(c) Shl 16)-x0)/h
		EndIf
; get right gradient
		If y=yval(d)
			While y=yval(d)
				x1=xval(d) Shl 16
				d=d-1
				If d<a d=b
				If y>yval(d) Return
			Wend
			h=yval(d)-y
			g1=((xval(d) Shl 16)-x1)/h
		EndIf
; calc horizontal span
		x=x1 Sar 16
		w=((x0 Sar 16)-x)+1
; draw down to next vert
		If (w>0 And y>-1 And x<width And x+w>0)
			If x<0 w=w+x x=0	;crop left
			If x+w>width w=width-x	;crop right
			Rect x,y,w,1
		EndIf
; next	
		x0=x0+g0
		x1=x1+g1
		y=y+1
	Wend
End Function
