; ID: 2785
; Author: Malice
; Date: 2010-11-18 08:47:35
; Title: Simple Pause Game
; Description: Allows the user to use Pause/Break key to Pause/Unpause the game

;INITIALISATION
Global b_gam_PAUSESTATE_PAUSED=(Not(GetKeyState(19)))

; FUNCTIONS
Function b_cnt_Paused() ;Allows Pause/Break key to toggle Pause mode and returns True if Paused.
	; Ensure v_cnt_InitialisePause() is called at program initialisation if you use this function!
	Return (GetKeyState(19)=b_gam_PAUSESTATE_PAUSED)	;$13 = VK_PAUSE
End Function

; EXAMPLE:
While Not KeyDown(1)
	Cls
	FlushKeys()
	If (b_cnt_Paused())
		Text 0,0,("Game is paused")
		Else Text 0,0, ("Game is not paused")
	End If
	Flip
Wend
