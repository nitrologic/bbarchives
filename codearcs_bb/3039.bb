; ID: 3039
; Author: dna
; Date: 2013-03-15 20:39:47
; Title: Find Rhyming Words
; Description: Rhyme

Graphics 800,600,16,2:AppTitle"TEST"

S$="LUCK":I$="TRUCK"

V=Len(S$):W=Len(I$)
SR=Len(S$):LI=Len(I$)

If  LI>2 And SR>2
	If SR>=LI
		While LI>2
		    If Right$(S$,LI)=Right$(I$,LI) Text 50,50+(12*LI), I$:Exit
		    LI=LI-1
		Wend
	EndIf
	If SR<LI
		While SR>2
		    If Right$(S$,SR)=Right$(I$,SR) Text 150,50+(12*SR),I$:Exit
		    SR=SR-1
		Wend
	EndIf

EndIf


WaitKey:End
