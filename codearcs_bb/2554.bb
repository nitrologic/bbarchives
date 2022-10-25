; ID: 2554
; Author: Sauer
; Date: 2009-08-04 20:53:09
; Title: Integer to Base 36
; Description: Converts Integers to Base 36 and back

Function int_to_base36$(value)
	conv$=""
	While value
		remainder=value Mod 36
		value=value/36
		If remainder<10
			conv$=conv$+Chr$(48+remainder)
		Else
			conv$=conv$+Chr$(55+remainder)
		EndIf
	Wend
	
	tmp$=""
	For x=0 To Len(conv$)
		tmp$=tmp$+Mid$(conv$,Len(conv$)-x,1)
	Next
	conv$=tmp$

	If conv$<>""
		Return conv$
	Else
		Return "0"
	EndIf
End Function 

Function base36_to_int(value$)
	conv=0
	For x=1 To Len(value$)
		ascii=Asc(Mid$(value$,x,1))
		If ascii>57
			ascii=ascii-7
		EndIf
		ascii=ascii-48
		conv=conv+(ascii)*36^(Len(value$)-x)
	Next
	Return conv
End Function
