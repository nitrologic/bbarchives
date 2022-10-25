; ID: 2518
; Author: Nilium
; Date: 2009-07-01 04:56:46
; Title: Format String
; Description: Light-weight-ish way to compile a string from one string and an array of objects

SuperStrict

Function FormatStringWithList$(format$, list:TList)
	Local arr:Object[]
	If list Then
		arr = list.ToArray()
	EndIf
	Return FormatString(format, arr)
End Function

Function FormatString$(format$, objects:Object[])
	Const CHAR_0% = 48
	Const CHAR_9% = 57
	Const CHAR_SLASH% = 92
	
	Local strings:String[objects.Length]
	
	For Local index:Int = 0 Until objects.Length
		If objects[index] Then
			strings[index] = objects[index].ToString()
		Else
			strings[index] = "Null"
		EndIf
	Next
	
	Local numberIndices:Int[] = [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1]
	Local numberLengths:Int[16]
	Local numbers:Int[16]
	Local numberIndex:Int = 0
	Local escape:Int = False
	Local length:Int = 0
	Local char:Int
	
	For Local index:Int = 0 Until format.Length
		char = format[index]
		
		If escape Then
			If CHAR_0 <= char And char <= CHAR_9 Then
				If numberLengths[numberIndex] = 0 Then
					numberIndices[numberIndex] = index-1
				EndIf
				
				char :- CHAR_0
				numbers[numberIndex] :* 10
				numbers[numberIndex] :+ char
				numberLengths[numberIndex] :+ 1
				Continue
			ElseIf numberIndices[numberIndex] <> -1 Then
				numbers[numberIndex] :- 1
				Assert numbers[numberIndex] >= 0 And numbers[numberIndex] < strings.Length ..
				 	Else "FormatString: Index out of range: index must be >= 1 and <= objects.Length"
				length :+ strings[numbers[numberIndex]].Length
				numberIndex :+ 1
				escape = False
			EndIf
		EndIf
		
		If char = CHAR_SLASH Then
			If Not escape Then
				If numberIndex >= numbers.Length Then
					numbers = numbers[..numbers.Length*2]
					numberLengths = numberLengths[..numbers.Length]
					numberIndices = numberIndices[..numbers.Length]
				EndIf
				escape = True
				Continue
			Else
				escape = False
			EndIf
		EndIf
		
		length :+ 1
	Next
	
	If escape And numberIndices[numberIndex] <> -1 Then
		numbers[numberIndex] :- 1
		length :+ strings[numbers[numberIndex]].Length
	EndIf
	
	Local buffer:Short Ptr = Short Ptr MemAlloc(length * 2)
	Local p:Short Ptr = buffer
	
	numberIndex = 0
	escape = False
	For Local index:Int = 0 Until format.Length
		If index = numberIndices[numberIndex] Then
			index :+ numberLengths[numberIndex]
			Local innerString$ = strings[numbers[numberIndex]]
			For Local innerStringIndex:Int = 0 Until innerString.Length
				p[0] = innerString[innerStringIndex]
				p :+ 1
			Next
			numberIndex :+ 1
			Continue
		ElseIf Not escape And format[index] = CHAR_SLASH Then
			escape = True
			Continue
		EndIf
		escape = False
		
		p[0] = format[index]
		p :+ 1
	Next
	
	Local output$ = String.FromShorts(buffer, length)
	MemFree(buffer)
	Return output
End Function
