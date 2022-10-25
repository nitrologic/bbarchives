; ID: 1940
; Author: ninjarat
; Date: 2007-03-05 12:07:34
; Title: Hex2$(), Bin2$()
; Description: Allows you to select the number of digits to return.

Function Bin2$(val:Long,digits=32)
	Local buf:Short[digits]
	For Local k=digits-1 To 0 Step -1
		buf[k]=(val&1)+Asc("0")
		val:Shr 1
	Next
	Return String.FromShorts(buf,digits)
End Function

Function Hex2$(val:Long,digits=8)
	Local buf:Short[digits]
	For Local k=digits-1 To 0 Step -1
		Local n=(val&15)+Asc("0")
		If n>Asc("9") n=n+(Asc("A")-Asc("9")-1)
		buf[k]=n
		val:Shr 4
	Next
	Return String.FromShorts(buf,digits)
End Function
