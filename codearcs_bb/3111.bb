; ID: 3111
; Author: Pakz
; Date: 2014-02-18 12:33:55
; Title: scrolling platformer with movable blocks
; Description: 2d scrolling platformer with pushable and pullable blocks

;
; Platformer with movable blocks
; By Pakz / Rudy van Etten

Graphics 640,480,32,2
SetBuffer BackBuffer()

Const mapwidth = 39
Const mapheight = 29
Const tilewidth = 32
Const tileheight = 32

Dim map(mapwidth,mapheight)

Global mapx = 0
Global mapy = 0
Global mapsx = 0
Global mapsy = 0
Global maprealx = 0
Global maprealy = 0

Global playerx = 2*tilewidth
Global playery = 10*tileheight
Const playerwidth = tilewidth
Const playerheight = tileheight

Global playerjumping = False
Global playerfalling = False
Global pfs# = 0.0 ; player falling speed
Global pjs# = 0.0 ; player jumping speed

; Movable blocks
Const mbwidth = tilewidth
Const mbheight = tileheight

Type mb
	Field num,x,y,isfalling
	Field fs# ; falling speed of block
End Type

readleveldata()

While KeyDown(1) = False
	Cls
	For i=0 To 5
		moveplayer()
		movemap()
	Next
	For i=0 To 1
		playergravity()
		blockgravity()
	Next
	drawmap()
	drawmb()
	drawplayer()
	Flip
Wend
End

Function blockgravity()
	For this.mb = Each mb
		If this\isfalling= False
			If mbmapcol(this\num,0,1) = False And mbmbcol(this\num,0,1) = False
				this\isfalling = True
				this\fs=0
			End If
		End If
		If this\isfalling = True
			If this\fs < 3 Then this\fs = this\fs + .1
			For i=0 To this\fs
				If mbmapcol(this\num,0,1) = False And mbmbcol(this\num,0,1) = False
					this\y = this\y + 1
				Else
					this\fs = 0
					this\isfalling = False
					Exit
				End If
				
			Next
		End If
	Next
End Function

Function mbmapcol(num,movex,movey)
	For this.mb = Each mb
		If num = this\num
			pcx = this\x / tilewidth
			pcy = this\y / tileheight
			For y=-2 To 2
			For x=-2 To 2
				If pcx+x > 0 And pcx+x < mapwidth And pcy+y > 0 And pcy+y < mapheight
					If map(pcx+x,pcy+y) = 1
					If RectsOverlap(this\x+movex,this\y+movey,tilewidth,tileheight,(pcx+x)*tilewidth,(pcy+y)*tileheight,tilewidth,tileheight)
						Return True
					End If
					End If
				End If
			Next
			Next
		End If
	Next
End Function

Function mbmbcol(num,movex,movey)
	For this.mb = Each mb
		If this\num = num
			For that.mb = Each mb
				If Not that\num = num
					If RectsOverlap(this\x+movex,this\y+movey,tilewidth,tileheight,that\x,that\y,tilewidth,tileheight)
						Return True
					End If
				End If
			Next
		End If
	Next	
End Function

Function moveblock(num,move)
	For this.mb = Each mb
		If this\num = num
			this\x = this\x+move
		End If
	Next
End Function

Function playermbcol(px,py)
	For this.mb = Each mb
		xx = this\x-(mapx*tilewidth)+mapsx-tilewidth
		yy = this\y-(mapy*tileheight)+mapsy-tileheight
		If RectsOverlap(px,py,playerwidth,playerheight,xx,yy,tilewidth,tileheight)
			Return this\num
		End If
	Next
End Function

Function drawmb()
	Color 255,255,0 ; yellow
	For this.mb = Each mb
		Rect this\x-(mapx*tilewidth)+mapsx-tilewidth,this\y-(mapy*tileheight)+mapsy-tileheight,tilewidth,tileheight,True
	Next
End Function

Function playergravity()
	If playerfalling = False And playerjumping = False
		If pmapcollision(playerx,playery+1) = False And playermbcol(playerx,playery+1) = False
			playerfalling = True
			pfs = 0.0
		End If
	End If
	If playerfalling = True
		If pfs < 3 Then pfs = pfs + 0.1
		For i=0 To pfs
			If pmapcollision(playerx,playery+1) = False And playermbcol(playerx,playery+1) = False
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
			If pmapcollision(playerx,playery-1) = False
				playery = playery - 1
			Else
				playerfalling = True
				pfs = 0.0
				Exit
			End If
		Next
	End If
