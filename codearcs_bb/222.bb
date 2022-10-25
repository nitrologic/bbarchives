; ID: 222
; Author: Jim Brown
; Date: 2002-06-18 15:15:03
; Title: Waggler
; Description: Keyboard/Joystick waggler function

;; Waggler - by Syntax_Error
; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

; A little bit of code for joystick/keyboard waggling
; games like the classic Decathlon & Hypersports


; #######################
Dim wkey(1) : wkey(0)=42 : wkey(1)=54	; left/right SHIFT keys (scancodes)
Global WaggleToggle			; waggle toggle flag

; waggle function
Function GetWaggleSpeed#(speed#,inputmethod=0)
	If inputmethod=1
		If JoyXDir()=WaggleToggle*2-1
			WaggleToggle=Not WaggleToggle
			speed=speed+5.7
		EndIf
	Else
		If KeyHit(wkey(WaggleToggle))
			WaggleToggle=Not WaggleToggle
			speed=speed+7.9
		EndIf
	EndIf
	If speed>0.89 Then speed=speed-0.89
	If speed>100 Then speed=100
	Return speed
End Function
; #######################


; example code
Graphics 640,480
SetBuffer BackBuffer()

wagglespeed#=75

While Not KeyHit(1)
	Cls
	wagglespeed#=GetWaggleSpeed(wagglespeed#,method)
	; simple bar
	Color 40,40,40 : Rect 100,100,201,16
	Color 20,200,40 : Rect 101,101,wagglespeed*2,14
	Text 125,128,"Speed:"+Int(wagglespeed)
	;
	If KeyHit(57) method=Not method
	Color 100,200,200
	Text 100,160,"Input Method: "+Mid$("KEYBOARDJOYSTICK",method*8+1,8)
	Text 140,180,"(Press SPACEBAR to toggle)"
	Flip
Wend

End
