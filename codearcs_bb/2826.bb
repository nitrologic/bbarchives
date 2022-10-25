; ID: 2826
; Author: Zethrax
; Date: 2011-02-23 22:53:45
; Title: FindLastString function
; Description: This function finds the position of the last occurrence of one string inside another string.

Function FindLastString( haystack$, needle$, is_case_sensitive = True )
	; This function finds the position of the last occurrence of
	; the 'needle$' string inside the 'haystack$' string.
	
	; If the 'needle$' string is not found inside the 'haystack$' string
	; then a zero value will be returned.
	
	; Note that the function is case sensitive by default.
	; Specify 'False' for the 'is_case_sensitive' parameter;
	; for a case insensitive check.
	
	Local offset = 1, lastpos
	
	If is_case_sensitive = False
		haystack$ = Lower$( haystack$ )
		needle$ = Lower$( needle$ )	
	EndIf
	
	Repeat
	
		offset = Instr ( haystack$, needle$, offset )
		
		If offset
			lastpos = offset
			offset = offset + Len( needle$ )
		EndIf
		
	Until offset = 0
	
	Return lastpos
End Function


; EXAMPLE USAGE


Print "Case Sensitive: " + FindLastString( "sABabcABadabABab", "AB" )
Print
Print "Not Case Sensitive: " + FindLastString( "sABabcABadabABab", "AB", False )

WaitKey : End
