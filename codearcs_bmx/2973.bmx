; ID: 2973
; Author: BlitzSupport
; Date: 2012-08-27 14:58:56
; Title: Convert hex string to byte array
; Description: Converts a hex string to a byte array

Function HexToByteArray:Byte [] (hx:String)
	
	Local bytes:Byte [Len (hx) / 2]
	
	For Local loop:Int = 1 To Len (hx) Step 2
		bytes [loop / 2] = Byte ("$" + Mid (hx, loop, 2))
	Next
	
	Return bytes
	
End Function
