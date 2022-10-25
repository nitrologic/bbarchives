; ID: 675
; Author: Marcelo
; Date: 2003-05-07 18:07:40
; Title: Container Classes
; Description: List, Array, Heap classes for easy use

;	Container classes
;	Marcelo Oliveira - marcelo@greenlandstudios.com

Const INVALID_VALUE = -1
Const OVERFLOW_VALUE = -2

;
;	Stack
;
;	- FILO (First In Last Out)
;	- Grow if needed
;
Const TSTACK_SIZE = 16

Type TStack
	Field Bank
	Field Allocated
	Field BaseAlloc
	
	Field Size
End Type

; Create a stack
; Size = Number of initial entries
Function CreateStack(Size)
	Stack.TStack = New TStack
	
	Stack\BaseAlloc = Size * 4
	Stack\Allocated = Stack\BaseAlloc
	
	Stack\Bank = CreateBank(Stack\Allocated)
		
	Return Handle(Stack)
End Function

Function FreeStack(StackHandle)
	Stack.TStack = Object.TStack(StackHandle)
	FreeBank(Stack\Bank)
	Delete Stack
End Function

; Pushes a value into the stack
Function Stack_Push(StackHandle, Value)
	Stack.TStack = Object.TStack(StackHandle)
	OffSet = Stack\Size * 4
	
	If (OffSet + 4) > Stack\Allocated
		Stack\Allocated = Stack\Allocated + Stack\BaseAlloc
		ResizeBank(Stack\Bank, Stack\Allocated)
	EndIf
	
	PokeInt(Stack\Bank, OffSet, Value)
	Stack\Size = Stack\Size + 1
	
	Return Stack\Size - 1
End Function

; Pop a value out of the stack (which will be the last pushed value), or return INVALID_VALUE is the stack is empty
Function Stack_Pop(StackHandle)
	Stack.TStack = Object.TStack(StackHandle)
	
	If Stack\Size > 0
		Stack\Size = Stack\Size - 1
		OffSet = Stack\Size * 4
	
		Return PeekInt(Stack\Bank, OffSet)
	EndIf
	
	Return INVALID_VALUE
End Function

; Approximated memory used by the stack structures
Function Stack_UsedMemory(StackHandle)
	Stack.TStack = Object.TStack(StackHandle)
	Return Stack\Allocated + TSTACK_SIZE
End Function


;
;	Array
;
;	- Can only insert new items to the bottom
;	- Grow if needed
;   - Inserting items not in the bottom will overwrite the previous value
;	- O(1) acess to the items (query, modify of mark as deleted)
; 	- Optional use of a free stack allows fast reuse of positions marked as deleted
;
Const TARRAY_SIZE = 24

Type TArray
	Field Bank
	Field Allocated
	Field BaseAlloc
	
	Field Size
	Field EntryPos
	
	Field FreeStack
End Type

; Create an array 
; Size - Initial number of elements
; UseFreeStack - If True the deleted values will be kept in a internal stack for reuse
Function CreateArray(Size, UseFreeStack = True)
	Array.TArray = New TArray
	
	Array\Size = 0
	Array\BaseAlloc = Size * 4
	Array\Allocated = Array\BaseAlloc
	Array\Bank = CreateBank(Array\Allocated)
	
	If INVALID_VALUE <> 0
		For i = 0 To Size-1
			PokeInt(Array\Bank, i * 4, INVALID_VALUE)
		Next
	EndIf
	
	If UseFreeStack
		Array\FreeStack = CreateStack(Array\Allocated)
	EndIf
	
	Return Handle(Array)
End Function

Function FreeArray(ArrayHandle)
	Array.TArray = Object.TArray(ArrayHandle)
	
	FreeBank(Array\Bank)
	
	If Array\FreeStack
		FreeStack(Array\FreeStack)
	EndIf
	
	Delete Array
End Function

