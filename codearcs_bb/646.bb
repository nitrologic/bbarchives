; ID: 646
; Author: cbmeeks
; Date: 2003-04-08 14:20:06
; Title: Freaky Aliens Attacking!
; Description: Using cos and sin waves

;	Super Cheesy alien animation by cbmeeks of signaldev.com

;graphics
Graphics 640,480,16,1
SetBuffer BackBuffer()


;vars and types
Dim csin#(360)
Dim ccos#(360)
For a=0 To 359
	csin#(a) = Sin(a) * 2
	ccos#(a) = Cos(a) * 10
Next

;stars I ripped from someone
Const MAX_STAR=5000,STAR_SPEED=2,WIDTH=640,HEIGHT=480
Dim star_x(MAX_STAR),star_y(MAX_STAR),star_z(MAX_STAR)

For c=0 To MAX_STAR
	star_x(c)=Rnd(-(WIDTH/2),(WIDTH/2))Shl 8
	star_y(c)=Rnd(-(HEIGHT/2),(HEIGHT/2))Shl 8
	star_z(c)=Rnd(STAR_SPEED,255)
Next 




Global ALIVE=1, DEAD=0
Global NumAliens = 100
Global AlienImg, ShipImg

Type AlienType
	Field x,y,xspeed,yspeed,status
End Type

;Alien Type
For a=1 To NumAliens
	Alien.AlienType= New AlienType
	Alien\x = Rnd(640)
	Alien\y = Rnd(0,300)
	Alien\xspeed = 0
	Alien\yspeed = 0
	Alien\status = ALIVE
Next

SeedRnd MilliSecs()

;load images
AlienImg = LoadImage("Bug.png")
MaskImage AlienImg,255,0,255
ShipImg = LoadAnimImage("Player.png",38,36,0,3)
MaskImage ShipImg,255,0,255

;main loop
Repeat
	Cls


	;draw stars
	UpdateStar()

	;draw aliens

	For Alien.AlienType = Each AlienType
		DrawImage AlienImg, Alien\x, Alien\y
		
		Alien\x = Alien\x + Alien\xspeed
		Alien\y = Alien\y + Alien\yspeed + 1
		
		Alien\xspeed = csin#(count) * Rnd(-1,1)
		Alien\yspeed = ccos#(count)
		
		If Alien\y > 550 Then Alien\y = Rnd(-200,-50)

	Next

	count = count + 1: If count > 359 Then count = 0
	
	;draw ship
	If MouseX() < 215 Then
		DrawImage ShipImg,MouseX(),400,1
	End If
	If MouseX() >= 215 And MouseX() < 430 Then
		DrawImage ShipImg,MouseX(),400,0
	End If
	If MouseX() >= 430 Then
		DrawImage ShipImg,MouseX(),400,2
	End If
	

	Flip	
Until KeyHit(1)
End


Function UpdateStar()
	For c=0 To MAX_STAR
	star_z(c)=star_z(c)-STAR_SPEED
	If star_z(c)<=STAR_SPEED Then star_z(c)=255
	s_x=(star_x(c)/star_z(c))+(WIDTH/2)
	s_y=(star_y(c)/star_z(c))+(HEIGHT/2)
	col=255-star_z(c)
	Color col,col,col
	Plot s_x,s_y
	Next
End Function
