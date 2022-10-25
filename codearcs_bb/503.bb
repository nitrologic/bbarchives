; ID: 503
; Author: makakoman
; Date: 2002-11-22 12:28:31
; Title: listClass
; Description: List Management

; listClass.BB
;
; Makakoman - 11/22/2002
; 
;

; Thanks to skn3[ac] for the gettok$ function!

; listClass.BB - A set of list management functions.  You can use freely
; in any programs you wish, but do not sell, redistribute, package with 
; another product, etc, etc, without the authors expressed written permission.
;
; BLAH, BLAH, BLAH...
;


; Key Type constants - what kind of value stored in key
Const KEYTYPE_INTEGER	= 1
Const KEYTYPE_FLOAT		= 2
Const KEYTYPE_STRING 	= 3
Const KEYTYPE_DATE 		= 4
Const KEYTYPE_TIME   	= 5
Const KEYTYPE_SPEED  	= 6; NOTE: KEYTYPE_SPEED bypasses the call to listCompare()
												 ; and does a straight string compare a$=b$.  Therefore,
												 ; all SORTS, SEARCHES, and ADD/INSERTS will be affected.
												 ; "Hello" and "hello" are not the same and can appear
												 ; far from each other in sort order.  Don't get confused.
												 ; Allowdups set to FALSE will still allow these two keys.
												 ; KEYTYPE_SPEED is ALWAYS CASE SENSITIVE!
												 

; Compare Type Constants
Const COMPARE_GT      =  1
Const COMPARE_LT			= -1
Const COMPARE_EQ 			=  0

; Compare Type Constants
Const LIST_CASESENSITIVE 	=  True
Const LIST_ALLOWDUPS			=  True

; List Errors
Const LISTERR_NONE 				=   0
Const LISTERR_NOTFOUND		=  -1
Const LISTERR_DUPLICATE		=  -2

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
;TYPE ITEMTYPE - used to store list item information
;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Type itemType
;```````````````````````````````````````````````````
	Field key$ ; lookup/search key - can be string,integer,float(date,time, wildcard coming soon!)
	Field objecthandle ; generic data pointer
End Type ; itemType



;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
;TYPE LISTTYPE - The list object contains any number
; 						   of ITEMTYPE objects and supports adding,
;								 deleting, moving, sorting, searching,
;                in many combinations.
;								 NOTE: Lists are 0 based (index starts at 0)
;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Type listType
;```````````````````````````````````````````````````
	Field indexes ; bank containing all list item handles in sequential(index) order.
	Field count% ; current number of items in list. (indexes -> 0 .. count-1)
	Field sizeincrement% ; how many 4 byte chunks to allocate when the list needs to grow.
	Field keytype ; how to cast the data in the key$ of each item
	Field index% ; index of most recently found or added item.
	Field sort ; Should the list be kept sorted? True/False
	Field error% ; most recent error raised while executing list function
	Field case_sensitive ; Should keys use case sensitivity? True/False (only works on KEYTYPE_STRING)
	Field allowdups ; Are duplicate keys allowed? True/False (only works on sorted list)
End Type ; listType







;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Create a new list
;
;  size:		Initial size of list
;  sizeincrement : how many items to increase list to when allocating more space
;  keytype : what kind of data is stored in key field
;  sort : True to sort the list
;  case_sensitive : True to enforce case sensativity in comparisons
;	 allowdups : True allows duplicate keys, False fails on insert.
;................................................................................
Function listNew.listType(size, sizeincrement = 5, keytype = KEYTYPE_SPEED, sort=True, case_sensitive=True, allowdups=True)
;```````````````````````````````````````````````````````````````````````````````
	l.listType 			= New listType
	l\indexes 			= CreateBank(size * 4) ; create storage area for handles to list items.
	l\keytype   		= keytype
	l\count%				= 0
	l\sizeincrement = sizeincrement
	l\sort					= sort
	l\allowdups    = allowdups
	l\case_sensitive = case_sensitive
	l\error%				= LISTERR_NONE
	l\index%			= LISTERR_NOTFOUND ; no current list item
	Return(l)
End Function ; listNew






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Return number of items in list
;...............................................................................
Function listCount%(l.listType)
;```````````````````````````````````````````````````````````````````````````````
	Return(l\count%)
