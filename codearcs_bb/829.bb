; ID: 829
; Author: fredborg
; Date: 2003-11-20 04:49:59
; Title: Resize and Maximize Blitz Window!
; Description: How to use Windows functions to modify a Blitz window.

;
; User32.decls - place in your userlibs folder
; And uncomment the commands.
;
;.lib "user32.dll"
;
;User32_GetActiveWindow%():"GetActiveWindow"
;User32_SetWindowLong% (hwnd%, nIndex%, dwNewLong%) : "SetWindowLongA"
;User32_GetSystemMetrics% (nIndex%) : "GetSystemMetrics"
;User32_MoveWindow% (hwnd%, x%, y%, nWidth%, nHeight%, bRepaint%) : "MoveWindow"
;User32_GetWindowRect% (hwnd%, lpRect*) : "GetWindowRect"
;User32_GetClientRect% (hwnd%, lpRect*) : "GetClientRect"
;User32_ClientToScreen% (hwnd%, lpwndpl*): "ClientToScreen"
;User32_ScreenToClient% (hwnd%, lpwndpl*): "ScreenToClient"
;User32_GetCursorPos%( lpwndpl* ):"GetCursorPos"

Type WinPoint
    Field X,Y
End Type

Type WinRect
	Field x,y,w,h
End Type

; Desktop Size
Const SM_CXSCREEN=0
Const SM_CYSCREEN=1

; Set Window Long 
Const GWL_WNDPROC		=(-4)
Const GWL_HINSTANCE		=(-6)
Const GWL_HWNDPARENT	=(-8)
Const GWL_STYLE			=(-16)
Const GWL_EXSTYLE		=(-20)
Const GWL_USERDATA		=(-21)
Const GWL_ID			=(-12)

; Window Style
Const WS_OVERLAPPED		=$0
Const WS_POPUP			=$80000000
Const WS_CHILD			=$40000000
Const WS_MINIMIZE		=$20000000
Const WS_VISIBLE		=$10000000
Const WS_DISABLED		=$8000000
Const WS_CLIPSIBLINGS	=$4000000
Const WS_CLIPCHILDREN	=$2000000
Const WS_MAXIMIZE		=$1000000
Const WS_CAPTION		=$C00000
Const WS_BORDER			=$800000
Const WS_DLGFRAME		=$400000
Const WS_VSCROLL		=$200000
Const WS_HSCROLL		=$100000
Const WS_SYSMENU		=$80000
Const WS_THICKFRAME		=$40000
Const WS_GROUP			=$20000
Const WS_TABSTOP		=$10000
Const WS_MINIMIZEBOX	=$20000
Const WS_MAXIMIZEBOX	=$10000
Const WS_TILED			=WS_OVERLAPPED
Const WS_ICONIC			=WS_MINIMIZE
Const WS_SIZEBOX		=WS_THICKFRAME
Const WS_OVERLAPPEDWINDOW=(WS_OVERLAPPED Or WS_CAPTION Or WS_SYSMENU Or WS_THICKFRAME Or WS_MINIMIZEBOX Or WS_MAXIMIZEBOX)
Const WS_TILEDWINDOW	=WS_OVERLAPPEDWINDOW
Const WS_POPUPWINDOW	=(WS_POPUP Or WS_BORDER Or WS_SYSMENU)
Const WS_CHILDWINDOW	=(WS_CHILD)

; Window Messages
Const SW_HIDE=0
Const SW_SHOWNORMAL=1
Const SW_NORMAL=1
Const SW_SHOWMINIMIZED=2
Const SW_SHOWMAXIMIZED=3
Const SW_MAXIMIZE=3
Const SW_SHOWNOACTIVATE=4
Const SW_SHOW=5
Const SW_MINIMIZE=6
Const SW_SHOWMINNOACTIVE=7
Const SW_SHOWNA=8
Const SW_RESTORE=9
Const SW_SHOWDEFAULT=10
Const SW_MAX=10

