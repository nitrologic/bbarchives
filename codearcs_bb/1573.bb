; ID: 1573
; Author: Valorden
; Date: 2005-12-23 08:14:42
; Title: Simple Gravity
; Description: Simple gravity effect for a platformer

Graphics 800, 600, 16, 2
SetBuffer BackBuffer()

Global X# = 400
Global Y# = 525

Global GRAVITY# = 4

Global JUMP = False

While Not KeyHit(1)

	Cls
	
		UPDATE_MOVEMENT()
	
	Flip
	
Wend
End

Function UPDATE_MOVEMENT()

	If KeyDown(57) = True				; INITIATE THE JUMP
		JUMP = True
	EndIf
	If KeyDown(203) = True
		X# = X# - 2
	EndIf
	If KeyDown(205) = True
		X# = X# + 2
	EndIf
	
	If JUMP = True 
		Y# = Y# - GRAVITY#
		GRAVITY# = GRAVITY# - .08		; ADJUST THIS TO CHANGE THE HEIGHT OF THE JUMP
		If Y# >= 524
			JUMP = False
			GRAVITY# = 4
		EndIf
	EndIf

	Oval X#, Y#, 25, 25, 0 				; BALL
	Rect 0, 550, 800, 50, 1				; FLOOR
	
	Text 0,0,"Press L and R arrow keys to move left and right"
	Text 0,10,"Press space bar to jump"

End Function