End Function ; listCount()






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Deletes all of the items from the list and resizes index bank
; NOTE: This will not FREE any objects you have attached to a list item. Use 
;       listitemDelete() to delete listitem and return object handle to user.
;...............................................................................
Function listDeleteAll(l.listType)
;```````````````````````````````````````````````````````````````````````````````
	Local item.itemType

	; delete all data objects
	For i = 0 To l\count-1
		item = Object.itemType(PeekInt(l\indexes, offset))
		Delete item
	Next

	; resize bank to initial size	
	ResizeBank(l\indexes, l\sizeincrement * 4) ; destroy data pointers
	l\count 		= 0 ; list is empty
	l\index%  = LISTERR_NOTFOUND ; no current list item
End Function ; listDeleteAll()






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Deletes all of the items from the list and then destroys list
; NOTE: This will not FREE any objects you have attached to a list item. Use 
;       listitemDelete() to delete listitem and return object handle to user.
;...............................................................................
Function listDelete(l.listType)
;```````````````````````````````````````````````````````````````````````````````
	listDeleteAll(l) ; call delete all nodes.
	FreeBank(l\indexes) ; free the memory for indexes
	
	Delete l ; destroy list object
End Function ; listDelete()






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Sequential Search for a listitem in an UNSORTED list with the specified key value.
; Returns the index of the list item if found.
;...............................................................................
Function listFind.itemType(l.listType, key$)
;```````````````````````````````````````````````````````````````````````````````
	listRaiseError(l, LISTERR_NONE)

	; search list sequentially for specified item.
	For i = 0 To listCount(l) - 1
		item.itemType = listItem(l, i)

		; if keys are equal, return list index
		If l\keytype = KEYTYPE_SPEED Then ; don't call compare function, sloooow...
			If key$ = item\key$ = COMPARE_EQ Then 
				l\index% = i ; save current item index
				Return(item)
			End If
		Else
			If listCompare(key$, item\key$, l\keytype, l\case_sensitive) = COMPARE_EQ Then 
				l\index% = i ; save current item index
				Return(item)
			End If
		End If
	Next

	listRaiseError(l, LISTERR_NOTFOUND)
	Return Null ; not found
End Function ; listFind()




;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Returns the index of the most recently found list item.
;...............................................................................
Function listIndex%(l.listType)
;```````````````````````````````````````````````````````````````````````````````
	Return(l\index%)
End Function ; listIndex()





;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Sort a list using key$.  Algorythm uses Selection Sort.
;...............................................................................
Function listSort(l.listType)
;```````````````````````````````````````````````````````````````````````````````
	Local item.itemType

	; This function implements a SELECTION SORT 
	count = listCount(l)-1
	If count < 2 Then Return ; no need to sort 1 item list

	; sort list shortening it by 1 each loop iteration
	For effectiveSize = count To 1 Step -1
		; initialize max base comparison values to first item
		item 		= listItem(l, 0) 
		maxkey$	= item\key$
		maxpos% = 0
		
		; check each item in remaining list to find highest value (maxkey$)
		For i = 0 To effectiveSize ; find maximum value in list
			item = listItem(l, i) ; get current item
			
			; check if this item is a higher value than current maxkey$

			If l\keytype = KEYTYPE_SPEED Then ; don't call compare function, sloooow...
				newmaxkey% = item\key$ > maxkey$
			Else
				newmaxkey% = listCompare(item\key$, maxkey$, l\keytype, l\case_sensitive) = COMPARE_GT
			End If
			
			; if current key$ > max key$ then we have a new max key$
			If newmaxkey% Then
				maxpos% = i
				maxkey$  = item\key$
			End If
		Next ; i
		
		; swap the max value with the highest unsorted value
		listItemSwap(l, maxpos%, effectiveSize)
	Next ; j
End Function ; listSort()




;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Set list sort flag.  It true, then listSort() is called.
;...............................................................................
Function listSetSort(l.listType, sort)
;```````````````````````````````````````````````````````````````````````````````
	; don't want to sort again if it is already sorted
	If l\sort Then If sort Then Return
	
	; if list is not sorted and sort is set to TRUE, then sort list.
	If Not l\sort Then If sort Then listSort(l)
	
	l\sort = sort ; assign value
