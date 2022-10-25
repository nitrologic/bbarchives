; ID: 686
; Author: sswift
; Date: 2003-05-13 00:08:47
; Title: GetWord$()
; Description: This function finds "word" number X within a string.

Print GetWord$(" ==Suck= it =down, ====monkeyf*ck!", 4, " =")


; -------------------------------------------------------------------------------------------------------------------
; This function gets a specific "word" from a string.
;
; A word is defined as a string of characters seperated by one or more seperator characters.
; The function defaults to using space as the seperator but you can make any character the seperator, and can even
; pass multiple seperators at the same time to the function like " =" so that all combinations of spaces and equal
; signs are treated as seperator characters.
;
; If you specify a word which is higher than the total number of words in the string then an empty string is
; returned.  
; -------------------------------------------------------------------------------------------------------------------
Function GetWord$(InputString$, WordNum, Seperators$=" ")

	FoundWord  = False
	WordsFound = 0

	; Loop through each character in the input string.
	For CharLoop = 1 To Len(InputString$)

		; Get the character at this location in the string.
		ThisChar$ = Mid$(InputString$, CharLoop, 1)

		; If the character at this position is one of the characters in the seperator list...
		If Instr(Seperators$, ThisChar$, 1)
		
			; If a word has been started...
			If FoundWord
		
				; ...then this character must mark the end of a word.

				; Increment the number of words we've found.
				WordsFound = WordsFound + 1

				; Is this word the word we want?
				If WordsFound = WordNum
				
					; Yes!  Exit the function and return the word.
					Return Word$
			
				Else
				
					; No.  Discard this word.
					Word$ = ""
					FoundWord = False
				
				EndIf
				
			Else
			
				; Ignore this character.  We have either not reached a word yet, or are between words.
			
			EndIf				
					
		Else
		
			; This is not a character in our seperator list.  Add it to our word.
			FoundWord = True
			Word$ = Word$ + ThisChar$
			
		EndIf
		
	Next	
		
	; We have finished looking through the string.  Was the last word we were on the one we were looking for?
	If (WordsFound+1) = WordNum

		; Yes! 
		; Return the word that at the end of the string which didn't have any seperators after it.
		Return Word$

	Else
	
		; No. 
		; The word number passed to the function was greater than the number of words in the string. 
		; Return an empty string.
		Return ""

	EndIf
	
End Function
