; ID: 1682
; Author: mindstorms
; Date: 2006-04-21 19:44:04
; Title: full screen
; Description: doesn't use full-screen mode

;needed in libs
;---------------------------------------------------------------------------------
; User32.decls
;==============
;
;.lib "user32.dll"
;
;api_FindWindow%( class$,Text$ ):"FindWindow"
;api_GetWindowLong%(hwnd%, nIndex%) : "GetWindowLong"
;api_GetSystemMetrics%(nIndex%) : "GetSystemMetrics"
;api_MoveWindow%(hwnd%, x%, y%, nWidth%, nHeight%, bRepaint%) : "MoveWindow"
;api_SetWindowLong%(hwnd%, nIndex%, dwNewLong%) : "SetWindowLong"
;api_ShowWindow%(hwnd%, nCmdShow%) : "ShowWindow"
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Const SM_CXSCREEN		= 0
Const SM_CYSCREEN		= 1
Const WS_VISIBLE		= $10000000
Const GWL_STYLE			= -16
Const title$ = "anything you want(must be set)(can't see it)"

Global width = api_GetSystemMetrics(SM_CXSCREEN) 
Global height = api_GetSystemMetrics(SM_CYSCREEN) 
Graphics width,height,0,2

AppTitle title$

blitz_hnd = api_FindWindow("Blitz Runtime Class", title$)



api_SetWindowLong(blitz_hnd, GWL_STYLE, WS_VISIBLE)


api_MoveWindow(blitz_hnd, (api_GetSystemMetrics(SM_CXSCREEN) - width) / 2, (api_GetSystemMetrics(SM_CYSCREEN) - height) / 2, width, height, 1)
