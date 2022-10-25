; ID: 3115
; Author: Pakz
; Date: 2014-03-29 12:28:07
; Title: Map Generator from roguebasin
; Description: From a description from roguebasin

Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Const mapwidth = 39
Const mapheight = 29
Const tilewidth = 16
Const tileheight = 16
Dim map(mapwidth,mapheight)

While KeyDown(1) = False
	Cls
	makemap()
	drawmap()
	Flip
	For i=0 To 1000
		If KeyDown(1) = True Then End
		Delay 1
	Next
Wend
End

Function drawmap()
	Color 255,255,255
	For y = 0 To 29
		For x = 0 To 39
			If map(x,y) = 1 Then
				Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
			End If
		Next
	Next
End Function

Function makemap()
	For y=0 To mapheight
		For x=0 To mapwidth
			map(x,y) = 0
		Next
	Next
	While ang < 359
		d = Rand(60,200)
		For i=0 To d
			x1 = Cos(ang) * i
			y1 = Sin(ang) * i
			x = mapwidth/2*tilewidth + x1
			y = mapheight/2*tileheight + y1
			x = x / tilewidth
			y = y / tileheight
			drawbrush(x,y)
		Next
		ang = ang + Rand(1,50)
	Wend
End Function

Function drawbrush(x,y)
	x1 = x
	y1 = y - 1
	map(x1,y1) = 1
	x1 = x - 1
	y1 = y
	map(x1,y1) = 1
	x1 = x + 1
	y1 = y
	map(x1,y1) = 1
	x1 = x
	y1 = y + 1
	map(x1,y1) = 1
End Function
