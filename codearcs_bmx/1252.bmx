; ID: 1252
; Author: rich41x
; Date: 2005-01-04 16:35:27
; Title: Bit Flags
; Description: Find if a bit can be found in a flag.

Repeat
	Print
	flag=Int(Input$("Flag:>"))
	bit=Int(Input$("Bit:>"))
	Select bitflag(flag,bit)
		Case 0
			Print "Bit "+bit+" has not been found in flag "+flag+"."
		Case 1
			Print "Bit "+bit+" has been found in flag "+flag+"."
	End Select
Forever



Function bitflag(flag,bit)
	p=0
	Repeat
		If 2^p>11 Then Exit
		p:+1
	Forever	
	p:-1	
	For i=p To 0 Step -1
		If flag-(2^i)>=0 Then
			flag:-(2^i)
			If 2^i=bit Then Return 1
		End If
	Next
	Return 0
End Function
