; ID: 3159
; Author: zoqfotpik
; Date: 2014-11-12 17:53:08
; Title: 2D Lichen
; Description: Generates Aggregate Lichen

' Aggregate Plants

Graphics 1024,600
Global WIDTH=1024
Global HEIGHT=600
Global map:Int[1024,600]
SetClsColor 75,75,75
#loop
maxheight = 598
Cls
While Not KeyDown(KEY_ESCAPE) And maxheight > 50
	For i = 1 To 1000
	x = Rand(1022)
	y = maxheight-2
	While map[x,y+1]=0 And map[x-1,y+1]=0 And map [x+1,y+1]=0 And y < HEIGHT-2
		y = y + 1
	Wend
	map[x,y]=1
	color = Rand(3)
	Select color
	Case 1
		SetColor 255,0,0
	Case 2
		SetColor 255,255,0
	Case 3 
		SetColor 255,102,0
	End Select
	Plot x,y
	SetColor 25,25,25
	Plot x+2,y+2
	If y < maxheight maxheight = y
	Next
	Flip
	If KeyDown(KEY_ESCAPE) End
Wend
time = MilliSecs()
While MilliSecs() < (time + 1000)
Wend
If KeyDown(KEY_ESCAPE) End
For i = 0 To 1023
For j = 0 To 599
map[i,j]=0
Next
Next
Goto loop