End Function

Function pmapcollision(px,py)
	Local pcx = mapx + (px/tilewidth)
	Local pcy = mapy + (py/tileheight)
	For y=-2 To 2
	For x=-2 To 2
		If pcx+x > 0 And pcx + x < mapwidth And pcy + y > 0 And pcy + y < mapheight
			If map(pcx+x,pcy+y) = 1
				xx = ((pcx-mapx)+x)*tilewidth+mapsx-tilewidth
				yy = ((pcy-mapy)+y)*tileheight+mapsy-tileheight
				If RectsOverlap(px,py,playerwidth,playerheight,xx,yy,tilewidth,tileheight)
					Return True
				End If
			End If
		End If
	Next
	Next
End Function

Function movemap()
	If playerx > GraphicsWidth() / 2
		If mapx < mapwidth - GraphicsWidth() / tilewidth
			mapsx = mapsx - 1
			playerx = playerx - 1
			If mapsx < 0
				mapsx = tilewidth-1
				mapx = mapx + 1
			End If
		End If
	End If
	If playerx < GraphicsWidth() / 2
		If mapx > 0
			mapsx = mapsx + 1
			playerx = playerx + 1
			If mapsx > tilewidth-1
				mapsx = 0
				mapx = mapx - 1
			End If
		End If
	End If
	If playery > GraphicsHeight() / 2
		If mapy < mapheight - GraphicsHeight() / tileheight
			mapsy = mapsy - 1
			playery = playery - 1
			If mapsy < 0
				mapsy = tileheight-1
				mapy = mapy + 1
			End If
		End If
	End If
	If playery < GraphicsHeight() / 2
		If mapy > 0
			mapsy = mapsy + 1
			playery = playery + 1
			If mapsy > tileheight-1
				mapsy = 0
				mapy = mapy - 1
			End If
		End If
	End If
End Function

Function moveplayer();42=shiftleft
	If KeyDown(42) ; if pulling block shift plus cursur l and r
		If KeyDown(203) And pmapcollision(playerx-1,playery) = False
			a = playermbcol(playerx+1,playery)
			If a>0
				playerx = playerx - 1
				moveblock(a,-1)
			End If
		End If
		If KeyDown(205) And pmapcollision(playerx+1,playery) = False
			a = playermbcol(playerx-1,playery)
			If a>0
				playerx = playerx + 1
				moveblock(a,1)
			End If
		End If
	End If
	If KeyDown(42) = False ; if not pulling
		If KeyDown(203) And pmapcollision(playerx-1,playery) = False ; left
			mp = -1
			a = playermbcol(playerx-1,playery)
			If a>0
				If mbmbcol(a,-1,0) = False And mbmapcol(a,-1,0) = False
						moveblock(a,-1)
					Else
					mp = 0
				End If
			End If
			playerx = playerx + mp
		End If
		If KeyDown(205) And pmapcollision(playerx+1,playery) = False ; right
			mp = 1
			a = playermbcol(playerx+1,playery)
			If a>0
				If mbmbcol(a,1,0) = False And mbmapcol(a,1,0) = False
					moveblock(a,1)
					Else
					mp = 0
				End If
			End If
			playerx = playerx + mp
		End If
	End If
	If KeyDown(57)
		If playerjumping = False And playerfalling = False
			playerjumping = True
			pjs = 5.0
		End If
	End If
End Function

Function drawplayer()
	Color 255,0,0
	Rect playerx,playery,playerwidth,playerheight,True
End Function

Function drawmap()
	Color 255,255,255
	For y=0 To GraphicsHeight() / tileheight
	For x=0 To GraphicsWidth() / tilewidth
		Select map(x+mapx,y+mapy)
			Case 1
			Rect x*tilewidth+mapsx-tilewidth,y*tileheight+mapsy-tileheight,tilewidth,tileheight,True
		End Select
	Next
	Next
End Function

Function readleveldata()
	Local blocknum = 1
	Restore leveldata
	For y=0 To mapheight
	For x=0 To mapwidth
		Read a
		If a=1
			map(x,y) = 1
		End If
		If a=2
			this.mb = New mb
			this\num = blocknum
			blocknum=blocknum+1
			this\x = x*tilewidth
			this\y = y*tileheight
			
		End If
	Next
	Next
End Function

.leveldata
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,2,0,1,0,2,0,0,1,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,0,0,2,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,1,1,1,1,1,1,0
Data 0,1,0,0,0,0,0,0,0,0,2,2,2,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
