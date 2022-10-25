; ID: 2506
; Author: JoshK
; Date: 2009-06-13 14:01:02
; Title: Simple Lua example
; Description: Demonstrates procedural Lua functions.

SuperStrict

Local source:String
source="n=myfunc(1,2)~n"
source:+"myfunc(n,4)~n"
'source:+"nonexistentfunction()~n"'uncomment this for error

Local L:Byte Ptr
L=luaL_newstate()
luaL_openlibs(L)

lua_register(L, "myfunc", myfunc)
If luaL_dostring(L,source)
	Print "Error: "+lua_tostring(L,-1)
Else
	Print "Everything is fine."
EndIf
lua_close(L)

Function myfunc:Int(L:Byte Ptr)
	Local x:Int = luaL_checkinteger(L, 1)
	Local y:Int = luaL_checkinteger(L, 2)
	Print x+","+y
	lua_pushnumber(L,3)
	Return 1
EndFunction
