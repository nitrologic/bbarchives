; ID: 41
; Author: Cpt. Sovok
; Date: 2001-09-10 14:57:24
; Title: Number from String
; Description: Extract Numbers from Strings

; Extract Numbers from an String.
; Robert Gerlach 2001
; www.robsite.de

numberstring$ = "1,22,333"
Print numberstring$
Print stringtonumber(numberstring$, 1)
Print stringtonumber(numberstring$, 2)
Print stringtonumber(numberstring$, 3)


Function stringtonumber(numberstring$, position)
n_number = 1 ; Number Of Numbers. 
lastcomma = 1
For i = 1 To Len(numberstring$)
If Mid(numberstring$, i, 1) = "," Or i = Len(numberstring$) Then
If n_number = position Then number$ = Mid(numberstring$, lastcomma, i)
lastcomma = i+1
n_number = n_number + 1
EndIf
Next
Return number$
End Function

