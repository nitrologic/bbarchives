; ID: 3177
; Author: Jimmy
; Date: 2015-01-07 14:08:30
; Title: Customize your icon
; Description: Customize your app icon without fuss (for Blitz3D)

;
; Customize app icon
;
; Description:
; Changes your app icon everywhere inside an app with minimal effort, no dll´s and no recompiling.
; It changes the icon at the Titlebar, taskbar and shift tabbing.
; How to use:
; 1) Use any app (eg resource hacker) to edit the exe file´s icon as you see fit.
; 2) Put this code into your app
; ------------------------------------------------------------
; You need these changes, and notice the % in lParam
;
; In file user32.decls ADD:
; api_SendMessage% (hwnd%, wMsg%, wParam%, lParam%) : "SendMessageA"
; 
; In file Shell32.decls ADD:
; api_ExtractIcon% ( hWnd%, File$, Index% ) : "ExtractIconA"
; ------------------------------------------------------------

; Changes icon
hwnd = SystemProperty( "AppHWND" ) : icon=api_ExtractIcon(hwnd,"myapp.exe",0)
api_SendMessage(hwnd, $80 , 0, icon) : api_SendMessage(hwnd, $80 , 1, icon)

WaitKey
