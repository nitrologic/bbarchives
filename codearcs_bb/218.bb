; ID: 218
; Author: Zenith(Matt Griffith)
; Date: 2002-02-02 14:33:37
; Title: Hex 2 String
; Description: Converts a Hexidecimal to a String

; Example:

stuff$="0xF0000F"
Print "hex: " +stuff

Print Hex2Str$(stuff)

WaitKey

; Here's the function

Function Hex2Str$(var$)
	local hexa$[5]
	hexa[0]="A"
	hexa[1]="B"
	hexa[2]="C"
	hexa[3]="D"
	hexa[4]="E"
	hexa[5]="F"

	lenhex=Len(var$)
	For x=0 To lenhex-3
	
		pnt$=Mid(var,lenhex-x,1)
		
		If pnt=>0 And pnt=<9
			v=pnt
		Else
		
			For i=0 To 5
				If pnt=hexa[i]
					v=i+10
					Exit
				EndIf
			Next
		
		EndIf
		
		v=v*(16^x)		
		n=n+v
	
	Next
	
	Return n

End Function
