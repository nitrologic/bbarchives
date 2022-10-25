; ID: 589
; Author: Jim Teeuwen
; Date: 2003-02-15 19:36:07
; Title: Win32 Constants
; Description: Just about any constant I could come up with for your win32 needs

;//
;// Win32 Constants
;//

;#region Peek Message Flags
Const PM_NOREMOVE = 0
Const PM_REMOVE = 1
Const PM_NOYIELD = 2
;#End Region
	
;#Region Windows Messages
Const WM_NULL                   = $0000
Const WM_CREATE                 = $0001
Const WM_DESTROY                = $0002
Const WM_MOVE                   = $0003
Const WM_SIZE                   = $0005
Const WM_ACTIVATE               = $0006
Const WM_SETFOCUS               = $0007
Const WM_KILLFOCUS              = $0008
Const WM_ENABLE                 = $000A
Const WM_SETREDRAW              = $000B
Const WM_SETTEXT                = $000C
Const WM_GETTEXT                = $000D
Const WM_GETTEXTLENGTH          = $000E
Const WM_PAINT                  = $000F
Const WM_CLOSE                  = $0010
Const WM_QUERYENDSESSION        = $0011
Const WM_QUIT                   = $0012
Const WM_QUERYOPEN              = $0013
Const WM_ERASEBKGND             = $0014
Const WM_SYSCOLORCHANGE         = $0015
Const WM_ENDSESSION             = $0016
Const WM_SHOWWINDOW             = $0018
Const WM_CTLCOLOR               = $0019
Const WM_WININICHANGE           = $001A
Const WM_SETTINGCHANGE          = $001A
Const WM_DEVMODECHANGE          = $001B
Const WM_ACTIVATEAPP            = $001C
Const WM_FONTCHANGE             = $001D
Const WM_TIMECHANGE             = $001E
Const WM_CANCELMODE             = $001F
Const WM_SETCURSOR              = $0020
Const WM_MOUSEACTIVATE          = $0021
Const WM_CHILDACTIVATE          = $0022
Const WM_QUEUESYNC              = $0023
Const WM_GETMINMAXINFO          = $0024
Const WM_PAINTICON              = $0026
Const WM_ICONERASEBKGND         = $0027
Const WM_NEXTDLGCTL             = $0028
Const WM_SPOOLERSTATUS          = $002A
Const WM_DRAWITEM               = $002B
Const WM_MEASUREITEM            = $002C
Const WM_DELETEITEM             = $002D
Const WM_VKEYTOITEM             = $002E
Const WM_CHARTOITEM             = $002F
Const WM_SETFONT                = $0030
Const WM_GETFONT                = $0031
Const WM_SETHOTKEY              = $0032
Const WM_GETHOTKEY              = $0033
Const WM_QUERYDRAGICON          = $0037
Const WM_COMPAREITEM            = $0039
Const WM_GETOBJECT              = $003D
Const WM_COMPACTING             = $0041
Const WM_COMMNOTIFY             = $0044 
Const WM_WINDOWPOSCHANGING      = $0046
Const WM_WINDOWPOSCHANGED       = $0047
Const WM_POWER                  = $0048
Const WM_COPYDATA               = $004A
Const WM_CANCELJOURNAL          = $004B
Const WM_NOTIFY                 = $004E
Const WM_INPUTLANGCHANGEREQUEST = $0050
Const WM_INPUTLANGCHANGE        = $0051
Const WM_TCARD                  = $0052
Const WM_HELP                   = $0053
Const WM_USERCHANGED            = $0054
Const WM_NOTIFYFORMAT           = $0055
Const WM_CONTEXTMENU            = $007B
Const WM_STYLECHANGING          = $007C
Const WM_STYLECHANGED           = $007D
Const WM_DISPLAYCHANGE          = $007E
Const WM_GETICON                = $007F
Const WM_SETICON                = $0080
Const WM_NCCREATE               = $0081
Const WM_NCDESTROY              = $0082
Const WM_NCCALCSIZE             = $0083
Const WM_NCHITTEST              = $0084
Const WM_NCPAINT                = $0085
Const WM_NCACTIVATE             = $0086
Const WM_GETDLGCODE             = $0087
Const WM_SYNCPAINT              = $0088
Const WM_NCMOUSEMOVE            = $00A0
Const WM_NCLBUTTONDOWN          = $00A1
Const WM_NCLBUTTONUP            = $00A2
Const WM_NCLBUTTONDBLCLK        = $00A3
Const WM_NCRBUTTONDOWN          = $00A4
Const WM_NCRBUTTONUP            = $00A5
Const WM_NCRBUTTONDBLCLK        = $00A6
Const WM_NCMBUTTONDOWN          = $00A7
Const WM_NCMBUTTONUP            = $00A8
Const WM_NCMBUTTONDBLCLK        = $00A9
Const WM_KEYDOWN                = $0100
Const WM_KEYUP                  = $0101
Const WM_CHAR                   = $0102
Const WM_DEADCHAR               = $0103
Const WM_SYSKEYDOWN             = $0104
Const WM_SYSKEYUP               = $0105
Const WM_SYSCHAR                = $0106
Const WM_SYSDEADCHAR            = $0107
Const WM_KEYLAST                = $0108
Const WM_IME_STARTCOMPOSITION   = $010D
Const WM_IME_ENDCOMPOSITION     = $010E
Const WM_IME_COMPOSITION        = $010F
Const WM_IME_KEYLAST            = $010F
Const WM_INITDIALOG             = $0110
Const WM_COMMAND                = $0111
Const WM_SYSCOMMAND             = $0112
Const WM_TIMER                  = $0113
Const WM_HSCROLL                = $0114
Const WM_VSCROLL                = $0115
Const WM_INITMENU               = $0116
Const WM_INITMENUPOPUP          = $0117
Const WM_MENUSELECT             = $011F
Const WM_MENUCHAR               = $0120
Const WM_ENTERIDLE              = $0121
Const WM_MENURBUTTONUP          = $0122
Const WM_MENUDRAG               = $0123
Const WM_MENUGETOBJECT          = $0124
Const WM_UNINITMENUPOPUP        = $0125
Const WM_MENUCOMMAND            = $0126
Const WM_CTLCOLORMSGBOX         = $0132
Const WM_CTLCOLOREDIT           = $0133
Const WM_CTLCOLORLISTBOX        = $0134
Const WM_CTLCOLORBTN            = $0135
Const WM_CTLCOLORDLG            = $0136
Const WM_CTLCOLORSCROLLBAR      = $0137
Const WM_CTLCOLORSTATIC         = $0138
Const WM_MOUSEMOVE              = $0200
Const WM_LBUTTONDOWN            = $0201
Const WM_LBUTTONUP              = $0202
Const WM_LBUTTONDBLCLK          = $0203
Const WM_RBUTTONDOWN            = $0204
Const WM_RBUTTONUP              = $0205
Const WM_RBUTTONDBLCLK          = $0206
Const WM_MBUTTONDOWN            = $0207
Const WM_MBUTTONUP              = $0208
Const WM_MBUTTONDBLCLK          = $0209
Const WM_MOUSEWHEEL             = $020A
Const WM_PARENTNOTIFY           = $0210
Const WM_ENTERMENULOOP          = $0211
Const WM_EXITMENULOOP           = $0212
Const WM_NEXTMENU               = $0213
Const WM_SIZING                 = $0214
Const WM_CAPTURECHANGED         = $0215
Const WM_MOVING                 = $0216
Const WM_DEVICECHANGE           = $0219
Const WM_MDICREATE              = $0220
Const WM_MDIDESTROY             = $0221
Const WM_MDIACTIVATE            = $0222
Const WM_MDIRESTORE             = $0223
Const WM_MDINEXT                = $0224
Const WM_MDIMAXIMIZE            = $0225
Const WM_MDITILE                = $0226
Const WM_MDICASCADE             = $0227
Const WM_MDIICONARRANGE         = $0228
Const WM_MDIGETACTIVE           = $0229
Const WM_MDISETMENU             = $0230
Const WM_ENTERSIZEMOVE          = $0231
Const WM_EXITSIZEMOVE           = $0232
Const WM_DROPFILES              = $0233
Const WM_MDIREFRESHMENU         = $0234
Const WM_IME_SETCONTEXT         = $0281
Const WM_IME_NOTIFY             = $0282
Const WM_IME_CONTROL            = $0283
Const WM_IME_COMPOSITIONFULL    = $0284
Const WM_IME_SELECT             = $0285
Const WM_IME_CHAR               = $0286
Const WM_IME_REQUEST            = $0288
Const WM_IME_KEYDOWN            = $0290
Const WM_IME_KEYUP              = $0291
Const WM_MOUSEHOVER             = $02A1
Const WM_MOUSELEAVE             = $02A3
Const WM_CUT                    = $0300
Const WM_COPY                   = $0301
Const WM_PASTE                  = $0302
Const WM_CLEAR                  = $0303
Const WM_UNDO                   = $0304
Const WM_RENDERFORMAT           = $0305
Const WM_RENDERALLFORMATS       = $0306
Const WM_DESTROYCLIPBOARD       = $0307
Const WM_DRAWCLIPBOARD          = $0308
Const WM_PAINTCLIPBOARD         = $0309
Const WM_VSCROLLCLIPBOARD       = $030A
Const WM_SIZECLIPBOARD          = $030B
Const WM_ASKCBFORMATNAME        = $030C
Const WM_CHANGECBCHAIN          = $030D
Const WM_HSCROLLCLIPBOARD       = $030E
Const WM_QUERYNEWPALETTE        = $030F
Const WM_PALETTEISCHANGING      = $0310
Const WM_PALETTECHANGED         = $0311
Const WM_HOTKEY                 = $0312
Const WM_PRINT                  = $0317
Const WM_PRINTCLIENT            = $0318
Const WM_HANDHELDFIRST          = $0358
Const WM_HANDHELDLAST           = $035F
Const WM_AFXFIRST               = $0360
Const WM_AFXLAST                = $037F
Const WM_PENWINFIRST            = $0380
Const WM_PENWINLAST             = $038F
Const WM_APP                    = $8000
Const WM_USER                   = $0400
Const WM_REFLECT                = WM_USER + $1c00
;#End Region

