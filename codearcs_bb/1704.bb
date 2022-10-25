; ID: 1704
; Author: Nicstt
; Date: 2006-05-09 09:36:42
; Title: Where is my current directory?
; Description: Ensure current directory is know, and where it should be

[CODE]
; *******************************************************************************************************************
; * * * * * * Checks to see where 'appdir' is.  Once program is installed will set directory to program dir * * * * *
; **************************************** by Nicholas Tindall 2006 *************************************************
; *******************************************************************************************************************
app$ = SystemProperty ("appdir")
DebugLog app$

If app$ = "F:\Program Files\BlitzPlus\bin\" Or app$ = "F:\Program Files\Blitz3D\bin\"
	cur_dir$ = "F:\Documents And Settings\Administrator.NSTT1\My Documents\Blitz Progs\Sticks n Stones\"
	ChangeDir cur_dir$
Else
	cur_dir$ = SystemProperty ("appdir")
	ChangeDir cur_dir$
EndIf

DebugLog "1: " + CurrentDir$()
; *******************************************************************************************************************
; *******************************************************************************************************************
[/CODE]
