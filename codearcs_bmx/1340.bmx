; ID: 1340
; Author: John Galt
; Date: 2005-03-29 14:32:39
; Title: Key name from key code
; Description: What it says on the tin

'Key names
'Falken '05

Graphics 800,600,32

Local keystring$[300]
RestoreData key_data

Repeat
	ReadData tempkey$
	ReadData put_index
	keystring(put_index)=tempkey
Until put_index=191

While Not KeyDown(KEY_ESCAPE)
	Cls
	DrawText keystring(WaitKey()),100,100
	Flip
Wend

#key_data
DefData "Mouse button (Left)",1
DefData "Mouse button (Right)",2
DefData "Mouse button (Middle)" ,4
DefData "Backspace",8
DefData "Tab",9
DefData "Return",13
DefData "Clear",12
DefData "Enter",13
DefData "Shift",16
DefData "Control",17
DefData "Alt",18
DefData "Pause",19
DefData "Caps Lock",20
DefData "Escape",27
DefData "Space",32
DefData "Page Up",33
DefData "Page Down",34
DefData "End",35
DefData "Home",36
DefData "Cursor (Left)",37
DefData "Cursor (Up)",38
DefData "Cursor (Right)",39
DefData "Cursor (Down)",40
DefData "Select",41
DefData "Print",42
DefData "Execute",43
DefData "Screen",44
DefData "Insert",45
DefData "Delete",46
DefData "Help",47
DefData "0",48
DefData "1",49
DefData "2",50
DefData "3",51
DefData "4",52
DefData "5",53
DefData "6",54
DefData "7",55
DefData "8",56
DefData "9",57
DefData "A",65
DefData "B",66
DefData "C",67
DefData "D",68
DefData "E", 69
DefData "F",70
DefData "G",71
DefData "H",72
DefData "I",73
DefData "J",74
DefData "K",75
DefData "L",76
DefData "M",77
DefData "N",78
DefData "O",79
DefData "P",80
DefData "Q",81
DefData "R",82
DefData "S",83
DefData "T",84
DefData "U",85
DefData "V",86
DefData "W",87
DefData "X",88
DefData "Y", 89
DefData "Z", 90
DefData "Sys key (Left)",91
DefData "Sys key (Right)",92
DefData "Numpad 0",96
DefData "Numpad 1",97
DefData "Numpad 2",98
DefData "Numpad 3",99
DefData "Numpad 4",100
DefData "Numpad 5",101
DefData "Numpad 6",102
DefData "Numpad 7",103
DefData "Numpad 8",104
DefData "Numpad 9",105
DefData "Numpad *",106
DefData "Numpad +",107
DefData "Numpad /",108
DefData "Numpad -",109
DefData "Numpad .",110
DefData "Numpad /",111
DefData "F1",112
DefData "F2",113
DefData "F3",114
DefData "F4",115
DefData "F5",116
DefData "F6",117
DefData "F7",118
DefData "F8",119
DefData "F9",120
DefData "F10",121
DefData "F11", 122
DefData "F12",123
DefData "Num Lock",144
DefData "Scroll Lock",145
DefData "Shift (Left)",160
DefData "Shift (Right)",161
DefData "Control (Left)",162
DefData "Control (Right)",163
DefData "Alt key (Left)",164
DefData "Alt key (Right)",165
DefData "Tilde",192
DefData "Minus",107
DefData "Equals",109
DefData "Bracket (Open)",219
DefData "Bracket (Close)",221
DefData "Backslash",226
DefData "Semi-colon",186
DefData "Quote",222
DefData "Comma",188
DefData "Period",190
DefData "Slash",191
