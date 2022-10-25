; ID: 3101
; Author: Pakz
; Date: 2014-01-25 21:43:56
; Title: 2d scrolling Platformer
; Description: Simple scrolling platformer

; 2d Platformer with scrolling by pakz
;

Graphics 640,480,32,2
SetBuffer BackBuffer()

Const mapwidth = 39
Const mapheight = 29
Const cellwidth = 32
Const cellheight = 32
Global mapx = 0
Global mapy = 0
Global mapsx = 0
Global mapsy = 0

Dim map(mapwidth,mapheight)

Global playerx = 2*cellwidth
Global playery = 2*cellheight
Global playerwidth = 32
Global playerheight = 32
Global playerfalling = False
Global playerjumping = False
Global pjs# = 0.0 ; player jumping speed
Global pfs# = 0.0 ; player falling speed

readlevel()

While KeyDown(1) = False
	Cls
	For i=0 To 5
		moveplayer()
		playergravity()
		movemap()
	Next
	drawlevel()
	drawplayer()
	Flip
Wend
End

Function playergravity()
	If playerjumping = False And playerfalling = False
		If pmapcollision(playerx,playery+1) = False
			playerfalling = True
		End If
	End If
	If playerfalling = True
		If pfs < 3 Then pfs = pfs + 0.1
		For i=0 To pfs
			If pmapcollision(playerx,playery+1) = False Then
				playery = playery + 1
				Else
				playerfalling = False
				Exit
			End If
		Next		
	End If
	If playerjumping = True
		pjs = pjs - 0.1
		If pjs < 0 
			playerjumping = False
			playerfalling = True
			pfs = 0.0
		End If
		For i=0 To pjs
			If pmapcollision(playerx,playery-1) = False Then
				playery = playery - 1				
			Else
				playerjumping = False
				playerfalling = True
				pfs = 0.0				
				Exit
			End If
		Next
	End If
End Function

Function pmapcollision(px,py)
	Local pcx = mapx + (px / cellwidth)
	Local pcy = mapy + (py / cellheight)
	For y=-2 To 2
	For x=-2 To 2
		If pcx + x > 0 And pcx + x < mapwidth And pcy + y > 0 And pcy + y < mapheight
			If map(pcx+x,pcy+y) = 1
				xx = ((pcx-mapx)+x)*cellwidth+mapsx-cellwidth
				yy = ((pcy-mapy)+y)*cellheight+mapsy-cellheight
				If RectsOverlap(px,py,playerwidth,playerheight,xx,yy,cellwidth,cellheight)
					Return True
				End If
			End If
		End If
	Next
	Next
End Function

Function movemap()
	If playerx > GraphicsWidth() / 2
		If mapx < mapwidth - GraphicsWidth() / cellwidth
			mapsx = mapsx - 1
			playerx = playerx - 1
			If mapsx < 0
				mapsx = cellwidth
				mapx = mapx + 1
			End If
		End If
	End If
	If playerx < GraphicsWidth() / 2
		If mapx > 0
			mapsx = mapsx + 1
			playerx = playerx + 1
			If mapsx > cellwidth
				mapsx = 0
				mapx = mapx - 1
			End If
		End If
	End If
	If playery > GraphicsHeight() / 2
		If mapy < mapheight - GraphicsHeight() / cellheight
			If pmapcollision(playerx,playery-1) = False
			mapsy = mapsy - 1
			playery = playery - 1
			If mapsy < 0
				mapsy = cellheight
				mapy = mapy + 1
			End If
			End If
		End If	
	End If
	If playery < GraphicsHeight() / 2
		If mapy > 0
			If pmapcollision(playerx,playery+1) = False
			mapsy = mapsy + 1
			playery = playery + 1
			If mapsy > cellheight
				mapsy = 0
				mapy = mapy - 1
			End If
			End If
		End If
	End If
	
End Function

Function moveplayer()
	Local px = playerx
	Local py = playery
	If KeyDown(203) And pmapcollision(px-1,py) = False ; left
		px = px - 1
	End If
	If KeyDown(205) And pmapcollision(px+1,py) = False ; right
		px = px + 1
	End If
	If KeyDown(57) And playerjumping = False And playerfalling = False
		pjs = 4.0
		playerjumping =  True
	End If
	playerx = px
	playery = py
End Function

Function drawplayer()
	Color 0,0,255
	Rect playerx,playery,playerwidth,playerheight,True
End Function

Function drawlevel()
	Color 255,255,255
	For y=0 To GraphicsHeight() / cellheight
	For x=0 To GraphicsWidth() / cellwidth
		Select map(x+mapx,y+mapy)
			Case 1
				Rect x*cellwidth-cellwidth+mapsx,y*cellheight-cellheight+mapsy,cellwidth,cellheight,True
		End Select
	Next
	Next
End Function

Function readlevel()
	For y=0 To mapheight
	For x=0 To mapwidth
		Read a
		Select a
		Case 1
		map(x,y) = a
		End Select
	Next
	Next
End Function

.leveldata
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0
Data 0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,1,0,0,0,0,0,1,1,1,1,1,0,0,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,0,0,0,1,0
Data 0,1,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1,1,1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,1,0
Data 0,1,1,1,1,1,1,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0
Data 0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0
Data 0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0
Data 0,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,1,1,1,0
Data 0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0
Data 0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0
Data 0,1,0,0,0,0,1,1,1,1,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0
Data 0,1,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
