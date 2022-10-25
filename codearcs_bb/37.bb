; ID: 37
; Author: Russell
; Date: 2001-09-02 20:38:04
; Title: GetScanCode()
; Description: Returns the current scan code

Function GetScanCode(Low,Hi)
For a = Low to Hi
If KeyDown(a) then Return a
Next
Return False
End Function

; Returns the current key, or false if none in the low/hi range are being pressed. Low/Hi allow you to only scan a certain range (ignoring the escape key, for example) 
