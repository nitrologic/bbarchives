; ID: 614
; Author: Ratboy
; Date: 2003-03-05 16:10:48
; Title: SpeedLimit
; Description: Keep your ships from going too fast

;^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
; FUNCTION SpeedLimit()
; returns a multiplier to reduce Xv & Yv by
; to keep mover speeds from going through the
; roof
;^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Function SpeedLimit#(SpeedX#,SpeedY#,TopSpeed#)

	Vector = Sqr(SpeedX * SpeedX + SpeedY * SpeedY)
	If Vector > TopSpeed Then
		Limiter# = TopSpeed / Vector
	Else
		Limiter# = 1
	EndIf
	
	Return Limiter

End Function
