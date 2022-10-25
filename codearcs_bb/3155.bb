; ID: 3155
; Author: Pakz
; Date: 2014-10-23 16:28:59
; Title: Topdown Space shooter Example
; Description: Has enemy turrets and ships. Very basic but playable

; Topdown Space Shooter Example by Pakz (Rudy van Etten)

; The map is drawn on a image.
; Every object needs to be moved with the player movement speed, this is done
; in the updatemap function.

Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Const mapwidth = 39
Const mapheight = 29
Dim map(mapwidth,mapheight)
Global mapimage = CreateImage(mapwidth*32+32,mapheight*32+32)
Global mapx# = 0
Global mapy# = 0

Global myfont = LoadFont("verdana.ttf",16)
SetFont myfont

; tx = the destination of the ship
Type ship
	Field x#,y#,mx#,my#,angle,tx,ty,shootdelaytime
	Field hull
End Type

Type sbullet ; ship bullets
	Field x#,y#,mx#,my#,dtraveled
End Type


Type turret ; the turrets
	Field x#,y#,angle,shootdelaytime,hull
End Type

Type tbullet ; turret bullets
	Field x#,y#,mx#,my#
	Field dtraveled ; Distance traveled by the bullet
End Type

Type player
	Field x#,y#,mx#,my#,angle,thrust#
	Field image[360],shootdelaytime
	Field score,hull
End Type

Type pbullet
	Field x#,y#,mx#,my#
	Field dtraveled
End Type

Global p.player = New player
p\x = GraphicsWidth()/2-16
p\y = GraphicsHeight()/2-16

initplayer()

restart()

timer = CreateTimer(60)
While KeyDown(1) = False
	WaitTimer timer
	Cls
	updateplayer()
	updateturrets()
	updateships()
	updatemap()
	updatepbullets()
	updatetbullets()
	updatesbullets()
	pbulletturretcollision()
	pbulletshipcollision()	
	tbulletplayercollision()
	sbulletplayercollision()
	drawmap()
	drawturrets()
	drawships()
	drawplayer()
	drawpbullets()
	drawtbullets()
	drawsbullets()
	Color 255,255,255
	Text GraphicsWidth()-100,5,"Hull:"+p\hull,1,1
	Text GraphicsWidth()-100,15,"Turrets left:"+turretsleft(),1,1
	Text GraphicsWidth()-100,25,"Ships left:"+shipsleft(),1,1
	If turretsleft() = 0 And shipsleft() = 0 Then restart()
	Flip
Wend
End

Function sbulletplayercollision()
	For this.sbullet = Each sbullet
		If RectsOverlap(this\x-3,this\y-3,6,6,p\x-16,p\y-16,32,32) Then 
			p\hull = p\hull - 1
			If p\hull = 0
				restart
				Return
			End If
			Delete this
		End If
	Next
End Function

Function updatesbullets()
	For this.sbullet = Each sbullet
		this\x = this\x + this\mx
		this\y = this\y + this\my
		this\dtraveled = this\dtraveled + 1
		If this\dtraveled > 640 Then Delete this
	Next
End Function

Function drawsbullets()
	For this.sbullet = Each sbullet
		Color 255,0,0
		Oval this\x-3,this\y-3,6,6,True
	Next
End Function

Function pbulletshipcollision()
	For this.ship = Each ship
	For that.pbullet = Each pbullet
		If RectsOverlap(this\x-16,this\y-16,32,32,that\x-3,that\y-3,6,6)
			Delete that
			this\hull = this\hull - 1
		End If
	Next
		If this\hull <= 0 Then Delete this
	Next
End Function

