; ID: 1319
; Author: sswift
; Date: 2005-03-09 19:55:11
; Title: Raw keyboard functions
; Description: Get the state of the keyboard (and mouse) even when a gadget or other appication has focus!

; -------------------------------------------------------------------------------------------------------------------------------------
; To use these functions, place the following lines in user32.decls in your userlib directory:
;
; 	.lib "user32.dll"
;	GetAsyncKeyState%(vKey%)
;	GetActiveWindow%()
;
; More info:
; http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/WindowsUserInterface/UserInput/VirtualKeyCodes.asp
; http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/userinput/keyboardinput/keyboardinputreference/keyboardinputfunctions/getasynckeystate.asp
; -------------------------------------------------------------------------------------------------------------------------------------

; Common keys:
Const VK_LEFTMOUSE 				= $01	; Left physical mouse button 
Const VK_RIGHTMOUSE 			= $02	; Right physical mouse button
Const VK_MIDDLEMOUSE			= $04	; Middle physical mouse button 
Const VK_BACKSPACE				= $08	; BACKSPACE key
Const VK_TAB 					= $09	; TAB key
Const VK_ENTER 					= $0D	; ENTER key (Main keyboard or keypad)
Const VK_SHIFT 					= $10	; SHIFT key (Left or right)
Const VK_CONTROL 				= $11	; CTRL key (Left or right)
Const VK_ALT 					= $12	; ALT key (Left or right)
Const VK_PAUSE 					= $13	; PAUSE key
Const VK_CAPITAL 				= $14	; CAPS LOCK key
Const VK_ESCAPE 				= $1B	; ESC key
Const VK_SPACE 					= $20	; SPACEBAR
Const VK_PGUP 					= $21	; PAGE UP key
Const VK_PGDN 					= $22	; PAGE DOWN key
Const VK_END 					= $23	; END key
Const VK_HOME 					= $24	; HOME key
Const VK_LEFT 					= $25	; LEFT ARROW key
Const VK_UP 					= $26	; UP ARROW key
Const VK_RIGHT 					= $27	; RIGHT ARROW key
Const VK_DOWN 					= $28	; DOWN ARROW key
Const VK_PRINTSCRN 				= $2C	; PRINT SCREEN key
Const VK_INSERT 				= $2D	; INSERT key
Const VK_DELETE 				= $2E	; DELETE key
Const VK_0 						= $30
Const VK_1 						= $31
Const VK_2 						= $32
Const VK_3 						= $33
Const VK_4 						= $34
Const VK_5 						= $35
Const VK_6 						= $36
Const VK_7 						= $37
Const VK_8 						= $38
Const VK_9 						= $39
Const VK_A 						= $41
Const VK_B 						= $42
Const VK_C 						= $43
Const VK_D 						= $44
Const VK_E 						= $45
Const VK_F 						= $46
Const VK_G 						= $47
Const VK_H 						= $48
Const VK_I 						= $49
Const VK_J 						= $4A
Const VK_K 						= $4B
Const VK_L 						= $4C
Const VK_M 						= $4D
Const VK_N 						= $4E
Const VK_O 						= $4F
Const VK_P 						= $50
Const VK_Q 						= $51
Const VK_R 						= $52
Const VK_S 						= $53
Const VK_T 						= $54
Const VK_U	 					= $55
Const VK_V 						= $56
Const VK_W 						= $57
Const VK_X 						= $58
Const VK_Y		 				= $59
Const VK_Z 						= $5A
Const VK_LEFTWIN				= $5B	; Left Windows key 
Const VK_RIGHTWIN				= $5C	; Right Windows key
Const VK_NUMPAD0 				= $60
Const VK_NUMPAD1 				= $61
Const VK_NUMPAD2 				= $62
Const VK_NUMPAD3 				= $63
Const VK_NUMPAD4 				= $64
Const VK_NUMPAD5 				= $65
Const VK_NUMPAD6 				= $66
Const VK_NUMPAD7 				= $67
Const VK_NUMPAD8 				= $68
Const VK_NUMPAD9 				= $69
Const VK_MULTIPLY 				= $6A	; Keypad *
Const VK_ADD 					= $6B	; Keypad +
Const VK_SEPARATOR 				= $6C	; Keypad /
Const VK_SUBTRACT 				= $6D	; Keypad -
Const VK_DECIMAL 				= $6E	; Keypad .
Const VK_DIVIDE 				= $6F	; Keypad \
Const VK_F1 					= $70
Const VK_F2 					= $71
Const VK_F3 					= $72
Const VK_F4 					= $73
Const VK_F5 					= $74
Const VK_F6 					= $75
Const VK_F7 					= $76
Const VK_F8 					= $77
Const VK_F9 					= $78
Const VK_F10 					= $79
Const VK_F11 					= $7A
Const VK_F12 					= $7B
Const VK_NUMLOCK 				= $90	
Const VK_SCROLLLOCK 			= $91	
Const VK_LEFTSHIFT				= $A0	; Windows 95 does not support the left and right distinguishing constants.	
Const VK_RIGHTSHIFT				= $A1	
Const VK_LEFTCTRL				= $A2	
Const VK_RIGHTCTRL				= $A3	
Const VK_LEFTALT				= $A4	
Const VK_RIGHTALT				= $A5

