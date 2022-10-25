; ID: 3067
; Author: Pineapple
; Date: 2013-08-26 14:40:00
; Title: ShuffleArray and ShuffleList
; Description: Randomize the order of elements in an array or linked list

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict

Import brl.linkedlist
Import brl.random
Import "mergesortarray.bmx"	' http://blitzbasic.com/codearcs/codearcs.php?code=3066


' Example code

Rem

Import brl.standardio

SeedRnd Millisecs()

' Generate a list of strings containing the first few letters of the alphabet
Local stringlist:TList=CreateList()
For Local a%=Asc("A") To Asc("F")
	stringlist.addlast Chr(a)
Next

' Shuffle the list
Local shuffledlist:TList=ShuffleList(stringlist)

' Display the results
Print "~nList before ShuffleList:"
For Local str$=EachIn stringlist
	Print str
Next
Print "~nList after ShuffleList:"
For Local str$=EachIn shuffledlist
	Print str
Next

EndRem


' Takes a list as input (and optional arguments for a sorting function that you shouldn't need to touch) and returns a new list with the same contents in a random order.
' The algorithm should be reasonably fast and without any bias.

Function ShuffleList:TList(list:TList,sortfunc(array:Object[],ascending%,comparefunc(o1:Object,o2:Object))=MergeSortArray,comparefunc(o1:Object,o2:Object)=_Array_CompareObjects)
	Local listlength%=list.count()
	Local nodes:ShuffleNode[]=New ShuffleNode[listlength]
	Local listindex%=0
	For Local obj:Object=EachIn list
		nodes[listindex]=New shufflenode
		nodes[listindex].value=obj
		listindex:+1
	Next
	sortfunc(nodes,1,comparefunc)
	Local retlist:TList=CreateList()
	For Local index%=0 Until nodes.length
		retlist.addlast nodes[index].value
	Next
	Return retlist
End Function

Function ShuffleArray:Object[](array:Object[],sortfunc(array:Object[],ascending%,comparefunc(o1:Object,o2:Object))=MergeSortArray,comparefunc(o1:Object,o2:Object)=_Array_CompareObjects)
	Local nodes:ShuffleNode[]=New ShuffleNode[array.length]
	Local listindex%=0
	For Local obj:Object=EachIn array
		nodes[listindex]=New shufflenode
		nodes[listindex].value=obj
		listindex:+1
	Next
	sortfunc(nodes,1,comparefunc)
	Local retarray:Object[]=New Object[array.length]
	For Local index%=0 Until nodes.length
		retarray[index]=nodes[index].value
	Next
	Return retarray
End Function

Type ShuffleNode
	Field value:Object
	Field num!=Rnd()
	Method compare%(obj:Object)
		Local node:ShuffleNode=ShuffleNode(obj)
		If num>node.num Return 1
		Return -1
	End Method
End Type
