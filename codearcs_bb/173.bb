; ID: 173
; Author: Matty B
; Date: 2001-12-30 07:40:43
; Title: Star field
; Description: Basic 3d Stars 

Const MAX_STAR=5000,STAR_SPEED=2,WIDTH=640,HEIGHT=480

Dim star_x(MAX_STAR),star_y(MAX_STAR),star_z(MAX_STAR)

Graphics WIDTH,HEIGHT
SetBuffer BackBuffer()

setup_stars()

While Not KeyDown(1)
	Cls
	UpdateStar()
	Flip
Wend
End

Function setup_stars()
  	For c=0 To MAX_STAR
	star_x(c)=Rnd(-(WIDTH/2),(WIDTH/2))Shl 8
	star_y(c)=Rnd(-(HEIGHT/2),(HEIGHT/2))Shl 8
    star_z(c)=Rnd(STAR_SPEED,255)
	Next 
End Function

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
