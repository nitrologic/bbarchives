; ID: 3042
; Author: Bobysait
; Date: 2013-03-20 23:51:18
; Title: bb Tokenizer + parser + bbToDecls
; Description: smart simple tokenizer + decls extractor

Const MAX_TOKEN_PER_SET% = 512

Const TOKEN_OP%=0, TOKEN_WORD%=1, TOKEN_NUM%=2, TOKEN_STR%=3

Type TokenSet Field Tokens, count% End Type
Type Token Field v$, t%, p%, e% End Type

Function TokenSetCount%(ts.TokenSet)
	Return ts\count
End Function

Function TokenSetToken.Token(ts.TokenSet,id)
	Return Object.Token(PeekInt(ts\Tokens,id*4-4))
End Function

Function TokenValue$(ts.TokenSet,id)
	Local tok.Token = Object.Token(PeekInt(ts\Tokens,id*4-4)) : If tok<>Null : Return tok\v : EndIf
	Return ""
End Function
Function TokenType%(ts.TokenSet,id)
	Local tok.Token = Object.Token(PeekInt(ts\Tokens,id*4-4)) : If tok<>Null : Return tok\t : EndIf
	Return 0
End Function
Function TokenStart%(ts.TokenSet,id)
	Local tok.Token = Object.Token(PeekInt(ts\Tokens,id*4-4)) : If tok<>Null : Return tok\p : EndIf
	Return 0
End Function
Function TokenEnd%(ts.TokenSet,id)
	Local tok.Token = Object.Token(PeekInt(ts\Tokens,id*4-4)) : If tok<>Null : Return tok\e : EndIf
	Return 0
End Function

Function FreeTokenSet(ts.TokenSet)
	If ts\Tokens
		Local size=BankSize(ts\Tokens)
		If size>3
			For n = 0 To size-1 Step 4
				Local tok.Token = Object.Token(PeekInt(ts\Tokens,n))
				If tok<>Null Then Delete tok
			Next
		EndIf
		FreeBank ts\Tokens
	EndIf
	Delete ts
End Function

Function NewToken.Token(ts.TokenSet, v$, t%, p%,e%)
	ResizeBank(ts\Tokens,BankSize(ts\Tokens)+4)
	Local tok.Token = New Token
	; value of the token
	tok\v = v
	; type of token (word, operator)
	tok\t = t
	; as the tokenizer declare numerals as they were words, check if is actually is a numeral or not
	If tok\t = TOKEN_WORD
		If IsNum(tok\v)
			tok\t=TOKEN_NUM
		EndIf
	EndIf
	; token position in the string
	tok\p = p
	; end position in the string ( length is e-p+1 ... or Len(v) )
	tok\e = e
	
	; insert the token in the token bank
	PokeInt(ts\Tokens,ts\count*4,Handle(tok))
	; increase the count of token in the set
	ts\count = ts\count + 1
	
	; eventually returns the token ... (not really usefull BTW)
	Return tok
End Function

