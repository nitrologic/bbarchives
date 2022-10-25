; ID: 1062
; Author: Nilium
; Date: 2004-05-31 15:02:05
; Title: Color Functions
; Description: Functions to convert integer colors to normal RGB values and back

Const RALPHA = 24	;Return Alpha when using RColor
Const RRED = 16	;Return Red when using RColor
Const RGREEN = 8	;Return Green when using RColor
Const RBLUE = 0	;Return Blue when using RColor

; IntColor returns an integer color useable by WritePixel() and WritePixelFast()
; R = Red
; G = Green
; B = Blue
; A = Alpha.
; return = An integer color useable by functions such as WritePixel() and WritePixelFast()
Function IntColor(R,G,B,A=255)
	Return A Shl 24 Or R Shl 16 Or G Shl 8 Or B Shl 0
End Function

; RColor returns the value of Red, Green, Blue, or Alpha in C.
; c = An integer color returned by, for example, ReadPixelFast()
; c = The amount of bits C should be shifted right to.  Valid constants are: RRED, RGREEN, RBLUE, RALPHA
; return = An integer ranging from 0 to 255.
Function RColor%(c%,d%)
	Return c Shr d And 255 Shl 0
End Function
