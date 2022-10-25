; ID: 2531
; Author: Nilium
; Date: 2009-07-15 00:42:53
; Title: Convert an Object to JSON
; Description: Converts an object to a JSON string

SuperStrict

Private
Function _objtoptr:Byte Ptr(obj:Byte Ptr)
	Return obj
End Function
Global ObjToPtr:Byte Ptr(obj:Object)=Byte Ptr(_objtoptr)

Function FieldToJSON:String(fid:TField, forObject:Object, map:TMap)
	Select fid.TypeId()
		Case IntTypeId,ShortTypeId,ByteTypeId
			Return String(fid.GetInt(forObject))
		Case LongTypeId
			Return String(fid.GetLong(forObject))
		Case DoubleTypeId
			Return String(fid.GetDouble(forObject))
		Case FloatTypeId
			Return String(fid.GetFloat(forObject))
		Default
			Return ObjectToJSON(fid.Get(forObject), map)
	End Select
End Function

Public

' The map argument is used to record what objects have already been put in a prior part of any JSON string
Function ObjectToJSON:String(obj:Object, map:TMap)
	Assert map Else "Inspection map not present"
	
	Local name$ = Int(ObjToPtr(obj))
	Local result$
	
	Local tid:TTypeId = TTypeId.ForObject(obj)
	
	If tid = Null Then
		Return "null"
	EndIf
	
	If tid <> StringTypeId And tid._class <> ArrayTypeId._class And map.Contains(name) Then
		Return "{~qhandle~q: "+name+"}"
	EndIf
	
	map.Insert(name,name)
	
	If tid._class = ArrayTypeId._class Then
		If tid.Name().StartsWith("Null[") Then
			Return "null"
		EndIf
		
		result = "{~qhandle~q: "+name+", ~qtype~q: ~q"+tid.Name()+"~q"
		Local elemt:TTypeId = tid.ElementType()
		If elemt Then
			Local dimensions:Int = tid.ArrayDimensions(obj)
			result :+ ", dimensions: ["
			
			Local dimString$ = ""
			Local last:Int = 1
			For Local dim:Int = dimensions-1 To 0 Step -1
				Local dimLength:Int = tid.ArrayLength(obj, dim)
				
				If dim < dimensions-1 Then
					dimString = ", " + dimString
				EndIf
				
				dimString = (dimLength / last) + dimString
				last = dimLength
			Next
			result :+ dimString
			result :+ "]"
			
			Local elems:String[] = New String[tid.ArrayLength(obj, 0)]
			For Local i:Int = 0 Until elems.Length
				Select elemt
					Case IntTypeId, ShortTypeId, ByteTypeId, LongTypeId, DoubleTypeId, FloatTypeId
						elems[i] = String(tid.GetArrayElement(obj, i))
					Default
						elems[i] = ObjectToJSON(tid.GetArrayElement(obj, i), map)
				End Select
			Next
			
			result :+ ", ~qcontent~q: ["+(", ".Join(elems))+"]}"
		Else
			result :+ ", ~qcontent~q: []}"
		EndIf
	ElseIf tid = StringTypeId
		If String(obj) = Null Then
			Return "~q~q"
		EndIf
		result = "~q" + String(obj).Replace("~q", "\~q").Replace("~n", "\n").Replace("~t", "\t").Replace("~r", "\r").Replace("~0", "\0") + "~q"
	Else
		If Not obj Then
			Return "null"
		EndIf
		
		result = "{~qhandle~q: "+name+", ~qtype~q: ~q"+tid.Name()+"~q"
		
		Local fields:TList = tid.EnumFields()
		Local enum:TListEnum = fields.ObjectEnumerator()
		
		While enum.HasNext()
			Local fid:TField = TField(enum.NextObject())
			result :+ ", ~q."+fid.Name()+"~q: "+FieldToJSON(fid, obj, map)
		Wend
		
		result :+ "}"
	EndIf
	
	Return result
End Function
