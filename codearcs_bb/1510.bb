; ID: 1510
; Author: SebHoll
; Date: 2005-10-29 06:36:19
; Title: Disable Alt+Tab Easily
; Description: :P

wndMain = CreateWindow("Hello",100,100,400,300,0,1)
hwnd = QueryObject(wndMain,1)

RegisterHotKey(hwnd,100,$1,$9)	;Alt Tab

Repeat

event = WaitEvent()

Select event

	Case $803
	UnregisterHotKey(hwnd,100)
	End
	
End Select

Forever
