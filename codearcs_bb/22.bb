; ID: 22
; Author: BlitzSupport
; Date: 2001-08-29 22:00:41
; Title: Wireframe x-ray target
; Description: Draws a small box onto the 3D scene, where everything inside the box is drawn in wireframe!

; --------------------------------------------------------------
; WireBox Demo... james @ hi - toro . com
; --------------------------------------------------------------
; Just plug right into your existing code to see it really rocking ;)
; --------------------------------------------------------------
; Use mouse to move wirebox...
; --------------------------------------------------------------

; --------------------------------------------------------------
; WireBox stuff...
; --------------------------------------------------------------

; x pos, y pos, width, height, image...
Global wireX, wireY, wireWidth, wireHeight, wireBox

; Call at start of main loop * after setting wireX and wireY *...
Function GetWireBox (width, height)
	WireFrame 1
	RenderWorld
	wireWidth = width: wireHeight = height
	wireBox = CreateImage (wireWidth, wireHeight)
	MaskImage wireBox, 255, 0, 255
	GrabImage wireBox, wireX, wireY
	WireFrame 0
End Function

; Call just before 'Flip'...
Function DrawWireBox ()
	If wireBox
		DrawImage wireBox, wireX, wireY		
		FreeImage wireBox
	EndIf
End Function

; --------------------------------------------------------------

AppTitle "Move mouse over cube..."
HidePointer ()

Graphics3D 640, 480
SetBuffer BackBuffer ()

cube = CreateCube ()

Global light = CreateLight ()
LightColor light, 255, 0, 0

cam = CreateCamera ()
MoveEntity cam, 0, 0.75, -5

Color 0, 255, 0 ; Just for on-screen 'target'...

Repeat

	; --------------------------------------------------------------
	; **** WireBox part 1, at start of main loop ****
	; --------------------------------------------------------------
	wireX = MouseX () - (wireWidth / 2): wireY = MouseY () - (wireHeight / 2)
	GetWireBox (64, 64)
	; --------------------------------------------------------------

	TurnEntity cube, 0, 0.5, 0
	
	UpdateWorld	
	RenderWorld

	; --------------------------------------------------------------
	; **** WireBox part 2, just before 'Flip' ****
	; --------------------------------------------------------------
	DrawWireBox ()
	; --------------------------------------------------------------

	; Quick target thing...
	Rect wireX - 1, wireY - 1, wireWidth + 2, wireHeight + 2, 0
	Line wireX + (wireWidth / 2) - 4, wireY + (wireHeight / 2), wireX + (wireWidth / 2) + 4, wireY + (wireHeight / 2)
	Line wireX + (wireWidth / 2), wireY + (wireHeight / 2) - 4, wireX + (wireWidth / 2), wireY + (wireHeight / 2) + 4

	Flip

Until KeyHit (1)

End
