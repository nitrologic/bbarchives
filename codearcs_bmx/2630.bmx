; ID: 2630
; Author: Zakk
; Date: 2009-12-23 11:53:28
; Title: Type Explorer
; Description: Let's you browse BMax's internal Types. (Requires GUI Module)

'type explorer.bmx
'=============================
SuperStrict
Import maxgui.drivers
'===========GADGETS===========
Global window:TGadget = CreateWindow("Type Explorer", 100, 100, 870, 600, , WINDOW_TITLEBAR|WINDOW_CENTER)

Local type_label:TGadget = CreateLabel("Types:", 13, 6, 35, 16, window)
Global type_listbox:TGadget = CreateListBox(13, 25, 200, 540, window)

Local field_label:TGadget = CreateLabel("Fields:", 226, 6, 50, 16, window)
Global field_listbox:TGadget = CreateListBox(226, 25, 200, 540, window)

Local method_label:TGadget = CreateLabel("Methods:", 439, 6, 50, 16, window)
Global method_listbox:TGadget = CreateListBox(439, 25, 200, 540, window)

Local super_label:TGadget = CreateLabel("Super Type:", 652, 6, 120, 16, window)
Global super_listbox:TGadget = CreateListBox(652, 25, 200, 25, window)

Local derives_label:TGadget = CreateLabel("Derived Types:", 652, 56, 120, 16, window)
Global derives_listbox:TGadget = CreateListBox(652, 75, 200, 490, window)

Global quick_scroll:TGadget = CreateTextField(53, 3, 160, 20, window)
'=============================



'============TYPES============
Type TType
	'=============================
	Global list:TList = New TList
	Global map:TMap = New TMap
	'=============================
	Field id:TTypeId
	Field index:Int
	Field name:String
	Field fields:TList
	Field methods:TList
	Field super_type:TType
	Field derived_types:TList
	'=============================
	Function Create:TType(id:TTypeId)
		Local new_type:TType = New TType
		new_type.id = id
		new_type.name = id.Name()
		new_type.GenerateKeys()
		new_type.ListFields()
		new_type.ListMethods()
		Return new_type
	End Function
	'=============================
	Method GenerateKeys()
		Local length:Int = Len(name), key:String, n:Int
		For n=1 To length
			key = Upper( Left(name, n) )
			If Not map.Contains(key)
				map.Insert(key, Self)
			EndIf
		Next
	End Method
	'=============================
	Method ListFields()
		Local fld:TField
		For fld = EachIn id.EnumFields() 
			fields.AddLast(fld)
		Next
	End Method
	'=============================
	Method ListMethods()
		Local meth:TMethod
		For meth = EachIn id.EnumMethods()
			methods.AddLast(meth)
		Next
	End Method
	'=============================
	Method FindSuper()
		Local super_typeid:TTypeId = id.SuperType()
		If super_typeid <> Null
			Local key:String = Upper( super_typeid.Name() )
			super_type = TType( map.ValueForKey(key) )
		EndIf
	End Method
	'=============================
	Method ListDerives()
		Local derived_typeids:TList = id.DerivedTypes(), type_id:TTypeId, derived_type:TType
		For type_id = EachIn derived_typeids
			Local key:String = Upper( type_id.Name() )
			derived_type = TType( map.ValueForKey(key) )
			derived_types.AddLast(derived_type)
		Next
	End Method
	'=============================
	Method New()
		list.AddLast(Self)
		fields = New TList
		methods = New TList
		derived_types = New TList
	End Method
	'=============================
	Method PopulateLists()
		PopulateFieldList(field_listbox)
		PopulateMethodList(method_listbox)
		PopulateSuperList(super_listbox)
		PopulateDerivesList(derives_listbox)
	End Method
	'=============================
	Method PopulateFieldList(listbox:TGadget)
		listbox.Clear()
		Local fld:TField
		For fld = EachIn fields
			Local key:String = Upper( fld.TypeId().Name() )
			Local a_type:TType = TType( map.ValueForKey(key) )
			AddGadgetItem(listbox, fld.Name()+" :"+fld.TypeId().Name(), , , , a_type)
		Next
	End Method
	'=============================
	Method PopulateMethodList(listbox:TGadget)
		listbox.Clear()
		Local meth:TMethod
		For meth = EachIn methods
			Local key:String = Upper( meth.TypeId().Name() )
			Local a_type:TType = TType( map.ValueForKey(key) )
			AddGadgetItem(listbox, meth.Name()+" :"+meth.TypeId().Name(), , , , a_type)
		Next
	End Method
	'=============================
	Method PopulateSuperList(listbox:TGadget)
		listbox.Clear()
		If super_type <> Null
			AddGadgetItem(listbox, super_type.name, , , , super_type)
		EndIf
	End Method
	'=============================
	Method PopulateDerivesList(listbox:TGadget)
		listbox.Clear()
		Local derived_type:TType
		For derived_type = EachIn derived_types
			AddGadgetItem(listbox, derived_type.name, , , , derived_type)
		Next
	End Method
	'=============================
End Type
'=============================





'=======INITIALIZATION========
Initialize()
PopulateTypeList()
'=============================

'==========MAIN LOOP==========
Repeat
	WaitEvent()
	Local event:TEvent = CurrentEvent
	Select event.id
		Case EVENT_GADGETACTION
			Select event.source
				Case type_listbox
					Local a_type:TType = TType(event.extra)
					a_type.PopulateLists()
					SetGadgetText(quick_scroll, a_type.name)
				Case super_listbox, derives_listbox, field_listbox, method_listbox
					Local a_type:TType = TType(event.extra)
					a_type.PopulateLists()
					SetGadgetText(quick_scroll, a_type.name)
					ScrollToType( a_type.name )
				Case quick_scroll
					ScrollToType( GadgetText(quick_scroll) )
				Default
			End Select
		Case EVENT_WINDOWCLOSE
			End
		Default
	End Select
Forever
'=============================





'==========FUNCTIONS==========
Function ScrollToType(name:String)
	name = Upper(name)
	Local a_type:TType = TType( TType.map.ValueForKey(name) )
	If a_type <> Null
		SelectGadgetItem(type_listbox, CountGadgetItems(type_listbox)-1)
		SelectGadgetItem(type_listbox, a_type.index)
		a_type.PopulateLists()
		Return
	EndIf
	field_listbox.Clear()
	method_listbox.Clear()
End Function
'=============================

'=======INIT FUNCTIONS========
Function Initialize()
	Local type_id:TTypeId
	Local new_type:TType
	For type_id = EachIn TTypeId.EnumTypes()
		If type_id.Name() <> "TType"
			new_type = TType.Create(type_id)
		EndIf
	Next
	For new_type = EachIn TType.list
		new_type.FindSuper()
		new_type.ListDerives()
	Next
End Function
'=============================
Function PopulateTypeList()
	Local a_type:TType
	For a_type = EachIn TType.list
		a_type.index = CountGadgetItems(type_listbox)
		AddGadgetItem(type_listbox, a_type.name, , , , a_type)
	Next
End Function
'=============================
