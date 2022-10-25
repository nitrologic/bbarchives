; ID: 1713
; Author: Zach3D
; Date: 2006-05-13 15:35:01
; Title: Timers
; Description: Simple timer include..

;=============================
;Timers
;=============================
Type Timer
Field Seconds#,Flagged,FlaggedTime#
Field SetOff
Field Active
End Type

Function UpdateTimers()
For A.timer = Each timer
If A\Active = True
If A\Flagged = 0
A\FlaggedTime# = MilliSecs() * 1000
A\Flagged = 1
Else
If MilliSecs() > A\FlaggedTime# + A\Seconds
A\SetOff = True
A\Active = False
A\Flagged = 0
EndIf
EndIf
EndIf
Next
End Function

Function NewTimer.timer(Seconds#)
T.timer = New timer
T\Seconds# = Seconds#
T\Flagged = 0
T\Active = 1
T\SetOff = 0
T\FlaggedTime# = 0.0
Return T
End Function

Function CheckTimer(TimerA.timer)
Return TimerA\SetOff
End Function

Function DeleteTimer(D.timer)
Delete D
End Function

;==============================================
;Here is code example for Timers
;==============================================
;
;
;Function BlastOff()
; A.timer = NewTimer(30)
; Text "Blast Off in 30 seconds..",400,400
;   Repeat
;     UpdateTimers()
;     If CheckTimer(A)
;       Text "BLAST OFF!!!!!",200,200
;     Endif
;   Until CheckTimer(A)
; DeleteTimer(A)
;End Function
;
;
;
;
;
