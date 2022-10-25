; ID: 482
; Author: BlitzSupport
; Date: 2002-11-09 11:14:20
; Title: DebugText
; Description: Special debug version of text, mostly for 3D debugging

Type DebugItem
	Field x, y, r, g, b, message$
End Type

Function DebugText (x, y, message$ = "", r = -1, g = -1, b = -1)
	d.DebugItem = New DebugItem
	d\x = x
	d\y = y
	d\message = message$
	d\r = r
	d\g = g
	d\b = b
End Function

Function Debug3D ()
	For d.DebugItem = Each DebugItem
		If (d\r > -1) And (d\g > -1) And (d\b > -1)
			Color d\r, d\g, d\b
		EndIf
		Text d\x, d\y, d\message
		Delete d
	Next	
End Function
