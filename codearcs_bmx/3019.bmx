; ID: 3019
; Author: jkrankie
; Date: 2013-02-01 12:53:03
; Title: GetKey()
; Description: Gets key scancode

Function GetKey:Int()
	Local thiskey:Int = 0
	For Local i:Int = 1 To 255
		If KeyHit(i)
		thiskey = i
		Exit
		EndIf
	Next
	Return thiskey
End Function

Function getKeyCodeString:String(inNum:Int)

If inNum=8  Then Return "Backspace"
If inNum=9  Then Return "Tab"
If inNum=12 Then Return "Clear"
If inNum=13 Then Return "Return"
If inNum=19 Then Return "Pause"
If inNum=20 Then Return "Caps Lock"
If inNum=32 Then Return "Space"
If inNum=33 Then Return "Page Up"
If inNum=34 Then Return "Page Down"
If inNum = 35 Then Return "End"
If inNum=36 Then Return "Home"
If inNum=37 Then Return "Left"
If inNum=38 Then Return "Up"
If inNum=39 Then Return "Right"
If inNum=40 Then Return "Down"
If inNum=41 Then Return "Select"
If inNum=42 Then Return "'print"
If inNum=43 Then Return "Execute"
If inNum=44 Then Return "Screen"
If inNum=45 Then Return "Insert"
If inNum=46 Then Return "Delete"
If inNum=47 Then Return "Help"
If inNum=48 Then Return "0"
If inNum=49 Then Return "1"
If inNum=50 Then Return "2"
If inNum=51 Then Return "3"
If inNum=52 Then Return "4"
If inNum=53 Then Return "5"
If inNum=54 Then Return "6"
If inNum=55 Then Return "7"
If inNum=56 Then Return "8"
If inNum=57 Then Return "9"
If inNum=65 Then Return "A"
If inNum=66 Then Return "B"
If inNum=67 Then Return "C"
If inNum=68 Then Return "D"
If inNum=69 Then Return "E"
If inNum=70 Then Return "F"
If inNum=71 Then Return "G"
If inNum=72 Then Return "H"
If inNum=73 Then Return "I"
If inNum=74 Then Return "J"
If inNum=75 Then Return "K"
If inNum=76 Then Return "L"
If inNum=77 Then Return "M"
If inNum=78 Then Return "N"
If inNum=79 Then Return "O"
If inNum=80 Then Return "P"
If inNum=81 Then Return "Q"
If inNum=82 Then Return "R"
If inNum=83 Then Return "S"
If inNum=84 Then Return "T"
If inNum=85 Then Return "U"
If inNum=86 Then Return "V"
If inNum=87 Then Return "W"
If inNum=88 Then Return "X"
If inNum=89 Then Return "Y"
If inNum=90 Then Return "Z"
If inNum=96 Then Return "Numpad 0"
If inNum=97 Then Return "Numpad 1"
If inNum=98 Then Return "Numpad 2"
If inNum=99 Then Return "Numpad 3"
If inNum=100 Then Return "Numpad 4"
If inNum=101 Then Return "Numpad 5"
If inNum=102 Then Return "Numpad 6"
If inNum=103 Then Return "Numpad 7"
If inNum=104 Then Return "Numpad 8"
If inNum=105 Then Return "Numpad 9"
If inNum=106 Then Return "Numpad *"
If inNum=107 Then Return "Numpad +"
If inNum=109 Then Return "Numpad -"
If inNum=110 Then Return "Numpad ."
If inNum=111 Then Return "Numpad /"
If inNum=112 Then Return "F1"
If inNum=113 Then Return "F2"
If inNum=114 Then Return "F3"
If inNum=115 Then Return "F4"
If inNum=116 Then Return "F5"
If inNum=117 Then Return "F6"
If inNum=118 Then Return "F7"
If inNum=119 Then Return "F8"
If inNum=120 Then Return "F9"
If inNum=121 Then Return "F10"
If inNum=122 Then Return "F11"
If inNum=123 Then Return "F12"
If inNum=144 Then Return "Num Lock"
If inNum=145 Then Return "Scroll Lock"
If inNum=160 Then Return "Shift (Left)"
If inNum=161 Then Return "Shift (Right)"
If inNum=162 Then Return "Control (Left)"
If inNum=163 Then Return "Control (Right)"
If inNum=164 Then Return "Alt key (Left)"
If inNum=165 Then Return "Alt key (Right)"
If inNum=192 Then Return "Tilde"
If inNum=107 Then Return "Minus"
If inNum=109 Then Return "Equals"
If inNum=219 Then Return "Bracket (Open)"
If inNum=221 Then Return "Bracket (Close)"
If inNum=226 Then Return "Backslash"
If inNum=186 Then Return "Semi-colon"
If inNum=222 Then Return "Quote"
If inNum=188 Then Return "Comma"
If inNum=190 Then Return "Period"		
If inNum=191 Then Return "Slash"

End Function
