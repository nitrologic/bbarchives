; ID: 3070
; Author: Zethrax
; Date: 2013-08-27 03:31:43
; Title: SaveString - saves a string value as a file
; Description: Saves the specified string value as a file.

Function SaveString( filepath$, stringval$ )
	; Saves the 'stringval$' to the specified 'filepath$' as a file.
	; Returns True on success or False if there was an error.
	
	Local file, i, l = Len( stringval$ )
	file = WriteFile( filepath$ )
	If file = 0 Then Return False
	For i = 1 To l
		WriteByte file, Asc( Mid( stringval$, i, 1 ) )
	Next
	CloseFile file
	Return True
End Function
