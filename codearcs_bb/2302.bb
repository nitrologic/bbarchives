; ID: 2302
; Author: blade007
; Date: 2008-08-22 02:17:00
; Title: MouseHold()
; Description: Detects if a mouse button has been held down for a specific amount of time

Graphics 640,480,16,2
SetBuffer BackBuffer()

Dim MousePress(12)

;main loop
While Not KeyDown(1)
	If MouseHold(1,2000)
		Text 0,0,"The left mouse button has been held down for 2 seconds."
	Else
		Text 0,0,"Hold down the left mouse button"
	EndIf
	
	Flip
	Cls
	;update the mouse buttons
	MouseUpdate()
Wend

Function MouseUpdate()
	For buttons = 1 To 12
		If MouseDown(buttons)
			If MousePress(buttons) = False Then MousePress(buttons) = MilliSecs()
		Else
			MousePress(buttons) = 0
		EndIf
	Next
End Function
			
Function MouseHold(ButtonSelected,HoldDelay)
	If MouseDown(buttonselected) And (MilliSecs() - MousePress(buttonselected)) > HoldDelay And MousePress(buttonselected) <> 0
		Return True
	EndIf
End Function
