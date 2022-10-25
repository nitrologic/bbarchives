; ID: 2632
; Author: Yasha
; Date: 2009-12-23 15:47:13
; Title: Simple regular expressions
; Description: Match strings against a flexible key

;===============================================================================
;Regular expressions
;===============================================================================


Include "LList.bb"	;Get this Include from: http://www.blitzbasic.com/codearcs/codearcs.php?code=2873


Type RegEx_Node
	Field nType, state
	Field value$, valBank
	Field optL.RegEx_Node, optR.RegEx_Node
	Field branch.RegEx_Node
	Field conc.RegEx_Node, root.RegEx_Node
End Type


Const REGEX_NTYPE_SIMPLE = -1, REGEX_NTYPE_ROOT = 1, REGEX_NTYPE_OR = 3, REGEX_NTYPE_STAR = 4
Const REGEX_NTYPE_PLUS = 5, REGEX_NTYPE_OPT = 6, REGEX_NTYPE_VALUE = 10, REGEX_NTYPE_FINAL = 12
Const REGEX_STATE_START = 1, REGEX_STATE_END = 2, REGEX_STATE_CIS = 4
Const REGEX_STATE_READY = 0, REGEX_STATE_OPT2 = 1, REGEX_STATE_DONE = 2;, REGEX_STATE_FAIL = -1


;Try to match a pattern against a string
Function RegEx_Process$(regIn$, inputString$)
	Local oNode.RegEx_Node = RegEx_Parse(regIn), bk = RegEx_StrToBank(inputString)
	Local char$ = RegEx_Match(oNode, bk, 0)
	RegEx_Delete(oNode) : FreeBank bk
	Return char
End Function

;Match an NFA against a character bank from the given offset
Function RegEx_Match$(root.RegEx_Node, inputStringBank, startPtr)
	
	If root\nType = REGEX_NTYPE_SIMPLE Then Return RegEx_MatchSimple(root, inputStringBank, startPtr)
	
	If (root\state And REGEX_STATE_START)	;If it's a line start pattern only...
		If startPtr > 0
			If PeekByte(inputStringBank, startPtr - 1) <> $0A Then Return ""
		EndIf
	EndIf
	
	Local caseInsensitive = (root\state And REGEX_STATE_CIS) <> 0
	Local cPtr = startPtr, endPtr = BankSize(inputStringBank)
	Local nStack.LList = CreateList(), pStack.LList = CreateList()
	
	Local node.RegEx_Node = root\branch
	RegEx_SetState node, REGEX_STATE_READY
	ListAddLast nStack, Handle(root)	;Push the current operator and pointer to their respective stacks
	ListAddLast pStack, cPtr
	
	Repeat
		
		If node\nType = REGEX_NTYPE_FINAL Or node\nType = REGEX_NTYPE_ROOT		;We're done here
			Exit
			
		ElseIf node\nType = REGEX_NTYPE_VALUE	;Try to match a character
			Local char$
			If cPtr < endPtr
				Local ch = PeekByte(inputStringBank, cPtr)
				char = Chr(ch + (32 * caseInsensitive * (ch >= 65 And ch <= 90)))
			EndIf
			
			If (Instr(node\value, char) > 0) And (cPtr < endPtr)		;node\value is already adjusted for case sensitivity
				cPtr = cPtr + 1
				node = node\conc
				
			Else
				Repeat
					node = Object.RegEx_Node(ListRemoveLast(nStack))
					cPtr = ListRemoveLast(pStack)
					
					If node\nType = REGEX_NTYPE_ROOT	;Total failure
						Exit
						
					ElseIf node\nType = REGEX_NTYPE_STAR Or node\nType = REGEX_NTYPE_OPT	;Allowed to fail
						node = node\conc
						Exit
						
					ElseIf node\nType = REGEX_NTYPE_PLUS And node\state = REGEX_STATE_DONE	;Can't fail first time
						node = node\conc
						Exit
						
					ElseIf node\nType = REGEX_NTYPE_OR And node\state = REGEX_STATE_READY
						ListAddLast nStack, Handle(node)	;Stack it up and try again
						ListAddLast pStack, cPtr
						node\state = REGEX_STATE_OPT2
						node = node\optR
						Exit
					EndIf
				Forever
				
			EndIf
			
		Else		;It's an operator: zero state if complicated, then move to next position
			ListAddLast nStack, Handle(node)
			ListAddLast pStack, cPtr
			If node\nType = REGEX_NTYPE_OR Or node\nType = REGEX_NTYPE_PLUS Then node\state = REGEX_STATE_READY
			If node\nType = REGEX_NTYPE_OR Then node = node\optL : Else node = node\branch
			
		EndIf
		
		;If this happens, it means we're in a success state but the branch has run out
		While node = Null
			node = Object.RegEx_Node(ListRemoveLast(nStack))	;Should always be non-null
			ListRemoveLast pStack
			
			If node\nType = REGEX_NTYPE_PLUS Or node\nType = REGEX_NTYPE_STAR
				ListAddLast nStack, Handle(node)	;Restack it
				ListAddLast pStack, cPtr
				node\state = REGEX_STATE_DONE
				node = node\branch
				
			Else
				node = node\conc
				
				If node <> Null
					ListRemoveLast pStack
					ListAddLast pStack, cPtr	;Update previous operator with moved cursor
				EndIf
			EndIf
		Wend
		
	Forever
	
	If node\nType <> REGEX_NTYPE_FINAL Then cPtr = startPtr		;Fail if it was too short to reach a mismatch
	
	
	FreeList nStack		;Doesn't hold anything that needs to be freed here
	FreeList pStack
	
	Local c, match$ = ""
	For c = startPtr To (cPtr - 1)
		match = match + Chr(PeekByte(inputStringBank, c))
	Next
	
	If (root\state And REGEX_STATE_END)		;If it's a line end pattern only
		If startPtr + Len(match) < BankSize(inputStringBank)
			If PeekByte(inputStringBank, startPtr + Len(match)) <> $0A Then match = ""
		EndIf
	EndIf
	
	Return match
