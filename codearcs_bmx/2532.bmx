; ID: 2532
; Author: Nilium
; Date: 2009-07-15 13:56:17
; Title: Object Allocation &amp; Type Changing
; Description: More of an example than something useful, some code that'll do what the title says.

SuperStrict

Private

Extern "C"
	Function bbGCAlloc:Byte Ptr Ptr(sz:Int, clas:Byte Ptr)
End Extern

Function _SwapClass:Byte Ptr(p:Byte Ptr Ptr, newtype:TTypeID)
	Assert p Else "No object provided"
	Assert newtype Else "No TTypeID provided"
	Local old_class:Byte Ptr = p[0]
	p[0] = Byte Ptr(newtype._class)
	Return old_class
End Function

Function _AllocateObject:Byte Ptr Ptr(size:Int, initType:TTypeID)
	If initType = Null Then
		initType = ObjectTypeID
	EndIf
	
	size :+ 8
	Assert size >= 8 Else "Invalid size for object"
	
	If size < initType._size+4 Then
		size = initType._size+4
	EndIf
' Allocate object
	Local p:Byte Ptr Ptr = bbGCAlloc(size, Byte Ptr(initType._class))
' Get constructor for initial type	
	Local clas:Byte Ptr Ptr = Byte Ptr Ptr(initType._class)
	Local _new(obj:Byte Ptr Ptr)=clas[4]
	clas = Byte Ptr Ptr(clas[0])
	
	While clas <> Null And Int(Byte Ptr(_new)) = 0
		clas = Byte Ptr Ptr(clas[0])
		_new = clas[4]
	Wend
	
	If Int(Byte Ptr(_new)) <> 0 Then
' Call constructor if one exists (if one doesn't, you should run very far in the opposite direction)
		_new(p)
	EndIf
	
	Return p
End Function

Public

Global SwapClass:Byte Ptr(obj:Object, newtype:TTypeID) = Byte Ptr(_SwapClass)
Global AllocateObject:Object(sz:Int, initType:TTypeID) = Byte Ptr(_AllocateObject)
