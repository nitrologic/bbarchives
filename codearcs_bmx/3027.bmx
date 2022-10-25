; ID: 3027
; Author: daaan
; Date: 2013-02-04 18:23:50
; Title: Calculate UPC-A Check Digit
; Description: Converts a 11 digit UPC to 12 digits.

Function CalcCheckDigit_UPCA:String(UPC:String="")
	
	Local CheckDigit:Int = 0
	
	For i:Int = 0 Until UPC.Length
		
		CheckDigit :+ (Chr(UPC[i]).ToInt() * (1 + 2 * (i Mod 2)))
		
	Next
	
	Return (UPC+(10-(CheckDigit Mod 10)))
	
End Function
