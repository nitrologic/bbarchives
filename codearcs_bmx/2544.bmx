; ID: 2544
; Author: Nilium
; Date: 2009-07-24 16:31:47
; Title: Generic Enumerator
; Description: Generic enumerator that will let you enumerate over anything, including enumerators

SuperStrict

Import brl.Reflection

'buildopt:threads
'buildopt:clean

Public

' Creates a generic enumerator and returns it
' This is pretty much the only function that matters
Function EnumForObject:TGenericEnumerator(obj:Object)
	Return New TGenericEnumerator.InitWithObject(obj)
End Function

' BMax: EnumForObject(obj)  OR  New TGenericEnumerator.Init(obj)
' Lua: Enum():Init(obj)
Type TGenericEnumerator {expose category="cower.generic.iterator" constructor="Enum"}
	Method HasNext:Int() {bool}
		Throw "Not implemented in TGenericEnumerator"
	End Method
	Method NextObject:Object()
		Throw "Not implemented in TGenericEnumerator"
	End Method
	
	Method InitWithObject:TGenericEnumerator(obj:Object) {rename="Init"}
		Assert obj Else "Object is Null"
		Local tid:TTypeID = TTypeID.ForObject(obj)
		Select tid._class
			Case ArrayTypeId._class
				Return New TArrayEnumerator.InitWithObjectAndTypeId(obj, tid)
			Case StringTypeId._class
				Return New TStringEnumerator.InitWithObject(obj)
			Default
				Local enum:TGenericEnumerator = TGenericEnumerator(obj)
				If enum Then
					Return enum
				ElseIf tid.ExtendsType(ObjectTypeId) Then
					If tid.FindMethod("HasNext") And tid.FindMethod("NextObject") Then
						Return New TObjectEnumerator.InitWithEnumeratorAndTypeId(obj, tid)
					Else
						Return New TObjectEnumerator.InitWithObjectAndTypeId(obj, tid)
					EndIf
				EndIf
		End Select
		Throw "Object is not enumerable"
	End Method
	
	Method ObjectEnumerator:TGenericEnumerator()
		Return Self
	End Method
End Type

Private

Type TObjectEnumerator Extends TGenericEnumerator
	Field _enum:Object
	Field _enumNextObject:Object(enum:Object)
	Field _enumHasNext:Int(enum:Object)
	
	Method InitWithEnumeratorAndTypeId:TObjectEnumerator(obj:Object, typeid:TTypeId)
		Assert obj Else "Enumerator is null"
		
		_enum = obj
		
		Local meth:TMethod = typeid.FindMethod("NextObject")
		Assert meth Else "Enumerator does not implement NextObject"
		
		If meth._index < 65536 Then
			_enumNextObject = Byte Ptr Ptr(typeid._class+meth._index)[0]
		Else
			_enumNextObject = Byte Ptr(meth._index)
		EndIf
		
		meth = typeid.FindMethod("HasNext")
		Assert meth Else "Enumerator does not implement HasNext"
		
		If meth._index < 65536 Then
			_enumHasNext = Byte Ptr Ptr(typeid._class+meth._index)[0]
		Else
			_enumHasNext = Byte Ptr(meth._index)
		EndIf
		
		Return Self
	End Method
	
	Method InitWithObjectAndTypeId:TObjectEnumerator(obj:Object, typeid:TTypeId)
		' Get the enumerator object
		Local objenum:TMethod = typeid.FindMethod("ObjectEnumerator")
		Assert objenum Else "Object is not enumerable"
		Local enumObject:Object = objenum.Invoke(obj, New Object[0])
		
		' Get enumerator methods
		typeid = TTypeID.ForObject(enumObject)
		
		Return InitWithEnumeratorAndTypeId(enumObject, typeid)
	End Method
	
	Method HasNext:Int()
		Return _enumHasNext(_enum)
	End Method
	
	Method NextObject:Object()
		Return _enumNextObject(_enum)
	End Method
End Type

Type TArrayEnumerator Extends TGenericEnumerator
	Field _typeid:TTypeID
	Field _object:Object
	Field _idx:Int, _length:Int
	
	Method InitWithObjectAndTypeId:TArrayEnumerator(obj:Object, typeid:TTypeId)
		_object = obj
		_typeid = typeid
		_idx = 0
		_length = _typeid.ArrayLength(_object, 0)
		Return Self
	End Method
	
	Method HasNext:Int()
		While _idx < _length And Not _typeid.GetArrayElement(_object, _idx)
			_idx :+ 1
		Wend
		Return _idx < _length
	End Method
	
	Method NextObject:Object()
		Local val:Object = _typeid.GetArrayElement(_object, _idx)
		_idx :+ 1
		Return val
	End Method
End Type

Type TStringEnumerator Extends TGenericEnumerator
	Field _string:String
	Field _idx:Int

	Method InitWithObject:TStringEnumerator(obj:Object)
		_string = String(obj)
		_idx = 0
		Return Self
	End Method

	Method HasNext:Int()
		Return _idx < _string.Length
	End Method

	Method NextObject:Object()
		Local nidx:Int = _idx + 1
		Local val:Object = _string[_idx .. nidx]
		_idx = nidx
		Return val
	End Method
End Type
