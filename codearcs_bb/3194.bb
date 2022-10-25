; ID: 3194
; Author: Guy Fawkes
; Date: 2015-03-09 15:16:56
; Title: Easy Changeable Types
; Description: Easy Changeable Types

Type player

	Field name$
	Field age
	Field sex$

End Type

info.player = Initiate_Type ( )

; Create a new pointer variable of type player
info.player = MyFunction( info.player, "Bill", 36, "Male" )

	Locate 0, 0

		Print info\name$ + " - " + info\age
		Print
		Print "Sex: "+info\sex$

		Print

		Print "Press any key..."

	WaitKey

	Cls

info.player = MyFunction( info.player, "Rachel", 21, "Female" )

	Locate 0, 0

		Print info\name$ + " - " + info\age
		Print
		Print "Sex: "+info\sex$
		
		Print
		
		Print "Press any key to quit..."

	WaitKey

	Cls

End

; store the returned pointer in it.
; Note that the 'player' and 'player' pointer variables now both point to the same ~
; custom type object.

Function MyFunction.player( info.player, name$, age%, sex$ )

	info\name$ = name$
	info\age% = age%
	info\sex$ = sex$

	Return info

End Function

Function Initiate_Type.player ( )

	info.player = New player ; Create a new 'player' custom type object.
	
	Return info

End Function
