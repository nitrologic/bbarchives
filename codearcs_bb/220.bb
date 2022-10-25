; ID: 220
; Author: Floyd
; Date: 2002-04-27 13:14:47
; Title: Insertion Sort
; Description: Example of sorting Blitz type collection.

; Example of Insertion Sort. For other data types, change the Index
; field and also the temp variable in the sort function.

Type Item
	Field Index%	; integer for this example
End Type

For n = 1 To 15 : t.Item = New Item : t\Index = Rand(9) : Next ; test data

ShowData
InsertionSort
ShowData

;=======================================================

Function InsertionSort()
Local Item.Item, NextItem.Item
Local p.Item, q.Item
Local temp%	; must be same type as the sort field.

	NextItem=After First Item
	While NextItem<>Null
		Item=NextItem
		NextItem=After Item
		p=Item : temp=Item\Index
		Repeat
			q=Before p
			If q=Null Then Exit
			If temp >= q\Index Then Exit
			p=q
		Forever
		q=Item
		Insert q Before p
	Wend
	
End Function

;=======================================================

Function ShowData( )
	For a.Item = Each Item
		Write a\Index+" "
	Next
	Print : Print
End Function
