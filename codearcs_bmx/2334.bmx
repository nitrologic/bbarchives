; ID: 2334
; Author: Aelin
; Date: 2008-10-16 16:47:07
; Title: Bit Access
; Description: Read/Write bits in a byte.

Const BIT_0			 = 1
Const BIT_1			 = 2
Const BIT_2			 = 4
Const BIT_3			 = 8
Const BIT_4			 = 16
Const BIT_5			 = 32
Const BIT_6			 = 64
Const BIT_7			 = 128

Function SetBit(B:Byte Var, Bit:Int) 
	B :| Bit
End Function

Function ClearBit(B:Byte Var, Bit:Int) 
	B :- Bit
End Function

Function GetBit:Int(B:Byte, Bit:Int)
	Return B & Bit > 0
End Function

' - TEST CODE, DELETE THIS.
Global Test:Byte = 0

SetBit(Test, BIT_0) 
SetBit(Test, BIT_2) 
SetBit(Test, BIT_4) 
SetBit(Test, BIT_6) 
ClearBit(Test, BIT_6)
Print("BIT_0: " + GetBit(Test, BIT_0) ) 
Print("BIT_1: " + GetBit(Test, BIT_1))
Print("BIT_2: " + GetBit(Test, BIT_2))
Print("BIT_3: " + GetBit(Test, BIT_3))
Print("BIT_4: " + GetBit(Test, BIT_4) ) 
Print("BIT_5: " + GetBit(Test, BIT_5))
Print("BIT_6: " + GetBit(Test, BIT_6))
Print("BIT_7: " + GetBit(Test, BIT_7))