; Returns the number of array items
Function ArraySize(ArrayHandle)
	Array.TArray = Object.TArray(ArrayHandle)
	Return Array\Allocated / 4
End Function

; Puts an item to the next available slot in the array
; Returns the stored position
Function Array_Push(ArrayHandle, Value)
	Array.TArray = Object.TArray(ArrayHandle)
	
	Pos = Array\EntryPos
	If Array\FreeStack
		Pos = Stack_Pop(Array\FreeStack)
		If Pos = INVALID_VALUE
			Pos = Array\EntryPos
		EndIf
	EndIf

	OffSet = Pos * 4
	
	If (OffSet + 4) > Array\Allocated
		OSize = Array\Allocated
		Array\Allocated = Array\Allocated + Array\BaseAlloc
		ResizeBank(Array\Bank, Array\Allocated)
		
		If INVALID_VALUE <> 0
			Num0 = OSize / 4
			Num1 = (Array\Allocated / 4) - 1

			For i = Num0 To Num1
				PokeInt(Array\Bank, i * 4, INVALID_VALUE)
			Next
		EndIf
	EndIf
	
	PokeInt(Array\Bank, OffSet, Value)

	Array\Size = Array\Size + 1
	Array\EntryPos = Array\EntryPos + 1
	
	Return Pos
End Function

; Puts a value into an arbitrary position
Function Array_Insert(ArrayHandle, Pos, Value)
	Array.TArray = Object.TArray(ArrayHandle)
	
	OffSet = Pos * 4

	If (OffSet + 4) > Array\Allocated
		OSize = Array\Allocated
		Array\Allocated = Array\Allocated + Array\BaseAlloc
		ResizeBank(Array\Bank, Array\Allocated)
		
		If INVALID_VALUE <> 0
			Num0 = (OSize / 4)
			Num1 = (Array\Allocated / 4) - 1

			For i = Num0 To Num1
				PokeInt(Array\Bank, i * 4, INVALID_VALUE)
				
				If Array\FreeStack
					Stack_Push(Array\FreeStack, i)
				EndIf
			Next
		EndIf
	EndIf
	
	PokeInt(Array\Bank, OffSet, Value)
	Array\Size = Array\Size + 1
	Array\EntryPos = Pos + 1
	
	Return Pos
End Function

; Get the value stored into position
Function Array_Get(ArrayHandle, Pos)
	Array.TArray = Object.TArray(ArrayHandle)
	
	If Pos >= (Array\Allocated / 4)
		Return OVERFLOW_VALUE
	EndIf
	
	Return PeekInt(Array\Bank, Pos * 4)
End Function

; Free the specified position
Function Array_Delete(ArrayHandle, Pos)
	Array.TArray = Object.TArray(ArrayHandle)
	
	OffSet = Pos * 4
	
	Value = PeekInt(Array\Bank, OffSet)
	If Value <> INVALID_VALUE
		PokeInt(Array\Bank, OffSet, INVALID_VALUE)
		
		If Array\FreeStack
			Stack_Push(Array\FreeStack, Pos)
		EndIf

		Array\Size = Array\Size - 1
	EndIf
End Function

; Approximated memory used by the array
Function Array_UsedMemory(ArrayHandle)
	Array.TArray = Object.TArray(ArrayHandle)
	
	Mem = Array\Allocated + TARRAY_SIZE
	
	If Array\FreeStack
		Mem = Mem + Stack_UsedMemory(Array\FreeStack)
	EndIf
	
	Return Mem
End Function

Type TArrayIterator
	Field Array.TArray
	Field Pos
End Type

; Creates an interator from the start of the array and return it's handle
Function Array_IterateStart(ArrayHandle)
	Array.TArray = Object.TArray(ArrayHandle)
	
	Iterator.TArrayIterator = New TArrayIterator
	Iterator\Array = Array
	Iterator\Pos = 0
	
	Return Handle(Iterator)
End Function

