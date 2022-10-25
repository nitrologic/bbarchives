; ID: 2456
; Author: Warpy
; Date: 2009-04-10 11:29:09
; Title: Number maps
; Description: Maps you can put numbers in

Strict

Private

Global nil:TNumberNode=New TNumberNode

nil._color=TNumberMap.BLACK
nil._parent=nil
nil._left=nil
nil._right=nil

Public

Type TKeyValue


End Type

Type TNumberNode
	Method Key:Object()
		Return _key
	End Method
	
	Method Value:Double()
		Return _value
	End Method
	
	Method nextnode:TNumberNode()
		Local node:TNumberNode=Self
		If node._right<>nil
			node=_right
			While node._left<>nil
				node=node._left
			Wend
			Return node
		EndIf
		Local parent:TNumberNode=_parent
		While node=parent._right
			node=parent
			parent=parent._parent
		Wend
		Return parent
	End Method
	
	Method PrevNode:TNumberNode()
		Local node:TNumberNode=Self
		If node._left<>nil
			node=node._left
			While node._right<>nil
				node=node._right
			Wend
			Return node
		EndIf
		Local parent:TNumberNode=node._parent
		While node=parent._left
			node=parent
			parent=node._parent
		Wend
		Return parent
	End Method
	
	Method Clear()
		_parent=Null
		If _left<>nil _left.Clear
		If _right<>nil _right.Clear
	End Method
	
	Method Copy:TNumberNode( parent:TNumberNode )
		Local t:TNumberNode=New TNumberNode
		t._key=_key
		t._value=_value
		t._color=_color
		t._parent=parent
		If _left<>nil t._left=_left.Copy( t )
		If _right<>nil t._right=_right.Copy( t )
		Return t
	End Method
	
	'***** PRIVATE *****
	
	Field _color,_parent:TNumberNode=nil,_left:TNumberNode=nil,_right:TNumberNode=nil

	'***** PRIVATE *****

	Field _key:Object,_value:Double


End Type

Type TNumberNodeEnumerator
	Method HasNext()
		Return _node<>nil
	End Method
	
	Method NextObject:Object()
		Local node:TNumberNode=_node
		_node=_node.nextnode()
		Return node
	End Method

	'***** PRIVATE *****
		
	Field _node:TNumberNode	
End Type

Type TKeyEnumerator Extends TNumberNodeEnumerator
	Method NextObject:Object()
		Local node:TNumberNode=_node
		_node=_node.nextnode()
		Return node._key
	End Method
End Type

Rem
Type TValueEnumerator Extends TNumberNodeEnumerator
	Method NextObject:Object()
		Local node:TNumberNode=_node
		_node=_node.nextnode()
		Return node._value
	End Method
End Type
EndRem

Type TNumberMapEnumerator
	Method ObjectEnumerator:TNumberNodeEnumerator()
		Return _enumerator
	End Method
	Field _enumerator:TNumberNodeEnumerator
End Type


'***** PUBLIC *****

