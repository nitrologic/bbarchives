; ID: 1227
; Author: Eikon
; Date: 2004-12-09 22:44:44
; Title: Virtual Keys
; Description: Function for simulating keystrokes B+/B3D

; Userlib
;.lib "user32.dll"
;keypress%(bVk%, bScan%, dwFlags%, dwExtraInfo%):"keybd_event"
;SetWindowPos%(hWnd%,hAfter%,x%,y%,cx%,cy%,flags%)
;GetActiveWindow%()

; Virtual Keys 1.1 by Eikon
; eikon@carolina.rr.com
; Last revised 12/09/04
Local DeskW = ClientWidth(Desktop()), DeskH = ClientHeight(Desktop())
Win = CreateWindow("Virtual Keys", DeskW / 2 - 150, DeskH / 2 - 60, 300, 120, 0, 1)
hwnd% = GetActiveWindow()
Font = LoadFont("System", 10, 0, 0, 0)
Button = CreateButton("GO!", 6, 55, 282, 32, Win, 1): SetGadgetFont Button, Font
msg$ = "Click on the button to fire up Notepad."
msg$ = msg$ + " Do not close Notepad until it's finished typing."
Label = CreateLabel(msg$, 7, 10, 280, 40, Win, 0)
SetGadgetFont Label, Font

Repeat

Select WaitEvent()
	
	Case $803: End
	Case $401
	If EventSource() = Button Then ; Begin
		SetWindowPos hwnd, -2, 0, 0, 0, 0, 1 + 2     ; Push window under notepad
		ExecFile Chr$(34) + "notepad.exe" + Chr$(34) ; Shell notepad
		Delay 500                                    ; Give it time to come up
		File = ReadFile("file.txt")                  ; Begin reading from file
		Repeat
			tmp$ = ReadLine(File)
				For i = 1 To Len(tmp$)
					vkey Mid(tmp$, i, 1), 1          ; Press the key
					Delay 5                          ; Small delay to slow typing speed
				Next
			vkey "ret", 1							 ; Return
		Until Eof(File)
		CloseFile File                               ; Close file
		
		; Begin some trickery
		vkey "alt", 0 ; Hold ALT
		vkey "f", 1   ; Press F
		vkey "alt", 2 ; Release ALT
		For i = 1 To 4 ; Move right
			vkey "right", 1: Delay 150
		Next
		For i = 1 To 4 ; Move left
			vkey "left", 1: Delay 150
		Next
		For i = 1 To 6 ; Move down to exit
			vkey "down", 1: Delay 150
		Next
		vkey "ret", 1   ; Press return
		vkey "right", 1: Delay 500 ; Go to "no"
		vkey "ret", 1   ; Exit notepad without saving
		
	EndIf
		
End Select	
Forever

; //===================================================
; // Virtual Keys VKEY Function
; // 
; // Char$  - String containing the character to be pressed
; // State% - 0 KeyDown
; //        - 1 Keypress
; //        - 2 KeyUp
; //
; // Special Char Codes
; //
; // RET   - Return
; // ALT   - ALT Key
; // UP    - Cursor key up
; // DOWN  - Cursor key down
; // LEFT  - Cursor key left
; // RIGHT - Cursor key right
; // F1 through F12 - Function Keys
; // NUM   - Num lock
; // SCR   - Scroll lock
; // LCTRL - Left control
; // RCTRL - Right control
; // LSH   - Left shift
; // RSH   - Right shift
; // LWIN  - Left Windows key
; // RWIN  - Right Windows key
; // DEL   - Delete
; // INS   - Insert
; // PRN   - Print screen
; // HOME  - Home
; // END   - End
; // ESC   - Escape
; // LMB   - Left mouse button
; // RMB   - Right mouse button
; // MMB   - Middle mouse button
; // BACK  - Backspace
; // TAB   - Tab
; // NEXT  - Page down
; // PRIOR - Page up
; // Chr$(34) - Double Quotes
; //===============================================================
Function vkey(char$, state%)
tmp = 0: shift = False

; Shift for uppercase
If Lower$(char$) <> char$ Or char$ = Chr$(34) Then
	keypress 161, 0, 0, 0 ; Hold shift
	shift = True
EndIf

char$ = Lower$(char$)
Select char$
	Case "0": tmp = 48
	Case "1": tmp = 49
	Case "2": tmp = 50
	Case "3": tmp = 51
	Case "4": tmp = 52
	Case "5": tmp = 53
	Case "6": tmp = 54
	Case "7": tmp = 55
	Case "8": tmp = 56
	Case "9": tmp = 57
	Case "a": tmp = 65
	Case "b": tmp = 66
	Case "c": tmp = 67
	Case "d": tmp = 68
	Case "e": tmp = 69
	Case "f": tmp = 70
	Case "g": tmp = 71
	Case "h": tmp = 72
	Case "i": tmp = 73
	Case "j": tmp = 74 
	Case "k": tmp = 75 
	Case "l": tmp = 76 
	Case "m": tmp = 77 
	Case "n": tmp = 78 
	Case "o": tmp = 79 
	Case "p": tmp = 80 
	Case "q": tmp = 81
	Case "r": tmp = 82
	Case "s": tmp = 83
	Case "t": tmp = 84
	Case "u": tmp = 85
	Case "v": tmp = 86
	Case "w": tmp = 87
	Case "x": tmp = 88
	Case "y": tmp = 89
	Case "z": tmp = 90 
	Case " ": tmp = 32
	Case ".": tmp = 190
	Case ",": tmp = 188
	Case "'": tmp = 222
	Case Chr$(34): tmp = 222 
	Case ";": tmp = 186
	Case "/": tmp = 191
	Case "\": tmp = 220
	Case "-": tmp = 189
	Case "ret": tmp = 13
	Case "alt": tmp = 18
	Case "down": tmp = 40
	Case "right": tmp = 39
	Case "up": tmp = 38
	Case "left": tmp = 37
	Case "f1": tmp = 112
	Case "f2": tmp = 113
	Case "f3": tmp = 114
	Case "f4": tmp = 115
	Case "f5": tmp = 116
	Case "f6": tmp = 117
	Case "f7": tmp = 118
	Case "f8": tmp = 119
	Case "f9": tmp = 120
	Case "f10": tmp = 121
	Case "f11": tmp = 122
	Case "f12": tmp = 123
	Case "num": tmp = 144
	Case "scr": tmp = 145
	Case "lctrl": tmp = 162
	Case "rctrl": tmp = 163
	Case "lwin": tmp = 91
	Case "rwin": tmp = 92
	Case "del": tmp = 46
	Case "ins": tmp = 45
	Case "prn": tmp = 44
	Case "home": tmp = 36
	Case "end": tmp = 35
	Case "esc": tmp = 27
	Case "lmb": tmp = 1
	Case "rmb": tmp = 2
	Case "mmb": tmp = 4
	Case "back": tmp = 8
	Case "tab": tmp = 9
	Case "next": tmp = 34
	Case "prior": tmp = 33
	
End Select

If tmp <> 0 Then 
	If state = 0 Then keypress tmp, 0, 0, 0 ; Down
	If state = 1 Then keypress tmp, 0, 0, 0: keypress tmp, 0, 2, 0 ; Press
	If state = 2 Then keypress tmp, 0, 2, 0 ; Up
EndIf
If shift = True Then keypress 161, 0, 2, 0 ; Release shift
End Function
