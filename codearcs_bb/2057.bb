; ID: 2057
; Author: Nebula
; Date: 2007-07-06 20:49:40
; Title: Land generator
; Description: Generate islands

; Land generator - Crom design

; Random colored blocks
; Image Rotation

Graphics 640,480,16,2
SetBuffer BackBuffer()

Global mim = CreateImage(50,50)

SetBuffer ImageBuffer(mim)
For y=0 To 50 Step 5
	For x=0 To 50 Step 5
		Select Rand(0,2)
			Case 0
			Color 0,0,0
			Case 1
			Color 144,255,0
			Case 2
			Color 0,255,0
		End Select
		Rect x,y,5,5,True
	Next
Next
SetBuffer BackBuffer()

TFormFilter  False
MidHandle mim
For i=0 To 25 : RotateImage mim,Rand(-12,12) : Next

While KeyDown(1) = False
	Cls
	DrawBlock mim,110,110
	Flip
Wend
End
