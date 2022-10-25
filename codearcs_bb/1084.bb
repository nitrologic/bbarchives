; ID: 1084
; Author: Sphinx
; Date: 2004-06-13 05:36:00
; Title: B+ window has a Minimize gadget
; Description: Show a minimize gadget to the Blitz Plus window

Const GWL_STYLE = -16		; Retrieve the window styles of the window
Const WS_SYSMENU = 13107200 	; &H80000 = The window has a system menu on its title bar
Const WS_MINIMIZEBOX = 131072	; &H20000 = The window has a minimize button (The WS_SYSMENU window style must also be specified)


Graphics 640,480,16,2

	hWND=FindWindow("","blitzcc") 				;Get the handle of the BlitzCC window (You should change that to the AppTitle you specified if you did)
	WLong = GetWindowLong(hWND, GWL_STYLE) 			;Now get the attribute/style of this window
	WLong = WLong Or WS_SYSMENU Or WS_MINIMIZEBOX		;(Or-ing to add attribute/style if it is not applied)
	SetWindowLong (hWND, GWL_STYLE, WLong)			;Change the attribute/style of the BlitzCC window to show the minimize button
	SetWindowText (hWnd,"My B+ window can be minimized :)") ;Now change the title to whatever you like
	ShowWindow(hWND,0) : ShowWindow(hWND,1) 		;make sure the changes will take effect

While Not KeyHit(1)
Wend
