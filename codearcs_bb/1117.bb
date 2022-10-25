; ID: 1117
; Author: dmaz
; Date: 2004-07-24 16:33:45
; Title: Simple Linked Lists
; Description: A set of functions to help with list management

; -BList.bb---------------------------------------------------------------------------------------------------
; created: July 2004
; version: 1.1
; author: dmaz
; Public Domain, use as you wish.
;
; userTypePtr% = AddFront%( list.Blist, userTypePtr% )
; userTypePtr% = AddBack%( list.Blist, userTypePtr% )
; trueFalse% = ResetList%( list.BList )
; userTypePtr% = NextItem%( list.BList )
; userTypePtr% = PrevItem%( list.BList )
; userTypePtr% = FirstItem%( list.BList )
; userTypePtr% = LastItem%( list.BList )
; userTypePtr% = CurrentItem%( list.BList )
; userTypePtr% = MoveItem%( fromList.BList, toList.BList )
; userTypePtr% = KillItem%( list.BList, node.BNode )
; node.BNode = CurrentNode.BNode( list.Blist )
; totalItems% = TotalItems%( list.BList )

Type BNode
	Field prevNode.BNode
	Field nextNode.BNode
	Field userTypePtr%
End Type

Type BList
	Field firstNode.BNode
	Field lastNode.BNode
	Field currentNode.BNode
	Field totalNodes%
End Type

; ------------------------------------------------------------------------------------------------------------

; AddFront%( list.Blist, userTypePtr% )
; Discription:
;     Inserts object at the beginning of the list.  AddFront() DOES NOT change
;     the 'current item' pointer to the newly added object.  The Parameter
;     userTypePtr should be passed using the Blitz Basic command 'Handle()'.
; Returns:
;     Pointer to the newly added user defined object.
; Example:
;     AddFront(myList,Handle(New MyUserType))
Function AddFront%( list.BList, userTypePtr% )
	If list = Null Or userTypePtr = 0 Then Return False

	n.BNode = New BNode

	If list\firstNode = Null	; list is empty
		n\prevNode = Null
		n\nextNode = Null		
		list\firstNode = n
		list\lastNode = n
	Else								; list is not empty
		n\prevNode = Null
		n\nextNode = list\firstNode
		list\firstNode\prevNode = n
		list\firstNode = n
	EndIf
		
	n\userTypePtr = userTypePtr
	list\totalNodes = list\totalNodes+1
	Return n\userTypePtr
End Function

; AddBack%( list.Blist, userTypePtr% )
; Discription:
;     Attaches object at the end of the list.  AddBack DOES NOT change
;     the 'current item' pointer to the newly added object.  The Parameter
;     userTypePtr should be passed using the Blitz Basic command 'Handle()'.
; Returns:
;     Pointer to the newly added user defined object.
; Example:
;     AddBack(myList,Handle(New MyUserType))
Function AddBack%( list.BList, userTypePtr% )
	If list = Null Or userTypePtr = 0 Then Return False
	
	n.BNode = New BNode
	
	If list\firstNode = Null	; list is empty
		n\prevNode = Null
		n\nextNode = Null
		list\firstNode = n
		list\lastNode = n
	Else								; list is not empty
		n\prevNode = list\lastNode
		n\nextNode = Null
		list\lastNode\nextNode = n
		list\lastNode = n
	EndIf
		
	n\userTypePtr = userTypePtr
	list\totalNodes = list\totalNodes+1
	Return n\userTypePtr
End Function

; ResetList%( list.BList )
; Discription:
;     Use ResetList() to prepare the list for processing with NextItem()
;     ResetList() DOES change the list's 'current item' pointer previous to
;     the first item.
; Returns:
;     True if successfull, false if it's been passed a null BList
; Example:
;     ResetList(myList)
Function ResetList( list.BList )
	If list = Null Then Return False

	list\currentNode = Null
	Return True
End Function


; NextItem%( list.BList )
; Discription:
;     NextItem() DOES change the list's 'current item' pointer to the item after the
;     current 'current item'.
; Returns:
;     If there is a next item, NextItem() will return a pointer to a user defined
;     object. Otherwise it will return false.  When a function returns a pointer to
;     an object you generally want to convert it using the Blitz Basic command
;     'Object()'
; Example:
;     ResetList(myList)
;     While NextItem(myList)
;        u.MyUserType = Object.Bull(CurrentItem(myList))
;        Print u\myField
;     Wend
Function NextItem%( list.BList )
	If list = Null Then Return False
	
	If list\currentNode = Null
		If list\firstNode = Null
			Return False	
		Else
			list\currentNode = list\firstNode
			Return list\currentNode\userTypePtr
		EndIf
	Else
		If list\currentNode\nextNode = Null
			Return False
		Else
			list\currentNode = list\currentNode\nextNode
			Return list\currentNode\userTypePtr
		EndIf 
	EndIf
End Function


; PrevItem%( list.BList )
; Discription:
;     PrevItem() DOES change the list's 'current item' pointer to the item before the
;     current 'current item'.
; Returns:
;     If there is a previous item, PrevItem() will return a pointer to a user defined
;     object. Otherwise it will return false.  When a function returns a pointer to
;     an object you generally want to convert it using the Blitz Basic command
;     'Object()'
; Example:
Function PrevItem%( list.BList )
	If list = Null Then Return False
	
	If list\currentNode = Null
		If list\lastNode = Null
			Return False	
		Else
			list\currentNode = list\lastNode
			Return list\currentNode\userTypePtr
		EndIf
	Else
		If list\currentNode\prevNode = Null
			Return False
		Else
			list\currentNode = list\currentNode\prevNode
			Return list\currentNode\userTypePtr
		EndIf 
	EndIf
