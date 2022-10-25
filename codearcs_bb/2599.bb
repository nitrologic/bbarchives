; ID: 2599
; Author: Diego
; Date: 2009-10-29 21:15:47
; Title: GetParameter
; Description: Returns each parameter of the commandline | Gibt jeden Parameter der Befehlszeile zurück

Global GetParameterCommandLine$ = CommandLine()

; Demo (see how it works)
GetParameterCommandLine$ = "'First Parameter' Newline:\r\n " + Chr(34) + "Hello World!" + Chr(34) + " 'Some Chars: \0\\\'\" + Chr(34) + "'"
Print GetParameterCommandLine$
Print
Parameter$ = GetParameter()
While Parameter$ <> ""
	Print Parameter$
	Parameter$ = GetParameter()
	Wend
Print 
Print "Press any key to end"
WaitKey

Function GetParameter$()
GetParameterCommandLine$ = Trim(GetParameterCommandLine$)
For I% = 1 To Len(GetParameterCommandLine$)
	Char$ = Mid(GetParameterCommandLine$, I%, 1)
	If Char$ = "\" Then
		I% = I% + 1
		Char$ = Mid(GetParameterCommandLine$, I%, 1)
		Select Char$
			Case "0"
				Char$ = Chr(0)
			Case "t"
				Char$ = Chr(9)
			Case "n"
				Char$ = Chr(10)
			Case "r"
				Char$ = Chr(13)
			End Select
		Else
		GPDel% = 1
		If GPMode% = 0 And Char$ = Chr(34) Then
			GPMode% = 1
			ElseIf GPMode% = 1 And Char$ = Chr(34) Then
			GPMode% = 0
			ElseIf GPMode% = 0 And Char$ = "'" Then
			GPMode% = 2
			ElseIf GPMode% = 2 And Char$ = "'" Then
			GPMode% = 0
			ElseIf GPMode% = 0 And Char$ = " " Then
			Exit
			Else GPDel% = 0
			EndIf
		If GPDel% Then Char$ = ""
		EndIf
	Parameter$ = Parameter$ + Char$
	Next
GetParameterCommandLine$ = Mid(GetParameterCommandLine$, I%)
Return Parameter$
End Function
