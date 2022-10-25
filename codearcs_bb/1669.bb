; ID: 1669
; Author: Grey Alien
; Date: 2006-04-14 08:44:22
; Title: String Handling Functions
; Description: String Handling Functions

; -----------------------------------------------------------------------------
; pad a string with spaces on the right (best for left aligning strings)
; -----------------------------------------------------------------------------
Function ccPadRight$(TheString$, TheSize)
	sl = TheSize - Len(TheString$) ; Get length of string
	If sl > 0 Then ; Needs padding?
		For p = 1 To sl										
			z$ = z$ + " " ; make a blank string
		Next
		TheString$ = TheString$ + z$; add the blank string to the main string
	EndIf
	Return TheString$
End Function 

; -----------------------------------------------------------------------------
; pad a string with spaces on the right (best for right aligning numbers numbers)
; -----------------------------------------------------------------------------
Function ccPadLeft$(TheString$, TheSize)
	sl = TheSize - Len(TheString$) ; Get length of string
	If sl > 0 Then ; Needs padding?
		For p = 1 To sl										
			z$ = z$ + " " ; make a blank string
		Next
		TheString$ = z$ + TheString$; add the blank string to the main string
	EndIf
	Return TheString$
End Function

; -----------------------------------------------------------------------------
; First String (return first part of string up to comma)
; -----------------------------------------------------------------------------
Function ccFirstString$(s$)
	;doesn't call ccFirstStringToSub so it stays fast for big files
	;pass in a string, this will only return the first part up to, but not including, the comma (or end)		
	pos% = Instr(s$, ",")
	;If pos = 0 then then end of the was reached, so return the whole thing.
	If pos = 0 Then
		Return s$
	Else
		Return Mid(s$, 1, pos-1)
	EndIf
End Function

; -----------------------------------------------------------------------------
; Last String (return last part of string from comma)
; -----------------------------------------------------------------------------
Function ccLastString$(s$)
	;doesn't call ccLastStringToSub so it stays fast for big files
	;pass in a string, this will only return the last part from, but not including, the comma
	pos% = Instr(s$, ",")
	;If pos = 0 then then end of the was reached, so return nothing
	If pos = 0 Then
		Return ""
	Else
		Return Mid(s$, pos+1, Len(s$)-pos)
	EndIf
End Function

; -----------------------------------------------------------------------------
; IniFirst String (return first part of string up to = sign)
; -----------------------------------------------------------------------------
Function ccIniFirstString$(s$)
	;pass in a string, this will only return the first part up to, but not including, the = sign (or end)		
	Return ccFirstStringToSub(s$, "=")
End Function

; -----------------------------------------------------------------------------
; IniLast String (return last part of string from = sign)
; -----------------------------------------------------------------------------
Function ccIniLastString$(s$)
	;pass in a string, this will only return the last part from, but not including, the = sign
	Return ccLastStringToSub(s$, "=")
End Function

; -----------------------------------------------------------------------------
; First String To Sub (return first part of string up to Substring)
; -----------------------------------------------------------------------------
Function ccFirstStringToSub$(s$, sub$)
	;pass in a string, this will only return the first part up to, but not including, the substring (or end)
	pos% = Instr(s$, sub$)
	;If pos = 0 then then end of the was reached, so return the whole thing.
	If pos = 0 Then
		Return s$
	Else
		Return Mid(s$, 1, pos-1)
	EndIf
End Function

; -----------------------------------------------------------------------------
; Last String To Sub (return last part of string from substring)
; -----------------------------------------------------------------------------
Function ccLastStringToSub$(s$, sub$)
	;pass in a string, this will only return the last part from, but not including, the substring
	pos% = Instr(s$, sub$)
	;If pos = 0 then then end of the was reached, so return nothing
	If pos = 0 Then
		Return ""
	Else
		Return Mid(s$, pos + Len(sub$), Len(s$)-pos)
	EndIf
End Function

; -----------------------------------------------------------------------------
; Percent To String
; -----------------------------------------------------------------------------
Function ccPercentToString(Per#)
	;simply times by 100 them look for decimal place and chop it and everything past it
	;warning no range checking is performed
	Local PerString$ = Per * 100
	Return Mid$(PerString, 1, Instr(PerString, ".", 1)-1)
End Function

; -----------------------------------------------------------------------------
; ccPadWithZeros
; -----------------------------------------------------------------------------
Function ccPadWithZeros$(TheText$, NumDigits%)
	;this does not truncate, only enlarges or does nothing
	While Len(TheText) < NumDigits 
		TheText = "0" + TheText
	Wend
	Return TheText
End Function

; -----------------------------------------------------------------------------
; ccGetEnvVar
; -----------------------------------------------------------------------------
Function ccGetEnvVar$(VariableName$)
    TempBank = CreateBank(1)
    size = api_GetEnvironmentVariable%(VariableName$,TempBank,1)
    If size > 0 Then
        ResizeBank TempBank,Size+1
        api_GetEnvironmentVariable%(VariableName$,TempBank,Size+1)
    Else
        Return ""
    End If

    retstring$ = ""
    For t = 0 To BankSize(tempbank)-3
        retstring$ = retstring$ + Chr(PeekByte(tempbank,t))
    Next
        
    FreeBank tempbank
    Return retstring$
End Function
