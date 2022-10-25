; ID: 181
; Author: mrtricks
; Date: 2002-01-04 19:46:05
; Title: Alphabetical Sort
; Description: Sort any number of strings into alphabetical order

;ALPHABETICAL SORT BY ROBIN KING 5/1/2002
;
;type in as many phrases/words as you like, then hit return with an empty input to sort them all


Type sort
	Field name$
End Type

;INPUT SOME TEXTS
Repeat
	in$=Input()
	If in$<>""
		s.sort=New sort
		s\name$=Lower$(in$)
	EndIf
Until in$=""

;PRINT THEM IN THE ORDER YOU TYPED THEM
Print
For s.sort=Each sort
Print s\name$
Next
Print
Print





;SORT

s.sort=First sort
Repeat
	If s=Null Then Exit
	a$=s\name$
	s.sort=After s
	If s=Null Then Exit
	b$=s\name$
	If a$>b$
		Insert s Before Before s
		If s.sort<>First sort Then s=Before s
	EndIf
Forever		





;PRINT THEM IN SORTED ORDER
For s.sort=Each sort
Print s\name$
Next

Repeat
Until KeyDown(1)
End