End Function ; listSetSort()



;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Set Allow Duplicates flag to True or False.
; This will affect listItemAdd() for a SORTED list. 
; allowdups has NO EFFECT on an unsorted list!
;...............................................................................
Function listSetAllowDups(l.listType, allowdups%)
;```````````````````````````````````````````````````````````````````````````````
	l\allowdups = allowdups% ; assign value
End Function ; listSetAllowDups()


;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Set Case Sensitive flag to True or False.
; This will affect listItemAdd(), listFind(), listSearch() and listSort()
; This flag will affect the way the item is sorted an searches.  Also,
; allowdups will act differently depending on case_sensitive
;...............................................................................
Function listSetCaseSensitive(l.listType, case_sensitive%)
;```````````````````````````````````````````````````````````````````````````````
	l\case_sensitive = case_sensitive% ; assign value
End Function ; listSetCaseSensitive()


;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Set KEYTYPE for list.  This tells the Comparison function how to treat your
; key$ data.  Strings of number sort different than actual numeric values, so
; It is important to choose the right keytype.  
; NOTE: KEYTYPE_SPEED does not use the compare function.  It just does a standard
; string compare (a$=b$), so it IS ALWAYS CASE SENSITIVE.
; NOTE: KEYTYPE can be changed anytime, but you should probably call
; listSort() after changing it, if you are working with sorted list.
;...............................................................................
Function listItemSetKey(l.listType, index%, key$)
;```````````````````````````````````````````````````````````````````````````````
	item.itemType = listItem(l, index%)
	If item = Null Then RuntimeError("listItemSetKey : Index out of bounds - "+index%)

	item\key$ = key$
End Function ; listItemSetKey()



;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Set KEYTYPE for list.  This tells the Comparison function how to treat your
; key$ data.  Strings of number sort different than actual numeric values, so
; It is important to choose the right keytype.  
; NOTE: KEYTYPE_SPEED does not use the compare function.  It just does a standard
; string compare (a$=b$), so it IS ALWAYS CASE SENSITIVE.
; NOTE: KEYTYPE can be changed anytime, but you should probably call
; listSort() after changing it, if you are working with sorted list.
;...............................................................................
Function listSetKeyType(l.listType, keytype%)
;```````````````````````````````````````````````````````````````````````````````
	l\keytype = keytype ; assign value
End Function ; listSetKeyType()




;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Use a Binary Search to find a key in a SORTED list.  Do not use on UNSORTED list!
; Returns the item if it is found, else NULL if not found.
;...............................................................................
Function listSearch.itemType(l.listType, value$)
;```````````````````````````````````````````````````````````````````````````````
	listRaiseError(l, LISTERR_NONE)
	
	; Perform Binary Search on entire list
	index% = listBinarySearch(l, 0, listCount(l)-1, value$)
	
	If index% = LISTERR_NOTFOUND Then 
		listRaiseError(l, LISTERR_NOTFOUND)
		Return Null ; return NULL if not found...
	End If
	
	; Get item from list and return handle
	item.itemType = listItem(l, index%) ; get list item
	l\index% = index% ; save current item index
	Return item
End Function ; listSearch()





;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; This is a PRIVATE recursive function that should not be called directly.
;...............................................................................
Function listBinarySearch%(l.listType, low%, high%, value$)
;```````````````````````````````````````````````````````````````````````````````
	Local middle%;
	
