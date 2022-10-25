; ID: 1580
; Author: Nilium
; Date: 2005-12-28 03:02:21
; Title: Lua State Wrapper
; Description: An object-oriented interface to Lua states

SuperStrict

Import Pub.Lua

Private

Function _lsb@ Ptr(s@ Ptr, d@ Ptr, sz:Int Ptr)
	Const _LBUF_READ:Int=1024
	Local bufP:Byte Ptr Ptr = Byte Ptr Ptr(d+4)
	Local remainderP:Int Ptr = Int Ptr(d)
	If remainderP[0] = 0 Then
		sz[0] = 0
		Return Null
	EndIf
	Local p@ Ptr = bufP[0]
	sz[0] = Min(remainderP[0],_LBUF_READ)
	remainderP[0] :- sz[0]
	bufP[0] :+ sz[0]
	Return p
End Function

Function _ls_alloc@ Ptr(ud@ Ptr, p@ Ptr, osize:Int, nsize:Int)
	If nsize = 0 Then
		MemFree(p)
		Return NULL
	ElseIf p Then
		Return MemExtend(p, osize, nsize)
	Else
		Return MemAlloc(nsize)
	EndIf
End Function

Public

Rem
bbdoc: A class for interfacing with Lua in an object-oriented manner.
EndRem
Type Lua
	Field _disposable:Int = False
	Field _state@ Ptr = Null
	
	Method Delete()
		Dispose()
	End Method

	Rem
	bbdoc:	Forces the disposal of the class and/or Lua state (depending on the settings specified
	on creation).
	EndRem
	Method Dispose:Lua()
		If _disposable = True And _state <> Null Then
			lua_close(_state)
		EndIf
		_state = Null
		Return Self
	End Method
	
	Method InitWithState:Lua(L@Ptr, canFree:Int = False)
		_state = L
		_disposable = canFree=True
		Return Self
	End Method
	
	Method Init:Lua()
		Return InitWithState(lua_newstate(_ls_alloc, Null), True)
	End Method
	
	Method IsValid:Int()
		Return (_state<>Null)
	End Method
	
	Rem
	bbdoc:	Creates a new Lua from an existing Lua state.<br/>
	<br/>
	If you pass True to @canFree, the state will be freed when the class is disposed of (either on
	garbage collection or when the @Dispose method is called).<br/>
	<br/>
	When extending this class, you should override this function so that it creates the extended
	class.	 That, or you can just do
	<pre>New YourExtendedClass.InitWithState(state, True/False)</pre>, provided you override
	InitWithState.
	EndRem
	Function FromState:Lua(s@ Ptr, canFree:Int = False)
		Return New Lua.InitWithState(s, canFree)
	End Function

	Rem
	bbdoc:	Creates a new Lua class and Lua state.	View @FromState for extra advice regarding
	implementation.
	EndRem
	Function Create:Lua()
		Return New Lua.Init()
	End Function

	Rem
	bbdoc:	Loads the lualib libraries.
	EndRem
	Method LoadLibraries:Lua()
		luaopen_base(_state)
		luaopen_package(_state)
		luaopen_string(_state)
		luaopen_math(_state)
		luaopen_table(_state)
		luaopen_io(_state)
		luaopen_os(_state)
		luaopen_debug(_state)
		Return Self
	End Method
	
	Method LoadBaseLibrary:Lua()
		luaopen_base(_state)
		Return Self
	End Method
	
	Method LoadPackageLibrary:Lua()
		luaopen_package(_state)
		Return Self
	End Method
	
	Method LoadStringLibrary:Lua()
		luaopen_string(_state)
		Return Self
	End Method
	
	Method LoadMathLibrary:Lua()
		luaopen_math(_state)
		Return Self
	End Method
	
	Method LoadTableLibrary:Lua()
		luaopen_table(_state)
		Return Self
	End Method
	
	Method LoadIOLibrary:Lua()
		luaopen_io(_state)
		Return Self
	End Method
	
	Method LoadOSLibrary:Lua()
		luaopen_os(_state)
		Return Self
	End Method
	
	Method LoadDebugLibrary:Lua()
		luaopen_debug(_state)
		Return Self
	End Method
	
	Method LoadBuffer:Lua(buf@ Ptr, length:Int, name$=Null)
		Global _lua_load:Int(L@Ptr,R@Ptr(L@Ptr,O@Ptr,S:Int Ptr),O@Ptr,N@Ptr) = Byte Ptr(lua_load)
		Local mem:Int Ptr = Int Ptr(MemAlloc(8))
		mem[0] = length
		mem[1] = Int(buf)
		
		Local nstr@ Ptr = Null
		If name Then nstr = name.ToCString()
		
		Local r:Int = _lua_load(_state, _lsb, mem, nstr)
		MemFree(mem)
		If nstr Then MemFree(nstr)
		
		Local err$
		If r <> 0 Then
			err = GetString()
			Pop 1
		EndIf
		Select r
		Case 0	' success
		Case 1
			Throw "Runtime error during lua_load:~n"+err
		Case 2
			Throw "File error during lua_load:~n"+err
		Case 3
			Throw "Syntax error during lua_load:~n"+err
		Default
			Throw "Error during lua_load:~n"+err
		End Select
		
		Return Self
	End Method
	
	Rem
	bbdoc:	Loads a string as a function and pushes it onto the stack.
	EndRem
	Method LoadString:Lua(s$, name$=Null)
		Local str@ Ptr = s.ToCString()
		Try
			LoadBuffer(str, s.Length, name)
		Catch ex:Object
			MemFree(str)
			Throw ex
		End Try
		MemFree(str)
		Return Self
	End Method

	Rem
	bbdoc:	Loads the contents of a stream or a string as a function and pushes it onto the stack.
	EndRem
	Method LoadURL:Lua(url:Object, length:Int=-1, name$=Null)
		Local s:TStream = OpenStream(url, True, False)
		If s = Null
			Throw "Could not open stream for reading"
		EndIf
		If length = -1 Then length = s.Size()
		Local buf@ Ptr = MemAlloc(length)
		s.ReadBytes(buf, length)
		s.Close(); s = Null
		Try
			LoadBuffer(buf, length, name)
		Catch ex:Object
			MemFree(buf)
			Throw ex
		End Try
		MemFree(buf)
		Return Self
	End Method
	
	Rem
	bbdoc:	Registers a callback so it's accessible to Lua scripts.
	EndRem
	Method RegisterFunction:Lua(name$, func:Int(state@ Ptr), upvalues:Int=0)
		lua_pushlstring(_state, name, name.Length)
		lua_pushcclosure(_state, func, upvalues)
		lua_rawset(_state, LUA_GLOBALSINDEX)
		Return Self
	End Method

	Rem
	bbdoc:	Pops @n elements from the stack.
	EndRem
	Method Pop:Lua(n:Int = 1)
		lua_settop(_state, -(n+1))
		Return Self
	End Method

	Rem
	bbdoc:	Gets a value from a table.<br/>
	<br/>
	To use this, you have to push the key (be it a number or string or what have you) and call this
	with the correct index of the table.  The key will be popped and the resulting value will be
	pushed onto the stack.
	EndRem
	Method GetTable:Lua(idx:Int)
		lua_gettable(_state, idx)
		Return Self
	End Method

	Rem
	bbdoc:	Gets a value from a table.<br/>
	<br/>
	To use this, you have to push the key (be it a number or string or what have you) and value,
	then call this with the correct index of the table.  The key and value will be popped from the
	stack, no cleanup is neccessary.
	EndRem
	Method SetTable:Lua(idx:Int)
		lua_settable(_state, idx)
		Return Self
	End Method
	
	Method RawGet:Lua(idx:Int)
		lua_rawget(_state, idx)
		Return Self
	End Method
	
	Method RawSet:Lua(idx:Int)
		lua_rawset(_state, idx)
		Return Self
	End Method
	
	Method RawGetWithIndex:Lua(idx:Int, n%)
		lua_rawgeti(_state, idx, n)
		Return Self
	End Method
	
	Method RawSetWithIndex:Lua(idx:Int, n%)
		lua_rawseti(_state, idx, n)
		Return Self
	End Method
	
	Method GetNext:Int(idx:Int)
		Return lua_next(_state, idx)
	End Method

	Rem
	bbdoc:	Creates a table and pushes it onto the stack.
	EndRem
	Method CreateTable:Lua(narr:Int=0, nrec:Int=0)
		lua_createtable(_state, narr, nrec)
		Return Self
	End Method
	
	Rem
	bbdoc: Creates a new userdata with the specified @size, pushes it onto the stack, and returns
	a pointer to the userdata.
	EndRem
	Method CreateUserdata@ Ptr(size:Int)
		Return lua_newuserdata(_state, size)
	End Method
	
	Rem
	bbdoc: Creates a new thread, pushes it onto the stack, and returns a new Lua state for the new
	thread.
	EndRem
	Method CreateThread:Lua()
		Local L@Ptr = lua_newthread(_state)
		Return New Lua.InitWithState(L, False)
	End Method
	
	Method PushThread:Lua(thread:Lua)
		lua_pushthread(thread._state)
		Return Self
	End Method

	Rem
	bbdoc:	Pushes a string onto the stack.
	EndRem
	Method PushString:Lua(s$)
		lua_pushlstring(_state, s, s.Length)
		Return Self
	End Method

	Rem
	bbdoc:	Pushes a boolean value onto the stack.	True or False only.
	EndRem
	Method PushBool:Lua(b:Int)
		lua_pushboolean(_state, b)
		Return Self
	End Method

	Rem
	bbdoc:	Pushes a number onto the stack.
	EndRem
	Method PushNumber:Lua(n!)
		lua_pushnumber(_state, n)
		Return Self
	End Method
	
	Rem
	bbdoc:	Pushes an integer onto the stack.
	EndRem
	Method PushInteger:Lua(n%)
		lua_pushinteger(_state, n)
		Return Self
	End Method

	Rem
	bbdoc:	Pushes a nil (Null) value onto the stack.  This is <em>not</em> the same as pushing a
	Null pointer onto the stack.
	EndRem
	Method PushNil:Lua()
		lua_pushnil(_state)
		Return Self
	End Method

	Rem
	bbdoc:	Pushes a pointer (light userdata) onto the stack.
	EndRem
	Method PushLightUserdata:Lua(p@ Ptr)
		lua_pushlightuserdata(_state, p)
		Return Self
	End Method
	
	Rem
	bbdoc:	Pushes a C closure onto the stack.
	EndRem
	Method PushCClosure:Lua(fn:Int(L@Ptr), upvalues:Int=0)
		lua_pushcclosure(_state, fn, upvalues)
		Return Self
	End Method
	
	Rem
	bbdoc:	Pushes the value at @idx onto the top of the stack.
	EndRem
	Method PushValue:Lua(idx:Int)
		lua_pushvalue(_state, idx)
		Return Self
	End Method
	
	Rem
	bbdoc:	Inserts the value at the top of the stack to the stack position @idx.
	EndRem
	Method InsertValue:Lua(idx:Int)
		lua_insert(_state, idx)
		Return Self
	End Method
	
	Method RemoveValue:Lua(idx:Int)
		lua_remove(_state, idx)
		Return Self
	End Method
	
	Method ReplaceValue:Lua(idx:Int)
		lua_replace(_state, idx)
		Return Self
	End Method

	Rem
	bbdoc:	Gets a string from @idx.
	EndRem
	Method GetString$(idx:Int = -1)
		Return String.FromCString(lua_tolstring(_state, idx, Null))
	End Method

	Rem
	bbdoc:	Gets a boolean from @idx.
	EndRem
	Method GetBool:Int(idx:Int = -1)
		Return lua_toboolean(_state, idx)
	End Method

	Rem
	bbdoc:	Gets a number from @idx.
	EndRem
	Method GetNumber!(idx:Int = -1)
		Return lua_tonumber(_state, idx)
	End Method
	
	Rem
	bbdoc:	Gets an integer from @idx.
	EndRem
	Method GetInteger:Int(idx:Int = -1)
		Return lua_tointeger(_state, idx)
	End Method
	
	Method GetFunction%(L@Ptr)(idx%)
		Return lua_tocfunction(_state, idx)
	End Method

	Rem
	bbdoc:	Gets a pointer (light userdata) from @idx.
	EndRem
	Method GetUserdata@ Ptr(idx:Int = -1)
		Return lua_touserdata(_state, idx)
	End Method

	Rem
	bbdoc:	Gets a thread from @idx.
	EndRem
	Method GetThread:Lua(idx:Int = -1)
		Return New Lua.InitWithState(lua_tothread(_state, idx), False)
	End Method
	
	Rem
	bbdoc:	Concatenates @n values at the top of the stack and pushes the result.
	EndRem
	Method Concatenate:Lua(n:Int)
		lua_concat(_state, n)
		Return Self
	End Method
	
	Rem
	bbdoc:	Performs 'left < right' where left and right are the values at the indices provided by
	@left and @right.  Returns true if the left value is smaller than the right value, otherwise
	false.
	EndRem
	Method LessThan:Int(left:Int, right:Int)
		Return lua_lessthan(_state, left, right)
	End Method
	
	Rem
	bbdoc:	Performs 'left == right' where left and right are the values at the indices provided by
	@left and @right.  Returns true if the left value is smaller than the right value, otherwise
	false.
	EndRem
	Method Equal:Int(left:Int, right:Int)
		Return lua_equal(_state, left, right)
	End Method
	
	Method RawEqual:Int(left:Int, right:Int)
		Return lua_rawequal(_state, left, right)
	End Method
	
	Method Length:Int(idx%=-1)
		Return lua_objlen(_state, idx)
	End Method
	
	Rem
	bbdoc:	Generates a Lua error whose message is the value at the top of the stack.<br/><br/>
	This function never returns.
	EndRem
	Method Error:Lua()
		lua_error(_state)
		Return Self
	End Method
	
	Rem
	bbdoc:	Generates a Lua error with the specified @message.<br/><br/>
	This function never returns.
	EndRem
	Method ErrorWithMessage:Lua(message$)
		PushString(message)
		lua_error(_state)
		Return Self
	End Method
	
	Rem
	bbdoc:	Generates a Lua error with the specified @message (if provided) if @cond is true.<br/>
	<br/>
	This function never returns.
	EndRem
	Method ErrorIf:Lua(cond:Int, message$="")
		If cond Then
			PushString(message)
			Error
		EndIf
		Return Self
	End Method

	Rem
	bbdoc:	Returns the type of value at @idx.<br/>
	<br/>
	This can be any of the following values:<br/>
	<ul>
	<li>LUA_TNONE</li>
	<li>LUA_TNIL</li>
	<li>LUA_TBOOLEAN</li>
	<li>LUA_TNUMBER</li>
	<li>LUA_TSTRING</li>
	<li>LUA_TUSERDATA</li>
	<li>LUA_TLIGHTUSERDATA</li>
	<li>LUA_TTABLE</li>
	<li>LUA_TFUNCTION</li>
	<li>LUA_TTHREAD</li>
	<li>LUA_TNONE</li>
	</ul>
	EndRem
	Method GetType:Int(idx:Int = -1)
		Return lua_type(_state, idx)
	End Method
	
	Method GetTypename$(idx:Int = -1)
		Return lua_typename(_state, idx)
	End Method

	Rem
	bbdoc:	Returns whether or not the value at @idx is of the specified type (or can be converted
	to that type).<br/><br/>
	
	ORed combinations of types work, but will not check whether or not the value at @idx can be
	converted to the specified types.
	EndRem
	Method Is:Int(idx:Int, klass:Int)
		Select klass
		Case LUA_TTABLE
			Return lua_istable(_state, idx)
		Case LUA_TSTRING
			Return lua_isstring(_state, idx)
		Case LUA_TUSERDATA
			Return lua_isuserdata(_state, idx)
		Case LUA_TLIGHTUSERDATA
			Return lua_islightuserdata(_state, idx)
		Case LUA_TNUMBER
			Return lua_isnumber(_state, idx)
		Case LUA_TNIL
			Return lua_isnil(_state, idx)
		Case LUA_TNONE
			Return lua_isnone(_state, idx)
		Case LUA_TNIL|LUA_TNONE
			Return lua_isnoneornil(_state, idx)
		Case LUA_TTHREAD
			Return lua_isthread(_state, idx)
		Case LUA_TFUNCTION
			Return lua_isfunction(_state, idx)
		Case LUA_TBOOLEAN
			Return lua_isboolean(_state, idx)
		Default
			Return (klass&lua_type(_state, idx))
		End Select
	End Method
	
	Method IsNumber:Int(idx%)
		Return lua_isnumber(_state, idx)
	End Method
	
	Method IsBoolean:Int(idx%)
		Return lua_isboolean(_state, idx)
	End Method
	
	Method IsString:Int(idx%)
		Return lua_isstring(_state, idx)
	End Method
	
	Method IsCFunction:Int(idx%)
		Return lua_iscfunction(_state, idx)
	End Method
	
	Method IsFunction:Int(idx%)
		Return lua_isfunction(_state, idx)
	End Method
	
	Method IsTable:Int(idx%)
		Return lua_istable(_state, idx)
	End Method
	
	Method IsUserdata:Int(idx%)
		Return lua_isuserdata(_state, idx)
	End Method
	
	Method IsLightUserdata:Int(idx%)
		Return lua_islightuserdata(_state, idx)
	End Method
	
	Method IsNil:Int(idx%)
		Return lua_isnil(_state, idx)
	End Method
	
	Method IsNone:Int(idx%)
		Return lua_isnone(_state, idx)
	End Method
	
	Method IsNoneOrNil:Int(idx%)
		Return lua_isnoneornil(_state, idx)
	End Method
	
	Method IsThread:Int(idx%)
		Return lua_isthread(_state, idx)
	End Method

	Rem
	bbdoc:	Returns the lua_State pointer.
	Endrem
	Method State@ Ptr()
		Return _state
	End Method
	
	Method Call:Lua(args:Int=0, results:Int=0, protected:Int=True)
		If Not protected Then
			lua_call(_state, args, results)
			Return Self
		EndIf
		
		Local r:Int = lua_pcall(_state, args, results, 0)
		
		Local err$
		If r <> 0 Then
			err = GetString()
			Pop 1
		EndIf
		Select r
		Case 0
		Case 1
			Throw "Runtime error encountered during lua_pcall:~n"+err
		Case 4
			Throw "Memory allocation error encountered during lua_pcall:~n"+err
		Case 5
			Throw "Error calling the error handler function during lua_pcall:~n"+err
		Default
			Throw "Error during lua_pcall:~n"+err
		End Select
		
		Return Self
	End Method
	
	Method GetField:Lua(idx:Int, name$)
		lua_getfield(_state, idx, name)
		Return Self
	End Method
	
	Method SetField:Lua(idx:Int, name$)
		lua_setfield(_state, idx, name)
		Return Self
	End Method
	
	Method GetGlobal:Lua(name$)
		lua_getfield(_state, LUA_GLOBALSINDEX, name)
		Return Self
	End Method
	
	Method SetGlobal:Lua(name$)
		lua_setfield(_state, LUA_GLOBALSINDEX, name)
		Return Self
	End Method
	
	Method GetEnvironment:Lua(idx:Int)
		lua_getfenv(_state, idx)
		Return Self
	End Method
	
	Method SetEnvironment:Lua(idx:Int)
		lua_setfenv(_state, idx)
		Return Self
	End Method
	
	Method GetMetatable:Lua(idx:Int)
		lua_getmetatable(_state, idx)
		Return Self
	End Method
	
	Method SetMetatable:Lua(idx:Int)
		lua_setmetatable(_state, idx)
		Return Self
	End Method
	
	Method Resume:Int(nargs:Int)
		Return lua_resume(_state, nargs)
	End Method
	
	Method Yield:Int(results:Int)
		Return lua_yield(_state, results)
	End Method
	
	Method Status:Int()
		Return lua_status(_state)
	End Method
	
	Method ExchangeValues:Lua(with:Lua, n:Int)
		lua_xmove(_state, with._state, n)
		Return Self
	End Method
	
	Rem
	bbdoc:	Returns the size of the stack.
	EndRem
	Method GetTop:Int()
		Return lua_gettop(_State)
	End Method
	
	Method SetTop:Lua(top:Int)
		lua_settop(_state, top)
		Return Self
	End Method
	
	Method CheckStack:Int(extra:Int)
		Return lua_checkstack(_state, extra)
	End Method
End Type
