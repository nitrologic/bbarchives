; ID: 782
; Author: Steve Hill
; Date: 2003-08-27 13:41:23
; Title: Blitz Lexical Analyser
; Description: Blitz functions to split a Blitz source file into tokens

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; Lexer.bb
;
; Tokenises Blitz Basic code
;
; Steve Hill, 2003
;
; OpenState(fileName$) - creates a new TState
; CloseState()         - destroys and closes the current state
; GetToken(state)      - read the next token
;
; The current token is available in state\tok$
;
; Versions
; 0.1			Initial version		27 Aug 2003
; 0.2			Added >< => =<		29 Aug 2003
;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; CONSTANTS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Constants for various character types
;
Const  SPACE        = 32
Const  TAB			= 9
Const  CR			= 13
Const  LF           = 10

Global WHITE_SPACE$ = Chr$(SPACE) + Chr$(TAB) + Chr$(CR)
Const  ALPHA$       = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
Const  DIGITS$      = "0123456789"
Const  HEXDIGITS$   = "0123456789abcdefABCDEF"
Const  BINDIGITS$   = "01"
Const  DELIM$		= "^*+-~<>/\#%.$()[],=\:"
Global QUOTE$		= Chr$(34)
Global ALPHANUM$	= ALPHA$ + DIGITS$

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; TYPES
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; TState
;
; Keeps track of the current file, look-ahead character and token.
; Can be used as a stack for include files.
;
Type TState
	Field file
	Field ch$
	Field tok$
	Field tokType
	Field lineNum
	; Field charNum
End Type

Const TOK_WORD			= 0
Const TOK_OPERATOR		= 1
Const TOK_COMMENT		= 2
Const TOK_DEC_NUMBER	= 3
Const TOK_HEX_NUMBER	= 4
Const TOK_BIN_NUMBER	= 5
Const TOK_STRING		= 6
Const TOK_EOL			= 7
Const TOK_EOF			= 8
Const TOK_UNKNOWN		= 9

; TDescriptor
;
; Describes a function: name, return type and parameter type list.
; Assigned unique id for each functions ... its "pointer"
;
Type TDescriptor
	Field name$
	Field typ$
	Field params$
	Field id
End Type

; Error
;
; Something has gone wrong bail out.
;
Function Error(e$, state.TState)
	Print e$
	If state\file <> 0
		Print "Error on line " + Str$(state\lineNum)
	EndIf
	Print "Press a key"
	WaitKey
	End
End Function

; OpenState
;
; Open the file initialise the fields
;
Function OpenState(name$)
	Print "Parsing " + name$
	state.TState = New TState
	state\file = ReadFile(name$)
	If state\file = 0 Then
		Error("File " + name$ + "not found", state)
	EndIf
	state\lineNum = 1
	state\ch$ = ""
	state\tok$ = ""
	
	GetToken(state)
End Function

; CloseState
;
; Close current file, pop state
;
Function CloseState()
	For state.TState = Each TState
		CloseFile(state\file)
	Next
	
	Delete Each TState
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; LEXICAL FUNCTIONS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; GetChar
;
; Read a character from the current file into the state
;
Function GetChar(state.TState)
	If state\ch$ = Chr$(LF)
		state\lineNum = state\LineNum + 1
	EndIf

	If Eof(state\file) Then
		state\ch$ = ""
		Return
	EndIf
	
	state\ch$ = Chr$(ReadByte(state\file))	
End Function

; SkipSpace
;
; Skip white space
;
Function SkipSpace(state.TState)
	While Instr(WHITE_SPACE$, state\ch$) <> 0
		GetChar(state)
		If state\ch$ = "" Return
	Wend
End Function

; GetFollowing
;
; Generic token reader, reads while characters are
; those in pat.  Places token in state.
;
Function GetFollowing(state.TState, pat$)
	tok$ = state\ch$
	GetChar(state)
	While Instr(pat$, state\ch$) <> 0
		If state\ch$ = "" Exit
		tok$ = tok$ + state\ch$
		GetChar(state)
	Wend
	state\tok$ = tok$
End Function

; GetDecNumber
;
; eg. 1 or 1.2
;
Function GetDecNumber$(state.TState)
	GetFollowing(state, DIGITS$ + ".")
	state\tokType = TOK_DEC_NUMBER
End Function

; GetBinNumber
;
; eg. %1100
;
Function GetBinNumber$(state.TState)
	GetFollowing(state, BINDIGITS$)
	state\tokType = TOK_BIN_NUMBER
