; ID: 1733
; Author: Petron
; Date: 2006-06-14 19:31:19
; Title: Fading Text
; Description: Make Text that fades in and then fades out!

Function Fade(textstring$,x#,y#,time#,font$,fontsize#)
r = 0
g = 0
b = 0
a = LoadFont(font$,fontsize#,0,0,0)
SetFont a

;Fade in
Repeat:
r = r + 1
Delay (time#)
g = g + 1
Delay (time#)
b = b + 1
Delay (time#)
Color r,g,b
Text x#,y#,textstring$
Until r = 255 And g = 255 And b = 255

;Fade out

Repeat:
r = r - 1
Delay (time#)
g = g - 1
Delay (time#)
b = b - 1
Delay (time#)
Color r,g,b
Text x#,y#,textstring$
Until r = 0 And g = 0 And b = 0
Cls
FreeFont (a)
End Function
