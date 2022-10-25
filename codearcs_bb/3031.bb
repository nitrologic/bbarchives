; ID: 3031
; Author: misth
; Date: 2013-02-17 04:53:12
; Title: GetWord() &amp; CountWords()
; Description: Functions for easy and accurate parsing.

; ***************
; ** Functions **

; GetWord$()
;  in$ = Input text where you want your word from
;  pos% = Position, or index number which word you want
;  sep$ = Separator. Can be anything. Input text is split with this. (Default = space)
Function GetWord$(in$, pos%, sep$ = " ")
	Local i%, sepPos%
	Local l% = Len(sep) ; Separators length
	
	For i = 2 To pos ; Go through every unwanted words
		sepPos = Instr(in, sep) ; Is separator found?
		If sepPos Then ;  Yes, it is.
			in = Mid(in, sepPos + l) ; Remove unwanted text all the way to first separator
		EndIf
	Next
	
	sepPos = Instr(in, sep) ;  Let's see again if we find separator
	
	If sepPos Then ; If found...
		Return Mid(in, 1, sepPos - 1) ; ...Return text without the next separator
	Else ; If not found...
		Return Mid(in, 1) ; Return the remainder
	EndIf
	
End Function

; CountWords$()
;  in$ = Input text where you want to count the words from
;  sep$ = Separator. Can be anything. Input text is split with this. (Default = space)
Function CountWords%(in$, sep$ = " ")
	Local count% = 0, sepPos%
	
	Repeat
		count = count + 1 ; Increase 'count' for how many words we've found
		sepPos = Instr(in, sep, sepPos + 1) ; Any separators?
		If Not sepPos Then ;  Nope.
			Exit ; Ok, let's just go out
		EndIf
	Forever
	Return count ; Return the word count
End Function


; *************
; ** EXAMPLE **

Local input_$ = "I am a sentence.|You can split me any way you want."
Local i%

; Split our 'input_' with "|" character
Print GetWord(input_, 1, "|")
Print GetWord(input_, 2, "|")
Print " "

; Split our 'input_' by spaces and print every word.
For i=1 To CountWords(input_," ")
	Print GetWord(input_, i, " ")
Next

End ; BYE!

; ****************
; ** THE OUTPUT **

; I am a sentence.
; You can split me any way you want.
;
; I
; am
; a
; sentence.|You
; can
; split
; me
; any
; way
; you
; want.
