; ID: 3136
; Author: Pakz
; Date: 2014-07-03 16:15:11
; Title: Filled Circle in 2 dimensional array
; Description: Code that shows how to draw filled ovals with math (function)

; Filled circle in 2 dimensional array
; By Rudy van Etten in 2014

Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()
Const mapwidth = 39
Const mapheight = 29
Const tilewidth = 16
Const tileheight = 16
Dim map(mapwidth,mapheight)

Type point
	Field x,y,radius,radmod
End Type

makepoint(3)

Repeat
	Cls
	If KeyDown(1) = True Then End
	Dim map(mapwidth,mapheight)
	For this.point= Each point
		mapcircle(this\x,this\y,this\radius)
		this\radius = this\radius + this\radmod
		If this\radius > 9 Then this\radmod = -1
		If this\radius < 2 Then this\radmod = 1
	Next
	drawmap()
	Flip
	For i=0 To 100
		Delay 1
		If KeyDown(1) = True Then End
	Next
Forever

End

Function mapcircle(x1,y1,radius)
	For y2=-radius To radius
	For x2=-radius To radius
		If (y2*y2+x2*x2) <= radius*radius+radius*0.8
			x3 = x1+x2
			y3 = y1+y2
			If x3>=0 And y3>=0 And x3<=mapwidth And y3<=mapheight
				map(x3,y3) = 1
			End If
		End If
	Next
	Next
End Function

Function makepoint(num)
	For i=0 To num
		this.point = New point
		this\x = Rand(mapwidth)
		this\y = Rand(mapheight)
		If Rand(0,1) = 1
			this\radmod = 1
		Else
			this\radmod = -1
		End If
		this\radius = Rand(1,9)
	Next
End Function

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) = 0
			Color 0,0,0
		ElseIf map(x,y) = 1
			Color 255,255,255
		End If
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
	Next
	Next
End Function
