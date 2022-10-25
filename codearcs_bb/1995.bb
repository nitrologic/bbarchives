; ID: 1995
; Author: Matt Merkulov
; Date: 2007-04-21 05:08:01
; Title: 2D turret firing algorhytms
; Description: rotation, straight, wave, forestalling, simple learning

'Turret firing algorhytms including forestalling and self-training AI by Matt Merkulov

Graphics3D 800, 600

PositionEntity CreateCamera(), 0, 0, -20
RotateEntity CreateLight(), 45, 45, 45

; Type for a turret - coordinates, angle, mode of shooting, handles for 3D - objects, variables for AI
Type turret
	Field x#, y#, Mode, head, barrel, ang#, dir#, dx#, dy# 
End Type

; Type for a shot - coordinates, increments, handle of 3D - object, 
; Distance from a turret, auxiliary variables for AI
Type shot
	Field x#, y#, dx#, dy#, h, r#, tx#, ty#, dir#, t.turret
End Type

Const aimax = 200, aistp# = .2
Dim aiang# (aimax)

SetFont LoadFont("arial", 14)
SetBuffer BackBuffer()

; Constants - the period of shooting, speed of shells, speed of the player
Const frq = 200, v0# = .01, vp# = .005
; Const frq = 200, v0# = .2, vp# = .1
Const bs# = 2

Global x#, y# 

; createturret 1:createturret 2:createturret 3:createturret 4
createturret 5:createturret 4:createturret 2


; Creation of the player
p = CreateSphere(16)
h = CreateSphere(8, p)
PositionEntity h, 1, 0, 0
ScaleEntity h, .4, .4, .4

