; ID: 3154
; Author: Pakz
; Date: 2014-10-22 09:13:08
; Title: 2d Space Invaders example
; Description: Bare version of space invaders. wave, destructable barriers, bullets

; Space invaders example by Pakz (Rudy van Etten)

Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Global wavetime,wavemx#
Global alienlaserfallspeed# = 2
Global playerbulletrisespeed# = -3

Type alien
	Field x,y
End Type

Type barrier ; destructable barriers
	Field x,y,image
End Type

Type player
	Field x,y,score
End Type

Type pbullet ; player bullets
	Field x#,y#
End Type

Type abullet ; alien bullets
	Field x#,y#
End Type

Global p.player = New player
p\x = GraphicsWidth()/2-16/2
p\y = GraphicsHeight()-32

initbarriers()
initaliens()
wavemx = 1

timer = CreateTimer(60)
While KeyDown(1) = False
	WaitTimer timer
	Cls
	updateplayer
	updateplayerbullets
	updatealiens
	updatealienbullets
	updatebarriers
	drawaliens
	drawalienbullets
	drawbarriers
	drawplayerbullets
	drawplayer
	; Restart if aliens are all gone
	restart =  True
	For this.alien = Each alien
		restart = False : Exit
	Next
	If restart = True
		Delete Each abullet
		Delete Each pbullet
		initaliens()
		wavemx = 1
		p\x = GraphicsWidth()/2-16		
	End If	
	Color 255,255,255
	Text 0,0,"Press cursors to move shipblock and space to fire"
	Flip
Wend
End

Function updatebarriers()
	For this.pbullet = Each pbullet
		delbullet = False
		For that.barrier = Each barrier
			If ImageRectCollide(that\image,that\x-32,that\y-24,0,this\x-4,this\y-6,8,12)
				SetBuffer ImageBuffer(that\image)
				Color 0,0,0
				;location in image to erase
				x1 = this\x 
				x2 = that\x 
				y1 = this\y 
				y2 = that\y 
				x3 = x1-x2
				y3 = y1-y2
				Oval x3+ImageWidth(that\image)/2-6,y3+16-6,12,12,True
				SetBuffer BackBuffer()
				delbullet = True
			End If
		Next
		If delbullet = True Then Delete this
	Next
	For a1.abullet = Each abullet
		delbullet = False
		For a2.barrier = Each barrier
			If ImageRectCollide(a2\image,a2\x-32,a2\y-24,0,a1\x-4,a1\y-6,8,12)
				SetBuffer ImageBuffer(a2\image)
				Color 0,0,0
				;location in image to erase
				x1 = a1\x 
				x2 = a2\x 
				y1 = a1\y 
				y2 = a2\y 
				x3 = x1-x2
				y3 = y1-y2
				DebugLog x3+","+y3
				Oval x3+ImageWidth(a2\image)/2-6,y3+28-4,12,12,True
				SetBuffer BackBuffer()
				delbullet = True
			End If
		Next
		If delbullet = True Then Delete a1
	Next

End Function

Function initbarriers()
	For x=64 To GraphicsWidth()-64 Step 96
		this.barrier = New barrier
		this\x = x
		this\y = GraphicsHeight()-128
		If this\image = 0 this\image = CreateImage(64,48)
		SetBuffer ImageBuffer(this\image)
		Color 200,200,200
		Rect 0,0,ImageWidth(this\image),ImageHeight(this\image),True
	Next
	SetBuffer BackBuffer()
End Function

Function drawbarriers()
	For this.barrier = Each barrier
		DrawImage this\image,this\x-ImageWidth(this\image)/2,this\y-ImageHeight(this\image)/2
	Next
End Function

Function updateplayerbullets()
	For this.pbullet = Each pbullet
		this\y = this\y + playerbulletrisespeed
		If this\y+6 < -32 Then Delete this
	Next
End Function

Function drawplayerbullets()
	Color 255,255,0
	For this.pbullet = Each pbullet
		Oval this\x-2,this\y-6,4,12,True
	Next
End Function

Function updateplayer()
	If KeyDown(205)
		If p\x < GraphicsWidth()-16
			p\x = p\x + 3
		End If		
	End If
	If KeyDown(203)
		If p\x > 16
			p\x = p\x - 3
		End If
	End If
	If KeyDown(57) = True
		fire = True
		nothing = True		
		For this.pbullet = Each pbullet
			nothing = False
			d = distance(this\x,this\y,p\x,p\y)			
			DebugLog d+","+MilliSecs()
			If d < 64
				fire = False
			End If
		Next
		If fire = True Or nothing = True Then
			that.pbullet = New pbullet
			that\x = p\x
			that\y = p\y-16
		End If
	End If
	For a1.abullet = Each abullet
		If RectsOverlap(p\x-16,p\y-16,32,32,a1\x-4,a1\y-6,8,12) = True
			p\score = 0
			p\x = GraphicsWidth()/2-16
			p\y = GraphicsHeight() - 32
			Delete a1
		End If 
	Next
End Function

Function updatealienbullets()
	For this.abullet = Each abullet
		this\y = this\y + alienlaserfallspeed
		If this\y-2 > GraphicsHeight() Then Delete this
	Next
End Function

Function drawalienbullets()
	Color 0,0,255
	For this.abullet = Each abullet
		Oval this\x-6,this\y-6,6,12,True
	Next
End Function

Function updatealiens()
	For this.alien = Each alien
		removecurrentalien = False
		If this\x + 32 > GraphicsWidth() Then wavemx = -1
		If this\x - 32 < 0 Then wavemx = 1
		this\x = this\x + wavemx
		If Rand(1,3000) = 1
			that.abullet = New abullet
			that\x = this\x
			that\y = this\y + 16
		End If
		; collision with the player bullets (pbullet)
		For thot.pbullet = Each pbullet
			If RectsOverlap(this\x-16,this\y-16,32,32,thot\x-4,thot\y-6,8,12)
				removecurrentalien = True
				Delete thot
			End If
		Next
		If removecurrentalien = True Then Delete this
	Next
End Function

Function drawaliens()
	Color 255,255,255
	For this.alien = Each alien
		Rect this\x-16,this\y-16,32,32,True
	Next
End Function

Function initaliens()
	For y=0 To 4
	For x=0 To 9
		this.alien = New alien
		this\x = x * 48 + 32
		this\y = y * 48 + 32
	Next
	Next	
End Function

Function drawplayer()
	Color 255,255,255
	Rect p\x-16,p\y-16,32,32,True
End Function

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
End Function