; Move the iterator to the next valid position in the array
; Returns the value of the current position or INVALID_VALUE if the array reaches its end
Function Array_IterateNext(IteratorHandle)
	Iterator.TArrayIterator = Object.TArrayIterator(IteratorHandle)
	
	LItem = Iterator\Array\Allocated / 4

	If Iterator\Pos = LItem
		Return INVALID_VALUE
	EndIf
	
	Value = PeekInt(Iterator\Array\Bank, Iterator\Pos * 4)
	
	Repeat 
		Iterator\Pos = Iterator\Pos + 1

		If Iterator\Pos = LItem
			Exit
		EndIf
	Until (PeekInt(Iterator\Array\Bank, Iterator\Pos * 4) <> INVALID_VALUE)
	
	Return Value
End Function

; Clears the iterator structure
Function Array_IterateEnd(IteratorHandle)
	Iterator.TArrayIterator = Object.TArrayIterator(IteratorHandle)
	Delete Iterator
End Function


;
;	List
;
;	- O(1) insertion, deletion, modification
;
;	- Can associate each node with an unique key
;	 - O(N) access if TLIST_NOINDEX is used
;	 - O(1) access if TLIST_INTEGERINDEX is used
;	 - O(1) to O(Log N) access if TLIST_HASHINDEX is used

Const TLISTNODE_SIZE = 24

Type TListNode
	Field Value$

	Field IKey
	Field SKey$

	Field IndexPos
	
	Field rNext.TListNode
	Field rPrev.TListNode
End Type


Const TLIST_SIZE = 28

Type TList
	Field Head.TListNode
	Field Tail.TListNode
	
	Field IndexType
	Field IndexArray
	Field IndexSize
	
	Field HashMask
	
	Field Size
End Type

Const TLIST_NOINDEX 	   = 0
Const TLIST_INTEGERINDEX   = 1
Const TLIST_HASHINDEX      = 2

; Creates a list
;  IndexType
;    TLIST_NOINDEX			No index will be used, searchs will be O(N)
;    TLIST_INTEGERINDEX 	Accept only integers, search in O(1), uses (N * 4) additional bytes
;    TLIST_HASHINDEX  		Accept strings, search from O(1) to O(Log N), uses (N * 4) additional bytes
;  IndexSize
;	 Number of initial entries in the index, for TLIST_HASHINDEX is better to use powers of 2... (2^X)

Function CreateList(IndexType = TLIST_NOINDEX, IndexSize = 64)
	List.TList = New TList
	List\Head = Null
	List\Tail = Null
	List\Size = 0
	
	List\IndexType = IndexType
	
	Select List\IndexType
		Case TLIST_INTEGERINDEX
			List\IndexArray = CreateArray(IndexSize, False)
			
		Case TLIST_HASHINDEX
			 List\IndexArray = CreateArray(IndexSize, False)
			 List\HashMask = IndexSize-1
	End Select
	
	Return Handle(List)
End Function

Function FreeList(ListHandle)
	List.TList = Object.TList(ListHandle)

	Node.TListNode = List\Head
	NextNode.TListNode = Null	
	
	While Node <> Null
		NextNode = Node\rNext
		Delete Node
		Node = NextNode
	Wend
	
	If List\IndexArray
		FreeArray(List\IndexArray)
	EndIf
	
	Delete List
End Function

; Clear all list nodes
Function List_Clear(ListHandle)
	List.TList = Object.TList(ListHandle)

	Node.TListNode = List\Head
	NextNode.TListNode = Null	
	
	While Node <> Null
		NextNode = Node\rNext
		Delete Node
		Node = NextNode
	Wend
End Function

