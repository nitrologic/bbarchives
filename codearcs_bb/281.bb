; ID: 281
; Author: Warpy
; Date: 2002-03-26 04:52:20
; Title: Fast Grid Draw
; Description: draw a grid quickly

;Drawgrid function!
;I don't do comments :)
Function drawgrid(minx,maxx,miny,maxy,width,height,scrollx,scrolly)
	x=minx+(scrollx Mod width)
	While x<maxx
		Line x,miny,x,maxy
		x=x+width
	Wend
	y=miny+(scrolly Mod height)
	While y<maxy
		Line minx,y,maxx,y
		y=y+height
	Wend
End Function

;example usage
Graphics 640,480,16,2
SetBuffer BackBuffer()

While Not KeyHit(1)
	mxs=MouseXSpeed()
	mys=MouseYSpeed()
	If MouseDown(1)
		scrollx=scrollx+mxs
		scrolly=scrolly+mys
	EndIf
	Color 150,150,150
	drawgrid(0,640,0,480,10,10,scrollx,scrolly)
	Color 255,255,255
	oldms=ms
	ms=MilliSecs()
	Text 0,0,1000/(ms-oldms)
	Flip
	Cls
Wend
