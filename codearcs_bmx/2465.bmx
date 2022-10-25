; ID: 2465
; Author: Otus
; Date: 2009-04-24 08:00:05
; Title: Hash Table
; Description: String-keyed hash table

SuperStrict

' String-keyed dynamically resizing HashTable

' Collision algorithm adapted from Python dict

Type TEntry
	
	Field _key:String, _value:Object
	
End Type

Type THashTable
	
	Const INIT_SIZE:Int = 256
	Const HASH_SHIFT:Int = 5
	Const HASH_PRIME:Int = 108301
	
	' = _table.length-1
	Field _last:Int
	
	' Total count and dummy count
	' real = _count - _dummies
	Field _count:Int, _dummies:Int
	
	' Data array, size a power of two
	Field _table:TEntry[]
	
	Method New()
		Clear
	End Method
	
	Method Clear()
		_last = INIT_SIZE - 1
		_count = 0 
		_dummies = 0
		_table = New TEntry[INIT_SIZE]
	End Method
	
	Method Contains:Int(key:String)
		Return _table[_FindIndex(key)]<>Null
	End Method
	
	Method Insert(key:String, value:Object)
		' Keep table < 50% full
		If _count > _last Shr 1 Then _Resize ((_last+1) Shl 1)
		
		Local i% = _FindIndex(key)
		
		If _table[i]
			_table[i]._value = value
		Else
			_count :+ 1
			_table[i] = New TEntry
			_table[i]._key = key
			_table[i]._value = value
		End If
	End Method
	
	Method IsEmpty:Int()
		Return _count=_dummies
	End Method
	
	Method Remove:Int(key:String)
		Local i% = _FindIndex(key)
		If Not _table[i] Then Return False
		_table[i] = _dummy
		_dummies :+ 1
		Return True
	End Method
	
	Method ValueForKey:Object(key:String)
		Local i% = _FindIndex(key)
		If Not _table[i] Then Return Null
		Return _table[i]._value
	End Method
	
	Method Keys:THashTableEnum()
		Local e:THashTableEnum = New THashKeysEnum
		e._ht = Self
		For e._i = 0 To _last
			If _table[e._i] And _table[e._i]<>_dummy Return e
		Next
		e._ht = Null
		Return e
	End Method
	
	Method Values:THashTableEnum()
		Local e:THashTableEnum = New THashValuesEnum
		e._ht = Self
		For e._i = 0 To _last
			If _table[e._i] And _table[e._i]<>_dummy Return e
		Next
		e._ht = Null
		Return e
	End Method
	
	Method ObjectEnumerator:THashTableEnum()
		Local e:THashTableEnum = New THashTableEnum
		e._ht = Self
		For e._i = 0 To _last
			If _table[e._i] And _table[e._i]<>_dummy Return e
		Next
		e._ht = Null
		Return e
	End Method
	
	' Private
	
	Method _FindIndex:Int(key:String)
		' Mul-Xor all chars
		Local h%
		For Local i% = 0 Until key.length
			h = h * HASH_PRIME ~ key[i]
		Next
		
		' Initial hash uses just the low bits
		Local j% = h & _last
		Repeat
			Local e:TEntry = _table[j]
			If Not e Return j
			If e._key = key Return j
			
			' Next iteration uses higher bits
			h :Shr HASH_SHIFT
			
			' Even if we run out of bits this is nonlinear
			' and (5*j + 1) visits every array index.
			j = (5*j + 1 + h) & _last
		Forever
	End Method
	
	Method _Resize(size:Int)
		Local t:TEntry[] = _table
		_last = size-1
		_table = New TEntry[size]
		_count = 0
		_dummies = 0
		
		For Local i% = 0 Until t.length
			Local e:TEntry = t[i]
			If (Not e) Or (e = _dummy) Then Continue
			
			Local j% = _FindIndex(e._key)
			_table[j] = e
			_count :+ 1
		Next
	End Method
	
End Type

Type THashTableEnum
	
	Field _ht:THashTable, _i:Int
	
	Method HasNext:Int()
		Return _ht<>Null
	End Method
	
	Method NextObject:Object()
		Local o:Object = _ht._table[_i]
		For _i = _i+1 To _ht._last
			If _ht._table[_i] And _ht._table[_i]<>_dummy Then Return o
		Next
		_ht = Null
		Return o
	End Method
	
	Method ObjectEnumerator:THashTableEnum()
		Return Self
	End Method
	
End Type

Type THashKeysEnum Extends THashTableEnum
	
	Method NextObject:Object()
		Local o:Object = _ht._table[_i]._key
		For _i = _i+1 To _ht._last
			If _ht._table[_i] And _ht._table[_i]<>_dummy Then Return o
		Next
		_ht = Null
		Return o
	End Method
	
End Type

Type THashValuesEnum Extends THashTableEnum
	
	Method NextObject:Object()
		Local o:Object = _ht._table[_i]._value
		For _i = _i+1 To _ht._last
			If _ht._table[_i] And _ht._table[_i]<>_dummy Then Return o
		Next
		_ht = Null
		Return o
	End Method
	
End Type

' For removed entries
Private
Global _dummy:TEntry = New TEntry
