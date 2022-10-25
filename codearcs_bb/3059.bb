; ID: 3059
; Author: Madk
; Date: 2013-06-21 18:23:07
; Title: 2D spatial hash
; Description: Data type useful for storing information in an arbitrarily-sized discrete 2D space

' 	--+-----------------------------------------------------------------------------------------+--
'	  | This code was originally written by Sophie Kirschner (meapineapple@gmail.com) and it is |  
' 	  | released as public domain. Please do not interpret that as liberty to claim credit that |  
' 	  | is not yours, or to sell this code when it could otherwise be obtained for free because |  
'	  |                    that would be a really shitty thing of you to do.                    |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict

Import brl.linkedlist


' Example code
Rem

' Create a new spatial hash
Local hash:hash2d=hash2d.Create(256,32)

' Add a bunch of numbers as strings to a rectangle of coordinates.
Local n%=0
For Local i%=2 Until 4
For Local j%=0 Until 8
	hash.insert i,j,String(n)
	n:+1
Next
Next

' Resize it, just for fun.
hash.resize(48,8)

' Iterate through the hash and print the values.
For Local t$=EachIn hash
	Print t
Next

EndRem


	Rem
	bbdoc: Spatial hash type.
	End Rem
Type hash2d
	' number of buckets in the hash
	Field size%
	' used in placing coordinates in the hash
	Field pitch%
	' number of objects in the hash
	Field count%
	' the buckets
	Field content:TList[]
	
	Rem
	bbdoc: Creates a new hash2d object.
	returns: A new hash2d object.
	End Rem
	Function Create:hash2d(size%,pitch%=128)
		Local n:hash2d=New hash2d
		n.setsize(size)
		n.pitch=pitch
		Return n
	End Function
	Rem
	bbdoc: Sets the size of the hash2d object. Deletes all existing values.
	End Rem
	Method setsize(nsize%)
		size=nsize
		content=New TList[size]
		count=0
	End Method
	Rem
	bbdoc: Sets the size of the hash2d object. Retains all existing values.
	End Rem
	Method resize(nsize%,npitch%=-1)
		If count=0
			setsize(nsize)
			pitch=npitch
		Else
			Local newc:TList[]=New TList[nsize]
			size=nsize
			pitch=npitch
			For Local i%=0 Until content.length
				If content[i] Then
					For Local node:hash2dnode=EachIn content[i]
						Local nindex%=contentindex(node.x,node.y)
						If Not newc[nindex] Then newc[nindex]=New TList
						newc[nindex].addlast node
					Next
				EndIf
			Next
			content=newc
		EndIf
	End Method
	Rem
	bbdoc: Find a node.
	returns: The node at the specified coordinates; null if none exists.
	End Rem
	Method findnode:hash2dnode(x%,y%)
		Assert content
		Local index%=contentindex(x,y)
		Local list:TList=content[index]
		If list Then
			Local link:TLink=list._head._succ
			While link<>content[index]._head
				Local node:hash2dnode=hash2dnode(link._value)
				If node.x=x And node.y=y Then
					link._pred._succ=link._succ
					link._succ._pred=link._pred
					link._succ=list._head._succ
					link._pred=list._head
					list._head._succ._pred=link
					list._head._succ=link
					Return node
				EndIf
				link=link._succ
			Wend
		EndIf
		Return Null
	End Method
	Rem
	bbdoc: Find a value.
	returns: The value at the specified coordinates; null if none exists.
	End Rem
	Method find:Object(x%,y%)
		Local node:hash2dnode=findnode(x,y)
		If node Then
			Return node.value
		Else
			Return Null
		EndIf
	End Method
	Rem
	bbdoc: Insert a value.
	returns: The node created to contain the value.
	End Rem
	Method insert:hash2dnode(x%,y%,value:Object)
		Assert content
		Local index%=contentindex(x,y)
		Local node:hash2dnode=hash2dnode.Create(x,y,value)
		If Not content[index] Then content[index]=CreateList()
		content[index].addfirst node
		count:+1
		Return node
	End Method
	Rem
	bbdoc: Remove a node.
	returns: True if the node was successfully removed, false otherwise.
	End Rem
	Method removenode%(node:hash2dnode)
		Assert content And node
		Local index%=contentindex(node.x,node.y)
		Local list:TList=content[index]
		If list.remove(node)
			count:-1
			If list.isempty() content[index]=Null
			Return True
		Else
			Return False
		EndIf
	End Method
	Rem
	bbdoc: Remove the value at a coordinate.
	returns: The node that was removed; null if no such node existed.
	End Rem
	Method remove:hash2dnode(x%,y%)
		Local node:hash2dnode=findnode(x,y)
		If node Then
			If Not removenode(node) Then node=Null
		EndIf
		Return node
	End Method
	' Returns the bucket index for a given coordinate.
	Method contentindex%(x%,y%)
		Return (((x+y*pitch) Mod size)+size) Mod size
	End Method
	Rem
	bbdoc: Implements EachIn support for values.
	returns: An iterator object.
	End Rem
	Method ObjectEnumerator:hash2denum()
		Return hash2denum.Create(Self,0)
	End Method
	Rem
	bbdoc: Implements EachIn support for nodes.
	returns: An iterator object.
	End Rem
	Method NodeEnumerator:hash2denum()
		Return hash2denum.Create(Self,1)
	End Method
End Type

	Rem
	bbdoc: Hash2d node type.
	End Rem
Type hash2dnode
	Field x%,y%
	Field value:Object
	Function Create:hash2dnode(x%,y%,value:Object)
		Local n:hash2dnode=New hash2dnode
		n.x=x;n.y=y;n.value=value
		Return n
	End Function
End Type

	Rem
	bbdoc: Enumerator type for EachIn support.
	End Rem
Type hash2denum
	Field hashindex%,nextindex%,hash:hash2d,link:TLink
	Field nodeenum%=0
	Function Create:hash2denum(hash:hash2d,nodeenum%)
		Local n:hash2denum=New hash2denum
		n.hash=hash
		n.nodeenum=nodeenum
		n.init
		Return n
	End Function
	Method init()
		hashindex=-1;nextindex=-1
		Local index%=0
		Repeat
			If hash.content[index] And (Not hash.content[index].isempty())
				If hashindex=-1 Then
					hashindex=index
					link=hash.content[index]._head._succ
				Else
					nextindex=index
					Exit
				EndIf
			EndIf
			index:+1
			If index>=hash.size Then Exit
		Forever
	End Method
	Method HasNext%()
		Return nextindex>-1 Or (link And link<>link._value)
	End Method
	Method NextObject:Object()
		Assert link
		Local value:Object=link._value
		link=link._succ
		If link=link._value And nextindex>-1 Then
			hashindex=nextindex
			link=hash.content[hashindex]._head._succ
			Repeat
				nextindex:+1
				If nextindex>=hash.size Then nextindex=-1;Exit
				If hash.content[nextindex] And (Not hash.content[nextindex].isempty()) Then Exit
			Forever
		EndIf
		If nodeenum
			Return value
		Else
			Return hash2dnode(value).value
		EndIf
	End Method
	Method ObjectEnumerator:hash2denum()
		Return Self
	End Method
End Type
