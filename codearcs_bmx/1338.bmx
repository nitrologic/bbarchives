; ID: 1338
; Author: Azathoth
; Date: 2005-03-25 09:32:30
; Title: Split
; Description: Splits a string into an array of words/substrings

Function Split:String[](s:String, sep:String)
	Local word:String, word_c=0
	Local o=1,in_str=0, char=0
	Local words:TList=CreateList()
	
	
	For Local i=1 To s.length
		Local sc=s[i-1]
		
		If in_str=0 And (sc=Asc("~q") Or sc=Asc("'"))
			in_str=sc
		ElseIf sc=in_str
			in_str=0
		EndIf
		
		If in_str=0
			For Local j=1 To sep.length
				char=sep[j-1]
				If char=sc
					word=Mid(s,o,i-o)
					If word
						words.AddLast(word)
						word_c:+1
					EndIf
					words.AddLast(Chr(char))
					word_c:+1
					o=i+1
				EndIf
			Next
		EndIf
	Next
	
	word=Mid(s,o,s.length)
	If word
		words.AddLast(word)
		word_c:+1
	EndIf
	
	Local word_array:String[]=String[](words.ToArray())
	Return word_array
EndFunction
