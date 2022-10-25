; ID: 1479
; Author: ImaginaryHuman
; Date: 2005-10-07 00:32:20
; Title: Draw Filled Ellipse Row by Row with Integer Math
; Description: Draws a filled ellipse one row at a time using integer math

'Midpoint ellipse algorithm

Strict
Graphics 640,480,0

Local xCenter:Int=320
Local yCenter:Int=240
Local Rx,Ry:Int
Local p,px,py,x,y,prevy:Int
Local Rx2,Ry2,twoRx2,twoRy2:Int
Local pFloat:Float
Repeat
	Cls
	If MouseDown(1)
		xCenter=MouseX()
		yCenter=MouseY()
	EndIf
	Rx=Abs(xCenter-MouseX())
	Ry=Abs(yCenter-MouseY())
	DrawText String(Rx)+" x "+String(Ry),20,20
	Rx2=Rx*Rx
	Ry2=Ry*Ry
	twoRx2=Rx2 Shl 1
	twoRy2=Ry2 Shl 1
	'Region 1
	x=0
	y=Ry
	SetColor $FF,$88,$00
	DrawRect xCenter-Rx,yCenter,Rx Shl 1,1
	SetColor $FF,$FF,$FF
	Plot xCenter+Rx,yCenter
	Plot xCenter-Rx,yCenter
	Plot xCenter,yCenter-Ry
	Plot xCenter,yCenter+Ry
	pFloat=(Ry2-(Rx2*Ry))+(0.25*Rx2)
	p=Int(pFloat + (Sgn(pFloat)*0.5))
	px=0
	py=twoRx2*y
	While px<py-1
		prevy=y
		x:+1
		px:+twoRy2
		If p>=0
			y:-1
			py:-twoRx2
		EndIf
		If p<0 Then p:+Ry2+px Else p:+Ry2+px-py
		If y<prevy And px<py-1
			SetColor $FF,$88,$00
			DrawRect xCenter-x,yCenter+y,x Shl 1,1
			DrawRect xCenter-x,yCenter-y,x Shl 1,1
			SetColor $FF,$FF,$FF
			Plot xCenter+x,yCenter+y
			Plot xCenter-x,yCenter+y
			Plot xCenter+x,yCenter-y
			Plot xCenter-x,yCenter-y
		EndIf
	Wend
	'Region 2
	pFloat=(Ry2*(x+0.5)*(x+0.5))+(Rx2*(y-1.0)*(y-1.0))-(Rx2*(Float(Ry2)))
	p=Int(pFloat + (Sgn(pFloat)*0.5))
	y:+1
	While y>1
		y:-1
		py:-twoRx2
		If p<=0
			x:+1
			px:+twoRy2
		EndIf
		If p>0 Then p:+Rx2-py Else p:+Rx2-py+px
		SetColor $FF,$88,$00
		DrawRect xCenter-x,yCenter+y,x Shl 1,1
		DrawRect xCenter-x,yCenter-y,x Shl 1,1
		SetColor $FF,$FF,$FF
		Plot xCenter+x,yCenter+y
		Plot xCenter-x,yCenter+y
		Plot xCenter+x,yCenter-y
		Plot xCenter-x,yCenter-y
	Wend
	Flip
Until KeyHit(KEY_ESCAPE)
End
