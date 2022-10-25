; ID: 1354
; Author: Mikle
; Date: 2005-04-17 08:40:55
; Title: CalcGunAngle
; Description: Returns gun's angle to hit the target

; This function was made by Mikle's friend Dima (DiGlib)
; with the help of MathLab. Thanks to MathLab developers!

; Calculates the angle, on which the gun must be rotated
; so that the bullet would appear currently at (x, y).
; x, y - target coordinates
; V - speed of bullet
; g - acceleration that will influence on bullet's Y-speed
;      when it flies (usually gravity and Archimede's Force)
; bStraight - if the trajectory is grazing or curved

Function CalcGunAngle#(x#, y#, V#, g#, bStraight = True)
	Local V2#, x2#, y2#, yg#
	Local w1#, w2#, sw2#, atx#, aty#, Ang#
	g = g*0.5
	V2 = V*V
	x2 = x*x
	y2 = y*y
	yg = y*g
	If bStraight > 0
		w1 = Sqr((V2*V2+4*V2*yg-4*g*g*x2))
	Else
		w1 = -Sqr((V2*V2+4*V2*yg-4*g*g*x2))
	EndIf
	w2 = (V2+2*yg+w1)/(x2+y2)
	sw2 = Sqr(w2)
	atx = (0.5*y*w2-g)/(V*sw2)
	aty = 0.5*sw2*x/V
	Ang = ATan2(atx, aty)
	Return Ang
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; TEST

Global GW = 640
Global GH = 480

Graphics GW, GH, 16, 2
SetBuffer BackBuffer()

HidePointer

Font = LoadFont("Courier", 16, True, False, False)
SetFont Font

Local Gravity# = -0.5
Local ArchForce# = 0.2

Local p_x# = 0
Local p_y# = 0

Local v# = 15
Local g# = Gravity+ArchForce

Local radius# = 5

Local ang#[1]
Local x#[1], y#[1]
Local cx#[1], cy#[1]
Local vx#[1], vy#[1]

While Not KeyHit(1)
	; Set the target to mouse's position
	p_x = MouseX()
	p_y = GH-MouseY()

	; Change speed, if necessary
	v = v+(MouseDown(1)-MouseDown(2))*0.1
	If (v < 0) Then v = 0

	; Calculate angles
	ang[0] = CalcGunAngle(p_x, p_y, v, g, True)
	ang[1] = CalcGunAngle(p_x, p_y, v, g, False)

	Cls

	; Show target
	Color 255, 0, 0
	Oval p_x-radius, GH-p_y-radius, radius*2, radius*2, False

	; Draw trajectories
	For ia = 0 To 1
		If Upper(ang[ia]) <> "NAN"
			x[ia] = 0
			y[ia] = 0
			cx[ia] = 0
			cy[ia] = 0
			vx[ia] = v*Cos(ang[ia])
			vy[ia] = v*Sin(ang[ia])
			Color 255*ia, 255, 0
			For t# = 1 To 1000
				cx[ia] = vx[ia]*t
				cy[ia] = vy[ia]*t+(g*0.5)*t^2
				Line x[ia], GH-y[ia], cx[ia], GH-cy[ia]
				x[ia] = cx[ia]
				y[ia] = cy[ia]
			Next
		EndIf
	Next

	; Output results
	Color 255, 255, 255
	txt_x = 0
	txt_y = 0
	Text txt_x, txt_y, "Move mouse to change target position."
	txt_y = txt_y + FontHeight()
	Text txt_x, txt_y, "Mouse left/right button to change speed."
	txt_y = txt_y + FontHeight()
	Text txt_x, txt_y, "X = "+p_x
	txt_y = txt_y + FontHeight()
	Text txt_x, txt_y, "Y = "+p_y
	txt_y = txt_y + FontHeight()
	Text txt_x, txt_y, "Speed = "+v
	txt_y = txt_y + FontHeight()
	Text txt_x, txt_y, "Acceleration = "+g
	txt_y = txt_y + FontHeight()
	Text txt_x, txt_y, "Grazing angle = "+ang[0]
	txt_y = txt_y + FontHeight()
	Text txt_x, txt_y, "Curved angle = "+ang[1]
	txt_y = txt_y + FontHeight()

	Flip
Wend

FreeFont Font

EndGraphics

End
