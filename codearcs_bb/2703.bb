; ID: 2703
; Author: Ian Caio
; Date: 2010-04-20 21:48:11
; Title: ScanCodes - Mouse - ASCII (Constants)
; Description: Constants for replace the numbers from Scancodes, Mouse buttons and ASCII codes.

;###################### KEYBOARD KEYS ##########################

	;NUMPAD
		Const KEY_NUM_NUMLOCK=69
		
		Const KEY_NUM_MULTIPLY=55
		Const KEY_NUM_SUBTRACT=74
		Const KEY_NUM_ADD=78
		Const KEY_NUM_DIVIDE=181
		
		Const KEY_NUM_0=82
		Const KEY_NUM_1=79
		Const KEY_NUM_2=80
		Const KEY_NUM_3=81
		Const KEY_NUM_4=75
		Const KEY_NUM_5=76
		Const KEY_NUM_6=77
		Const KEY_NUM_7=71
		Const KEY_NUM_8=72
		Const KEY_NUM_9=73
		
		Const KEY_NUM_DECIMAL=83 ;(.)
		Const KEY_NUM_ENTER=156 ;ENTER ON NUMPAD
		
		Const KEY_NUM_EQUALS=141 ;= ON NUMPAD (NEC PC98)
		Const KEY_NUM_COMMA=179 ;COMMA ON NUMPAD (NEX PC98)
	
	;LETTERS AND NUMBERS
		Const KEY_1=2
		Const KEY_2=3
		Const KEY_3=4
		Const KEY_4=5
		Const KEY_5=6
		Const KEY_6=7
		Const KEY_7=8
		Const KEY_8=9
		Const KEY_9=10
		Const KEY_0=11
		Const KEY_Q=16
		Const KEY_W=17
		Const KEY_E=18
		Const KEY_R=19
		Const KEY_T=20
		Const KEY_Y=21
		Const KEY_U=22
		Const KEY_I=23
		Const KEY_O=24
		Const KEY_P=25
		Const KEY_A=30
		Const KEY_S=31
		Const KEY_D=32
		Const KEY_F=33
		Const KEY_G=34
		Const KEY_H=35
		Const KEY_J=36
		Const KEY_K=37
		Const KEY_L=38
		Const KEY_Z=44
		Const KEY_X=45
		Const KEY_C=46
		Const KEY_V=47
		Const KEY_B=48
		Const KEY_N=49
		Const KEY_M=50
	
	;KEYS
		Const KEY_ESCAPE=1
	
		Const KEY_MINUS=12
		Const KEY_EQUALS=13
		Const KEY_BACKSPACE=14
		Const KEY_TAB=15
		Const KEY_LEFT_BRACKET=26
		Const KEY_RIGHT_BRACKET=27
		Const KEY_ENTER=28
		Const KEY_LEFT_CONTROL=29
		Const KEY_SEMI_COLON=39
		Const KEY_APOSTROPHE=40
		Const KEY_GRAVE=41
		Const KEY_LEFT_SHIFT=42
		Const KEY_BACK_SLASH=43
		Const KEY_COMMA=51
		Const KEY_PERIOD=52
		Const KEY_FORWARD_SLASH=53
		Const KEY_RIGHT_SHIFT=54
		Const KEY_LEFT_ALT=56
		Const KEY_SPACE=57
		Const KEY_CAPITAL=58
		Const KEY_SCROLLLOCK=70
		Const KEY_RIGHT_CONTROL=157
		Const KEY_SYS_REQ=183
		Const KEY_RALT=184
		Const KEY_PAUSE=197
		Const KEY_HOME=199
		Const KEY_UP=200
		Const KEY_PAGE_UP=201
		Const KEY_LEFT=203
		Const KEY_RIGHT=205
		Const KEY_END=207
		Const KEY_DOWN=208
		Const KEY_NEXT=209
		Const KEY_INSERT=210
		Const KEY_DELETE=211
		Const KEY_LEFT_WINDOWS=219
		Const KEY_RIGHT_WINDOWS=220
		
		Const KEY_AT=145 ;(NEC PC98)
		Const KEY_COLON=146 ;(NEC PC98)
		Const KEY_UNDERLINE=147 ;(NEC PC98)
		
	;F(?) KEYS
		Const KEY_F1=59
		Const KEY_F2=60
		Const KEY_F3=61
		Const KEY_F4=62
		Const KEY_F5=63
		Const KEY_F6=64
		Const KEY_F7=65
		Const KEY_F8=66
		Const KEY_F9=67
		Const KEY_F10=68
		Const KEY_F11=87
		Const KEY_F12=88
		Const KEY_F13=100 ;(NEC PC98)
		Const KEY_F14=101 ;(NEC PC98)
		Const KEY_F15=102 ;(NEC PC98)
	
	;GERMAN KEYBOARD
		Const KEY_OEM_102=86
	
	;JAPANESE KEYBOARD
		Const KEY_KANA=112
		Const KEY_CONVERT=121
		Const KEY_NO_CONVERT=123
		Const KEY_YEN=125
		Const KEY_AX=150
		Const KEY_KANJI=148
	
	;PORTUGUESE (BRASILIAN) KEYBOARD
		Const KEY_ABNT_C1=115 ;/?
		Const KEY_ABNT_C2=126 ;NUMPAD .
	
	;SPECIAL KEYS
		Const KEY_PREVIOUS_TRACK=144 ;Previous Track (DIK_CIRCUMFLEX on Japanese keyboard)
		Const KEY_STOP=149 ;(NEC PC98)
		Const KEY_UNLABELED=151 ;(J3100)
		Const KEY_NEXT_TRACK=153
		Const KEY_MUTE=160
		Const KEY_CALCULATOR=161
		Const KEY_PLAY_PAUSE=162
		Const KEY_MEDIA_STOP=164
		Const KEY_VOLUME_DOWN=174
		Const KEY_VOLUME_UP=176
		Const KEY_APPS=221
		Const KEY_POWER=222
		Const KEY_SLEEP=223
		Const KEY_WAKE=227
		Const KEY_MY_COMPUTER=235
		Const KEY_MEDIA_SELECT=237
	
	;WEB KEYS
		Const KEY_WEB_HOME=178
		Const KEY_WEB_SEARCH=229
		Const KEY_WEB_FAVORITES=230
		Const KEY_WEB_REFRESH=231
		Const KEY_WEB_STOP=232
		Const KEY_WEB_FORWARD=233
		Const KEY_WEB_BACK=234
		Const KEY_MAIL=236

