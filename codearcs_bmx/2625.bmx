; ID: 2625
; Author: Nilium
; Date: 2009-12-06 17:59:07
; Title: lua_objectfromtable
; Description: Function to create an object of a specified type from a Lua table

Strict

Import Brl.Reflection
Import LuGI.Core

Function lua_objectfromtable:Object(L@Ptr, index%, as:TTypeID)
	Local reset% = lua_gettop(L)
	
	If Not (lua_istable(L, index) And as) Then
		Return Null
	EndIf
	
	Assert as._class <> ArrayTypeID._class Else "Cannot use lua_objectfromtable to create an array"
	
	lua_pushvalue(L, index)
	lua_pushnil(L)
	
	Local obj:Object = as.NewObject()

	' poor man's profiling
'	Local findtime%=0
'	Local fsettime%=0
'	Local msettime%=0
	
'	Local _t%, _t2%, _t3%
	
	While lua_next(L, -2)
		If lua_isstring(L, -2) Then
'			_t=millisecs()
			Local key$ = lua_tostring(L, -2)
			Local f:TField = as.FindField(key)
			If Not f Then
				Local m:TMethod = as.FindMethod("set"+key)
				If m Then
'					_t2 = Millisecs()
					Select m.ArgTypes()[0]
						Case ByteTypeID, ShortTypeID, IntTypeID, LongTypeID
							Assert lua_isnumber(L, -1) Else "Invalid value for integer setter argument"
							m.Invoke(obj, [Object(String(lua_tointeger(L, -1)))])
						Case FloatTypeID, DoubleTypeID
							Assert lua_isnumber(L, -1) Else "Invalid value for float/double setter argument"
							m.Invoke(obj, [Object(String(lua_tonumber(L, -1)))])
						Case StringTypeID
							Assert lua_isstring(L, -1) Else "Invalid value for string setter argument"
						Default
							If lua_istable(L, -1) And f.TypeID()._class <> ArrayTypeID._class Then
								m.Invoke(obj, [lua_objectfromtable(L, -1, f.TypeID())])
							Else
								Assert lua_isbmaxobject(L, -1) Else "Invalid value for object setter argument"
								m.Invoke(obj, [lua_tobmaxobject(L, -1)])
							EndIf
					End Select
'					_t2 = Millisecs()-_t2
'					msettime :+ _t2
'					findtime :- _t2
				Else
					f = as.FindField("_"+key)
				EndIf
			EndIf
'			findtime :+ Millisecs()-_t
			
			If f Then
'				_t = Millisecs()
				Select f.TypeID()
					Case ByteTypeID, ShortTypeID, IntTypeID
						Assert lua_isnumber(L, -1) Else "Invalid value for integer field"
						f.SetInt(obj, lua_tointeger(L, -1))
					Case LongTypeID
						Assert lua_isnumber(L, -1) Else "Invalid value for long field"
						f.SetLong(obj, lua_tointeger(L, -1))
					Case FloatTypeID
						Assert lua_isnumber(L, -1) Else "Invalid value for float field"
						f.SetFloat(obj, lua_tonumber(L, -1))
					Case DoubleTypeID
						Assert lua_isnumber(L, -1) Else "Invalid value for double field"
						f.SetDouble(obj, lua_tonumber(L, -1))
					Case StringTypeID
						Assert lua_isstring(L, -1) Else "Invalid value for string field"
						f.Set(obj, lua_tostring(L, -1))
					Default
						If lua_istable(L, -1) And f.TypeID()._class <> ArrayTypeID._class Then
							f.Set(obj, lua_objectfromtable(L, -1, f.TypeID()))
						Else
							Assert lua_isbmaxobject(L, -1) Else "Invalid value for object field"
							f.Set(obj, lua_tobmaxobject(L, -1))
						EndIf
				End Select
'				fsettime :+ Millisecs()-_t
			EndIf
		EndIf
		
		lua_pop(L, 1)
	Wend
	
'	DebugLog "find time:       "+findtime+"ms"
'	DebugLog "method set time: "+msettime+"ms"
'	DebugLog "field set time:  "+fsettime+"ms"
	
	lua_settop(L, reset)
	
	Return obj
End Function
