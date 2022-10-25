; ID: 1214
; Author: semar
; Date: 2004-12-01 06:20:08
; Title: Use FindWindow Without Class Name
; Description: Find your window without to know the class it belongs to

;FindWindow without class name demo
;by Sergio - semar

title$ = "myblitzapp"
AppTitle title
Print "handle of " + title + " = " + findwindow(title)
WaitKey
End

;===============================
Function findwindow(name$)
;===============================
;api_FindWindow% (lpClassName$, lpWindowName$)
;api_FindWindow_0% (lpClassName%, lpWindowName$)
;
;method 1:
;passing a null value

method = 1 ;change to method 2 but check the class name first !

If method = 1 Then
hwnd% =  api_FindWindow_0% (0,name)
Else

;method 2:
;passing a string value = class name
;the class name of a b3d application is: "Blitz Runtime Class"

class$ = "Blitz Runtime Class"
hwnd% =  api_FindWindow% (class,name)

EndIf

Return hwnd

End Function