Type win
	Field TempPos.WinPoint
	Field MousePos.WinPoint
	Field WindowRect.WinRect
	Field DesktopW,DesktopH
	Field hWindow
	Field WindowStyle
	Field W,H,X,Y
	Field MX,MY,MZ,MSX,MSY,MSZ
End Type

;
; This function initializes the actual blitz window
Function InitBlitzWindow()

	; Prepare custom types
	win\TempPos.WinPoint	= New WinPoint
	win\MousePos.WinPoint	= New WinPoint
	win\WindowRect.WinRect	= New WinRect
	
	; Get Desktop Dimensions
	win\DesktopW = User32_GetSystemMetrics(SM_CXSCREEN)
	win\DesktopH = User32_GetSystemMetrics(SM_CYSCREEN)

	; Initialize Graphics Window
	Graphics3D win\DesktopW,win\DesktopH,0,2
	
	; Get hWnd pointer for the Blitz Window
	win\hWindow = User32_GetActiveWindow()

	; Set the Blitz Window Style
	win\WindowStyle = WS_VISIBLE + WS_BORDER + WS_MINIMIZEBOX + WS_MAXIMIZEBOX + WS_SIZEBOX + WS_SYSMENU + WS_DLGFRAME;+ WS_THICKFRAME
	User32_SetWindowLong(win\hWindow,GWL_STYLE,win\WindowStyle)
	
	; Resize and center Blitz Window	
	User32_MoveWindow(win\hWindow,(win\DesktopW/2)-(640/2),(win\DesktopH/2)-(480/2),640,480,True)

End Function


Function UpdateBlitzWindow()

	User32_GetWindowRect(win\hWindow,win\WindowRect)
	
	win\WindowRect\w = win\WindowRect\w-win\WindowRect\x
	win\WindowRect\h = win\WindowRect\h-win\WindowRect\y
			
	If win\WindowRect\w<320
		toosmall = True
		win\WindowRect\w = 320
	End If
	If win\WindowRect\h<240
		toosmall = True
		win\WindowRect\h = 240
	End If
	If toosmall
		User32_MoveWindow(win\hWindow,win\WindowRect\x,win\WindowRect\y,win\WindowRect\w,win\WindowRect\h,True)	
	End If
	
	; Get Screen position of pixel 0,0
	win\TempPos\x = 0
	win\TempPos\y = 0
	User32_ClientToScreen(win\hWindow,win\TempPos)
	win\X = win\TempPos\x
	win\Y = win\TempPos\y
	
	; Get Width and Height of the Blitz Window
	User32_GetClientRect(win\hWindow,win\WindowRect)
	win\W = win\WindowRect\w
	win\H = win\WindowRect\h
	Viewport 0,0,win\W,win\H
	
	; Store old mouse position
	win\MSX = win\MX
	win\MSY = win\MY
	win\MSZ = win\MZ
		
	; Get the mouse position (even if mouse is outside the Blitz Window)
	User32_GetCursorPos(win\MousePos)
	User32_ScreenToClient(win\hWindow,win\MousePos)
	win\MX		 = win\MousePos\x
	win\MY		 = win\MousePos\y
	win\MZ		 = MouseZ()

	; Update Mouse Speed
	win\MSX = win\MX-win\MSX
	win\MSY = win\MY-win\MSY
	win\MSZ = win\MZ-win\MSZ
	
End Function

; Example

Global win.win = New win
InitBlitzWindow()
SetBuffer BackBuffer()

Repeat
	Cls
	UpdateBlitzWindow()
	Text 0, 0,"Window Width  - "+win\W
	Text 0,10,"Window Height - "+win\H
	Text 0,20,"Window X - "+win\X
	Text 0,30,"Window Y - "+win\Y
	Text 0,40,"Mouse X - "+win\MX
	Text 0,50,"Mouse Y - "+win\MY
	Flip
Until KeyHit(1)
End
