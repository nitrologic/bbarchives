; ID: 585
; Author: Snarty
; Date: 2003-02-12 22:12:04
; Title: R, G, B To LockedFormat Short/Int
; Description: Converts Red, Green, Blue elements into 1 Short/Integer

Function ConvertRGB(r,g,b,Mode=0)

	Select Mode
		
		Case 1
			Col=((r/8) Shl 11) Or ((g/4) Shl 5) Or (b/8) 
			Return Col
		
		Case 2
			Col=((r/8) Shl 10) Or ((g/8) Shl 5) Or (b/8) 
			Return Col
					
		Default
			Col=(r Shl 16) Or (g Shl 8) Or b
			Return Col
			
	End Select

End Function