;################################# MOUSE BUTTONS ########################################
	Const MOUSE_LEFT_BUTTON=1
	Const MOUSE_RIGHT_BUTTON=2
	Const MOUSE_MIDDLE_BUTTON=3
	

;################################# ASCII ##################################################

	;IT USES ABREVIATIONS FOR THE NON-CHARACTER ASCII CODES
		Const ASCII_NUL=0
		Const ASCII_SOH=1
		Const ASCII_STX=2
		Const ASCII_ETX=3
		Const ASCII_EOT=4
		Const ASCII_ENQ=5
		Const ASCII_ACK=6
		Const ASCII_BEL=7
		Const ASCII_BS=8
		Const ASCII_HT=9
		Const ASCII_LF=10
		Const ASCII_VT=11
		Const ASCII_FF=12
		Const ASCII_CR=13
		Const ASCII_SO=14
		Const ASCII_SI=15
		Const ASCII_DLE=16
		Const ASCII_DC1=17 ;XON
		Const ASCII_DC2=18
		Const ASCII_DC3=19
		Const ASCII_DC4=20 ;XOFF
		Const ASCII_NAK=21
		Const ASCII_SYN=22
		Const ASCII_ETB=23
		Const ASCII_CAN=24
		Const ASCII_EM=25
		Const ASCII_SUB=26
		Const ASCII_ESC=27
		Const ASCII_FS=28
		Const ASCII_GS=29
		Const ASCII_RS=30
		Const ASCII_US=31
		Const ASCII_DEL=127
	
	;CHARS
		Const ASCII_SPACE=32
		Const ASCII_EXCLAMATION=33
		Const ASCII_DOUBLE_QUOTE=34
		Const ASCII_NUMBER_SIGN=35
		Const ASCII_DOLLAR_SIGN=36
		Const ASCII_PERCENT=37
		Const ASCII_AMPERSAND=38
		Const ASCII_SINGLE_QUOTE=39
		Const ASCII_LEFT_PARENTHESIS=40
		Const ASCII_RIGHT_PARENTHESIS=41
		Const ASCII_ASTERISK=42
		Const ASCII_PLUS=43
		Const ASCII_COMMA=44
		Const ASCII_MINUS=45
		Const ASCII_DOT=46
		Const ASCII_FORWARD_SLASH=47
		Const ASCII_0=48
		Const ASCII_1=49
		Const ASCII_2=50
		Const ASCII_3=51
		Const ASCII_4=52
		Const ASCII_5=53
		Const ASCII_6=54
		Const ASCII_7=55
		Const ASCII_8=56
		Const ASCII_9=57
		Const ASCII_COLON=58
		Const ASCII_SEMI_COLON=59
		Const ASCII_LESS_THAN=60
		Const ASCII_EQUAL=61
		Const ASCII_GREATER_THAN=62
		Const ASCII_QUESTION_MARK=63
		Const ASCII_AT_SIMBOL=64
		Const ASCII_UP_A=65
		Const ASCII_UP_B=66
		Const ASCII_UP_C=67
		Const ASCII_UP_D=68
		Const ASCII_UP_E=69
		Const ASCII_UP_F=70
		Const ASCII_UP_G=71
		Const ASCII_UP_H=72
		Const ASCII_UP_I=73
		Const ASCII_UP_J=74
		Const ASCII_UP_K=75
		Const ASCII_UP_L=76
		Const ASCII_UP_M=77
		Const ASCII_UP_N=78
		Const ASCII_UP_O=79
		Const ASCII_UP_P=80
		Const ASCII_UP_Q=81
		Const ASCII_UP_R=82
		Const ASCII_UP_S=83
		Const ASCII_UP_T=84
		Const ASCII_UP_U=85
		Const ASCII_UP_V=86
		Const ASCII_UP_W=87
		Const ASCII_UP_X=88
		Const ASCII_UP_Y=89
		Const ASCII_UP_Z=90
		Const ASCII_LEFT_BRACKET=91
		Const ASCII_BACK_SLASH=92
		Const ASCII_RIGHT_BRACKET=93
		Const ASCII_CARRET=94
		Const ASCII_UNDERSCORE=95
		Const ASCII_GRAVE_ACCENT=96
		Const ASCII_LOW_A=97
		Const ASCII_LOW_B=98
		Const ASCII_LOW_C=99
		Const ASCII_LOW_D=100
		Const ASCII_LOW_E=101
		Const ASCII_LOW_F=102
		Const ASCII_LOW_G=103
		Const ASCII_LOW_H=104
		Const ASCII_LOW_I=105
		Const ASCII_LOW_J=106
		Const ASCII_LOW_K=107
		Const ASCII_LOW_L=108
		Const ASCII_LOW_M=109
		Const ASCII_LOW_N=110
		Const ASCII_LOW_O=111
		Const ASCII_LOW_P=112
		Const ASCII_LOW_Q=113
		Const ASCII_LOW_R=114
		Const ASCII_LOW_S=115
		Const ASCII_LOW_T=116
		Const ASCII_LOW_U=117
		Const ASCII_LOW_V=118
		Const ASCII_LOW_W=119
		Const ASCII_LOW_X=120
		Const ASCII_LOW_Y=121
		Const ASCII_LOW_Z=122
		Const ASCII_LEFT_BRACE=123
		Const ASCII_VERTICAL_BAR=124
		Const ASCII_RIGHT_BRACE=125
		Const ASCII_TILDE=126
