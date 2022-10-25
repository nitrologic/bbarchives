; ID: 230
; Author: AngelEyes
; Date: 2002-02-11 06:35:33
; Title: Split CSV parameters out of a string
; Description: CSV allows text to be encapsulated in "" marks - a simple Split command is no use for getting these parameters out, as the ',' may appear within the string. This is a dirty way of splitting the strings down.

Dim split_params$(500)

Function SplitCSVLine%(l$,delim$)
	param = 0
	split_params$(param) = ""
	quote = False
	For loopy = 1 To Len(l$)
		bit$ = Mid$(l$,loopy,1)
		If bit$=Chr$(34) Then
			If quote = False Then
				quote = True 
			Else
				quote = False
			End If
		ElseIf bit$=delim$ And quote = False Then
				; end of param
				param = param + 1
				split_params$(param) = ""
		Else
				split_params$(param)=split_params$(param)+bit$
		End If
	Next
	Return param 
End Function
