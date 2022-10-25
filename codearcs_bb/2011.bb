; ID: 2011
; Author: Kev
; Date: 2007-05-14 07:35:17
; Title: word parse
; Description: parses passed string

show_Tokens("this is a line of text, using a simple lexer to display each word.")
Function show_Tokens(SLine$,identifier$=" ")
	;
	For loop = 1 To Len(SLine$)
		loop1 = loop - pos - 1 
		pos = Instr(SLine$,identifier$,loop)
		If pos > 0 Then
			Print Trim(Mid(SLine$,loop1+loop,pos-loop))
			loop = pos
		ElseIf(loop1+loop < Len(SLine$))
			Print Trim(Mid(SLine$,loop1+loop,pos-loop))
			Exit
		EndIf
	Next
End Function
