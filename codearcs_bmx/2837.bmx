; ID: 2837
; Author: Mainsworthy
; Date: 2011-04-03 14:50:00
; Title: mouse buttons onscreen
; Description: press a screen button with mouse

If MouseDown(1) And MouseX() > 957 And MouseX() < 957+45 And MouseY() > 705 And MouseY() < 735
#qwbnms
If MouseDown(1) Then Goto qwbnms
'execute mymousemovedfunction()
EndIf

' if mouse is pressed within screen button region do function, BUT wait until mouse released!
