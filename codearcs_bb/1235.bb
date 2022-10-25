; ID: 1235
; Author: PowerPC603
; Date: 2004-12-14 19:22:50
; Title: New Replace command
; Description: Replace-command with NumberOfOccurances parameter

Graphics 800, 600, 0, 2

StringToSearch$ = "This is a line where " + Chr(34) + "is" + Chr(34) + " must be replaced"
StringToFind$ = "is"
StringToReplace$ = "was"
NumOfOccurances% = 1

Print StringToSearch$
Print StringToFind$
Print StringToReplace$
Print NumOfOccurances%
Print

If NumOfOccurances% > 0 Then
	Print NewReplace$(StringToSearch$, StringToFind$, StringToReplace$, NumOfOccurances%)
Else
	Print NewReplace$(StringToSearch$, StringToFind$, StringToReplace$)
EndIf

WaitKey()



Function NewReplace$(SourceString$, OldString$, NewString$, NumOfOcc% = 0)
	Local TargetString$
	Local FindOccPos%

	Local Len_Old% = Len(OldString$)

	If NumOfOcc% = 0 Then
		; If number of occurances = 0, replace all occurances of "OldString$" with "NewString$"
		Return Replace$(SourceString$, OldString$, NewString$)
	Else
		; Proces the string as many times as stated by "NumOfOcc%"
		For i = 1 To NumOfOcc%
			; Find the first occurance of the "OldString$"
			FindOccPos% = Instr(SourceString$, OldString$)
			; If it's found, proces it
			If FindOccPos% > 0 Then
				; Copy the first part (before the "OldString$") to the "TargetString$"
				TargetString$ = Mid$(SourceString$, 1, FindOccPos% - 1)
				; Copy the "NewString$" to the end of "TargetString$"
				TargetString$ = TargetString$ + NewString$
				; Copy the rest of the "SourceString$" to the "TargetString$"
				TargetString$ = TargetString$ + Mid$(SourceString$, FindOccPos% + Len_Old%)

				; Replace the "SourceString$" by the newly created "TargetString$"
				; to begin reprocessing again (if needed = if NumOfOcc% > 1)
				SourceString$ = TargetString$
			EndIf
		Next

		; Return the new "SourceString$"
		Return SourceString$
	EndIf
End Function
