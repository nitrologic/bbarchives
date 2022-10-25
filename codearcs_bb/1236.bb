; ID: 1236
; Author: PowerPC603
; Date: 2004-12-14 21:20:14
; Title: Alternate Replace command (2nd version)
; Description: Adds a NumberOfOccurances parameter to the command

Graphics 800, 600, 0, 2

StringToSearch$ = Input$("Enter Source-String: ")
StringToFind$ = Input$("Enter Find$-string: ")
StringToReplace$ = Input$("Enter Replace$-string: ")
NumOfOccurances% = Input$("Enter NumOfOcc%: ")

Print NewReplace$(StringToSearch$, StringToFind$, StringToReplace$, NumOfOccurances%)

WaitKey()



Dim OccPos%(0)
Function NewReplace$(SourceString$, OldString$, NewString$, NumOfOcc% = 0)
	Local TotalOcc%
	Local TargetString$

	Local Len_Source% = Len(SourceString$)
	Local Len_Old% = Len(OldString$)

	; Scan the entire string for each occurance and count them
	For i = 1 To ((Len_Source% - Len_Old%) + 1)
		If Mid$(SourceString$, i, Len_Old%) = OldString$ Then
			TotalOcc% = TotalOcc% + 1
		EndIf
	Next

	; Set maximum occurances (in case user stated more than present)
	If NumOfOcc% > TotalOcc% Then NumOfOcc% = TotalOcc%

	; Redim the array to hold the positions of all required occurances
	Dim OccPos(NumOfOcc%)

	; Create the new "TargetString$"
	If NumOfOcc% = 0 Then
		Return Replace$(SourceString$, OldString$, NewString$)
	Else
		; Find the positions of all occurances of the "OldString$" in the "SourceString$"
		For i = 1 To NumOfOcc%
			; Find the position of each occurance
			If i = 1 Then
				OccPos(i) = Instr(SourceString$, OldString$)
			Else
				OccPos(i) = Instr(SourceString$, OldString$, OccPos(i-1) + Len_Old%)
			EndIf
		Next

		; Create the new string
		; Copy the part before the first occurance to the "TargetString$"
		TargetString$ = Mid$(SourceString$, 1, OccPos(1) - 1)

		; Copy the "NewString$" for all but one of the occurances and the chars between each occurance
		For i = 1 To NumOfOcc% - 1
			If OccPos(i) <> 0 Then
				TargetString$ = TargetString$ + NewString$
				TargetString$ = TargetString$ + Mid$(SourceString$, OccPos(i) + Len_Old%, OccPos(i + 1) - (OccPos(i) + Len_Old%))
			EndIf
		Next

		; Copy the "NewString$" for the last occurance and the remaining part of the "SourceString$"
		If OccPos(NumOfOcc%) <> 0 Then
			TargetString$ = TargetString$ + NewString$
			TargetString$ = TargetString$ + Mid$(SourceString$, OccPos(NumOfOcc%) + Len_Old%)
		EndIf

		Return TargetString$
	EndIf
End Function
