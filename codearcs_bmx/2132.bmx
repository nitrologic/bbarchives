; ID: 2132
; Author: Azathoth
; Date: 2007-10-31 03:39:42
; Title: Clone Objects
; Description: This uses reflection to make deep or shallow copies of objects.

' Clones an object and returns the clone.
' Any fields that references an object only gets the reference copied unless MetaData contains {Clone}
' which then a copy is also made of the object referenced.
' {NoClone} prevents the field being copied.
Function CloneObject:Object(obj:Object)
	Local cobj:Object
	
	If obj=Null Then Return Null
	
	Local objId:TTypeId=TTypeId.ForObject(obj)
	
	If objId.ExtendsType(StringTypeId)
		Return String(obj)
	EndIf
	
	If objId.ExtendsType(ArrayTypeId)
		If objId.ArrayLength(obj)>0
			cobj=objId.NewArray(objId.ArrayLength(obj))
			
			If cobj
				For Local i=0 Until objId.ArrayLength(obj)
					If objId.ElementType().ExtendsType(ArrayTypeId) Or objId.ElementType().ExtendsType(StringTypeId) ..
						Or objId.ElementType().ExtendsType(ObjectTypeId)
						objId.SetArrayElement(cobj,i,CloneObject(objId.GetArrayElement(obj,i)))
					Else
						objId.SetArrayElement(cobj,i,objId.GetArrayElement(obj,i))
					EndIf
				Next
			EndIf
		EndIf
		
		Return cobj
	EndIf
	
	cobj=New obj
	
	For Local fld:TField=EachIn objId.EnumFields()
		Local fldId:TTypeId=fld.TypeId()
		
		If fld.Get(obj)<>Null And fld.MetaData("NoClone")=Null
			If Not fld.MetaData("Clone")=Null
				fld.Set(cobj,CloneObject(fld.Get(obj)))
			Else
				fld.Set(cobj,fld.Get(obj))
			EndIf
		EndIf
	Next
	
	Return cobj
	
EndFunction
