; ID: 1501
; Author: Perturbatio
; Date: 2005-10-23 23:54:16
; Title: Array concatenation functions
; Description: Easily concatenate arrays or portions of them

Local a:Float[] = [0.1,1.2,2.3,3.4,3.4,4.5,5.6,6.7,7.8,8.9,9.99]

a = ConcatFloatArray(a[..3], a[4..])

Print a[3]



Function ConcatIntArray:Int[](Array1:Int[], Array2:Int[])
	Local result:Int[]
	
	For Local count:Int = 0 To Len(Array1)-1
		result = result[..Len(result)+1]
		result[count] = Array1[count]
	Next
	
	For Local count2:Int = 0 To Len(Array2)-1
		result = result[..Len(result)+1]
		result[count+count2] = Array2[count2]
	Next
	
	Return result
End Function




Function ConcatFloatArray:Float[](Array1:Float[], Array2:Float[])
	Local result:Float[]
	
	For Local count:Float = 0 To Len(Array1)-1
		result = result[..Len(result)+1]
		result[count] = Array1[count]
	Next
	
	For Local count2:Float = 0 To Len(Array2)-1
		result = result[..Len(result)+1]
		result[count+count2] = Array2[count2]
	Next
	
	Return result
End Function




Function ConcatDoubleArray:Double[](Array1:Double[], Array2:Double[])
	Local result:Double[]
	
	For Local count:Double = 0 To Len(Array1)-1
		result = result[..Len(result)+1]
		result[count] = Array1[count]
	Next
	
	For Local count2:Double = 0 To Len(Array2)-1
		result = result[..Len(result)+1]
		result[count+count2] = Array2[count2]
	Next
	
	Return result
End Function




Function ConcatByteArray:Byte[](Array1:Byte[], Array2:Byte[])
	Local result:Byte[]
	
	For Local count:Byte = 0 To Len(Array1)-1
		result = result[..Len(result)+1]
		result[count] = Array1[count]
	Next
	
	For Local count2:Byte = 0 To Len(Array2)-1
		result = result[..Len(result)+1]
		result[count+count2] = Array2[count2]
	Next
	
	Return result
End Function
