; ID: 1357
; Author: Andres
; Date: 2005-04-21 15:40:55
; Title: Web compatible URL
; Description: Convert an URL to HTTP protocol compatible one

Function WebCompatible$(address$)
	For i = 1 To Len(address$)
		char = Asc(Mid$(address$, i, 1))
		
		If Encode(char)
			result$ = result$ + "%" + Right$(Hex(char), 2)
		Else
			result$ = result$ + Chr(char)
		EndIf
	Next
	Return result$
End Function

Function Encode(code)
	If code =< 31 Or code => 127 Then Return True
	Select code
		Case 36, 38, 43, 44, 47, 58, 59, 61, 63, 64, 32, 91, 93
			Return True
		Case 34, 60, 35, 37, 123, 125, 124, 92, 94, 126, 96
			Return True
		Default
			Return False
	End Select
End Function
