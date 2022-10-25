; ID: 1174
; Author: Perturbatio
; Date: 2004-10-17 08:16:34
; Title: Key Constants and String Array
; Description: Contains constants for all scancodes and a string array containing key names

Const KEY_NONE				= 0
Const KEY_ESCAPE			= 1
Const KEY_1					= 2
Const KEY_2					= 3
Const KEY_3					= 4
Const KEY_4					= 5
Const KEY_5					= 6
Const KEY_6					= 7
Const KEY_7					= 8
Const KEY_8					= 9
Const KEY_9					= 10
Const KEY_0					= 11
Const KEY_HYPHEN			= 12
Const KEY_EQUAL				= 13
Const KEY_BACKSPACE			= 14
Const KEY_TAB				= 15
Const KEY_Q					= 16
Const KEY_W					= 17
Const KEY_E					= 18
Const KEY_R					= 19
Const KEY_T					= 20
Const KEY_Y					= 21
Const KEY_U					= 22
Const KEY_I					= 23
Const KEY_O					= 24
Const KEY_P					= 25
Const KEY_BRACKET_LEFT		= 26
Const KEY_BRACKET_RIGHT		= 27
Const KEY_ENTER				= 28
Const KEY_CTRL_LEFT			= 29
Const KEY_A					= 30
Const KEY_S					= 31
Const KEY_D					= 32
Const KEY_F					= 33
Const KEY_G					= 34
Const KEY_H					= 35
Const KEY_J					= 36
Const KEY_K					= 37
Const KEY_L					= 38
Const KEY_SEMICOLON			= 39
Const KEY_APOSTROPHE		= 40
Const KEY_GRAVE				= 41
Const KEY_SHIFT_LEFT		= 42
Const KEY_BACKSLASH			= 43
Const KEY_Z					= 44
Const KEY_X					= 45
Const KEY_C					= 46
Const KEY_V					= 47
Const KEY_B					= 48
Const KEY_N					= 49
Const KEY_M					= 50
Const KEY_COMMA				= 51
Const KEY_PERIOD			= 52
Const KEY_SLASH				= 53
Const KEY_SHIFT_RIGHT		= 54
Const KEY_NUMPAD_MULTIPLY	= 55
Const KEY_ALT_LEFT			= 56
Const KEY_SPACE				= 57
Const KEY_CAPS_LOCK			= 58
Const KEY_F1				= 59
Const KEY_F2				= 60
Const KEY_F3				= 61
Const KEY_F4				= 62
Const KEY_F5				= 63
Const KEY_F6				= 64
Const KEY_F7				= 65
Const KEY_F8				= 66
Const KEY_F9				= 67
Const KEY_F10				= 68
Const KEY_NUM_LOCK			= 69
Const KEY_SCROLL_LOCK		= 70
Const KEY_NUMPAD_7			= 71
Const KEY_NUMPAD_8			= 72
Const KEY_NUMPAD_9			= 73
Const KEY_NUMPAD_HYPHEN		= 74
Const KEY_NUMPAD_4			= 75
Const KEY_NUMPAD_5			= 76
Const KEY_NUMPAD_6			= 77
Const KEY_PLUS				= 78
Const KEY_NUMPAD_1			= 79
Const KEY_NUMPAD_2			= 80
Const KEY_NUMPAD_3			= 81
Const KEY_NUMPAD_0			= 82
Const KEY_NUMPAD_PERIOD		= 83
Const KEY_F11				= 87
Const KEY_F12				= 88
Const KEY_F13				= 100
Const KEY_F14				= 101
Const KEY_F15				= 102
Const KEY_NUMPAD_EQUAL		= 141
Const KEY_NUMPAD_ENTER		= 156
Const KEY_CTRL_RIGHT		= 157
Const KEY_NUMPAD_SLASH		= 181
Const KEY_SYS_RQ			= 183
Const KEY_ALT_RIGHT			= 184
Const KEY_PAUSE				= 197
Const KEY_HOME				= 199
Const KEY_ARROW_UP			= 200
Const KEY_PAGE_UP			= 201
Const KEY_ARROW_LEFT		= 203
Const KEY_ARROW_RIGHT		= 205
Const KEY_END				= 207
Const KEY_ARROW_DOWN		= 208
Const KEY_PAGE_DOWN			= 209
Const KEY_INSERT			= 210
Const KEY_DELETE			= 211
Const KEY_WINDOWS_LEFT		= 219
Const KEY_WINDOWS_RIGHT		= 220

