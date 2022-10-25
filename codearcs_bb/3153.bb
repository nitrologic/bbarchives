; ID: 3153
; Author: Pakz
; Date: 2014-10-20 09:42:58
; Title: Sideview shooter example
; Description: I remade a small part of Project X in this

; Sideview shooter example by Pakz
Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Type ast
	Field x,y
End Type

Type bullet
	Field t,x#,y#,mx#,my#
End Type
Type player
	Field x,y,w,h,e	;e=energy
	Field s 		; s = score
End Type
Type asteroid
	Field x#,y#,mx#,my#,f,ftime,p ; f = flash , flash time,p = power
	Field im,fim ; image and flash image
End Type

Global p.player = New player
p\x = 32
p\y = GraphicsHeight()/2
p\w = 32
p\h = 32

; make 10 asteroids
For i=0 To 10
	newasteroid
Next

timer = CreateTimer(60)
While KeyDown(1) = False
	WaitTimer(timer)
	Cls
	updateplayer()
	updatebullets()
	updateasteroids()
	drawbullets()
	drawasteroids()
	Color 255,0,0
	Oval p\x-p\w/2,p\y-p\h/2,p\w,p\h,True
	Color 255,255,255
	Text 0,0,"score:"+p\s
	Flip
Wend
End

Function newasteroid() ; create a new asteroid
	this.asteroid = New asteroid
	n = MilliSecs()
	; create the images
	this\im = CreateImage(64,64)
	this\fim = CreateImage(64,64)
	; draw the asteroids into the containers
	this\im = makeasteroid(0,n)
	this\fim = makeasteroid(1,n)
	; choose top right or bottom position for asteroid to begin
	a = Rand(1,3)
	Select a
		Case 1 ; top
			this\x = Rand(100,GraphicsWidth())
			this\y = -48
			this\mx = Rand(-1,-2)
			this\my = Rand(1,2)
		Case 2 ; bottom
			this\x = Rand(100,GraphicsWidth())
			this\y = GraphicsHeight()+48
			this\mx = Rand(-1,-2)
			this\my = Rand(-1,-2)		
		Case 3 ; right
			this\x = GraphicsWidth()+48
			this\y = Rand(-48,GraphicsHeight()+92)
			this\mx = Rand(-1,-2)
			this\my = Rand(-1,2)		
	End Select
	this\p = Rand(2,3)
End Function

Function updateasteroids()
	For this.asteroid = Each asteroid
		; move the asteroids
		this\x = this\x + this\mx
		this\y = this\y + this\my
		; stop the flashing
		If this\ftime < MilliSecs() Then this\f = False
		; if the asteroid is to far away out of the screen then delete it and remake it elsewhere
		If RectsOverlap(this\x-24,this\y-24,48,48,-64,-64,GraphicsWidth()+128,GraphicsHeight()+128) = False
			FreeImage this\im
			FreeImage this\fim
			Delete this
			newasteroid()
		End If	
	Next
End Function

Function drawasteroids()
	For this.asteroid = Each asteroid
		Select this\f
			Case False : DrawImage this\im,this\x-32,this\y-32
			Case True : DrawImage this\fim,this\x-32,this\y-32			
		End Select
	Next
End Function

Function updatebullets()
	For this.bullet = Each bullet
		rembullet = False
		this\x = this\x + this\mx
		this\y = this\y + this\my		
		For that.asteroid = Each asteroid		
			Select this\t
				Case 1
				If RectsOverlap(this\x-3,this\y-16,3,32,that\x-32,that\y-32,64,64)
					that\p = that\p - 1
					that\f = True
					that\ftime = MilliSecs()+120
					If that\p < 0
						p\s = p\s + 10
						FreeImage that\im
						FreeImage that\fim
						Delete that
						newasteroid
					End If
					rembullet = True
				End If
				Case 2
				If RectsOverlap(this\x-16,this\y-3,32,3,that\x-32,that\y-32,64,64)
					that\p = that\p - 1
					that\f = True
					that\ftime = MilliSecs()+120
					If that\p < 0
						p\s = p\s + 10					
						FreeImage that\im
						FreeImage that\fim
						Delete that
						newasteroid
					End If
					rembullet = True
				End If
				
			End Select
		Next
		If RectsOverlap(this\x,this\y,3,32,-32,-32,GraphicsWidth()+64,GraphicsHeight()+64) = False 
			Delete this
		End If
		If rembullet = True Then Delete this
	Next
End Function

;
; Move the player towards the mouse position
; Fire if pressed
; If collision with player then reset to start position
Function updateplayer()
	a = getangle(p\x,p\y,MouseX(),MouseY())
	p\x = p\x + Cos(a) * 5
	p\y = p\y + Sin(a) * 5
	If MouseHit(1)
		initshot()
	End If
	For this.asteroid = Each asteroid
		If RectsOverlap(p\x-16,p\y-16,32,32,this\x-32,this\y-32,64,64)
			p\x = 64
			p\y = GraphicsHeight()/2-16
			p\s = 0
		End If
	Next
End Function

Function initshot()
	this.bullet = New bullet
	this\x = p\x
	this\y = p\y
	this\mx = 12 ; bullet shooting speed
	this\my = 0
	this\t = 1
	this.bullet = New bullet
	this\x = p\x
	this\y = p\y
	this\mx = 0
	this\my = -12
	this\t = 2
	this.bullet = New bullet
	this\x = p\x
	this\y = p\y
	this\mx = 0
	this\my = 12
	this\t = 2

End Function

Function drawbullets()
	For this.bullet = Each bullet
		If this\t = 1
			Color 0,0,255
			Oval this\x,this\y-16,4,32,True
		End If
		If this\t = 2
			Color 0,0,255
			Oval this\x-16,this\y,32,4,True
		End If
	Next
End Function

Function getangle(x1,y1,x2,y2)
         Local dx = x2 - x1
         Local dy = y2 - y1
         Return ATan2(dy,dx)+360 Mod 360
End Function

;
; This function makes a random asteroid
;
; It goes in a circle and uses a random distance to place points
; then it draws lines from the last point to the last point
;
Function makeasteroid(c=0,seed)
	Delete Each ast
	SeedRnd seed
	Local im = CreateImage(64,64)
	SetBuffer ImageBuffer(im)
	Select c
		Case 0:Color 150,150,150
		Case 1:Color 255,255,255
	End Select
	a=-180
	For i=0 To 20
		a = a + 360/20
		this.ast = New ast
		this\x = Cos(a)*Rand(20,32)
		this\y = Sin(a)*Rand(20,32)
	Next

	this.ast = Last ast
	x1 = this\x
	y1 = this\y
	For this.ast = Each ast
		Line x1+32,y1+32,this\x+32,this\y+32
		x1 = this\x
		y1 = this\y	 
	Next
	SetBuffer BackBuffer()
	Return im
End Function
