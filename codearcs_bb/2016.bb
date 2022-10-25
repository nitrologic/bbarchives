; ID: 2016
; Author: Diego
; Date: 2007-05-17 08:57:37
; Title: Urlencode / Urldecode
; Description: Url encoder / decoder based on skn3[ac]s algorithm

Const HexValues$ = "0123456789ABCDEF"

Function UrlEncode$(Url$)
Local RetVal$ = ""
For I% = 1 To Len(Url$)
	Char% = Asc(Mid(Url$, I%, 1))
	If (Char% >= 48 And Char% <= 57) Or (Char% >= 65 And Char% <= 90) Or (Char% >= 97 And Char% <= 122) Or Char% = 43 Or Char% = 45 Or Char% = 46 Or Char% = 95
		RetVal$ = RetVal$ + Chr(Char%)
		Else
		RetVal$ = RetVal$ + "%" + Right(Hex(Char%), 2)
		End If
	Next
Return RetVal$
End Function

Function UrlDecode$(Url$)
Local RetVal$ = ""
For I% = 1 To Len(Url$)
	If Mid(Url$, I%, 1) = "%" Then
		RetVal$ = RetVal$ + Chr(Instr(HexValues$, Mid(Url$, I% + 1, 1)) Shl 4 + Instr(HexValues$, Mid(Url$, I% + 2, 1)) - 17)
		I% = I% + 2
		Else
		RetVal$ = RetVal$ + Mid(Url$, I%, 1)
		End If
	Next
Return RetVal$
End Function
