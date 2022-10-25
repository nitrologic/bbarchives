; ID: 2579
; Author: JoshK
; Date: 2009-09-08 20:04:53
; Title: Lua Class
; Description: Lua virtual machine class

Type TLuaError
	
	Field fulltext:String
	Field text:String
	Field file:String
	Field linenumber:Int=-1
	
	Function Create:TLuaError(s:String)
		Local l:TLuaError
		Local char$,n:Int,open:Int
		Local errorstring:String
		Local sarr$[]
		Local file$
		
		l=New TLuaError
		l.fulltext=s
		l.text=s
		For n=0 To s.length-1
			char=Chr(s[n])
			Select char
			Case "'","~q"
				open=Not open
			Case ":"
				If open
					char="|"
				EndIf
			EndSelect
			errorstring:+char
		Next
		sarr=errorstring.split(":")
		If errorstring[0]<>Asc("[")
			l.file=sarr[0].Replace("~q","")
		EndIf
		If sarr.length>1
			l.linenumber=Int(sarr[1])
		EndIf
		If sarr.length>2
			l.text=sarr[sarr.length-1]
		EndIf
		Return l
	EndFunction
	
EndType


Type TLua
	
	Global LastError:TLuaError
	Global InitCallback(L:Byte Ptr)
	Global CollectGarbageFrequency:Int=5000
		
	Field L:Byte Ptr
	Field corrupt:Int
	Field file:String
	Field lastcollectgarbagetime:Int
	Field autogc:Int=1
	
	'Constructor
	Method New()
		Local callback(L:Byte Ptr)
		L=luaL_newstate()
		luaL_openlibs(L)
		InitLugi(L)
		If InitCallback InitCallback(L)
		lastcollectgarbagetime=MilliSecs()
	EndMethod
	
	'Destructor
	Method Delete()
		If L
			lua_close(L)
			L=Null
		EndIf
	EndMethod
	
	'Collect garbage
	Method CollectGarbage()
		lua_gc(L,LUA_GCCOLLECT,0)
	EndMethod
	
	'Stack methods
	Method Pop(n:Int=1)
		If n>0
			lua_pop(L,n)
		EndIf
	EndMethod
	
	Method StackSize:Int()
		Return lua_gettop(L)
	EndMethod
	
	Method ClearStack()
		Local size:Int
		size=lua_gettop(L)
		If size lua_pop(L,size)
	EndMethod
	
	Method SetStackSize(size:Int)
		Local currentsize:Int=StackSize()
		If size<currentsize
			lua_pop(L,currentsize-size)
		EndIf
	EndMethod
	
	Method PushFunction:Int(functionname:String)
		Local size:Int=StackSize()
		lua_getglobal(L,functionname)
		If lua_isfunction(L,-1)
			Return 1
		Else
			SetStackSize(size)
			Return 0
		EndIf
	EndMethod
	
	'Set global variables
	Method SetInt(name:String,i:Int)
		Local size:Int=StackSize()
		lua_pushinteger(L,i)
		lua_setglobal(L,name)
		SetStackSize(size)
	EndMethod
	
	Method SetFloat(name:String,f:Float)
		Local size:Int=StackSize()
		lua_pushnumber(L,f)
		lua_setglobal(L,name)
		SetStackSize(size)
	EndMethod
	
	Method SetString(name:String,s:String)
		Local size:Int=StackSize()
		lua_pushstring(L,s)
		lua_setglobal(L,name)
		SetStackSize(size)
	EndMethod
	
	Method SetObject(name:String,o:Object)
		Local size:Int=StackSize()
		If o
			lua_pushbmaxobject(L,o)
		Else
			lua_pushnil(L)
		EndIf
		lua_setglobal(L,name)
		SetStackSize(size)
	EndMethod
	
	'Get global variables
	Method GetInt:Int(variablename:String)
		Local result:Int
		Local size:Int=StackSize()
		lua_getglobal(L,variablename)
		result=PopInt()
		SetStackSize(size)
		Return result
	EndMethod

	Method GetFloat:Float(variablename:String)
		Local result:Float
		Local size:Int=StackSize()
		lua_getglobal(L,variablename)
		result=PopFloat()
		SetStackSize(size)
		Return result
	EndMethod

	Method GetString:String(variablename:String)
		Local result:String
		Local size:Int=StackSize()
		lua_getglobal(L,variablename)
		result=PopString()
		SetStackSize(size)
		Return result
	EndMethod

	Method GetObject:Object(variablename:String)
		Local result:Object
		Local size:Int=StackSize()
		lua_getglobal(L,variablename)
		result=PopObject()
		SetStackSize(size)
		Return result
	EndMethod
	
	'Push methods	
	Method PushInt(i:Int)
		lua_pushinteger(L,i)
	EndMethod
	
	Method PushFloat(t:Double)
		lua_pushnumber(L,t)
	EndMethod

	Method PushString(t:String)
		lua_pushstring(L,t)
	EndMethod	

	Method PushObject(o:Object)
		If o
			lua_pushbmaxobject(L,o)
		Else
			lua_pushnil(L)
		EndIf
	EndMethod
	
	'Pop methods
	Method PopInt:Int()
		Local result:Int
		If lua_isnumber(L,-1)
			result=lua_tointeger(L,-1)
			Pop()
		EndIf
		Return result
	EndMethod
	
	Method PopFloat:Float()
		Local result:Float
		If lua_isnumber(L,-1)
			result=lua_tonumber(L,-1)
			Pop()
		EndIf
		Return result
	EndMethod
	
	Method PopString:String()
		Local result:String
		If lua_isstring(L,-1)
			result=lua_tostring(L,-1)
			Pop()
		EndIf
		Return result
	EndMethod
	
	Method PopObject:Object()
		Local result:Object
		If lua_isbmaxobject
			result=lua_tobmaxobject(L,-1)
			Pop()
		EndIf
		Return result
	EndMethod
	
	Rem
	Function Preprocess:String(source:String)
		source=source.Replace("!=","~~=")
		source=source.Replace("<>","~~=")
		source=source.Replace("//","--")
		source=source.Replace("/*","--[[")
		source=source.Replace("*/","]]--")
		source=source.Replace("~q","'")
		Return source
	EndFunction
	
	Function LoadString:String(url:Object)
		Local stream:TStream=ReadStream(url)
		Local s:String
		If Not stream Return Null
		While Not stream.Eof()
			s:+Chr(stream.ReadByte())
		Wend
		stream.close()
		Return s
	EndFunction
	EndRem
		
	'Invoke methods
	Method DoString:Int(source:String)
		Local result:Int
		'source=PreProcess(source)
		result=luaL_dostring(L,source)
		If result=0
			Return True
		Else
			HandleError()
			Return False
		EndIf
	EndMethod
	
	Method Invoke:Int(in:Int=0,out:Int=0)
		Local error:String
		Local result:Int
		Local time:Int
		result=lua_pcall(L,in,out,0)
		If result
			HandleError()
			Return False
		EndIf
		If autogc
			If CollectGarbageFrequency>0
				time=MilliSecs()
				If time-lastcollectgarbagetime>CollectGarbageFrequency
					lastcollectgarbagetime=time
					CollectGarbage()
				EndIf
			EndIf
		EndIf
		Return True
	EndMethod
	
	Method HandleError()
		LastError=TLuaError.Create(lua_tostring(L,-1))
		If Not LastError.file LastError.file=file
		Print "Lua error: "+Lasterror.fulltext
		corrupt=True
	EndMethod
	
EndType