; Pushes a value to the top of the list
; 	Value can be a string or a integer
;	Key$
;		Optional string if TLIST_NOINDEX is selected
;		Integer if TLIST_INTEGERINDEX is selected
;		String if TLIST_HASHINDEX is selected
Function List_PushFront(ListHandle, Value$, Key$ = "")
	List.TList = Object.TList(ListHandle)
	
	Node.TListNode = New TListNode
	Node\Value = Value
	
	List_AddIndex(List, Key, Node)
	
	If List\Tail <> Null
		List\Head\rPrev = Node
		Node\rNext = List\Head
		List\Head = Node
	Else
		List\Head = Node
		List\Tail = Node
	EndIf
	
	List\Size = List\Size + 1
	
	Return Handle(Node)
End Function

; Pushes a value to the bottom of the list (see List_PushFront for parameters)
Function List_PushBack(ListHandle, Value$, Key$ = "")
	List.TList = Object.TList(ListHandle)
	
	Node.TListNode = New TListNode
	Node\Value = Value
	
	List_AddIndex(List, Key, Node)	
	
	If List\Tail <> Null
		Node\rPrev = List\Tail
		List\Tail\rNext = Node
		List\Tail = Node
	Else
		List\Head = Node
		List\Tail = Node
	EndIf
	
	List\Size = List\Size + 1
	
	Return Handle(Node)
End Function

; Inserts a value before node Pos (see List_PushFront for the other parameters)
Function List_InsertBefore(ListHandle, Pos, Value$, Key$ = "")
	List.TList = Object.TList(ListHandle)
	
	PNode.TListNode = Object.TListNode(Pos)
	
	If PNode <> Null
		Node.TListNode = New TListNode
		Node\Value = Value

		List_AddIndex(List, Key, Node)
		
		If PNode\rPrev <> Null
			PNode\rPrev\rNext = Node
		EndIf
		
		Node\rPrev = PNode\rPrev
		PNode\rPrev = Node
		Node\rNext = PNode
	
		If PNode = List\Head
			List\Head = Node
		EndIf
		
		Return Handle(Node)
	EndIf
	
	Return 0
End Function

; Inserts a value after node Pos (see List_PushFront for the other parameters)
Function List_InsertAfter(ListHandle, Pos, Value$, Key$ = "")
	List.TList = Object.TList(ListHandle)
	
	PNode.TListNode = Object.TListNode(Pos)
	
	If PNode <> Null
		Node.TListNode = New TListNode
		Node\Value = Value
		
		List_AddIndex(List, Key, Node)
		
		Node\rNext = PNode\rNext
		PNode\rNext = Node
		Node\rPrev = PNode
		
		If PNode = List\Tail
			List\Tail = Node
		EndIf
		
		Return Handle(Node)
	EndIf
	
	Return 0
End Function

; Gets the first value
Function List_GetFront$(ListHandle)
	List.TList = Object.TList(ListHandle)
	
	If List\Head <> Null
		Return List\Head\Value
	EndIf
	
	Return INVALID_VALUE
End Function

; Pops a value from the top of the list
Function List_PopFront$(ListHandle)
	List.TList = Object.TList(ListHandle)
	
	Node.TListNode = List\Head
	
	If Node <> Null
		List\Head = Node\rNext
	
		If List\Head <> Null
			List\Head\rPrev = Null
		EndIf
		
		If Node = List\Tail
			List\Tail = List\Head
		EndIf

		Value = Node\Value
		List_DelIndex(List, Node)
		Delete Node
		List\Size = List\Size - 1
		
		Return Value
	EndIf
	
	Return INVALID_VALUE
End Function

; Get the list tail value
Function List_GetBack$(ListHandle)
	List.TList = Object.TList(ListHandle)
	
	If List\Tail <> Null
		Return List\Tail\Value
	EndIf
	
	Return INVALID_VALUE
End Function

; Pops a value from the bottom of the list
Function List_PopBack$(ListHandle)
	List.TList = Object.TList(ListHandle)
	
	Node.TListNode = List\Tail
	
	If Node <> Null
		If Node = List\Head
			List\Head = Node\rNext
		EndIf
	
		List\Tail = Node\rPrev

		If List\Tail <> Null
			List\Tail\rNext = Null	
		EndIf
		
		Value = Node\Value
		List_DelIndex(List, Node)
		Delete Node
		List\Size = List\Size - 1
		
		Return Value
	EndIf
	
	Return INVALID_VALUE
