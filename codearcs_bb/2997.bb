; ID: 2997
; Author: Zethrax
; Date: 2012-11-02 06:18:34
; Title: DecodeHtmlEntities$() function
; Description: Function to convert encoded HTML entities in a string into their character counterpart.

Function DecodeHtmlEntities$( strval$ )
	; This function converts any encoded HTML characters in 'strval$' into their character couterparts
	; and returns the resulting unencoded string.

	; Not sure if this is the complete set of encoded entities, but it's good enough for my project.
	; I haven't bothered with the HTML entities that require a numeric code as they require unicode,
	; which I don't have a need to work with.

	; Reference Links:-
	; http://www.web2generators.com/html/entities

	Local pos = 1, endpos, old_pos, c$, html_char_code$, strlimit = Len( strval$ ) + 1, output$ = ""
	Repeat 
		old_pos = pos
		pos = Instr( strval$, "&", pos )
		If pos
			output$ = output$ + Mid( strval$, old_pos, pos - old_pos )
			pos = pos + 1
			endpos = Instr( strval$, ";", pos )
			If endpos
				html_char_code$ = Mid( strval$, pos, endpos - pos )
				Select html_char_code$
					Case "amp" : c$ = "&"
					Case "quot" : c$ = Chr(34)
					Case "apos" : c$ = "'"
					Case "lt" : c$ = "<"
					Case "gt" : c$ = ">"
					Case "nbsp" : c$ = " "
					Case "iexcl" : c$ = "�"
					Case "cent" : c$ = "�"
					Case "pound" : c$ = "�"
					Case "curren" : c$ = "�"
					Case "yen" : c$ = "�"
					Case "brvbar" : c$ = "�"
					Case "sect" : c$ = "�"
					Case "uml" : c$ = "�"
					Case "copy" : c$ = "�"
					Case "ordf" : c$ = "�"
					Case "laquo" : c$ = "�"
					Case "not" : c$ = "�"
					Case "shy" : c$ = Chr(173)
					Case "reg" : c$ = "�"
					Case "macr" : c$ = "�"
					Case "deg" : c$ = "�"
					Case "plusmn" : c$ = "�"
					Case "sup2" : c$ = "�"
					Case "sup3" : c$ = "�"
					Case "acute" : c$ = "�"
					Case "micro" : c$ = "�"
					Case "para" : c$ = "�"
					Case "middot" : c$ = "�"
					Case "cedil" : c$ = "�"
					Case "sup1" : c$ = "�"
					Case "ordm" : c$ = "�"
					Case "raquo" : c$ = "�"
					Case "frac14" : c$ = "�"
					Case "frac12" : c$ = "�"
					Case "frac34" : c$ = "�"
					Case "iquest" : c$ = "�"
					Case "times" : c$ = "�"
					Case "divide" : c$ = "�"
					Case "Agrave" : c$ = "�"
					Case "Aacute" : c$ = "�"
					Case "Acirc" : c$ = "�"
					Case "Atilde" : c$ = "�"
					Case "Auml" : c$ = "�"
					Case "Aring" : c$ = "�"
					Case "AElig" : c$ = "�"
					Case "Ccedil" : c$ = "�"
					Case "Egrave" : c$ = "�"
					Case "Eacute" : c$ = "�"
					Case "Ecirc" : c$ = "�"
					Case "Euml" : c$ = "�"
					Case "Igrave" : c$ = "�"
					Case "Iacute" : c$ = "�"
					Case "Icirc" : c$ = "�"
					Case "Iuml" : c$ = "�"
					Case "ETH" : c$ = "�"
					Case "Ntilde" : c$ = "�"
					Case "Ograve" : c$ = "�"
					Case "Oacute" : c$ = "�"
					Case "Ocirc" : c$ = "�"
					Case "Otilde" : c$ = "�"
					Case "Ouml" : c$ = "�"
					Case "Oslash" : c$ = "�"
					Case "Ugrave" : c$ = "�"
					Case "Uacute" : c$ = "�"
					Case "Ucirc" : c$ = "�"
					Case "Uuml" : c$ = "�"
					Case "Yacute" : c$ = "�"
					Case "THORN" : c$ = "�"
					Case "szlig" : c$ = "�"
					Case "agrave" : c$ = "�"
					Case "aacute" : c$ = "�"
					Case "acirc" : c$ = "�"
					Case "atilde" : c$ = "�"
					Case "auml" : c$ = "�"
					Case "aring" : c$ = "�"
					Case "aelig" : c$ = "�"
					Case "ccedil" : c$ = "�"
					Case "egrave" : c$ = "�"
					Case "eacute" : c$ = "�"
					Case "ecirc" : c$ = "�"
					Case "euml" : c$ = "�"
					Case "igrave" : c$ = "�"
					Case "iacute" : c$ = "�"
					Case "icirc" : c$ = "�"
					Case "iuml" : c$ = "�"
					Case "eth" : c$ = "�"
					Case "ntilde" : c$ = "�"
					Case "ograve" : c$ = "�"
					Case "oacute" : c$ = "�"
					Case "ocirc" : c$ = "�"
					Case "otilde" : c$ = "�"
					Case "ouml" : c$ = "�"
					Case "oslash" : c$ = "�"
					Case "ugrave" : c$ = "�"
					Case "uacute" : c$ = "�"
					Case "ucirc" : c$ = "�"
					Case "uuml" : c$ = "�"
					Case "yacute" : c$ = "�"
					Case "thorn" : c$ = "�"
					Case "yuml" : c$ = "�"
					Default 
						c$ = ""
				End Select 
				output$ = output$ + c$
				pos = endpos + 1 
			Else
				Exit
			EndIf
		Else
			output$ = output$ + Right( strval$, strlimit - old_pos )
			Exit
		EndIf
	Forever
	Return output$
End Function


; *** DEMO ***


Graphics 1024, 768, 0, 2

Print DecodeHtmlEntities$( "<Harry Potter � PERSONALISED Hogwarts Acceptance Letter & ticket - Christmas Gift>" )	

WaitKey : End
