; ID: 1374
; Author: Perturbatio
; Date: 2005-05-14 14:17:28
; Title: SplitString
; Description: Function to split a string at the specified delimiters and returns a Tlist as the result

Function SplitString:TList(inString:String, Delim:String)
	Local tempList : TList = New TList
	Local currentChar : String = ""
	Local count : Int = 0
	Local TokenStart : Int = 0
	
	If Len(Delim)<>1 Then Return Null
	
	inString = Trim(inString)
	
	For count = 0 Until Len(inString)
		If inString[count..count+1] = delim Then
			tempList.AddLast(inString[TokenStart..Count])
			TokenStart = count + 1
		End If
	Next
	tempList.AddLast(inString[TokenStart..Count])	
	Return tempList
End Function

'Example usage:
Local myList:TList = SplitString("This is a longer test string that I am using to test this split string test thing", " ")

If myList Then
	For a$ = EachIn myList
		Print a$
	Next
EndIf
