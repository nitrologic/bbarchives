; ID: 3151
; Author: Pakz
; Date: 2014-10-18 13:46:46
; Title: Player Jumping Example
; Description: Jump with gravity

; Player jumping example by Rudy van Etten (Pakz)
;
Graphics 640,480,32,2
SetBuffer BackBuffer()

Global pw = 32 ; player width
Global ph = 32 ; player height
Global px# = GraphicsWidth() / 2 - pw/2 ; player x position
Global py# = 320-ph ; player y position
Global pmy# = 0 ; 
Global pj = False

While KeyDown(1) = False
	Cls
	updateplayer()
	drawplayer()
	Color 255,255,255
	Line 0,320,GraphicsWidth(),320
	Text 0,0,"pj:"+pj+", pmy:"+pmy
	Flip
Wend

Function updateplayer()
	; If the player is not currently in a jump and the key for jumping
	; is pressed then start the jump.
	If pj = False And KeyDown(57) = True 
		pmy = -3
		pj = True
	End If
	; If we are jumping
	If pj = True
		pmy = pmy + 0.1
		; if the player jump move variable is 0 or lower then zero (going up)
		If pmy =<0
			For y=0 To Abs(pmy) ; loop whole pixels the amount of the jump movement variable
				py = py - 1 ; go up
			Next
		End If
		; if the player jump move variable is bigger then 0 (going down)
		If pmy >0
			For y=0 To pmy
				py = py + 1
				If py > 320-ph ; if below the ground
					pj = False ; stop jumping code
					py = 320-ph; put player on the ground level
					Exit ; exit the for loop
				EndIf
			Next
		End If
	End If
End Function

Function drawplayer()
	Color 255,255,255
	Rect px,py,pw,ph,True
End Function
