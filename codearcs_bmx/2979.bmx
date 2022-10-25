; ID: 2979
; Author: Alberto-Diablo
; Date: 2012-09-25 18:10:04
; Title: Object streams
; Description: The useful interface for (de)serialize bmax objects

Rem

 (с) 2012 Альберт Гаскаров. Все права защищены.
 
 Лицензионное соглашение дает Вам право распространять файлы с любыми Вашими 
 приложениями, которые в свою очередь обязаны распространяться со следующей 
 суб-лицензией: "Все файлы включенный в данное приложение является 
 интеллектуальной собственностью автора и не может распространятся отдельно 
 от данного приложения без личного разрешения автора." Также ЗАПРЕЩЕНО 
 распространять все остальные файлы, включенные в данный архив (дистрибутив)
 без личного разрешения автора.
 
 ВНИМАНИЕ! ПОЖАЛУЙСТА ПРОЧТИТЕ ВНИМАТЕЛЬНО:КОПИРУЯ ИЛИ ИСПОЛЬЗУЯ ЛЮБЫМ ИНЫМ 
 ОБРАЗОМ ПРОГРАММНЫЙ ПРОДУКТ, ПОСТАВЛЯЕМЫЙ С ДАННЫМ ЛИЦЕНЗИОННЫМ СОГЛАШЕНИЕМ, 
 ВЫ (КАК ЮРИДИЧЕСКОЕ ИЛИ ФИЗИЧЕСКОЕ ЛИЦО), СОГЛАШАЕТЕСЬ СО ВСЕМИ УСЛОВИЯМИ 
 НАСТОЯЩЕГО ЛИЦЕНЗИОННОГО СОГЛАШЕНИЯ (ДАЛЕЕ ПО ТЕКСТУ "СОГЛАШЕНИЕ"), 
 ПРИВЕДЕННЫМИ НИЖЕ, ОТНОСИТЕЛЬНО ИСПОЛЬЗОВАНИЯ ПРОГРАММНОГО ПРОДУКТА 
 (ДАЛЕЕ ПО ТЕКСТУ "ПО"). ЕСЛИ ВЫ НЕ СОГЛАСНЫ ПРИНЯТЬ НА СЕБЯ УСЛОВИЯ СОГЛАШЕНИЯ,
 ВЫ НЕ ИМЕЕТЕ ПРАВА ИСПОЛЬЗОВАТЬ ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ В КАКИХ ЛИБО 
 ЦЕЛЯХ И ОБЯЗАНЫ УДАЛИТЬ ВСЕ КОПИИ ПРОГРАММНОГО ПРОДУКТА С КОМПЬЮТЕРОВ И 
 НОСИТЕЛЕЙ, ПРИНАДЛЕЖАЩИХ ВАМ.
 
EndRem

SuperStrict

Rem
bbdoc: Данные
End Rem
Module data.TData

ModuleInfo "Версия: 1.21"
ModuleInfo "Автор : Альберт Гаскаров"
ModuleInfo "Сервер: API"

ModuleInfo "История: 1.21"
ModuleInfo "История: Доработки"
ModuleInfo "История: 1.20"
ModuleInfo "История: Переработка модуля"
ModuleInfo "История: 1.16"
ModuleInfo "История: Добавлены методы Write\ReadCString в качестве эксперимента"
ModuleInfo "История: 1.15"
ModuleInfo "История: Добавлен флаг 'no_save' классам"
ModuleInfo "История: 1.14"
ModuleInfo "История: Переименован метод Tag() -> ToString()"
ModuleInfo "История: Теперь в методе ToString() можно задавать фильтр"
ModuleInfo "История: 1.12"
ModuleInfo "История: Доработки в деструкторе"
ModuleInfo "История: Создание обьектного потока теперь происходит через метод-конструктор"
ModuleInfo "История: 1.10"
ModuleInfo "История: Улучшения"
ModuleInfo "История: Улучшения"
ModuleInfo "История: 1.00"
ModuleInfo "История: Первый релиз"

