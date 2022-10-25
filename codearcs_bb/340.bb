; ID: 340
; Author: BlitzSupport
; Date: 2002-06-07 22:11:44
; Title: Lorenz 3D
; Description: Fractal exploration in 3D

; Lorenz Attractor in 3D... from 'Computers and Chaos' by Conrad Bessant,
; converted from AmigaBASIC to Blitz3D by james @ hi - toro . com :)

; CONTROLS:

; Cursors + A & Z, or mouse + both buttons.
; SPACE toggles drawing.

AppTitle "Lorenz Attractor in 3D"

; Display mode...

sw = 640 : sh = 480
;sw = 1024: sh = 768

Graphics3D sw, sh

; I don't pretend to understand any of this, but play with a, b and c for
; different results...

a# = 10
b# = 28
c# = 8.0 / 3.0

dt# = 0.01

x# = 1
y# = 1
z# = 1

; Render every iteration (eg. 2 renders every 2nd iteration, etc)...

detail = 1

cam = CreateCamera ()
CameraRange cam, 0.1, 1000
PositionEntity cam, 0, 0, -50

ball = LoadSprite ("point.bmp", 16 + 32)
If ball
	ScaleSprite ball, 2.5, 2.5
	spritescale# = 0.5
Else
	ball = CreateSprite (): EntityColor ball, 0, 255, 0
	spritescale# = 0.25
EndIf

wind = Load3DSound ("wind.wav")
If wind
	LoopSound wind
	CreateListener (cam, 1, 0.1)
	windchannel = EmitSound (wind, ball)
EndIf

SetFont LoadFont ("arial", 15, 1)

fps = CreateTimer (60)

Repeat

	WaitTimer (fps)
	
	MoveMouse GraphicsWidth () / 2, GraphicsHeight () / 2
	TurnEntity cam, -MouseYSpeed () / 5.0, -MouseXSpeed () / 5.0, 0
	
	If (KeyDown (30)) Or (MouseDown (1))
		MoveEntity cam, 0, 0, 0.5
	Else
		If (KeyDown (44)) Or (MouseDown (2))
			MoveEntity cam, 0, 0, -0.5
		EndIf
	EndIf
	
	If KeyHit (57)
		stopdrawing = 1 - stopdrawing
		If wind Then ChannelVolume windchannel, 1 - stopdrawing
	EndIf
	
	If KeyDown (203)
		TurnEntity cam, 0, 2, 0
	Else
		If KeyDown (205)
			TurnEntity cam, 0, -2, 0
		EndIf
	EndIf

	If KeyDown (200)
		TurnEntity cam, 2, 0, 0
	Else
		If KeyDown (208)
			TurnEntity cam, -2, 0, 0
		EndIf
	EndIf

	If stopdrawing = False

		; These six lines are the equation which produces the whole thing!
		
		dx# = a * (y - x)
		dy# = b * x - y - x * z
		dz# = x * y - c * z

		x = x + dx * dt
		y = y + dy * dt
		z = z + dz * dt
	
		frames = frames + 1
		If frames Mod detail = 0
			PositionEntity ball, x, y, z
			newball = CopyEntity (ball)
			ScaleSprite newball, spritescale, spritescale
		EndIf

	EndIf
	
	RenderWorld

	If stopdrawing
		Text 20, 20, "Iterations, drawn every " + detail + " frame(s): " + frames + "  -- use SPACE to pause/continue"
	EndIf
	
	Flip
	
Until KeyHit (1)

End