End Function

; Get the value of node Pos
Function List_Get$(ListHandle, Pos)
	PNode.TListNode = Object.TListNode(Pos)
	
	If PNode <> Null
		Return PNode\Value
	EndIf
	
	Return INVALID_VALUE
End Function

; Get the value of associated with Key$
Function List_GetByKey$(ListHandle, Key$)
	Pos = List_FindKey(ListHandle, Key)
	
	If Pos <> INVALID_VALUE
		PNode.TListNode = Object.TListNode(Pos)
		Return PNode\Value
	EndIf
	
	Return INVALID_VALUE
End Function

; Finds the key and returns it's node
Function List_FindKey(ListHandle, Key$ = "")
	List.TList = Object.TList(ListHandle)

	Pos = INVALID_VALUE
	
	Select List\IndexType
		Case TLIST_NOINDEX
			PNode.TListNode = List\Head
			
			While PNode <> Null
				If PNode\SKey = Key
					Pos = Handle(PNode)
				EndIf
				PNode = PNode\rNext
			Wend
	
		Case TLIST_INTEGERINDEX
			Pos = Array_Get(List\IndexArray, Int(Key))


		Case TLIST_HASHINDEX
			IKey = List_HashString(Key$, 0)
			
			Pos = IKey And List\HashMask
			Value = Array_Get(List\IndexArray, Pos)
			If Value <> INVALID_VALUE
				PNode.TListNode = Object.TListNode(Value)
				
				While PNode <> Null
					If PNode\IKey = IKey
						If PNode\SKey = Key
							Pos = Value
							Exit
						EndIf
					EndIf
					
					Pos = Pos + 1
					
					Value = Array_Get(List\IndexArray, Pos)
					If Value = OVERFLOW_VALUE
						Pos = INVALID_VALUE
						Exit
					EndIf
					
					If Value <> INVALID_VALUE
						PNode.TListNode = Object.TListNode(Value)
					EndIf
				Wend
			Else
				Pos = INVALID_VALUE
			EndIf
	End Select

	Return Pos
End Function

; Return the Pos node key
Function List_Key$(ListHandle, Pos)
	List.TList = Object.TList(ListHandle)

	PNode.TListNode = Object.TListNode(Pos)
	
	Select List\IndexType
		Case TLIST_NOINDEX : Return PNode\SKey
		Case TLIST_INTEGERINDEX : Return PNode\IKey
		Case TLIST_HASHINDEX : Return PNode\SKey
	End Select
End Function

; Change the key of Pos node
Function List_ChangeKey(ListHandle, Pos, Key$)
	List.TList = Object.TList(ListHandle)

	PNode.TListNode = Object.TListNode(Pos)
	
	List_DelIndex(List, PNode)
	List_AddIndex(List, Key, PNode)
End Function

; Delete the node Pos from the list
Function List_Delete(ListHandle, Pos)
	List.TList = Object.TList(ListHandle)
	
	PNode.TListNode = Object.TListNode(Pos)
	
	If PNode <> Null
		If PNode = List\Head
			List\Head = PNode\rNext
		EndIf

		If PNode = List\Tail
			List\Tail = PNode\rPrev
		EndIf
		
		If PNode\rPrev <> Null
			PNode\rPrev\rNext = PNode\rNext
		EndIf

		If PNode\rNext <> Null
			PNode\rNext\rPrev = PNode\rPrev
		EndIf
		
		List_DelIndex(List, PNode)
		Delete PNode
		List\Size = List\Size - 1
	EndIf
End Function

; Approximated used memory by the list
Function List_UsedMemory(ListHandle)
	List.TList = Object.TList(ListHandle)
	
	Mem = (List\Size * TLISTNODE_SIZE) + TLIST_SIZE
	
	If List\IndexArray
		Mem = Mem + Array_UsedMemory(List\IndexArray)
	EndIf
		
	Return Mem