End Function

;Quicker test for simple patterns
Function RegEx_MatchSimple$(node.RegEx_Node, inputStringBank, startPtr)
	Local caseInsensitive, i, c, inputString$
	
	;If the remaining string is shorter than the pattern, it can't match
	If BankSize(inputStringBank) < startPtr + BankSize(node\valBank) Then Return ""
	
	If (node\state And REGEX_STATE_START)	;If it's a line start pattern only...
		If startPtr > 1
			If PeekShort(inputStringBank, startPtr - 2) <> $A0D Then Return ""
		Else
			If startPtr = 1 Then Return ""
		EndIf
	EndIf
	caseInsensitive = node\state And REGEX_STATE_CIS
	
	Local pLimit = BankSize(node\valBank) - 1
	For i = 0 To pLimit
		c = PeekByte(inputStringBank, startPtr + i)
		If caseInsensitive
			If (c >= 65 And c <= 90) Then c = c + 32	;Force lower for case-insensitive comparison
		EndIf
		If c <> PeekByte(node\valBank, i) Then Return "" : Else inputString = inputString + Chr(c)
	Next
	
	If (node\state And REGEX_STATE_END)		;If it's a line end pattern only
		i = BankSize(node\valBank)
		If startPtr + i < BankSize(inputStringBank)
			If startPtr + i = BankSize(inputStringBank) - 1
				Return ""
			Else
				If PeekShort(inputStringBank, startPtr + i) <> $A0D Then Return ""
			EndIf
		EndIf
	EndIf
	
	Return inputString
End Function

;Parse a regular expression into a tree of nodes
Function RegEx_Parse.RegEx_Node(regIn$, caseSensitive = True)
	Local lineStart, lineEnd
	
	If Left(regIn, 1) = "^"		;This pattern may only match the start of a line
		regIn = Mid(regIn, 2)
		lineStart = True
	EndIf
	If Right(regIn, 1) = "$"		;This pattern may only match the end of a line
		If Right(regIn,2) <> "\$"
			regIn = Left(regIn, Len(regIn) - 1)
			lineEnd = True
		EndIf
	EndIf
	
	;If the Regex doesn't use any operators or groups, return a "simple Regex" instead for fast matching
	If RegEx_IsSimple(regIn) Then Return RegEx_CreateSimple(regIn, lineStart, lineEnd, caseSensitive)
	
	Local n.RegEx_Node = New RegEx_Node
	
	n\nType = REGEX_NTYPE_ROOT
	n\value = regIn		;May as well store it in case required for reference
	
	If lineStart Then n\state = n\state Or REGEX_STATE_START
	If lineEnd Then n\state = n\state Or REGEX_STATE_END
	If (Not caseSensitive) Then n\state = n\state Or REGEX_STATE_CIS
	
	n\branch = RegEx_ParseBranch(regIn, n, caseSensitive)
	
	n\conc = New RegEx_Node
	n\conc\nType = REGEX_NTYPE_FINAL
	
	Return n
