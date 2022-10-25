; ID: 785
; Author: Ken Lynch
; Date: 2003-08-30 10:59:01
; Title: Data Bank System
; Description: Data Bank System

;=================================================
;
; DataBank library
;
; (c)2003 Ken Lynch
;
;=================================================

;=================================================
;
; Global variables (NB: Do not use)
;
;=================================================

Global db_field_index
Global db_field_name$
Global db_data_offset

;=================================================
;
; Peek and poke string functions
;
;=================================================

;
; PokeString bank,offset,value$
;
Function PokeString(bank, offset, value$)
	For i = 1 To Len(value$)
		a = Asc(Mid(value$, i, 1))
		PokeByte bank, offset + i - 1, a
	Next
	PokeByte bank, offset + i - 1, 0
End Function

;
; PeekString bank,offset
;
Function PeekString$(bank, offset)
	a = PeekByte(bank, offset)
	While a <> 0
		s$ = s$ + Chr(a)
		i = i + 1
		a = PeekByte(bank, offset + i)
	Wend
	Return s$
End Function

;=================================================
;
; Internal functions (NB: Do not use)
;
;=================================================

;
; LocateField(bank,name$)
;
Function LocateField(bank, name$)
	If BankSize(bank) > 0 Then
		field_count = PeekInt(bank, 0)
		If field_count > 0
			low = 0
			high = field_count
			old_middle = -1
			Repeat
				middle = low + (high - low) / 2
				If middle = old_middle Then
					If name$ > db_field_name$ Then middle = middle + 1
					db_field_index = middle
					db_data_offset = -1
					Return
				End If
				db_field_name$ = PeekString(bank, middle * 64 + 4)
				If name$ < db_field_name$ Then high = middle
				If name$ > db_field_name$ Then low = middle
				If name$ = db_field_name$ Then
					db_field_index = middle
					db_data_offset = (field_count * 64 + 4) + PeekInt(bank, db_field_index * 64 + 4 + 60)
					Return
				End If
				old_middle = middle
			Forever
		End If
	End If
	db_field_index = 0
	db_field_name$ = ""
	db_data_offset = -1
End Function

;
; AllocateField bank,name$,size
;
Function AllocateField(bank, name$, size)
	If BankSize(bank) = 0 Then
		ResizeBank(bank, 4)
		PokeInt bank, 0, 0
	End If
	
	name$ = Left(name$, 59)

	field_count = PeekInt(bank, 0)
	old_bank_size = BankSize(bank)
	new_bank_size = old_bank_size + 64 + size
	header_size = (field_count * 64) + 4
	data_size = old_bank_size - header_size
	insert_pos = db_field_index
	copy_offset = header_size - (field_count - insert_pos) * 64
	copy_size = data_size + (field_count - insert_pos) * 64

	ResizeBank bank, new_bank_size
	
	CopyBank bank, copy_offset, bank, copy_offset + 64, copy_size

	PokeString bank, copy_offset, name$
	offset = new_bank_size - size - header_size - 64
	PokeInt bank, copy_offset + 60, offset
	
	PokeInt bank, 0, field_count + 1
		
	db_data_offset = new_bank_size - size
End Function

;=================================================
;
; Set Field functions
;
;=================================================

;
; SetByteField bank,name$,value
;
Function SetByteField(bank, name$, value)
	LocateField(bank, name$)
	If db_data_offset = -1 Then AllocateField(bank, name$, 1)
	PokeByte bank, db_data_offset, value
End Function

;
; SetShortField bank,name$,value
;
Function SetShortField(bank, name$, value)
	LocateField(bank, name$)
	If db_data_offset = -1 Then AllocateField(bank, name$, 2)
	PokeShort bank, db_data_offset, value
End Function

;
; SetIntField bank,name$,value
;
Function SetIntField(bank, name$, value)
	LocateField(bank, name$)
	If db_data_offset = -1 Then AllocateField(bank, name$, 4)
	PokeInt bank, db_data_offset, value
End Function

;
; SetFloatField bank,name$,value#
;
Function SetFloatField(bank, name$, value#)
	LocateField(bank, name$)
	If db_data_offset = -1 Then AllocateField(bank, name$, 4)
	PokeFloat bank, db_data_offset, value#
End Function

;
; SetStringField bank,name$,value$[,size]
;
Function SetStringField(bank, name$, value$, size=255)
	LocateField(bank, name$)
	If db_data_offset = -1 Then AllocateField(bank, name$, size + 1)
	PokeString bank, db_data_offset, Left(value$, size)
End Function

;=================================================
;
; Get Field functions
;
;=================================================

;
; GetByteField(bank,name$)
;
Function GetByteField(bank, name$)
	LocateField(bank, name$)
	If db_data_offset = -1 Then Return 0
	Return PeekByte(bank, db_data_offset)
End Function

;
; GetShortField(bank,name$)
;
Function GetShortField(bank, name$)
	LocateField(bank, name$)
	If db_data_offset = -1 Then Return 0
	Return PeekShort(bank, db_data_offset)
End Function

;
; GetIntField(bank,name$)
;
Function GetIntField(bank, name$)
	LocateField(bank, name$)
	If db_data_offset = -1 Then Return 0
	Return PeekInt(bank, db_data_offset)
End Function

;
; GetFloatField#(bank,name$)
;
Function GetFloatField#(bank, name$)
	LocateField(bank, name$)
	If db_data_offset = -1 Then Return 0
	Return PeekFloat(bank, db_data_offset)
End Function

;
; GetStringField$(bank,name$)
;
Function GetStringField$(bank, name$)
	LocateField(bank, name$)
	If db_data_offset = -1 Then Return ""
	Return PeekString(bank, db_data_offset)
End Function

;=================================================
;
; Load/save functions
;
;=================================================

;
; SaveBank bank,file_name$
;
Function SaveBank(bank, file_name$)
	file = WriteFile(file_name$)
	WriteBytes bank, file, 0, BankSize(bank)
	CloseFile file
End Function

;
; LoadBank(file$,bank)
;
Function LoadBank(file_name$)
	bank = CreateBank(FileSize(file_name$))
	file = ReadFile(file_name$)
	ReadBytes bank, file, 0, BankSize(bank)
	CloseFile file
	Return bank
End Function

;=================================================
;
; DataBank system test code
;
; (c)2003 Ken Lynch
;
;=================================================

db = CreateBank()

SetIntField db, "Field1", 125
SetIntField db, "Field2", 45
SetIntField db, "Field3", 72
SetFloatField db, "Field4", 12.324
SetFloatField db, "Field5", 1.43
SetFloatField db, "Field6", 9.34
SetStringField db, "String1", "Hello World!"

Print GetIntField(db, "Field1")
Print GetIntField(db, "Field2")
Print GetIntField(db, "Field3")
Print GetFloatField(db, "Field4")
Print GetFloatField(db, "Field5")
Print GetFloatField(db, "Field6")
Print GetStringField(db, "String1")