End Function


Type TListIterator
	Field List.TList
	Field Node.TListNode
End Type

; Creates a iterator for the list, return it's handle
Function List_IterateStart(ListHandle)
	List.TList = Object.TList(ListHandle)
	
	Iterator.TListIterator = New TListIterator
	Iterator\List = List
	Iterator\Node = List\Head
	
	Return Handle(Iterator)
End Function

; Goto next node in the iterator, return the current node value or INVALID_VALUE if the list reached the end
Function List_IterateNext(IteratorHandle)
	Iterator.TListIterator = Object.TListIterator(IteratorHandle)
	
	If Iterator\Node = Null
		Return INVALID_VALUE
	EndIf
	
	Node.TListNode = Iterator\Node
	
	Iterator\Node = Iterator\Node\rNext
	
	Return Handle(Node)
End Function

; Clears the iterator structure
Function List_IterateEnd(IteratorHandle)
	Iterator.TListIterator = Object.TListIterator(IteratorHandle)
	Delete Iterator
End Function



;
;	List private functions 
;

; Add a index to a node
Function List_AddIndex(List.TList, Key$, Node.TListNode)
	Select List\IndexType
		Case TLIST_NOINDEX
			Node\SKey = Key
			
		Case TLIST_INTEGERINDEX
			If Key = 0
				Node\IKey = Array_Push(List\IndexArray, Handle(Node))
			Else
				Array_Insert(List\IndexArray, Int(Key), Handle(Node))
				Node\IKey = Int(Key)
			EndIf
			Node\IndexPos = Node\IKey

		Case TLIST_HASHINDEX
			IKey = List_HashString(Key$, 0)
			Pos = IKey And List\HashMask
			
			Value = Array_Get(List\IndexArray, Pos)
			If Value <> INVALID_VALUE
				PNode.TListNode = Object.TListNode(Value)
				
				While PNode <> Null
					If PNode\IKey = IKey
						If PNode\SKey = Key
							Return False
						EndIf
					EndIf
		
					Pos = Pos + 1
					
					Value = Array_Get(List\IndexArray, Pos)
					If (Value = INVALID_VALUE) Or (Value = OVERFLOW_VALUE)
						Exit
					EndIf
					
					PNode.TListNode = Object.TListNode(Value)
				Wend
			EndIf
			
			Node\SKey = Key$
			Node\IKey = IKey
			Node\IndexPos = Pos
		
			OSize = ArraySize(List\IndexArray)
			Array_Insert(List\IndexArray, Pos, Handle(Node))
			
			NSize = ArraySize(List\IndexArray)
			
			; Size has changed, reconstruct the array
			If OSize <> NSize
				NewMask = NSize - 1
				NewArray = CreateArray(NSize)
				
				Iter = Array_IterateStart(List\IndexArray)
		
				Value = Array_IterateNext(Iter)
				While Value <> INVALID_VALUE
					PNode.TListNode = Object.TListNode(Value)
								
					Pos = PNode\IKey And NewMask				
					
					Value2 = Array_Get(NewArray, Pos)
					If Value2 <> INVALID_VALUE
						PNode2.TListNode = Object.TListNode(Value2)
						
						While PNode2 <> Null
							Pos = Pos + 1
							
							Value2 = Array_Get(NewArray, Pos)
							If (Value = INVALID_VALUE) Or (Value = OVERFLOW_VALUE)
								Exit
							EndIf
							
							PNode2.TListNode = Object.TListNode(Value2)
						Wend
					EndIf
					
					PNode\IndexPos = Pos
					Array_Insert(NewArray, Pos, Handle(PNode))
					
					Value = Array_IterateNext(Iter)
				Wend
				
				Array_IterateEnd(Iter)
				
				List\HashMask = NewMask
				FreeArray(List\IndexArray)
				List\IndexArray = NewArray
			EndIf
			
	End Select
