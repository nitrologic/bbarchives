; ID: 127
; Author: Entity
; Date: 2001-11-05 17:01:20
; Title: Linked Lists 1.2
; Description: Amiga style Linked List functions

;
; Lists.bb -- Version 1.2 -- Amiga style linked lists
; by Jamie "Entity" van den Berge
;

Type List
	Field lh_Head.Node
	Field lh_Tail.Node
End Type

Type Node
	Field ln_Succ.Node
	Field ln_Pred.Node
	Field ln_List.List
	Field ln_ID
End Type


;______________________________________________________________________________
; Remove
;
; FUNCTION
;	Removes a node from the list it is in
;
; INPUTS
;	node	- node to unlink
;
Function Remove .Node ( node.Node )
	If node\ln_Pred <> Null
		node\ln_Pred\ln_Succ = node\ln_Succ
	Else
		If node = node\ln_List\lh_Head Then node\ln_List\lh_Head = node\ln_Succ
	EndIf
	If node\ln_Succ <> Null
		node\ln_Succ\ln_Pred = node\ln_Pred
	Else
		If node = node\ln_List\lh_Tail Then node\ln_List\lh_Tail = node\ln_Pred
	EndIf
	node\ln_Pred = Null
	node\ln_Succ = Null
	node\ln_List = Null
	Return node
End Function


;______________________________________________________________________________
; RemHead
;
; FUNCTION
;	Removes first node from a list
;
; INPUTS
;	list	- list to remove first node of
;
; RESULT
;	the node that was removed
;
Function RemHead .Node ( list.List )
	Local node.Node = list\lh_Head
	If node <> Null Then list\lh_Head = node\ln_Succ
	If list\lh_Tail = node Then list\lh_Tail = Null ; list is empty?
	node\ln_List = Null
	Return node
End Function


;______________________________________________________________________________
; RemTail
;
; FUNCTION
;	Removes last node from a list
;
; INPUTS
;	list	- list to remove the last node of
;
; RESULT
;	the node that was removed
;
Function RemTail .Node ( list.List )
	Local node.Node = list\lh_Tail
	If node <> Null Then list\lh_Tail = node\ln_Pred
	If list\lh_Head = node Then list\lh_Head = Null ; list is empty?
	node\ln_List = Null
	Return node
End Function


;______________________________________________________________________________
; FindNode
;
; FUNCTION
;	Searches for a node with a particular ID in given list
;
; INPUTS
;	list	- the list to search
;	id		- the id to look for
;
; RESUT
;	the first node that id, or Null.
;
Function FindNode .Node ( list.List, id )
	Local n.Node = list\lh_Head
	While n <> Null
		If n\ln_Id = id Then Return n
		n = n\ln_Succ
	Wend
	Return Null
End Function


;______________________________________________________________________________
; AddHead
;
; FUNCTION
;	Inserts a node before the first in given list
;
; INPUTS
;	list	- the list to put the node in
;	node	- the node to insert
;
Function AddHead( list.List, node.Node )
	node\ln_Succ = list\lh_Head ; current head becomes next node of node one
	If list\lh_Head <> Null Then list\lh_Head\ln_Pred = node ; current head gets node node as prev
	If list\lh_Tail =  Null Then list\lh_Tail = node         ; no tail? then head is tail
	node\ln_Pred = Null ; node is the new head so it cant have prev node
	list\lh_Head = node ; make node node the new head
	node\ln_List = list
End Function


;______________________________________________________________________________
; AddTail
;
; FUNCTION
;	Inserts a node after the last in given list
;
; INPUTS
;	list	- the list to put the node in
;	node	- the node to insert
;
Function AddTail( list.List, node.Node )
	node\ln_Pred = list\lh_Tail ; current tail becomes prev node if node one
	If list\lh_Tail <> Null Then list\lh_Tail\ln_Succ = node ; current tail gets node node as next
	If list\lh_Head =  Null Then list\lh_Head = node         ; no head? then tail is head
	node\ln_Succ = Null ; node is the new tail so it can't have next node
	list\lh_Tail = node ; make node node the new tail
	node\ln_List = list
End Function


; Next 4 are for clarity. It's more efficient to use the type fields directly
Function FirstNode .Node ( list.List ) Return list\lh_Head: End Function
Function LastNode  .Node ( list.List ) Return list\lh_Tail: End Function
Function NextNode  .Node ( node.Node ) Return node\ln_Succ: End Function
Function PrevNode  .Node ( node.Node ) Return node\ln_Pred: End Function


;##############################################################################
; EXAMPLE CODE
;
Graphics 640,480,0,2
SetFont LoadFont("FixedSys", 8)

Function White( a$ )
	Color 255,255,255: Write a
End Function

l.List = New List		; Create a new list

; Now add a few nodes
White "Adding nodes to top         : ": Color 0,255,0
For x = 1 To 10	
	n.Node = New Node	; create the node
	n\ln_ID = x+20		; give it an ID for the example
	Write n\ln_ID + " "
	AddHead( l, n )		; add node to top of list
Next
Print ""


White "How the list looks now      : "
Gosub dumplist

White "Removing 3 nodes from top   : ": Color 255,0,0
For t = 1 To 3
	n = RemHead( l )	; remove from list
	Write n\ln_ID + " "	; print its value (node still exists!)
	Delete n			; destroy node completely
Next
Print ""

White "Removing 2 nodes from bottom: ": Color 255,0,0
For t = 1 To 2
	n = RemTail( l )	; remove from list
	Write n\ln_ID + " "	; print the value (node still exists!)
	Delete n			; destroy node completely
Next
Print ""

; Show what it looks like now
White "Remaining list              : "
Gosub dumplist

; Move first node to the end
AddTail( l, RemHead( l ) )
White "Moved top node to bottom    : "
Gosub dumplist

; Remove a node by its ID
n.Node = FindNode( l, 24 )
If n <> Null Then Remove( n ): Delete n

White "Removed node with id 24     : "
Gosub dumplist

WaitKey
End


.dumplist:
	Color 255,255,0
	n = FirstNode( l )		; get first node in list
	While n <> Null			; while there's nodes
		Write n\ln_ID + " "	; print its value
		n = NextNode( n )	; move to next node
	Wend
	Print ""
Return


