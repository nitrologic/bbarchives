; ID: 784
; Author: dan_upright
; Date: 2003-08-28 21:09:43
; Title: skinnable windows
; Description: creates irregular shaped windows

user32.decls:
----
.lib "user32.dll"

SetWindowRgn%(hWnd%, hRgn%, bRedraw%)
----

gdi32.decls:
----
.lib "gdi32.dll"

CreateRectRgn%(X1%, Y1%, X2%, Y2%)
CombineRgn%(hDestRgn%, hSrcRgn1%, hSrcRgn2%, nCombineMode%)
DeleteObject%(hObject%)
----

and the blitz function:
----
Function skin_window(window, img, r, g, b)

	w = ImageWidth(img)
	h = ImageHeight(img)
	LockBuffer ImageBuffer(img)
	mask = (255 Shl 24) + (r Shl 16) + (g Shl 8) + b

	hWnd = QueryObject(window, 1)
	hRgn = CreateRectRgn(0, 0, w, h)
	For y = 0 To h - 1
		For x = 0 To w - 1
			pixel = ReadPixelFast(x, y, ImageBuffer(img))
			If pixel = mask
				hTempRgn = CreateRectRgn(x, y, x + 1, y + 1)
				CombineRgn(hRgn, hRgn, hTempRgn, 3)
				DeleteObject(hTempRgn)
			EndIf
		Next
	Next
	UnlockBuffer ImageBuffer(img)
	
	SetWindowRgn(hWnd, hRgn, True)
	DeleteObject(hRgn)

End Function
