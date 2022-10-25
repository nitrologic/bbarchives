; ID: 1943
; Author: Andres
; Date: 2007-03-13 06:01:28
; Title: Simulate mouse click
; Description: Simulates left mouse click on a desired position

Function SimulateMouseClick(x%, y%)
	cur_x% = MouseX():cur_y% = MouseY()
	api_SetCursorPos(x%, y%)
	api_mouse_event($0201, 0, 0, 0, 0)
	api_mouse_event($0202, 0, 0, 0, 0)
	api_SetCursorPos(cur_x%, cur_y%)
End Function
