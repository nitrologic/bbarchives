; ID: 2913
; Author: Zethrax
; Date: 2012-01-30 04:03:56
; Title: Function to capitalize the first letter of each word in a string
; Description: Capitalizes the first character in each word in a text string

Function Capitalize$( t$ )

	; This function will accept a case insensitive string
	; and return the string with the first character in each word
	; capitalized, and the rest of the characters converted to lower-case.
	
	t$ = Lower( t$ )
	Local l = Len( t$ )
	Local c$ = "", r$ = "", old_c$, i
		
	For i = 1 To l
		old_c$ = c$
		c$ = Mid( t$, i, 1 )
		If ( i = 1 ) Or ( old_c$ = " " ) Then c$ = Upper( c$ )
		r$ = r$ + c$
	Next
	
	Return r$
	
End Function

; == EXAMPLE CODE ==

Print Capitalize( "the cat sat on the mat" )

WaitKey : End
