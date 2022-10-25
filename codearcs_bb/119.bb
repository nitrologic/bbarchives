; ID: 119
; Author: Entity
; Date: 2001-11-01 05:23:54
; Title: Display_IsWindowed
; Description: Checks whether running in windowed mode or in full screen

;______________________________________________________________________________
; Display_IsWindowed()
; Invented by Jamie "Entity" van den Berge <entity@vapor.com>
;
; FUNCTION
;   Determines whether Blitz is currently in windowed or full screen mode.
;
; RESULT
;   True if running in windowed mode, otherwise False
;
Function Display_IsWindowed()
	Local x,y,x1,y1,x2,y2
	Local w = GraphicsWidth(), h = GraphicsHeight()

	; store old coords so we can set the mouse back to its old position
	; after the check
	x = MouseX(): y = MouseY()

	; now set the mouse at coordinates that can only be set in windowed mode
	; and see where we were ACTUALLY put.
	MoveMouse -1,-1: x1 = MouseX(): y1 = MouseY()
	MoveMouse  w, h: x2 = MouseX(): y2 = MouseY()

	; before we verify the coordinates, we fool blitz into thinking nothing
	; ever happened
	MoveMouse omx,omy: MouseXSpeed(): MouseYSpeed()

	; ok, now, if the pointer was placed where we set it, we MUST be in
	; windowed mode
	If (x1 = -1) Or (x2 = w) Then Return True
	If (y1 = -1) Or (y2 = h) Then Return True
	
	; if the mouse coordinates were clipped to the display size, we MUST be
	; in full screen mode, so ...
	If x1 = 0 And y1 = 0 And (x2 = w-1) And (y2 = h-1) Then Return False

	; in any other case we got bogus coords, usually when the window is positioned
	; partly outside the desktop, which obviously means we're in windowed mode
	Return True
End Function


