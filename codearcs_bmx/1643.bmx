; ID: 1643
; Author: ozak
; Date: 2006-03-16 03:55:14
; Title: Simple system tray module
; Description: System tray module

' Tray minimize win32 application

?Win32
' tray icons 
'
Const NIM_ADD:Int			= 0
Const NIM_MODIFY:Int		= 1
Const NIM_DELETE:Int		= 2
Const NIM_SETFOCUS:Int		= 3
Const NIM_SETVERSION:Int	= 4

Const NIF_MESSAGE:Int	= $00000001
Const NIF_ICON:Int		= $00000002
Const NIF_TIP:Int		= $00000004
Const NIF_STATE:Int	= $00000008
Const NIF_INFO:Int		= $00000010
Const NIF_GUID:Int		= $00000020

Type TNotifyIconData
	Field Size:Int
	Field HWND:Int
	Field id:Int
	Field Flags:Int
	Field CallbackMessage:Int
	Field Icon:Int 				' HICON	
	Field Tip:Long				' array [0..63] of AnsiChar;
	Field Tip2:Long
	Field Tip3:Long
	Field Tip4:Long
	Field Tip5:Long
	Field Tip6:Long
	Field Tip7:Long
	Field Tip8:Long
EndType

Extern "WIN32"
	Function Shell_NotifyIcon:Int( message:Int, notifyicondata:Byte Ptr) = "Shell_NotifyIconA@8"
EndExtern

Function SetNotifyIconDataTip( nid:TNotifyIconData, s:String)
	MemClear( Varptr nid.Tip, 64)
	If s.length > 0 Then
		Local p:Byte Ptr = s.ToCString()
		If s.length < 64 Then
			MemCopy( Varptr nid.Tip, p, s.length)
		Else			
			MemCopy( Varptr nid.Tip, p, 63)			
		EndIf
		MemFree( p)
	EndIf
EndFunction

'
' window messages (allso used by tray icon)
'
Const WM_MOUSEMOVE:Int        = $0200
Const WM_LBUTTONDOWN:Int      = $0201
Const WM_LBUTTONUP:Int        = $0202
Const WM_LBUTTONDBLCLK:Int    = $0203
Const WM_RBUTTONDOWN:Int      = $0204
Const WM_RBUTTONUP:Int        = $0205
Const WM_RBUTTONDBLCLK:Int    = $0206
Const WM_MBUTTONDOWN:Int      = $0207
Const WM_MBUTTONUP:Int        = $0208
Const WM_MBUTTONDBLCLK:Int    = $0209

'
' icon resources
'
Const IMAGE_BITMAP:Int      = 0
Const IMAGE_ICON:Int        = 1
Const IMAGE_CURSOR:Int      = 2
Const IMAGE_ENHMETAFILE:Int = 3

Const LR_DEFAULTSIZE:Int      = 64
Const LR_DEFAULTCOLOR:Int     = 0
Const LR_MONOCHROME:Int       = 1
Const LR_COLOR:Int            = 2
Const LR_COPYRETURNORG:Int    = 4
Const LR_COPYDELETEORG:Int    = 8
Const LR_LOADFROMFILE:Int     = 16
Const LR_LOADTRANSPARENT:Int  = 32
Const LR_LOADREALSIZE:Int     = 128
Const LR_LOADMAP3DCOLORS:Int  = 4096
Const LR_CREATEDIBSECTION:Int = 8192
Const LR_COPYFROMRESOURCE:Int = $4000 ' 0x4000
Const LR_SHARED:Int      	  = 32768           
	
Global nid:TNotifyIconData

Extern "WIN32"
	Function LoadImage_:Int( Instance:Int, Name$z, Type_:Int, DesiredX:Int, DesiredY:Int, Load:Int) = "LoadImageA@24"
EndExtern

Function LoadIcon:Int( filename:String, width:Int=16,Height:Int=16, Flags:Int=LR_SHARED)
	Return LoadImage_( 0, filename, IMAGE_ICON, width,Height, LR_LOADFROMFILE| Flags)
EndFunction

?

' Register tray icon
Function RegisterTrayIcon(window:TGadget, toolTip:String, iconFile:String)

?Win32
	nid = New TNotifyIconData
	nid.Size = SizeOf(TNotifyIconData)
	nid.hwnd = QueryGadget(window, QUERY_HWND)
	nid.id = 0
	nid.CallbackMessage = WM_MOUSEMOVE
	nid.Icon = LoadIcon( iconFile)
	nid.Flags = NIF_MESSAGE | NIF_TIP | NIF_ICON
	SetNotifyIconDataTip( nid, toolTip )
	Shell_NotifyIcon( NIM_ADD, nid)
?
End Function   

'Remove tray icon
Function RemoveTrayIcon()    
?Win32
	Shell_NotifyIcon(NIM_DELETE, nid)
?
End Function
