; ID: 221
; Author: Floyd
; Date: 2002-04-27 06:47:32
; Title: Insertion Sort with sentinel
; Description: A faster version of Insertion Sort.

; Example of Insertion Sort using a sentinel.
; This is a dummy item which marks the start of the list.

; Use one of the following as the sentinel:

MinStr$ = ""
MinInt% = -(2^31) ; This is $80000000
MinFlt# = -2 * ( 2^127 - 2^103 ) 
; Example uses integers. If this changes then change temp to match.

Type Item
	Field Index%	; integer for this example
End Type

t.Item = New Item : t\Index = MinInt ; sentinel, the smallest integer

For n = 1 To 15 : t = New Item : t\Index = Rand(9) : Next ; the actual data

ShowData
InsertionSortSentinel
ShowData

;=======================================================

; Needs a sentinel, a guaranteed first item.
; This simplifies and speeds up the sort. 

Function InsertionSortSentinel()
Local Item.Item, NextItem.Item, p.Item
Local temp%	; must be same type as the sort field.

	NextItem=After After First Item
	Repeat
		If NextItem = Null Then Return
		Item = NextItem : NextItem=After NextItem
		temp=Item\Index : p = Before Item
		While temp < p\Index
			p = Before p
		Wend
		Insert Item After p
	Forever
	
End Function

;=======================================================

Function ShowData( )
	For a.Item = Each Item
		Write a\Index+" "
	Next
	Print : Print
End Function
