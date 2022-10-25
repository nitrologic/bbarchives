; ID: 2428
; Author: Nilium
; Date: 2009-03-06 10:39:12
; Title: Replace Method at Runtime
; Description: Code to replace a type's method at runtime

Strict

Global SCOPE_NAME:String[] = ["NULL", "FUNCTION", "USERTYPE", "LOCALBLOCK"]
Const SCOPE_FUNCTION=1
Const SCOPE_USERTYPE=2
Const SCOPE_LOCALBLOCK=3

Global DECL_NAME:String[] = ["END", "CONST", "LOCAL", "FIELD", "GLOBAL", "VARPARAM", "TYPEMETHOD", "TYPEFUNCTION", "NULL"]
Const DECL_END = 0
Const DECL_CONST = 1
Const DECL_LOCAL = 2
Const DECL_FIELD = 3
Const DECL_GLOBAL = 4
Const DECL_VARPARAM = 5
Const DECL_TYPEMETHOD = 6
Const DECL_TYPEFUNCTION = 7

'#region Testing

Type DebugScope
	Field _class:Int
	
	Field kind%
	Field name$
	Field decls:TList
	
	Method New()
		kind = 0
		name = ""
		decls = New TList
	End Method
	
	Method Spit()
		Print "Kind: "+SCOPE_NAME[kind]
		Print "Name: "+name
		Print "Decls {"
		For Local i:DebugDecl = EachIn decls
			i.Spit(); Print ""
		Next
		Print "}"
	End Method
	
	Function ForClass:DebugScope(cp@ Ptr)
		If cp = Null Then
			Return Null
		EndIf
		
		Local scope:DebugScope = New DebugScope
		scope._class = Int cp
		Local p% Ptr = Int Ptr cp
		p = Int Ptr p[2]
		scope.kind = p[0]
		scope.name = String.FromCString(Byte Ptr p[1])
		
		p = p + 2
		
		While p[0]
			Local decl:DebugDecl = New DebugDecl
			decl.ref = p
			decl.kind = p[0]
			decl.name = String.FromCString(Byte Ptr p[1])
			decl.tag = String.FromCString(Byte Ptr p[2])
			decl.opaque = p[3]
			scope.decls.AddLast(decl)
			p :+ 4
		Wend
		
		Return scope
	End Function
	
	Function ForName:DebugScope(_type$)
		Local typeid:TTypeId = TTypeId.ForName(_type)
		If typeid = Null Then
			Return Null
		EndIf
		Return DebugScope.ForClass(Byte Ptr typeid._class)
	End Function
	
	Method DeclForName:DebugDecl(declname$, declkind%)
		For Local i:DebugDecl = EachIn decls
			If i.kind = declkind And i.name = declname
				Return i
			EndIf
		Next
		Return Null
	End Method
End Type

Type DebugDecl
	Field ref@ Ptr
	Field kind%
	Field name$
	Field tag$
	Field opaque%
	
	Method New()
		kind = 8
		opaque = 0
		name = ""
		tag = ""
	End Method
	
	Method Spit()
		Print "Kind:     "+DECL_NAME[kind]
		Print "Name:     "+name
		Print "Tag:      "+tag
		Select kind
			Case DECL_FIELD
				Print "Index:    "+opaque
			Default
				Print "Opaque:   "+opaque
		End Select
	End Method
End Type

'#endregion

Type Foobar
	Field _name$
	
	Method setName( n$ )
		_name = n
	End Method
	
	Method ToString$()
		Return "Normal"
	End Method
End Type

Function ReplaceMethod@ Ptr( _method:String, inClass:String, with@ Ptr, searchSuperTypes:Int = False )
	Local result@ Ptr = Null
	Local scope:DebugScope = DebugScope.ForName(inClass)
	Local class:Int Ptr = Int Ptr scope._class
	While scope And class
		Local decl:DebugDecl = scope.DeclForName(_method, DECL_TYPEMETHOD)
		
		If decl = Null And searchSuperTypes Then
			class = Int Ptr class[0]
			scope = DebugScope.ForClass(class)
			Continue
		ElseIf decl = Null
			scope = Null
			class = Null
			Exit
		EndIf
		
		Local mp@ Ptr Ptr = Byte Ptr Ptr(Byte Ptr(class)+decl.opaque)
		result = mp[0]
		mp[0] = with
		
		scope = Null
		class = Null
	Wend
	
	Return result
End Function

Local foo:Foobar = New Foobar

Local scope:DebugScope = DebugScope.ForName("Foobar")
scope.Spit()

Local decl:DebugDecl = scope.DeclForName("ToString", DECL_TYPEMETHOD)

Print foo.ToString()
foo.setName("razzledazzlerootbeer")

Local oldMethod:String( obj:Object )
oldMethod = ReplaceMethod( "ToString", "Foobar", newToString, False )

Print "New method: "+foo.ToString()
Print "Old method: "+oldMethod( foo )

Function newToString:String( _self:Foobar )
	Return "Foo._name = "+_self._name
End Function
