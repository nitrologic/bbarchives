; ID: 897
; Author: pantsonhead.com
; Date: 2004-01-27 12:01:58
; Title: Minimize/Maximize Buttons
; Description: Add Minimize / Maximize Button To A Non-Resizable Window

Global winMain = CreateWindow("Main Window",100,100,200,200,0,1)

; Here's the magic bit :)
hWND=QueryObject(winMain,1)
x = GetWindowLong(hWnd, -16)
x = x Or $20000   ;WS_MINIMIZEBOX - add Minimize button
;x = x Or $10000   ;WS_MAXIMIZEBOX - add Maximize button
SetWindowLong (hWnd, -16, x)

;Refresh WindowMenu to see new buttons
UpdateWindowMenu winMain

Repeat
	If WaitEvent(1) =$803 Then
		End
	EndIf
Forever
