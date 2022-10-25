; ID: 3133
; Author: Pakz
; Date: 2014-06-16 20:05:44
; Title: Scrolling Platformer
; Description: 2d scrolling platformer with enemies and shooting.

; Scrolling platformer with enemies and shooting (c key) by Rudy van Etten
;

Graphics 640,480,32,2
SetBuffer BackBuffer()

Const tilewidth = 32
Const tileheight = 32

Const mapwidth = 39
Const mapheight = 29

Dim map(mapwidth,mapheight)

Global mapx = 0
Global mapy = 0
Global mapsx = 0
Global mapsy = 0

Global firedelay
Global playerx = 4*32
Global playery = 2*32
Global playerdirection
Const playerwidth = 48
Const playerheight = 32
Global pfalling = False
Global pjumping = False
Global pjs# = 0.0
Global pfs# = 0.0

Const enemywidth = 32
Const enemyheight = 32

Type enemy
	Field x#,y#
	Field mx#
	Field enemydirection
	Field deleteme
End Type

Const bulletwidth = 8
Const bulletheight = 8

Type bullet
	Field x#,y#,mx#
	Field bulletdirection
	Field deleteme
	Field timeout
End Type


readlevel()

While KeyDown(1) = False
	Cls
	For i=0 To 2
		moveplayer()
		movemap()
		playergravity()
	Next
	updateenemies()
	updatebullets()
	playerbulletcollision()
	drawlevel()
	drawenemies()
	drawbullets()
	drawplayer()
	Flip
Wend
End

Function playerbulletcollision()
	For this.bullet = Each bullet
	For that.enemy = Each enemy
		If RectsOverlap(this\x,this\y,bulletwidth,bulletheight,that\x,that\y,enemywidth,enemyheight) = True Then
			this\deleteme = True
			that\deleteme = True
		End If
	Next
	Next
End Function

Function updatebullets()
	Local tx#,ty#
	For this.bullet = Each bullet
		tx = this\x
		ty = this\y
		tx=tx+this\mx
		this\timeout = this\timeout + 1
		If this\timeout > 300
			this\deleteme = True
		End If
		If bmapcollision(tx-32,ty-32) = True Then
			this\deleteme = True
		End If
		this\x = tx
		this\y = ty
	Next
	For that.bullet = Each bullet
		If that\deleteme = True Then
			Delete that
		End If
	Next
End Function

Function drawbullets()
	For this.bullet = Each bullet
		Color 0,0,255
		Oval this\x-(mapx*tilewidth)+mapsx-tilewidth,this\y-(mapy*tileheight)+mapsy-tileheight,bulletwidth,bulletheight,True
	Next
End Function

Function updateenemies()
	Local tx,ty,goahead
	For this.enemy = Each enemy
		tx = this\x
		ty = this\y
		If this\enemydirection = -1 Then
			tx=tx - 1
			Else
			tx=tx + 1
		End If
		goahead = True
		Select this\enemydirection
			Case -1
				If emapcollision(tx-32,ty-32) = True Or emapcollision(tx-64,ty) = False Then
					this\enemydirection = 1		
					goahead = False
				End If
			Case 1
				If emapcollision(tx-32,ty-32) = True Or emapcollision(tx,ty) = False Then
					this\enemydirection = -1
					goahead = False
				End If
		End Select
		If goahead = True Then
			this\x = tx
			this\y = ty
		End If
	Next
	For that.enemy = Each enemy
		If that\deleteme = True Then
			Delete that
		End If
	Next
End Function

Function drawenemies()
	For this.enemy = Each enemy
		Color 255,255,0
		Rect this\x-(mapx*tilewidth)+mapsx-tilewidth,this\y-(mapy*tileheight)+mapsy-tileheight,enemywidth,enemyheight,True
	Next
End Function

Function playergravity()
	If pmapcollision(playerx,playery+1) = False
	If pfalling = False And pjumping = False
		pfalling = True
		pfs = 0.0
	End If
	End If
	If pfalling = True Then
		If pfs < 2
			pfs = pfs + 0.1
		End If
		For i=0 To pfs
			If pmapcollision(playerx,playery+1) = True
				pfalling = False
				pfs = 0.0
			Else
				playery = playery + 1
			End If
		Next
	End If
	If pjumping = True Then
		pjs = pjs - .1
		If pjs < 0 Then
			pjumping = False
			pfalling = True
			pfs = 0.0
		End If
		For i=0 To pjs
			If pmapcollision(playerx,playery-1) = True
				pjumping = False
				pfalling = True
				pfs = 0.0
				Exit
			Else
				playery = playery - 1
			End If
		Next
	End If
End Function