Function updateships()
	For this.ship = Each ship
		da = getangle(this\tx,this\ty,this\x,this\y)
		v1 = 0 ; left side 
		v2 = 0 ; right side
		ta = this\angle ; get the current angle into ta
		; Count to the left to get the steps to the target angle
		exitloop = False
		While exitloop = False
			ta = ta - 1
			v1 = v1 + 1				
			If RectsOverlap(ta,ta,4,4,da,da,4,4) Exitloop = True
			If ta < -180 Then ta = 181
		Wend
		ta = this\angle ;'get the current angle into ta
		; Count to the right to get the steps top the target angle
		exitloop = False
		While exitloop = False
			ta = ta + 1
			v2 = v2 + 1
			If RectsOverlap(ta,ta,4,4,da,da,4,4) exitloop = True
			If ta >= 180 Then ta = -181
		Wend
		;
		If v1 > v2 ; if the left side is closer to the target angle
			this\angle = this\angle - 3
		Else	; if the right side is closer to the target angle
			this\angle = this\angle + 3
		End If
		; move the missile
		this\x = this\x + Cos(this\angle) * 1
		this\y = this\y + Sin(this\angle) * 1
		
		If distance(this\x,this\y,p\x,p\y) < 96 Or Rand(0,200) = 1
			exitloop = False
			While exitloop = False
				x1 = Rand(-(GraphicsWidth()*2),GraphicsWidth()*2)
				y1 = Rand(-(GraphicsHeight()*2),GraphicsHeight()*2)
				If distance(p\x,p\y,x1,y1) > 320
					this\tx = x1
					this\ty = y1
					exitloop = True
				End If
			Wend
		End If		 
		If distance(this\x,this\y,p\x,p\y) > 300
			this\tx = p\x
			this\ty = p\y
		End If
		; Shoot at the player if he is nearby
		If distance(this\x,this\y,p\x,p\y) < 150
		If this\shootdelaytime < MilliSecs()
			this\shootdelaytime = MilliSecs() + 1000
			sb.sbullet = New sbullet
			ta = getangle(this\x,this\y,p\x,p\y)
			sb\x = this\x-16 + Cos(ta)*16
			sb\y = this\y-16 + Sin(ta)*16
			sb\mx = Cos(ta)*2
			sb\my = Sin(ta)*2
		End If
		End If
	Next
	
End Function

Function initships()
	For i=0 To 3
		this.ship = New ship
		this\x = Rand(-(GraphicsWidth()*2),GraphicsWidth()*2)
		this\y = Rand(-(GraphicsHeight()*2),GraphicsHeight()*2)
		this\tx = p\x+Rand(-64,64)
		this\ty = p\y+Rand(-64,64)
		this\angle = 0
		this\hull = 3
	Next
End Function

Function drawships()
	For this.ship = Each ship
		DrawImage p\image[0],this\x-16,this\y-16
	Next
End Function


Function shipsleft()
	For this.ship = Each ship
		cnt = cnt + 1
	Next
	Return cnt
End Function

Function turretsleft()
	For this.turret = Each turret
		cnt = cnt + 1
	Next
	Return cnt
End Function

Function tbulletplayercollision()
	For this.tbullet = Each tbullet
		If RectsOverlap(this\x-3,this\y-3,6,6,p\x-16,p\y-16,32,32)
			Delete this
			p\hull = p\hull - 1
			If p\hull < 1
				restart
				Return
			End If
		End If
	Next
End Function


Function restart()
	Delete Each pbullet
	Delete Each tbullet
	Delete Each sbullet
	Delete Each turret
	Delete Each ship
	p\hull = 10
	p\thrust = .1
	mapx = 0
	mapy = 0
	Cls
	Text GraphicsWidth()/2,GraphicsHeight()/2,"Get Ready",1,1
	Flip
	Delay 1000
	initmap()
	initships()
End Function

Function pbulletturretcollision()
	For this.turret = Each turret
	For that.pbullet = Each pbullet		
		If RectsOverlap(this\x+4,this\y+4,32-8,32-8,that\x-3,that\y-3,6,6) Then
			Delete that
			this\hull = this\hull - 1
		End If
	Next
	If this\hull < 0 Then Delete this
	Next
End Function

Function updateplayer()
	Local my# = 0
	Local mx# = 0
	If KeyDown(203)
		p\angle = p\angle - 1
		If p\angle < 0 Then p\angle = 360
	End If
	If KeyDown(205)
		p\angle = p\angle + 1
		If p\angle >= 360 Then p\angle = 0
	End If
	If KeyDown(200)
		mx = Cos(p\angle) * p\thrust
		my = Sin(p\angle) * p\thrust
		If mx < 0  
			If p\mx > -1 Then p\mx = p\mx + mx
		End If
		If mx > 0
			If p\mx < 1 Then p\mx = p\mx + mx
		End If
		If my < 0
			If p\my > -1 Then p\my = p\my + my
		End If
		If my > 0
			If p\my < 1 Then p\my = p\my + my
		End If
	End If 
	If KeyDown(57) And p\shootdelaytime < MilliSecs()
		pb.pbullet = New pbullet
		pb\x = GraphicsWidth()/2-16 + Cos(p\angle-180) * 16
		pb\y = GraphicsHeight()/2-16 + Sin(p\angle-180) * 16
		pb\mx = Cos(p\angle-180) * 4.5
		pb\my = Sin(p\angle-180) * 4.5
		p\shootdelaytime = MilliSecs() + 200
	End If
