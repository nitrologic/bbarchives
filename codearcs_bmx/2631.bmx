; ID: 2631
; Author: grable
; Date: 2009-12-23 12:19:39
; Title: Ad-Hoc Interfaces
; Description: Interfaces for BlitzMax via Abstract Types

Rem
	Type Interfaces

	this unit adds ad-hoc Interfaces via Abstract Types.

!!!!!!!
		note that i use a modified BRL.Reflection supporting Object->Object[] Type casting.
		If this doesnt work For you, search For HACK And enable the line after it
		(and disable the one right after that) there are 3 such places.


		you will allso have To add this Function To "BRL.Reflection/reflection.cpp" Or make en equivalent in blitzmax.

			void *bbSetMethodPtr( BBObject *obj, int index, void *ptr){
				void *old = *( (void**) ((char*)obj->clas+index) );
				*( (void**) ((char*)obj->clas+index) ) = ptr;
				return old;
			}
!!!!!!!

	public interface:
		Type TInterfaceClass

		Type Interface
			.. these two interfaces are wrappers for blitzmaxs internal enumeration "interfaces"
				Type IEnumerable	' ObjectEnumerator()
				Type IEnumerator	' HasNext() / NextObject()

		Type TInterfaceEnum
			Type TInterfaceEnumArray

		Function InitializeInterfaces( verify:Int)
			.. initializes all Interface classes and caches all interface->type lookups for faster runtime queries
			.. [verify]: If True, verifies ReturnType and ArgumentTypes. Default is True in debug mode, False in Release mode.

		Function LookupInterfaceClass:TInterfaceClass( name:String)
			.. lookup an interface class by name

		Function QueryInterface:Int( out:Interface Var, interfacename:Object, implementation:Object, verify:Int)
			.. checks if the methods in [interfacename] are present in [implementation]
			.. on Success: returns True and [out] contains new interface
			.. on Failure: returns False And [out] contains Null
			.. [interfacename]: either a String representing an interface Or a TInterfaceClass instance

		Function UpdateInterface:Int( out:Interface Var, implementation:Object, verify:Int)
			.. updates an interface instance to a new [implementation]
			.. on Success: returns True and [out] is patched To the New implementation
			.. on Failure: returns False and [out] is left unchanged

		Function EnumInterface:TInterfaceEnum( interfacename:Object, interfaces:Object, verify:Int)
			.. enumerates over objects in [interfaces], either Arrays Or IEnumerable compatible
			.. and queries each element for [interfacename], skips non-compliant objects

	interface defenition:
		Type TTheType
			Method TheMethod()
				Print "The Method!"
			EndMethod
		EndType

		Type TTheOtherType
			Method TheMethod()
				Print "The Other Method!"
			EndMethod
		EndType

		Type ITheInterface Extends Interface
			Method TheMethod() Abstract
		EndType

	interface instantiation:
		Local obj:TTheType = New TTheType
		Local intf:ITheInterface
		If QueryInterface( intf, "ITheInterface", obj) Then
			intf.TheMethod()
		EndIf

	interface enumeration:
		Local list:TList = New TList
		list.AddLast New TTheType
		list.AddLast "this isnt an interface"
		list.AddLast New TTheOtherType
		For Local intf:ITheInterface = EachIn EnumInterface( "ITheInterface", list)
			intf.TheMethod()
		Next
EndRem

SuperStrict

Import BRL.Map
Import BRL.LinkedList
Import BRL.Reflection


Private
?debug
Const DEFAULT_VERIFY_METHOD:Int = True
' logging modes
'	0 = no logging
'	1 = basic 1 line log item
'	2 = same as 1 with extra sub items
Const LOG_VERBOSE:Int = 0
?Not debug
Const DEFAULT_VERIFY_METHOD:Int = False
?

Const INTERFACE_TYPENAME:String = "Interface"

' from BRL.Reflection
Extern
	Function bbRefMethodPtr:Byte Ptr( obj:Object, index:Int)
	Function bbSetMethodPtr:Byte Ptr( obj:Object, index:Int, p:Byte Ptr)	' reflection.cpp addition
	'HACK: enable this and the other 2 HACK spots if object->array casting doesnt work
	'Function bbRefAssignObject( p:Byte Ptr,obj:Object)
