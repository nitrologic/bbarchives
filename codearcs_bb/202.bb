; ID: 202
; Author: Jim Teeuwen
; Date: 2002-01-24 21:20:29
; Title: VB InstrRev() command
; Description: Works as Instr() command, but starts search from the end of the string

;// Usage: InStrRev(String$, Substring$[, Start%])

;// String: The source in wich to look
;// Substring: The string for wich to look
;// Start(optional): The numeric position, counted from the left,
;// which defines where to start the search for the substring. 

;// ### EXAMPLE ##################################
mystring$=InstrRev("c:\blitz3D\blitz3d.exe","\",1)
print "The filename is: "+mystring$

;// output
The filename is: blitz3d.exe

;// ### THE GOODS ################################

Function InstrRev$(sT$,sS$,index=0)
	While (Instr(sT$,sS$)>0)
		If Instr(sT$,sS$)>0 Then
			sT$=Mid$(sT$,Instr(sT$,sS$)+1)
		EndIf
	Wend
	If index=0 Then sT$=sS$+sT$
	Return sT$
End Function
