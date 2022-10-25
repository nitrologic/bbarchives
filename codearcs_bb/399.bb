; ID: 399
; Author: Miracle
; Date: 2002-08-19 12:33:50
; Title: Sorted Linked Type Lists
; Description: Create and maintain lists sorted on an arbitrary field

Global NUM_COWS = 8

Type cow
	Field z
	Field pr.cow
	Field nx.cow
End Type

Type list
	Field firstcow.cow
	Field lastcow.cow
End Type

Global list.list = New list
For t = 1 To NUM_COWS
	moo.cow = New cow
	moo\z = Rand(0,100)
	SortCowsOnZ(moo)
Next

Print "Unsorted list:"
For moo.cow = Each cow
	Print moo\z
Next
Print
Print "Sorted list:"
moo = list\firstcow
Repeat
	Print moo\z
	moo = moo\nx
Until moo = Null

WaitKey()

End

Function SortCowsOnZ( cow.cow )
	moo.cow = list\lastcow
	done = 0
	If cow\nx <> Null And cow\pr <> Null Then
		If cow\pr <> Null Then cow\pr\nx = cow\nx
		If cow\nx <> Null Then cow\nx\pr = cow\pr
	EndIf
	If moo <> Null Then
		Repeat
			If moo\z >= cow\z Then
				cow\pr = moo
				cow\nx = moo\nx
				If moo\nx <> Null Then moo\nx\pr = cow
				moo\nx = cow
				done = 1
			EndIf
			moo = moo\pr
		Until moo = Null Or done = 1
	EndIf
	If done = 0 Then
		cow\nx = list\firstcow
		If list\firstcow <> Null Then list\firstcow\pr = cow
	EndIf
	If cow\nx = Null Then list\lastcow = cow
	If cow\pr = Null Then list\firstcow = cow
End Function