;#Region Window Styles
Const WS_OVERLAPPED       = $00000000
Const WS_POPUP            = $80000000
Const WS_CHILD            = $40000000
Const WS_MINIMIZE         = $20000000
Const WS_VISIBLE          = $10000000
Const WS_DISABLED         = $08000000
Const WS_CLIPSIBLINGS     = $04000000
Const WS_CLIPCHILDREN     = $02000000
Const WS_MAXIMIZE         = $01000000
Const WS_CAPTION          = $00C00000
Const WS_BORDER           = $00800000
Const WS_DLGFRAME         = $00400000
Const WS_VSCROLL          = $00200000
Const WS_HSCROLL          = $00100000
Const WS_SYSMENU          = $00080000
Const WS_THICKFRAME       = $00040000
Const WS_GROUP            = $00020000
Const WS_TABSTOP          = $00010000
Const WS_MINIMIZEBOX      = $00020000
Const WS_MAXIMIZEBOX      = $00010000
Const WS_TILED            = $00000000
Const WS_ICONIC           = $20000000
Const WS_SIZEBOX          = $00040000
Const WS_POPUPWINDOW      = $80880000
Const WS_OVERLAPPEDWINDOW = $00CF0000
Const WS_TILEDWINDOW      = $00CF0000
Const WS_CHILDWINDOW      = $40000000
;#End Region

;#Region Window Extended Styles
Const WS_EX_DLGMODALFRAME     = $00000001
Const WS_EX_NOPARENTNOTIFY    = $00000004
Const WS_EX_TOPMOST           = $00000008
Const WS_EX_ACCEPTFILES       = $00000010
Const WS_EX_TRANSPARENT       = $00000020
Const WS_EX_MDICHILD          = $00000040
Const WS_EX_TOOLWINDOW        = $00000080
Const WS_EX_WINDOWEDGE        = $00000100
Const WS_EX_CLIENTEDGE        = $00000200
Const WS_EX_CONTEXTHELP       = $00000400
Const WS_EX_RIGHT             = $00001000
Const WS_EX_LEFT              = $00000000
Const WS_EX_RTLREADING        = $00002000
Const WS_EX_LTRREADING        = $00000000
Const WS_EX_LEFTSCROLLBAR     = $00004000
Const WS_EX_RIGHTSCROLLBAR    = $00000000
Const WS_EX_CONTROLPARENT     = $00010000
Const WS_EX_STATICEDGE        = $00020000
Const WS_EX_APPWINDOW         = $00040000
Const WS_EX_OVERLAPPEDWINDOW  = $00000300
Const WS_EX_PALETTEWINDOW     = $00000188
Const WS_EX_LAYERED           = $00080000
;#End Region

;#Region ShowWindow Styles
Const SW_HIDE             = 0
Const SW_SHOWNORMAL       = 1
Const SW_NORMAL           = 1
Const SW_SHOWMINIMIZED    = 2
Const SW_SHOWMAXIMIZED    = 3
Const SW_MAXIMIZE         = 3
Const SW_SHOWNOACTIVATE   = 4
Const SW_SHOW             = 5
Const SW_MINIMIZE         = 6
Const SW_SHOWMINNOACTIVE  = 7
Const SW_SHOWNA           = 8
Const SW_RESTORE          = 9
Const SW_SHOWDEFAULT      = 10
Const SW_FORCEMINIMIZE    = 11
Const SW_MAX              = 11
;#End Region

;#Region SetWindowPos Z Order
Const HWND_TOP        = 0
Const HWND_BOTTOM     = 1
Const HWND_TOPMOST    = -1
Const HWND_NOTOPMOST  = -2
;#End Region

;#Region SetWindowPosFlags
Const SWP_NOSIZE          = $0001
Const SWP_NOMOVE          = $0002
Const SWP_NOZORDER        = $0004
Const SWP_NOREDRAW        = $0008
Const SWP_NOACTIVATE      = $0010
Const SWP_FRAMECHANGED    = $0020
Const SWP_SHOWWINDOW      = $0040
Const SWP_HIDEWINDOW      = $0080
Const SWP_NOCOPYBITS      = $0100
Const SWP_NOOWNERZORDER   = $0200 
Const SWP_NOSENDCHANGING  = $0400
Const SWP_DRAWFRAME       = $0020
Const SWP_NOREPOSITION    = $0200
Const SWP_DEFERERASE      = $2000
Const SWP_ASYNCWINDOWPOS  = $4000
;#End Region

;#Region Virtual Keys
Const VK_LBUTTON     = $01
Const VK_CANCEL      = $03
Const VK_BACK        = $08
Const VK_TAB         = $09
Const VK_CLEAR       = $0C
Const VK_RETURN      = $0D
Const VK_SHIFT       = $10
Const VK_CONTROL     = $11
Const VK_MENU        = $12
Const VK_CAPITAL     = $14
Const VK_ESCAPE      = $1B
Const VK_SPACE       = $20
Const VK_PRIOR       = $21
Const VK_NEXT        = $22
Const VK_END         = $23
Const VK_HOME        = $24
Const VK_LEFT        = $25
Const VK_UP          = $26
Const VK_RIGHT       = $27
Const VK_DOWN        = $28
Const VK_SELECT      = $29
Const VK_EXECUTE     = $2B
Const VK_SNAPSHOT    = $2C
Const VK_HELP        = $2F
Const VK_0       = $30
Const VK_1       = $31
Const VK_2       = $32
Const VK_3       = $33
Const VK_4       = $34
Const VK_5       = $35
Const VK_6       = $36
Const VK_7       = $37
Const VK_8       = $38
Const VK_9       = $39
Const VK_A       = $41
Const VK_B       = $42
Const VK_C       = $43
Const VK_D       = $44
Const VK_E       = $45
Const VK_F       = $46
Const VK_G       = $47
Const VK_H       = $48
Const VK_I       = $49
Const VK_J       = $4A
Const VK_K       = $4B
Const VK_L       = $4C
Const VK_M       = $4D
Const VK_N       = $4E
Const VK_O       = $4F
Const VK_P       = $50
Const VK_Q       = $51
Const VK_R       = $52
Const VK_S       = $53
Const VK_T       = $54
Const VK_U       = $55
Const VK_V       = $56
Const VK_W       = $57
Const VK_X       = $58
Const VK_Y       = $59
Const VK_Z       = $5A
Const VK_NUMPAD0       = $60
Const VK_NUMPAD1       = $61
Const VK_NUMPAD2       = $62
Const VK_NUMPAD3       = $63
Const VK_NUMPAD4       = $64
Const VK_NUMPAD5       = $65
Const VK_NUMPAD6       = $66
Const VK_NUMPAD7       = $67
Const VK_NUMPAD8       = $68
Const VK_NUMPAD9       = $69
Const VK_MULTIPLY      = $6A
Const VK_ADD           = $6B
Const VK_SEPARATOR     = $6C
Const VK_SUBTRACT      = $6D
Const VK_DECIMAL       = $6E
Const VK_DIVIDE        = $6F
Const VK_ATTN          = $F6
Const VK_CRSEL         = $F7
Const VK_EXSEL         = $F8
Const VK_EREOF         = $F9
Const VK_PLAY          = $FA  
Const VK_ZOOM          = $FB
Const VK_NONAME        = $FC
Const VK_PA1           = $FD
Const VK_OEM_CLEAR     = $FE
Const VK_LWIN          = $5B
Const VK_RWIN          = $5C
Const VK_APPS          = $5D   
Const VK_LSHIFT        = $A0   
Const VK_RSHIFT        = $A1   
Const VK_LCONTROL      = $A2   
Const VK_RCONTROL      = $A3   
Const VK_LMENU         = $A4   
Const VK_RMENU         = $A5
;#End Region

;#Region PatBlt Types
Const SRCCOPY          =   $00CC0020
Const SRCPAINT         =   $00EE0086
Const SRCAND           =   $008800C6
Const SRCINVERT        =   $00660046
Const SRCERASE         =   $00440328
Const NOTSRCCOPY       =   $00330008
Const NOTSRCERASE      =   $001100A6
Const MERGECOPY        =   $00C000CA
Const MERGEPAINT       =   $00BB0226
Const PATCOPY          =   $00F00021
Const PATPAINT         =   $00FB0A09
Const PATINVERT        =   $005A0049
Const DSTINVERT        =   $00550009
Const BLACKNESS        =   $00000042
Const WHITENESS        =   $00FF0062
;#End Region
	
