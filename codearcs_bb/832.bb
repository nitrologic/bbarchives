; ID: 832
; Author: Marcelo
; Date: 2003-11-21 09:07:46
; Title: Blitz Close
; Description: Allows detection of blitz window close

; Example code (need the DLL above)
AppTitle "test123"

; First parameter is the scancode to fake, the second is the window name
InstallCloseHandler(57, "test123")

While (Not KeyHit(1))
	; This if will be TRUE when the user tries to close the window, usually don't use a common key
	If KeyHit(57)  
		DebugLog("yeah")
	EndIf
Wend

UnInstallCloseHandler()
