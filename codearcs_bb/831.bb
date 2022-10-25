; ID: 831
; Author: Perturbatio
; Date: 2003-11-21 06:32:02
; Title: Disable window Close button
; Description: Uses windows API calls to disable the close button on the window system menu

AppTitle "testclose"
Graphics 640,480,32,2
SetBuffer BackBuffer()

Global hwndHandle%
Global hMenuHandle%
Const SC_CLOSE% = 61536
Const MF_BYCOMMAND% = 0

hwndHandle= api_FindWindow("Blitz Runtime Class", "testclose")

If hwndHandle<> 0 Then

	hMenuHandle= api_GetSystemMenu(hwndHandle, False);
	
	If hMenuHandle<> 0 Then
		api_DeleteMenu(hMenuHandle, SC_CLOSE, MF_BYCOMMAND)	
	EndIf

EndIf


While Not KeyDown(1)
	Print hwndHandle
	Print hMenuHandle
	Flip
	Cls
Wend
End
