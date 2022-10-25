; ID: 3146
; Author: Pakz
; Date: 2014-10-12 20:17:28
; Title: Top down scrolling with ai
; Description: Map with simple obstacle avoidant roaming ai

; Topdown movement scrolling map with simple ai by Rudy van Etten
;
; ai will find new destination if stuck to long in one area

Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()
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

Type ai
	Field x,y
	; target x and y
	Field tx,ty
	; delay
	Field dl
	; millisecs
	Field ms
	; stuck in area
	Field x1,y1,w1,h1
	Field stuckintime
End Type

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

createai(100)

; Main game loop
Global mytimer = CreateTimer(15)
While KeyDown(1) = False
	WaitTimer mytimer
	Cls
	doai()
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

Function doai()
	For this.ai = Each ai
		; where to next
		If this\ms < MilliSecs()
		If this\x = this\tx And this\y = this\ty Then
			sel = False
			While sel = False
				x = Rand(3,mapwidth-6)
				y = Rand(3,mapheight-6)
				If map(x,y) = 0
					this\tx = x
					this\ty = y
					sel = True
				End If
			Wend
		End If
		;
		If this\tx < this\x Then nx = this\x - 1
		If this\tx > this\x Then nx = this\x + 1
		If this\ty < this\y Then ny = this\y - 1
		If this\ty > this\y Then ny = this\y + 1
		If map(nx,ny) = 0
			map(this\x,this\y) = 0
			map(nx,ny) = 3
			this\x = nx
			this\y = ny
		End If
		If map(nx,ny) = 1 Or map(nx,ny) = 2
		; if blocked, move to a random block
			sel = False
			While sel = False
				For y=-1 To 1
				For x=-1 To 1
					If Rand(10) = 1 Then 
						If map(this\x+x,this\y+y) = 0
							sel = True
						End If
					End If
				Next
				Next
				If KeyDown(1) = True Then End
			Wend
			map(this\x,this\y) = 0
			map(this\x+x,this\y+y) = 3
			this\x = this\x+x
			this\y = this\y+y
		End If
		this\ms = MilliSecs() + this\dl
		; ai stuck in area fix
		If this\stuckintime < MilliSecs()
			If RectsOverlap(this\x1-3,this\y1-3,12,12,this\x1,this\y1,this\w1,this\h1) = True Then
				this\tx = Rand(3,mapwidth-6)
				this\ty = Rand(3,mapheight-6)
			End If
			this\x1 = this\x - 3
			this\y1 = this\y - 3
			this\stuckintime = MilliSecs()+Rand(2000,5000)
		End If
		;
		End If
	Next
End Function

Function createai(num)
	For i=0 To num
		this.ai = New ai
		this\x = Rand(3,mapwidth-6)
		this\y = Rand(3,mapheight-6)
		map(this\x,this\y) = 3
		this\tx = Rand(3,mapwidth-6)
		this\ty = Rand(3,mapheight-6)
		this\dl = Rand(10,1000)
		this\ms = MilliSecs()
		this\x1 = this\x - 3
		this\y1 = this\y - 3
		this\w1 = 6
		this\h1 = 6
		this\stuckintime = MilliSecs()+Rand(2000,5000)
	Next
End Function

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
			If map(tx,ty) =3 
				Color 255,0,255
				Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
			End If	
		End If
	Next
	Next
End Function
