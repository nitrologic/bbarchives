; ID: 1623
; Author: zawran
; Date: 2006-02-17 10:23:28
; Title: Insert value into an array
; Description: A function to insert a value into any location of an array

Local a:Int[] = [0,1,2,3,4,6,7,8,9]

Print "Before: "+Len(a)+" fields"
For Local b:Int = 0 To 8
	Print a[b]
Next

a = insertIntoIntArray( a[..], 5, 5 )

Print "After: "+Len(a)+" fields"
For Local c:Int = 0 To 9
	Print a[c]
Next

Function insertIntoIntArray:Int[](Array:Int[],index:Int,value:Int)
	Local remain:Int = (Len(array)) - index
	Local result:Int[Len(array)+1]
	For Local count:Int = 0 To index-1	' copy all values before insert
		result[count] = Array[count]
	Next
	result[index] = value				' insert value
	For Local count2:Int = 0 To remain-1	' copy remaining values after insert
		result[index+count2+1] = Array[index+count2]
	Next
	Return result
End Function
