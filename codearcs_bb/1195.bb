; ID: 1195
; Author: pexe
; Date: 2004-11-14 00:10:34
; Title: Windows Shutdown
; Description: Shutdown your windows

Function DLLShutdown(parm$="-s -t 0")
ExecFile SystemProperty("systemdir")+"shutdown.exe"+parm$
End Function
