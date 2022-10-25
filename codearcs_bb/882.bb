; ID: 882
; Author: Jeremy Alessi
; Date: 2004-01-11 04:33:46
; Title: - Line Helpers -
; Description: - General y=mx+b type stuff -

;====== RETURNX ==========================================================
; == Use this to return an x# for any given y# and two other
; == points on a coordinate plane

Function ReturnedX#(y#, X1#, Y1#, X2#, Y2#)
		
	Return ( ( y# - YIntercept( X1#, Y1#, X2#, Y2# ) ) / Slope#( X1#, Y1#, X2#, Y2# ) )
	
End Function

;=========================================================================

;====== RETURNY ==========================================================
; == Use this to return a y# for any given x# and two other
; == points on a coordinate plane

Function ReturnedY#(x#, X1#, Y1#, X2#, Y2#)
	
	Return ( Slope#( X1#, Y1#, X2#, Y2# ) * x# + YIntercept( X1#, Y1#, X2#, Y2# ) )
	
End Function

;=========================================================================
	
;====== SLOPE ============================================================

Function Slope#(X1#, Y1#, X2#, Y2#)
	
	m# = ( ( Y2# - Y1# ) / ( X2# - X1# ) )

	If m#=0
		Return .001 ;avoid infinity
	Else
		Return m#
	EndIf
	
End Function

;=========================================================================

;====== YINTERCEPT =======================================================

Function YIntercept(X1#, Y1#, X2#, Y2#)

	Return ( (-Slope#( X1#, Y1#, X2#, Y2# ) * X1#) + Y1#)
	
End Function

;=========================================================================
