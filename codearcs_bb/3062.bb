; ID: 3062
; Author: dna
; Date: 2013-06-28 19:43:23
; Title: Capitalization
; Description: Capitalizes the first letter of each word in a string of words.

Function CFLOW$(W$)
	While Instr(W$," ")
		C$=Upper$(Left$(W$,1)):V$=V$+C$+Mid$(W$,2,Instr(W$," ")-1):W$=Mid$(W$,Instr(W$," ")+1)
	Wend
	V$=V$+Upper$(Left$(W$,1))+Mid$(W$,2)
	Return V$
End Function
