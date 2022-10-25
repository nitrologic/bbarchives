; ID: 346
; Author: Skully
; Date: 2002-06-17 17:55:49
; Title: GetRGB
; Description: Returns an RGB colour code for the red/green/blue components and back again

Function GetRed(rgb) 
	Return rgb Shr 16 And %11111111 
End Function 

Function GetGreen(rgb) 
	Return rgb Shr 8 And %11111111 
End Function 

Function GetBlue(rgb) 
	Return rgb And %11111111 
End Function

Function GetRGB(red,green,blue)
	Return blue Or (green Shl 8) Or (red Shl 16)
End Function
