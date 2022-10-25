; ID: 3081
; Author: Pineapple
; Date: 2013-09-24 13:37:27
; Title: Remove redundant values from a TList
; Description: Iterates through and removes duplicates in a linked list

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


SuperStrict

Import brl.linkedlist

Function RemoveListDupes(list:TList)
	Local link:TLink=list._head._succ
	While link<>list._head
		Local link2:TLink=link._succ
		While link2<>list._head
			Local nlink2:TLink=link2._succ
			If link._value=link2._value Then link2.remove()
			link2=nlink2
		Wend
		link=link._succ
	Wend
End Function
