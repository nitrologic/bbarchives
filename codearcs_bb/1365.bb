; ID: 1365
; Author: Andres
; Date: 2005-05-03 03:23:10
; Title: Sectors in string
; Description: Retrieve easily a sector from within a string

Function Sector$(txt$, separator$, sector%)
	Local result$ = "", occ
	For i = 1 To Len(txt$)
		If Mid$(txt$, i, 1) = separator$
			occ = occ + 1
		Else
			If occ = sector Then result$ = result$ + Mid$(txt$, i, 1)
		EndIf
		If occ > sector Then Exit
	Next
	Return result$
End Function