;#Region Clipboard Formats	
Const CF_TEXT             = 1
Const CF_BITMAP           = 2
Const CF_METAFILEPICT     = 3
Const CF_SYLK             = 4
Const CF_DIF              = 5
Const CF_TIFF             = 6
Const CF_OEMTEXT          = 7
Const CF_DIB              = 8
Const CF_PALETTE          = 9
Const CF_PENDATA          = 10
Const CF_RIFF             = 11
Const CF_WAVE             = 12
Const CF_UNICODETEXT      = 13
Const CF_ENHMETAFILE      = 14
Const CF_HDROP            = 15
Const CF_LOCALE           = 16
Const CF_MAX              = 17
Const CF_OWNERDISPLAY     = $0080
Const CF_DSPTEXT          = $0081
Const CF_DSPBITMAP        = $0082
Const CF_DSPMETAFILEPICT  = $0083
Const CF_DSPENHMETAFILE   = $008E
Const CF_PRIVATEFIRST     = $0200
Const CF_PRIVATELAST      = $02FF
Const CF_GDIOBJFIRST      = $0300
Const CF_GDIOBJLAST       = $03FF
;#End Region

;#Region Common Controls Initialization flags
Const ICC_LISTVIEW_CLASSES   = $00000001
Const ICC_TREEVIEW_CLASSES   = $00000002
Const ICC_BAR_CLASSES        = $00000004
Const ICC_TAB_CLASSES        = $00000008
Const ICC_UPDOWN_CLASS       = $00000010
Const ICC_PROGRESS_CLASS     = $00000020
Const ICC_HOTKEY_CLASS       = $00000040
Const ICC_ANIMATE_CLASS      = $00000080
Const ICC_WIN95_CLASSES      = $000000FF
Const ICC_DATE_CLASSES       = $00000100
Const ICC_USEREX_CLASSES     = $00000200
Const ICC_COOL_CLASSES       = $00000400
Const ICC_INTERNET_CLASSES   = $00000800
Const ICC_PAGESCROLLER_CLASS = $00001000
Const ICC_NATIVEFNTCTL_CLASS = $00002000
;#End Region

;#Region Common Controls Styles
Const CCS_TOP                 = $00000001
Const CCS_NOMOVEY             = $00000002
Const CCS_BOTTOM              = $00000003
Const CCS_NORESIZE            = $00000004
Const CCS_NOPARENTALIGN       = $00000008
Const CCS_ADJUSTABLE          = $00000020
Const CCS_NODIVIDER           = $00000040
Const CCS_VERT                = $00000080
Const CCS_LEFT                = (CCS_VERT Or CCS_TOP)
Const CCS_RIGHT               = (CCS_VERT Or CCS_BOTTOM)
Const CCS_NOMOVEX             = (CCS_VERT Or CCS_NOMOVEY)
;#End Region

;#Region Toolbar button styles
Const TBSTYLE_BUTTON          = $0000
Const TBSTYLE_SEP             = $0001
Const TBSTYLE_CHECK           = $0002
Const TBSTYLE_GROUP           = $0004
Const TBSTYLE_CHECKGROUP      = (TBSTYLE_GROUP Or TBSTYLE_CHECK)
Const TBSTYLE_DROPDOWN        = $0008
Const TBSTYLE_AUTOSIZE        = $0010
Const TBSTYLE_NOPREFIX        = $0020
Const TBSTYLE_TOOLTIPS        = $0100
Const TBSTYLE_WRAPABLE        = $0200
Const TBSTYLE_ALTDRAG         = $0400
Const TBSTYLE_FLAT            = $0800
Const TBSTYLE_LIST            = $1000
Const TBSTYLE_CUSTOMERASE     = $2000
Const TBSTYLE_REGISTERDROP    = $4000
Const TBSTYLE_TRANSPARENT     = $8000
Const TBSTYLE_DRAWDDARROWS = $00000001
;#End Region

;#Region ToolBar Ex Styles
Const TBSTYLE_EX_DRAWDDARROWS        = $1
Const TBSTYLE_EX_HIDECLIPPEDBUTTONS  = $10
Const TBSTYLE_EX_DOUBLEBUFFER        = $80
;#End Region

;#Region ToolBar Messages
Const TB_ENABLEBUTTON         = (WM_USER + 1)
Const TB_CHECKBUTTON          = (WM_USER + 2)
Const TB_PRESSBUTTON          = (WM_USER + 3)
Const TB_HIDEBUTTON           = (WM_USER + 4)
Const TB_INDETERMINATE        = (WM_USER + 5)
Const TB_MARKBUTTON           = (WM_USER + 6)
Const TB_ISBUTTONENABLED      = (WM_USER + 9)
Const TB_ISBUTTONCHECKED      = (WM_USER + 10)
Const TB_ISBUTTONPRESSED      = (WM_USER + 11)
Const TB_ISBUTTONHIDDEN       = (WM_USER + 12)
Const TB_ISBUTTONINDETERMINATE= (WM_USER + 13)
Const TB_ISBUTTONHIGHLIGHTED  = (WM_USER + 14)
Const TB_SETSTATE             = (WM_USER + 17)
Const TB_GETSTATE             = (WM_USER + 18)
Const TB_ADDBITMAP            = (WM_USER + 19)
Const TB_ADDBUTTONSA          = (WM_USER + 20)
Const TB_INSERTBUTTONA        = (WM_USER + 21)
Const TB_ADDBUTTONS           = (WM_USER + 20)
Const TB_INSERTBUTTON         = (WM_USER + 21)
Const TB_DELETEBUTTON         = (WM_USER + 22)
Const TB_GETBUTTON            = (WM_USER + 23)
Const TB_BUTTONCOUNT          = (WM_USER + 24)
Const TB_COMMANDTOINDEX       = (WM_USER + 25)
Const TB_SAVERESTOREA         = (WM_USER + 26)
Const TB_CUSTOMIZE            = (WM_USER + 27)
Const TB_ADDSTRINGA           = (WM_USER + 28)
Const TB_GETITEMRECT          = (WM_USER + 29)
Const TB_BUTTONSTRUCTSIZE     = (WM_USER + 30)
Const TB_SETBUTTONSIZE        = (WM_USER + 31)
Const TB_SETBITMAPSIZE        = (WM_USER + 32)
Const TB_AUTOSIZE             = (WM_USER + 33)
Const TB_GETTOOLTIPS          = (WM_USER + 35)
Const TB_SETTOOLTIPS          = (WM_USER + 36)
Const TB_SETPARENT            = (WM_USER + 37)
Const TB_SETROWS              = (WM_USER + 39)
Const TB_GETROWS              = (WM_USER + 40)
Const TB_GETBITMAPFLAGS       = (WM_USER + 41)
Const TB_SETCMDID             = (WM_USER + 42)
Const TB_CHANGEBITMAP         = (WM_USER + 43)
Const TB_GETBITMAP            = (WM_USER + 44)
Const TB_GETBUTTONTEXTA       = (WM_USER + 45)
Const TB_GETBUTTONTEXTW       = (WM_USER + 75)
Const TB_REPLACEBITMAP        = (WM_USER + 46)
Const TB_SETINDENT            = (WM_USER + 47)
Const TB_SETIMAGELIST         = (WM_USER + 48)
Const TB_GETIMAGELIST         = (WM_USER + 49)
Const TB_LOADIMAGES           = (WM_USER + 50)
Const TB_GETRECT              = (WM_USER + 51)
Const TB_SETHOTIMAGELIST      = (WM_USER + 52)
Const TB_GETHOTIMAGELIST      = (WM_USER + 53)
Const TB_SETDISABLEDIMAGELIST = (WM_USER + 54)
Const TB_GETDISABLEDIMAGELIST = (WM_USER + 55)
Const TB_SETSTYLE             = (WM_USER + 56)
Const TB_GETSTYLE             = (WM_USER + 57)
Const TB_GETBUTTONSIZE        = (WM_USER + 58)
Const TB_SETBUTTONWIDTH       = (WM_USER + 59)
Const TB_SETMAXTEXTROWS       = (WM_USER + 60)
Const TB_GETTEXTROWS          = (WM_USER + 61)
Const TB_GETOBJECT            = (WM_USER + 62)
Const TB_GETBUTTONINFOW       = (WM_USER + 63)
Const TB_SETBUTTONINFOW       = (WM_USER + 64)
Const TB_GETBUTTONINFOA       = (WM_USER + 65)
Const TB_SETBUTTONINFOA       = (WM_USER + 66)
Const TB_INSERTBUTTONW        = (WM_USER + 67)
Const TB_ADDBUTTONSW          = (WM_USER + 68)
Const TB_HITTEST              = (WM_USER + 69)
Const TB_SETDRAWTEXTFLAGS     = (WM_USER + 70)
Const TB_GETHOTITEM           = (WM_USER + 71)
Const TB_SETHOTITEM           = (WM_USER + 72)
Const TB_SETANCHORHIGHLIGHT   = (WM_USER + 73)
Const TB_GETANCHORHIGHLIGHT   = (WM_USER + 74)
Const TB_SAVERESTOREW         = (WM_USER + 76)
Const TB_ADDSTRINGW           = (WM_USER + 77)
Const TB_MAPACCELERATORA      = (WM_USER + 78)
Const TB_GETINSERTMARK        = (WM_USER + 79)
Const TB_SETINSERTMARK        = (WM_USER + 80)
Const TB_INSERTMARKHITTEST    = (WM_USER + 81)
Const TB_MOVEBUTTON           = (WM_USER + 82)
Const TB_GETMAXSIZE           = (WM_USER + 83)
Const TB_SETEXTENDEDSTYLE     = (WM_USER + 84)
Const TB_GETEXTENDEDSTYLE     = (WM_USER + 85)
Const TB_GETPADDING           = (WM_USER + 86)
Const TB_SETPADDING           = (WM_USER + 87)
Const TB_SETINSERTMARKCOLOR   = (WM_USER + 88)
Const TB_GETINSERTMARKCOLOR   = (WM_USER + 89)
;#End Region

