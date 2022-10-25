; ID: 3214
; Author: BlitzSupport
; Date: 2015-07-24 16:15:45
; Title: Full-screen borderless window (Win32)
; Description: Rough example of creating a full-screen borderless window on Windows

' https://stackoverflow.com/questions/2382464/win32-full-screen-and-hiding-taskbar
' https://stackoverflow.com/questions/2398746/removing-window-border

' Used method in second code box of first link, plus second link for modifying border.

' Just found this method is recommended by Raymond Chen of M$, who knows his stuff:

' http://blogs.msdn.com/b/oldnewthing/archive/2005/05/05/414910.aspx

SuperStrict

' Used by FindWindow; set to name of your app!

AppTitle = "MY GAME"

' Open window at full size, depth = 0 for windowed mode...

Graphics DesktopWidth (), DesktopHeight (), 0

' Win32 monitor info structure...

Type MONITORINFO

	Field cbSize:Int

	Field rcMonitorLeft:Int
	Field rcMonitorTop:Int
	Field rcMonitorRight:Int
	Field rcMonitorBottom:Int

	Field rcWorkLeft:Int
	Field rcWorkTop:Int
	Field rcWorkRight:Int
	Field rcWorkBottom:Int

	Field dwFlags:Int

End Type

' Win32 constants...

Const MONITOR_DEFAULTTONEAREST:Int	= 2

Const GWL_STYLE:Int					= -16
Const GWL_EXSTYLE:Int				= -20

Const HWND_TOP:Int					= 0
Const SWP_FRAMECHANGED:Int			= $20

Const WS_POPUP:Int					= $80000000
Const WS_VISIBLE:Int				= $10000000
Const WS_CAPTION:Int				= $C00000
Const WS_THICKFRAME:Int				= $00040000
Const WS_MINIMIZE:Int				= $20000000
Const WS_MAXIMIZE:Int				= $01000000
Const WS_SYSMENU:Int				= $80000

Const WS_EX_DLGMODALFRAME:Int		= $00000001
Const WS_EX_CLIENTEDGE:Int			= $200
Const WS_EX_STATICEDGE:Int			= $20000

' Win32 function pointers...

Global FindWindow (lpClassName:Byte Ptr, lpWindowName:Byte Ptr)	"win32"
Global MonitorFromWindow (hwnd:Int, dwFlags:Int)				"win32"
Global GetMonitorInfo (hMonitor:Int, lpmi:Byte Ptr)				"win32"
Global GetWindowLong (hwnd:Int, nIndex:Int)						"win32"
Global SetWindowLong (hwnd:Int, nIndex:Int, dwNewLong:Int)		"win32"
Global SetWindowPos (hWnd:Int, hWndInsertAfter:Int, X:Int, Y:Int, cx:Int, cy:Int, uFlags:Int)

' Set up function pointers...

Local user32:Int = LoadLibraryA ("user32.dll")

If user32

	FindWindow			= GetProcAddress (user32, "FindWindowA")
	MonitorFromWindow	= GetProcAddress (user32, "MonitorFromWindow")
	GetMonitorInfo		= GetProcAddress (user32, "GetMonitorInfoA")
	GetWindowLong 		= GetProcAddress (user32, "GetWindowLongA")
	SetWindowLong		= GetProcAddress (user32, "SetWindowLongA")
	SetWindowPos		= GetProcAddress (user32, "SetWindowPos")
	
	If Not (FindWindow And MonitorFromWindow And GetMonitorInfo And GetWindowLong And SetWindowLong And SetWindowPos)
		Print "Missing function!"
		End
	EndIf
	
EndIf

' AppTitle allocated as C-string...

Local cbytes:Byte Ptr = AppTitle.ToCString ()

' Find app window...

Local appwindow:Int = FindWindow (Null, cbytes)

' Free C-string memory...

MemFree cbytes

' Find which monitor the app window is on...

Local monitor:Int = MonitorFromWindow (appwindow, MONITOR_DEFAULTTONEAREST)

' Monitor info...

Local mi:MONITORINFO = New MONITORINFO
mi.cbSize = SizeOf (MONITORINFO)

GetMonitorInfo monitor, mi

' Read window style/extended style...

Local style:Int		= GetWindowLong (appwindow, GWL_STYLE)
Local exstyle:Int	= GetWindowLong (appwindow, GWL_EXSTYLE)

' Prepare to remove unwanted styles...

style	= style And Not (WS_CAPTION | WS_THICKFRAME | WS_MINIMIZE | WS_MAXIMIZE | WS_SYSMENU)
exstyle	= exstyle And Not (WS_EX_DLGMODALFRAME | WS_EX_CLIENTEDGE | WS_EX_STATICEDGE)

' Remove unwanted styles...

SetWindowLong (appwindow, GWL_STYLE, style | WS_POPUP | WS_VISIBLE | SWP_FRAMECHANGED)
SetWindowLong (appwindow, GWL_EXSTYLE, exstyle)

' Update window...

SetWindowPos appwindow, HWND_TOP, mi.rcMonitorLeft, mi.rcMonitorTop, mi.rcMonitorRight - mi.rcMonitorLeft, mi.rcMonitorBottom - mi.rcMonitorTop, 0

Repeat

	Cls
	
	DrawRect MouseX (), MouseY (), 32, 32
	Flip
	
Until KeyHit (KEY_ESCAPE)

End
