; ID: 3145
; Author: Pakz
; Date: 2014-10-12 19:35:35
; Title: Topdown scrolling map
; Description: A big map where a player is moved example

; Topdown movement scrolling map by Rudy van Etten
;
;

Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd 1
AppTitle "Use cursor keys to move the map"
Global tilewidth = 8
Global tileheight =8
Global playerwidth = tilewidth
Global playerheight = tileheight
Global mapwidth = 512
Global mapheight = 512
Dim map(mapwidth,mapheight)
Global mapx = 256
Global mapy = 256
Global playerx = mapx + ((GraphicsWidth()/tilewidth)/2)
Global playery = mapy + ((GraphicsHeight()/tileheight)/2)

; Make lines with pasways that goes a little bit wavy
x = 0
y = 0
While y=<mapheight-5
	y=y+3
	While x=<mapwidth		
		If Rand(5) = 1 Then 
			If m1<2
				m1 = m1 + 1
				Else
				m1 = m1 -1
			End If
		End If
		If Rand(10) < 8
			map(x,y+m1) = 1
		End If
		x=x+1		
	Wend
	x=0
Wend

; Make open spaces
For i=0 To 200
	x1 = Rand(0,mapwidth)
	y1 = Rand(0,mapheight)
	rad = Rand(10,30)
	For y2 = -rad To rad
	For x2 = -rad To rad
		If (y2*y2+x2*x2) <= rad*rad+rad*0.8
			x3 = x1+x2
			y3 = y1+y2
			If Rand(10) < 8
			If x3=>0 And x3<=mapwidth And y3>=0 And y3<=mapheight
				map(x3,y3) = 0
			End If
			End If
		End If
	Next
	Next
Next

; Make the borders
For y=0 To mapheight
	map(0,y) = 1
	map(mapwidth,y) = 1
Next
For x=0 To mapwidth
	map(x,0) = 1
	map(x,mapheight) = 1
Next


; Make sure the player location is not a wall
map(playerx,playery) = 2

; Main game loop
Global mytimer = CreateTimer(15)
While KeyDown(1) = False
	WaitTimer mytimer
	Cls
	drawmap()
	; up
	If KeyDown(200) And map(playerx,playery-1) = 0 Then 
		map(playerx,playery) = 0
		playery = playery - 1
		mapy = mapy - 1
		map(playerx,playery) = 2
	End If
	; down
	If KeyDown(208) And map(playerx,playery+1) = 0 Then
		map(playerx,playery) = 0
		playery = playery + 1
		mapy = mapy + 1
		map(playerx,playery) = 2
	End If
	; left
	If KeyDown(203) And map(playerx-1,playery) = 0
		map(playerx,playery) = 0
		playerx = playerx - 1
		mapx = mapx - 1
		map(playerx,playery) = 2
	End If
	; right
	If KeyDown(205) And map(playerx+1,playery) = 0
		map(playerx,playery) = 0
		playerx = playerx + 1
		mapx = mapx + 1
		map(playerx,playery) = 2
	End If

	Flip
Wend
End

Function drawmap()
	For y=0 To GraphicsHeight()/tileheight
	For x=0 To GraphicsWidth()/tilewidth
		tx = mapx + x
		ty = mapy + y
		If tx => 0  And tx <= mapwidth And ty => 0 And ty <= mapheight
			If map(tx,ty) = 1
				Color 255,255,255
				Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
			End If
			If map(tx,ty) = 2
				Color 255,0,0
				Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
			End If
			
	
		End If
	Next
	Next
End Function
