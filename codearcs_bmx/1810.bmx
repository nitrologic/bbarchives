; ID: 1810
; Author: Azathoth
; Date: 2006-09-11 14:22:42
; Title: Function Hooking
; Description: Hooking function addresses

Strict

Extern "Win32"
	Function GetCurrentProcess:Int()
	Function ReadProcessMemory:Int(hProcess:Int,lpBaseAddress:Byte Ptr,lpBuffer:Byte Ptr,nSize:Int,lpNumberOfBytesRead:Byte Ptr)
	Function WriteProcessMemory:Int(hProcess:Int,lpBaseAddress:Byte Ptr,lpBuffer:Byte Ptr,nSize:Int,lpNumberOfBytesWritten:Byte Ptr)
EndExtern

Type AzHook
	Field _func:Byte Ptr
	Field _newfunc:Byte Ptr
	Field _d:Byte[6]
	Field _backup:Byte[6]
	
	Method New()
		_d[0]=$E9
		_d[5]=$C3
	EndMethod
	
	Method Hook:Int(func:Byte Ptr, newfunc:Byte Ptr)
		Local c=(Byte Ptr(newfunc)-Byte Ptr(func)-5)
		Local cp:Byte Ptr=Varptr c
		
		_func=func
		_newfunc=newfunc
		
		If _savefunc(func)
		
			_d[1]=cp[0]
			_d[2]=cp[1]
			_d[3]=cp[2]
			_d[4]=cp[3]
			Return _writefunc(func,_d)
		EndIf
		Return False
	EndMethod
	
	Method UnHook:Int()
		Return _writefunc(_func,_backup)
	EndMethod
	
	Method ReHook:Int()
		Return _writefunc(_func,_d)
	EndMethod
	
	Method _writefunc:Int(func:Byte Ptr, datatowrite:Byte Ptr)
		Local ret
		
		Return WriteProcessMemory(GetCurrentProcess(), func, datatowrite, 6, Varptr ret)
	EndMethod

	Method _savefunc:Int(func:Byte Ptr)
		Local ret
		
		Return ReadProcessMemory(GetCurrentProcess(), func, _backup, 6, Varptr ret)
	EndMethod
EndType
