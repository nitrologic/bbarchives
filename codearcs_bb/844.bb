; ID: 844
; Author: Bot Builder
; Date: 2003-11-30 19:28:34
; Title: Decimal to the length of the binary equivalent
; Description: Takes a decimal and quickly returns the length of the binary equivalent

Function BinaryLength(dec)
 Return Ceil(Log(dec)*1.4427)
End Function
