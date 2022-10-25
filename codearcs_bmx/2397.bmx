; ID: 2397
; Author: Galaxy613
; Date: 2009-01-23 05:22:56
; Title: Lua Hooks
; Description: A simple way of hooking Lua functions with simple string hooks.

Rem

	Lua_hooks.bmx		Rev 1.0
	Written by 'Galaxy613' on 1/23/2009

	
	For use in Lua Scripts to Hook Lua Functions into Abstract Hooks which can be called
	anytime within the BlitzMax code.
	
End Rem

'#Region Register Lua Functions
lua_register(LuaState, "AddHook", AddHook_luahook)
lua_register(LuaState, "UnHook", UnHook_luahook)

lua_register(LuaState, "CallHook", CallHook_luahook)

lua_register(LuaState, "RegisterHook", RegisterHookName_luahook)
lua_register(LuaState, "UnregisterHook", UnregisterHookName_luahook)
'#End Region

Type LuaHook
	Global LuaHookList:TList = CreateList()
	Global LuaHookNameList:TList = CreateList()
	
	Field HookName:String, LuaFuncName:String
	
	Method SetNames(Hnme:String, LFnme:String)
		HookName = Hnme
		LuaFuncName = LFnme
	End Method
	
	Function CheckAll()
		For hookname:String = EachIn LuaHookNameList
			For hook:LuaHook = EachIn LuaHookList
				If hook.HookName = HookName Then
					lua_getfield(LuaState, LUA_GLOBALSINDEX, hook.LuaFuncName)
					lua_call(LuaState, 0, 0)
				EndIf
			Next
		Next
	End Function
	
	Function Check(HookName:String)
		For hook:LuaHook = EachIn LuaHookList
			If hook.HookName = HookName Then
				lua_getfield(LuaState, LUA_GLOBALSINDEX, hook.LuaFuncName)
				lua_call(LuaState, 0, 0)
			EndIf
		Next
	End Function
	
	Function RegisterHookName(HookName:String)
		ListAddLast LuaHookNameList, HookName
	End Function
	
	Function UnregisterHookName(HookName:String)
		For hooknme:String = EachIn LuaHookNameList
			If hooknme = HookName Then
				ListRemove LuaHookNameList, hooknme
			EndIf
		Next
	End Function
	
	Function Register(LuaFuncName:String, HookName:String)
		Local tmp:LuaHook = New LuaHook
		tmp.HookName = HookName
		tmp.LuaFuncName = LuaFuncName
		ListAddLast LuaHookList, tmp
		'ListAddLast LuaHookList, (New LuaHook).SetNames(HookName,LuaFuncName) ' This isn't working..?
	End Function
	
	Function Unregister(LuaFuncName:String, HookName:String)
		For hook:LuaHook = EachIn LuaHookList
			If hook.HookName = HookName And hook.LuaFuncName = LuaFuncName Then
				ListRemove LuaHookList, hook
			EndIf
		Next
	End Function
End Type

'#Region Lua Functions
Function RegisterHookName_luahook(LuaState:Byte Ptr)
	Local HookName:String = luaL_checkstring(LuaState, 1)
	
	LuaHook.RegisterHookName(HookName)
	
	Return 0
End Function

Function UnregisterHookName_luahook(LuaState:Byte Ptr)
	Local HookName:String = luaL_checkstring(LuaState, 1)
	
	LuaHook.UnregisterHookName(HookName)
	
	Return 0
End Function

Function CallHook_luahook(LuaState:Byte Ptr)
	Local HookName:String = luaL_checkstring(LuaState, 1)
	
	LuaHook.Check(HookName)
	
	Return 0
End Function

Function AddHook_luahook(LuaState:Byte Ptr)
	Local HookName:String = luaL_checkstring(LuaState, 1)
	Local HookFunc:String = luaL_checkstring(LuaState, 2)
	
	LuaHook.Register(HookFunc, HookName)
	
	Return 0
End Function

Function UnHook_luahook(LuaState:Byte Ptr)
	Local HookName:String = luaL_checkstring(LuaState, 1)
	Local HookFunc:String = luaL_checkstring(LuaState, 2)
	
	LuaHook.Unregister(HookFunc, HookName)
	
	Return 0
End Function
'#End Region