Function bmapcollision(px,py)
	Local pcx = px / tilewidth
	Local pcy = py / tileheight
	
	For y=-2 To 3
	For x=-2 To 3
		If pcx + x > 0 And pcx+x <mapwidth And pcy + y > 0 And pcy+y<mapheight
			If map(pcx+x,pcy+y) = 1
				xx = ((pcx)+x)*tilewidth-tilewidth
				yy = ((pcy)+y)*tileheight-tileheight
				If RectsOverlap(px,py,bulletwidth,bulletheight,xx,yy,tilewidth,tileheight) = True
					Return True
				End If
			End If
		End If
	Next
	Next	
End Function


Function emapcollision(px,py)
	Local pcx = px / tilewidth
	Local pcy = py / tileheight
	
	For y=-2 To 3
	For x=-2 To 3
		If pcx + x > 0 And pcx+x <mapwidth And pcy + y > 0 And pcy+y<mapheight
			If map(pcx+x,pcy+y) = 1
				xx = ((pcx)+x)*tilewidth-tilewidth
				yy = ((pcy)+y)*tileheight-tileheight
				If RectsOverlap(px,py,enemywidth,enemyheight,xx,yy,tilewidth,tileheight) = True
					Return True
				End If
			End If
		End If
	Next
	Next	
End Function

Function pmapcollision(px,py)
	Local pcx = mapx + (px / tilewidth)
	Local pcy = mapy + (py / tileheight)
	
	For y=-2 To 3
	For x=-2 To 3
		If pcx + x > 0 And pcx+x <mapwidth And pcy + y > 0 And pcy+y<mapheight
			If map(pcx+x,pcy+y) = 1
				xx = ((pcx-mapx)+x)*tilewidth+mapsx-tilewidth
				yy = ((pcy-mapy)+y)*tileheight+mapsy-tileheight
				If RectsOverlap(px,py,playerwidth,playerheight,xx,yy,tilewidth,tileheight) = True
					Return True
				End If
			End If
		End If
	Next
	Next	
End Function

Function movemap()
	If playerx > GraphicsWidth()/2
		If mapx < (mapwidth - GraphicsWidth() / tilewidth)
			mapsx = mapsx - 1
			playerx= playerx - 1
			If mapsx < 0 Then
				mapsx = tilewidth
				mapx = mapx + 1
			End If
		End If
	End If
	If playerx < GraphicsWidth()/2
		If mapx > 0
			mapsx = mapsx + 1
			playerx = playerx + 1
			If mapsx > tilewidth
				mapsx = 0
				mapx = mapx - 1
			End If
		End If
	End If
	If playery > GraphicsHeight() / 2
		If mapy < (mapheight - GraphicsHeight() / tileheight)
		If pmapcollision(playerx,playery-1) = False
			mapsy = mapsy - 1
			playery = playery - 1
			If mapsy < 0
				mapsy = tileheight
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
			If mapsy > tileheight
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
	If KeyDown(203) ; left
		If pmapcollision(px-1,py) = False
			px = px - 1
		End If
		playerdirection = -1
	End If
	If KeyDown(205) ; right
		If pmapcollision(px+1,py) = False 
			px = px + 1
		End If
		playerdirection = 1
	End If
	If KeyDown(57) ; space
		If pjumping = False And pfalling = False
			pjumping = True
			pjs = 4.0
		End If
	End If
	If KeyDown(46) ; c fire key
		If firedelay < MilliSecs()
			this.bullet = New bullet
			Select playerdirection
				Case -1
					this\x = playerx+tilewidth-8+(mapx*tilewidth)
					this\y = playery+(mapy*tileheight)-mapsy+tileheight
					this\mx = -1
					this\bulletdirection = -1
				Case 1
					this\x = playerx + playerwidth+tilewidth+(mapx*tilewidth)
					this\y = playery+(mapy*tileheight)-mapsy+tileheight
					this\mx = 1
					this\bulletdirection = 1
			End Select
	 		firedelay = MilliSecs()+500
		End If
	End If
	
	playerx = px
	playery = py
End Function

Function drawplayer()
	Color 255,0,0
	Rect playerx,playery,playerwidth,playerheight,True
End Function

Function drawlevel()
	For y=0 To GraphicsHeight() / tileheight
	For x=0 To GraphicsWidth() / tilewidth
		If map(x+mapx,y+mapy) = 1 Then
			Color 255,255,255
			Rect x*tilewidth+mapsx-tilewidth,y*tileheight+mapsy-tileheight,32,32,True
		End If
	Next
	Next
End Function

Function readlevel()
	For y=0 To mapheight
	For x=0 To mapwidth
		Read a
		Select a
			Case 1
			map(x,y) = 1
			Case 2
				this.enemy = New enemy
				this\x = x*tilewidth+tilewidth
				this\y = y*tileheight
				this\enemydirection = -1
				this\mx = 0.1
		End Select	
	Next
	Next
End Function

.leveldata
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,1,1,1,1,1,1,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,2,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,1,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,2,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 1,1,0,0,2,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,1,1,1,1,1,0,0,1,0
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
