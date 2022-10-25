; ID: 1427
; Author: Neochrome
; Date: 2005-07-22 10:43:48
; Title: Sorting Array BASIC
; Description: A Real Basic Sorting method

Dim Words$(1001)

words(0) = "CAT"
words(1) = "DOG"
words(2) = "MAN"
words(3) = "WOMAN"
words(4) = "TREE"
words(5) = "GRASS"
words(6) = "FOAM"
words(7) = "NEOMANCER"


; 5, 0, 3, 2, 1, 4

; Demo Sort Array

Print "NON SORTED :" 
Print ""
For i=0 To 1000
	If words(i)<>""	Print words(i)
Next

tmp$ = ""

For o=0 To 1000
For i=0 To 1000
	
	t1$ = words(i)
	t2$ = words(o+1)
	
	If t2$<>""
		res% = t1$ > t2$
	
		If res=1
			tmp$ = t1$
			words(i)=t2$
			words(o+1) = tmp$

		EndIf
	EndIf
Next	
If t2$="" Then o=1000
Next

Print "------------"
Print "SORTED : "
Print ""
For i=0 To 10
	If words(i)<>""	Print words(i)
Next
