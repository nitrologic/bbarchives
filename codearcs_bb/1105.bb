; ID: 1105
; Author: Dabbede
; Date: 2004-07-11 15:33:55
; Title: Human Input Library
; Description: A more simple library to manage player's inputs

;;===============================;;
;;===============================;;
;;				 ;;
;; Human Input Library		 ;;
;;				 ;;
;;	by Dabbede		 ;;
;;				 ;;
;;			         ;;
;;	10/07/2004	v. 1.0	 ;;
;;				 ;;
;;===============================;;
;;===============================;;
;;		 		 ;;
;; The easiest way to handle 	 ;;
;; player's inputs like		 ;;
;; keyboard, joypad or mouse	 ;;
;;	 			 ;;
;;===============================;;
;;===============================;;


Type HIL_Key				; Any input type
	Field Typ				; 0=none	1=keyb	2=joy	3=mouse
	Field Value				; Keyboard:
							;	Value = scancode
							; Joystick:
							;	Value(>0)  = button#
							;	Value(-1)  = x-axis +
							;	Value(-2)  = x-axis -
							;	Value(-3)  = y-axis +
							;	Value(-4)  = y-axis -
							;	Value(-5)  = z-axis +
							;	Value(-6)  = z-axis -
							;	Value(-7)  = u-axis +
							;	Value(-8)  = u-axis -
							;	Value(-9)  = v-axis +
							;	Value(-10) = v-axis -
							;	Value(-11) = pitch  +
							;	Value(-12) = pitch  -
							;	Value(-13) = yaw    +
							;	Value(-14) = yaw    -
							;	Value(-15) = roll   +
							;	Value(-16) = roll   -
							; Mouse:
							;	Value=button#
End Type

Dim HIL_scancodes$(238)		; Array with keyboard scancodes
Global HIL_SpecialJoystick = False	;True if you want to use JoyZ\U\V\Pitch\Yaw\Roll
Const HIL_MaxJoyButtons = 30


; Initialization ==================
Restore HIL_Scancodes
For i=1 To 237
	Read  HIL_scancodes$(i)
Next









; Little example ==================
Print "Choose the key to exit"
quit.HIL_Key=HIL_NewKey()
HIL_FlushAnyKeys()
Cls
Locate 0,0
Print "Press some keys..."
temp.HIL_Key=HIL_NewKey()
While (temp\typ<>quit\typ) Or (temp\value<>quit\value)
	Print HIL_KeyLabel(temp)+" is the wrong key... retry!"
	HIL_ModifyKey(temp)
Wend
Print "Ok!"
Cls
HIL_TestJoy()
End











Function HIL_AnyKeyHit()
	quit=False
	
	;Keyboard
	For i=1 To 237
		If KeyHit(i) Then quit=1
	Next
	
	;Joystick
	For i=1 To HIL_MaxJoyButtons
		If JoyHit(i) Then quit=2
	Next	
	If (JoyXDir() Or JoyYDir()) Then quit=2
	If HIL_SpecialJoystick And (JoyZDir() Or JoyUDir() Or JoyVDir() Or Int(JoyPitch()/180) Or Int(JoyYaw()/180) Or Int(JoyRoll()/180)) Then quit=2
	
	;Mouse
	For i=1 To 3	
		If MouseHit(i) Then quit=3
	Next
	
	Return quit
End Function

Function HIL_AnyKeyDown()	; Not very useful
	quit=False
	
	;Keyboard
	For i=1 To 237
		If KeyDown(i) Then quit=1
	Next
	
	;Joystick
	For i=1 To HIL_MaxJoyButtons
		If JoyDown(i) Then quit=2
	Next	
	If (JoyXDir() Or JoyYDir()) Then quit=2
	If HIL_SpecialJoystick And (JoyZDir() Or JoyUDir() Or JoyVDir() Or Int(JoyPitch()/180) Or Int(JoyYaw()/180) Or Int(JoyRoll()/180)) Then quit=2
	
	;Mouse
	For i=1 To 3	
		If MouseDown(i) Then quit=3
	Next
	
	Return quit
End Function

