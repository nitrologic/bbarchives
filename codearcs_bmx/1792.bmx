; ID: 1792
; Author: Dreamora
; Date: 2006-08-22 07:42:11
; Title: TListExtended
; Description: extended TList type with powerfull additional features.

Test App
'----------------------------------
Import "tlistextended.bmx"


Strict

Global list:tlistextended	= New tlistextended
SeedRnd MilliSecs()

For Local i:Int = 1 To 10

	list.addlast(String(Rand(0,25)))

Next
'list.ResetEnumeratorRange()

Print "Listcount: " + list.count()


Print "~n~nTestoutput: Nodes"
For Local temp:TLink	= EachIn list.links()
	Print temp.toString() + " with value: " + temp.value().toString()
Next

list.reverse()
Print "~n~nTestoutput: Nodes, inverted!"
For Local temp:TLink	= EachIn list.links()
	Print temp.toString() + " with value: " + temp.value().toString()
Next

list.sort(True,compareNumbers)
list.ResetEnumeratorRange()		' This is needed as the whole list is sorted
Print "~n~nTestoutput: Values, sorted ascending!"
For Local value:Object = EachIn list.values()
	Print value.tostring()
Next

list.sort(False,compareNumbers)
list.ResetEnumeratorRange()		' This is needed as the whole list is sorted
Print "~n~nTestoutput: Values, sorted descending!"
For Local value:Object = EachIn list.values()
	Print value.tostring()
Next


Print "~n~nTestoutput: Enumerator Range from 3rd to 8th element"
list.SetEnumeratorRange(list.FirstLink().NextLink().NextLink(),list.lastlink().PrevLink().PrevLink())
For Local value:Object = EachIn list.values()
	Print value.tostring()
Next

Print "~n~nTestoutput: Manual pointer movement from start"
list.MoveStart()
Print "Start: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()
list.MoveForward()
Print "Step Forward: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()
list.MoveForward()
Print "Step Forward: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()
list.MoveBackward()
Print "Step Backward: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()
list.MoveBackward()
Print "Step Backward: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()

Print "~n~nTestoutput: Manual pointer movement from end"
list.MoveEnd()
Print "Start: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()
list.MoveBackward()
Print "Step Backward: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()
list.MoveBackward()
Print "Step Backward: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()
list.MoveForward()
Print "Step Forward: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()
list.MoveForward()
Print "Step Forward: " + list.CurrentLink().tostring() + " with value: " + list.CurrentValue().tostring()




Local b:Int = GCMemAlloced()
list = Null
GCCollect()
Print "Memory: " + b + " compared to now: " + GCMemAlloced()


Function compareNumbers:Int(one:Object, two:Object)
	Return Int(one.tostring())-Int(two.tostring())
End Function

'-----------------------------------------------------




TListExtended.bmx
'-----------------------------------------------------
Strict
Import brl.linkedlist

rem	
	Created by Marc 'Dreamora' Schärer
	Moderator at the german blitzbasic community
End Rem
Rem
    bbdoc: TListExtended
    about: Extended version of BRLs TList <br>
		Eachin still behaves the same as on the original list, this means without support for enumeration ranges.<br>
		Enumeration ranges only work on eachin with list.values() and list.links()
