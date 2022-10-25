; ID: 2825
; Author: Zethrax
; Date: 2011-02-23 22:06:19
; Title: CountString function
; Description: Returns the number of occurences of a sub-string within a larger string.

Function CountString( haystack$, needle$, is_case_sensitive = True )
	; This function counts how many times the 'needle$' string
	; occurs inside the 'haystack$' string.
	
	; Note that the function is case sensitive by default.
	; Specify 'False' for the 'is_case_sensitive' parameter;
	; for a case insensitive check.
	
	Local offset = 1, count
	
	If is_case_sensitive = False
		haystack$ = Lower$( haystack$ )
		needle$ = Lower$( needle$ )	
	EndIf
	
	Repeat
		offset = Instr ( haystack$, needle$, offset )
		
		If offset
			count = count + 1
			offset = offset + Len( needle$ )
		EndIf
		
	Until offset = 0
	
	Return count
End Function


; EXAMPLE USAGE


Print "Case Sensitive: " + CountString( "sABabcadadabAB", "AB" )
Print
Print "Not Case Sensitive: " + CountString( "sABabcadadabAB", "AB", False )

WaitKey : End
