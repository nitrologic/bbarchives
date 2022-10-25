; ID: 2993
; Author: Zethrax
; Date: 2012-10-29 06:00:29
; Title: EncodeURLValue() - Function used to encode the value parameter in a URL's query string.
; Description: By default the function encodes everything except 0 to 9, A to Z, a to z, and the characters: -_.~ and !'()* The last set will also be encoded if 'strict' is set to True.

Function EncodeURLValue$( value$, strict = False )
	; This function is used to encode the value parameter in a URL's query string.
	
	; eg. For the URL: https://www.google.com/search?client=opera
	; You would use the function to encode the 'opera' part if you suspected
	; that it contained illegal characters.
	
	; The 'value$' parameter should hold the string value to be encoded.
	; The encoded version of the value string is returned.
	
	; By default the function encodes everything except 0 to 9, A to Z, a to z,
	; and the characters: -_.~ and !'()*
	
	; The optional 'strict' parameter can be set to True if you also want to
	; encode the characters: !'()*
	; These characters don't do any harm, but aren't strictly allowed.

	; Reference Links:-
	; https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/encodeURIComponent
	; http://en.wikipedia.org/wiki/Query_string
	; http://tools.ietf.org/html/rfc3986


	Local encode, i, c, output$, l = Len( value$ )

	For i = 1 To l

		c = Asc( Mid( value$, i, 1 ) )

		; If c is numeric or is either uppercase or lowercase alphabetic then don't encode.
		If ( ( c > 47 ) And ( c < 58 ) ) Or ( ( c > 64 ) And ( c < 91 ) ) Or ( ( c > 96 ) And ( c < 123 ) )
			encode = False
		Else
			encode = True
			; Otherwise check if it's still an allowed character.
			Select c
				Case 32 : encode = False : c = 43 ; Convert space to a plus sign.
				Case 46 : encode = False ; .
				Case 45 : encode = False ; -
				Case 95 : encode = False ; _
				Case 126 : encode = False ; ~
				Default
					; These characters don't do any harm, but aren't strictly allowed.
					If Not strict 
						Select c
							Case 39 : encode = False ; '
							Case 33 : encode = False ; !
							Case 40 : encode = False ; (
							Case 41 : encode = False ; )
							Case 42 : encode = False ; *
						End Select
					EndIf
			End Select
		EndIf

		If encode
			output$ = output$ + "%" + Right(Hex$( c ), 2 )
		Else
			output$ = output$ + Chr( c )
		EndIf

	Next

	Return output$
End Function

; *** DEMO ***

Graphics 800, 600, 0, 2

Print EncodeURLValue( "012789-ABCXYZ-abcxyz" )
Print
Print EncodeURLValue( "/:-@[-`{-#$%&^{|}" )
Print
Print EncodeURLValue( " .-_~" )
Print
Print EncodeURLValue( "'!()*" )
Print
Print EncodeURLValue( "'!()*", True )
Print
Print

url$ = "https://www.google.com/search?q="
value$ = "Blitz Basic Code Archive"

Print url$ + EncodeURLValue( value$ )

WaitKey : End