End Function

; Remove the index from the node
Function List_DelIndex(List.TList, Node.TListNode)
	Select List\IndexType
		Case TLIST_INTEGERINDEX
			Array_Delete(List\IndexArray, Int(Node\IndexPos))

		Case TLIST_HASHINDEX
			Array_Delete(List\IndexArray, Int(Node\IndexPos))
	End Select
End Function

; Returns a 31 bit value by hashing the string (32 bits will be negative)
Function List_HashString%(Key$, Level)
	Ln = Len(Key$)
	
	If Ln > 255
		Ln = 255
	EndIf
	
	Local K%[255]
	For i = 1 To Ln
		K[i-1] = Asc(Mid(Key$, i, 1))
	Next
	
	Pos = 0

	A = $9e3779b9
	B = $9e3779b9
	C = Level
	
	While Ln >= 12
		A = A + (K[Pos + 0] + (K[Pos + 1] Shl 8) + (K[Pos + 2]  Shl 16) + (K[Pos + 3]  Shl 24))
		B = B + (K[Pos + 4] + (K[Pos + 5] Shl 8) + (K[Pos + 6]  Shl 16) + (K[Pos + 7]  Shl 24))
		C = C + (K[Pos + 8] + (K[Pos + 9] Shl 8) + (K[Pos + 10] Shl 16) + (K[Pos + 11] Shl 24))
		
		; MIX
		A = A - B  :  A = A - C  :  A = A Xor (C Shr 13)
		B = B - C  :  B = B - A  :  B = B Xor (A Shl 8)
		C = C - A  :  C = C - B  :  C = C Xor (B Shr 13)
		A = A - B  :  A = A - C  :  A = A Xor (C Shr 12)
		B = B - C  :  B = B - A  :  B = B Xor (A Shl 16)
		C = C - A  :  C = C - B  :  C = C Xor (B Shr 5)
		A = A - B  :  A = A - C  :  A = A Xor (C Shr 3)
		B = B - C  :  B = B - A  :  B = B Xor (A Shl 10)
		C = C - A  :  C = C - B  :  C = C Xor (B Shr 15)
		
		Pos = Pos + 12  :  Ln = Ln - 12
	Wend
	
	C = C + Ln
	
	If Ln >= 11 Then C = C + K[Pos + 10] Shl 24
	If Ln >= 10 Then C = C + K[Pos +  9] Shl 16
	If Ln >= 9 Then  C = C + K[Pos +  8] Shl 8
	If Ln >= 8 Then  B = B + K[Pos +  7] Shl 24
	If Ln >= 7 Then  B = B + K[Pos +  6] Shl 16
	If Ln >= 6 Then  B = B + K[Pos +  5] Shl 8
	If Ln >= 5 Then  B = B + K[Pos +  4]
	If Ln >= 4 Then  A = A + K[Pos +  3] Shl 24
	If Ln >= 3 Then  A = A + K[Pos +  2] Shl 16
	If Ln >= 2 Then  A = A + K[Pos +  1] Shl 8
	If Ln >= 1 Then  A = A + K[Pos]

	; MIX
	A = A - B  :  A = A - C  :  A = A Xor (C Shr 13)
	B = B - C  :  B = B - A  :  B = B Xor (A Shl 8)
	C = C - A  :  C = C - B  :  C = C Xor (B Shr 13)
	A = A - B  :  A = A - C  :  A = A Xor (C Shr 12)
	B = B - C  :  B = B - A  :  B = B Xor (A Shl 16)
	C = C - A  :  C = C - B  :  C = C Xor (B Shr 5)
	A = A - B  :  A = A - C  :  A = A Xor (C Shr 3)
	B = B - C  :  B = B - A  :  B = B Xor (A Shl 10)
	C = C - A  :  C = C - B  :  C = C Xor (B Shr 15)
	
	Return C And $7FFFFFFF