End Function

; GetHexNumber
;
; eg. $abC1
;
Function GetHexNumber$(state.TState)
	GetFollowing(state, HEXDIGITS$)
	If state\tok$ = "$"
		state\tokType = TOK_OPERATOR
	Else
		state\tokType = TOK_HEX_NUMBER
	EndIf
End Function

; GetEOL
;
; Get end of line
Function GetEOL(state.TState)
	state\tok$ = state\ch$
	GetChar(state)
	state\tokType = TOK_EOL
End Function

; GetWord
;
; eg. myVar_2 or WaitKey
;
Function GetWord(state.TState)
	GetFollowing(state, ALPHANUM$ + "_")
	If state\tok$ = "Or" Or state\tok$ = "And" Or state\tok$ = "Xor" Then
		state\tokType = TOK_OPERATOR
	Else
		state\tokType = TOK_WORD
	EndIf
End Function

; GetOperator
;
; eg. , . \ + - = > < <> etc.
;
Function GetOperator(state.TState)
	t$ = state\ch$
	GetChar(state)

	r$ = t$
	
	Select t$
	Case ">"
		t$ = state\ch$
		Select t$ 
		Case "="
			r$ = ">="
			GetChar(state)
		Case "<"
			r$ = "><"
			GetChar(state)
		Default
			r$ = ">"
		End Select
	Case "<"
		t$ = state\ch$
		Select t$
		Case "="
			r$ = "<="
			GetChar(state)
		Case ">"
			r$ = "<>"
			GetChar(state)
		Default
			r$ = "<"
		End Select
	Case "="
		t$ = state\ch$
		Select t$
		Case ">"
			r$ = "=>"
			GetChar(state)
		Case "<"
			r$ = "=<"
			GetChar(state)
		Default
			r$ = "="
		End Select
	End Select
		
	state\tok$ = r$
	state\tokType = TOK_OPERATOR
End Function

; GetComment
;
; eg. ; a comment
;
Function GetComment(state.TState)
	tok$ = state\ch$
	GetChar(state)
	While state\ch$ <> Chr$(LF)
		If state\ch$ = "" Exit
		If state\ch$ <> Chr$(CR)
			tok$ = tok$ + state\ch$
		EndIf
		GetChar(state)
	Wend
	state\tok$ = tok$
	state\tokType = TOK_COMMENT
End Function

; GetString
;
; eg. "a string"
;
Function GetString(state.TState)
	tok$ = ""
	GetChar(state)
	While state\ch$ <> QUOTE$ And state\ch$ <> ""
		tok$ = tok$ + state\ch$
		GetChar(state)
	Wend

	If state\ch$ <> ""
		state\tok$ = QUOTE$ + tok$ + QUOTE$
		GetChar(state)
	EndIf
	
	state\tokType = TOK_STRING
End Function

; GetToken
;
; Use first character to determine type of token and then
; read appropriate token using the corresponding Get function
;
Function GetToken(state.TState)
	SkipSpace(state)

	ch$ = state\ch$
	
	If ch$ = "" Then
		state\tok$ = ""
		state\tokType = TOK_EOF
		Return
	EndIf
	
	If Instr(DIGITS$, ch$) <> 0 Then
		GetDecNumber$(state)
	ElseIf Instr(ALPHA$, ch$) <> 0 Then
		GetWord(state)
	ElseIf ch$ = ";" Then
		GetComment(state)
	ElseIf ch$ = QUOTE$ Then
		GetString(state)
	ElseIf ch$ = "%" Then
		GetBinNumber(state)
	ElseIf ch$ = "$" Then
		GetHexNumber(state)
	ElseIf Instr(DELIM$, ch$) <> 0 Then
		GetOperator(state)
	ElseIf ch$ = Chr$(LF)
		GetEOL(state)
	Else
		Error("Unrecognised character " + ch$ + "(" + Asc(ch$) + ") in file", state)
	EndIf

	; DebugLog Str$(state\lineNum) + ": " + state\tok$
	
End Function

; Example usage
;
;
;
;inFile$  = Input$("Input file: ")
;
;OpenState(inFile$)
;state.TState = Last TState
;While state\tok$ <> ""
;	If state\tokType <> TOK_EOL Then
;		Print state\tok$
;	EndIf
;	GetToken(state)
;Wend
;CloseState()
;
;Print "Press a key"
;WaitKey
;
;End
