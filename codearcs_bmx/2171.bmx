; ID: 2171
; Author: Zethrax
; Date: 2007-12-22 12:52:13
; Title: Functions to trim whitespace from the left or right of a string
; Description: RightTrim() and LeftTrim() functions to trim whitespace from the right or left of a string.

Print ">" + RightTrim( "   abc   " ) + "<"
Print ">" + LeftTrim( "   abc   " ) + "<"

End

Function RightTrim:String( value:String )
	For Local pos:Int = value.length - 1 To 0 Step -1
		If Asc( value[ pos..pos + 1 ] ) > 32
			value = value[ ..pos + 1 ]
			Exit
		EndIf
	Next
	Return value
End Function


Function LeftTrim:String( value:String )
	For Local pos:Int = 0 To value.length - 1
		If Asc( value[ pos..pos + 1 ] ) > 32
			value = value[ pos.. ]
			Exit
		EndIf
	Next
	Return value
End Function
