; ID: 3033
; Author: Hezkore
; Date: 2013-02-20 13:01:23
; Title: Integers inside longer integers
; Description: A simple way to store 2 bytes in a short, 4 bytes in an int or 2 shorts in an int.

'=Usage===================================================================
'Storage variables
Local byte_myShort:Short
Local byte_myInt:Int
Local short_myInt:Int

'--------------------------------
'Store bytes in our short
storeByte_short(byte_myShort, 26, 0)
storeByte_short(byte_myShort, 205, 1)

'Show stored bytes from our short
Print("(Byte)Short: " + Right(Bin(byte_myShort), SizeOf(byte_myShort) * 8))
Print(getByte_short(byte_myShort, 0))
Print(getByte_short(byte_myShort, 1))
Print()
Delay(1000)

'--------------------------------
'Store bytes in our int
storeByte_int(byte_myInt, 3, 0)
storeByte_int(byte_myInt, 26, 1)
storeByte_int(byte_myInt, 128, 2)
storeByte_int(byte_myInt, 205, 3)

'Show stored bytes from our int
Print("(Byte)Int: " + Right(Bin(byte_myInt), SizeOf(byte_myInt) * 8))
Print(getByte_int(byte_myInt, 0))
Print(getByte_int(byte_myInt, 1))
Print(getByte_int(byte_myInt, 2))
Print(getByte_int(byte_myInt, 3))
Print()
Delay(1000)

'--------------------------------
'Store shorts in our int
storeShort_int(short_myInt, 256, 0)
storeShort_int(short_myInt, 65535, 1)

'Show stored shorts from our int
Print("(Short)Int: " + Right(Bin(short_myInt), SizeOf(short_myInt) * 8))
Print(getShort_int(short_myInt, 0))
Print(getShort_int(short_myInt, 1))
Print()
Delay(1000)

End
'=Functions===============================================================
Function storeByte_int(pntr:Int Var, value:Byte, pos:Byte)		'Holds 4 Bytes
	pntr:+(value Shl ((pos Mod SizeOf(pntr)) Shl 3))
End Function
Function getByte_int:Byte(pntr:Int Var, pos:Byte)				'Holds 4 Bytes
	Return(pntr Shr ((pos Mod SizeOf(pntr)) Shl 3))
End Function

Function storeByte_short(pntr:Short Var, value:Byte, pos:Byte)	'Holds 2 Bytes
	pntr:+(value Shl ((pos Mod SizeOf(pntr)) Shl 3))
End Function
Function getByte_short:Byte(pntr:Short Var, pos:Byte)			'Holds 2 Bytes
	Return(pntr Shr ((pos Mod SizeOf(pntr)) Shl 3))
End Function

Function storeShort_int(pntr:Int Var, value:Short, pos:Byte)	'Holds 2 Shorts
	pntr:+(value Shl ((pos Mod SizeOf(pntr)) Shl 4))
End Function
Function getShort_int:Short(pntr:Int Var, pos:Byte)				'Holds 2 Shorts
	Return(pntr Shr ((pos Mod SizeOf(pntr)) Shl 4))
End Function
