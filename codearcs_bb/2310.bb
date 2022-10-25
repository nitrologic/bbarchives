; ID: 2310
; Author: ShadowTurtle
; Date: 2008-09-07 13:21:27
; Title: matchPattern
; Description: compare a source String with a String expression

; ------------------------------------------------------------------------
; compare a source String with a String expression.
; 
; ** Syntax **
;   result = matchPattern(source_string$, expression_pattern$,
;     case_sensitivity)
; 
; ** Parameters **
;   source_string            Target String To compare pattern against.
;   expression_pattern       Wildcard expression pattern:
;   ?                        Any single character
;   *                        Zero Or more characters
;   #                        Any single digit (0-9)
;   case_sensitivity         Toggle for case sensitivity (true Or false).
;
; ** Return **
;   "True" or "False"
; ------------------------------------------------------------------------
Function matchPattern(stri$, pattern$, lCase = False)
	prevChar$ = ""
	nextChar$ = ""
	
	If lCase = True Then
		stri$ = stri$ + Chr(0)
		pattern$ = pattern$ + Chr(0)
	Else
		stri$ = Upper(stri$ + Chr(0))
		pattern$ = Upper(pattern$ + Chr(0))
	End If
	
	retBack = False
	
	x = 1
	y = 1
	
	Repeat
		If Mid(stri$, x, 1) = Chr(0) Then
			If Mid(pattern$, y, 1) = Chr(0) Then
				retBack = True
			End If
			Return retBack
		EndIf
		
		If Mid(pattern$, y, 1) = Chr(0) Then
			Return retBack
		ElseIf Mid(pattern$, y, 1) = Chr(35) Then
			x_asc = Asc(Mid(stri$, x, 1))
			If x_asc < 48 Or x_asc > 57 Then
				Return retBack
			End If
		ElseIf Mid(pattern$, y, 1) = Chr(42) Then
			y = y + 1
			Repeat
				If Mid(stri$, x, 1) = Mid(pattern$, y, 1) Then
					Exit
				ElseIf Mid(stri$, x, 1) = Chr(0) Then
					Exit
				End If
				x = x + 1
			Forever
			If Mid(stri$, x, 1) = Chr(0) Then
				If Mid(pattern$, y, 1) = Chr(0) Then
					retBack = True
				End If
				Return retBack
			End If
		ElseIf Mid(pattern$, y, 1) = Chr(63) Then
			; nothing to do, it's a match
		Else
			If Mid(pattern$, y, 1) <> Mid(stri$, x, 1) Then
				Exit
			End If
		End If
		
		x = x + 1
		y = y + 1
	Forever
End Function
