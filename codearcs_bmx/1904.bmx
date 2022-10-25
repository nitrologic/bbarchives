; ID: 1904
; Author: Plash
; Date: 2007-01-26 08:02:58
; Title: Virtual Keys
; Description: Uses keybd_event from user32.dll, win32 only.

SuperStrict

Const KEYEVENTF_KEYUP:Byte = $00000002

'#Region Virtual Keys
Const VK_0:Byte = $30
Const VK_1:Byte = $31
Const VK_2:Byte = $32
Const VK_3:Byte = $33
Const VK_4:Byte = $34
Const VK_5:Byte = $35
Const VK_6:Byte = $36
Const VK_7:Byte = $37
Const VK_8:Byte = $38
Const VK_9:Byte = $39
Const VK_A:Byte = $41
Const VK_B:Byte = $42
Const VK_C:Byte = $43
Const VK_D:Byte = $44
Const VK_E:Byte = $45
Const VK_F:Byte = $46
Const VK_G:Byte = $47
Const VK_H:Byte = $48
Const VK_I:Byte = $49
Const VK_J:Byte = $4A
Const VK_K:Byte = $4B
Const VK_L:Byte = $4C
Const VK_M:Byte = $4D
Const VK_N:Byte = $4E
Const VK_O:Byte = $4F
Const VK_P:Byte = $50
Const VK_Q:Byte = $51
Const VK_R:Byte = $52
Const VK_S:Byte = $53
Const VK_T:Byte = $54
Const VK_U:Byte = $55
Const VK_V:Byte = $56
Const VK_W:Byte = $57
Const VK_X:Byte = $58
Const VK_Y:Byte = $59
Const VK_Z:Byte = $5A
Const VK_ADD:Byte = $6B
Const VK_ATTN:Byte = $F6
Const VK_BACK:Byte = $8
Const VK_CANCEL:Byte = $3
Const VK_CAPITAL:Byte = $14
Const VK_CLEAR:Byte = $C
Const VK_CONTROL:Byte = $11
Const VK_CRSEL:Byte = $F7
Const VK_DECIMAL:Byte = $6E
Const VK_DELETE:Byte = $2E
Const VK_DIVIDE:Byte = $6F
Const VK_DOWN:Byte = $28
Const VK_END:Byte = $23
Const VK_EREOF:Byte = $F9
Const VK_ESCAPE:Byte = $1B
Const VK_EXECUTE:Byte = $2B
Const VK_EXSEL:Byte = $F8
Const VK_F1:Byte = $70
Const VK_F10:Byte = $79
Const VK_F11:Byte = $7A
Const VK_F12:Byte = $7B
Const VK_F13:Byte = $7C
Const VK_F14:Byte = $7D
Const VK_F15:Byte = $7E
Const VK_F16:Byte = $7F
Const VK_F17:Byte = $80
Const VK_F18:Byte = $81
Const VK_F19:Byte = $82
Const VK_F2:Byte = $71
Const VK_F20:Byte = $83
Const VK_F21:Byte = $84
Const VK_F22:Byte = $85
Const VK_F23:Byte = $86
Const VK_F24:Byte = $87
Const VK_F3:Byte = $72
Const VK_F4:Byte = $73
Const VK_F5:Byte = $74
Const VK_F6:Byte = $75
Const VK_F7:Byte = $76
Const VK_F8:Byte = $77
Const VK_F9:Byte = $78
Const VK_HELP:Byte = $2F
Const VK_HOME:Byte = $24
Const VK_INSERT:Byte = $2D
Const VK_LBUTTON:Byte = $1
Const VK_LCONTROL:Byte = $A2
Const VK_LEFT:Byte = $25
Const VK_LMENU:Byte = $A4
Const VK_LSHIFT:Byte = $A0
Const VK_MBUTTON:Byte = $4
Const VK_MENU:Byte = $12
Const VK_MULTIPLY:Byte = $6A
Const VK_NEXT:Byte = $22
Const VK_NONAME:Byte = $FC
Const VK_NUMLOCK:Byte = $90
Const VK_NUMPAD0:Byte = $60
Const VK_NUMPAD1:Byte = $61
Const VK_NUMPAD2:Byte = $62
Const VK_NUMPAD3:Byte = $63
Const VK_NUMPAD4:Byte = $64
Const VK_NUMPAD5:Byte = $65
Const VK_NUMPAD6:Byte = $66
Const VK_NUMPAD7:Byte = $67
Const VK_NUMPAD8:Byte = $68
Const VK_NUMPAD9:Byte = $69
Const VK_OEM_CLEAR:Byte = $FE
Const VK_PA1:Byte = $FD
Const VK_PAUSE:Byte = $13
Const VK_PLAY:Byte = $FA
Const VK_PRINT:Byte = $2A
Const VK_PRIOR:Byte = $21
Const VK_PROCESSKEY:Byte = $E5
Const VK_RBUTTON:Byte = $2
Const VK_RCONTROL:Byte = $A3
Const VK_RETURN:Byte = $D
Const VK_RIGHT:Byte = $27
Const VK_RMENU:Byte = $A5
Const VK_RSHIFT:Byte = $A1
Const VK_SCROLL:Byte = $91
Const VK_SELECT:Byte = $29
Const VK_SEPARATOR:Byte = $6C
Const VK_SHIFT:Byte = $10
Const VK_SNAPSHOT:Byte = $2C
Const VK_SPACE:Byte = $20
Const VK_SUBTRACT:Byte = $6D
Const VK_TAB:Byte = $9
Const VK_UP:Byte = $26
Const VK_ZOOM:Byte = $FB
'#End Region

Extern "win32"
	Function keybd_event(bVk:Byte, bScan:Byte, dwFlags:Byte, dwExtraInfo:Int)
End Extern

'Example: KeyStroke VK_A
Function KeyStroke(VK_KEYV:Byte)
keybd_event(VK_KEYV, 0, 0, 0)
keybd_event(VK_KEYV, 0, KEYEVENTF_KEYUP, 0)
End Function
