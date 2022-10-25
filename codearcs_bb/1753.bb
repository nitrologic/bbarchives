; ID: 1753
; Author: Jesse B Andersen
; Date: 2006-07-14 16:46:09
; Title: Get File Function
; Description: Removes the path and returns only the file name in a string

;Jesse B Andersen
;jesse.andersen@live.com
;www.jessebandersen.com
;Removes path

Print Get_File("c:\documents\myfile.txt")
Delay 1000

Function Get_File$(file$)
	For i = 1 To Len(file$)
		If Mid$(file$,i,1) = "\" Then
			c = 0
		EndIf
		c = c + 1
	Next
	Return Right(file$, c-1)
End Function
