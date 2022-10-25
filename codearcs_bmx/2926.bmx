; ID: 2926
; Author: EdzUp[GD]
; Date: 2012-03-04 05:06:22
; Title: Int to RGBA and back
; Description: convert Int to its components and back

Function IntToRGBA( Value:Int, r:Byte Var, G:Byte Var, B:Byte Var, A:Byte Var )
	a = ( Value Shr 24 ) & $FF
	r = ( Value Shr 16) & $FF
	g = ( Value Shr 8) & $FF
	b = Value & $FF
End Function

Function RGBAtoInt:Int( red:Byte, Green:Byte, Blue:Byte, Alpha:Byte )
	Return ( alpha Shl 24 | red Shl 16 | green Shl 8 | blue )
End Function
