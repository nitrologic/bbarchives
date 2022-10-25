; ID: 2939
; Author: skn3
; Date: 2012-03-22 14:05:31
; Title: Lua - add blitzmax function to lua object method
; Description: Adds a member function(method) to any lua object

Function lua_getMetaIndex(vm:Byte ptr,index:Int)
	' --- add a meta table and index to the object on the stack ---
	'get the meta table
	If lua_getmetatable(vm,index) = 0
		'add new meta table to index if there wasn't one
		lua_newtable(vm)
		If index < 0
			lua_setmetatable(vm,index-1)
		Else
			lua_setmetatable(vm,index+1)
		EndIf
		lua_getmetatable(vm,-1)
	EndIf
	
	'get __index
	lua_getfield(vm,-1,"__index")
	
	'create __index if there was none
	If lua_istable(vm,-1) = False
		lua_remove(vm,-1)
		lua_newtable(vm)
		lua_setfield(vm,-2,"__index")
		lua_getfield(vm,-1,"__index")
	EndIf
	
	'remove the meta table from stack
	lua_remove(vm,-2)
End Function

Function lua_addMethod(vm:Byte ptr,index:Int,name:String,func:Int(vm:Byte ptr))
	' --- add a method to the object ---
	'force object to have meta table and __index table
	lua_getMetaIndex(vm,index)
	
	'add teh method
	lua_pushcclosure(vm,func,0)
	lua_setfield(vm,-2,name)
		
	'remove __index from stack
	lua_remove(vm,-1)
End Function

Function lua_addMethods(vm:Byte ptr,index:Int,name:String[],func:Int(vm:Byte ptr)[])
	' --- add a method to the object ---
	'validate correct info passed in
	Assert name<>Null And func <> Null And name.Length = func.Length,"invalid methods"
	
	'force object to have meta table and __index table
	lua_getMetaIndex(vm,index)
	
	'add teh method
	For Local funcIndex:Int = 0 Until name.Length
		lua_pushcclosure(vm,func[funcIndex],0)
		lua_setfield(vm,-2,name[funcIndex])
	Next
		
	'remove __index from stack
	lua_remove(vm,-1)
End Function
