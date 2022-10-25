; ID: 26
; Author: BlitzSupport
; Date: 2001-08-26 22:37:26
; Title: 3D Doppler Effect
; Description: Using Blitz3D's 3D sound commands...

; -----------------------------------------------------------------------------
; 3D sound -- Doppler and stereo effects (crank the volume UP!)
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; IMPORTANT: Download this sample BEFORE RUNNING! This code's positioning is
; hard-coded for this sample!
; -----------------------------------------------------------------------------
;
; http://www.railroadxing.com/sounds/amtrak/amtrak%20290%20train%20horn%20for%20public%20crossing%20at%20grade.wav
;
; -----------------------------------------------------------------------------

Global gameFPS = 50

Graphics3D 640, 480
SetBuffer BackBuffer ()

cam = CreateCamera ()
CameraViewport cam, 0, 0, GraphicsWidth (), GraphicsHeight ()

; -----------------------------------------------------------------------------
; Hack up a really bad train (yes, those carriages ARE meant to be blank!)
; -----------------------------------------------------------------------------

box = CreateCube ()
carriage = CopyEntity (box, box)
carriage2 = CopyEntity (box, box)
carriage3 = CopyEntity (box, box)
carriage4 = CopyEntity (box, box)
ScaleEntity box, 1, 1, 4
PositionEntity carriage, 0, 0, 2.5
PositionEntity carriage2, 0, 0, 5
PositionEntity carriage3, 0, 0, 7.5
PositionEntity carriage4, 0, 0, 10
PositionEntity box, 50, 0, 50
TurnEntity box, 0, -48, 0

SetBuffer FrontBuffer ()
doppler = Input ("Enter 0 for normal sound or 1 for 3D Doppler: ")
SetBuffer BackBuffer ()

; -----------------------------------------------------------------------------
; Sample from 'RailRoadXing.com' -- URL at top of source!
; -----------------------------------------------------------------------------

sample$ = "amtrak%20290%20train%20horn%20for%20public%20crossing%20at%20grade.wav"

If doppler
	; 3D version
	horn = Load3DSound (sample$)
	CreateListener (cam, 0.5, 15, 2)			; Listener is attached to camera (trial-and-error parameters ;)
	EmitSound horn, box						; Play the sound from the front of the train
Else
	; Normal version
	horn = LoadSound (sample$)
	PlaySound horn
EndIf

; Frame-timing stuff...
framePeriod = 1000 / gameFPS
frameTime = MilliSecs () - framePeriod

Repeat

	; Frame-timing stuff...
	Repeat
		frameElapsed = MilliSecs () - frameTime
	Until frameElapsed
	frameTicks = frameElapsed / framePeriod
	frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod)
	For frameLimit = 1 To frameTicks
		If frameLimit = frameTicks Then CaptureWorld
		frameTime = frameTime + framePeriod
		MoveEntity box, 0, 0, -0.5
		UpdateWorld
	Next

	RenderWorld frameTween	
	
	Text 20, 20, "Crap train comin' through!"
	Flip

Until EntityZ (box) < -110 Or KeyHit (1)

End

