; ID: 3142
; Author: Pineapple
; Date: 2014-09-17 08:41:29
; Title: Hashtable with integer keys and values
; Description: Simple but hopefully useful, provides 1:1 mapping for ints

' 	--+-----------------------------------------------------------------------------------------+--
'	  | This code was originally written by Sophie Kirschner (meapineapple@gmail.com) and it is |  
' 	  | released as public domain. Please do not interpret that as liberty to claim credit that |  
' 	  | is not yours, or to sell this code when it could otherwise be obtained for free because |  
'	  |                    that would be a really shitty thing of you to do.                    |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict

Import brl.linkedlist


' Example program

Rem

' Create the hash
Local hash:inthash=inthash.Create(32)

' For each index up to 128, insert index*4 as the associated value
For Local i%=0 Until 128
	hash.insert i,i*4
Next

' Print some examples
Print "Sampling some inserted values..."
Print "4 * 4 = "+hash.find(4)
Print "4 * 12 = "+hash.find(12)
Print "4 * 100 = "+hash.find(100)

' Demonstrate removal
Print "Removing value at index 8..."
hash.remove(8)
Print "Retrieving: 4 * 8 = "+hash.find(8)

EndRem


' class for mapping ints to other ints
Type inthash
	' contains the data
	Field bucket:TList[]
	' thrown error message that hopefully you'll never have to see
	Const accesserror$="inthash buckets not initialized, use setsize before accessing contents"
	' returns a new inthash of specified size
	Function Create:inthash(size%)
		Local n:inthash=New inthash
		n.setsize(size)
		Return n
	End Function
	' set inthash bucket array size
	Method setsize(size%)
		bucket=New TList[size]
	End Method
	' clear hash
	Method clear()
		For Local i%=0 Until bucket.length
			bucket[i]=Null
		Next
	End Method
	' create copy of inthash
	Method copy:inthash()
		Local c:inthash=New inthash
		c.setsize(bucket.length)
		For Local i%=0 Until bucket.length
			If bucket[i]
				c.bucket[i]=New TList
				For Local node:inthashnode=EachIn bucket[i]
					Local add:inthashnode=inthashnode.Create(node.index,node.value)
					add.link=c.bucket[i].addlast(add)
				Next
			EndIf
		Next
		Return c
	End Method
	' insert value at index
	Method insert:inthashnode(index%,value%)
		Assert bucket,accesserror
		Local i%=Abs(index Mod bucket.length)
		If Not bucket[i] bucket[i]=New TList
		Local node:inthashnode=inthashnode.Create(index,value)
		node.link=bucket[i].addlast(node)
		Return node
	End Method
	' retrieve value at index (returns 0 if none exists)
	Method find%(index%)
		Local node:inthashnode=findnode(index)
		If node Return node.value
		Return 0
	End Method
	' removes node for index (returns 1 if successful, 0 if none existed)
	Method remove%(index%)
		Local node:inthashnode=findnode(index)
		If node node.remove;Return 1
		Return 0
	End Method
	' retrieve node for index (returns null if none exists)
	Method findnode:inthashnode(index%)
		Assert bucket,accesserror
		Local i%=Abs(index Mod bucket.length)
		If bucket[i]
			For Local n:inthashnode=EachIn bucket[i]
				If n.index=index Return n
			Next
		EndIf
		Return Null
	End Method
End Type

' inthash contains inthashnodes in each bucket
Type inthashnode
	' index and associated value
	Field index%,value%
	' list link recorded for swift removal
	Field link:TLink
	' returns a new inthashnode with specified arguments
	Function Create:inthashnode(index%,value%)
		Local n:inthashnode=New inthashnode
		n.index=index;n.value=value
		Return n
	End Function
	' remove the inthashnode from its bucket
	Method remove()
		If link link.remove
	End Method
End Type
