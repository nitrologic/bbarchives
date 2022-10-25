; ID: 1581
; Author: Perturbatio
; Date: 2005-12-28 06:25:30
; Title: URL String Encode and Decode
; Description: Use these functions to encode and decode a URL in BMax

SuperStrict

Function EncodeString:String(value:String, EncodeUnreserved:Int = False, UsePlusForSpace:Int = True)
	Local ReservedChars:String = "!*'();:@&=+$,/?%#[]~r~n"  'added space, newline and carriage returns
	Local rc:Int
	Local urc:Int
	Local s:Int
	Local result:String

	For s = 0 To value.length - 1
		If ReservedChars.Find(value[s..s + 1]) > -1 Then
			result:+ "%"+ IntToHexString(Asc(value[s..s + 1]))
			Continue
		ElseIf value[s..s+1] = " " Then
			If UsePlusForSpace Then result:+"+" Else result:+"%20"
			Continue
		ElseIf EncodeUnreserved Then
				result:+ "%" + IntToHexString(Asc(value[s..s + 1]))
			Continue
		EndIf
		result:+ value[s..s + 1]
	Next

	Return result
End Function


Function DecodeString:String(EncStr:String)
	Local Pos : Int = 0
	Local HexVal : String
	Local Result : String
	Local starttime:Int = MilliSecs()

	While Pos<Len(EncStr)
		If EncStr[Pos..Pos+1] = "%" Then
			HexVal = EncStr[Pos+1..Pos+3]
			Result :+ Chr(HexToInt(HexVal))
			Pos:+3
		ElseIf EncStr[Pos..Pos+1] = "+" Then
			Result :+ " "
			Pos:+1
		Else
			Result :+ EncStr[Pos..Pos+1]
			Pos:+1	
		EndIf
	Wend
	
	Return Result
End Function


Function HexToInt:Int( HexStr:String )
	If HexStr.Find("$") <> 0 Then HexStr = "$" + HexStr$
	Return Int(HexStr)
End Function


Function IntToHexString:String(val:Int, chars:Int = 2)
	Local Result:String = Hex(val)
	Return result[result.length-chars..]
End Function


Local enc:String = EncodeString("normalization and <> replacing spaces with ~q+~q instead of ~q%20~q.~n~r~n~r", True)
Print enc
Print decodeString(enc)
Print EncodeString("http://www.blitzbasic.com", True)