Repeat
	tim = MilliSecs()

	For t.turret = Each turret

		; Auxiliary variables + targetting on the player by default
		dx# = x# - t\x
		dy# = y# - t\y
		cang# = ATan2(dy#, dx)
		t\dx# = dx# 
		t\dy# = dy# 
		t\dir# = cang# 
		pr# = Sqr(dx# * dx# + dy# * dy#)

		; Modes of an aiming of turrets:
		Select t\Mode
			Case 0; shooting in the given direction
				cang = 0
			Case 1; shooting with rotation
				cang# = t\ang# + .05 * dt# 
			Case 2; exact targetting on object

			Case 3; shooting by a fan
				cang# = cang# + Sin(dcang#) * 30
				dcang# = dcang# + 2
			Case 4; shooting on an forestalling
				; Calculation of factors of a quadratic
				a# = v# * v# - v0# * v0# 
				b# = 2.0 * v# * (dx# * Cos(ang#) + dy# * Sin(ang#) -bs#)
				c# = dx# * dx# + dy# * dy# - bs# * bs# 
				; Calculation of a discriminant
				d# = b# * b# - 4.0 * a# * c# 
				If d# >= 0 Then
					; Calculation of the moment of a meeting of a shell and the player
					t1# = (-b# + Sqr(d#)) / 2.0 / a#
					t2# = (-b# - Sqr(d#)) / 2.0 / a#
					If t2# > 0 Then t1# = t2# 
					If t1# >= 0 Then
						; Calculation of a angle through coordinates of object during the found moment of time
						cang# = ATan2(y# + t1# * v# * Sin(ang#) - t\y#, x# + t1# * v# * Cos(ang#) - t\x#)
					End If
				End If
			Case 5; a self - training turret (the amendment to a angle undertakes from a file)
				cang# = cang# + aiang(Int(pr# * aistp#))
		End Select

		; Turn of a turret
		rotateturret t, cang# 
		; Return of a barrel on former position
		If EntityX(t\barrel) < .75 Then MoveEntity t\barrel, 0, - .25 / frq * dt#, 0
	
		; A shot and displacement of a barrel inside of a turret
		If nextshot <= tim Then
			fire t
			PositionEntity t\barrel, .5, 0, 0
		End If
	Next

	; Delay between shots
	If nextshot <= tim Then nextshot = tim + frq

	For s.shot = Each shot
		; Moving bullets
		s\x# = s\x# + s\dx# * dt# 
		s\y# = s\y# + s\dy# * dt# 
		s\r# = s\r# + v0# * dt# 
		PositionEntity s\h, s\x#, s\y#, 0
		; If distances from a turret up to a bullet and up to the player are equal, 
		If pr# <= s\r# And s\dir# < 999 Then
			; The adjusting angle for the given distance is calculated
			Newang# = s\t\dir# - s\dir# 
			; Squeezes into borders (-180, 180)
			Newang# = Newang# - Floor((Newang# + 180.0) / 360.0) * 360.0
			; Also storing it in a file, 
			aiang(Int(pr# * aistp#)) = Newang# 
			;(A next line - for not to store this bullet in a file once again)
			s\dir# = 999
		End If
		If(s\x# - x#) ^ 2 + (s\y# - y#) ^ 2 < 1 Then
			FreeEntity s\h: Delete s
			red = 255
			hits# = hits# + 1
		Else
			; Removing the bullets which have overstepped the bounds of the screen
			If Abs(s\x#) + Abs(s\y#) > 40 Then FreeEntity s\h:Delete s
		End If
	Next

	; Indication of hit
	If red > 0 Then
		red = red - Int(dt / 3)
	Else
		red = 0
	End If
	EntityColor p, 255, 255 - red, 255 - red

	; Moving and turn of the player (speed is calculated depending on last time)
	PositionEntity p, x#, y#, 0
	RotateEntity p, 0, 0, ang# 
	v# = (KeyDown(200) - KeyDown(208)) * vp# 
	ang# = ang# + .3 * dt# * (KeyDown(203) - KeyDown(205))
	x# = x# + v# * Cos(ang#) * dt# 
	y# = y# + v# * Sin(ang#) * dt# 
	
	RenderWorld
	; Counter
	Text 0, 0, "Hits / sec:" + (hits# / sec#)
	Flip
	; Time, spent for this cycle (fractional as it is multiplied by fractional values)
	dt# = MilliSecs() - tim
	sec# = sec# + 0.001 * dt# 
Until KeyHit(1)

Function createturret(Mode)
	; Creation of a head part
	t.turret = New turret
	t\head = CreateCylinder(6)
	ScaleMesh t\head, 1, .5, 1
	RotateMesh t\head, 90, 0, 0
	; A barrel created separately, but to adhere to a head part
	t\barrel = CreateCylinder(16, False)
	RotateEntity t\barrel, 0, 0, 90
	ScaleEntity t\barrel, .2, 1, .2
	PositionEntity t\barrel, .75, 0, 0
	h = CreateCylinder(16, True)
	ScaleEntity h, .3, .3, .3
	RotateEntity h, 0, 0, 90
	PositionEntity h, 2, 0, 0
	EntityParent t\barrel, t\head
	EntityParent h, t\barrel
	; Setting random coordinates of a turret 
	t\x# = Rnd(-20, 20)
	t\y# = Rnd(-20, 20)
	PositionEntity t\head, t\x#, t\y#, 0
	t\Mode = Mode
End Function

Function rotateturret(t.turret, ang#)
	t\ang# = ang# 
	RotateEntity t\head, 0, 0, ang# 
End Function

Function fire(t.turret)
	ang# = t\ang# 
	s.shot = New shot
	s\x# = t\x# + Cos(ang#) * r
	s\y# = t\y# + Sin(ang#) * r
	s\r# = r
	s\dx# = Cos(ang#) * v0# 
	s\dy# = Sin(ang#) * v0# 
	s\h = CreateSphere(2)
	ScaleEntity s\h, .15, .15, .15
	EntityColor s\h, 255, 255, 0
	; Auxiliary variables(for AI)
	s\tx# = t\x# 
	s\ty# = t\y# 
	s\dir = t\dir# 
	s\t = t
End Function
