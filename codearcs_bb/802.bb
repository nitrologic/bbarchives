; ID: 802
; Author: Ken Lynch
; Date: 2003-10-02 04:17:57
; Title: DataType Function
; Description: Checks the type of data stored in a string

;=================================================
;
; DataType Function
;
; (c)2003 Ken Lynch
;
;=================================================

;
; DataType(value$)
;
; Returns 0 if empty string, 1 if string, 2 if integer, 3 if float
;
Function DataType(value$)
	Local i, p$, c$, n$, dp, ex

	value$ = Trim(value$)
	If value$ = "" Then Return 0
	
	For i = 1 To Len(value$)
		If i > 1 Then p$ = Mid(value$, i-1, 1)
		c$ = Mid(value$, i, 1)
		n$ = Mid(value$, i+1, 1)
		
		If c$ = "." Then
			If dp = 1 Or Instr("0123456789", p$) = 0 Or Instr("0123456789", n$) = 0 Or p$ = "" Or n$ = "" Then Return 1
			dp = 1
			i = i + 1
		ElseIf c$ = "e" Then
			If ex = 1 Or Instr("0123456789", p$) = 0 Or Instr("0123456789+-", n$) = 0 Or p$ = "" Or n$ = "" Then Return 1
			ex = 1
			i = i + 1
		ElseIf Instr("+-", c$) > 0 Then
			If i > 1 Or Instr("0123456789", n$) = 0 Or n$ = "" Then Return 1
			i = i + 1
		ElseIf Instr("0123456789", c$) = 0 Then
			Return 1
		End If
	Next
	If dp = 1 or ex = 1 Then Return 3
	Return 2
End Function