Function HIL_GetValue(typ)
	Select typ
		Case 1 
			For i=1 To 237
				If KeyDown(i) Then Return i
			Next
		Case 2
			For i=1 To HIL_MaxJoyButtons
				If JoyDown(i) Then Return i
			Next
			If JoyXDir()=+1 Return -1
			If JoyXDir()=-1 Return -2
			If JoyYDir()=+1 Return -3
			If JoyYDir()=-1 Return -4
			If HIL_SpecialJoystick Then
				If JoyZDir()=+1 Return -5
				If JoyZDir()=-1 Return -6
				If JoyUDir()=+1 Return -7
				If JoyUDir()=-1 Return -8
				If JoyVDir()=+1 Return -9
				If JoyVDir()=-1 Return -10
				If Int(JoyPitch()/180)=+1 Return -11
				If Int(JoyPitch()/180)=-1 Return -12
				If Int(JoyYaw()  /180)=+1 Return -13
				If Int(JoyYaw()  /180)=-1 Return -14
				If Int(JoyRoll() /180)=+1 Return -15
				If Int(JoyRoll() /180)=-1 Return -16
			EndIf
		Case 3
			For i=1 To 3	
				If MouseDown(i) Then Return i
			Next
		Default
			Return 0
	End Select
End Function

Function HIL_NewKey.HIL_Key()
	key.HIL_Key = New HIL_Key
	
	Repeat
		t = HIL_AnyKeyHit()
	Until t
	
	key\Typ = t
	key\value = HIL_GetValue(t)
	
	Return key
End Function

Function HIL_ModifyKey.HIL_Key(key.HIL_Key)
	If key=Null Then 
		key = New HIL_Key
	EndIf
	
	Repeat
		t = HIL_AnyKeyHit()
	Until t
	
	key\Typ = t
	key\value = HIL_GetValue(t)
End Function

Function HIL_KeyDown(key.HIL_Key)
	If key=Null Return False
	
	Select key\Typ
		Case 1
			If KeyDown(key\Value) Return True
		
		Case 2
			Select key\Value
				Case 0
					Return False
				Case -1
					If JoyXDir()=+1 Return True
				Case -2
					If JoyXDir()=-1 Return True
				Case -3
					If JoyYDir()=+1 Return True
				Case -4
					If JoyYDir()=-1 Return True
				Case -5
					If JoyZDir()=+1 Return True
				Case -6
					If JoyZDir()=-1 Return True
				Case -7
					If JoyUDir()=+1 Return True
				Case -8
					If JoyUDir()=-1 Return True
				Case -9
					If JoyVDir()=+1 Return True
				Case -10
					If JoyVDir()=-1 Return True
				Case -11
					If Int(JoyPitch()/180)=+1 Return True
				Case -12
					If Int(JoyPitch()/180)=-1 Return True
				Case -13
					If Int(JoyYaw()  /180)=+1 Return True
				Case -14
					If Int(JoyYaw()  /180)=-1 Return True
				Case -15
					If Int(JoyRoll() /180)=+1 Return True
				Case -16
					If Int(JoyRoll() /180)=-1 Return True
				Default
					If JoyDown(key\Value) Return True
			End Select
		
		Case 3
			If MouseDown(key\Value) Return True
	End Select
	
	Return False
End Function

Function HIL_KeyHit(key.HIL_Key)
	If key=Null Return False
	
	Select key\Typ
		Case 1
			If KeyHit(key\Value) Return True
		
		Case 2
			Select key\Value
				Case 0
					Return False
				Case -1
					If JoyXDir()=+1 Return True
				Case -2
					If JoyXDir()=-1 Return True
				Case -3
					If JoyYDir()=+1 Return True
				Case -4
					If JoyYDir()=-1 Return True
				Case -5
					If JoyZDir()=+1 Return True
				Case -6
					If JoyZDir()=-1 Return True
				Case -7
					If JoyUDir()=+1 Return True
				Case -8
					If JoyUDir()=-1 Return True
				Case -9
					If JoyVDir()=+1 Return True
				Case -10
					If JoyVDir()=-1 Return True
				Case -11
					If Int(JoyPitch()/180)=+1 Return True
				Case -12
					If Int(JoyPitch()/180)=-1 Return True
				Case -13
					If Int(JoyYaw()  /180)=+1 Return True
				Case -14
					If Int(JoyYaw()  /180)=-1 Return True
				Case -15
					If Int(JoyRoll() /180)=+1 Return True
				Case -16
					If Int(JoyRoll() /180)=-1 Return True
				Default
					If JoyHit(key\Value) Return True
			End Select
		
		Case 3
			If MouseHit(key\Value) Return True
	End Select
	
	Return False