End Function

;Check if a Regex contains any operators - if not, it's simple
Function RegEx_IsSimple(regIn$)
	Local i, char
	For i = 1 To Len(regIn)
		char = Asc(Mid(regIn, i, 1))
		Select char
			Case 40, 42, 43, 46, 63, 91, 92, 124	;(*+.?[\|
				Return False
		End Select
	Next
	Return True
End Function

;Create a simple regex object for faster comparison of plain string keys
Function RegEx_CreateSimple.RegEx_Node(regIn$, lineStart, lineEnd, caseSensitive)
	Local n.RegEx_Node = New RegEx_Node
	n\nType = REGEX_NTYPE_SIMPLE
	If lineStart Then n\state = REGEX_STATE_START
	If lineEnd Then n\state = n\state Or REGEX_STATE_END
	If caseSensitive = False
		n\state = n\state Or REGEX_STATE_CIS
		regIn = Lower(regIn)
	EndIf
	n\valBank = RegEx_StrToBank(regIn)
	n\value = regIn
	Return n
End Function

;Split am input Regex into the parts to the left and to the right of the first Or operator
Function RegEx_Split(regIn$, out$[1])
	Local cPtr, char$, esc, inClass, pDepth
	
	While cPtr < Len(regIn)
		cPtr = cPtr + 1
		char = Mid(regIn, cPtr, 1)
		
		If esc		;Current character is escaped - doesn't affect anything for our purposes
			esc = False
			
		Else
			If char = "\"
				esc = True
				
			ElseIf char = "["
				inClass = inClass + 1
				
			ElseIf char = "]" And inClass > 0
				inClass = inClass - 1
				
			ElseIf inClass = 0
				If char = "("
					pDepth = pDepth + 1
					
				ElseIf char = ")" And pDepth > 0
					pDepth = pDepth - 1
					
				ElseIf char = "|" And pDepth = 0
					Exit
				EndIf
			EndIf
		EndIf
	Wend
	
	If char = "|"
		out[0] = Left(regIn, cPtr - 1)
		out[1] = Mid(regIn, cPtr + 1)
		Return True
	Else
		out[0] = regIn
		Return False
	EndIf
End Function

;Parse a Regex with choice operators
Function RegEx_ParseBranch.RegEx_Node(regIn$, root.RegEx_Node, caseSensitive)
	Local rBranch$[1], node.RegEx_Node
	
	If RegEx_Split(regIn, rBranch)
		node = New RegEx_Node
		node\nType = REGEX_NTYPE_OR
		node\optL = RegEx_ParseLinear(rBranch[0], node, caseSensitive)
		node\optR = RegEx_ParseBranch(rBranch[1], node, caseSensitive)	;This recursion may cause problems with long regexen
		node\root = root
		node\conc = Null	;May have been set by first value
		Return node
		
	Else
		Return RegEx_ParseLinear(rBranch[0], root, caseSensitive)
		
	EndIf
End Function

;Parse a Regex consiting only of postfix operators and characters/classes
Function RegEx_ParseLinear.RegEx_Node(regIn$, root.RegEx_Node, caseSensitive)
	Local cPtr, char$, esc, head.RegEx_Node, tail.RegEx_Node = root
	
	While cPtr < Len(regIn)
		cPtr = cPtr + 1
		char = Mid(regIn, cPtr, 1)
		
		If esc		;Create a simple value node for escaped characters
			Select char
				Case "n" : char = Chr(10)
				Case "r" : char = Chr(13)
				Case "t" : char = Chr(9)
			End Select
			esc = False
			tail = RegEx_ConcatValue(tail, char, caseSensitive)		;Add char as a single character concatenation
			
		Else
			Select char		;Note that char should never be "|" here
				Case "\"
					esc = True
					
				Case "("
					Local subExpr$ = RegEx_ScanSubExpression(Mid(regIn, cPtr))
					tail\conc = RegEx_ParseBranch(subExpr, tail, caseSensitive)	;Note mutual recusrion - avoid extreme depth
					tail = tail\conc
					cPtr = cPtr + Len(subExpr) + 1	;subExpr doesn't include the enclosing parens
					
				Case "["
					Local class$ = RegEx_ScanCharacterClass(Mid(regIn, cPtr))
					tail = RegEx_ConcatValue(tail, RegEx_GetCharacterClass(class), caseSensitive)
					cPtr = cPtr + Len(class) + 1	;As above
					
				Case "*"
					tail = RegEx_AppendPostfix(tail, REGEX_NTYPE_STAR)
					
				Case "+"
					tail = RegEx_AppendPostfix(tail, REGEX_NTYPE_PLUS)
					
				Case "?"
					tail = RegEx_AppendPostfix(tail, REGEX_NTYPE_OPT)
					
				Case "."
					tail = RegEx_ConcatValue(tail, RegEx_GetCharacterClass("..."), caseSensitive)
					
				Default
					tail = RegEx_ConcatValue(tail, char, caseSensitive)		;Add char as a single character concatenation
					
			End Select
			
		EndIf
		
		If head = Null
			If tail <> root Then head = tail
		ElseIf head = tail\branch
			head = tail
		EndIf
	Wend
	
	Return head
End Function

;Attach a value node to another node as following it
Function RegEx_ConcatValue.RegEx_Node(root.RegEx_Node, val$, caseSensitive)
	Local v.RegEx_Node = New RegEx_Node
	v\nType = REGEX_NTYPE_VALUE
	If (Not caseSensitive) Then val = Lower(val)
	v\value = val
	v\root = root
	If root <> Null Then root\conc = v
	Return v
End Function

;Append a postfix operator of the given type to a linear node chain
Function RegEx_AppendPostfix.RegEx_Node(tail.RegEx_Node, nType)
	Local n.RegEx_Node = New RegEx_Node
	n\nType = nType
	If tail\root <> Null Then tail\root\conc = n		;Replace the tail node on the line
	tail\root = n
	n\branch = tail			;And attach it to this node as a branch
	Return n
End Function

;Read a subexpression from within parentheses (always starts on a "(")
Function RegEx_ScanSubExpression$(reg$)
	Local cPtr, cDepth, pDepth, char$, esc
	
	While cPtr < Len(reg)
		cPtr = cPtr + 1
		char = Mid(reg, cPtr, 1)
		
		If esc
			esc = False
		Else
			Select char
				Case "\"
					esc = True
				Case "["
					cDepth = cDepth + 1
				Case "]"
					cDepth = cDepth - 1
				Case "("
					If cDepth = 0 Then pDepth = pDepth + 1
				Case ")"
					If cDepth = 0 Then pDepth = pDepth - 1
			End Select
		EndIf
		
		If pDepth = 0 Then Return Mid(reg, 2, cPtr - 2)
	Wend
End Function

;Read a character class from a string (always starts on a "[")
Function RegEx_ScanCharacterClass$(reg$)
	Local cPtr, cDepth, char$, esc
	
	While cPtr < Len(reg)
		cPtr = cPtr + 1
		char = Mid(reg, cPtr, 1)
		
		If esc
			esc = False
		Else
			Select char
				Case "\"
					esc = True
				Case "["
					cDepth = cDepth + 1
				Case "]"
					cDepth = cDepth - 1
			End Select
		EndIf
		
		If cDepth = 0 Then Return Mid(reg, 2, cPtr - 2)
	Wend
End Function

;Returns a full character list from a character class
Function RegEx_GetCharacterClass$(cClass$)
	Local invert, cList$, char$, cPtr, i
	
	If cClass = "..."		;Not an expected input class - this is shorthand for "any character"
		For i = 0 To 127
			cList = cList + Chr(i)
		Next
		Return cList
	EndIf
	
	If Left(cClass, 1) = ":" And Right(cClass, 1) = ":"		;A POSIX-style named class
		cClass = Lower(Mid(cClass, 2, Len(cClass) - 2))
		If Left(cClass, 1) = "^"
			invert = True
			cClass = Mid(cClass, 2)
		EndIf
		Select cClass
			Case "alnum" : cClass = "A-Za-z0-9"
			Case "word"  : cClass = "A-Za-z0-9_"
			Case "alpha" : cClass = "A-Za-z"
			Case "blank" : cClass = " " + Chr(9)
			Case "cntrl" : cClass = Chr(0) + "-" + Chr($1F) + Chr($7F)
			Case "digit" : cClass = "0-9"
			Case "graph" : cClass = Chr($21) + "-" + Chr($7E)
			Case "lower" : cClass = "a-z"
			Case "print" : cClass = Chr($20) + "-" + Chr($7E)
			Case "punct" : cClass = "-!" + Chr(34) + "#$%&'()*+,./:;<=>?@\[\\\]_`{|}~"
			Case "space" : cClass = " " + Chr(13) + Chr(12) + Chr(11) + Chr(10) + Chr(9)
			Case "upper" : cClass = "A-Z"
			Case "xdigit": cClass = "A-Fa-f0-9"
			Default : cClass=""
		End Select
		If invert Then cClass="^"+cClass
		
	Else	;A negative class
		If Left(cClass, 1) = "^"
			invert = True
			cClass = Mid(cClass, 2)
		EndIf
	EndIf
	
	For cPtr = 1 To Len(cClass)
		char = Mid(cClass, cPtr, 1)
		
		If char = "\"		;Add escaped characters to the class directly
			cPtr = cPtr + 1
			char = Mid(cClass, cPtr, 1)
			Select char
				Case "n" : char = Chr(10)
				Case "r" : char = Chr(13)
				Case "t" : char = Chr(9)
			End Select
			cList = cList + char
			
		ElseIf Mid(cClass, cPtr + 1, 1) = "-" And cPtr < (Len(cClass) - 1)		;Expand any character ranges
			For i = Asc(char) To Asc(Mid(cClass, cPtr + 2, 1))
				cList = cList + Chr(i)
			Next
			cPtr = cPtr + 2
			
		Else
			
			If char = "["		;Expand nested classes
				Local subClass$ = "", cDepth = 1
				
				Repeat
					cPtr = cPtr + 1
					char = Mid(cClass, cPtr, 1)
					
					If char = "\"
						cPtr = cPtr + 1
						char = Mid(cClass, cPtr, 1)
						subClass = subClass + "\"
						
					ElseIf char = "["
						cDepth = cDepth + 1
						
					ElseIf char = "]"
						cDepth = cDepth - 1
						If cDepth = 0 Then Exit
					EndIf
					
					subClass = subClass + char
				Forever
				
				char = RegEx_GetCharacterClass(subClass)
				
			EndIf
			
			cList = cList + char
		EndIf
	Next
	
	If invert
		cClass = cList
		cList = ""
		For i = 0 To 127
			If Not Instr(cClass, Chr(i)) Then cList = cList + Chr(i)
		Next
	EndIf
	
	Return cList
End Function

;Set the state of a node and all child nodes to the given value
Function RegEx_SetState(node.RegEx_Node, state)
	If node\branch <> Null Then RegEx_SetState node\branch, state
	If node\optL <> Null Then RegEx_SetState node\optL, state
	If node\optR <> Null Then RegEx_SetState node\optR, state
	If node\conc <> Null Then RegEx_SetState node\conc, state
	node\state = state
End Function

;Delete a node and all branch/following nodes
Function RegEx_Delete(node.RegEx_Node)
	If node\branch <> Null Then RegEx_Delete node\branch
	If node\optL <> Null Then RegEx_Delete node\optL
	If node\optR <> Null Then RegEx_Delete node\optR
	If node\conc <> Null Then RegEx_Delete node\conc
	If node\nType = REGEX_NTYPE_SIMPLE Then FreeBank node\valBank
	Delete node
End Function

;Utility - return a bank containing the binary value of the given string
Function RegEx_StrToBank(s$)
	Local i, bank = CreateBank(Len(s))
	For i = 0 To Len(s) - 1
		PokeByte bank, i, Asc(Mid(s, i + 1, 1))
	Next
	Return bank
End Function

;Utility - return a string containing the ASCII value of the given bank
Function RegEx_BankToStr$(bank)
	Local i, s$
	For i = 0 To BankSize(bank) - 1
		s = s + Chr(PeekByte(bank, i))
	Next
	Return s
End Function


;===============================================================================


;~IDEal Editor Parameters:
;~F#9#19#21#93#BA#DD#EA#F9#126#139#177#182#18C#1A9#1C2#22B#234#23E#247
;~C#Blitz3D