;#Region ToolBar Notifications
Const TTN_NEEDTEXTA           = ((0-520)-0)
Const TTN_NEEDTEXTW           = ((0-520)-10)
Const TBN_QUERYINSERT         = ((0-700)-6)
Const TBN_DROPDOWN            = ((0-700)-10)
Const TBN_HOTITEMCHANGE       = ((0 - 700) - 13)
;#End Region

;#Region Reflected Messages
Const OCM__BASE               = (WM_USER+$1c00)
Const OCM_COMMAND             = (OCM__BASE + WM_COMMAND)
Const OCM_CTLCOLORBTN         = (OCM__BASE + WM_CTLCOLORBTN)
Const OCM_CTLCOLOREDIT        = (OCM__BASE + WM_CTLCOLOREDIT)
Const OCM_CTLCOLORDLG         = (OCM__BASE + WM_CTLCOLORDLG)
Const OCM_CTLCOLORLISTBOX     = (OCM__BASE + WM_CTLCOLORLISTBOX)
Const OCM_CTLCOLORMSGBOX      = (OCM__BASE + WM_CTLCOLORMSGBOX)
Const OCM_CTLCOLORSCROLLBAR   = (OCM__BASE + WM_CTLCOLORSCROLLBAR)
Const OCM_CTLCOLORSTATIC      = (OCM__BASE + WM_CTLCOLORSTATIC)
Const OCM_CTLCOLOR            = (OCM__BASE + WM_CTLCOLOR)
Const OCM_DRAWITEM            = (OCM__BASE + WM_DRAWITEM)
Const OCM_MEASUREITEM         = (OCM__BASE + WM_MEASUREITEM)
Const OCM_DELETEITEM          = (OCM__BASE + WM_DELETEITEM)
Const OCM_VKEYTOITEM          = (OCM__BASE + WM_VKEYTOITEM)
Const OCM_CHARTOITEM          = (OCM__BASE + WM_CHARTOITEM)
Const OCM_COMPAREITEM         = (OCM__BASE + WM_COMPAREITEM)
Const OCM_HSCROLL             = (OCM__BASE + WM_HSCROLL)
Const OCM_VSCROLL             = (OCM__BASE + WM_VSCROLL)
Const OCM_PARENTNOTIFY        = (OCM__BASE + WM_PARENTNOTIFY)
Const OCM_NOTIFY              = (OCM__BASE + WM_NOTIFY)
;#End Region

;#Region Notification Messages
Const NM_FIRST      = (0-0)
Const NM_CUSTOMDRAW = (NM_FIRST-12)
Const NM_NCHITTEST  = (NM_FIRST-14) 
;#End Region

;#Region ToolTip Flags
Const TTF_CENTERTIP           = $0002
Const TTF_RTLREADING          = $0004
Const TTF_SUBCLASS            = $0010
Const TTF_TRACK               = $0020
Const TTF_ABSOLUTE            = $0080
Const TTF_TRANSPARENT         = $0100
Const TTF_DI_SETITEM          = $8000
;#End Region

;#Region Custom Draw Return Flags
Const CDRF_DODEFAULT          = $00000000
Const CDRF_NEWFONT            = $00000002
Const CDRF_SKIPDEFAULT        = $00000004
Const CDRF_NOTIFYPOSTPAINT    = $00000010
Const CDRF_NOTIFYITEMDRAW     = $00000020
Const CDRF_NOTIFYSUBITEMDRAW  = $00000020
Const CDRF_NOTIFYPOSTERASE    = $00000040
;#End Region

;#Region Custom Draw Item State Flags
Const CDIS_SELECTED       = $0001
Const CDIS_GRAYED         = $0002
Const CDIS_DISABLED       = $0004
Const CDIS_CHECKED        = $0008
Const CDIS_FOCUS          = $0010
Const CDIS_DEFAULT        = $0020
Const CDIS_HOT            = $0040
Const CDIS_MARKED         = $0080
Const CDIS_INDETERMINATE  = $0100
;#End Region

;#Region Custom Draw Draw State Flags
Const CDDS_PREPAINT           = $00000001
Const CDDS_POSTPAINT          = $00000002
Const CDDS_PREERASE           = $00000003
Const CDDS_POSTERASE          = $00000004
Const CDDS_ITEM               = $00010000
Const CDDS_ITEMPREPAINT       = (CDDS_ITEM Or CDDS_PREPAINT)
Const CDDS_ITEMPOSTPAINT      = (CDDS_ITEM Or CDDS_POSTPAINT)
Const CDDS_ITEMPREERASE       = (CDDS_ITEM Or CDDS_PREERASE)
Const CDDS_ITEMPOSTERASE      = (CDDS_ITEM Or CDDS_POSTERASE)
Const CDDS_SUBITEM            = $00020000
;#End Region

;#Region Toolbar button info flags
Const TBIF_IMAGE             = $00000001
Const TBIF_TEXT              = $00000002
Const TBIF_STATE             = $00000004
Const TBIF_STYLE             = $00000008
Const TBIF_LPARAM            = $00000010
Const TBIF_COMMAND           = $00000020
Const TBIF_SIZE              = $00000040
Const I_IMAGECALLBACK        = -1
Const I_IMAGENONE            = -2
;#End Region

;#Region Toolbar button state
Const TBSTATE_CHECKED         = $01
Const TBSTATE_PRESSED         = $02
Const TBSTATE_ENABLED         = $04
Const TBSTATE_HIDDEN          = $08
Const TBSTATE_INDETERMINATE   = $10
Const TBSTATE_WRAP            = $20
Const TBSTATE_ELLIPSES        = $40
Const TBSTATE_MARKED          = $80
;#End Region

;#Region Windows Hook Codes
Const WH_MSGFILTER        = (-1)
Const WH_JOURNALRECORD    = 0
Const WH_JOURNALPLAYBACK  = 1
Const WH_KEYBOARD         = 2
Const WH_GETMESSAGE       = 3
Const WH_CALLWNDPROC      = 4
Const WH_CBT              = 5
Const WH_SYSMSGFILTER     = 6
Const WH_MOUSE            = 7
Const WH_HARDWARE         = 8
Const WH_DEBUG            = 9
Const WH_SHELL            = 10
Const WH_FOREGROUNDIDLE   = 11
Const WH_CALLWNDPROCRET   = 12
Const WH_KEYBOARD_LL      = 13
Const WH_MOUSE_LL         = 14
;#End Region

;#Region Mouse Hook Filters
Const MSGF_DIALOGBOX      = 0
Const MSGF_MESSAGEBOX     = 1
Const MSGF_MENU           = 2
Const MSGF_SCROLLBAR      = 5
Const MSGF_NEXTWINDOW     = 6
;#End Region

;#Region Draw Text format flags
Const DT_TOP              = $00000000
Const DT_LEFT             = $00000000
Const DT_CENTER           = $00000001
Const DT_RIGHT            = $00000002
Const DT_VCENTER          = $00000004
Const DT_BOTTOM           = $00000008
Const DT_WORDBREAK        = $00000010
Const DT_SINGLELINE       = $00000020
Const DT_EXPANDTABS       = $00000040
Const DT_TABSTOP          = $00000080
Const DT_NOCLIP           = $00000100
Const DT_EXTERNALLEADING  = $00000200
Const DT_CALCRECT         = $00000400
Const DT_NOPREFIX         = $00000800
Const DT_INTERNAL         = $00001000
Const DT_EDITCONTROL      = $00002000
Const DT_PATH_ELLIPSIS    = $00004000
Const DT_END_ELLIPSIS     = $00008000
Const DT_MODIFYSTRING     = $00010000
Const DT_RTLREADING       = $00020000
Const DT_WORD_ELLIPSIS    = $00040000
;#End Region

