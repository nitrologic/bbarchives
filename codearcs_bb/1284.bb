; ID: 1284
; Author: BlackJumper
; Date: 2005-02-08 16:11:17
; Title: Saving/Loading Types
; Description: Reload Str$() saved type

[code]
;------------------------------------------------------------------
;----   demo of saving and reloading game data using Str$()    ----
;----             Blackjumper  - Jan 2005                      ----
;------------------------------------------------------------------

Type test
	Field x
	Field y
	Field name$
End Type

For count = 1 To 4
	n.test = New test
	n\x = Rand(10)
	n\y = Rand(10)+100
	n\name$ = Chr(Rand(26)+65) +Chr(Rand(26)+65) +Chr(Rand(26)+65)
Next


fileout = WriteFile("C:\storedgame.txt")
For n.test = Each test
	Print Str$(n)
	WriteString (fileout, Str$(n))
Next
CloseFile( fileout ) 

Print "game data written to file... press any key to continue"
Print
WaitKey

Print "deleting all instances of type 'test'..."
For n.test = Each test
	Delete n
Next
Print "printing all type information...
Print "________________________________"
For n = Each test
	Print Str$(n)
Next
Print "--------------------------------"
WaitKey
Print


Print "... now reading from disk..."
filein = ReadFile("C:\storedgame.txt") 
While Not Eof(filein)
Read1$ = ReadString$( filein ) 
RestoreTestInfo(Read1$)
Wend

Print "printing all reloaded type information...
Print "------------------------"
For n.test = Each test
	Print Str$(n)
Next
Print "------------------------"

WaitKey
End

Function RestoreTestInfo( SavedString$ )
	Print "Read from file --> " + SavedString$
	SavedString$ = Mid$( SavedString$, 2, Len(SavedString$)-2) ; remove end square brackets
	
	firstcomma = Instr(SavedString$, ",")
	firstvalue% = Left$(SavedString$, firstcomma-1)   ; convert first value (up to comma) to an int
	
	SavedString$ = Mid$( SavedString$, firstcomma+1, Len(SavedString$)-firstcomma+1)  ; eat up to 1st comma

	firstcomma = Instr(SavedString$, ",")
	secondvalue% = Left$(SavedString$, firstcomma-1)  ; convert up to new first comma to another int
	
	SavedString$ = Mid$( SavedString$, firstcomma+1, Len(SavedString$)-firstcomma+1)  ; eat up to new 1st comma
	ThirdString$ = Mid$( SavedString$, 2, Len(SavedString$)-2)                        ; remove quotes from string

	reloaded.test = New test			; make a new type
	reloaded\x = firstvalue%			; and assign the
	reloaded\y = secondvalue%			; reloaded values
	reloaded\name$ = ThirdString$		; to the fields
	
End Function

[/code]
