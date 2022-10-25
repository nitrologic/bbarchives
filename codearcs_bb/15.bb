; ID: 15
; Author: BlitzSupport
; Date: 2001-08-16 21:00:11
; Title: Diminish ()
; Description: Good routine for reducing speeds, power, etc.

Function Diminish# (value#, amount#)
	If Abs (value) <= Abs (amount) Then Return 0
	value = value - (amount * Sgn (value))
	Return value
End Function

; Example...

speed# = -100 ; 100

Repeat
    speed = Diminish (speed, 0.1)
    Cls: Locate 10, 10: Print speed: Flip
Until speed = 0

