; ID: 1566
; Author: Chroma
; Date: 2005-12-15 11:57:48
; Title: Send Multi-Dimensional Arrays to a Function
; Description: This let's you send arrays like MyArray[5,5,5] to a function and return it.

Print

'Make array1 and fill it with values
Local array1:Int[2,2]
For b=0 To 1
	For a=0 To 1
		array1[a,b]=a
		Print "array1 "+a+","+b+" = "+array1[a,b]
	Next
Next
Print

'Make array2
Local array2:Int[,]

'Pass array1 to the function and get array1 back and put it in array2
array2 = ArrayFunc(array1)

'Show the results
Print
For b=0 To 1
	For a = 0 To 1
		Print "Back From ArrayFunc "+a+","+b+" = "+array2[a,b]
	Next
Next

End



Function ArrayFunc[,](this:Int[,])
	For b=0 To this.length/2-1
		For a=0 To this.length/2-1
			Print "Inside ArrayFunc "+a+","+b+" = "+this[a,b]
			this[a,b]:+5
		Next
	Next
	Return this
End Function
