; ID: 574
; Author: Zenith
; Date: 2003-02-04 21:42:04
; Title: Base Conversion
; Description: Quick and easy base conversion function

Function BaseConv$(var$,power)
	Local hexa$="abcdef"
	lenhex=Len(var$)
	For x=0 To lenhex-1
		pnt$=Mid(var,lenhex-x,1)
		If pnt=>0 And pnt=<9
			v=pnt
		Else
			For i=0 To 5
				If Lower(pnt)=Mid(hexa,i,1)
					v=i+10
					Exit
				EndIf
			Next
		EndIf
		v=v*(power^x)
		n=n+v
	Next
	Return n
End Function
