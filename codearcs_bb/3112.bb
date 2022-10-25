; ID: 3112
; Author: Pakz
; Date: 2014-03-03 10:18:10
; Title: 2d Level Generator
; Description: Uses Random walk method to create levels.

Graphics 640,480,32,2
SetBuffer BackBuffer()

Global mapwidth = 39
Global mapheight = 29
Dim map(mapwidth,mapheight)

SeedRnd MilliSecs()

While exitloop = False
	makemap()
	Cls
	drawmap()
	Color 255,255,255
	Rect 640-120,0,119,479,True
	Flip
	For i = 0 To 200
		Delay 1
		If KeyDown(1) = True Then exitloop = True
	Next
Wend
End

Function makemap(steps = 100)
	Local aproved = False
	While aproved = False
		For y = 0 To mapheight
		For x = 0 To mapwidth
			map(x,y) = 0
		Next
		Next
		x = mapwidth / 2
		y = mapheight / 2
		steps = Rand(500) + 500
		For i=0 To steps
			nstepf = False
			While nstepf = False
				dir = Rand(8)
				Select dir
					Case 1 : nx = x - 1 : ny = y - 1
					Case 2 : ny = y - 1
					Case 3 : nx = x + 1 : ny = y - 1
 					Case 4 : nx = x - 1
					Case 5 : nx = x + 1
					Case 6 : nx = x - 1 : ny = y + 1
					Case 7 : ny = y + 1
					Case 8 : nx = x + 1 : ny = y + 1
				End Select
					If nx < mapwidth And nx > 0 And ny < mapheight And ny > 0 Then
						x = nx
						y = ny
						nstepf = True
					End If
			Wend
			drawbrush(x,y) 
		Next
		aproved = True
		For y=0 To mapheight
			If map(0,y) = 1 Then aproved = False
		Next
		For y=0 To mapheight
			If map(mapwidth,y) = 1 Then aproved = False
		Next
		For x=0 To mapwidth
			If map(x,0) = 1 Then aproved = False
		Next
		For x=0 To mapwidth
			If map(x,mapheight) = 1 Then aproved = False
		Next
		For y=0 To mapheight
			For x=mapwidth-7 To mapwidth
				If map(x,y) = 1 Then aproved = False
			Next
		Next
		hasone = False
		For y=0 To mapheight
			If map(mapwidth-8,y) = 1 Then hasone = True
		Next
		If hasone = False Then aproved = False
		hasone = False
		For y=0 To mapheight
			If map(3,y) = 1 Then hasone = True
		Next
		If hasone = False Then aproved = False
		hasone = False
		For x=0 To mapwidth
			If map(x,3) = 1 Then hasone = True
		Next
		If hasone = False Then aproved = False
		hasone = False
		For x=0 To mapwidth
			If map(x,mapheight-3) = 1 Then hasone = True
		Next
		If hasone = False Then aproved = False
	Wend
End Function 

Function drawbrush(x,y)
	x1 = x - 1
	y1 = y - 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x
	y1 = y - 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x + 1
	y1 = y - 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x - 1
	y1 = y 
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x + 1
	y1 = y
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x - 1
	y1 = y + 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x 
	y1 = y + 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x + 1
	y1 = y + 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
End Function

Function drawmap()
	Color 255,255,255
	For y = 0 To mapheight
		For x = 0 To mapwidth
			If map(x,y) = 1 Then
				Rect x*16,y*16,16,16,True
			End If
		Next
	Next
End Function
