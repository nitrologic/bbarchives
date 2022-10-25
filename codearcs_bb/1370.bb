; ID: 1370
; Author: KuRiX
; Date: 2005-05-11 14:30:21
; Title: Real Maximize of B3D Window
; Description: Code to Maximize a Window

hwnd=Api_GetActiveWindow()
iStyle=Api_GetWindowLong(hwnd, GWL_STYLE)
Api_SetWindowLong(hwnd, GWL_STYLE, iStyle Or $10000)
Api_ShowWindow(hwnd,3)