Import brl.blitz      ' import BlitzMax
Import brl.stream     ' import TStream class
Import brl.reflection ' import Objects reflection

Public

Rem
bbdoc: Обьект данных
End Rem
Type TData Abstract
	Method New()
		TObjectStream._datas:+[Self]
	End Method
	
	Rem
	bbdoc: Тэг"
	End Rem
	Method ToString:String() Abstract
	
	Rem
	bbdoc: Функция чтения обьекта
	End Rem
	Method ReadObject:Object(stream:TStream) Abstract
	
	Rem
	bbdoc: Функция записи обьекта
	End Rem
	Method WriteObject(obj:Object, stream:TStream) Abstract
End Type

Rem
bbdoc: Обьект данных
End Rem
Function CreateObjectStream:TStream(stream:TStream)
	Return New TObjectStream.Create(stream)
End Function

Private

rem	
 	------OBJECT-STREAMS------
	| PROTOCOL   : "object"  |
	| NOSAVE TAG : "no_save" |
 	--------------------------
	
	-------------------------READING--------------------------
	| Local stream:TStream = ReadStream("object::file.txt")  |
	| Local obj:Object = stream.ReadObject()                 |
	----------------------------------------------------------
	
	-------------------------WRITEING-------------------------
	| Local stream:TStream = WriteStream("object::file.txt") |
	| stream.WriteObject(obj)                                |
	----------------------------------------------------------
endrem

Type TObjectStream Extends TStreamWrapper
	Global _datas:TData[]
	Global _tid:TTypeId = Null
	
	Method Close()
		_stream.Close()
		SetStream(Null)
	End Method
	
	Method Delete()
		If _stream Then Close()
	End Method
	
	Method Create:TStream(stream:TStream)
		SetStream(stream)
		Return Self
	End Method
	
	Method ClearArrayName:String(name:String)
		Return name[..name.FindLast("[")]
	End Method
	
	Method DataTypeId:TData(tid:TTypeId)
		Local tags:String[], i:Int
		For Local data:TData = EachIn _datas
			tags = data.ToString().Split(" ")
			If tid.Name().ToLower() <> tags[0].ToLower() Then Continue
			For i = 1 Until tags.Length
				If Not tid.MetaData(tags[i])
					i = 0
					Exit
				End If
			Next
			If i = 0 Then Continue
			Return data
		Next
	End Method
	
	rem
	Method WriteCString:Int(str:String)
		Return Write(str + "~0", str.Length + 1)
	End Method
	
	Method ReadCString:String()
		Local mem:Byte Ptr = MemAlloc(Size() - pos())
		Local i:Int
		While Not Eof()
			mem[i] = ReadByte()
			If mem[i] = 0 Then Exit
			i:+1
		Wend
		Local str:String = String.FromCString(mem)
		MemFree(mem)
		Return str
	End Method
	end rem
	
	'--------------------------------------------------------------------------------------------------------------
	
	Method ReadObject:Object()
		Select ReadByte()
			Case 59 Return Null
			Case 123
			Default Throw New TStreamReadException
		End Select
	
		Local tid:TTypeId = ReadTypeId()
		
		Local obj:Object = ReadSingle(tid)
		If obj Then Return obj
		
		If tid.MetaData("no_save") Then Return tid.NewObject()
		
		obj = ReadArray(tid)
		If obj Then Return obj
		
		obj = ReadFromData(tid)
		If obj Then Return obj
		
		Return ReadFields(tid)
	End Method
	
	Method ReadTypeId:TTypeId()
		Local mem:Byte Ptr = MemAlloc(Size() - pos())
		Local i:Int
		While Not Eof()
			mem[i] = ReadByte()
			If mem[i] = 125
				mem[i] = 0
				Exit
			End If
			i:+1
		Wend
		Local name:String = String.FromCString(mem)
		MemFree(mem)
?Debug
		Local t:TTypeId = TTypeId.ForName(name)
		If Not t Then WriteStdout("WARNING! Type id not created -> " + name + "~n")
		Return t