EndExtern

'
' method trampoline
'
Const TRAMPOLINE_SIZE:Int = 16	' size of each block, not the same as method_trampoline.Length
Const METHOD_OFFSET:Int = 7		' offset of method pointer in trampoline
Const IMPLREF_OFFSET:Byte = 8		' must be byte

Global method_trampoline:Byte[] = [	..
	$59:Byte, ..											' pop <ecx>				; store return-addr
	$58:Byte, ..											' pop <eax>				; get interface-ptr
	$FF:Byte, $70:Byte, IMPLREF_OFFSET, ..					' push dword [eax + offs]	; push implementation-ptr
	$51:Byte, ..											' push <ecx>				; restore return-addr
	$B8:Byte, $00:Byte, $00:Byte, $00:Byte, $00:Byte, ..	' mov eax, $method-ptr	; load and jump to method
	$FF:Byte, $E0:Byte ..									' jmp eax
]


Type TInterfaceTypeCache
	Field Methods:TMethod[]
	Field Trampolines:Byte[]
EndType

'used by EnumInterface() as null value
Global NullInterfaceEnum:TInterfaceEnum = New TNullInterfaceEnum

Global InterfaceMap:TMap = New TMap
Public


'
' initializes all Interface classes, must be called by user
'
Function InitializeInterfaces( verify:Int = DEFAULT_VERIFY_METHOD)
	Function RemoveSpecialMethods:TList( list:TList)	' remove New() and Delete()
		Local n:TLink = list.FirstLink()
		While n
			Select TMethod(n.Value()).Name()
				Case "New", "Delete"
					Local t:TLink = n.NextLink()
					n.Remove()
					n = t
					Continue
			EndSelect
			n = n.NextLink()
		Wend
		Return list
	EndFunction
	
	Function AddInterfaces( list:TList)
		If Not list Then Return
		For Local intf:TTypeId = EachIn list
			Local inst:TInterfaceClass = New TInterfaceClass
			inst.TypeId = intf
			'HACK: if Object->TMethod[] casting doesnt work, this might
			'bbRefAssignObject( Varptr inst.Methods, RemoveSpecialMethods( intf.EnumMethods()).ToArray())
			inst.Methods = TMethod[] RemoveSpecialMethods( intf.EnumMethods()).ToArray()
			inst.NumMethods = inst.Methods.Length
			InterfaceMap.Insert( intf.Name().ToLower(), inst)
			AddInterfaces( intf.DerivedTypes())
		Next		
	EndFunction

' find and register all interfaces classes
	Local itype:TTypeId = TTypeId.ForName(INTERFACE_TYPENAME)
	AddInterfaces( itype.DerivedTypes())

' cache INTERFACE -> TYPE relationships for faster runtime instantiation
	For Local intfc:TInterfaceClass = EachIn InterfaceMap.Values()
		Local mfuncs:TMethod[]
		For Local tid:TTypeId = EachIn TTypeId.EnumTypes()
			If  tid.ExtendsType(itype) Then Continue
		' validate methods
			If Not mfuncs Then mfuncs = New TMethod[intfc.NumMethods]
			Local midx:Int = 0, ok:Int = True
			For Local m:TMethod = EachIn intfc.Methods
				Local impl:TMethod = tid.FindMethod( m.Name())
				If Not impl Then
					ok = False
					Exit
				EndIf
				If verify Then
		' verify method return-type	
				' special hack for ObjectEnumerator() which needs different result types
					If m.Name() <> "ObjectEnumerator" Then
						Local mret:TTypeId = m.ReturnType()
						Local iret:TTypeId = impl.ReturnType()
						If (mret.Name() <> iret.Name()) And (Not mret.ExtendsType(iret)) Then
							ok = False
							Exit
						EndIf
					EndIf
						
		' verify argument types
					Local args1:TTypeId[] = m.ArgTypes()
					Local args2:TTypeId[] = impl.ArgTypes()
					If args1.Length <> args2.Length Then
						ok = False
						Exit
					EndIf
					For Local i:Int = 0 Until args1.Length
						If args1[i].Name() <> args2[i].Name() Then
							ok = False
							Exit
						EndIf
					Next
				EndIf
		' create trampoline for this method
				mfuncs[midx] = impl
				midx :+ 1
			Next
			If ok Then
		' cache the methods
				Local cache:TInterfaceTypeCache = New TInterfaceTypeCache
				cache.Methods = mfuncs
			' build the trampoline buffer
				cache.Trampolines = New Byte[ intfc.NumMethods * TRAMPOLINE_SIZE]
				Local tr:Byte Ptr = cache.Trampolines				
				For Local i:Int = 0 Until mfuncs.Length
					MemCopy tr, method_trampoline, method_trampoline.Length
					tr :+ TRAMPOLINE_SIZE
				Next				
				intfc.TypeImpls.Insert( tid, cache)
				mfuncs = Null
			EndIf
		Next
