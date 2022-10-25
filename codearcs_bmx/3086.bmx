; ID: 3086
; Author: JoshK
; Date: 2013-11-01 14:51:09
; Title: Cross-platform Dynamic Libraries
; Description: Load dynamic libraries (.dll and .so files) on all platforms

SuperStrict

Import brl.filesystem
?win32
Import pub.win32
?
?Not win32
Import "-ldl"
?

Private

Extern "c"
?win32
	Function LoadLibrary:Int(path$z)
	Function GetProcAddress:Byte Ptr(name$z)
	Function FreeLibrary(handle:Int)
?
?Not win32
	Function dlopen:Int(path$z,Mode:Int)
	Function dlsym:Byte Ptr(handle:Int,name$z)
	Function dlclose(handle:Int)
?
EndExtern

?Not win32
Const RTLD_LAZY:Int=1
?

Public

Type TLibrary
	
	Field handle:Int
	
	Method Delete()
		Free()
	EndMethod	
	
	Method Free()
		If handle
?win32
			FreeLibrary handle
?
?Not win32
			dlclose handle
?
			handle=0
		EndIf
	EndMethod
	
	Method GetFunction:Byte Ptr(name:String)
?win32
		Return GetProcAddress(handle,name)
?
?Not win32
		Return dlsym(handle,name)
?
	EndMethod
	
	Function Load:TLibrary(path:String)
		Local library:TLibrary=New TLibrary
		Select ExtractExt(path).ToLower()
		Case "dll","so"path=StripExt(path)
		EndSelect
		path=RealPath(path)
?win32	
		library.handle=LoadLibrary(path+".dll")
?
?Not win32
		library.handle=dlopen(path+".so",RTLD_LAZY)
?
		If Not library.handle Return Null
		Return library
	EndFunction
	
EndType