; Termination check 
	if (low% > high%) Return LISTERR_NOTFOUND;

	; calculate the middle point between low% and high%
	middle% = (high%+low%)/2;
	
	item.itemType = listItem(l, middle%) ; get list item
	
	; compare to key we are searching for in list
	If l\keytype = KEYTYPE_SPEED Then ; 
		If item\key$ = value$ Then Return middle%
		If item\key$ > value$ Then Return listBinarySearch(l, low%, middle%-1, value$)
		If item\key$ < value$ Then Return listBinarySearch(l, middle%+1, high%, value$)
	Else ; use listCompare function, a little slow...
		If listCompare(item\key$, value$, l\keytype, l\case_sensitive) = COMPARE_EQ Then Return middle%
		If listCompare(item\key$, value$, l\keytype, l\case_sensitive) = COMPARE_GT Then Return listBinarySearch(l, low%, middle%-1, value$)
		If listCompare(item\key$, value$, l\keytype, l\case_sensitive) = COMPARE_LT Then Return listBinarySearch(l, middle%+1, high%, value$)
	End If
	Return LISTERR_NOTFOUND ; returns error if value not found.
End Function ; listBinarySearch()






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; This is a PRIVATE recursive function used by listItemAdd().  Should not be
; called directly.
;...............................................................................
Function listInsertionSearch%(l.listType, low%, high%, value$)
;```````````````````````````````````````````````````````````````````````````````
	Local middle%;
	
; Termination check 
	If listCount(l) < 1 Then Return 0 ; list is empty, append item!
	
	; This is where item shold be inserted...
	If (low% >= high%) 
		; check if it goes before or after current node
		item.itemType = listItem(l, low%) ; get list item
		If l\keytype = KEYTYPE_SPEED Then ; 
			If item\key$ < value$ Then low% = low%+1
			If (item\key$ = value$) And (Not l\allowdups) Then listRaiseError(l, LISTERR_DUPLICATE)
		Else
			If listCompare(item\key$, value$, l\keytype, l\case_sensitive) = COMPARE_LT Then low% = low%+1
			If (listCompare(item\key$, value$, l\keytype, l\case_sensitive) = COMPARE_EQ) And (Not l\allowdups) Then listRaiseError(l, LISTERR_DUPLICATE)
		End If
		Return low% ; insertion position
	End If

	; calculate the middle point between low% and high%
	middle% = (high%+low%)/2;
	
	item.itemType = listItem(l, middle%) ; get list item
;	DebugLog "low="+low%+" high="+high%+" middle="+middle%+" id$="+item\id$+" value$="+value$
	
	; compare to key we are searching for in list
	If l\keytype = KEYTYPE_SPEED Then ; 
		If item\key$ = value$ Then 
			If Not l\allowdups Then listRaiseError(l, LISTERR_DUPLICATE)
			Return middle%
		End If
		If item\key$ > value$ Then Return listInsertionSearch(l, low%, middle%-1, value$)
		If item\key$ < value$ Then Return listInsertionSearch(l, middle%+1, high%, value$)
	Else ; use listCompare function, a little slow...
		If listCompare(item\key$, value$, l\keytype, l\case_sensitive) = COMPARE_EQ Then
			If Not l\allowdups Then listRaiseError(l, LISTERR_DUPLICATE)
			Return middle%
		End If
		If listCompare(item\key$, value$, l\keytype, l\case_sensitive) = COMPARE_GT Then Return listInsertionSearch(l, low%, middle%-1, value$)
		If listCompare(item\key$, value$, l\keytype, l\case_sensitive) = COMPARE_LT Then Return listInsertionSearch(l, middle%+1, high%, value$)
	End If
End Function ; listInsertionSearch()







;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Creates a new list item.
; Returns handle to listItem.
;...............................................................................
Function itemNew.itemType(key$, objecthandle=0)
;``````````````````````````````````````````````````````````````	`````````````````
	; Create & initialize new itemType object
	item.itemType = New itemType
	item\key$ 		= key$
	item\objecthandle 	= objecthandle
	
	Return(item) ; return pointer to item just added