?debug
		If LOG_VERBOSE Then
			If intfc.TypeImpls.IsEmpty() Then
				DebugLog intfc.TypeId.Name() + " is not implemented by any types"
			Else
				Local count:Int = 0
				For Local n:TNode = EachIn intfc.TypeImpls
					count :+ 1
				Next
				DebugLog intfc.TypeId.Name() + " is implemented by " + count + " types"
				If LOG_VERBOSE >= 2 Then
			' list all implementation types
					For Local tid:TTypeId = EachIn intfc.TypeImpls.Keys()
						DebugLog "~t" + tid.Name()
					Next
				EndIf 
			EndIf
		EndIf
?
	Next
	
' internal interface identifiers
	IEnumerable.IID = LookupInterfaceClass("IEnumerable")
	IEnumerator.IID = LookupInterfaceClass("IEnumerator")
EndFunction

' InitializeInterfaces() must be called first
Function LookupInterfaceClass:TInterfaceClass( name:String)
	Return TInterfaceClass( InterfaceMap.ValueForKey( name.ToLower()))
EndFunction


'
' interface classes
'
Type TInterfaceClass
	Field TypeId:TTypeId
	Field Methods:TMethod[]
	Field NumMethods:Int
	Field TypeImpls:TMap = New TMap
EndType



'
' interfaces
'
Type Interface Abstract
	Field Ref:Object ' reference to the implementation object (must match position of IMPLREF_OFFSET)
	Field Mem:Byte[]	' trampoline buffer
	Field Class:TInterfaceClass
EndType

Type IEnumerable Extends Interface
	Global IID:TInterfaceClass ' used internally to skip class lookup (set by InitializeInterfaces())
	
	Method ObjectEnumerator:IEnumerator() Abstract
EndType

Type IEnumerator Extends Interface
	Global IID:TInterfaceClass ' used internally to skip class lookup (set by InitializeInterfaces())
		
	Method HasNext:Int() Abstract
	Method NextObject:Object() Abstract
EndType


'
' enumerations
'
Type TInterfaceEnum
	Field intfclass:TInterfaceClass
	Field enum:IEnumerator
	Field intf:Interface
	
	Method HasNext:Int()
		If Not enum.HasNext() Then Return False
		Local val:Object = enum.NextObject()
		If Not intf Then
			If QueryInterface( intf, intfclass, val) Then Return True
		Else
			If UpdateInterface( intf, val) Then Return True
		EndIf
		Return HasNext()
	EndMethod
	
	Method NextObject:Object()
		Return intf
	EndMethod
	
	Method ObjectEnumerator:TInterfaceEnum()
		Return Self
	EndMethod
EndType

Type TInterfaceEnumArray Extends TInterfaceEnum
	'Field ref:Object
	Field array:Object[]
	Field index:Int

	Method HasNext:Int()
		If index >= array.Length Then Return False
		Local val:Object = array[index]
		index :+ 1
		If Not intf Then
			If QueryInterface( intf, intfclass, val) Then Return True
		Else
			If UpdateInterface( intf, val) Then Return True
		EndIf
		Return HasNext()
	EndMethod
EndType

