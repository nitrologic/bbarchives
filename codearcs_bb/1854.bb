; ID: 1854
; Author: b32
; Date: 2006-11-07 13:07:41
; Title: reverse a type list
; Description: how to reverse a custom type

;HOW TO REVERSE A SERIES OF A CUSTOM TYPE

;example custom type
Type pt
	Field i
End Type

;create 10 instances
For i = 0 To 9

	p.pt = New pt
	p\i = i

Next

;reverse instance order
For p.pt = Each pt
	If p <> Last pt Then 
		q.pt = After p
		Insert p Before First pt		
		p = Before q
	Else
		Insert p Before First pt
		Exit
	End If
Next

;show result
For p.pt = Each pt
	Print p\i
Next

WaitKey()
End
