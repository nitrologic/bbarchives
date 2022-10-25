; ID: 1190
; Author: Eikon
; Date: 2004-11-10 23:02:25
; Title: Custom Input boxes
; Description: bare bones GUI example

AppTitle "Rpg Input Example by Eikon"
Graphics 640, 320, 16, 2
SetBuffer BackBuffer()

Font = LoadFont("Lucida Console", 16, 0, 0) ; Inputbox Font
SetFont Font

Local name$, class$, gender$
Global itotal% = 3  ; 3 Inputboxes
Global aInbox% = 0, inTime%, inB% = 0 ; Active Inputbox and blink timer

Dim InputBox$(itotal) ; Strings to hold input

ClsColor 212, 208, 200
While Not KeyDown(1)

DrawInputBoxes 50, 50

Delay 5
Flip: Cls
Wend: End

Function DrawInputBoxes(x, y)
MX = MouseX(): MY = MouseY(): MH = MouseHit(1)
inkey% = GetKey() ; Grab Input

If inkey > 31 And inkey < 128 Then ; Filter out unwanted keys
	; Add input to string
	If Len(InputBox$(aInbox)) < 9 Then InputBox$(aInbox) = InputBox$(aInbox) + Chr$(inkey)
EndIf

If inkey = 8 Or inkey = 4 ; Delete
	If Len(InputBox$(aInbox)) > 0 Then InputBox$(aInbox) = Left$(InputBox$(aInbox), Len(InputBox$(aInbox)) - 1)
EndIf

i = 0
For b = y To y + ((itotal - 1) * 64) Step 64
	Color 128, 128, 128: Rect x, b, 100, 28, 0     ; Draw Inputbox
	Color 64, 64, 64: Rect x + 1, b + 1, 98, 26, 0
	Color 212, 208, 200: Rect x + 1, b + 26, 98, 1: Rect x + 98, b + 1, 1, 26
	Color 255, 255, 255: Rect x, b + 27, 100, 1: Rect x + 99, b, 1, 27: Rect x + 2, b + 2, 96, 24
	If i = 0 Then Color 0, 128, 0 ElseIf i = 1 Then Color 0, 0, 128 Else Color 0, 0, 0
	Text x + 4, b + 8, InputBox$(i)
	
	Color 0, 0, 0: 
	If aInbox% = i ; Draw Cursor in active inbox
		If inB = True Then Color 0, 0, 0: Rect x + 4 + (Len(InputBox$(aInbox)) * 10), b + 4, 2, 20, 1
		If MilliSecs() >= inTime + 500 Then ; Blink
			If inB = 0 Then inB = 1 Else inB = 0
			inTime = MilliSecs()
		EndIf
	EndIf

	; Mouse click Detection to change active inputbox
	If MH = 1 And RectsOverlap(MX, MY, 1, 1, x, b, 100, 28) Then aInbox = i

	Select i ; Draw Captions
		Case 0: Text x, b - 20, "Name:"   ; Name
		Case 1: Text x, b - 20, "Class:"  ; Class
		Case 2: Text x, b - 20, "Gender:" ; Gender

	End Select

	i = i + 1
Next

If KeyHit(15) Then aInbox = aInbox + 1 ; Tab support
If aInbox >= 3 Then aInbox = 0

End Function
