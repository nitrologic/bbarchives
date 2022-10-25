; ID: 1934
; Author: ninjarat
; Date: 2007-02-24 10:46:01
; Title: FPS module
; Description: Import and go

Module Rat.FramesPerSecond
ModuleInfo "Description: Does simple frame rate tracking in less than 50 lines of code."
ModuleInfo "Author: Bill Whitacre (ninjarat)"
ModuleInfo "Version: 1.0"

Private

Global fpsobject:CFps=New CFps
Type CFps
	Field ms#,oms#,fps#,mspf#
	
	Method getFps()
		ms=MilliSecs()
		mspf=ms-oms
		fps=1000*(mspf^-1)
		oms=ms
	End Method
End Type

Function internalUpdate()
	fpsobject.getFps
End Function

Function internalFPS#(update)
	If update Then internalUpdate
	Return fpsobject.fps
End Function

Function internalMSPF#(update)
	If update Then internalUpdate
	Return fpsobject.mspf
End Function

Public

Function FPSUpdate()
	internalUpdate
End Function

Function FPS#(update=True)
	Return internalFPS(update)
End Function

Function MSPF#(update=True)
	Return internalMSPF(update)
End Function