;#Region Rebar Styles
Const RBS_TOOLTIPS        = $0100
Const RBS_VARHEIGHT       = $0200
Const RBS_BANDBORDERS     = $0400
Const RBS_FIXEDORDER      = $0800
Const RBS_REGISTERDROP    = $1000
Const RBS_AUTOSIZE        = $2000
Const RBS_VERTICALGRIPPER = $4000
Const RBS_DBLCLKTOGGLE    = $8000
;#End Region

;#Region Rebar Notifications
Const RBN_FIRST           = (0-831)
Const RBN_HEIGHTCHANGE    = (RBN_FIRST - 0)
Const RBN_GETOBJECT       = (RBN_FIRST - 1)
Const RBN_LAYOUTCHANGED   = (RBN_FIRST - 2)
Const RBN_AUTOSIZE        = (RBN_FIRST - 3)
Const RBN_BEGINDRAG       = (RBN_FIRST - 4)
Const RBN_ENDDRAG         = (RBN_FIRST - 5)
Const RBN_DELETINGBAND    = (RBN_FIRST - 6)
Const RBN_DELETEDBAND     = (RBN_FIRST - 7)
Const RBN_CHILDSIZE       = (RBN_FIRST - 8)
Const RBN_CHEVRONPUSHED   = (RBN_FIRST - 10)
;#End Region

;#Region Rebar Messages
Const CCM_FIRST           =    $2000
Const RB_INSERTBANDA      =	(WM_USER +  1)
Const RB_DELETEBAND       =	(WM_USER +  2)
Const RB_GETBARINFO       =	(WM_USER +  3)
Const RB_SETBARINFO       =	(WM_USER +  4)
Const RB_GETBANDINFO      =	(WM_USER +  5)
Const RB_SETBANDINFOA     =	(WM_USER +  6)
Const RB_SETPARENT        =	(WM_USER +  7)
Const RB_HITTEST          =	(WM_USER +  8)
Const RB_GETRECT          =	(WM_USER +  9)
Const RB_INSERTBANDW      =	(WM_USER +  10)
Const RB_SETBANDINFOW     =	(WM_USER +  11)
Const RB_GETBANDCOUNT     =	(WM_USER +  12)
Const RB_GETROWCOUNT      =	(WM_USER +  13)
Const RB_GETROWHEIGHT     =	(WM_USER +  14)
Const RB_IDTOINDEX        =	(WM_USER +  16)
Const RB_GETTOOLTIPS      =	(WM_USER +  17)
Const RB_SETTOOLTIPS      =	(WM_USER +  18)
Const RB_SETBKCOLOR       =	(WM_USER +  19)
Const RB_GETBKCOLOR       =	(WM_USER +  20)
Const RB_SETTEXTCOLOR     =	(WM_USER +  21)
Const RB_GETTEXTCOLOR     =	(WM_USER +  22)
Const RB_SIZETORECT       =	(WM_USER +  23)
Const RB_SETCOLORSCHEME   =	(CCM_FIRST + 2)
Const RB_GETCOLORSCHEME   =	(CCM_FIRST + 3)
Const RB_BEGINDRAG        =	(WM_USER + 24)
Const RB_ENDDRAG          =	(WM_USER + 25)
Const RB_DRAGMOVE         =	(WM_USER + 26)
Const RB_GETBARHEIGHT     =	(WM_USER + 27)
Const RB_GETBANDINFOW     =	(WM_USER + 28)
Const RB_GETBANDINFOA     =	(WM_USER + 29)
Const RB_MINIMIZEBAND     =	(WM_USER + 30)
Const RB_MAXIMIZEBAND     =	(WM_USER + 31)
Const RB_GETDROPTARGET    =	(CCM_FIRST + 4)
Const RB_GETBANDBORDERS   =	(WM_USER + 34)
Const RB_SHOWBAND         =	(WM_USER + 35)
Const RB_SETPALETTE       =	(WM_USER + 37)
Const RB_GETPALETTE       =	(WM_USER + 38)
Const RB_MOVEBAND         =	(WM_USER + 39)
Const RB_SETUNICODEFORMAT =   (CCM_FIRST + 5)
Const RB_GETUNICODEFORMAT =   (CCM_FIRST + 6)
;#End Region

;#Region Rebar Info Mask
Const RBBIM_STYLE         = $00000001
Const RBBIM_COLORS        = $00000002
Const RBBIM_TEXT          = $00000004
Const RBBIM_IMAGE         = $00000008
Const RBBIM_CHILD         = $00000010
Const RBBIM_CHILDSIZE     = $00000020
Const RBBIM_SIZE          = $00000040
Const RBBIM_BACKGROUND    = $00000080
Const RBBIM_ID            = $00000100
Const RBBIM_IDEALSIZE     = $00000200
Const RBBIM_LPARAM        = $00000400
Const BBIM_HEADERSIZE     = $00000800
;#End Region

;#Region Rebar Styles
Const RBBS_BREAK              = $1
Const RBBS_CHILDEDGE          = $4
Const RBBS_FIXEDBMP           = $20
Const RBBS_GRIPPERALWAYS	= $80
Const RBBS_USECHEVRON         = $200
;#End Region

;#Region Object types
Const OBJ_PEN             = 1
Const OBJ_BRUSH           = 2
Const OBJ_DC              = 3
Const OBJ_METADC          = 4
Const OBJ_PAL             = 5
Const OBJ_FONT            = 6
Const OBJ_BITMAP          = 7
Const OBJ_REGION          = 8
Const OBJ_METAFILE        = 9
Const OBJ_MEMDC           = 10
Const OBJ_EXTPEN          = 11
Const OBJ_ENHMETADC       = 12
Const OBJ_ENHMETAFILE     = 13
;#End Region

;#Region WM_MENUCHAR Return values
Const MNC_IGNORE  = 0
Const MNC_CLOSE   = 1
Const MNC_EXECUTE = 2
Const MNC_SELECT  = 3
;#End Region

;#Region Background Mode
Const TRANSPARENT = 1
Const OPAQUE = 2
;#End Region

;#Region ListView Messages
Const LVM_FIRST           =    $1000
Const LVM_GETSUBITEMRECT  = (LVM_FIRST + 56)
Const LVM_GETITEMSTATE    = (LVM_FIRST + 44)
Const LVM_GETITEMTEXTW    = (LVM_FIRST + 115)
;#End Region

;#Region Header Control Messages
Const HDM_FIRST        =  $1200
Const HDM_GETITEMRECT  = (HDM_FIRST + 7)
Const HDM_HITTEST      = (HDM_FIRST + 6)
Const HDM_SETIMAGELIST = (HDM_FIRST + 8)
Const HDM_GETITEMW     = (HDM_FIRST + 11)
Const HDM_ORDERTOINDEX = (HDM_FIRST + 15)
;#End Region

;#Region Header Control Notifications
Const HDN_FIRST       = (0-300)
Const HDN_BEGINTRACKW = (HDN_FIRST-26)
Const HDN_ENDTRACKW   = (HDN_FIRST-27)
Const HDN_ITEMCLICKW  = (HDN_FIRST-22)
;#End Region

;#Region Header Control HitTest Flags
Const HHT_NOWHERE             = $0001
Const HHT_ONHEADER            = $0002
Const HHT_ONDIVIDER           = $0004
Const HHT_ONDIVOPEN           = $0008
Const HHT_ABOVE               = $0100
Const HHT_BELOW               = $0200
Const HHT_TORIGHT             = $0400
Const HHT_TOLEFT              = $0800
;#End Region

;#Region List View sub item portion
Const LVIR_BOUNDS = 0
Const LVIR_ICON   = 1
Const LVIR_LABEL  = 2
;#End Region

;#Region Cursor Type
Const IDC_ARROW       = 32512
Const IDC_IBEAM       = 32513
Const IDC_WAIT        = 32514
Const IDC_CROSS       = 32515
Const IDC_UPARROW     = 32516
Const IDC_SIZE        = 32640
Const IDC_ICON        = 32641
Const IDC_SIZENWSE    = 32642
Const IDC_SIZENESW    = 32643
Const IDC_SIZEWE      = 32644
Const IDC_SIZENS      = 32645
Const IDC_SIZEALL     = 32646
Const IDC_NO          = 32648
Const IDC_HAND        = 32649
Const IDC_APPSTARTING = 32650
Const IDC_HELP        = 32651
;#End Region
	
;#Region Tracker Event Flags
Const TME_HOVER	= $00000001
Const TME_LEAVE	= $00000002
Const TME_QUERY	= $40000000
Const TME_CANCEL	= $80000000
;#End Region

;#Region Mouse Activate Flags
Const MA_ACTIVATE          = 1
Const MA_ACTIVATEANDEAT    = 2
Const MA_NOACTIVATE        = 3
Const MA_NOACTIVATEANDEAT  = 4
;#End Region

;#Region Dialog Codes
Const DLGC_WANTARROWS         = $0001
Const DLGC_WANTTAB            = $0002
Const DLGC_WANTALLKEYS        = $0004
Const DLGC_WANTMESSAGE        = $0004
Const DLGC_HASSETSEL          = $0008
Const DLGC_DEFPUSHBUTTON      = $0010
Const DLGC_UNDEFPUSHBUTTON	= $0020
Const DLGC_RADIOBUTTON        = $0040
Const DLGC_WANTCHARS          = $0080
Const DLGC_STATIC             = $0100
Const DLGC_BUTTON             = $2000
;#End Region

