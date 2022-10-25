; ID: 2262
; Author: Otus
; Date: 2008-06-07 17:02:10
; Title: Object Loader
; Description: Read/write any object to a stream

' Loads and saves any objects to a stream using the reflection module.
' Recursive - any and all objects referenced are also saved!
' Use the metadata key "no_save" to mark fields not to be saved.

SuperStrict


Import BRL.Reflection


'Saves an object to the stream specified
Function SaveObject(o:Object, s:TStream, tid:TTypeId = Null) 
	
	'Null?
	If o
		s.WriteByte True
	Else
		s.WriteByte False
		Return
	End If
	
	'Get type Id
	If tid = ObjectTypeId Then tid = TTypeId.ForObject(o)
	If Not tid Then tid = TTypeId.ForObject(o)
	
	'Save type name
	s.WriteInt tid.Name().length
	s.WriteString tid.Name() 
	
	'DebugLog "Saving "+tid.Name()
	
	'Primitive type?
	Select tid
	'Integers
	Case ByteTypeId
		s.WriteByte	Byte(String(o) )
		Return
	Case ShortTypeId
		s.WriteShort	Short(String(o) )
		Return
	Case IntTypeId
		s.WriteInt	Int(String(o) )
		Return
	Case LongTypeId
		s.WriteLong	Long(String(o) )
		Return
	'Floating Points
	Case FloatTypeId
		s.WriteFloat	Float(String(o) )
		Return
	Case DoubleTypeId
		s.WriteDouble	Double(String(o) )
		Return
	'Strings
	Case StringTypeId
		s.WriteInt	String(o).length
		s.WriteString	String(o)
		Return
	End Select
	
	'Array?
	If tid.Name().EndsWith("[]")
		'Save length
		s.WriteInt tid.ArrayLength(o)
		
		'Element type
		Local etid:TTypeId = TTypeId.ForName(tid.Name()[..tid.Name().length - 2]) 
		
		'Save elements
		For Local i:Int = 0 To tid.ArrayLength(o)-1
			SaveObject tid.GetArrayElement(o, i), s, etid
		Next
		Return
	End If
	
	'Map?
	If tid.Name()="TMap"
		Local m:TMap = TMap(o)
		
		'Save length
		Local l:Int = 0
		For Local key:Object = EachIn m.Keys()
			l:+1
		Next
		s.WriteInt l
		
		'Save key-value pairs
		For Local node:TNode = EachIn m
			SaveObject node.Key(), s
			SaveObject node.Value(), s
		Next
		Return
	End If
	
	'List?
	If tid.Name()="TList"
		Local l:TList = TList(o)
		
		'Save length
		s.WriteInt l.Count()
		
		'Save contents
		For Local obj:Object = EachIn l
			SaveObject obj, s
		Next
		Return
	End If
	
	'Save fields
	For Local f:TField = EachIn tid.EnumFields()
		If f.MetaData("no_save") Then Continue
		SaveObject f.Get(o), s, f.TypeId()
	Next
	
End Function

'Reads an object to the stream specified
Function LoadObject:Object(s:TStream) 
	
	'Null?
	Select s.ReadByte() 
	Case False
		Return Null
	Case True
	Default
		RuntimeError "Not an object"
	End Select
	
	'Get type Id
	Local tid:TTypeId = TTypeId.ForName(s.ReadString(s.ReadInt() ) )
	
	'DebugLog "Loading "+tid.Name()
	
	'Primitive type?
	Select tid
	'Integers
	Case ByteTypeId
		Return String.FromInt(s.ReadByte())
	Case ShortTypeId
		Return String.FromInt(s.ReadShort())
	Case IntTypeId
		Return String.FromInt(s.ReadInt())
	Case LongTypeId
		Return String.FromLong(s.ReadLong() ) 
	'Floating point numbers
	Case FloatTypeId
		Return String.FromFloat(s.ReadFloat())
	Case DoubleTypeId
		Return String.FromDouble(s.ReadDouble() ) 
	'Strings
	Case StringTypeId
		Return s.ReadString(s.ReadInt())
	End Select
	
	Local o:Object
	
	'Array?
	If tid.ElementType() 
		'Get length
		Local l:Int = s.ReadInt() 
		
		'Create array
		o = tid.NewArray(l)
		
		'Load elements
		For Local i:Int = 0 To l - 1
			Local obj:Object = LoadObject(s)
			If obj Then tid.SetArrayElement o, i, obj
		Next
		Return o
	End If
	
	'Map?
	If tid.Name()="TMap"
		Local m:TMap = New TMap
		
		'Get length
		Local l:Int = s.ReadInt()
		
		'Load key-value pairs
		For Local i:Int = 0 To l - 1
			Local key:Object = LoadObject(s)
			Local value:Object = LoadObject(s)
			m.Insert key, value
		Next
		Return m
	End If
	
	'List?
	If tid.Name()="TList"
		Local l:TList = New TList
		
		'Get length
		Local length:Int = s.ReadInt()
		
		'Load key-value pairs
		For Local i:Int = 0 To length - 1
			l.AddLast LoadObject(s)
		Next
		Return l
	End If
	
	'Create the object
	o = tid.NewObject()
	
	'Load fields
	For Local f:TField = EachIn tid.EnumFields() 
		If f.MetaData("no_save") Then Continue
		Local obj:Object = LoadObject(s) 
		If obj Then f.Set o, obj
	Next
	
	Return o
	
End Function