Type TNullInterfaceEnum Extends TInterfaceEnum
	Method HasNext:Int()
		Return False
	EndMethod
	
	Method NextObject:Object()
		Return Null
	EndMethod
	
	Method ObjectEnumerator:TInterfaceEnum()
		Return Self
	EndMethod
EndType


'
' public interface functions
'
Function QueryInterface:Int( out:Interface Var, interfacename:Object, obj:Object)
	If (Not obj) Then Return False
	
' get interface class
	Local intf:TInterfaceClass = TInterfaceClass(interfacename)
	If Not intf Then intf = TInterfaceClass( InterfaceMap.ValueForKey( String(interfacename).ToLower()))
	If Not intf Then Return False
	
' create interface object
	out = Interface( intf.TypeId.NewObject())
	out.Ref = obj
	out.Class = intf

	
' search for interface methods
	Local ot:TTypeId = TTypeId.ForObject(obj)
	Local cache:TInterfaceTypeCache = TInterfaceTypeCache(intf.TypeImpls.ValueForKey(ot))
	If cache Then
' use cached result to create the trampoline
		out.Mem = cache.Trampolines[..]
		Local tr:Byte Ptr = out.Mem
		For Local i:Int = 0 Until cache.Methods.Length
	' update method pointer
			Local methptr:Byte Ptr
			If cache.Methods[i]._index < 65536 Then
				methptr = bbRefMethodPtr( obj, cache.Methods[i]._index)
			Else
				methptr = Byte Ptr(cache.Methods[i]._index)
			EndIf
			Int Ptr(tr + METHOD_OFFSET)[0] = Int methptr
			bbSetMethodPtr( out, intf.Methods[i]._index, tr)
			tr :+ TRAMPOLINE_SIZE			
		Next
		Return True
	EndIf
	Return False
EndFunction

Function UpdateInterface:Int( out:Interface Var, obj:Object)
	If (Not out) Or (Not obj) Then Return False
	
	Local ot:TTypeId = TTypeId.ForObject(obj)
	Local cache:TInterfaceTypeCache = TInterfaceTypeCache(out.Class.TypeImpls.ValueForKey(ot))
	If cache Then
' use cached result to update the trampoline
		out.Ref = obj
		Local tr:Byte Ptr = out.Mem
		For Local i:Int = 0 Until cache.Methods.Length
	' update method pointer
			Local methptr:Byte Ptr
			If cache.Methods[i]._index < 65536 Then
				methptr = bbRefMethodPtr( obj, cache.Methods[i]._index)
			Else
				methptr = Byte Ptr(cache.Methods[i]._index)
			EndIf
			Int Ptr(tr + METHOD_OFFSET)[0] = Int methptr
			bbSetMethodPtr( out, out.Class.Methods[i]._index, tr)
			tr :+ TRAMPOLINE_SIZE
		Next
		Return True
	EndIf
	Return False
EndFunction

Function EnumInterface:TInterfaceEnum( interfacename:Object, obj:Object)
	If interfacename And obj Then
' get interface class
		Local intf:TInterfaceClass = TInterfaceClass(interfacename)
		If Not intf Then intf = TInterfaceClass( InterfaceMap.ValueForKey( String(interfacename).ToLower()))
		If Not intf Then Return Null		
	' check if it supports IEnumerable / IEnumerator
		Local e:IEnumerable
		If QueryInterface( e, IEnumerable.IID, obj) Then
			Local enum:TInterfaceEnum = New TInterfaceEnum
			enum.intfclass = intf
			If QueryInterface( enum.enum, IEnumerator.IID, e.ObjectEnumerator()) Then Return enum
		Else
	' check if its an array
			Local t:TTypeId = TTypeId.ForObject(obj)
			If t.ExtendsType(ArrayTypeId) Then
				Local enum:TInterfaceEnumArray = New TInterfaceEnumArray
				'HACK: if Object->Object[] casting doesnt work, this might.
				'bbRefAssignObject( Varptr enum.array, obj)
				enum.array = Object[] obj				
				enum.index = 0
				enum.intfclass = intf
				Return enum
			EndIf
		EndIf
	EndIf
	Return NullInterfaceEnum
EndFunction
