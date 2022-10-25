; ID: 2705
; Author: Warpy
; Date: 2010-04-24 03:05:52
; Title: Quick If
; Description: Like the ternary operator a ? b : c

Function qif$(p,a$,b$)
	If p Return a Else Return b
End Function


For c=1 To 4
	Print String(c)+" "+qif(c Mod 2 = 1, "odd","even")
Next
