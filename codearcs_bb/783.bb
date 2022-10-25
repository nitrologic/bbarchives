; ID: 783
; Author: Steve Hill
; Date: 2003-08-27 13:44:29
; Title: BlitzBeautifier
; Description: Creates beautiful looking code from ugly code

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; BB.bb
;
; Blitz Beautifier -- reformats your Blitz program to look nice ... to me
;
; Steve Hill, 2003
;
; RESTRICTIONS
; + doesn't like If <cond> <statement> -- use If <cond> Then <statement>
;
; Versions
; 0.1			Initial version		27 Aug 2003
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Include "Lexer.bb"

Type TPState
	Field indent
	Field spacePending
	Field indentPending
	Field previousArithOp
	Field thenFound
End Type

Function Tabs$(n)
	Return String$(Chr$(TAB), n)
End Function

Function PrintEOL(out, pState.TPState)
	pState\indentPending = True
	WriteByte(out, CR)
	WriteByte(out, LF)
End Function

Function PrintFile(out, s$, pState.TPState)
	If pState\indentPending Then
		s$ = Tabs$(pState\indent) + s$
		pState\indentPending = False
		pState\spacePending = False
	EndIf
	
	If pState\spacePending Then
		s$ = " " + s$
	EndIf
	
	For i = 1 To Len(s$)
		WriteByte(out, Asc(Mid$(s$, i, 1)))
	Next
End Function

Function PrintWord(outFile, tok$, pState.TPState)
	outdent =            tok$ = "EndIf"      Or tok$ = "Wend"      Or tok$ = "Until"
	outdent = outdent Or tok$ = "Next"       Or tok$ = "End Type" 
	outdent = outdent Or tok$ = "End Select" Or tok$ = "Case"      Or tok$ = "Default"
	outdent = outdent Or tok$ = "ElseIf"	 Or tok$ = "Else"
	
	If tok$ = "End Function" Or tok$ = "Function"
		pState\indent = 0
	ElseIf outdent
		pState\indent = pState\indent - 1
		If tok$ = "End Select" Then
			pState\indent = pState\indent - 1
		EndIf
			
		If pState\indent < 0 Then
			pState\indent = 0
		EndIf
	EndIf
	
	PrintFile(outFile, tok$, pState)
	pState\spacePending = True
	
	indent =           tok$ = "If"     Or tok$ = "While"    Or tok$ = "Repeat" 
	indent = indent Or tok$ = "For"    Or tok$ = "Function" Or tok$ = "Type"
	indent = indent Or tok$ = "Select" Or tok$ = "Case"     Or tok$ = "Default"
	indent = indent Or tok$ = "ElseIf" Or tok$ = "Else"

	If indent Then
		pState\indent = pState\indent + 1
		If tok$ = "Select"
			pState\indent = pState\indent + 1
		EndIf
	EndIf
End Function

Function PrintOperator(outFile, tok$, pState.TPState)
	If tok$ = "(" Or tok$ = ")"
		spaceBefore = pState\previousArithOp
	Else
		spaceBefore = Instr("[].$#%\,", tok$) = 0
	EndIf
		
	pState\spacePending = spaceBefore

	PrintFile(outFile, tok$, pState)

	; Special case for prefix + and -
	If pState\previousArithOp And (tok$ = "+" Or tok$ = "-") Then
		pState\spacePending = False
	Else
		spaceAfter = Instr("[()].$#%\", tok$) = 0
		
		pState\spacePending = spaceAfter
	EndIf
End Function

Function Parse(outFile)
	state.TState = Last TState
	pState.TPstate = New TPState
	pState\indent = 0
	pState\spacePending = False
	pState\indentPending   = False
	pState\previousArithOp = False
	pState\thenFound       = False
	
	While state\tok$ <> ""
		tokType 	= state\tokType
		tok$    	= state\tok$
		wasArithOp 	= False
		wasComment 	= False
		GetToken(state)
		
		Select tokType

		Case TOK_WORD
			; Special case nasties
			If tok$ = "End" Then
				If state\tok$ = "Function" Or state\tok$ = "Type" Or state\tok$ = "Select" Then
					tok$ = tok$ + " " + state\tok$
					GetToken(state)
				ElseIf state\tok$ = "If" Then
					tok$ = "EndIf"
					GetToken(state)
				EndIf
			ElseIf tok$ = "Else" And state\tok$ = "If" Then
				tok$ = "ElseIf"
				GetToken(state)
			EndIf
				
			PrintWord(outFile, tok$, pState)
			
		Case TOK_OPERATOR
			PrintOperator(outFile, tok$, pState)
			wasArithOp = Instr("#%$.()", tok$) = 0
			
		Case TOK_COMMENT
			PrintFile(outFile, tok$, pState)
			wasComment = True

		Case TOK_DEC_NUMBER
			PrintFile(outFile, tok$, pState)
			pState\spacePending = True

		Case TOK_HEX_NUMBER
			PrintFile(outFile, tok$, pState)
			pState\spacePending = True


		Case TOK_BIN_NUMBER
			PrintFile(outFile, tok$, pState)
			pState\spacePending = True

		Case TOK_STRING
			PrintFile(outFile, tok$, pState)
			pState\spacePending = True

		Case TOK_EOL
			pState\thenFound = False
			PrintEOL(outFile, pState)
			
		Case TOK_EOF
		
		Default
			PrintFile(outFile, tok$, pState)
			
		End Select
		
		pState\previousArithOp = wasArithOp

		; HACK to try to cope with nasty if usage
		If pState\thenFound And (Not wasComment) Then
			pState\indent = pState\indent - 1
			pState\thenFound = False
		EndIf
		
		If tok$ = "Then" Then
			pState\thenFound = True
		EndIf

	Wend
	
	Delete pState
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; MAIN PROGRAM
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

inFile$  = Input$("Input file: ")
outFile$ = Input$("Output file (or return): ")

If outFile$ = ""
	outFile$ = "Beautiful.bb"
EndIf

out = WriteFile(outFile$)
If out <> 0 Then
	OpenState(inFile$)
	Parse(out)
	CloseState()
EndIf

Print "Done"
Print "Press a key"
WaitKey

End