; tokenize a string @s
; @ops  : a string containing all symbols (one char length per symbol)
; @seps : a string containing all symbols removed from the set
;         -> like spaces or tabs, they are used to split words
;		     but not relevant in for the language.
Function Tokenize.TokenSet(s$, ops$, seps$=" ", strchars$="'")
	Local ts.TokenSet = New TokenSet
	ts\Tokens = CreateBank(0)
	Local word$="",start%=0
	Local ln=Len(s), i, c$, b
	
	For i = 1 To ln
		
		c = Mid(s,i,1)
		
		; first -> detect strings !
		If Instr(strchars,c)
			
			; register eventual started word
			If word<>"" Then NewToken(ts,word,TOKEN_WORD,start,i)
			
			; find the respective closer
			start=i
			i=Instr(s,c,i+1)
			If i>start
				; add the string to the tokenset
				NewToken(ts,Mid(s,start,i-start+1),TOKEN_STR,start,i)
			; not closed ? let's close it with to the end of the string.
			Else
				NewToken(ts,Right(s,ln-start),TOKEN_STR,start,i)
				; exit the loop. We encountered the end, there 's nothing left to parse.
				Exit
			EndIf
			
		; ok, so it's not a String, maybe an empty char.
		ElseIf Instr(seps,c)
			
			; add the current word (if any)
			If word<>"" Then NewToken(ts,word,TOKEN_WORD,start,i)
			; reset position
			word="" : start=i+1
			; do not add the token ... it's an empty token!
			
		; or an operator maybe
		ElseIf Instr(ops,c)
			
			Local isnumber=False
			; as a dot may be an operator or the separator between integer and decimal
			; we wouldn't to separate them, don't we ?! ... Do you ? oO ... it's bad ! really bad !
			
			; then let's track numerals
			If c="."
				
				; if current word is not empty, check if its first char is litteral or numeral
				; generally a word can contain some number, but can't start with a number.
				; (except for Hex wich actually are not really numbers but a litteral expression
				;  to represent a number .... outch ... my brain is bleeding
				;  anyway, it's a kind of a rule up to the user to define. so ... we don't care about 'hex'.)
				If Len(word)
					b=Asc(Left(word,1))
					; alright, we found a number (or an ... something erroneous containing some chars that we don't care about.
					;                             Did I told you about Hex ?... mmm... probably.)
					If b>=Asc("0") And b<=Asc("9")
						isnumber=True
						word=word+c
					; else the first char is not a numeral
					; it means we have a word and a dot. So the dot will be managed as an operator.
					EndIf
				Else
				; no word currently ? so we have to be sure the next char is a numeral or not.
				; because we can start a decimal using just the dot ( ex : ".0704" )
					If i<ln
						b=Asc(Mid(s,i+1,1))
						; ok we found a numeral :) (the integer part will get back its baby \o/)
						If b>=Asc("0") And b<=Asc("9")
							isnumber=True
							word=word+c
						EndIf
					; else the string ends with a dot ... uncommun oO ...
					; but maybe it's an unfinished string or ... don't know ..."
					; maybe you're trying to parse a book ? ... did I noticed you this parser is only made for programming language ?
					; my bad, I should have... doesn't matter, now, you know it.
					EndIf
				EndIf
			EndIf
			
			; we reach this point without finding numeral, so this is an operator (wether it's a dot or not)
			If isnumber=False
				; if any started word, then just add it to the set.
				If word<>"" Then NewToken(ts,word,TOKEN_WORD,start,i)
				; add the symbol
				NewToken(ts,c,TOKEN_OP,i,i)
				; reset start and word
				word="" : start=i+1
			EndIf
			
			; by the way, don't leave a variable with a state (doesn't really matter but, it's an habit to have)
			isnumber=False
			
		; Else, this is just a legal char ... let's say it's part of a word.
		; (this tokenizer doesn't care about illegal chars, it lives in a wonderfull world of freedom)
		Else
			; add the char to the word.
			word=word+c
		EndIf
	Next
	
	; the word contains some chars ? let's register them before ending the set.
	If word<>"" Then NewToken(ts,word,TOKEN_WORD,start,Len(s))
	
	; and Voila !
	; we can return our set, the user will take care of it (I hope ...)
	Return ts
	
End Function

; a basic function that returns true if the string contains only numeral chars (0-9 + ".")
; actually this function returns true if there is more than one "." ...
; but as we don't deal this kind of errors, whatever ... let's say it's a number !
Function IsNum%(v$)
	Local a0=Asc("0"), a9=Asc("9"), ad=Asc("."), l=Len(v), i, b
	For i = 1 To l:b=Asc(Mid(v,i,1)):If((b<a0 Or b>a9) And b<>ad):Return False:EndIf:Next
	Return l>0 ; return false is the string is empty, it's not a numeral.
End Function


Type Keyword
	Field word$, cs
End Type
Function NewKeyWord(word$,casesensitive%=False)
	Local kw.Keyword = New Keyword
	If casesensitive
		kw\word = word
	Else
		kw\word = Lower(word)
	EndIf
	kw\cs=casesensitive
End Function

; small sample
Function Tokenizer_SimpleSample()
	Graphics 800,600,0,2
	
	ClsColor 0,40,60
	Cls
	
	; let's load a font (I like consolas, as it's a fixed width and lite font)
	; but if you don't have it, let's go for the blitz font (you should have it, as it's the ... blitz font)
	Local font=LoadFont("Consolas",18) : If Not(font) Then font=LoadFont("Blitz",16)
	SetFont font
	
	; setup our tokenizer
	Local ops$ = ".=()[]#$%;:/\+-*.,?" ; basic symbols to parse
	Local emptyops$ = " "+Chr(9) ; space + tab -> thoose symbols split words but are not output by the tokenizer
	Local strops$ = Chr(34); (chr(34) = ["] ) -> the string chars. everything started with thoose symbol end with the same symbol.
	
	; some strings to parse
	Local mystring$[4]
	mystring[0] = "; the Holy function"
	mystring[1] = "Function TokenizeMe.ReturnType(Params%=12.6,param2$="+Chr(34)+"I'm a striiiiIiing"+Chr(34)+")"
	mystring[2] = "  This code will never compile ... but the parser doesn't know it"
	mystring[3] = " Return Null"
	mystring[4] = "End Function ; and I'm a bronish comment"
	
	Local ns
	For ns = 0 To 4
	
		; tokenize the string
		Local ts.TokenSet=Tokenize(mystring[ns], ops, emptyops,strops)
		
		NewKeyWord("Function")
		NewKeyWord("End")
		NewKeyWord("Return")
		NewKeyWord("Null")
		
		Local NbToken = TokenSetCount(ts), n
		
		If NbToken
			
			Local tok.Token
			; parse each token in the set with a Blitz-like style
			For n = 1 To NbToken
				
				Select TokenType(ts,n)
					
					; if it's a word color it Yellow
					Case TOKEN_WORD : Color 255,255,000
						
						; we should parse all keywords, but this is just a sample, we'll only track the "Function" and "End keywords
						; keywords are light-blue (actually, it's turquoise... or orange ... mmm ... colorblindness is a pain in the ****)
						Local word$ = TokenValue(ts,n)
						Local lowerword$=Lower(word)
						Local kw.Keyword
						For kw = Each Keyword
							If kw\cs
								If word=kw\word Then Color 000,255,255:Exit
							Else
								If lowerword=kw\word Then Color 000,255,255:Exit
							EndIf
						Next
						
					; operators are whyte
					Case TOKEN_OP   : Color 255,255,255
						; except the comments ...
						If TokenValue(ts,n)=";"
							; let's color the comments in a brownish orange
							Color 255,100,000
							; write all the left tokens and quit the loop.
							Write "; "
							Local n2=n+1
							For n=n2 To NbToken
								Write TokenValue(ts,n)+" "
							Next
							Exit
						EndIf
						
					; numbers are blue
					Case TOKEN_NUM  : Color 000,128,255
					
					; and finally, the string in green
					Case TOKEN_STR  : Color 000,255,000
					
				End Select
				
				; here we are. Write the token
				Write TokenValue(ts,n)+" "
				
			Next
		EndIf
		FreeTokenSet(ts)
		Print ""
	Next
	
	Flip True
	
	FreeFont font
	
	WaitKey
	End
End Function


Tokenizer_SimpleSample()
