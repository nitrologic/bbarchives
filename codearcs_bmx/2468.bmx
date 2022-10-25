; ID: 2468
; Author: Otus
; Date: 2009-05-03 04:53:27
; Title: Synchronized Data Structures
; Description: Somewhat

SuperStrict

Rem
bbdoc: Synchronized Map
End Rem
'Module Otus.SynchronizedMap

'ModuleInfo "Version: 1.00"
'ModuleInfo "Author: Jan Varho"
'ModuleInfo "License: Public domain"

Import BRL.Map

?threaded
Import BRL.Threads
?

Rem
bbdoc: Synchronized Map
about:
TSynchronizedMap implements atomic operations using a mutex so 
that every operation finishes before another can begin.

Note: Enumeration locks the list. Exiting from a For-EachIn 
loop can leave the list locked until GC happens.

#Lock, #TryLock and #Unlock can be used to make a sequence of 
operations atomic.
End Rem
Type TSynchronizedMap Extends TMap
?threaded
	Field _mutex:TMutex 
	
	Method New()
		_mutex = CreateMutex()
	End Method
	
	Method Delete()
		CloseMutex _mutex
	End Method
	
	Rem
	bbdoc: Lock the map
	about: A locked map cannot be used in other threads
	End Rem
	Method Lock()
		_mutex.Lock
	End Method
	
	Rem
	bbdoc: Try to lock the map
	returns: True on success
	about: A locked map cannot be used in other threads
	End Rem
	Method TryLock%()
		Return _mutex.TryLock()
	End Method
	
	Rem
	bbdoc: Unlock the map
	End Rem
	Method Unlock()
		_mutex.Unlock
	End Method
	
	Method Clear()
		_mutex.Lock
		Super.Clear
		_mutex.Unlock
	End Method
	
	Method IsEmpty%()
		_mutex.Lock
		Local ret% = Super.IsEmpty()
		_mutex.Unlock
		Return ret
	End Method
	
	Method Insert( key:Object,value:Object )
		_mutex.Lock
		Super.Insert key, value
		_mutex.Unlock
	End Method
	
	Method Contains%( key:Object )
		_mutex.Lock
		Local ret% = Super.Contains(key)
		_mutex.Unlock
		Return ret
	End Method

	Method ValueForKey:Object( key:Object )
		_mutex.Lock
		Local ret:Object = Super.ValueForKey(key)
		_mutex.Unlock
		Return ret
	End Method
	
	Method Remove%( key:Object )
		_mutex.Lock
		Local ret% = Super.Remove(key)
		_mutex.Unlock
		Return ret
	End Method
	
	Method Keys:TMapEnumerator()
		' Leaves the map locked! See enum.Delete
		_mutex.Lock
		Local enum:TSynchronizedKeyEnumerator = New TSynchronizedKeyEnumerator
		enum._node = _FirstNode()
		enum._mutex = _mutex
		Local menum:TMapEnumerator = New TMapEnumerator
		menum._enumerator = enum
		Return menum
	End Method
	
	Method Values:TMapEnumerator()
		' Leaves the map locked! See enum.Delete
		_mutex.Lock
		Local enum:TSynchronizedValueEnumerator = New TSynchronizedValueEnumerator
		enum._node = _FirstNode()
		enum._mutex = _mutex
		Local menum:TMapEnumerator = New TMapEnumerator
		menum._enumerator = enum
		Return menum
	End Method
	
	Method Copy:TMap()
		_mutex.Lock
		Local map:TMap = Super.Copy()
		_mutex.Unlock
		Local ret:TSynchronizedMap = New TSynchronizedMap
		Local r:TNode = ret._root
		ret._root = map._root
		map._root = r
		Return ret
	End Method
	
	Method ObjectEnumerator:TNodeEnumerator()
		' Leaves the map locked! See enum.Delete
		_mutex.Lock
		Local enum:TSynchronizedNodeEnumerator = New TSynchronizedNodeEnumerator
		enum._node = _FirstNode()
		enum._mutex = _mutex
		Return enum
	End Method
?Not threaded
	' Dummy methods
	Method Lock()
	End Method
	
	Method TryLock%()
		Return True
	End Method
	
	Method Unlock()
	End Method
?
End Type

?threaded
Type TSynchronizedNodeEnumerator Extends TNodeEnumerator
	
	Field _mutex:TMutex
	
	Method Delete()
		' Safeguard against incomplete enumeration
		If _mutex Then _mutex.Unlock
	End Method
	
	Method HasNext%()
		If Super.HasNext() Then Return True
		If _mutex
			_mutex.Unlock
			_mutex = Null
		End If
		Return False
	End Method
	
End Type

Type TSynchronizedKeyEnumerator Extends TKeyEnumerator
	
	Field _mutex:TMutex
	
	Method Delete()
		' Safeguard against incomplete enumeration
		If _mutex Then _mutex.Unlock
	End Method
	
	Method HasNext%()
		If Super.HasNext() Then Return True
		If _mutex
			_mutex.Unlock
			_mutex = Null
		End If
		Return False
	End Method
	
End Type

Type TSynchronizedValueEnumerator Extends TValueEnumerator
	
	Field _mutex:TMutex
	
	Method Delete()
		' Safeguard against incomplete enumeration
		If _mutex Then _mutex.Unlock
	End Method
	
	Method HasNext%()
		If Super.HasNext() Then Return True
		If _mutex
			_mutex.Unlock
			_mutex = Null
		End If
		Return False
	End Method
	
End Type
?

Rem
bbdoc: Create a synchronized map
returns: A new map object
End Rem
Function CreateSynchronizedMap:TSynchronizedMap()
	Return New TSynchronizedMap
End Function
