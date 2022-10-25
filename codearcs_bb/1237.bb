; ID: 1237
; Author: PowerPC603
; Date: 2004-12-15 10:24:11
; Title: Again a new Replace command
; Description: This command accepts a NumOfOcc parameter and a CaseSensitive parameter

Graphics 800, 600, 0, 2

StringToSearch$ = Input$("Enter Source-String: ")
StringToFind$ = Input$("Enter Find$-string: ")
StringToReplace$ = Input$("Enter Replace$-string: ")
NumOfOccurances% = Input$("Enter NumOfOcc%: ")
CaseSens% = Input$("Case-sensitive (enter 0 or 1): ")

Print NewReplace$(StringToSearch$, StringToFind$, StringToReplace$, NumOfOccurances%, CaseSens%)

WaitKey()



Function NewReplace$(SourceString$, OldString$, NewString$, NumOfOcc% = 0, CaseSensitive% = True)
	Local TargetString$

	Local Len_Source% = Len(SourceString$)
	Local Len_Old% = Len(OldString$)

	If NumOfOcc% = 0 Then
		; If user stated "0" occurances, replace them all
		Return Replace$(SourceString$, OldString$, NewString$)
	Else
		; Scan the entire string for each occurance
		For i = 1 To Len_Source%
			If CaseSensitive% = False Then
				; If stated NOT "Case-sensitive"
				; Convert the PartToSearch$ to LowerCase
				PartOfSource$ = Lower$(Mid$(SourceString$ , i, Len_Old%))
				; Convert the Find$-string to LowerCase
				OldString$ = Lower$(OldString$)
			Else
				; If stated "Case-sensitive", keep things original (don't convert to LowerCase)
				PartOfSource$ = Mid$(SourceString$ , i, Len_Old%)
			EndIf

			If (PartOfSource$ = OldString$) And (NumOfOcc% > 0) Then
				; If the occurance has been found and the number of occurances > 0,
				; Copy the "NewString$"
				TargetString$ = TargetString$ + NewString$
				; Skip the number of chars of the "OldString$"
				i = i + Len_Old% - 1
				; Decrease the remaining number of occurances to replace by 1
				NumOfOcc% = NumOfOcc% - 1
			Else
				; If the occurance hasn't been found, copy the current character
				TargetString$ = TargetString$ + Mid$(SourceString$, i, 1)
			EndIf
		Next
	EndIf

	; Return the "TargetString$"
	Return TargetString$
End Function
