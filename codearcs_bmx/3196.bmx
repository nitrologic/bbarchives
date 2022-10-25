; ID: 3196
; Author: GW
; Date: 2015-03-11 20:22:47
; Title: Randomly Generated Icons
; Description: Procedurally generated Icons

SuperStrict
Framework brl.glmax2d
Import brl.basic
AppTitle = "Hit space for more icons"
Graphics 320,240

While Not KeyHit( key_escape)
	Cls
		DrawImage(makeIcon(),0,0)'x*32,y*32)
	Flip
	WaitKey
Wend



Function makeIcon:TImage()
	For Local i% = 0 Until 50
		Local x% = Rand(-5,50)
		Local y% = Rand(-5,50)
		SetColor(Rand(255),Rand(255),Rand(255))
		SetRotation Rand(300)
		SetLineWidth(Rand(1,2))
		Select i Mod 3
			Case 0
				DrawRect(x,y,Rand(20),Rand(20))
			Case 1
				DrawOval(x,y,Rand(20),Rand(20))
			Case 2
				DrawLine(x,y,Rand(64),Rand(64))
		End Select
	Next
	
	SetColor(255,255,255)
	SetRotation 0
	SetLineWidth 1
	
	Local img:TImage = CreateImage(16,16,1,0) ;  GrabImage(img, 0,0)
	SetImageHandle img, 16,16

	For Local j% = 0 Until 360/4
		SetRotation (360/4)*j
		DrawImage(img,16,16)
	Next

	SetRotation 0
	Local outimg:TImage = CreateImage(32,32,1,0)
	GrabImage(outimg,0,0)
	Cls
	Return outimg
End Function