End Function

; Returns a value associated with the string ASCII order for the first 4 characters
; Useful for sorting issues
Function List_ValueString(Key$)
	Ln = Len(Key$)-1
	If Ln > 3 Then Ln = 3
	
	For i = 0 To Ln
		Char = Asc(Mid(Key, i+1, 1))
		Value = Value Or (Char Shl ((3 - i) * 8))
	Next
	
	Return Value
End Function


;
;	SortQueue
;
;	  - Priority queue using a binary tree
Type TSortQueue
	Field Bank
	Field Allocated
	Field BaseAlloc
	
	Field Size
End Type

; Creates a new sort queue of Size items
Function CreateSortQueue(Size)
	Queue.TSortQueue = New TSortQueue
	
	Queue\BaseAlloc = Size * 8
	Queue\Allocated = Queue\BaseAlloc

	Queue\Bank = CreateBank(Queue\Allocated)
	Queue\Size = 1
	
	Return Handle(Queue)
End Function

; Pushes a new value and key pair into the queue
Function SortQueue_Push(QueueHandle, Value, Key)
	Queue.TSortQueue = Object.TSortQueue(QueueHandle)
	
	i = Queue\Size
	
	Queue\Size = Queue\Size + 1
	If (Queue\Size * 8) > Queue\Allocated
		Queue\Allocated = Queue\Allocated + Queue\BaseAlloc
		ResizeBank(Queue\Bank, Queue\Allocated)
	EndIf
	
	While (i > 1)
		PValue = PeekInt(Queue\Bank, ((i/2) * 8) + 0)
		PKey = PeekInt(Queue\Bank, ((i/2) * 8) + 4)
		
		If PKey > Key
			PokeInt(Queue\Bank, (i * 8) + 0, PValue) 
			PokeInt(Queue\Bank, (i * 8) + 4, PKey) 			
			i = i / 2
		Else
			Exit
		EndIf
	Wend
	
	PokeInt(Queue\Bank, (i * 8) + 0, Value) 
	PokeInt(Queue\Bank, (i * 8) + 4, Key) 			
	
End Function

; Pops the value with the mininum key value
Function SortQueue_Pop(QueueHandle)
	Queue.TSortQueue = Object.TSortQueue(QueueHandle)
	
	If Queue\Size = 1
		Return INVALID_VALUE
	EndIf
	
	Queue\Size = Queue\Size - 1
	RetVal = PeekInt(Queue\Bank, 8)
	
	TMPValue = PeekInt(Queue\Bank, ((Queue\Size) * 8) + 0)
	TMPKey = PeekInt(Queue\Bank, ((Queue\Size) * 8) + 4)
	
	i = 1 : j = 0
	
	While i <= (Queue\Size/2)
		j = 2 * i
		
		Value = PeekInt(Queue\Bank, (j * 8) + 0)
		Key = PeekInt(Queue\Bank, (j * 8) + 4)
		
		If j < Queue\Size
			Value2 = PeekInt(Queue\Bank, ((j+1) * 8) + 0)
			Key2 = PeekInt(Queue\Bank, ((j+1) * 8) + 4)
			
			If Key > Key2
				j = j + 1
				Value = Value2
				Key = Key2
			EndIf
		EndIf
		
		If Key >= TMPKey
			Exit
		EndIf
		
		PokeInt(Queue\Bank, (i * 8) + 0, Value) 
		PokeInt(Queue\Bank, (i * 8) + 4, Key) 			
		
		i = j	
	Wend
	
	PokeInt(Queue\Bank, (i * 8) + 0, TMPValue) 
	PokeInt(Queue\Bank, (i * 8) + 4, TMPKey)
	
	Return RetVal		
End Function


Function FreeSortQueue(QueueHandle)
	Queue.TSortQueue = Object.TSortQueue(QueueHandle)
	FreeBank(Queue\Bank)
	Delete Queue
End Function
