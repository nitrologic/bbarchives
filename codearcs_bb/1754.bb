; ID: 1754
; Author: Andres
; Date: 2006-07-16 12:24:15
; Title: Config file reader/changer
; Description: easy functions to read/edit config file

Const ConfigFile$ = "config.cfg"

Type file
	Field txt$
End Type

Function ConfigValue$(variable$)
	rf = ReadFile(ConfigFile$)
	If rf
		While Not Eof(rf)
			txt$ = ReadLine$(rf)
			If Left$(Lower(txt$), Instr(txt$, "=")) = Lower(variable$ + "=")
				Return Right$(txt$, Len(txt$) - Instr(txt$, "="))
			EndIf
		Wend
		CloseFile rf
	EndIf
	Return False
End Function

Function ChangeConfigValue(variable$, value$)
	rf = ReadFile(ConfigFile$)
	If rf
		For this.file = Each file
			Delete this
		Next
		While Not Eof(rf)
			this.file = New file
				this\txt$ = ReadLine$(rf)
		Wend
		CloseFile rf
	EndIf
	
	wf = WriteFile("config.cfg")
	If wf
		For this.file = Each file
			If Left$(Lower(this\txt$), Instr(this\txt$, "=")) = Lower(variable$ + "=")
				WriteLine wf, Left$(this\txt$, Instr(this\txt$, "=")) + value$
			Else
				WriteLine wf, this\txt$
			EndIf
		Next
		CloseFile wf
	EndIf
End Function
