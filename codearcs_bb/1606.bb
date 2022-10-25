; ID: 1606
; Author: Subirenihil
; Date: 2006-01-30 16:01:28
; Title: WritePixelFast color
; Description: Converts rgba color into WritePixelFast usable format.

Function ColorInt(r1%,g2%,b3%,a4%)
	c%=b+(g Shl 8)+(r Shl 16)+((a Mod 128) Shl 24)
	If a>128 Then c=c-2147483648
	Return c
End Function
