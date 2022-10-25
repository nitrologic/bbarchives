; ID: 1560
; Author: CoderLaureate
; Date: 2005-12-12 10:34:20
; Title: SmartSplit Function
; Description: Splits Strings Intelligently.

Function SmartSplit:String[](str:String, dels:String, text_qual:String = "~q")
	Local Parms:String[] = New String[1]
	Local pPtr:Int = 0
	Local chPtr:Int = 0
	Local delPtr:Int = 0
	Local qt:Int = False
	Local str2:String = ""
	
	Repeat
		Local del:String = Chr(dels[delPtr])
		Local ch:String = Chr(str[chPtr])
		If ch = text_qual Then 
			If qt = False Then
				qt = True
			Else
				qt = False
			End If
		End If
		If ch = del Then
			If qt = True Then str2:+ ch
		Else
			str2:+ ch
		End If
		If ch = del Or chPtr = str.Length - 1 Then
			If qt = False Then
				Parms[pPtr] = str2.Trim()
				str2 = ""
				pPtr:+ 1
				Parms = Parms[..pPtr + 1]
				If dels.length > 1 And delPtr < dels.length Then delPtr:+ 1
			End If
		End If
		chPtr:+ 1
		If chPtr = str.Length Then Exit
	Forever
	If Parms.Length > 1 Then Parms = Parms[..Parms.Length - 1]
	Return Parms
			
End Function
	

'Test Code
'------------------------------------------------------------------------------
Local i:Int

Print "~r~n~qjim,ami,liz~q, ~q,~q"
Local p:String[] = SmartSplit("jim,ami,liz",",")

For i = 0 To p.Length - 1
	Print i + ": " + p[i]
Next


Print "~r~n~qCommand(parm1 + ~q, ~q + parmB, parm2)~q, ~q(,)~q"
Local p2:String[] = SmartSplit("Command(parm1 + ~q, ~q + parmB, parm2)","(,)")

For i = 0 To p2.Length - 1
	Print i + ": " + p2[i]
Next