?
		Return TTypeId.ForName(name)
	End Method
	
	Method ReadSingle:Object(tid:TTypeId)
		Select tid
			Case ByteTypeId Return String.FromInt(ReadByte())
			Case ShortTypeId Return String.FromInt(ReadShort())
			Case IntTypeId Return String.FromInt(ReadInt())
			Case LongTypeId Return String.FromLong(ReadLong())
			Case FloatTypeId Return String.FromFloat(ReadFloat())
			Case DoubleTypeId Return String.FromDouble(ReadDouble())
			Case StringTypeId Return ReadString(ReadInt())
		End Select
	End Method
	
	Method ReadArray:Object(tid:TTypeId)
		If tid.ElementType()
			Local dims:Int = ReadInt()
			Local array:Int[] = New Int[dims]
			Local ln:Int
			For Local i:Int = 0 Until dims
				array[i] = ReadInt()
				If i = 0
					ln = array[i]
				Else
					array[i + ~0] :/array[i]
				End If
			Next
			Local obj:Object = tid.NewArray(ln, array)
			Local etid:TTypeId = TTypeId.ForName(ClearArrayName(tid.Name()))
			For Local pos:Int = 0 Until ln
				Select etid
					Case ByteTypeId tid.SetArrayElement(obj, pos, Object(String(ReadByte())))
					Case ShortTypeId tid.SetArrayElement(obj, pos, Object(String(ReadShort())))
					Case IntTypeId tid.SetArrayElement(obj, pos, Object(String(ReadInt())))
					Case LongTypeId tid.SetArrayElement(obj, pos, Object(String(ReadLong())))
					Case FloatTypeId tid.SetArrayElement(obj, pos, Object(String(ReadFloat())))
					Case DoubleTypeId tid.SetArrayElement(obj, pos, Object(String(ReadDouble())))
					Case StringTypeId tid.SetArrayElement(obj, pos, Object(ReadString(ReadInt())))
					Default tid.SetArrayElement(obj, pos, ReadObject())
				End Select
			Next
			Return obj
		End If
	End Method
	
	Method ReadFromData:Object(tid:TTypeId)
		Local data:TData = DataTypeId(tid)
		If data Then Return data.ReadObject(Self)
	End Method
	
	Method ReadFields:Object(tid:TTypeId)
		Local obj:Object = tid.NewObject()
		For Local f:TField = EachIn tid.EnumFields()
			If f.MetaData("no_save") Then Continue
			Local etid:TTypeId = TTypeId.ForName(f.TypeId().Name())
			DebugLog "Reading field : " + etid.Name()
			Select etid
				Case ByteTypeId f.SetInt(obj, ReadByte())
				Case ShortTypeId f.SetInt(obj, ReadShort())
				Case IntTypeId f.SetInt(obj, ReadInt())
				Case LongTypeId f.SetLong(obj, ReadLong())
				Case FloatTypeId f.SetFloat(obj, ReadFloat())
				Case DoubleTypeId f.SetDouble(obj, ReadDouble())
				Case StringTypeId f.SetString(obj, ReadString(ReadInt()))
				Default f.Set(obj, ReadObject())
			End Select
		Next
		Return obj
	End Method
	
	'--------------------------------------------------------------------------------------------------------------
	
	Method WriteObject:Int(obj:Object)
		If Not obj
			WriteByte(59)
			Return False
		End If
		WriteByte(123)
		
		Local tid:TTypeId = _tid
		_tid = Null
		If tid = ObjectTypeId Then tid = TTypeId.ForObject(obj)
		If Not tid Then tid = TTypeId.ForObject(obj)
		
		DebugLog "TID = " + tid.Name()
		
		If Not WriteTypeId(tid) Then Return False
		
		If WriteSingle(tid, obj) Then Return True
		
		If tid.MetaData("no_save") Then Return True
		
		If WriteArray(tid, obj) Then Return True
		
		If WriteData(tid, obj) Then Return True
		
		Return WriteFields(tid, obj)
	End Method
	
	Method WriteTypeId:Byte(tid:TTypeId)
		DebugLog "Writing : " + tid.Name()
		Local name:String = tid.Name() + "}"
		Return WriteBytes(name, name.Length) = name.Length
	End Method
	
	Method WriteSingle:Byte(tid:TTypeId, obj:Object)
		Select tid
			Case ByteTypeId WriteByte(Byte(obj.ToString())) ; Return True
			Case ShortTypeId WriteShort(Short(obj.ToString())) ; Return True
			Case IntTypeId WriteInt(Int(obj.ToString())) ; Return True
			Case LongTypeId WriteLong(Long(obj.ToString())) ; Return True
			Case FloatTypeId WriteFloat(Float(obj.ToString())) ; Return True
			Case DoubleTypeId WriteDouble(Double(obj.ToString())) ; Return True
			Case StringTypeId
				WriteInt(obj.ToString().Length)
				WriteString(obj.ToString())
				Return True
		End Select
	End Method
	
	Method WriteArray:Byte(tid:TTypeId, obj:Object)
		If tid.Name().EndsWith("]")
			Local etid:TTypeId = TTypeId.ForName(ClearArrayName(tid.Name()))
			WriteInt(tid.ArrayDimensions(obj))
			For Local i:Int = 0 Until tid.ArrayDimensions(obj)
				WriteInt(tid.ArrayLength(obj, i))
			Next
			For Local pos:Int = 0 Until tid.ArrayLength(obj)
				Select etid
					Case ByteTypeId WriteByte(tid.GetArrayElement(obj, pos).ToString().ToInt())
					Case ShortTypeId WriteShort(tid.GetArrayElement(obj, pos).ToString().ToInt())
					Case IntTypeId WriteInt(tid.GetArrayElement(obj, pos).ToString().ToInt())
					Case LongTypeId WriteLong(tid.GetArrayElement(obj, pos).ToString().ToLong())
					Case FloatTypeId WriteFloat(tid.GetArrayElement(obj, pos).ToString().ToFloat())
					Case DoubleTypeId WriteDouble(tid.GetArrayElement(obj, pos).ToString().ToDouble())
					Case StringTypeId
						WriteInt(tid.GetArrayElement(obj, pos).ToString().Length)
						WriteString(tid.GetArrayElement(obj, pos).ToString())
					Default
						_tid = Null
						WriteObject(tid.GetArrayElement(obj, pos))
				End Select
			Next
			Return True
		End If
	End Method
	
	Method WriteData:Byte(tid:TTypeId, obj:Object)
		Local data:TData = DataTypeId(tid)
		If data
			data.WriteObject(obj, Self)
			Return True
		End If
	End Method
	
	Method WriteFields:Byte(tid:TTypeId, obj:Object)
		For Local f:TField = EachIn tid.EnumFields()
			If f.MetaData("no_save") Then Continue
			Local etid:TTypeId = f.TypeId()
			DebugLog "Write field : " + etid.Name()
			Select etid
				Case ByteTypeId WriteByte(f.GetInt(obj))
				Case ShortTypeId WriteShort(f.GetInt(obj))
				Case IntTypeId WriteInt(f.GetInt(obj))
				Case LongTypeId WriteLong(f.GetLong(obj))
				Case FloatTypeId WriteFloat(f.GetFloat(obj))
				Case DoubleTypeId WriteDouble(f.GetDouble(obj))
				Case StringTypeId
					WriteInt(f.GetString(obj).Length)
					WriteString(f.GetString(obj))
				Default
					_tid = etid
					WriteObject(f.Get(obj))
			End Select
		Next
		Return True
	End Method
End Type

Type TObjectStreamFactory Extends TStreamFactory
	Method CreateStream:TStream(url:Object, proto:String, path:String, readable:Int, writeable:Int)
		If proto = "object" Then
			Local stream:TStream = OpenStream(path, readable, writeable)
			If stream Then Return New TObjectStream.Create(stream)
		End If
	End Method
End Type

Function Free()
	TObjectStream._datas = Null
	TObjectStream._tid = Null
End Function

Function install()
	New TObjectStreamFactory
	OnEnd(Free)
End Function

install()
