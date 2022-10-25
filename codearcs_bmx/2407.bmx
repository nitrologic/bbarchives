; ID: 2407
; Author: Otus
; Date: 2009-02-04 13:46:16
; Title: Round to power of two
; Description: Rounds a float to the closest power of two

Print Round(7) + "=" + 8.0
Print Round(1.0/17) + "=" + (1.0/16)
Print Round(1 Shl 9 + 1 Shl 7)+ "=" + Float(1 Shl 9)
Print Round(1 Shl 9 + 1 Shl 8)+ "=" + Float(1 Shl 10)

Function Round:Float(f:Float)
	Const MASK_EXPONENT:Int = %11111111 Shl 23
	Const MASK_NOMANTISSA:Int = %111111111 Shl 23
	Const MASK_MANTISSA_FIRST:Int = 1 Shl 22
	Const MASK_EXPONENT_LAST:Int = 1 Shl 23
	
	Local i:Int = Int Ptr( Varptr f )[0]
	
	If 0 > (i & MASK_EXPONENT) Or (i & MASK_EXPONENT) = MASK_EXPONENT
		' Zero, infinity, NaN...
		Return f
	End If
	
	If i&MASK_MANTISSA_FIRST
		' Round up
		i = (i & MASK_NOMANTISSA) + MASK_EXPONENT_LAST
	Else
		' Round down
		i = (i & MASK_NOMANTISSA)
	End If
	
	Return Float Ptr( Varptr i )[0]
End Function
