; ID: 2991
; Author: Zethrax
; Date: 2012-10-26 06:07:57
; Title: SplitString function - Splits a string into fields and returns it in an array
; Description: This function takes a string value and splits it into fields using a delimiter string. The resulting fields are returned in an array.

Dim A_string$( 0 )

Function SplitString( stringval$, div$, max_elements = 0 )

	; Splits the specified stringval$ into fields using the div$
	; string as the delimiter. The resulting string fields are
	; placed into the A_string$() array.
	
	; The div$ string can contain multiple characters. If it is
	; empty then the array will have one element which will
	; contain the entire string.
	
	; The index of the last array slot is returned by the function.
	
	; The max_elements parameter can be used to set a limit on
	; the number of fields the string is split into. For no
	; element limits, use a zero value for max_elements (default).
	
	; When max_elements or the end of the string is reached, the
	; remainder of the string is returned in the last array element.
	; The returned array will contain at least one element even if
	; there is no divider string found.
	
	If div$ = ""
		Dim A_string$( 0 )
		A_string$( 0 ) = stringval$
		Return 0
	EndIf

	Local divlen = Len( div$ )
	Local x = 0, startpos = 1, divpos = 1

	Repeat
		divpos = Instr( stringval$, div$, divpos )
		If divpos
			x = x + 1
			divpos = divpos + divlen
		EndIf
	Until divpos = 0

	If max_elements
		If max_elements < x Then x = max_elements
	EndIf

	Dim A_string$( x )

	max_elements = max_elements - 1
 
	x = 0

	While x <> max_elements
		divpos = Instr( stringval$, div$, startpos )
		If divpos = 0 Then Exit
		A_string$(x) = Mid( stringval$, startpos, divpos - startpos )
		startpos = divpos + divlen
		x = x + 1
	Wend
	A_string$(x) = Right( stringval$, Len( stringval$ ) - startpos + 1 )
	Return x
End Function

; *** EXAMPLE CODE ***

max_index = SplitString( "aa<!>bb<!>cc<!>dd<!>ee<!>", "<!>", 0 )

Print "Highest Index: " + max_index
Print

For i = 0 To max_index
	Print "["+A_string$( i )+"]"
Next

Print
Print "Press any key to exit."
WaitKey
End
