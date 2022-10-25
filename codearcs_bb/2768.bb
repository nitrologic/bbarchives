; ID: 2768
; Author: virtualjesus
; Date: 2010-09-16 16:16:34
; Title: NUMBER FORMAT FOR INTEGERS
; Description: format number very easy

Function Format$(Number$,Sep$=".")
	Local I%,Cadena$="",Cadena2$="",C%,Long%=Len(Number)
	For I=1 To Long
		C=C+1
		If C>3 Then C=1:Cadena=Cadena+Sep
		Cadena=Cadena+Mid$(Number,Len(Number)-I+1,1)
	Next
	Long=Len(Cadena)
	For I=1 To Long
		Cadena2=Cadena2+Mid$(Cadena,Long-I+1,1)
	Next
	Return Cadena2
End Function

Print "["+Format("1365454",".")+"]"
WaitMouse()
