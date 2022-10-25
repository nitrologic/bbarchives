; ID: 518
; Author: Rob
; Date: 2002-12-04 09:23:42
; Title: Mouse Released, or Mouse Up
; Description: determine if user has let go of mouse button easily.

; For inner - how to do a "mouse released" or "mouse up"
; By Rob Cummings (rob@redflame net)

Global oldbutton,newbutton

While Not KeyHit(1)
	If MouseUp() Then Print "MOUSEUP!"
	UpdateMouse()
Wend
End

Function updatemouse()
	oldbutton=newbutton
	newbutton=MouseDown(1)
End Function

Function MouseUp()
	If oldbutton=1 And newbutton=0 Return 1
End Function
