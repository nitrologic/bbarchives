; ID: 31
; Author: BlitzSupport
; Date: 2001-09-05 19:31:48
; Title: Commented 3D game framework (lite)
; Description: The basic framework of a NON frame-limited Blitz 3D game

; ------------------------------------------------------------------
; 	GameCore -- support@blitzbasic.com
; ------------------------------------------------------------------
; The basics of a frame-limited Blitz 3D game -- play about with it!
; ------------------------------------------------------------------

; ------------------------------------------------------------------
;	Open 3D display mode
; ------------------------------------------------------------------

Graphics3D 640, 480

; ------------------------------------------------------------------
; Single camera setup
; ------------------------------------------------------------------

cam = CreateCamera ()

; ------------------------------------------------------------------
; General setup -- load and arrange objects, textures, etc here...
; ------------------------------------------------------------------

; Create a cube and move it forward 5 'units', into view...
	
box = CreateCube ()
MoveEntity box, 0, 0, 5

Repeat
		
	If KeyDown (203) TurnEntity box, 0, 0.5, 0
	If KeyDown (205) TurnEntity box, 0, -0.5, 0

	; --------------------------------------------------------------
	; Draw 3D world
	; --------------------------------------------------------------

	RenderWorld

	; --------------------------------------------------------------
	; Show result
	; --------------------------------------------------------------
	
	Flip

Until KeyHit (1)

End
