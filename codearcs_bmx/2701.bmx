; ID: 2701
; Author: Shortwind
; Date: 2010-04-17 11:45:38
; Title: Bit Fiddling
; Description: Fiddle with bits in a Integer

'x=the Int with the value, bit is the bit to set, ie. 0-31

Function SetBit:Int(x:Int, bit:Int)
 	Return x | (1 Shl bit)
End Function

Function ClearBit:Int(x:Int, bit:Int)
	Return x & ~(1 Shl bit)
End Function

Function ToggleBit:Int(x:Int, bit:Int)
	Return x ~ (1 Shl bit)
End Function

Function ReadBit:Int(x:Int, bit:Int) 'This function returns 0 or 1...
	If (x & (1 Shl bit)) Return 1
	Return 0 
End Function
