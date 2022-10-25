; ID: 2657
; Author: vivaigiochi
; Date: 2010-03-03 05:17:41
; Title: urlencode for string
; Description: parse a string and remove %code

Function urlencode(htmlstringa$)
	begin=1
	
	pos=0
	
	numcar=Len(htmlstringa)
	
	finito=0
	
	
	While begin<numcar And finito=0
		
		trovato=Instr(htmlstringa,"%",begin)
		If trovato<>0 Then
			stringasx$=Mid(htmlstringa,1,trovato-1)
			stringadx$=Mid(htmlstringa,trovato+3,numcar)
			numesa$=Mid(htmlstringa,trovato+1,2)
			intero=hex2dec(numesa$)
			If intero<>-1 Then 
				htmlstringa=stringasx$+Chr$(intero)+stringadx$
				
			EndIf 
			begin=trovato+1
		Else
			finito=1
		EndIf
	Wend
End Function

Function hex2dec(hexin$)
	Local c, dec, hexval$ = "0123456789ABCDEF"
	For c=1 To Len(hexin$)
		dec = (dec Shl 4) Or (Instr(hexval$, Upper$(Mid$(hexin$, c, 1))) - 1)
	Next
	Return dec
End Function
