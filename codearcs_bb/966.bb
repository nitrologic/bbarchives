; ID: 966
; Author: PantsOn
; Date: 2004-03-13 16:02:08
; Title: Command Line Parser
; Description: Obtain values and switches in the command line

; commandline scanner
; ===================
; Richard Hanson, PantsOn Software
; code can be used freely


; INPUT
; =====
; commandline = "/f pic.jpg /o hello.txt"
;
; SOURCE
; ======
; CMD_Init()
; Print CMD_param(2)
; Print CMD_switch("/o")
; CMD_free()
; WaitKey
;
; OUTPUT
; ======
; pic.jpg
; hello.txt


Type CMD_typ
	Field index
	Field command
	Field cmdlne$
End Type

; return the value at parameter 'index'
Function CMD_param$(index)
	; scan each type
	For a.CMD_typ = Each CMD_typ
		If a\index = index Then Return a\CMDlne$
	Next
	
	; return default if nothing found
	Return ""
End Function

; return value with special 'command$' before
Function CMD_switch$(command$)
	; find command
	For a.CMD_typ = Each CMD_typ
		If a\cmdlne$ = command$ Then tmp = a\index + 1 
	Next
	
	; search for next value
	For a.CMD_typ = Each CMD_typ
		If a\index = tmp Then Return a\CMDlne$
	Next
	
	; return nothing if nothing set
	Return ""
End Function

; call at start of the util
Function CMD_init()
	; delete each type
	Delete Each CMD_typ

	index = 1
	strng$ = CommandLine()
		
	While strng<>""
		s = 1
		While Mid(strng,s,1) = " "
			s = s + 1
			If s > Len(strng)
				s = -1
				Exit
			EndIf
		Wend
	
		If s = -1 Then Exit
		
		srch$ = " "
		If Mid(strng,s,1) = Chr(34) Then srch$ = Chr(34)
		 
		f = s + 1
		If f > Len(strng)
			f = s
		Else
			While Mid(strng,f,1) <> srch
				f = f + 1
				If f > Len(strng)
					f = Len(strng)
					Exit
				EndIf
			Wend
		EndIf
		param.CMD_typ = New CMD_typ
		param\cmdlne = CMD_trim(Mid(strng,s,f - s + 1))
		If Left(param\cmdlne,1) = "/" Then param\command = True
		param\index = index
		
		index = index + 1
		strng = Right(strng,Len(strng)-f)
	Wend
		
End Function

; use to free all CMD line values
Function CMD_free()
	; delete each type
	Delete Each CMD_typ
End Function

; used to trim value " and [SPACE]
Function CMD_trim$(strng$)
	If strng = "" Then Return ""
	
	s = 1
	While Mid(strng,s,1) = " " Or Mid(strng,s,1) = Chr(34)
		s = s + 1
		If s > Len(strng) Then Return ""
	Wend

	f = Len(strng)
	While Mid(strng,f,1) = " " Or Mid(strng,f,1) = Chr(34)
		f = f - 1
		If f = 0 Then Return ""
	Wend
	
	Return Mid(strng,s,f - s + 1)
End Function
