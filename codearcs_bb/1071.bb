; ID: 1071
; Author: Zethrax
; Date: 2004-06-06 06:26:14
; Title: Create a maximized sized window.
; Description: Creates a window with the same size and position as a maximized window.

; -- Declare Windows API constants.
Const SM_CXSCREEN_ = 0
Const SM_CYSCREEN_ = 1
Const SM_CYCAPTION_ = 4
Const SM_CYBORDER_ = 6
;^^^^^^

; -- Declare globals.
Global window_caption_bar_height = API_GetSystemMetrics( SM_CYCAPTION_ )
Global window_border_height = API_GetSystemMetrics( SM_CYBORDER_ )
Global desktop_screen_width = API_GetSystemMetrics( SM_CXSCREEN_ )
Global desktop_screen_height = API_GetSystemMetrics( SM_CYSCREEN_ )
;^^^^^^

; Set the graphics mode.
Graphics desktop_screen_width, desktop_screen_height - ( window_caption_bar_height * 2 + window_border_height * 3 ), 0, 2

WaitKey ()
End
