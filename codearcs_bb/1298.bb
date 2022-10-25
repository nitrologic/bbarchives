; ID: 1298
; Author: pexe
; Date: 2005-02-20 22:33:59
; Title: Transparent Background
; Description: A simple effect

Function TransparentBackground(Start_x%,Start_y%,Width%,Height%,Color_R% = 255,Color_G% = 255,Color_B% = 255)
End_y = Start_y%+Height%
End_x = Start_x%+Width%
WP_Color = (Color_B% Or (Color_G% Shl 8) Or (Color_R% Shl 16) Or ($ff000000))
LockBuffer
For y = Start_y% To End_y-1
	a = Abs(a-1)
	For x = Start_x%+(a*2) To End_x-1 Step 4
		WritePixelFast x,y,WP_Color
	Next
Next
UnlockBuffer
End Function
