; ID: 1204
; Author: Rob Farley
; Date: 2004-11-23 07:17:04
; Title: Delimeted string handler
; Description: Find an element of a single delimeted string

; Entry function by Rob Farley 2004
; rob@mentalillusion.co.uk

Function Entry$(number,list$,delimeter$)

n=1
count = 1
found = False
start = 1

If number > 1
	Repeat
		If Mid(list,n,1)=delimeter
			count = count + 1
			If count = number
				found=True
				start = n + 1
				Exit
			EndIf
		EndIf
		n=n+1
	Until n >= Len(list)
	If found = False Then RuntimeError("List Element out of Range")
EndIf

Endof = Instr(list,delimeter,start)
If endof = 0 Then endof = Len(list)+1

Return Mid(list,start,endof-start)

End Function
