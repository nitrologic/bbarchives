; ID: 10
; Author: BlitzSupport
; Date: 2001-08-29 18:03:41
; Title: Asynchronous timers
; Description: Timers that don't stop your game from continuing

; Timer structure:

Type Timer
	Field start
	Field timeOut
End Type

; Set a timer:

Function SetTimer.Timer (timeOut)
	t.Timer = New Timer
	t\start   = MilliSecs ()
	t\timeOut = t\start + timeOut
	Return t
End Function

; Check for timeout:

Function TimeOut (test.Timer)
	If test <> Null
		If test\timeOut < MilliSecs ()
			Delete test
			Return 1
		EndIf
	EndIf
End Function

Graphics 640, 480
SetBuffer BackBuffer ()

; Set timer before main loop:
t.Timer = SetTimer (1000)

Repeat

	Cls

	; If timer runs out, add another "o" and reset the timer:
	If TimeOut (t)
		a$ = a$ + "o"
		t = SetTimer (1000)
	EndIf
	
    ; This stuff carries on regardless:
	Rect MouseX (), MouseY (), 20, 20
	Text 0, 0, a$
	
	Flip
	
Until KeyHit (1)

End
