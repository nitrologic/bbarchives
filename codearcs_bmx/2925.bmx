; ID: 2925
; Author: col
; Date: 2012-02-29 08:57:09
; Title: Basic Stack Type
; Description: A basic TStack type

Type TNode	
	Field _next:TNode
	Field _item:Object
EndType

Type TStack	
	Field _head:TNode = New TNode
	Field _itemcount
	
	Method Push(item:Object)
		Assert item Else "Trying to push Null object to TStack!"

		Local node:TNode = New TNode
		node._item = item
		node._next = _head._next
		_head._next = node

		_itemcount :+ 1			
	EndMethod

	Method Pop:Object()
		Local node:TNode
		node = _head._next

		If Not node Return Null
		
		_head._next = node._next
		_itemcount :- 1
		Return node._item
	EndMethod
		
	Method Peek:Object()
		If _head._next
			Return _head._next._item
		EndIf
	EndMethod
	
	Method ItemCount()
		Return _itemcount
	EndMethod
EndType

'Wrapper functions
Function CreateStack:TStack()
	Local Stack:TStack = New TStack
	Return Stack
EndFunction

Function StackPush(Stack:TStack,Item:Object)
	Stack.Push Item
EndFunction

Function StackPop:Object(Stack:TStack)
	Return Stack.Pop()
EndFunction

Function StackPeek:Object(Stack:TStack)
	Return Stack.Peek()
EndFunction

Function StackCount(Stack:TStack)
	Return Stack.Itemcount()
EndFunction