End Function

Function HIL_WaitAnyKey()
	While Not HIL_AnyKeyHit()
	Wend
End Function

Function HIL_FlushAnyKeys()
	FlushKeys
	FlushMouse
	FlushJoy
End Function

Function HIL_KeyLabel$(key.HIL_Key)
	If key=Null	Return ""
	
	Select key\typ
		Case 1
			Return HIL_scancodes$(key\value)
		Case 2
			Select key\value
				Case 0
					Return "Joystick button"
				Case -1
					Return "Joystick x-axis (+)"
				Case -2
					Return "Joystick x-axis (-)"
				Case -3
					Return "Joystick y-axis (+)"
				Case -4
					Return "Joystick y-axis (-)"
				Case -5
					Return "Joystick z-axis (+)"
				Case -6
					Return "Joystick z-axis (-)"
				Case -7
					Return "Joystick u-axis (+)"
				Case -8
					Return "Joystick u-axis (-)"
				Case -9
					Return "Joystick v-axis (+)"
				Case -10
					Return "Joystick v-axis (-)"
				Case -11
					Return "Joystick pitch (+)"
				Case -12
					Return "Joystick pitch (-)"
				Case -13
					Return "Joystick yaw (+)"
				Case -14
					Return "Joystick yaw (-)"
				Case -15
					Return "Joystick roll (+)"
				Case -16
					Return "Joystick roll (-)"
				Default
					Return "Joystick button "+key\value
			End Select
		Case 3
			Select key\value
				Case 1
					Return "Left Mouse button"
				Case 2
					Return "Right Mouse button"
				Case 3
					Return "Middle Mouse button"
				Default
					Return "Mouse button"
			End Select
		Default
			Return ""
	End Select
End Function

Function HIL_TestJoy()
	While Not KeyDown(1)
		; Output joystick values
		Text 0,0,"Move joystick to output values onto screen"
		Text 0,20, "JoyXDir(): "+JoyXDir()
		Text 0,40, "JoyYDir(): "+JoyYDir()
		Text 0,60, "JoyZDir(): "+JoyZDir()
		Text 0,80, "JoyUDir(): "+JoyUDir()
		Text 0,100,"JoyVDir(): "+JoyVDir()
		Text 0,120,"JoyPitchDir(): "+Int(JoyPitch()/180)
		Text 0,140,"JoyYawDir(): "+Int(JoyYaw()/180)
		Text 0,160,"JoyRollDir(): "+Int(JoyRoll()/180)
	
		Flip
		Cls
	Wend
End Function













