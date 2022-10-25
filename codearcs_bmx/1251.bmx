; ID: 1251
; Author: Jim Brown
; Date: 2005-01-03 09:13:30
; Title: Starfield 3D
; Description: Simple 3D starfield effect

' Starfield - adapted from Amiga Blitz II code
' JimB

Framework BRL.GLMax2D
Import BRL.Random
Import BRL.LinkedList

Const width=640 , height=480
Const numstars=775  	' total number of stars to render

Graphics width,height
HideMouse

Global mylist:TList=CreateList()

Type star
	Const spread=64     ' spread of stars (smaller=narrow field)
	Const speed#=1.9    ' speed of stars movement
	Const cx=width/2    ' center xpos of display
	Const cy=height/2   ' center ypos of display
	Field x#,y#,z#,sx#,sy#
	Method New()
		ListAddLast mylist,Self
		SetPosition
	End Method
	Method SetPosition()
		z=Rand(200,255)
		x=Rand(-1000,1000)
		y=Rand(-1000,1000)
	End Method
	Method DrawStar()
	  If z<speed SetPosition
  	z:-speed
  	sx=(x*spread)/(z)+cx
  	sy=(y*spread)/(4+z)+cy
  	If sx<0 Or sx>width SetPosition
  	If sy<0 Or sy>height SetPosition
  	SetColor 255-z,255-z,255-z
		glVertex2f sx+.5,sy+.5			'Plot sx,sy
	End Method
End Type

For d=1 To numstars
	Local s:star=New star
Next

glDisable GL_TEXTURE_2D

While Not KeyHit(KEY_ESCAPE)
	Cls
	glBegin GL_POINTS
	For s:star=EachIn mylist
		s.DrawStar
	Next
	glEnd
	Flip
Wend

End
