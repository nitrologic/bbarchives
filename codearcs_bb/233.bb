; ID: 233
; Author: Snarkbait
; Date: 2002-02-11 14:30:24
; Title: Binary string to integer
; Description: Converts a string created w/ bin$ back to an int.

Function bin2int(this$)
	thisint = 0
	blen = Len(this$)
	For a = 1 To blen
		ibit = Int(Mid$(this$,a,1))
		If ibit thisint = thisint + (ibit * (2^(blen - a)))
	Next
	Return thisint
End Function
