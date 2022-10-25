; ID: 3117
; Author: Pakz
; Date: 2014-04-02 06:49:38
; Title: Simple land generator
; Description: Land generator using rand()

; simple land generator using rand()
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
	For i=0 To 500
		If KeyDown(1) = True Then End
		Delay 1
	Next
Wend	
WaitKey
End

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) < 5
			Color 0,0,200
		End If
		If map(x,y)>5 And map(x,y)<8
			Color 0,150,0
		End If
		If map(x,y)>8 And map(x,y)<10
			Color 0,255,0
		End If
		If map(x,y) > 10 
			Color 200,200,200
		End If
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
	Next
	Next
End Function

Function makemap()
	For y=0 To mapheight
	For x=0 To mapwidth
		map(x,y) = 0
	Next
	Next
	For i=0 To 50 ; number of draws on random locations
		x = Rand(mapwidth)
		y = Rand(mapheight)
		For y1=-4 To 4
		For x1=-4 To 4
			x2 = x+ x1
			y2 = y+ y1
			If x2=>0 And y2=> 0 And x2=<mapwidth And y2=<mapheight
				map(x2,y2) = map(x2,y2) + 1
			End If
		Next
		Next
		For y1=-2 To 2
		For x1=-2 To 2
			x2 = x+ x1
			y2 = y+ y1
			If x2=>0 And y2=> 0 And x2=<mapwidth And y2=<mapheight
				map(x2,y2) = map(x2,y2) + 1
			End If
		Next
		Next
		For y1=-1 To 1
		For x1=-1 To 1
			x2 = x+ x1
			y2 = y+ y1
			If x2=>0 And y2=> 0 And x2=<mapwidth And y2=<mapheight
				map(x2,y2) = map(x2,y2) + 1
			End If
		Next
		Next

	Next
	For y=0 To mapheight
	For x=0 To mapwidth
	DebugLog map(x,y)
	Next
	Next
End Function
