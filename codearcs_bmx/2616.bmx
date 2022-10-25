; ID: 2616
; Author: Nilium
; Date: 2009-11-29 03:53:26
; Title: Lua-Scriptable Type
; Description: Small type that allows for easily binding Lua functions to objects

SuperStrict

?Debug
Import LuGI.Generator
?
Import LuGI.Core

Include "scriptable_glue.bmx"

'LUGI_CATEGORIES="scriptable"
'GenerateGlueCode("scriptable_glue.bmx")
'End

Type Scriptable {expose disablenew category="scriptable"}
	Field _state@Ptr
	Field _closures%=-2
	
	Method Delete()
		Dispose
	End Method
	
	Method Init:Scriptable(state@Ptr) {hidden}
		_state = state
		lua_newtable(state)
		_closures = luaL_ref(state, LUA_REGISTRYINDEX)
		Return Self
	End Method
	
	Method GetScriptFunc:Int(L@Ptr, name$)
		Assert L=_state Else "Invalid Lua state for scriptable object"
		
		If _closures = -1 Then
			Return False
		EndIf
		
		lua_rawgeti(L, LUA_REGISTRYINDEX, _closures)	'-1=table
		lua_pushstring(L, name)							'-2=table,-1=name
		lua_gettable(L, -2)								'-2=table,-1=value
		
		If lua_isfunction(L, -1) Then
			lua_remove(L, -2)							'-1=value
			Return True
		Else
			lua_pop(L, 2)								'clear
			Return False
		EndIf
	End Method
	
	Method Dispose()
		If _state And _closures <> -2 Then
			If GetScriptFunc(_state, "Dispose") Then
				lua_pushbmaxobject(_state, Self)
				lua_pcall(_state, 1, 0, 0)
			EndIf
			
			luaL_unref(_state, LUA_REGISTRYINDEX, _closures)
			_closures = -2
		EndIf
	End Method
	
	Method ToString$()
		Return "Scriptable"
	End Method
End Type

'metadata isn't used since there's currently no way to use reflection to grab it for functions anyway
Function l_Scriptable_SetScript%(L@Ptr) {bindto="Scriptable" as="SetScript"}
	Local so:Scriptable
	
 	If lua_gettop(L) <> 3 Then
		lua_pushstring(L, "Invalid number of arguments to SetScript - expected 3, got "+lua_gettop(L))
		lua_error(L)
	EndIf
	
	luaL_argcheck(L, lua_isbmaxobject(L, 1) And (Not lua_isnoneornil(L, 1)), 1, "must be a subclass of the Scriptable type")
	luaL_argcheck(L, lua_isstring(L, 2), 2, "must be a string naming the routine added to the object")
	luaL_argcheck(L, lua_isfunction(L, 3) Or lua_isnil(L, 3), 3, "must be a function or nil")
	
	so = Scriptable(lua_tobmaxobject(L, 1))
	If Not so Then
		luaL_argerror(L, 1, "must be a subclass of the Scriptable type")
	EndIf
	
	If so._closures <> -2 Then
		lua_rawgeti(L, LUA_REGISTRYINDEX, so._closures)
		lua_insert(L, -3)
		lua_settable(L, -3)
	EndIf
End Function

Function l_Scriptable_GetScript%(L@Ptr)
	Local so:Scriptable
	luaL_argcheck(L, lua_isbmaxobject(L, 1) And (Not lua_isnoneornil(L, 1)), 1, "must be a subclass of the Scriptable type")
	so = Scriptable(lua_tobmaxobject(L, 1))
	If Not so Then
		luaL_argerror(L, 1, "must be a subclass of the Scriptable type")
	EndIf
	Local args:Int = lua_gettop(L)
	For Local i:Int = 2 To args
		luaL_argcheck(L, lua_isstring(L, i), i, "must be a string naming a requested routine")
		If Not so.GetScriptFunc(L, lua_tostring(L, i)) Then
			lua_pushnil(L)
		EndIf
	Next
	Return args-1
End Function
BindFunctionToType(l_Scriptable_SetScript, "SetScript", TTypeID.ForName("Scriptable"))
BindFunctionToType(l_Scriptable_GetScript, "GetScript", TTypeID.ForName("Scriptable"))
