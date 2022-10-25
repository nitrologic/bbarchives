; ID: 1276
; Author: fredborg
; Date: 2005-02-01 11:09:34
; Title: Application Directory
; Description: Avoid confusion, and use this to set the real path to your game!

; Path to executable
Global apppath$ = SystemProperty$("appdir")
If Lower$(Right$(apppath$,4))="bin\"
	; Running from the IDE
	apppath$ = CurrentDir$()
End If
If Right$(apppath,1)<>"\" Then apppath = apppath+"\"
ChangeDir(apppath)
