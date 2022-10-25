; ID: 1464
; Author: Lomat
; Date: 2005-09-17 14:55:23
; Title: Create Process (windows)
; Description: Process spawning class for windows

Extern "Win32"
	Function WaitForSingleObject:Int(hHandle:Int, dwMilliseconds:Int)
	Function GetExitCodeProcess:Int(hProcess:Int, lpExitCode:Int)
	Function CreateProcessA:Int(lpApplicationName:Byte Ptr, lpCommandLine:Byte Ptr, lpProcessAttributes:Int, lpThreadAttributes:Int, bInheritHandles:Int, dwCreationFlags:Int, lpEnvironment:Int, lpCurrentDirectory:Byte Ptr, lpStartupInfo:ProcessStartUpInfo, lpProcessInformation:ProcessInformation)
	Function CloseHandle:Int(hProcess:Int)
	Function GetActiveWindow:Int()
	Function SetForegroundWindow(hWnd:Int)
	Function SetActiveWindow(hWnd:Int)
	Function OpenProcess:Int(dwDesiredAccess:Int, bInheritHandle:Int, dwProcessId:Int)
	Function keybd_event(bVk:Byte, bScan:Byte, dwFlags:Int, dwExtraInfo:Int)
End Extern

Type ProcessStartUpInfo
   Field cb:Int
   Field lpReserved:String
   Field lpDesktop:String
   Field lpTitle:String
   Field dwX:Int
   Field dwY:Int
   Field dwXSize:Int
   Field dwYSize:Int
   Field dwXCountChars:Int
   Field dwYCountChars:Int
   Field dwFillAttribute:Int
   Field dwFlags:Int
   Field wShowWindow:Int
   Field cbReserved2:Int
   Field lpReserved2:Int
   Field hStdInput:Int
   Field hStdOutput:Int
   Field hStdError:Int
End Type

Type ProcessInformation
   Field hProcess:Int
   Field hThread:Int
   Field dwProcessID:Int
   Field dwThreadID:Int
End Type

Type Process

	Field exe:String
	Field args:String
	Field proc:ProcessInformation
	Field returnValue:Int
	Field pHandle:Int
	Field myhWnd:Int
	
	Method New()
		myHwnd = GetActiveWindow()
	End Method
	
	Method setExe (appPath:String)
		exe = appPath
	End Method

	Method setArgs (argString:String)
		args = argString
	End Method
	
	Method getReturnValue:Int ()
		Return returnValue
	End Method
	
	Method create()
		' change to exe directory
		ChangeDir(ExtractDir(exe))
		' spawn process...
		Local cmd:String  = "~q" + exe + "~q" + " " + "~q" + args + "~q"
		Print "Run process: " + cmd
		Local start:ProcessStartUpInfo = New ProcessStartUpInfo
		proc = New ProcessInformation
		CreateProcessA(Null, cmd.ToCString(), 0, 0, 1, 0, 0, Null, start, proc)
		pHandle     = OpenProcess($100000, -1, proc.hProcess)
		returnValue = Null
	End Method
	
	Method isRunning:Int()
		Local v:Int = WaitForSingleObject(pHandle, 0)
		Select v
			Case 0
				returnValue = GetExitCodeProcess(pHandle, v)
				CloseHandle(pHandle)
				Print "Process finished with return value: " + returnValue
				' change back to this app dir.
				ChangeDir(LaunchDir$)
				' Set focus back to this application.
				SetForegroundWindow(myHWnd)
				SetActiveWindow(myHWnd)
				
				' Since were a full screen app Windows does not like
				' makign you go back into full screen mode so we have
				' to emulate the user pressing Alt + Enter :)
				
				' Add a little delay
				Delay(100)
				' send alt + enter to get back full screen window :)
				keybd_event(KEY_ALT, 0, 0, 0)
				keybd_event(KEY_RETURN, 0, 0, 0)
				' we have to let go of the keys :)
				keybd_event(KEY_RETURN, 0, 2, 0)
				keybd_event(KEY_ALT, 0, 2, 0)
				' app should now be back in focus and on the screen.
				Return 0
			Case -1
				' We normally get this if the process failed to launch.
				Print "WaitForSingleObject failed."
				CloseHandle(pHandle)
				Return 0
			Default
				' Process is running.
				Return 1				
		End Select
	End Method
	
End Type
