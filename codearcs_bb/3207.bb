; ID: 3207
; Author: dna
; Date: 2015-05-23 15:46:34
; Title: Words into Array
; Description: Chops up a sentence into individual words while placing them into an array

Dim a$(20):E$="word1 word2 word3"
IN=Instr(E$," ")
While IN>0
	X=X+1:A$(X)=Left$(E$,IN):E$=Mid$(E$,IN+1):IN=Instr(E$," ")
Wend
X=X+1:A$(X)=E$
For G=1 To X:Print A$(G):Next
WaitKey:End
