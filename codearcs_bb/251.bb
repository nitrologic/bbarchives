; ID: 251
; Author: octothorpe
; Date: 2002-02-27 23:15:52
; Title: Windows-style Keyboard Interface
; Description: GetKey() on steroids.  Deals with automatic key repeating just like Windows.  Useful for creating intuitive textbox controls.

Type KEY_Global
	Field scancode_to_ascii.KEY_info[255]
	Field repeat_initial_delay
	Field repeat_subsequent_delay
	Field repeat_timer
	Field depressed_key.KEY_info
End Type
Global KEY_Global.KEY_Global = New KEY_Global
KEY_Global\repeat_initial_delay     = 750
KEY_Global\repeat_subsequent_delay  = 75

Type KEY_info
	Field scancode
	Field ascii_plain     ; lowercase
	Field ascii_shift     ; uppercase
End Type

;--------------------------------------------------------
Function KEY_register_keyinfo(scancode, ascii_plain=0, ascii_shift=0)
;--------------------------------------------------------
	KEY_Global\scancode_to_ascii[scancode] = New KEY_info
	KEY_Global\scancode_to_ascii[scancode]\scancode    = scancode
	KEY_Global\scancode_to_ascii[scancode]\ascii_plain = ascii_plain
	KEY_Global\scancode_to_ascii[scancode]\ascii_shift = ascii_shift
End Function

