; ID: 2271
; Author: Devils Child
; Date: 2008-06-13 16:58:36
; Title: Alphabetical sort
; Description: This function checks two strings and tells which one is higher in alphabet.

;Return values of this function:
;    -1  => both strings are identical
;     1  => s1$ is higher in alphabet
;     0  => s2$ is higher in alphabet

;This function is NOT case sensitive.

Function SortStrings(s1$, s2$)
If Len(s1$) > Len(s2$) Then
	While Len(s1$) > Len(s2$)
		s2$ = s2$ + Chr(1)
	Wend
ElseIf Len(s2$) > Len(s1$) Then
	While Len(s2$) > Len(s1$)
		s1$ = s1$ + Chr(1)
	Wend
EndIf
ln = Len(s1$)
For i = 1 To ln
	ch1 = Asc(Mid(s1$, i, 1))
	If ch1 => 65 And ch1 <= 90 Then ch1 = Asc(Lower(Chr(ch1)))
	ch2 = Asc(Mid(s2$, i, 1))
	If ch2 => 65 And ch2 <= 90 Then ch2 = Asc(Lower(Chr(ch2)))
	
	If ch1 < ch2 Then
		Return True
	ElseIf ch1 > ch2 Then
		Return False
	EndIf
Next
Return -1
End Function
