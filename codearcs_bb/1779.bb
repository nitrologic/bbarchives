; ID: 1779
; Author: Andres
; Date: 2006-08-06 06:41:20
; Title: UnHex/reHex
; Description: Get a hex value out of int and reverse

Function ReHex$(value%)
	Return Right$(Hex(value%), 2)
End Function

Function UnHex%(txt$)
	txt$ = Upper(txt$)
	
	Local a = Asc(Mid$(txt$, 1, 1)) - 48
	If a > 10 Then a = a - 7
	Local b = Asc(Mid$(txt$, 2, 1)) - 48
	If b > 10 Then b = b - 7
	
	Return a * 16 + b
End Function
