; ID: 1478
; Author: ImaginaryHuman
; Date: 2005-10-07 00:29:55
; Title: Draw Filled Circle Row by Row with Integer Math
; Description: Draws a filled circle one row at a time using integer math

'Midpoint Circle algorithm

Strict
Graphics 640,480,0

Local xCenter:Int=320
Local yCenter:Int=240
Local radius:Int
Local p,x,y,prevy:Int
Repeat
	Cls
	If MouseDown(1)
		xCenter=MouseX()
		yCenter=MouseY()
	EndIf
	radius=Abs(xCenter-MouseX())
	x=0
	y=radius
	SetColor $FF,$88,$00
	DrawRect xCenter-y,yCenter+x,y Shl 1,1
	SetColor $FF,$FF,$FF
	Plot xCenter+x,yCenter+y
	Plot xCenter-x,yCenter+y
	Plot xCenter+x,yCenter-y
	Plot xCenter-x,yCenter-y
	Plot xCenter+y,yCenter+x
	Plot xCenter-y,yCenter+x
	Plot xCenter+y,yCenter-x
	Plot xCenter-y,yCenter-x
	p=1-radius
	While x<y-1
		prevy=y
		If p<0
			x:+1
		Else
			x:+1
			y:-1
		EndIf
		If p<0
			p=p+(x Shl 1)+1
		Else
			p=p+((x-y) Shl 1)+1
		EndIf
		If y<prevy And x<y
			SetColor $FF,$88,$00
			DrawRect xCenter-x,yCenter+y,x Shl 1,1
			DrawRect xCenter-x,yCenter-y,x Shl 1,1
			SetColor $FF,$FF,$FF
			Plot xCenter+x,yCenter+y
			Plot xCenter-x,yCenter+y
			Plot xCenter+x,yCenter-y
			Plot xCenter-x,yCenter-y
		EndIf
		SetColor $FF,$88,$00
		DrawRect xCenter-y,yCenter+x,y Shl 1,1
		DrawRect xCenter-y,yCenter-x,y Shl 1,1
		SetColor $FF,$FF,$FF
		Plot xCenter+y,yCenter+x
		Plot xCenter-y,yCenter+x
		Plot xCenter+y,yCenter-x
		Plot xCenter-y,yCenter-x
	Wend
	Flip
Until KeyHit(KEY_ESCAPE)
End