;#Region Update Layered Windows Flags
Const ULW_COLORKEY = $00000001
Const ULW_ALPHA    = $00000002
Const ULW_OPAQUE   = $00000004
;#End Region

;#Region Blend Flags
Const AC_SRC_OVER  = $00
Const AC_SRC_ALPHA = $01
;#End Region

;#Region ComboBox messages
Const CB_GETDROPPEDSTATE = $0157
;#End Region

;#Region TreeView Messages
Const TV_FIRST            =  $1100
Const TVM_GETITEMRECT     = (TV_FIRST + 4)
Const TVM_SETIMAGELIST	  = (TV_FIRST + 9)
Const TVM_HITTEST         = (TV_FIRST + 17)
Const TVM_SORTCHILDRENCB  = (TV_FIRST + 21)
Const TVM_GETITEMW        = (TV_FIRST + 62)
Const TVM_SETITEMW        = (TV_FIRST + 63)
Const TVM_INSERTITEMW     = (TV_FIRST + 50)
;#End Region

;#Region TreeViewImageListFlags
Const TVSIL_NORMAL  = 0
Const TVSIL_STATE   = 2
;#End Region

;#Region TreeViewItem Flags
Const TVIF_NONE               = $0000
Const TVIF_TEXT               = $0001
Const TVIF_IMAGE              = $0002
Const TVIF_PARAM              = $0004
Const TVIF_STATE              = $0008
Const TVIF_HANDLE             = $0010
Const TVIF_SELECTEDIMAGE      = $0020
Const TVIF_CHILDREN           = $0040
Const TVIF_INTEGRAL           = $0080
Const I_CHILDRENCALLBACK      = -1
Const LPSTR_TEXTCALLBACK      = -1
;Const I_IMAGECALLBACK   	= -1
;Const I_IMAGENONE             = -2
;#End Region

;#Region ListViewItem flags
Const LVIF_TEXT               = $0001
Const LVIF_IMAGE              = $0002
Const LVIF_PARAM              = $0004
Const LVIF_STATE              = $0008
Const LVIF_INDENT             = $0010
Const LVIF_NORECOMPUTE        = $0800
;#End Region

;#Region HeaderItem flags
Const HDI_WIDTH               = $0001
Const HDI_HEIGHT              = HDI_WIDTH
Const HDI_TEXT                = $0002
Const HDI_FORMAT              = $0004
Const HDI_LPARAM              = $0008
Const HDI_BITMAP              = $0010
Const HDI_IMAGE               = $0020
Const HDI_DI_SETITEM          = $0040
Const HDI_ORDER               = $0080
;#End Region

;#Region GetDCExFlags
Const DCX_WINDOW           = $00000001
Const DCX_CACHE            = $00000002
Const DCX_NORESETATTRS     = $00000004
Const DCX_CLIPCHILDREN     = $00000008
Const DCX_CLIPSIBLINGS     = $00000010
Const DCX_PARENTCLIP       = $00000020
Const DCX_EXCLUDERGN       = $00000040
Const DCX_INTERSECTRGN     = $00000080
Const DCX_EXCLUDEUPDATE    = $00000100
Const DCX_INTERSECTUPDATE  = $00000200
Const DCX_LOCKWINDOWUPDATE = $00000400
Const DCX_VALIDATE         = $00200000
;#End Region

;#Region HitTest 
Const HTERROR             = (-2)
Const HTTRANSPARENT       = (-1)
Const HTNOWHERE           =   0
Const HTCLIENT            =   1
Const HTCAPTION           =   2
Const HTSYSMENU           =   3
Const HTGROWBOX           =   4
Const HTSIZE              =   HTGROWBOX
Const HTMENU              =   5
Const HTHSCROLL           =   6
Const HTVSCROLL           =   7
Const HTMINBUTTON         =   8
Const HTMAXBUTTON         =   9
Const HTLEFT              =   10
Const HTRIGHT             =   11
Const HTTOP               =   12
Const HTTOPLEFT           =   13
Const HTTOPRIGHT          =   14
Const HTBOTTOM            =   15
Const HTBOTTOMLEFT        =   16
Const HTBOTTOMRIGHT       =   17
Const HTBORDER            =   18
Const HTREDUCE            =   HTMINBUTTON
Const HTZOOM              =   HTMAXBUTTON
Const HTSIZEFIRST         =   HTLEFT
Const HTSIZELAST          =   HTBOTTOMRIGHT
Const HTOBJECT            =   19
Const HTCLOSE             =   20
Const HTHELP              =   21
;#End Region

;#Region ActivateFlags
Const WA_INACTIVE     = 0
Const WA_ACTIVE       = 1
Const WA_CLICKACTIVE  = 2
;#End Region

;#Region StrechModeFlags
Const BLACKONWHITEConst   = 1
Const WHITEONBLACK        = 2
Const COLORONCOLOR        = 3
Const HALFTONE            = 4
Const MAXSTRETCHBLTMODE   = 4
;#End Region

;#Region ScrollBarFlags
Const SBS_HORZ                    = $0000
Const SBS_VERT                    = $0001
Const SBS_TOPALIGN                = $0002
Const SBS_LEFTALIGN               = $0002
Const SBS_BOTTOMALIGN             = $0004
Const SBS_RIGHTALIGN              = $0004
Const SBS_SIZEBOXTOPLEFTALIGN     = $0002
Const SBS_SIZEBOXBOTTOMRIGHTALIGN = $0004
Const SBS_SIZEBOX                 = $0008
Const SBS_SIZEGRIP                = $0010
;#End Region

;#Region System Metrics Codes
Const SM_CXSCREEN             = 0
Const SM_CYSCREEN             = 1
Const SM_CXVSCROLL            = 2
Const SM_CYHSCROLL            = 3
Const SM_CYCAPTION            = 4
Const SM_CXBORDER             = 5
Const SM_CYBORDER             = 6
Const SM_CXDLGFRAME           = 7
Const SM_CYDLGFRAME           = 8
Const SM_CYVTHUMB             = 9
Const SM_CXHTHUMB             = 10
Const SM_CXICON               = 11
Const SM_CYICON               = 12
Const SM_CXCURSOR             = 13
Const SM_CYCURSOR             = 14
Const SM_CYMENU               = 15
Const SM_CXFULLSCREEN         = 16
Const SM_CYFULLSCREEN         = 17
Const SM_CYKANJIWINDOW        = 18
Const SM_MOUSEPRESENT         = 19
Const SM_CYVSCROLL            = 20
Const SM_CXHSCROLL            = 21
Const SM_DEBUG                = 22
Const SM_SWAPBUTTON           = 23
Const SM_RESERVED1            = 24
Const SM_RESERVED2            = 25
Const SM_RESERVED3            = 26
Const SM_RESERVED4            = 27
Const SM_CXMIN                = 28
Const SM_CYMIN                = 29
Const SM_CXSIZE               = 30
Const SM_CYSIZE               = 31
Const SM_CXFRAME              = 32
Const SM_CYFRAME              = 33
Const SM_CXMINTRACK           = 34
Const SM_CYMINTRACK           = 35
Const SM_CXDOUBLECLK          = 36
Const SM_CYDOUBLECLK          = 37
Const SM_CXICONSPACING        = 38
Const SM_CYICONSPACING        = 39
Const SM_MENUDROPALIGNMENT    = 40
Const SM_PENWINDOWS           = 41
Const SM_DBCSENABLED          = 42
Const SM_CMOUSEBUTTONS        = 43
Const SM_CXFIXEDFRAME         = SM_CXDLGFRAME
Const SM_CYFIXEDFRAME         = SM_CYDLGFRAME
Const SM_CXSIZEFRAME          = SM_CXFRAME
Const SM_CYSIZEFRAME          = SM_CYFRAME
Const SM_SECURE               = 44
Const SM_CXEDGE               = 45
Const SM_CYEDGE               = 46
Const SM_CXMINSPACING         = 47
Const SM_CYMINSPACING         = 48
Const SM_CXSMICON             = 49
Const SM_CYSMICON             = 50
Const SM_CYSMCAPTION          = 51
Const SM_CXSMSIZE             = 52
Const SM_CYSMSIZE             = 53
Const SM_CXMENUSIZE           = 54
Const SM_CYMENUSIZE           = 55
Const SM_ARRANGE              = 56
Const SM_CXMINIMIZED          = 57
Const SM_CYMINIMIZED          = 58
Const SM_CXMAXTRACK           = 59
Const SM_CYMAXTRACK           = 60
Const SM_CXMAXIMIZED          = 61
Const SM_CYMAXIMIZED          = 62
Const SM_NETWORK              = 63
Const SM_CLEANBOOT            = 67
Const SM_CXDRAG               = 68
Const SM_CYDRAG               = 69
Const SM_SHOWSOUNDS           = 70
Const SM_CXMENUCHECK          = 71  
Const SM_CYMENUCHECK          = 72
Const SM_SLOWMACHINE          = 73
Const SM_MIDEASTENABLED       = 74
Const SM_MOUSEWHEELPRESENT    = 75
Const SM_XVIRTUALSCREEN       = 76
Const SM_YVIRTUALSCREEN       = 77
Const SM_CXVIRTUALSCREEN      = 78
Const SM_CYVIRTUALSCREEN      = 79
Const SM_CMONITORS            = 80
Const SM_SAMEDISPLAYFORMAT    = 81
Const SM_CMETRICS             = 83
;#End Region

