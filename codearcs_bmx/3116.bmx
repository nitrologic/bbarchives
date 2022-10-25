; ID: 3116
; Author: Hardcoal
; Date: 2014-03-30 07:51:49
; Title: ElementNaming
; Description: A Process for naming an element avoiding repeated name

Function NamingProcess:String(WantedName:String, ListOfNames:TList)    'By Hardcoal
                Local Counter, NewName:String
		
                If WantedName = "" Then WantedName = "NewElement"
		
		NewName = WantedName
		
		Repeat

			If CheckNameExistence(NewName, ListOfNames) = False Then
				Return NewName
			Else
				Counter = Counter + 1
				NewName = WantedName + " " + Counter
			End If

		Forever
		
		Function CheckNameExistence(Name:String, ListOfNames:TList)
			Local TempName:String
			For TempName = EachIn ListOfNames
				If LowerTrim(Name) = LowerTrim(TempName) Then Return True
			Next
		End Function
		
		Function LowerTrim:String(AString:String)
			Return Trim(Lower(AString))
		End Function

End Function
