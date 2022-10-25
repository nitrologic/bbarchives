; ID: 1399
; Author: klepto2
; Date: 2005-06-14 01:13:59
; Title: 2 Split String to Array funcs
; Description: 2 Functions to divide a string at given chars

Strict

Local I:Int



Print "Return_strip2 : ('Load Map:Test.dat')"

Local Strip:String[]
Strip = Return_Strip2("Load Map:Test.dat"," :")
For I = 0 To Strip.length-1
Print I + " : " + Strip[I]
Next

Print "Return_strip  : ('Load Map:Test.dat')"

Local Strip2:String[]
Strip2 = Return_Strip("Load Map:Test.dat",32)
For I = 0 To Strip2.length-1
Print I + " : " + Strip2[I]
Next




Function Return_Strip2:String[](_String:String,_strip:String)

	Local Text_Array : String[1]
	Local R_Text:String = _string
	Local i:Int = 0

	Repeat
		If R_Text.Length = 0 Then Exit
			Local sp_p:Int = R_Text.Find(Mid(_strip,I+1,1))
			If sp_p = - 1 Or I > _strip.length -1 Then
				Text_Array[I] = R_Text
				Exit
			End If
			Text_Array[I] = Left(R_Text,sp_p)
			R_Text = Right(R_Text,(R_text.Length - sp_p)-1)
			I:+1
			Text_Array = Text_array[..I+1]
	Forever
	
	Return Text_array	
	
End Function

Function Return_Strip:String[](_String:String,_strip:Int)

	Local Text_Array : String[1]
	Local R_Text:String = _string
	Local i:Int = 0

	Repeat
		If R_Text.Length = 0 Then Exit
			Local sp_p:Int = R_Text.Find(Chr(_strip))
			If sp_p = - 1  Then
				Text_Array[I] = R_Text
				Exit
			End If
			Text_Array[I] = Left(R_Text,sp_p)
			R_Text = Right(R_Text,(R_text.Length - sp_p)-1)
			I:+1
			Text_Array = Text_array[..I+1]
	Forever
	
	Return Text_array	
	
End Function