;#Region ScrollBarTypes
Const SB_HORZ  = 0
Const SB_VERT  = 1
Const SB_CTL   = 2
Const SB_BOTH  = 3
;#End Region

;#Region SrollBarInfoFlags
Const SIF_RANGE           = $0001
Const SIF_PAGE            = $0002
Const SIF_POS             = $0004
Const SIF_DISABLENOSCROLL = $0008
Const SIF_TRACKPOS        = $0010
Const SIF_ALL             = (SIF_RANGE Or SIF_PAGE Or SIF_POS Or SIF_TRACKPOS)
;#End Region

;#Region Enable ScrollBar flags
Const ESB_ENABLE_BOTH     = $0000
Const ESB_DISABLE_BOTH    = $0003
Const ESB_DISABLE_LEFT    = $0001
Const ESB_DISABLE_RIGHT   = $0002
Const ESB_DISABLE_UP      = $0001
Const ESB_DISABLE_DOWN    = $0002
Const ESB_DISABLE_LTUP    = ESB_DISABLE_LEFT
Const ESB_DISABLE_RTDN    = ESB_DISABLE_RIGHT
;#End Region

;#Region Scroll Requests
Const SB_LINEUP           = 0
Const SB_LINELEFT         = 0
Const SB_LINEDOWN         = 1
Const SB_LINERIGHT        = 1
Const SB_PAGEUP           = 2
Const SB_PAGELEFT         = 2
Const SB_PAGEDOWN         = 3
Const SB_PAGERIGHT        = 3
Const SB_THUMBPOSITION    = 4
Const SB_THUMBTRACK       = 5
Const SB_TOP              = 6
Const SB_LEFT             = 6
Const SB_BOTTOM           = 7
Const SB_RIGHT            = 7
Const SB_ENDSCROLL        = 8
;#End Region

;#Region SrollWindowEx flags
Const SW_SCROLLCHILDREN   = $0001
Const SW_INVALIDATE       = $0002
Const SW_ERASE            = $0004
Const SW_SMOOTHSCROLL     = $0010
;#End Region

;#region ImageListFlags
Const ILC_MASK             = $0001
Const ILC_COLOR            = $0000
Const ILC_COLORDDB         = $00FE
Const ILC_COLOR4           = $0004
Const ILC_COLOR8           = $0008
Const ILC_COLOR16          = $0010
Const ILC_COLOR24          = $0018
Const ILC_COLOR32          = $0020
Const ILC_PALETTE          = $0800
;#end region

;#region ImageListDrawFlags
Const ILD_NORMAL              = $0000
Const ILD_TRANSPARENT         = $0001
Const ILD_MASK                = $0010
Const ILD_IMAGE               = $0020
Const ILD_ROP                 = $0040
Const ILD_BLEND25             = $0002
Const ILD_BLEND50             = $0004
Const ILD_OVERLAYMASK         = $0F00
;#end region

;#region List View Notifications
Const LVN_FIRST             = (0-100)
Const LVN_GETDISPINFOW      = (LVN_FIRST-77)
Const LVN_SETDISPINFOA      = (LVN_FIRST-51)
;#end region

;#region Drive Type
Const DRIVE_UNKNOWN     = 0
Const DRIVE_NO_ROOT_DIR = 1
Const DRIVE_REMOVABLE   = 2
Const DRIVE_FIXED       = 3
Const DRIVE_REMOTE      = 4
Const DRIVE_CDROM       = 5
Const DRIVE_RAMDISK     = 6
;#End region

;#region Shell File Info Flags
Const SHGFI_ICON              = $000000100
Const SHGFI_DISPLAYNAME       = $000000200
Const SHGFI_TYPENAME          = $000000400
Const SHGFI_ATTRIBUTES        = $000000800
Const SHGFI_ICONLOCATION      = $000001000  
Const SHGFI_EXETYPE           = $000002000
Const SHGFI_SYSICONINDEX      = $000004000
Const SHGFI_LINKOVERLAY       = $000008000   
Const SHGFI_SELECTED          = $000010000  
Const SHGFI_ATTR_SPECIFIED    = $000020000 
Const SHGFI_LARGEICON         = $000000000
Const SHGFI_SMALLICON         = $000000001 
Const SHGFI_OPENICON          = $000000002
Const SHGFI_SHELLICONSIZE     = $000000004
Const SHGFI_PIDL              = $000000008
Const SHGFI_USEFILEATTRIBUTES = $000000010
;#end region

;#region Shell Special Folder
Const CSIDL_DESKTOP                   = $0000
Const CSIDL_INTERNET                  = $0001
Const CSIDL_PROGRAMS                  = $0002
Const CSIDL_CONTROLS                  = $0003
Const CSIDL_PRINTERS                  = $0004
Const CSIDL_PERSONAL                  = $0005
Const CSIDL_FAVORITES                 = $0006
Const CSIDL_STARTUP                   = $0007
Const CSIDL_RECENT                    = $0008
Const CSIDL_SENDTO                    = $0009
Const CSIDL_BITBUCKET                 = $000a
Const CSIDL_STARTMENU                 = $000b
Const CSIDL_DESKTOPDIRECTORY          = $0010
Const CSIDL_DRIVES                    = $0011
Const CSIDL_NETWORK                   = $0012
Const CSIDL_NETHOOD                   = $0013
Const CSIDL_FONTS                     = $0014
Const CSIDL_TEMPLATES                 = $0015
Const CSIDL_COMMON_STARTMENU          = $0016
Const CSIDL_COMMON_PROGRAMS           = $0017
Const CSIDL_COMMON_STARTUP            = $0018
Const CSIDL_COMMON_DESKTOPDIRECTORY   = $0019
Const CSIDL_APPDATA                   = $001a
Const CSIDL_PRINTHOOD                 = $001b
Const CSIDL_ALTSTARTUP                = $001d         
Const CSIDL_COMMON_ALTSTARTUP         = $001e         
Const CSIDL_COMMON_FAVORITES          = $001f
Const CSIDL_INTERNET_CACHE            = $0020
Const CSIDL_COOKIES                   = $0021
Const CSIDL_HISTORY                   = $0022
;#end region

;#region ImageList Draw Colors
Const CLR_NONE		= $FFFFFFFF
Const CLR_DEFAULT       = $FF000000
;#end region

;#region ShellEnumFlags
Const SHCONTF_FOLDERS         = 32      ;// For shell browser
Const SHCONTF_NONFOLDERS      = 64      ;// For Default view
Const SHCONTF_INCLUDEHIDDEN   = 128     ;// For hidden/system objects
;#end region

;#region ShellGetDisplayNameOfFlags
Const SHGDN_NORMALConst        = 0         ;// Default (display purpose)
Const SHGDN_INFOLDERConst      = 1         ;// displayed under a folder (relative)
Const SHGDN_INCLUDE_NONFILESYS = $2000	;// If Not set display names For shell name space items that are Not in the file system will fail.
Const SHGDN_FORADDRESSBARConst = $4000     ;// For displaying in the address (drives dropdown) bar
Const SHGDN_FORPARSINGConst 	 = $8000     ;// For ParseDisplayName Or path
;#end region

;#region STRRETFlags
Const STRRET_WSTR     = $0000	;// Use STRRET.pOleStr
Const STRRET_OFFSET   = $0001	;// Use STRRET.uOffset To Ansi
Const STRRET_CSTR     = $0002	;// Use STRRET.cStr
;#end region

;#region GetAttributeOfFlags
Const DROPEFFECT_NONE	     = 0
Const DROPEFFECT_COPY	     = 1
Const DROPEFFECT_MOVE	     = 2
Const DROPEFFECT_LINK	     = 4
Const DROPEFFECT_SCROLL	     = $80000000
Const SFGAO_CANCOPY          = DROPEFFECT_COPY	;// Objects can be copied
Const SFGAO_CANMOVE          = DROPEFFECT_MOVE	;// Objects can be moved
Const SFGAO_CANLINK          = DROPEFFECT_LINK	;// Objects can be linked
Const SFGAO_CANRENAME        = $00000010	;// Objects can be renamed
Const SFGAO_CANDELETE        = $00000020	;// Objects can be deleted
Const SFGAO_HASPROPSHEET     = $00000040	;// Objects have property sheets
Const SFGAO_DROPTARGET       = $00000100	;// Objects are drop target
Const SFGAO_CAPABILITYMASK   = $00000177
Const SFGAO_LINK             = $00010000	;// Shortcut (link)
Const SFGAO_SHARE            = $00020000	;// shared
Const SFGAO_READONLY         = $00040000	;// Read-only
Const SFGAO_GHOSTED          = $00080000	;// ghosted icon
Const SFGAO_HIDDEN           = $00080000	;// hidden Object
Const SFGAO_DISPLAYATTRMASK  = $000F0000
Const SFGAO_FILESYSANCESTOR  = $10000000	;// It contains file system folder
Const SFGAO_FOLDER           = $20000000	;// It's a folder.
Const SFGAO_FILESYSTEM       = $40000000	;// is a file system thing (file/folder/root)
Const SFGAO_HASSUBFOLDER     = $80000000	;// Expandable in the map pane
Const SFGAO_CONTENTSMASK     = $80000000
Const SFGAO_VALIDATE         = $01000000	;// invalidate cached information
Const SFGAO_REMOVABLE        = $02000000	;// is this removeable media?
Const SFGAO_COMPRESSED       = $04000000	;// Object is compressed (use alt Color)
Const SFGAO_BROWSABLE        = $08000000	;// is in-place browsable
Const SFGAO_NONENUMERATED    = $00100000	;// is a non-enumerated Object
Const SFGAO_NEWCONTENT       = $00200000	;// should show bold in explorer tree
;#end region

