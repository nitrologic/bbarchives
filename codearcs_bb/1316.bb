; ID: 1316
; Author: Rottbott
; Date: 2005-03-08 07:30:25
; Title: Power of two
; Description: Finds the next power of two down from a positive integer

; Returns the next power of two down from a positive integer
Function PowerOfTwo(N)

	; Optimise for smaller numbers
	If (N And $FFFF0000) <> 0
		Start = 31
	Else
		Start = 15
	EndIf

	; Find power
	For i = Start To 0 Step -1
		Number = 1 Shl i
		If (N And Number) = Number Then Return Number
	Next

	; N is 0 or less
	Return 0

End Function
