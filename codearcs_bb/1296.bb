; ID: 1296
; Author: sswift
; Date: 2005-02-19 16:35:40
; Title: RemoveTabberHottracking()
; Description: This function stops the tabber gadget in Blitzplus from highlighting the tabs in blue when you mouse over them.

; Add to User32.decls:
;
; .lib "user32.dll"
; SetWindowLong%(hWnd%,Val%,Long%):"SetWindowLongA"
; GetWindowLong%(hWnd%,Val%):"GetWindowLongA"
; GetParent%(hwnd%)

Function RemoveTabberHottrack(Gadget)

	Local hWnd% = QueryObject(Gadget, 1)
	hWnd = GetParent(hWnd)
	
	Local GWL_STYLE        = -16
	Local TCS_HOTTRACK     = $40
	Local NOT_TCS_HOTTRACK = -65  ; Blitz has no bitwise NOT, so we need to NOT TCS_HOTTRACK manually.
	
	SetWindowLong(hWnd, GWL_STYLE, GetWindowLong(hWnd, GWL_STYLE) And NOT_TCS_HOTTRACK)

End Function