;#region ListViewItemState
Const LVIS_FOCUSED            = $0001
Const LVIS_SELECTED           = $0002
Const LVIS_CUT                = $0004
Const LVIS_DROPHILITED        = $0008
Const LVIS_ACTIVATING         = $0020
Const LVIS_OVERLAYMASK        = $0F00
Const LVIS_STATEIMAGEMASK     = $F000
;#end region

;#region TreeViewItemInsertPosition
Const TVI_ROOT                = $FFFF0000
Const TVI_FIRST               = $FFFF0001
Const TVI_LAST                = $FFFF0002
Const TVI_SORT                = $FFFF0003
;#end region

;#region TreeViewNotifications
Const TVN_FIRST               =  -400
Const TVN_SELCHANGINGA        = (TVN_FIRST-1)
Const TVN_SELCHANGINGW        = (TVN_FIRST-50)
Const TVN_SELCHANGEDA         = (TVN_FIRST-2)
Const TVN_SELCHANGEDW         = (TVN_FIRST-51)
Const TVN_GETDISPINFOA        = (TVN_FIRST-3)
Const TVN_GETDISPINFOW        = (TVN_FIRST-52)
Const TVN_SETDISPINFOA        = (TVN_FIRST-4)
Const TVN_SETDISPINFOW        = (TVN_FIRST-53)
Const TVN_ITEMEXPANDINGA      = (TVN_FIRST-5)
Const TVN_ITEMEXPANDINGW      = (TVN_FIRST-54)
Const TVN_ITEMEXPANDEDA       = (TVN_FIRST-6)
Const TVN_ITEMEXPANDEDW       = (TVN_FIRST-55)
Const TVN_BEGINDRAGA          = (TVN_FIRST-7)
Const TVN_BEGINDRAGW          = (TVN_FIRST-56)
Const TVN_BEGINRDRAGA         = (TVN_FIRST-8)
Const TVN_BEGINRDRAGW         = (TVN_FIRST-57)
Const TVN_DELETEITEMA         = (TVN_FIRST-9)
Const TVN_DELETEITEMW         = (TVN_FIRST-58)
Const TVN_BEGINLABELEDITA     = (TVN_FIRST-10)
Const TVN_BEGINLABELEDITW     = (TVN_FIRST-59)
Const TVN_ENDLABELEDITA       = (TVN_FIRST-11)
Const TVN_ENDLABELEDITW       = (TVN_FIRST-60)
Const TVN_KEYDOWN             = (TVN_FIRST-12)
Const TVN_GETINFOTIPA         = (TVN_FIRST-13)
Const TVN_GETINFOTIPW         = (TVN_FIRST-14)
Const TVN_SINGLEEXPAND        = (TVN_FIRST-15)
;#end region

;#region TreeViewItemExpansion
Const TVE_COLLAPSE            = $0001
Const TVE_EXPAND              = $0002
Const TVE_TOGGLE              = $0003
Const TVE_EXPANDPARTIAL       = $4000
Const TVE_COLLAPSERESET       = $8000
;#end region

;#region WinErrors
Const NOERROR = $00000000
;#end region

;#region TreeViewHitTest
Const TVHT_NOWHERE           = $0001
Const TVHT_ONITEMICON        = $0002
Const TVHT_ONITEMLABEL       = $0004
Const TVHT_ONITEMINDENT      = $0008
Const TVHT_ONITEMBUTTON      = $0010
Const TVHT_ONITEMRIGHT       = $0020
Const TVHT_ONITEMSTATEICON   = $0040
Const TVHT_ABOVE             = $0100
Const TVHT_BELOW             = $0200
Const TVHT_TORIGHT           = $0400
Const TVHT_TOLEFT            = $0800
Const TVHT_ONITEM            = (TVHT_ONITEMICON Or TVHT_ONITEMLABEL Or TVHT_ONITEMSTATEICON)
;#End Region

;#Region TreeViewItemState
Const TVIS_SELECTED           = $0002
Const TVIS_CUT                = $0004
Const TVIS_DROPHILITED        = $0008
Const TVIS_BOLD               = $0010
Const TVIS_EXPANDED           = $0020
Const TVIS_EXPANDEDONCE       = $0040
Const TVIS_EXPANDPARTIAL      = $0080
Const TVIS_OVERLAYMASK        = $0F00
Const TVIS_STATEIMAGEMASK     = $F000
Const TVIS_USERMASK           = $F000
;#End Region

;#Region Windows System Objects
;// Reserved IDs For system objects
Const OBJID_WINDOW        = $00000000
Const OBJID_SYSMENU       = $FFFFFFFF
Const OBJID_TITLEBAR      = $FFFFFFFE
Const OBJID_MENU          = $FFFFFFFD
Const OBJID_CLIENT        = $FFFFFFFC
Const OBJID_VSCROLL       = $FFFFFFFB
Const OBJID_HSCROLL       = $FFFFFFFA
Const OBJID_SIZEGRIP      = $FFFFFFF9
Const OBJID_CARET         = $FFFFFFF8
Const OBJID_CURSOR        = $FFFFFFF7
Const OBJID_ALERT         = $FFFFFFF6
Const OBJID_SOUND         = $FFFFFFF5
;#End Region

;#Region SystemState

Const STATE_SYSTEM_UNAVAILABLE        = $00000001 ;// Disabled
Const STATE_SYSTEM_SELECTED           = $00000002
Const STATE_SYSTEM_FOCUSED            = $00000004
Const STATE_SYSTEM_PRESSED            = $00000008
Const STATE_SYSTEM_CHECKED            = $00000010
Const STATE_SYSTEM_MIXED              = $00000020 ;// 3-state checkbox Or toolbar button
Const STATE_SYSTEM_READONLY           = $00000040
Const STATE_SYSTEM_HOTTRACKED         = $00000080
Const STATE_SYSTEM_DEFAULT            = $00000100
Const STATE_SYSTEM_EXPANDED           = $00000200
Const STATE_SYSTEM_COLLAPSED          = $00000400
Const STATE_SYSTEM_BUSY               = $00000800
Const STATE_SYSTEM_FLOATING           = $00001000 ;// Children "owned" Not "contained" by parent
Const STATE_SYSTEM_MARQUEED           = $00002000
Const STATE_SYSTEM_ANIMATED           = $00004000
Const STATE_SYSTEM_INVISIBLE          = $00008000
Const STATE_SYSTEM_OFFSCREEN          = $00010000
Const STATE_SYSTEM_SIZEABLE           = $00020000
Const STATE_SYSTEM_MOVEABLE           = $00040000
Const STATE_SYSTEM_SELFVOICING        = $00080000
Const STATE_SYSTEM_FOCUSABLE          = $00100000
Const STATE_SYSTEM_SELECTABLE         = $00200000
Const STATE_SYSTEM_LINKED             = $00400000
Const STATE_SYSTEM_TRAVERSED          = $00800000
Const STATE_SYSTEM_MULTISELECTABLE    = $01000000  ;// Supports multiple selection
Const STATE_SYSTEM_EXTSELECTABLE      = $02000000  ;// Supports extended selection
Const STATE_SYSTEM_ALERT_LOW          = $04000000  ;// This information is of low priority
Const STATE_SYSTEM_ALERT_MEDIUM       = $08000000  ;// This information is of medium priority
Const STATE_SYSTEM_ALERT_HIGH         = $10000000  ;// This information is of high priority
Const STATE_SYSTEM_VALID              = $1FFFFFFF
;#End Region


;#Region QueryContextMenuFlags
Const CMF_NORMAL              = $00000000
Const CMF_DEFAULTONLY         = $00000001
Const CMF_VERBSONLY           = $00000002
Const CMF_EXPLORE             = $00000004
Const CMF_NOVERBS             = $00000008
Const CMF_CANRENAME           = $00000010
Const CMF_NODEFAULT           = $00000020
Const CMF_INCLUDESTATIC       = $00000040
Const CMF_RESERVED            = $ffff0000  
;#End Region

;#Region GetWindowLongFlags
Const GWL_WNDPROC         = (-4)
Const GWL_HINSTANCE       = (-6)
Const GWL_HWNDPARENT      = (-8)
Const GWL_STYLE           = (-16)
Const GWL_EXSTYLE         = (-20)
Const GWL_USERDATA        = (-21)
Const GWL_ID              = (-12)
;#End Region
