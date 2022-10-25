; ID: 1768
; Author: Vertex
; Date: 2006-07-30 18:49:17
; Title: Simple Math Compiler
; Description: x86 output and calculation

Global In$
Global Position%
Global Token%

In$ = Input(">")+Chr(13)
Position% = 1
Parse()
WaitKey()
End

Function Parse()
	GetToken()
	Command()
End Function

Function Error()
	Print "Parse error"
	WaitKey()
	End
End Function

Function GetToken()
	If Position% > Len(In$) Then Error()
	Token% = Asc(Mid(In$, Position%, 1))
	Position% = Position% + 1
End Function

Function Match(Char%)
	If Token% = Char% Then
		GetToken()
	Else
		Error()
	EndIf
End Function

Function Command()
	Local Result%
	
	Result% = Expression()
	If Token% = 13 Then
		Print "pop  eax"
		Print "; Result: "+Result%
		Print ""
	Else
		Error()
	EndIf
End Function

Function Expression%()
	Local Result%
	
	Result = Term()
	While Token% = Asc("+") Or Token% = Asc("-")
		If Token% = Asc("+")
			GetToken()
			Result% = Result% + Term()
			Print "pop  ebx"
			Print "pop  eax"
			Print "add  eax, ebx"
			Print "push eax"
		Else
			GetToken()
			Result% = Result% - Term()
			Print "pop  ebx"
			Print "pop  eax"
			Print "sub  eax, ebx"
			Print "push eax"
		EndIf
	Wend

	Return Result%
End Function

Function Term%()
	Local Result%
	
	Result = Factor()
	While Token% = Asc("*") Or Token% = Asc("/")
		If Token% = Asc("*") Then
			GetToken()
			Result% = Result% * Factor()
			Print "pop  ebx"
			Print "pop  eax"
			Print "mul  eax, ebx"
			Print "push eax"
		Else
			GetToken()
			Result = Result% / Factor()
			Print "pop  ebx"
			Print "pop  eax"
			Print "div  eax, ebx"
			Print "push eax"
		EndIf
	Wend

	Return Result%
End Function

Function Factor%()
	Local Result%

	If Token% = Asc("(") Then
		Match(Asc("("))
		Result% = Expression()
		Match(Asc(")"))
	Else
		Result% = Number()
	EndIf

	Return Result%
End Function

Function Number%()
	Local Result%

	While Token% => Asc("0") And Token% <= Asc("9")
		Result% = Result%*10 + Digit()
	Wend

	Print "push "+Result%
	Return Result%
End Function

Function Digit%()
	Local Result%

	If Token% => Asc("0") And Token% <= Asc("9") Then
		Result% = Token% - Asc("0")
		Match(Token%)
	Else
		Error()
	EndIf

	Return Result%
End Function
