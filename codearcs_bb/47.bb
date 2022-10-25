; ID: 47
; Author: skidracer
; Date: 2001-09-20 22:11:27
; Title: FloatString
; Description: float to string conversion with n decimal points

Function FloatString$(f#,dp=2)
	a$=Int(f*(10^dp))
	l=Len(a$)
	If l<=dp a$=Right$("000000",dp+1-l)+a$
	Return Left$(a$,Len(a$)-dp)+"."+Right$(a$,dp)
End Function


