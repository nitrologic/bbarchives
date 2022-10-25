; ID: 350
; Author: King Dave
; Date: 2002-06-22 12:56:06
; Title: BevelRect
; Description: Beveled version of the Rect command

;BevelRect(x,y,width,height,radius,fill)
;
;	x - x position
;	y - y position
;	width - width in pixels from x
;	height - height in pixels from y
;	radius - radius of circles at corners (defaults to 15)
;	fill - fill the center (defaults to true)

Function BevelRect(x,y,w,h,r=15,f=1)
	Local ro,go,bo,img,buf,xo,yo
	If Not f
		ro=ColorRed()
		go=ColorGreen()
		bo=ColorBlue()
		img=CreateImage(w,h)
		buf=GraphicsBuffer()
		SetBuffer ImageBuffer(img)
		xo=x
		yo=y
		x=0
		y=0
	EndIf
	Oval x,y,r,r,1
	Oval x+w-r,y,r,r,1
	Oval x,y+h-r,r,r,1
	Oval x+w-r,y+h-r,r,r,1
	Rect x+(r/2),y,w-r,h,1
	Rect x,y+(r/2),w,h-r,1
	If Not f
		Color 0,0,0
		x=x+1
		y=y+1
		w=w-2
		h=h-2
		Oval x,y,r,r,1
		Oval x+w-r,y,r,r,1
		Oval x,y+h-r,r,r,1
		Oval x+w-r,y+h-r,r,r,1
		Rect x+(r/2),y,w-r,h,1
		Rect x,y+(r/2),w,h-r,1
		Color ro,go,bo
		SetBuffer buf
		DrawImage img,xo,yo
		FreeImage img
	EndIf
End Function
