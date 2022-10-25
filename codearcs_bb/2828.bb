; ID: 2828
; Author: Diego
; Date: 2011-02-25 09:09:06
; Title: RGB &lt;=&gt; HSL
; Description: Converts RGB to HSL - Integer Version

Global ret_h%, ret_s%, ret_l%
Global ret_R%, ret_G%, ret_B%

Graphics 360, 276, 0, 2
AppTitle "HSL Demo"
SetFont LoadFont("Arial", 20)
Color 255, 255, 255
LockBuffer()
MS% = MilliSecs()
For Y% = 0 To 255
	For X% = 0 To 359
		hsl_to_rgb(X%, 255 - Y%, 128)
		WritePixel X%, Y%, ret_r% Shl 16 Or ret_g% Shl 8 Or ret_b%
		Next
	Next
MS% = MilliSecs() - MS%
UnlockBuffer
Text 2, 256, MS% + " millisecs taken. Press any key to end."
WaitKey

; Converts a RGB color to hsl.
;
; Params:
; R, G, B: red, green & blur in range [0-255]
;
; Returns:
; ret_h - Hue component [0-255].
; ret_s - Saturation component [0-255].
; ret_l - Luminance component [0-359].

Function rgb_to_hsl(R%, G%, B%)
If R% > G% Then
	max_is% = 0
	max_color% = R%
	min_color% = G%
	Else
	max_is% = 1
	max_color% = G%
	min_color% = R%
	EndIf
If B% > max_color% Then
	max_is% = 2
	max_color% = B%
	ElseIf B% < min_color%
	min_color% = B%
	EndIf
	
ret_l% = (max_color% + min_color%) Shr 1	; Luminance
If max_color% = min_color%
	; Color is grey.
	ret_s% = 0
	Else
	delta% = max_color% - min_color%
	; Saturation
	If ret_l% < 128 Then
		ret_s% = 255 * delta% / (max_color% + min_color%)
		Else
		ret_s% = 255 * delta% / (512 - max_color% - min_color%)
		EndIf
	
	; Hue
	Select max_is%
		Case 0 ; Red
			ret_h% = 60 * (G% - B%) / delta%
		Case 1 ; Green
			ret_h% = 120 + 60 * (B% - R%) / delta%
		Case 2 ; Blue
			ret_h% = 240 + 60 * (R% - G%) / delta%
		End Select
	If ret_h% < 0 Then ret_h% = ret_h% + 360
	EndIf
End Function


; Converts a HSL color to RGB.
;
; Params:
; h, s, l - Color's hue [0-359], saturation [0-255] and luminance [0-255] components.
;
; Returns:
;	The converted color's RGB values via the following globals:
;   ret_R%, ret_G%, ret_B% in [0-255]

Function hsl_to_rgb(h%, s%, l%)
If s% = 0 Then
	ret_r% = l%
	ret_g% = l%
	ret_b% = l%
	Else
	If l% < 128 Then temp2% = l% * (255 + s%) / 255	Else temp2% = (l% + s%) - (l% * s%) / 255
	temp1% = 2 * l% - temp2%
	
	rtemp3% = h% + 120
	If rtemp3% > 360 Then rtemp3% = rtemp3% - 360
	gtemp3% = h%
	btemp3% = h% + 240
	If btemp3% > 360 Then btemp3% = btemp3% - 360
	
	If rtemp3% < 60 Then ; Set red
		ret_R% = temp1% + (temp2% - temp1%) * rtemp3% / 60
		ElseIf rtemp3% < 180 Then
		ret_R% = temp2%
		ElseIf rtemp3% < 240 Then
		ret_R% = temp1% + (temp2% - temp1%) * (240 - rtemp3%) / 60
		Else
		ret_R% = temp1%
		EndIf
	If gtemp3% < 60 Then ; Set green
		ret_G% = temp1% + (temp2% - temp1%) * gtemp3% / 60
		ElseIf gtemp3% < 180 Then
		ret_G% = temp2%
		ElseIf gtemp3% < 240 Then
		ret_G% = temp1% + (temp2% - temp1%) * (240 - gtemp3%) / 60
		Else
		ret_G% = temp1%
	EndIf
	If btemp3% < 60 Then ; Set blue
		ret_B% = temp1% + (temp2% - temp1%) * btemp3% / 60
		ElseIf btemp3% < 180 Then
		ret_B% = temp2%
		ElseIf btemp3% < 240 Then
		ret_B% = temp1% + (temp2% - temp1%) * (240 - btemp3%) / 60
		Else
		ret_B% = temp1%
	EndIf
EndIf
End Function
