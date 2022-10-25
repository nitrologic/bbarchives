; ID: 2627
; Author: Nate the Great
; Date: 2009-12-11 18:35:04
; Title: faster nested loops
; Description: intuitive arrays for faster nested loops

Type Array		'array to act like tlist except a million times faster
	Field a:Object[10]	'starts off with 100 objects...
	Field last:Int
		
	Function Create:Array(Length:Int)
		Local a:array = New array
		a.a = a.a[..length]
		Return a:array
	End Function
	
	Method length:Int()
		Return (last-1)
	End Method
	
	Method add:Int(obj:Object)
		a[last] = obj
		If a.length < last+2 Then
			a = a[..(a.length+1000)]
		EndIf
		last :+ 1
		Return (last-1)
	End Method
	
	Method Getbyindex:Object(in:Int)
		Return a[in]
	End Method
	
	Method remove:Object(index:Int)
		a[index] = a[last-1]
		a[last-1] = Null
		last :- 1
		Return a[index]
	End Method
End Type
