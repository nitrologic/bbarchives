; ID: 1281
; Author: xlsior
; Date: 2005-02-05 05:02:41
; Title: BlitzMax Fading Effects
; Description: Cross-Fade, Fade to Black, Fade To White

' BlitzMax Fade Routines
' - CrossFade
' - FadeToWhite
' - FadeToBlack
'
' Freeware, (C) 2005 by xlsior/Marc van den Dikkenberg
'
' - Press any key to progress to the next fade effect.
' - Press Escape to exit.
'
' Note: the 'perc' variable is used to define the intensity of an effect percentage-wise, and
' and should be passed a value between 0 and 100 (percent).
' Use any two full-screen images to see these effects.


Graphics 800,600,32,60
SetColor 255,255,255
Local Counter:Int

pic1=LoadImage("pic1.png")
pic2=LoadImage("pic2.jpg")

DrawImage (pic1,0,0)
Flip
WaitKey()

While Not KeyHit(Key_Escape)
	' do a crossfade
	For Counter=0 To 100
		CrossFade(pic1,pic2,Counter)
		Flip
	Next
	WaitKey()
	If KeyDown(key_escape) Then Exit

	' ...and back again!
	For Counter=0 To 100
		CrossFade(pic2,pic1,Counter)
		Flip
	Next
	WaitKey()
	If KeyDown(key_escape) Then Exit

	' Fade to white
	For counter=0 To 100
		DrawImage (pic1,0,0)
		FadeToWhite(counter)
		Flip
	Next
	WaitKey()
	If KeyDown(key_escape) Then Exit

	' Re-draw the image
	DrawImage (pic1,0,0)
	Flip
	WaitKey()
	' ...and fade to black
	For Counter=0 To 200 
		DrawImage (pic1,0,0)
		FadeToBlack(Counter/2)
		Flip
	Next
	WaitKey()
	If KeyDown(key_escape) Then Exit
Wend

Function FadeToWhite(perc:Float)
	If perc>100 Then
		perc=100 
	ElseIf perc<0 Then
		perc=0
	End If
	If perc>0 Then 
		SetColor 255,255,255
		SetBlend (alphablend)
		SetAlpha (perc/100)
		DrawRect (0,0,GraphicsWidth(),GraphicsHeight())
	End If 
End Function

Function FadeToBlack(perc:Float)
	If perc>100 Then
		perc=100 
	ElseIf perc<0 Then
		perc=0
	End If
	If perc>0 Then 
		SetColor 0,0,0
		SetBlend (alphablend)
		SetAlpha (perc/100)
		DrawRect (0,0,GraphicsWidth(),GraphicsHeight())
	End If 
End Function

Function CrossFade(pic1:timage,pic2:timage,perc:Float)
	If perc<1 Then
		perc=1
	ElseIf perc>100 Then
		perc=100
	End If
	SetBlend (SolidBlend)
	SetColor 255,255,255
	DrawImage (pic1,0,0)
	SetBlend (Alphablend)
	SetAlpha (perc/100)
	DrawImage (pic2,0,0)
End Function
