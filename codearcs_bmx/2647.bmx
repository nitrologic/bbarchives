; ID: 2647
; Author: Galaxy613
; Date: 2010-01-25 10:46:27
; Title: Full 2D Starfield
; Description: A starfield that scrolls in ALL directions, specifcally for space games.

Strict
Global scnx = 640
Global scny = 480

Graphics scnx,scny
SetBlend ALPHABLEND
SeedRnd MilliSecs()

Type StarField
	Global Stars:TList = CreateList()
	Field x,y,a#
	
	Method New()
		ListAddLast Stars,Self
	End Method
	
	Method _draw(gx#,gy#,gscale# = 1.0)
		Local tx = x+(gx*a)
		Local ty = y+(gy*a)
		
		If tx < 0 Then 	tx = scnx+(tx Mod scnx)
		If tx > scnx Then tx = (tx Mod scnx)
		If ty < 0 Then 	ty = scny+(ty Mod scny)
		If ty > scny Then ty = (ty Mod scny)
		
		SetAlpha a
		If gscale*a < 1.0 Then
			Plot x+(gx*a),y+(gy*a)
		Else
			DrawOval x+(gx*a),y+(gy*a),gscale*a,gscale*a
		EndIf
		SetAlpha 1
	End Method
	
	Function draw(gx#,gy#,gscale# =1.0)
		For Local tmpStar:Starfield = EachIn Stars
			tmpStar._draw gx,gy,gscale
		Next
	End Function
	
	Function Fill(num_of_stars% = 100)
		For Local i = 0 To num_of_stars
			Local tmpStar:Starfield = New starfield
			tmpStar.x = Rand(0,scnx)
			tmpStar.y = Rand(0,scny)
			tmpstar.a = Rnd(0.1,0.9)
		Next
	End Function
End Type

'--------------------------------------------------
'	Example Program, arrow keys to scroll around
Local sX# = 0
Local sY# = 0

Starfield.Fill

While (Not KeyHit(key_Escape)) And (Not AppTerminate())
	Cls
	
	Starfield.draw sX, sY, 3 ' closer stars are 3 pixels wide, further stars get smaller.
	'Starfield.draw sX. sY ' every star is a single pixel
	
	If KeyDown(Key_Left) Then 	sX :+ 2.5
	If KeyDown(Key_Right) Then	sX :- 2.5
	If KeyDown(Key_Up) Then 	sY :+ 2.5
	If KeyDown(Key_Down) Then	sY :- 2.5 
	
	DrawText Int(sX) +" , "+Int(sY) , 0,0
	
	Flip
Wend