End Function

Function updateturrets()
	For this.turret = Each turret
		If distance(this\x,this\y,p\x,p\y) < 220 Then
		If this\shootdelaytime < MilliSecs()
			a = getangle(this\x,this\y,p\x,p\y)
			that.tbullet = New tbullet
			that\x = this\x + 16 + Cos(a) * 16
			that\y = this\y + 16 + Sin(a) * 16
			that\mx = Cos(a)*2
			that\my = Sin(a)*2
			this\shootdelaytime = MilliSecs()+1000
		End If
		End If
	Next
End Function

Function updatetbullets() ; update turret bullets
	For this.tbullet = Each tbullet
		this\dtraveled = this\dtraveled + 1
		this\x = this\x + this\mx
		this\y = this\y + this\my
		If this\dtraveled > 640 Then Delete this
	Next
End Function


Function drawtbullets() ; draw turret bullet
	For this.tbullet = Each tbullet
		Color 255,255,0
		Oval this\x-3,this\y-3,6,6,True
	Next
End Function

Function drawturrets()
	For this.turret = Each turret
		Color 0,0,255
		Oval this\x,this\y,32,32,True
	Next
End Function

Function updatepbullets()
	For this.pbullet = Each pbullet
		this\dtraveled = this\dtraveled + 1
		this\x = this\x + this\mx
		this\y = this\y + this\my
		If this\dtraveled > 640 Then Delete this
	Next
End Function

; draw player bullets
Function drawpbullets()	
	For this.pbullet = Each pbullet
		Color 200,0,0
		Oval this\x,this\y,6,6,True
		Color 255,255,255
		Oval this\x,this\y,6,6,False	
	Next
End Function

Function updatemap()
		mapx = mapx + p\mx
		mapy = mapy + p\my
		; Move every player bullet with the movement speed of the player
		For pb.pbullet = Each pbullet
			pb\x = pb\x + p\mx
			pb\y = pb\y + p\my
		Next
		; Move every turret 
		For t.turret = Each turret
			t\x = t\x + p\mx
			t\y = t\y + p\my
		Next
		; Move every turret bullet
		For tb.tbullet = Each tbullet
			tb\x = tb\x + p\mx
			tb\y = tb\y + p\my
		Next
		; Move every ship
		For s.ship = Each ship
			s\x = s\x + p\mx
			s\y = s\y + p\my
		Next
End Function

Function drawmap()
	DrawImage mapimage,mapx,mapy
End Function

Function initplayer()
	Local image = CreateImage(32,32)
	MidHandle image
	SetBuffer ImageBuffer(image)
	Color 255,255,255
	Rect 0,0,32,32,True
	Color 55,55,55
	Oval 0,16-3,6,6,True
	For i=0 To 360
		p\image[i] = CreateImage(32,32)
		p\image[i] = CopyImage(image)
		RotateImage p\image[i],i
	Next
	SetBuffer BackBuffer()
End Function

Function drawplayer()
	DrawImage p\image[p\angle],p\x,p\y
End Function

Function initmap()
	Restore mymap
	For y=0 To mapheight
	For x=0 To mapwidth
		Read a
		If a = 1 Then map(x,y) = a
		If a = 2 
			t.turret = New turret
			t\x = x * 32
			t\y = y * 32
			t\hull = 3
		End If
	Next
	Next
	SetBuffer ImageBuffer(mapimage)
	For y=0 To mapheight
	For x=0 To mapwidth
		Select map(x,y)
			Case 0:Color 0,0,0
			Case 1:Color 55,55,155
		End Select
		Rect x*32,y*32,32,32,True
	Next
	Next
	SetBuffer BackBuffer()
End Function

.mymap
Data 1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1
Data 1,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,1
Data 1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1
Data 0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0
Data 0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0
Data 0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,0,0,0,0
Data 0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0
Data 0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,1,2,1,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,1,2,1,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,1,1,1,1,2,1,1,1,1,1,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,2,1,0,0,0,0,0,0,0,1,2,1,0,0,0,0,0,0,1,2,1,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0
Data 0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0
Data 0,0,0,0,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,0,0,0,0
Data 0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0
Data 0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0
Data 1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1
Data 1,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,1
Data 1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
End Function

Function getangle(x1,y1,x2,y2)
         Local dx = x2 - x1
         Local dy = y2 - y1
         Return ATan2(dy,dx)+360 Mod 360
End Function
