; ID: 2254
; Author: Metatron
; Date: 2008-05-14 19:16:49
; Title: CreateShortcut()
; Description: Creates a Windows Explorer Shortcut file

Strict
Import "xsystem.cpp"
CreateShortcut(GetEnv("USERPROFILE")+"\Start Menu\My Program.lnk","c:\windows\notepad.exe","My Program","CTRL+ALT+SHIFT+X","notepad.exe, 0","","c:\")
End



Extern "C"
	Function xsystem(command:Byte Ptr)
EndExtern

Function CreateShortcut(linkfile$,exefile$,title$,hotkey$,icon$,exeargs$,workdir$)
	Local filename$=GetEnv("TEMP")+"\creascut.vbs"
	Local f:TStream
	f=WriteStream(filename)
	WriteLine(f,"set objWSHShell = CreateObject(~qWScript.Shell~q)")
	WriteLine(f,"set objSC = objWSHShell.CreateShortcut(~q"+linkfile+"~q)")
	WriteLine(f,"objSC.Description = ~q"+title+"~q")
	WriteLine(f,"objSC.HotKey = ~q"+hotkey+"~q")
	WriteLine(f,"objSC.IconLocation = ~q"+icon$+"~q")  ' 0 is the index
	WriteLine(f,"objSC.TargetPath = ~q"+exefile+"~q")
	WriteLine(f,"objSC.Arguments = ~q"+exeargs+"~q")
	WriteLine(f,"objSC.WindowStyle = 1")   ' 1 = normal; 3 = maximize window; 7 = minimize
	WriteLine(f,"objSC.WorkingDirectory = ~q"+workdir+"~q")
	WriteLine(f,"objSC.Save")
	CloseStream(f)
	xsystem("cscript.exe ~q"+filename+"~q")
	DeleteFile filename
EndFunction



Extern "Win32"
	Function GetEnvironmentVariable(lpName$z, lpBuffer:Byte Ptr, nSize) = "GetEnvironmentVariableA@12"
End Extern

Function GetEnv$(envVar$)
	Local buff@[64]
	Local rtn = GetEnvironmentVariable(envVar$, buff@, buff.length)
	If rtn > buff.length
		buff@ = buff@[..rtn]
		rtn = GetEnvironmentVariable(envVar$, buff@, buff.length)
	EndIf
	Return String.FromBytes(buff@, rtn)
End Function