End Function ; itemNew()





;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Creates a new list item and inserts it in the list. If list is sorted, it
; inserts it at the correct location, else it appends it to the list.
;
; Returns handle to listItem just added to list.
;...............................................................................
Function listItemAdd.itemType(l.listType, key$, objecthandle=0)
;``````````````````````````````````````````````````````````````	`````````````````

	; if list is to be sorted, use insertion for order
	If l\sort Then 
		;......................................................
		; List is Sorted
		;......................................................
		
		; find index to insert 
		listRaiseError(l, LISTERR_NONE) ; reset error flags
		index% = listInsertionSearch(l, 0, listCount(l)-1, key$)

		; Insert item into list
		If listError(l) = LISTERR_NONE Then
			item.itemType = listItemInsert(l, index%, key$, objecthandle)
			Return(item) ; return handle of list item.
		Else
			listRaiseError(l, LISTERR_DUPLICATE) ; reset error flags
			Return(Null)
		End If
	End If
	
	;......................................................
	; List is NOT sorted
	;......................................................

	; Create & initialize new itemType object
	item.itemType = itemNew(key$, objecthandle)

	; Calculate position of handle in bank
	offset = l\count * 4
	If offset >= BankSize(l\indexes) Then ResizeBank(l\indexes, offset+l\sizeincrement * 4)
		
	; store handle in bank
	PokeInt(l\indexes, offset, Handle(item))
	l\index% = l\count% ; set current item point
	l\count 	 = l\count + 1 ; increment list count
	
	Return(item) ; return pointer to item just added
End Function ; listItemAdd()





;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Return handle of list item at specific index
;...............................................................................
Function listItem.itemType(l.listType, index)
;```````````````````````````````````````````````````````````````````````````````
	If index >= l\count Or index < 0 Then RuntimeError("listItem: Item out of bounds: "+index)

	; calculate index position
	offset = index * 4
	item.itemType = Object.itemType(PeekInt(l\indexes, offset))
	Return(item)
End Function ; listItem()






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Return object handle for a list item at a specific index
;...............................................................................
Function listItemObject(l.listType, index)
;```````````````````````````````````````````````````````````````````````````````
	If index >= l\count Then RuntimeError("listItemObject: Item out of bounds: "+index)

	; calculate index position
	offset = index * 4
	item.itemType = Object.itemType(PeekInt(l\indexes, offset))
	Return(item\objecthandle)
End Function ; listItemObject()






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Return Key value for list item at specific index
;...............................................................................
Function listItemKey$(l.listType, index)
;```````````````````````````````````````````````````````````````````````````````
	If index >= l\count Then RuntimeError("listItemkey$: Item out of bounds: "+index)

	offset = index * 4
	item.itemType = Object.itemType(PeekInt(l\indexes, offset))
	Return(item\key$)