End Function


; FirstItem%( list.BList )
; Discription:
;     FirstItem() DOES change the list's 'current item' pointer to the first item
;     the list
; Returns:
;     If there are any items in the list, FirstItem() will return a pointer to a
;     user defined object.  Otherwise it will return false if the list is empty.
; Example:
;     u.MyUserType = Object.MyUserType(FirstItem(myList))
Function FirstItem%( list.BList )
	If list = Null Then Return False
	
	If list\firstNode = Null
		Return False
	Else
		list\currentNode = list\firstNode
		Return list\currentNode\userTypePtr	
	EndIf
End Function

; LastItem%( list.BList )
; Discription:
;     LastItem() DOES change the list's 'current item' pointer to the last item
;     the list
; Returns:
;     If there are any items in the list, LastItem() will return a pointer to a
;     user defined object.  Otherwise it will return false if the list is empty.
; Example:
;     u.MyUserType = Object.MyUserType(LastItem(myList))
Function LastItem%( list.BList )
	If list = Null Then Return False
		
	If list\lastNode = Null
		Return False
	Else
		list\currentNode = list\lastNode
		Return list\currentNode\userTypePtr	
	EndIf
End Function


; CurrentItem%( list.BList )
; Discription:
;     CurrentItem() returns a pointer to the current item in the list.
; Returns:
;     If there are any items in the list, CurrentItem() will return a pointer to a
;     user defined object.  Otherwise it will return false if the list is empty or
;     ResetList() was just called.
; Example:
;     u.MyUserType = Object.MyUserType(CurrentItem(myList))
Function CurrentItem%( list.BList )
	If list = Null Then Return False
	
	If list\currentNode = Null
		Return False
	Else
		Return list\currentNode\userTypePtr
	EndIf
End Function

; MoveItem%( fromList.BList, toList.BList )
; Discription:
;     Moves the current item from one list to the BACK of another list.
; Returns:
;     It will return a pointer to a user defined object.  Otherwise it will
;     return false if the from list is empty or either list is Null.
; Example:
;     MoveItem(myList,myOtherList)
Function MoveItem%( fromList.BList, toList.BList )
	If fromList = Null Or toList = Null Then Return False
	
	If fromList\currentNode = Null
		Return False
	Else
		If fromList\currentNode\nextNode <> Null Then fromList\currentNode\nextNode\prevNode = fromList\currentNode\prevNode
		If fromList\currentNode\prevNode <> Null Then fromList\currentNode\prevNode\nextNode = fromList\currentNode\nextNode
		If fromList\firstNode = fromList\currentNode Then fromList\firstNode = fromList\currentNode\nextNode
		If fromList\lastNode = fromList\currentNode Then fromList\lastNode = fromList\currentNode\prevNode
		n.BNode = fromList\currentNode
		fromList\currentNode = fromList\currentNode\prevNode
		fromList\totalNodes = fromList\totalNodes-1
		
		If toList\firstNode = Null	; list is empty
			n\prevNode = Null
			n\nextNode = Null
			toList\firstNode = n
			toList\lastNode = n
		Else								; list is not empty
			n\prevNode = toList\lastNode
			n\nextNode = Null
			toList\lastNode\nextNode = n
			toList\lastNode = n
		EndIf
		toList\totalNodes = toList\totalNodes+1
		Return n\userTypePtr
	EndIf
End Function

; KillItem%( list.BList, node.BNode )
; Discription:
;     Removes the node from the list.  If itme is Null it then removes the
;     'current item' from the list.
; Returns:
;     It will return a pointer to a user defined object so you can delete that.
;     Otherwise it will return false if the list is empty or current item and
;     the passed node are null.
; Example:
;     Delete Object.MyUserType(KillItem(myList,Null))
Function KillItem%( list.BList, node.BNode )
	If list = Null Then Return False
	
	If node = Null And list\currentNode = Null
		Return False
	Else
		If node = Null Then node = list\currentNode

		If node\nextNode <> Null Then node\nextNode\prevNode = node\prevNode
		If node\prevNode <> Null Then node\prevNode\nextNode = node\nextNode
		If list\firstNode = node Then list\firstNode = node\nextNode
		If list\lastNode = node Then list\lastNode = node\prevNode
		If list\currentNode = node Then list\currentNode = node\prevNode
		userTypePtr% = node\userTypePtr
		list\totalNodes = list\totalNodes-1
		Delete node
		Return userTypePtr
	EndIf
End Function

; CurrentNode.BNode( list.Blist )
; Discription:
;    Use with KillItem to remove an item that's not pointed to by 'current item'.
; Returns:
;    It will return the BNode of the 'current item'
; Example:
;    Print CountItems(myList)
Function CurrentNode.BNode( list.Blist )
	If list = Null Then Return Null
	Return list\currentNode
End Function

; TotalItems%( list.BList )
; Discription:
;     Returns how many items are currently in the list
; Example:
;    Print CountItems(myList)
Function TotalItems%( list.BList )
	If list = Null Then Return False
	Return list\totalNodes
End Function
