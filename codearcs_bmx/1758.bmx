; ID: 1758
; Author: Azathoth
; Date: 2006-07-22 10:11:14
; Title: Hook/Redirect functions
; Description: Hooking/redirecting of function calls

Extern "Win32"
	Function GetCurrentProcess:Int()
	Function WriteProcessMemory:Int(hProcess:Int,lpBaseAddress:Byte Ptr,lpBuffer:Byte Ptr,nSize:Int,lpNumberOfBytesWritten:Byte Ptr)
EndExtern

Function HookFunc:Int(func:Byte Ptr,newfunc:Byte Ptr)
	Local d:Byte[6]
	Local c=(Byte Ptr(newfunc)-Byte Ptr(func)-5)
	Local cp:Byte Ptr=Varptr c
	Local rb
	
	d[0]=$e9
	d[5]=$c3
	
	d[1]=cp[0]
	d[2]=cp[1]
	d[3]=cp[2]
	d[4]=cp[3]
	
	Return WriteProcessMemory(GetCurrentProcess(), func, d, 6, Varptr rb)
EndFunction
