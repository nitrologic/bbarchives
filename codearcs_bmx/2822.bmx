; ID: 2822
; Author: Galaxy613
; Date: 2011-02-10 09:45:03
; Title: Lua For-Each Function
; Description: Go through a TList in Lua using a temporary Lua function

''' Put somewhere after you start your LuaState
lua_register( lua_state, "ForEach", ForEach )

''' The actual function

Function ForEach:Int( lua_vm:Byte Ptr )

	Local _arg_1:TList = Null
	
	If lua_gettop(lua_vm) > 1
		_arg_1 = TList(lua_tobmaxobject(lua_vm, 1))
		
		If lua_isfunction(lua_vm,2)
			Local tmpFunc% = luaL_ref(lua_vm, LUA_REGISTRYINDEX)
				
			For Local tmp:Object = EachIn _arg_1
				'call luaFunc
				lua_rawgeti(lua_vm,LUA_REGISTRYINDEX,tmpFunc)
				lua_pushbmaxobject(lua_vm,tmp)
				If lua_pcall( lua_vm, 1, 1, 0 ) Then
						scriptError = lua_tostring( lua_vm, -1 )
						lua_pop( lua_vm, 1 )
						Notify("ForEach :: "+scriptError)
						scriptIsGood = False
				'Else
					' Get the result
					'local result% = lua_tointeger( luaState, -1 )
				EndIf
				lua_pop( lua_vm, 1 )
			Next
			
			luaL_unref(lua_vm,LUA_REGISTRYINDEX,tmpFunc)
		EndIf
	End If

	lua_pushinteger( lua_vm, 0 )

	Return 1

End Function
