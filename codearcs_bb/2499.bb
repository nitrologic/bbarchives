; ID: 2499
; Author: Ross C
; Date: 2009-06-07 04:33:24
; Title: Mouse Helper functions
; Description: Functions that return - Mouse just pressed, let go, held and clicked

;mouse modes are:
; 0 = nothing
; 1 = in
; 2 = held
; 3 = out
; 4 = click
; 5 = just this loop being held in
Const mouse_in = 1
Const mouse_held = 2
Const mouse_out = 3
Const mouse_click = 4
Const mouse_just_held = 5

;left mouse button
Global mouse_1_mode = 0
Global mouse_1_hold_time = 150 ; length of time for mouse to be considered held down
Global mouse_1_hold_timer

;right mouse button
Global mouse_2_mode = 0
Global mouse_2_hold_time = 150 ; length of time for mouse to be considered held down
Global mouse_2_hold_timer

; middle mouse button
Global mouse_3_mode = 0
Global mouse_3_hold_time = 5 ; length of time for mouse to be considered held down
Global mouse_3_hold_timer


Function process_mouse_hits()

	If mouse_1_mode = mouse_just_held Then
		mouse_1_mode = mouse_held
	End If
	If mouse_2_mode = mouse_just_held Then
		mouse_2_mode = mouse_held
	End If
	If mouse_3_mode = mouse_just_held Then
		mouse_3_mode = mouse_held
	End If

	If MouseDown(1) Then
		If mouse_1_mode = 0 Then
			mouse_1_mode = mouse_in
			mouse_1_hold_timer = MilliSecs()
		ElseIf mouse_1_mode = mouse_in Then
			If MilliSecs() > mouse_1_hold_timer+mouse_1_hold_time Then
				mouse_1_mode = mouse_just_held
			End If
		End If
	Else
		If mouse_1_mode = mouse_click Or mouse_1_mode = mouse_out Then ; check for click first, as other may set click below
			mouse_1_mode = 0
		ElseIf mouse_1_mode = mouse_in Then
			mouse_1_mode = mouse_click
		ElseIf mouse_1_mode = mouse_held Then
			mouse_1_mode = mouse_out
		End If
	End If


	If MouseDown(2) Then
		If mouse_2_mode = 0 Then
			mouse_2_mode = mouse_in
			mouse_2_hold_timer = MilliSecs()
		ElseIf mouse_2_mode = mouse_in Then
			If MilliSecs() > mouse_2_hold_timer+mouse_2_hold_time Then
				mouse_2_mode = mouse_just_held
			End If
		End If
	Else
		If mouse_2_mode = mouse_click Or mouse_2_mode = mouse_out Then ; check for click first, as other may set click below
			mouse_2_mode = 0
		ElseIf mouse_2_mode = mouse_in Then
			mouse_2_mode = mouse_click
		ElseIf mouse_2_mode = mouse_held Then
			mouse_2_mode = mouse_out
		End If
	End If

	If MouseDown(3) Then
		If mouse_3_mode = 0 Then
			mouse_3_mode = mouse_in
			mouse_3_hold_timer = MilliSecs()
		ElseIf mouse_3_mode = mouse_in Then
			If MilliSecs() > mouse_3_hold_timer+mouse_3_hold_time Then
				mouse_3_mode = mouse_just_held
			End If
		End If
	Else
		If mouse_3_mode = mouse_click Or mouse_3_mode = mouse_out Then ; check for click first, as other may set click below
			mouse_3_mode = 0
		ElseIf mouse_3_mode = mouse_in Then
			mouse_3_mode = mouse_click
		ElseIf mouse_3_mode = mouse_held Then
			mouse_3_mode = mouse_out
		End If
	End If

End Function
