; ID: 171
; Author: wedoe
; Date: 2001-12-30 11:21:24
; Title: Highscore functions
; Description: Load, save and sort highscores !

; Highscore load/save/sort by Wedoe

Dim names$(21)
Dim scores(21)

; Your game goes here !

End
;-------------------------------- Read highscore from file
Function readhs()
file=ReadFile("highscore.sco")
For a=1 To 20
	names$(a)=ReadLine(file)
	scores(a)=ReadLine(file)
Next
CloseFile (file)
End Function
;-------------------------------- Write highscore to file
Function writehs()
file=WriteFile("highscore.sco")
For a=1 To 20
	WriteLine (file,names$(a))
	WriteLine (file,scores(a))
Next
CloseFile (file)
End Function
;------------------------------- Simple bubblesort
Function sorths()														
.sorths
swap=0
For a=1 To 20
	If scores(a+1) > scores(a) Then
		tmp1=scores(a) 
		tmp2$=names$(a)
		scores(a)=scores(a+1)
		names$(a)=names$(a+1)
		scores(a+1)=tmp1
		names$(a+1)=tmp2$
		swap=1
		EndIf	
Next
If swap=1 Then Goto sorths
End Function
;-------------------------------

; When you have some data for the scorelist
; always put last player in position 21
; and then sort the list.
; If he is worthy to get on the list he will
; and if not he will remain at number 21
; which you of course never prints out
