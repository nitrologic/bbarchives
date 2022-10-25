; ID: 587
; Author: Snarty
; Date: 2003-02-13 21:06:07
; Title: WaitTimer()
; Description: Replacement for the Blitz2D command

Function WaitTimer()

	Repeat:Until WaitEvent()=$4001

End Function