; Less common keys:
; Some of the keys here would seem to be common keys, but the documentation does not say they work in 95/98,
; so I did not include them with the other keys above just to be safe.
Const VK_CANCEL 				= $03	; Control-break processing
Const VK_X1MOUSE				= $05	; Windows 2000/XP: X1 mouse button
Const VK_X2MOUSE				= $06	; Windows 2000/XP: X2 mouse button
Const VK_CLEAR 					= $0C	; CLEAR key
Const VK_SELECT 				= $29	; SELECT key
Const VK_EXECUTE 				= $2B	; EXECUTE key
Const VK_PRINT 					= $2A	; PRINT key
Const VK_HELP 					= $2F	; HELP key
Const VK_KANA					= $15	; Input Method Editor (IME) Kana mode
Const VK_HANGUL					= $15	; IME Hangul mode
Const VK_JUNJA					= $17	; IME Junja mode
Const VK_FINAL 					= $18	; IME final mode
Const VK_KANJI 					= $19	; IME Hanja/Kanji mode	
Const VK_CONVERT 				= $1C	; IME convert
Const VK_NONCONVERT				= $1D	; IME nonconvert
Const VK_ACCEPT					= $1E	; IME accept
Const CK_MODECHANGE				= $1F	; IME mode change request
Const VK_APPS 					= $5D	; Applications key 
Const VK_SLEEP					= $5F	; Computer Sleep key
Const VK_F13 					= $7C
Const VK_F14 					= $7D
Const VK_F15 					= $7E
Const VK_F16 					= $7F
Const VK_F17 					= $80
Const VK_F18 					= $81
Const VK_F19 					= $82
Const VK_F20 					= $83
Const VK_F21 					= $84
Const VK_F22 					= $85
Const VK_F23 					= $86
Const VK_F24 					= $87
Const VK_BROWSER_BACK			= $A6	; Windows 2000/XP: Browser Back key
Const VK_BROWSER_FORWARD		= $A7	; Windows 2000/XP: Browser Forward key
Const VK_BROWSER_REFRESH 		= $A8	; Windows 2000/XP: Browser Refresh key
Const VK_BROWSER_STOP 			= $A9	; Windows 2000/XP: Browser Stop key
Const VK_BROWSER_SEARCH 		= $AA	; Windows 2000/XP: Browser Search key 
Const VK_BROWSER_FAVORITES		= $AB	; Windows 2000/XP: Browser Favorites key
Const VK_BROWSER_HOME 			= $AC	; Windows 2000/XP: Browser Start And Home key
Const VK_VOLUME_MUTE 			= $AD	; Windows 2000/XP: Volume Mute key
Const VK_VOLUME_DOWN 			= $AE	; Windows 2000/XP: Volume Down key
Const VK_VOLUME_UP 				= $AF	; Windows 2000/XP: Volume Up key
Const VK_MEDIA_NEXT_TRACK		= $B0	; Windows 2000/XP: Next Track key
Const VK_MEDIA_PREV_TRACK 		= $B1	; Windows 2000/XP: Previous Track key
Const VK_MEDIA_STOP 			= $B2	; Windows 2000/XP: Stop Media key
Const VK_MEDIA_PLAY_PAUSE 		= $B3	; Windows 2000/XP: Play/Pause Media key
Const VK_LAUNCH_MAIL 			= $B4	; Windows 2000/XP: Start Mail key
Const VK_LAUNCH_MEDIA_SELECT	= $B5	; Windows 2000/XP: Select Media key
Const VK_LAUNCH_APP1 			= $B6	; Windows 2000/XP: Start Application 1 key
Const VK_LAUNCH_APP2 			= $B7	; Windows 2000/XP: Start Application 2 key	
Const VK_OEM_1 					= $BA	; Windows 2000/XP: For the US standard keyboard, the ';:' key
Const VK_OEM_PLUS 				= $BB	; Windows 2000/XP: For any country/region, the '+' key
Const VK_OEM_COMMA 				= $BC	; Windows 2000/XP: For any country/region, the ',' key
Const VK_OEM_MINUS 				= $BD	; Windows 2000/XP: For any country/region, the '-' key
Const VK_OEM_PERIOD 			= $BE	; Windows 2000/XP: For any country/region, the '.' key
Const VK_OEM_2 					= $BF	; Windows 2000/XP: For the US standard keyboard, the '/?' key
Const VK_OEM_3 					= $C0	; Windows 2000/XP: For the US standard keyboard, the '`~' key
Const VK_OEM_4 					= $DB	; Windows 2000/XP: For the US standard keyboard, the '[{' key
Const VK_OEM_5 					= $DC	; Windows 2000/XP: For the US standard keyboard, the '\|' key
Const VK_OEM_6 					= $DD	; Windows 2000/XP: For the US standard keyboard, the ']}' key
Const VK_OEM_7					= $DE	; Windows 2000/XP: For the US standard keyboard, the 'single-quote/double-quote' key
Const VK_OEM_8					= $DF	; Used for miscellaneous characters; it can vary by keyboard.
Const VK_ICO_F17				= $E0	
Const VK_ICO_F18				= $E1	
Const VK_OEM102					= $E2	; Windows 2000/XP: Either the angle bracket key or the backslash key on the RT 102-key keyboard
Const VK_ICO_HELP				= $E3
Const VK_ICO_00					= $E4	
Const VK_PROCESSKEY				= $E5	; Windows 95/98/Me, Windows NT 4.0, Windows 2000/XP: IME PROCESS key
Const VK_ICO_CLEAR				= $E6
Const VK_PACKET					= $E7	; Windows 2000/XP: Used to pass Unicode characters as if they were keystrokes.
Const VK_OEM_RESET				= $E9
Const VK_OEM_JUMP				= $EA
Const VK_OEM_PA1				= $EB
Const VK_OEM_PA2				= $EC
Const VK_OEM_PA3				= $ED
Const VK_OEM_WSCTRL				= $EE
Const VK_OEM_CUSEL				= $EF
Const VK_OEM_ATTN				= $F0
Const VK_OEM_FINNISH			= $F1
Const VK_OEM_COPY				= $F2
Const VK_OEM_AUTO				= $F3
Const VK_OEM_ENLW				= $F4
Const VK_OEM_BACKTAB			= $F5
Const VK_ATTN					= $F6	; Attn key
Const VK_CRSEL					= $F7	; CrSel key
Const VK_EXSEL					= $F8	; ExSel key
Const VK_EREOF 					= $F9	; Erase EOF key
Const VK_PLAY 					= $FA	; Play key
Const VK_ZOOM 					= $FB	; Zoom key
Const VK_NONAME 				= $FC	; Reserved
Const VK_PA1 					= $FD	; PA1 key
Const VK_OEM_CLEAR				= $FE	; Clear key


; -------------------------------------------------------------------------------------------------------------------------------------
; This function returns 1 if the specified virtual key is being pressed, and 0 if it is not.
;
; Normally, the function checks to see if your application has focus and ignores any keys being held down if it does not,
; but if you want it to tell you if a key is pressed regardless of whether your app has focus, you can set IgnoreFocus to True.
; -------------------------------------------------------------------------------------------------------------------------------------
Function GDI_VKeyDown(VirtualKey, IgnoreFocus=False)
	
	If (IgnoreFocus = True) And (GetActiveWindow() = 0) Then Return
	Return (GetAsyncKeyState%(VirtualKey) And %1000000000000000) > 0
	
End Function
