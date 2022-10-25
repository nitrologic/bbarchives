; ID: 1345
; Author: Eikon
; Date: 2005-04-08 15:42:20
; Title: Task Icon in Windows
; Description: How to assign a task icon to your program

Import "-lshell32"

Extern "win32"
	Function LoadIcon(hWnd, file$z, index) = "ExtractIconA@12"
	Function GetActiveWindow()
	Function GetDesktopWindow()
	Function GetWindowRect(hWnd, lpRect:Byte Ptr)
	Function SetWindowText(hWnd, lpString$z) = "SetWindowTextA@8"
	Function SetWindowPos(hWnd, after, x, y, w, h, flags)
	Function GetWindowLong(hwnd, nIndex) = "GetWindowLongA@8"
	Function SetWindowLong(hwnd, index, nIndex) = "SetWindowLongA@12"
	Function SetClassLong(hWnd, nIndex, dwNewLong) = "SetClassLongA@12"
	Function GetSystemMenu(hWnd, revert)
	Function GetMenuItemCount(hMenu)
	Function RemoveMenu(hMenu, position, flags)
End Extern

Type lpRECT
	Field l, t, r, b
End Type

Const GFX_WIDTH = 320, GFX_HEIGHT = 200

Graphics GFX_WIDTH, GFX_HEIGHT, 0, -1
Local hWnd% = GetActiveWindow() ' Get Window Handle

Local style = GetWindowLong(hWnd, -16)           ' Add system menu to window style
SetWindowLong hWnd, -16, style + $80000 + $20000 ' so that it supports a task icon

Local hMenu = GetSystemMenu(hWnd, 0)      ' Remove close from the system 
Local iMenu = GetMenuItemCount(hMenu)     ' menu to disable the non-working
RemoveMenu hMenu, iMenu - 1, $1000 + $400 ' close button
RemoveMenu hMenu, iMenu - 2, $1000 + $400

SetWindowText hWnd, "My BlitzMax App"      ' Give it a title
Local icon = LoadIcon(hWnd, "icon.ico", 0) ' Assign it an icon
SetClassLong hWnd, -14, icon

Local desk_hWnd% = GetDesktopWindow(), l:lpRect = New lpRECT
GetWindowRect desk_hWnd, l:lpRECT 

' Center it on-screen
SetWindowPos hWnd, -2, (l.r / 2) - (GFX_WIDTH / 2), (l.b / 2) - (GFX_HEIGHT / 2), 0, 0, 1
l:lpRECT = Null

Repeat; Flip; Until KeyDown(KEY_ESCAPE)