end rem	
Type TListExtended Extends TList
    Field _iterator:TLink
	
	Field _enumStart:TLink
	Field _enumStop:TLink
    
	Method New()
		ResetEnumeratorRange()
                _iterator	= _head
	End Method
	
	Rem
        bbdoc: Clear
        about: Clears the extended TList and resets the enumeration range
    End Rem
    Method Clear()
        _iterator   = Null
		_enumStart	= Null
		_enumStop	= Null
		
		Super.clear()
    End Method

	Rem
		bbdoc: Swap
		about: Swaps the content of 2 lists. if used on extended lists, their enumeration range will be reseted.
	End Rem
	Method Swap( list:TList )
		Super.Swap(list)
		If TListExtended(Self)		ResetEnumeratorRange()
		If TListExtended(list)		TListExtended(list).ResetEnumeratorRange()
	End Method
    
	Method Reverse()
		Super.Reverse()
		Local t:TLink	= _enumStart
		_enumStart	= _enumStop
		_enumStop	= t
	End Method
	
	Rem
        bbdoc: Links
        about: TLink enumerator
    End Rem
    Method Links:TLLEnumer()
		Local enum:TLLEnumer	= New TLLEnumer
		enum._enum				= New TLinkEnumIn
		If _enumStart = _head Or _enumStop = _head		ResetEnumeratorRange()
		TLinkEnumIn(enum._enum)._link		= _enumStart
		TLinkEnumIn(enum._enum)._stop		= _enumStop
		Return enum
    End Method

	Rem
        bbdoc: Values
        about: Value enumerator
    End Rem
    Method Values:TLLEnumer()
		Local enum:TLLEnumer	= New TLLEnumer
		enum._enum				= New TValueEnumIn
		If _enumStart = _head Or _enumStop = _head		ResetEnumeratorRange()
		TValueEnumIn(enum._enum)._link		= _enumStart
		TValueEnumIn(enum._enum)._stop		= _enumStop
		Return enum
    End Method

	Rem
		bbdoc: SetEnumeratorRange
		about: Sets the enumerator for eachin as well as sorting<br>
	End Rem
	Method SetEnumeratorRange(start:TLink, stop:TLink)
		_enumStart	= start
		_enumStop	= stop
	End Method
	
	Rem
		bbdoc: ResetEnumeratorRange()
		about: Resets the enumeration range to "from start to end of list" <br>
				This must be called if you call sort on a whole list.
	End Rem
	Method ResetEnumeratorRange()
		_enumStart	= _head._succ
		_enumStop	= _head._pred
	End Method
    
	Rem
        bbdoc: MoveStart
        about: Moves the iteration reference to the start of the list
    End Rem
    Method MoveStart()
		_iterator	= _head._succ
    End Method

	Rem
        bbdoc: MoveEnd
        about: Moves the iteration reference to the end of the list
    End Rem
    Method MoveEnd()
		_iterator	= _head._pred
    End Method

	Rem
        bbdoc: MoveForward
        about: Moves the iteration reference one position forward in the list
		returns: true if moving was possible, false otherwise
    End Rem
    Method MoveForward:Int()
		If _iterator._succ <> _head	
			_iterator	= _iterator._succ
			Return True
		EndIf
		Return False
		
    End Method

	Rem
        bbdoc: MoveBackward
        about: Moves the iteration reference one position backward in the list
		returns: true if moving was possible, false otherwise
    End Rem
    Method MoveBackward:Int()
		If _iterator._pred <> _head	
			_iterator	= _iterator._pred
			Return True
		EndIf
		Return False
    End Method

	Rem
		bbdoc: CurrentValue
		about: Returns the value of the link the iterator is currently pointing to
	End Rem
    Method CurrentValue:Object()
		Return _iterator.Value()
    End Method

	Rem
		bbdoc: CurrentLink
		about: Returns the reference to the link the iterator is currently pointing to
	End Rem
    Method CurrentLink:TLink()
		Return _iterator
    End Method
	
End Type

Type TLLEnumer
	Field _enum:TListEnumIn
	
	Method ObjectEnumerator:TListEnumIn()
		Return _enum
	End Method
End Type



Type TListEnumIn Extends TListEnum
	Field _stop:TLink

	Method HasNext()
		Return _link<>_link._succ And _link._pred <> _stop
	End Method

	Method NextObject:Object()
		Local value:Object=_link.value()
        _link   = _link._succ
		Return value
	End Method
End Type

Type TLinkEnumIn Extends TListEnumIn 
    Method NextObject:Object()
		Local value:Object=_link
        _link   = _link._succ
		Return value
	End Method
End Type

Type TValueEnumIn Extends TListEnumIn 

    Method NextObject:Object()
		Local value:Object=_link.value()
        _link   = _link._succ
		Return value
	End Method
End Type
