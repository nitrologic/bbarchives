; ID: 1363
; Author: Cybersed
; Date: 2005-04-30 23:52:18
; Title: Hex$ to Decimal
; Description: Very compact function to convert an hex$ to a decimal

Function Dec(h$)

	t2$=Upper$(Trim$(h$))
	d=0
	For z=1 To Len(t2$)
		i=Instr("0123456789ABCDEF",Mid$(t2$,z,1))
		If i>0 Then d=d*16+i-1
	Next

	Return d

End Function