End Function ; listItemkey$






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Deletes list item at specific index and returns handle to object data
; so user can dispose of apprpriatly.
;...............................................................................
Function listItemDelete%(l.listType, index)
;```````````````````````````````````````````````````````````````````````````````
	If index >= l\count Then RuntimeError("listItemDelete: Item out of bounds: "+index)

	; Delete item for itemlist
	offset = index * 4
	item.itemType = Object.itemType(PeekInt(l\indexes, offset))
	objecthandle			= item\objecthandle ; save handle to data object.
	Delete item

	; update bank of pointers
	l\count = l\count - 1 ; reduce counter 
	; copybank(src, src offset, dest, dest offset, count bytes)
	CopyBank(l\indexes, offset+4, l\indexes, offset, (l\count - index) * 4)
	Return(objecthandle) ; return object handle so user can call destructor.
End Function ; listItemDelete()







;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Moves a list item from one position to another. 
; NOTE: This function should not be used on a SORTED list, unless you call
; listSort() afterwards.
;...............................................................................
Function listItemMove(l.listType, srcidx, destidx)
;```````````````````````````````````````````````````````````````````````````````

	; adjust bounds to make sure src and dest are within list size.
	If srcidx  > l\count-1 Then srcidx = l\count-1
	If srcidx < 0 Then srcidx = 0
	If destidx > l\count-1 Then destidx = l\count-1
	If destidx < 0 Then destidx = 0

	If srcidx = destidx Then Return ; no need to move to same spot.

	; calculate offset in bank from item indexes.
	soffset = srcidx  * 4
	doffset = destidx * 4

	; save item handle so we can move it to new position.
	itemHandle% = PeekInt(l\indexes, soffset)
	
	; if src is before dest, shift list down.
	If soffset < doffset Then
		; copybank(src, src offset, dest, dest offset, count bytes)
		CopyBank(l\indexes, soffset+4, l\indexes, soffset, doffset - soffset)
		PokeInt(l\indexes, doffset, itemHandle%)
	Else
		; else src is after dest, shift list up.
		; copybank(src, src offset, dest, dest offset, count bytes)
		CopyBank(l\indexes, doffset, l\indexes, doffset+4, soffset - doffset)
		PokeInt(l\indexes, doffset, itemHandle%)
	End If
End Function ; listItemMove()






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Creates and inserts a new list item before specified index.
;
; NOTE: This function should not be used on a SORTED list, unless you call
; listSort() afterwards.
;...............................................................................
Function listItemInsert.itemType(l.listType, index, key$, objecthandle=0)
;```````````````````````````````````````````````````````````````````````````````
	; make room if list is too small
	If (l\count * 4) >= BankSize(l\indexes) Then ResizeBank(l\indexes, (l\count+l\sizeincrement) * 4)

	; if index > list count, then add to end of list.
	If index > l\count Then 
		Return(listItemAdd(l, key$, objecthandle))
	End If

	; Create New item instance
	item.itemType = itemNew(key$, objecthandle)

	offset = index * 4 ; calculate place to insert
	
	; shift all of the indexes to make room for item to insert
	; copybank(src, src offset, dest, dest offset, count bytes)
	CopyBank(l\indexes, offset, l\indexes, offset+4, (l\count - index) * 4)
	
	; insert item in list
	PokeInt(l\indexes, offset, Handle(item))
	
	l\index% = l\count ; set current index to item
	l\count = l\count + 1 ; increment size of list
	
	Return(item)
End Function ; listItemInsert()






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Swaps the index position of two list items.  
; NOTE: This function should not be used on a SORTED list, unless you call
; listSort() afterwards.
;...............................................................................
Function listItemSwap(l.listType, srcidx, destidx)
;```````````````````````````````````````````````````````````````````````````````
	If srcidx  > l\count-1 Or srcidx  < 0 Then Return ; if src index is out of bounds
	If destidx > l\count-1 Or destidx < 0 Then Return ; if dest index is out of bounds
	If srcidx = destidx Then Return ; if src and dest are same item

	; calculate offset of index
	soffset = srcidx  * 4
	doffset = destidx * 4

	; get handles of each item from bank.
	srctemp% 	= PeekInt(l\indexes, soffset)
	desttemp% = PeekInt(l\indexes, doffset)
	; swap handles back into bank
	PokeInt(l\indexes, soffset, desttemp%)
	PokeInt(l\indexes, doffset, srctemp%)
End Function ; listItemSwaps()