Dim Keynames$(220)
For n = 0 To 220
	Keynames(n)= "Unknown"
Next
	Keynames(1)= "Escape"
	Keynames(2)= "1"
	Keynames(3)= "2"
	Keynames(4)= "3"
	Keynames(5)= "4"
	Keynames(6)= "5"
	Keynames(7)= "6"
	Keynames(8)= "7"
	Keynames(9)= "8"
	Keynames(10)= "9"
	Keynames(11)= "0"
	Keynames(12)= "-"
	Keynames(13)= "="
	Keynames(14)= "Backspace"
	Keynames(15)= "Tab"
	Keynames(16)= "Q"
	Keynames(17)= "W"
	Keynames(18)= "E"
	Keynames(19)= "R"
	Keynames(20)= "T"
	Keynames(21)= "Y"
	Keynames(22)= "U"
	Keynames(23)= "I"
	Keynames(24)= "O"
	Keynames(25)= "P"
	Keynames(26)= "["
	Keynames(27)= "]"
	Keynames(28)= "Return"
	Keynames(29)= "Left Ctrl"
	Keynames(30)= "A"
	Keynames(31)= "S"
	Keynames(32)= "D"
	Keynames(33)= "F"
	Keynames(34)= "G"
	Keynames(35)= "H"
	Keynames(36)= "J"
	Keynames(37)= "K"
	Keynames(38)= "L"
	Keynames(39)= ";"
	Keynames(40)= "'"
	Keynames(41)= "#";UK Keyboard
	Keynames(42)= "Left Shift"
	Keynames(43)= "\"
	Keynames(44)= "Z"
	Keynames(45)= "X"
	Keynames(46)= "C"
	Keynames(47)= "V"
	Keynames(48)= "B"
	Keynames(49)= "N"
	Keynames(50)= "M"
	Keynames(51)= ","
	Keynames(52)= "."
	Keynames(53)= "/"
	Keynames(54)= "Right Shift"
	Keynames(55)= "Numpad *"
	Keynames(56)= "Left Alt"
	Keynames(57)= "Space"
	Keynames(58)= "Caps Lock"
	Keynames(59)= "F1"
	Keynames(60)= "F2"
	Keynames(61)= "F3"
	Keynames(62)= "F4"
	Keynames(63)= "F5"
	Keynames(64)= "F6"
	Keynames(65)= "F7"
	Keynames(66)= "F8"
	Keynames(67)= "F9"
	Keynames(68)= "F10"
	Keynames(69)= "Num Lock"
	Keynames(70)= "Scroll Lock"
	Keynames(71)= "Numpad 7"
	Keynames(72)= "Numpad 8"
	Keynames(73)= "Numpad 9"
	Keynames(74)= "Numpad -"
	Keynames(75)= "Numpad 4"
	Keynames(76)= "Numpad 5"
	Keynames(77)= "Numpad 6"
	Keynames(78)= "Numpad +"
	Keynames(79)= "Numpad 1"
	Keynames(80)= "Numpad 2"
	Keynames(81)= "Numpad 3"
	Keynames(82)= "Numpad 0"
	Keynames(83)= "Numpad ."

	Keynames(87)= "F11"
	Keynames(88)= "F12"

	Keynames(100)= "F13"
	Keynames(101)= "F14"
	Keynames(102)= "F15"
	
	Keynames(141)= "Numpad ="
	
	Keynames(156)= "Numpad Enter"
	Keynames(157)= "Right Ctrl"
	
	Keynames(181)= "Numpad /"
	
	Keynames(183)= "Sys RQ"
	Keynames(184)= "Right Alt"
	
	Keynames(197)= "Pause"
	
	Keynames(199)= "Home"
	Keynames(200)= "Up Arrow"
	Keynames(201)= "Page Up"
	
	Keynames(203)= "Left Arrow"
	
	Keynames(205)= "Right Arrow"
	
	Keynames(207)= "End"
	Keynames(208)= "Down Arrow"
	Keynames(209)= "Page Down"
	Keynames(210)= "Insert"
	Keynames(211)= "Delete"
	
	Keynames(219)= "Left Windows"
	Keynames(220)= "Right Windows"
