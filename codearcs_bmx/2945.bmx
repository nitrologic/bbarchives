; ID: 2945
; Author: col
; Date: 2012-05-14 19:06:21
; Title: How to tell if a window is FullScreen or not (Win32)
; Description: Simple True or False if any window is fullscreen

Extern"Win32"
	Function EnumWindows(lpEnumProc:Byte Ptr,lParam Var) 'Customised for our purpose :-)
	Function GetSystemMetrics(nIndex)
EndExtern

Function IsFullScreen()
	Function IsTopMost(hWnd)
		Local info:WINDOWINFO = New WINDOWINFO
		GetWindowInfo(hWnd,info)
	
		If info.dwExStyle & WS_EX_TOPMOST Return True
	EndFunction
	
	Function IsFullScreenSize(hWnd,cx,cy)
		Local rect[4]
	
		GetWindowRect(hWnd,rect)
	
		Return (rect[2]-rect[0] = cx) And (rect[3]-rect[1] = cy)
	EndFunction
	
	Function IsFullScreenAndMaximized(hWnd)
		If IsTopMost(hWnd)
			Local cx = GetSystemMetrics(0) 'CM_CXSCREEN
			Local cy = GetSystemMetrics(1) 'CM_CYSCREEN
		
			If IsFullScreenSize(hWnd,cx,cy) Return True
		EndIf
	EndFunction

	Function EnumWindowProc(hWnd,lParam Var)"win32"
		If IsFullScreenAndMaximized(hWnd)
			lParam = True
			Return False
		EndIf
	
		Return True
	EndFunction
	
	Local FullScreenWindow
	EnumWindows(EnumWindowProc,FullScreenWindow)
	
	Return FullScreenWindow
EndFunction
