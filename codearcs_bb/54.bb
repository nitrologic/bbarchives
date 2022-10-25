; ID: 54
; Author: Unknown
; Date: 2001-09-26 05:28:13
; Title: Parser/Occurence functions
; Description: Parser/Occurence functions by HunterSD [huntersd@iprimus.com.au]

;setup for the parser (the array that the variables are returned in)
Dim parserreturn$(1)

;simple parser, designed for a net game
;txt$ is the string that you want to parse
;give it some numbers in a string seperated by comma's (say, "2,4,5,6,1,55").
;it will redim the 'parserreturn' array, and put all of the numbers in it, it will also return the size of this array
;heres and example:

;z = parser$("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20")
z = parser$("n,0,100,0")
For a = 1 To z
	Print parserreturn(a)
Next

Function parser$(txt$)
	occurence = occurence(txt$, ",")
	Dim parserreturn(occurence+1)
	For a = 1 To occurence
		Repeat
			b = b + 1
			If Mid$(txt$, b, 1) <> ","
				c = c + Mid$(txt$, b, 1)
			Else
				endloop = 1
				parserreturn(a) = c
			EndIf
		Until endloop = 1
		endloop = 0
		c = 0
	Next
	b = b + 1
	parserreturn(occurence+1) = Mid$(txt$, b)
	Return occurence+1
End Function



;simple occurence function, it finds all instances of one string in another
;txt$ is the string to find stuff in and txt2$ is the string to find in txt$
;example:
;z = occurence("doremesofadole", "e")
;print z
;(and I dont know what the REAL music notes are :P)
Function occurence(txt$, txt2$)
	offset = 1
	occur = 0
	Repeat
		If Instr(txt$, txt2$, offset) > 0
			offset = Instr(txt$, txt2$, offset) + 1
			occur = occur + 1
		Else
			endloop = 1
		EndIf
	Until endloop = 1
	Return occur
End Function
