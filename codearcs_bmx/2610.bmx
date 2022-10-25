; ID: 2610
; Author: beanage
; Date: 2009-11-15 15:18:48
; Title: Binary Tree with Integer Keys/ Values
; Description: Binary tree with integer Key/ Values, currently only for little Endian.

'------------------------------------------------------------------
'Simple binary tree implementation (32b little endian integer keys)
'------------------------------------------------------------------

'#####################
'      C 2009 by
'    B.e.A.n.A.g.e.
'       L.a.b.s.
'#####################

'//////////
SuperStrict
'//////////

'------------------------------------------------------------------
Rem
bbdoc: Simple binary tree implementation (32b little endian integer keys)
about: Int->Int binary tree helper type module, coded by BeAnAge Labs for free use.
endrem
Module beanage.BTree

ModuleInfo "Version: 1.1.00"
ModuleInfo "License: GNU GPL"
ModuleInfo "Copyright: BeAnAge Labs 2010"
ModuleInfo "Author: Joseph Birkner"
ModuleInfo "Modserver: beanage"

ModuleInfo "History: 1.0 < Release"
ModuleInfo "History: 1.1 < Added BTreeGetNumElements(); Reformatted code"
'------------------------------------------------------------------

'///////
'Private
'///////

Type BTree

	Field _0:BTree
	Field _1:BTree
	Field _count:Int = 0
	Field _key:Int = 0
	Field _value:Int = 0
	Field _level:Int = -1
	
	Method _Insert0( key_:Int, value_:Int )
		If _0
			_0._Insert key_, value_
		Else
			_0 = New BTree
			_0._key = _key
			_0._level = _level+ 1
			_0._Insert key_, value_
			
		End If
	End Method
	
	Method _Insert1( key_:Int, value_:Int )
		If _1
			_1._Insert key_, value_
		Else
			_1 = New BTree
			_1._key = _key| ( 1 Shl ( _level+ 1 ) )
			_1._level = _level+ 1
			_1._Insert key_, value_
			
		End If
	End Method
	
	Method _ValueForKey:Int( key_:Int ) 'key here shifts right by level
		If ( key_& ( _key Shr _level ) ) = key_ Then Return _value
		Local next_	:Int	= key_ Shr ( _level> -1 )
		
		If ( next_& 1 ) And _1
			Return _1._ValueForKey( next_ )
			
		ElseIf _0
			If _level	= 31 Then DebugStop; Return False
			Return _0._ValueForKey( next_ )
			
		End If
		Return False
	End Method
	
	Method _Insert( key_:Int,value_:Int )
		_count :+ 1
		If _key = key_ Then _value= value_; Return
		Local next_	:Int	= key_ Shr ( ( _level> -1 )* ( _level+ 1 ) )
		
		If next_& 1
			_Insert1 key_, value_
		Else
			_Insert0 key_, value_
		End If
	End Method
	
End Type

'//////
'Public
'//////

Function CreateBTree:BTree()
	Return New BTree
End Function

Function BTreeInsert( tree_:BTree, key_:Int, value_:Int )
	tree_._Insert key_, value_
End Function

Function BTreeValueForKey:Int( tree_:BTree, key_:Int )
	Return tree_._ValueForKey( key_ )
End Function

Function BTreeGetNumElements:Int( tree_:BTree )
	Return tree_._count
End Function
