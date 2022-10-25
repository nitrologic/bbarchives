; ID: 3021
; Author: BlitzSupport
; Date: 2013-02-02 13:43:21
; Title: Run program (Windows-only)
; Description: Run program with optional maximised/minimised windows, parameters, etc

?Win32

' See http://msdn.microsoft.com/en-gb/library/windows/desktop/bb762153(v=vs.85).aspx
' for explanation of each constant...

Const SW_HIDE			= 0
Const SW_SHOWNORMAL		= 1
Const SW_SHOWMINIMIZED		= 2
Const SW_SHOWMAXIMIZED		= 3
Const SW_MAXIMIZE		= 3 ' No idea why this duplicate exists...
Const SW_SHOWNOACTIVATE		= 4
Const SW_SHOW			= 5
Const SW_MINIMIZE		= 6
Const SW_SHOWMINNOACTIVE	= 7
Const SW_SHOWNA			= 8
Const SW_RESTORE		= 9
Const SW_SHOWDEFAULT		= 10

Extern "win32"
	Function ShellExecuteA (window:Int, op:Byte Ptr, file:Byte Ptr, params:Byte Ptr, dir:Byte Ptr, show:Int)
End Extern

' RunProgram parameters: exe = path to executable file, params = valid parameters for executable, showflag = one of the above constants, dir = working directory for program

Function RunProgram:Int (exe:String, params:String = "", showflag:Int = SW_SHOWNORMAL, dir:String = "")
	If dir = "" Then dir = ExtractDir (exe)
	Print dir
	If ShellExecuteA (0, "open".ToCString (), exe.ToCString (), params.ToCString (), dir, showflag) > 32
		Return True
	Else
		Return False
	EndIf
End Function
?

' Run one instance maximised, one minimised and one normal...

RunProgram "notepad.exe", "", SW_SHOWMAXIMIZED
Delay 500

RunProgram "notepad.exe", "", SW_SHOWMINIMIZED
Delay 500

RunProgram "notepad.exe"

' You can also run programs "hidden"; if you try this, you'll have to use Task Manager to kill notepad.exe afterwards!

' RunProgram "notepad.exe", "", SW_HIDE

' NB. Notepad is normally in the default Windows path, hence no need to provide the full path in this case; however, most programs will need the full path to the executable.
