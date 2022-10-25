; ID: 2987
; Author: Xaymar
; Date: 2012-10-23 11:13:57
; Title: LockPointerToWindow
; Description: Locks the windows Cursor into a window defined by you.

;----------------------------------------------------------------
;-- Userlib
;----------------------------------------------------------------
;.lib "User32.dll"
;User32_ClientToScreen%(hwnd%, point*):"ClientToScreen"
;User32_ClipCursor%(rect*):"ClipCursor"
;User32_GetSystemMetrics%(index%):"GetSystemMetrics"
;----------------------------------------------------------------

;----------------------------------------------------------------
;-- Types
;----------------------------------------------------------------
Type Rectangle
	Field X,Y,X2,Y2
End Type

Type Point
	Field X,Y
End Type
;----------------------------------------------------------------

;----------------------------------------------------------------
;-- Global
;----------------------------------------------------------------
Global Utility_Rect.Rectangle = New Rectangle
Global Utility_Point.Point = New Point
;----------------------------------------------------------------

;----------------------------------------------------------------
;-- Functions
;----------------------------------------------------------------
Function Utility_LockPointerToWindow(hwnd=0)
	If hwnd = 0 Then
		Utility_Rect\X = 0
		Utility_Rect\Y = 0
		Utility_Rect\X2 = User32_GetSystemMetrics(78)
		Utility_Rect\Y2 = User32_GetSystemMetrics(79)
		User32_ClipCursor(Utility_Rect)
	Else
		;Grab TopLeft
		Utility_Point\X = 0
		Utility_Point\Y = 0
		User32_ClientToScreen(hwnd, Utility_Point)
		Utility_Rect\X = Utility_Point\X
		Utility_Rect\Y = Utility_Point\Y
		
		;Grab BottomRight
		Utility_Point\X = GraphicsWidth()
		Utility_Point\Y = GraphicsHeight()
		User32_ClientToScreen(hwnd, Utility_Point)
		Utility_Rect\X2 = Utility_Point\X
		Utility_Rect\Y2 = Utility_Point\Y
		
		User32_ClipCursor(Utility_Rect)
	EndIf
End Function
;----------------------------------------------------------------
