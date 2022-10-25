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
					Case "iexcl" : c$ = "¡"
					Case "cent" : c$ = "¢"
					Case "pound" : c$ = "£"
					Case "curren" : c$ = "¤"
					Case "yen" : c$ = "¥"
					Case "brvbar" : c$ = "¦"
					Case "sect" : c$ = "§"
					Case "uml" : c$ = "¨"
					Case "copy" : c$ = "©"
					Case "ordf" : c$ = "ª"
					Case "laquo" : c$ = "«"
					Case "not" : c$ = "¬"
					Case "shy" : c$ = Chr(173)
					Case "reg" : c$ = "®"
					Case "macr" : c$ = "¯"
					Case "deg" : c$ = "°"
					Case "plusmn" : c$ = "±"
					Case "sup2" : c$ = "²"
					Case "sup3" : c$ = "³"
					Case "acute" : c$ = "´"
					Case "micro" : c$ = "µ"
					Case "para" : c$ = "¶"
					Case "middot" : c$ = "·"
					Case "cedil" : c$ = "¸"
					Case "sup1" : c$ = "¹"
					Case "ordm" : c$ = "º"
					Case "raquo" : c$ = "»"
					Case "frac14" : c$ = "¼"
					Case "frac12" : c$ = "½"
					Case "frac34" : c$ = "¾"
					Case "iquest" : c$ = "¿"
					Case "times" : c$ = "×"
					Case "divide" : c$ = "÷"
					Case "Agrave" : c$ = "À"
					Case "Aacute" : c$ = "Á"
					Case "Acirc" : c$ = "Â"
					Case "Atilde" : c$ = "Ã"
					Case "Auml" : c$ = "Ä"
					Case "Aring" : c$ = "Å"
					Case "AElig" : c$ = "Æ"
					Case "Ccedil" : c$ = "Ç"
					Case "Egrave" : c$ = "È"
					Case "Eacute" : c$ = "É"
					Case "Ecirc" : c$ = "Ê"
					Case "Euml" : c$ = "Ë"
					Case "Igrave" : c$ = "Ì"
					Case "Iacute" : c$ = "Í"
					Case "Icirc" : c$ = "Î"
					Case "Iuml" : c$ = "Ï"
					Case "ETH" : c$ = "Ð"
					Case "Ntilde" : c$ = "Ñ"
					Case "Ograve" : c$ = "Ò"
					Case "Oacute" : c$ = "Ó"
					Case "Ocirc" : c$ = "Ô"
					Case "Otilde" : c$ = "Õ"
					Case "Ouml" : c$ = "Ö"
					Case "Oslash" : c$ = "Ø"
					Case "Ugrave" : c$ = "Ù"
					Case "Uacute" : c$ = "Ú"
					Case "Ucirc" : c$ = "Û"
					Case "Uuml" : c$ = "Ü"
					Case "Yacute" : c$ = "Ý"
					Case "THORN" : c$ = "Þ"
					Case "szlig" : c$ = "ß"
					Case "agrave" : c$ = "à"
					Case "aacute" : c$ = "á"
					Case "acirc" : c$ = "â"
					Case "atilde" : c$ = "ã"
					Case "auml" : c$ = "ä"
					Case "aring" : c$ = "å"
					Case "aelig" : c$ = "æ"
					Case "ccedil" : c$ = "ç"
					Case "egrave" : c$ = "è"
					Case "eacute" : c$ = "é"
					Case "ecirc" : c$ = "ê"
					Case "euml" : c$ = "ë"
					Case "igrave" : c$ = "ì"
					Case "iacute" : c$ = "í"
					Case "icirc" : c$ = "î"
					Case "iuml" : c$ = "ï"
					Case "eth" : c$ = "ð"
					Case "ntilde" : c$ = "ñ"
					Case "ograve" : c$ = "ò"
					Case "oacute" : c$ = "ó"
					Case "ocirc" : c$ = "ô"
					Case "otilde" : c$ = "õ"
					Case "ouml" : c$ = "ö"
					Case "oslash" : c$ = "ø"
					Case "ugrave" : c$ = "ù"
					Case "uacute" : c$ = "ú"
					Case "ucirc" : c$ = "û"
					Case "uuml" : c$ = "ü"
					Case "yacute" : c$ = "ý"
					Case "thorn" : c$ = "þ"
					Case "yuml" : c$ = "ÿ"
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

Print DecodeHtmlEntities$( "<Harry Potter ¦ PERSONALISED Hogwarts Acceptance Letter & ticket - Christmas Gift>" )	

WaitKey : End