.HIL_Scancodes
Data "ESCAPE"
Data "1"  
Data "2"
Data "3"
Data "4"
Data "5"
Data "6"
Data "7"
Data "8"
Data "9"
Data "0"
Data "Minus (-)"			;On Main Keyboard 
Data "Equals (=)"
Data "Backspace"			;Backspace key
Data "Tab"
Data "Q"
Data "W"
Data "E"
Data "R"
Data "T"
Data "Y"
Data "U"
Data "I"
Data "O"
Data "P"
Data "Left Bracket ([)"
Data "Right Bracket (])"
Data "Return/Enter"			;Return/Enter on Main Keyboard
Data "Left Control"
Data "A"
Data "S"
Data "D"
Data "F"
Data "G"
Data "H"
Data "J"
Data "K"
Data "L"
Data "Semi-Colon (;)"
Data "Apostrophe (')"
Data "Grave"				;Accent Grave
Data "Left Shift"
Data "Backslash (\)"
Data "Z"
Data "X"
Data "C"
Data "V"
Data "B"
Data "N"
Data "M"
Data "Comma (,)"
Data "Period (.)"			;On Main keyboard
Data "Slash (/)"			;On Main Keyboard
Data "Right Shift"
Data "Multiply (*)"			;On Numeric Keypad
Data "Left Alt/Menu"
Data "Space"
Data "Capital"
Data "F1"
Data "F2"
Data "F3"
Data "F4"
Data "F5"
Data "F6"
Data "F7"
Data "F8"
Data "F9"
Data "F10"
Data "NumLock"
Data "Scroll Lock"
Data "NumPad 7"
Data "NumPad 8"
Data "NumPad 9"
Data "Subtract (-)"			;On Numeric Keypad
Data "NumPad 4"
Data "NumPad 5"
Data "NumPad 6"
Data "Add (+)"				;On Numeric Keypad
Data "NumPad 1"
Data "NumPad 2"
Data "NumPad 3"
Data "NumPad 0"
Data "Decimal (.)"			;On Numeric Keypad
Data "Unknown"
Data "Unknown"
Data "OEM_102"				;On UK/Germany Keyboards 
Data "F11"
Data "F12"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "F13"					;(NEC PC98)
Data "F14"					;(NEC PC98)
Data "F15"					;(NEC PC98)
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Kana"					;Japanese Keyboard
Data "Unknown"
Data "Unknown"
Data "ABNT_C1"				;/? on Portugese (Brazilian) keyboards
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Convert"				;Japanese Keyboard
Data "Unknown"
Data "NoConvert"			;Japanese Keyboard
Data "Unknown"
Data "Yen"					;Japanese Keyboard
Data "ABNT_C2"				;Numpad . on Portugese (Brazilian) keyboards
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Equals"				;= on numeric keypad (NEC PC98) 
Data "Unknown"
Data "Unknown"
Data "PrevTrack"			;Previous Track (DIK_CIRCUMFLEX on Japanese keyboard)
Data "AT"					;(NEC PC98)
Data "Colon (:)"			;(NEC PC98)
Data "Underline"			;(NEC PC98)
Data "Kanji"				;Japanese Keyboard
Data "Stop"					;(NEC PC98)
Data "AX"					;Japan AX
Data "Unlabeled"			;(J3100)
Data "Unknown"
Data "Next Track"			;Next Track
Data "Unknown"
Data "Unknown"
Data "Enter"				;ENTER on Numeric Keypad
Data "Right Control"
Data "Unknown"
Data "Unknown"
Data "Mute"					;Mute
Data "Calculator"			;Calculator
Data "Play/Pause"			;Play/Pause
Data "Unknown"
Data "Media Stop"			;Media Stop
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Volume Down"			;Volume -
Data "Unknown"
Data "Volume Up"			;Volume +
Data "Unknown"
Data "Web Home"				;Web Home
Data "Comma (,)"			;On Numeric Keypad (NEX PC98)
Data "Unknown"
Data "Divide (/)"			;On Numeric Keypad
Data "Unknown"
Data "SysReq"
Data "Right Alt/Menu"		;Right Alt
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Pause"				;Pause
Data "Unknown"
Data "Home"					;Home on Arrow Pad
Data "Up"					;Up Arrow on Arrow Keypad
Data "Page Up/Prior"		;Page Up on Arrow Keypad
Data "Unknown"
Data "Left"					;Left Arrow on Arrow Keypad
Data "Unknown"
Data "Right"				;Right Arrow on Arrow Keypad
Data "Unknown"
Data "End"					;End Key on Arrow Keypad
Data "Down"					;Down Key on Arrow Keypad
Data "Next"					;Next Key on Arrow Keypad
Data "Insert"				;Insert Key on Arrow Keypad
Data "Delete"				;Delete Key on Arrow Keypad
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Left Windows"			;Left Windows Key
Data "Right Windows"		;Right Windows Key
Data "Apps"					;Apps Menu Key
Data "Power"				;System Power
Data "Sleep"				;System Sleep
Data "Unknown"
Data "Unknown"
Data "Unknown"
Data "Wake"					;System Wake
Data "Unknown"
Data "Web Search"
Data "Web Favorites"
Data "Web Refresh"
Data "Web Stop"
Data "Web Forward"
Data "Web Back"
Data "My Computer"
Data "Mail"
Data "Media Select"
