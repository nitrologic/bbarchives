; ID: 9
; Author: BlitzSupport
; Date: 2001-09-17 23:31:53
; Title: Commented 3D game framework
; Description: The basic framework of a frame-limited/frame-tweening Blitz 3D game

; ------------------------------------------------------------------
; 	GameCore -- support@blitzbasic.com
; ------------------------------------------------------------------
; The basics of a frame-limited Blitz 3D game, ready to rock
; ------------------------------------------------------------------
;             Adapted from Mark Sibly's code
; ------------------------------------------------------------------



; ------------------------------------------------------------------
;	Game's frames-per-second setting
; ------------------------------------------------------------------

Global gameFPS = 50

; ------------------------------------------------------------------
;	Open 3D display mode
; ------------------------------------------------------------------

Graphics3D 640, 480

; ------------------------------------------------------------------
; Single camera setup
; ------------------------------------------------------------------

cam = CreateCamera ()
CameraViewport cam, 0, 0, GraphicsWidth (), GraphicsHeight ()

; ------------------------------------------------------------------
; General setup
; ------------------------------------------------------------------

; Load and arrange objects, textures, etc here...

	; Quick example (just delete this)...
	
	Global box = CreateCube ()
	MoveEntity box, 0, 0, 5

; ------------------------------------------------------------------
;	Frame limiting code setup
; ------------------------------------------------------------------

framePeriod = 1000 / gameFPS
frameTime = MilliSecs () - framePeriod

Repeat

	; --------------------------------------------------------------
	; Frame limiting
	; --------------------------------------------------------------

	Repeat
		frameElapsed = MilliSecs () - frameTime
	Until frameElapsed

	frameTicks = frameElapsed / framePeriod
	
	frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod)

	; --------------------------------------------------------------
	; Update game and world state
	; --------------------------------------------------------------
	
	For frameLimit = 1 To frameTicks
	
		If frameLimit = frameTicks Then CaptureWorld
		frameTime = frameTime + framePeriod
		
		UpdateGame ()

		UpdateWorld
			
	Next

	; --------------------------------------------------------------
	; **** Wireframe for DEBUG only -- remove before release! ****
	; --------------------------------------------------------------
		
	If KeyHit (17): w = 1 - w: WireFrame w: EndIf ; Press 'W'
	
	; --------------------------------------------------------------
	; Draw 3D world
	; --------------------------------------------------------------

	RenderWorld frameTween

	; --------------------------------------------------------------
	; Show result
	; --------------------------------------------------------------

	Flip

Until KeyHit (1)

End

; ------------------------------------------------------------------
; Game update routine, called from frame limiting code
; ------------------------------------------------------------------

Function UpdateGame ()

	; Get keypresses, move entities, etc

	; EXAMPLE CODE -- REMOVE! Uses cursors...
	If KeyDown (203) TurnEntity box, 0, 0.5, 0
	If KeyDown (205) TurnEntity box, 0, -0.5, 0
	
End Function