;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Populates a list from a string with items delimited by sep$.  This function
; adds items in string to existing items in list.
;...............................................................................
Function listFromString(l.listType, src$, sep$="|")
;```````````````````````````````````````````````````````````````````````````````
	idx = 1
	tok$ = gettok$(src$, idx, sep$)
	While tok$ <> ""
		listItemAdd(l, tok$, 0)
		idx = idx+1
		tok$ = gettok$(src$, idx, sep$)
	Wend
End Function ; listFromString()


;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
Function listFromDir(l.listType, folder$)
;```````````````````````````````````````````````````````````````````````````````
	count = 0
	dir = ReadDir(folder$)
	
	Repeat
		file$ = NextFile$(dir)
		If file$ = "" Then Exit

		; Use FileType to determine if it is a folder (value 2) or a file		
		If FileType(folder$+"\"+file$) = 2 Then ; this is a folder
			file$ = "<"+file$+">"
		End If
		
		listItemAdd(l, file$, 0)
		count = count + 1 ; keep count of dir entries
	Forever
	CloseDir(dir)
	
	Return count
End Function ; listFromDir()





;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
Function listFromFile(l.listType, file$)
;```````````````````````````````````````````````````````````````````````````````
	fh = ReadFile(file$)
	If fh = 0 Then Return False
	
	count = 0
	While Not Eof(fh)
		listItemAdd(l, ReadLine$(fh))
		count = count + 1
	Wend
	CloseFile(fh)
	
	Return count
End Function ; listFromFile()





;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
Function listCompare%(key1$,key2$, keytype, case_sensitive)
;``````````````````````````````````````````````````````````````````````
	Select keytype
		Case KEYTYPE_INTEGER
			If Int(key1$) > Int(key2$) Then Return  COMPARE_GT
			If Int(key1$) < Int(key2$) Then Return  COMPARE_LT
			If Int(key1$) = Int(key2$) Then Return  COMPARE_EQ		
		Case KEYTYPE_FLOAT
			If Float(key1$) > Float(key2$) Then Return  COMPARE_GT
			If Float(key1$) < Float(key2$) Then Return  COMPARE_LT
			If Float(key1$) = Float(key2$) Then Return  COMPARE_EQ			
		Case KEYTYPE_STRING
			If Not case_sensitive Then	key1$ = Upper(key1$) key2$ = Upper(key2$)
			DebugLog key1$+" "+key2$+" "+Str(key1$ = key2$)
			If key1$ > key2$ Then Return  COMPARE_GT
			If key1$ < key2$ Then Return  COMPARE_LT
			If key1$ = key2$ Then Return  COMPARE_EQ			
		Case KEYTYPE_DATE
		Case KEYTYPE_TIME
		Default
			RuntimeError "listCompare: Unknown Keytype "+keytype
	End Select
End Function ; listCompare()





;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
Function listRaiseError(l.listType, errnum)
;``````````````````````````````````````````````````````````````````````
	l\error = errnum
	
	Select errnum
		Case LISTERR_NOTFOUND ; reset current if search fails
			l\index% = errnum
	End Select
End Function ; listRaiseError()




;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
Function listError%(l.listType)
;``````````````````````````````````````````````````````````````````````
	error% = l\error ; save current error
	l\error = LISTERR_NONE ; reset error flag
	Return(error%)
End Function ; listError()




;&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
; Thanks to skn3[ac] for this function.
;......................................................................
Function gettok$(from$,which,space$=" ")
;``````````````````````````````````````````````````````````````````````
	Local foundword=False
	Local mode=False
	Local current=0
	Local maketok$=""
	Local getchar$=""
	For i=1 To Len(from$)
		getchar$=Mid$(from$,i,1)
		If foundword=False Then
			If mode=False Then
				If getchar$<>space$ Then
					mode=True
					current=current+1
				End If
				If current=which Then
					foundword=True
					maketok$=maketok$+getchar$
				End If
			Else
				If getchar$=space$ Then 
					mode=False
				End If
			End If
		Else
			If getchar$=space$ Then
				Exit
			Else
				maketok$=maketok$+getchar$
			End If
		End If
	Next
	Return maketok$
End Function ; gettok$