Type TNumberMap

	Method Delete()
		Clear
	End Method

	Method Clear()
		If _root=nil Return
		_root.Clear
		_root=nil
	End Method
	
	Method IsEmpty()
		Return _root=nil
	End Method
	
	Method Insert( key:Object,value:Double )

		Assert key Else "Can't insert Null key into map"

		Local node:TNumberNode=_root,parent:TNumberNode=nil,cmp
		
		While node<>nil
			parent=node
			cmp=key.Compare( node._key )
			If cmp>0
				node=node._right
			Else If cmp<0
				node=node._left
			Else
				node._value=value
				Return
			EndIf
		Wend
		
		node=New TNumberNode
		node._key=key
		node._value=value
		node._color=RED
		node._parent=parent
		
		If parent=nil
			_root=node
			Return
		EndIf
		If cmp>0
			parent._right=node
		Else
			parent._left=node
		EndIf
		
		_InsertFixup node
	End Method
	
	Method Contains( key:Object )
		Return _FindNode( key )<>nil
	End Method

	Method ValueForKey:Double( key:Object )
		Local node:TNumberNode=_FindNode( key )
		If node<>nil Return node._value
	End Method
	
	Method Remove( key:Object )
		Local node:TNumberNode=_FindNode( key )
		If node=nil Return 0
		 _RemoveNode node
		Return 1
	End Method
	
	Method Keys:TNumberMapEnumerator()
		Local nodeenum:TNumberNodeEnumerator=New TKeyEnumerator
		nodeenum._node=_FirstNode()
		Local mapenum:TNumberMapEnumerator=New TNumberMapEnumerator
		mapenum._enumerator=nodeenum
		Return mapenum
	End Method
	
	Method ToArray:Double[]()
		Local o:Double[]
		Local n:TNumberNode=_FirstNode()
		While n<>nil
			o:+[n._value]
			n=n.nextnode()
		Wend
		Return o
	End Method
	
	Rem
	Method Values:TNumberMapEnumerator()
		Local nodeenum:TNumberNodeEnumerator=New TValueEnumerator
		nodeenum._node=_FirstNode()
		Local mapenum:TNumberMapEnumerator=New TNumberMapEnumerator
		mapenum._enumerator=nodeenum
		Return mapenum
	End Method
	EndRem
	
	Method Copy:TNumberMap()
		Local map:TNumberMap=New TNumberMap
		map._root=_root.Copy( nil )
		Return map
	End Method
	
	Rem
	Method ObjectEnumerator:TNumberNodeEnumerator()
		Local nodeenum:TNumberNodeEnumerator=New TNumberNodeEnumerator
		nodeenum._node=_FirstNode()
		Return nodeenum
	End Method
	EndRem
	
	'***** PRIVATE *****
	
	Method _FirstNode:TNumberNode()
		Local node:TNumberNode=_root
		While node._left<>nil
			node=node._left
		Wend
		Return node
	End Method
	
	Method _LastNode:TNumberNode()
		Local node:TNumberNode=_root
		While node._right<>nil
			node=node._right
		Wend
		Return node
	End Method
	
	Method _FindNode:TNumberNode( key:Object )
		Local node:TNumberNode=_root
		While node<>nil
			Local cmp=key.Compare( node._key )
			If cmp>0
				node=node._right
			Else If cmp<0
				node=node._left
			Else
				Return node
			EndIf
		Wend
		Return node
	End Method
	
	Method _RemoveNode( node:TNumberNode )
		Local splice:TNumberNode,child:TNumberNode
		
		If node._left=nil
			splice=node
			child=node._right
		Else If node._right=nil
			splice=node
			child=node._left
		Else
			splice=node._left
			While splice._right<>nil
				splice=splice._right
			Wend
			child=splice._left
			node._key=splice._key
			node._value=splice._value
		EndIf
		Local parent:TNumberNode=splice._parent
		If child<>nil
			child._parent=parent
		EndIf
		If parent=nil
			_root=child
			Return
		EndIf
		If splice=parent._left
			parent._left=child
		Else
			parent._right=child
		EndIf
		
		If splice._color=BLACK _DeleteFixup child,parent
	End Method
	
	Method _InsertFixup( node:TNumberNode )
		While node._parent._color=RED And node._parent._parent<>nil
			If node._parent=node._parent._parent._left
				Local uncle:TNumberNode=node._parent._parent._right
				If uncle._color=RED
					node._parent._color=BLACK
					uncle._color=BLACK
					uncle._parent._color=RED
					node=uncle._parent
				Else
					If node=node._parent._right
						node=node._parent
						_RotateLeft node
					EndIf
					node._parent._color=BLACK
					node._parent._parent._color=RED
					_RotateRight node._parent._parent
				EndIf
			Else
				Local uncle:TNumberNode=node._parent._parent._left
				If uncle._color=RED
					node._parent._color=BLACK
					uncle._color=BLACK
					uncle._parent._color=RED
					node=uncle._parent
				Else
					If node=node._parent._left
						node=node._parent
						_RotateRight node
					EndIf
					node._parent._color=BLACK
					node._parent._parent._color=RED
					_RotateLeft node._parent._parent
				EndIf
			EndIf
		Wend
		_root._color=BLACK
	End Method
	
	Method _RotateLeft( node:TNumberNode )
		Local child:TNumberNode=node._right
		node._right=child._left
		If child._left<>nil
			child._left._parent=node
		EndIf
		child._parent=node._parent
		If node._parent<>nil
			If node=node._parent._left
				node._parent._left=child
			Else
				node._parent._right=child
			EndIf
		Else
			_root=child
		EndIf
		child._left=node
		node._parent=child
	End Method
	
	Method _RotateRight( node:TNumberNode )
		Local child:TNumberNode=node._left
		node._left=child._right
		If child._right<>nil
			child._right._parent=node
		EndIf
		child._parent=node._parent
		If node._parent<>nil
			If node=node._parent._right
				node._parent._right=child
			Else
				node._parent._left=child
			EndIf
		Else
			_root=child
		EndIf
		child._right=node
		node._parent=child
	End Method
	
	Method _DeleteFixup( node:TNumberNode,parent:TNumberNode )
		While node<>_root And node._color=BLACK
			If node=parent._left
				Local sib:TNumberNode=parent._right
				If sib._color=RED
					sib._color=BLACK
					parent._color=RED
					_RotateLeft parent
					sib=parent._right
				EndIf
				If sib._left._color=BLACK And sib._right._color=BLACK
					sib._color=RED
					node=parent
					parent=parent._parent
				Else
					If sib._right._color=BLACK
						sib._left._color=BLACK
						sib._color=RED
						_RotateRight sib
						sib=parent._right
					EndIf
					sib._color=parent._color
					parent._color=BLACK
					sib._right._color=BLACK
					_RotateLeft parent
					node=_root
				EndIf
			Else	
				Local sib:TNumberNode=parent._left
				If sib._color=RED
					sib._color=BLACK
					parent._color=RED
					_RotateRight parent
					sib=parent._left
				EndIf
				If sib._right._color=BLACK And sib._left._color=BLACK
					sib._color=RED
					node=parent
					parent=parent._parent
				Else
					If sib._left._color=BLACK
						sib._right._color=BLACK
						sib._color=RED
						_RotateLeft sib
						sib=parent._left
					EndIf
					sib._color=parent._color
					parent._color=BLACK
					sib._left._color=BLACK
					_RotateRight parent
					node=_root
				EndIf
			EndIf
		Wend
		node._color=BLACK
	End Method
	
	Const RED=-1,BLACK=1
	
	Field _root:TNumberNode=nil
	
End Type