;--------------------------------------------------------
Function KEY_register_printable_keys()
;--------------------------------------------------------
	KEY_register_keyinfo( 41,  96, 126)    ; ` and ~
	KEY_register_keyinfo(  2,  49,  33)    ; 1 and !
	KEY_register_keyinfo(  3,  50,  64)    ; 2 and @
	KEY_register_keyinfo(  4,  51,  35)    ; 3 and #
	KEY_register_keyinfo(  5,  52,  36)    ; 4 and $
	KEY_register_keyinfo(  6,  53,  37)    ; 5 and %
	KEY_register_keyinfo(  7,  54,  94)    ; 6 and ^
	KEY_register_keyinfo(  8,  55,  38)    ; 7 and &
	KEY_register_keyinfo(  9,  56,  42)    ; 8 and *
	KEY_register_keyinfo( 10,  57,  40)    ; 9 and (
	KEY_register_keyinfo( 11,  48,  41)    ; 0 and )
	KEY_register_keyinfo( 12,  45,  95)    ; - and _
	KEY_register_keyinfo( 13,  61,  43)    ; = and +
	KEY_register_keyinfo( 16, 113,  81)    ; q and Q
	KEY_register_keyinfo( 17, 119,  87)    ; w and W
	KEY_register_keyinfo( 18, 101,  69)    ; e and E
	KEY_register_keyinfo( 19, 114,  82)    ; r and R
	KEY_register_keyinfo( 20, 116,  84)    ; t and T
	KEY_register_keyinfo( 21, 121,  89)    ; y and Y
	KEY_register_keyinfo( 22, 117,  85)    ; u and U
	KEY_register_keyinfo( 23, 105,  73)    ; i and I
	KEY_register_keyinfo( 24, 111,  79)    ; o and O
	KEY_register_keyinfo( 25, 112,  80)    ; p and P
	KEY_register_keyinfo( 26,  91, 123)    ; [ and {
	KEY_register_keyinfo( 27,  93, 125)    ; ] and }
	KEY_register_keyinfo( 43,  92, 124)    ; \ and |
	KEY_register_keyinfo( 30,  97,  65)    ; a and A
	KEY_register_keyinfo( 31, 115,  83)    ; s and S
	KEY_register_keyinfo( 32, 100,  68)    ; d and D
	KEY_register_keyinfo( 33, 102,  70)    ; f and F
	KEY_register_keyinfo( 34, 103,  71)    ; g and G
	KEY_register_keyinfo( 35, 104,  72)    ; h and H
	KEY_register_keyinfo( 36, 106,  74)    ; j and J
	KEY_register_keyinfo( 37, 107,  75)    ; k and K
	KEY_register_keyinfo( 38, 108,  76)    ; l and L
	KEY_register_keyinfo( 39,  59,  58)    ; ; and :
	KEY_register_keyinfo( 40,  39,  34)    ; ' and "
	KEY_register_keyinfo( 44, 122,  90)    ; z and Z
	KEY_register_keyinfo( 45, 120,  88)    ; x and X
	KEY_register_keyinfo( 46,  99,  67)    ; c and C
	KEY_register_keyinfo( 47, 118,  86)    ; v and V
	KEY_register_keyinfo( 48,  98,  66)    ; b and B
	KEY_register_keyinfo( 49, 110,  78)    ; n and N
	KEY_register_keyinfo( 50, 109,  77)    ; m and M
	KEY_register_keyinfo( 51,  44,  60)    ; , and <
	KEY_register_keyinfo( 52,  46,  62)    ; . and >
	KEY_register_keyinfo( 53,  47,  63)    ; / and ?
	KEY_register_keyinfo( 15,   9,   9)    ; tab
	KEY_register_keyinfo( 57,  32,  32)    ; space
	KEY_register_keyinfo( 28,  13,  13)    ; enter
	KEY_register_keyinfo(181,  47,  47)    ; numpad /
	KEY_register_keyinfo( 55,  42,  42)    ; numpad *
	KEY_register_keyinfo( 74,  45,  45)    ; numpad -
	KEY_register_keyinfo( 78,  43,  43)    ; numpad +
	KEY_register_keyinfo(156,  13,  13)    ; numpad enter
	KEY_register_keyinfo( 79,  49,  49)    ; numpad 1
	KEY_register_keyinfo( 80,  50,  50)    ; numpad 2
	KEY_register_keyinfo( 81,  51,  51)    ; numpad 3
	KEY_register_keyinfo( 75,  52,  52)    ; numpad 4
	KEY_register_keyinfo( 76,  53,  53)    ; numpad 5
	KEY_register_keyinfo( 77,  54,  54)    ; numpad 6
	KEY_register_keyinfo( 71,  55,  55)    ; numpad 7
	KEY_register_keyinfo( 72,  56,  56)    ; numpad 8
	KEY_register_keyinfo( 73,  57,  57)    ; numpad 9
	KEY_register_keyinfo( 82,  48,  48)    ; numpad 0
	KEY_register_keyinfo( 83,  46,  46)    ; numpad .
End Function

;--------------------------------------------------------
Function KEY_register_manipulation_keys()
;--------------------------------------------------------
	KEY_register_keyinfo(199,  1,  1)     ; home
	KEY_register_keyinfo(207,  2,  2)     ; end
	KEY_register_keyinfo( 14,  8,  8)     ; backspace
	KEY_register_keyinfo(211,  4,  4)     ; delete
	KEY_register_keyinfo(203, 31, 31)     ; left
	KEY_register_keyinfo(205, 30, 30)     ; right
End Function

;--------------------------------------------------------
Function KEY_GetKey()
;--------------------------------------------------------

	Local trigger_keystroke = False

	; is the user still pressing the previous key?
	If KEY_Global\depressed_key <> Null Then
		If KeyDown(KEY_Global\depressed_key\scancode) Then
			; user is still pressing the key, so generate repeating keystrokes
			If KEY_Global\repeat_timer < MilliSecs() Then
				KEY_Global\repeat_timer = MilliSecs() + KEY_Global\repeat_subsequent_delay
				trigger_keystroke = True
			EndIf
		Else
			; user has stopped pressing the key
			KEY_Global\depressed_key = Null
		EndIf
	EndIf

	; has the user pressed a new key? (check the key positions for each recognized scancode)
	For key.KEY_info = Each KEY_info
		If KeyHit(key\scancode) Then Exit
	Next
	; if we found a recognized key, remember it's being pressed, and return it
	If key <> Null Then
		KEY_Global\depressed_key = key
		KEY_Global\repeat_timer = MilliSecs() + KEY_Global\repeat_initial_delay
		trigger_keystroke = True
	EndIf

	If trigger_keystroke Then
		; the ascii code we want to return often depends on if shift is being pressed
		If KeyDown(42) Or KeyDown(54) Then
			Return KEY_Global\depressed_key\ascii_shift
		Else
			Return KEY_Global\depressed_key\ascii_plain
		EndIf
	EndIf

	Return 0
End Function


;--------------------------------------------------------
; EXAMPLE
;--------------------------------------------------------
;
;SetBuffer = BackBuffer()
;
;KEY_register_printable_keys()
;
;input_string$ = ""
;While Not KeyHit(1)
;	char = KEY_GetKey()
;	If char > 0 Then input_string$ = input_string$ + Chr$(char)
;
;	Cls
;	Locate 1, 1
;	Print "Press and hold keys to see repeating effect"
;	Print input_string$ + cursor$
;	Flip
;Wend
