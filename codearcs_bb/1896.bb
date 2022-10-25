; ID: 1896
; Author: Diego
; Date: 2007-01-06 00:53:21
; Title: Example to Lists
; Description: Some example how to work with my lists

Include "Liste.bi"

AddItem 1, "rot", $FF0000
AddItem 1, "grün", $00FF00
AddItem 1, "blau", $0000FF
AddItem 1, "violett", $800080
AddItem 1, "pink", $FF00FF
AddItem 1, "orange", $FF8000
AddItem 1, "gelb", $FFFF00
AddItem 1, "indigo",$8000FF
AddItem 1, ""
AddItem 1, "Eine"
AddItem 1, "Kuh"
AddItem 1, "macht"
AddItem 1, "muh."
AddItem 1, ""
AddItem 1, "Viele"
AddItem 1, "Kühe"
AddItem 1, "machen"
AddItem 1, "Mühe."

Graphics 160, 160, 0, 2 ; Ausgabe
SetBuffer BackBuffer()
ClsColor 0, 0, 255
Cls
;ClsColor 255, 0, 0
NormalBank% = CreateUserColorBank($FFFF00, $0000FF, $000088, $FFFF00, $8888FF, $4444FF, $000088, $000044, -1, $000080)
PushBank% = CreateUserColorBank($FFFF00, $0000FF, $000088, $FFFF00, $8888FF, $4444FF, $000088, $000044, 0, $FFFF00)

;Repeat
;	While Not MouseDown(2)
;		If KeyDown(1) Then End
;		Wend
;	ContextMenue 1, NormalBank%
;	Cls
;	Flip
;	Forever

DrawList 30, 30, 100, FontHeight() * 6 + 6, NormalBank%, 1, LOffset%, Not MouseDown(2)
Flip
While Not KeyDown(1)
	If GetKey() = 32 Then DeleteItem(1, 2)
	LOffset% = ListOffset(30, 30, 100, FontHeight() * 6 + 6, 1, LOffset%)
	If MouseYSpeed() Or Mouse% <> AltMouse% Or AltLOffset% <> LOffset% Then
		If MouseDown(1) Then ColorBank% = PushBank% Else ColorBank% = NormalBank%
		DrawList 30, 30, 100, FontHeight() * 6 + 6, ColorBank%, 1, LOffset%, Not MouseDown(2)
		Flip
		EndIf
	AltLOffset% = LOffset%
	Altmouse% = Mouse%
	Mouse% = MouseDown(1) + MouseDown(2)
	Delay 20
	Wend
End
