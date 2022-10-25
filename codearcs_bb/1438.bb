; ID: 1438
; Author: octothorpe
; Date: 2005-08-08 00:25:11
; Title: Container: double-linked list
; Description: plug'n'play list containers which can be used to store objects - push, pop, shift, unshift, delete, iterators

;list_test()

; ------------------------------------------------------------------------------
;= TO DO
; ------------------------------------------------------------------------------
; iterator sanity checking (try to catch problems which might be caused by deleting elements while iterating)

; ------------------------------------------------------------------------------
;= CHANGE LOG
; ------------------------------------------------------------------------------
; 12/11/2005 - iterator sample usage for quick copying and pasting
; 12/11/2005 - list_find_node_by_position()
; 06/11/2005 - new() for forwards compatibility
; 06/11/2005 - dropped humpBack notation in favour of under_score
; 03/11/2005 - renamed class from listType
; 02/11/2005 - iterators now treat null list objects as empty lists
; 12/08/2005 - fixed iterators to work with empty lists, added list_unshift() and list_destroy()
; 16/08/2005 - added list_insertBefore(), list_insertAfter(), list_findNodeByValue().
;              revised iterators to allow: multiple simultaneous iterators, reverse iterators,
;              safe deletion of the current node (not safe with multiple iterators!)

; ------------------------------------------------------------------------------
;= TYPES
; ------------------------------------------------------------------------------
type listC
	field elements%                       ; number of elements in list
	field first_node.listC_node
	field last_node.listC_node
end type

type listC_node
	field list.listC                      ; parent list
	field prev_node.listC_node            ; or null if this is the first node
	field next_node.listC_node            ; or null if this is the last node
	field value
end type

type listC_iter
	field list.listC
	field forwards                        ; bool to keep track of which direction we're going
	field current_node.listC_node
	field next_node.listC_node            ; keep track of this so we can safely delete the current_node
end type

; ------------------------------------------------------------------------------
;= FUNDAMENTAL
; ------------------------------------------------------------------------------

; ------------------------------------------------------------------------------
function list_new.listC()
; create a new list

	return new listC
end function

; ------------------------------------------------------------------------------
function list_destroy(our_list.listC)
; delete a list and all of its nodes

	if our_list = null then runtimeerror("not a valid listC: null")
	
	; walk through the nodes, deleting them
	this_node.listC_node = our_list\first_node
	while (this_node <> null)
		old_node.listC_node = this_node
		this_node = this_node\next_node
		delete old_node
	wend
	
	; finally, delete the list itself
	delete our_list
end function

; ------------------------------------------------------------------------------
function list_push.listC_node(our_list.listC, value)
; add a new node to the end of a linked list

	if our_list = null then runtimeerror("not a valid listC: null")

	; create a new node
	local our_node.listC_node = new listC_node
	our_node\list = our_list
	our_node\value = value

	; if the list is empty, set our node as the first and last
	if (our_list\first_node = null) then

		our_list\first_node = our_node
		our_list\last_node  = our_node
		our_list\elements   = 1

	; otherwise, add our node after the last
	else

		our_list\last_node\next_node = our_node
		our_node\prev_node           = our_list\last_node
		our_list\last_node           = our_node

		our_list\elements = our_list\elements + 1

	endif

	return our_node

end function

; ------------------------------------------------------------------------------
function list_unshift.listC_node(our_list.listC, value)
; add a new node to the beginning of a linked list

	if our_list = null then runtimeerror("not a valid listC: null")

	; create a new node
	local our_node.listC_node = new listC_node
	our_node\list = our_list
	our_node\value = value

	; if the list is empty, set our node as the first and last
	if (our_list\first_node = null) then

		our_list\first_node = our_node
		our_list\last_node  = our_node
		our_list\elements   = 1

	; otherwise, add our node before the first
	else

		our_list\first_node\prev_node = our_node
		our_node\next_node            = our_list\first_node
		our_list\first_node           = our_node

		our_list\elements = our_list\elements + 1

	endif

	return our_node

end function

; ------------------------------------------------------------------------------
function list_pop(our_list.listC)
; remove an element from the end of a linked list, returning its value

	return list_remove_node(our_list\last_node)
end function

; ------------------------------------------------------------------------------
function list_shift(our_list.listC)
; remove an element from the beginning of a linked list, returning its value

	return list_remove_node(our_list\first_node)
end function

; ------------------------------------------------------------------------------
function list_insert_after.listC_node(reference_node.listC_node, value)
; add a new node after an existing one

	if reference_node = null then runtimeerror("not a valid listC_node: null")

	; create a new node
	local our_node.listC_node = new listC_node
	our_node\list = reference_node\list
	our_node\value = value

	our_node\prev_node = reference_node
	our_node\next_node = reference_node\next_node
	reference_node\next_node = our_node
	
	if our_node\next_node <> null then
		our_node\next_node\prev_node = our_node

	else
		our_node\list\last_node = our_node
	endif

	our_node\list\elements = our_node\list\elements + 1

	return our_node

end function

; ------------------------------------------------------------------------------
function list_insert_before.listC_node(reference_node.listC_node, value)
; add a new node before an existing one

	if reference_node = null then runtimeerror("not a valid listC_node: null")

	; create a new node
	local our_node.listC_node = new listC_node
	our_node\list = reference_node\list
	our_node\value = value

	our_node\next_node = reference_node
	our_node\prev_node = reference_node\prev_node
	reference_node\prev_node = our_node
	
	if our_node\prev_node <> null then
		our_node\prev_node\next_node = our_node
	else
		our_node\list\first_node = our_node
	endif

	our_node\list\elements = our_node\list\elements + 1

	return our_node

end function

; ------------------------------------------------------------------------------
function list_remove_node(our_node.listC_node)
; remove an arbitrary element from a linked list, returning its value

	; return 0 if we're trying to return an element that doesn't exist (e.g. empty_list\last_node)
	if (our_node = null) then return 0

	; if there's a node before this one, it gets our next_node as its new next_node (or null if this is the last node)
	if (our_node\prev_node <> null) then
		our_node\prev_node\next_node = our_node\next_node
	endif

	; if there's a node after this one, it gets our prev_node as its new prev_node (or null if this is the first node)
	if (our_node\next_node <> null) then
		our_node\next_node\prev_node = our_node\prev_node
	endif

	; if this was the first node, the next node is now the first node (or null if the list is now empty)
	if (our_node = our_node\list\first_node) then
		our_node\list\first_node = our_node\next_node
	endif

	; if this was the last node, the prev node is now the last node (or null if the list is now empty)
	if (our_node = our_node\list\last_node) then
		our_node\list\last_node = our_node\prev_node
	endif

	; update the number of elements in the list
	our_node\list\elements = our_node\list\elements - 1

	; destroy the node, returning its value
	local value = our_node\value
	delete our_node
	return value

end function

; ------------------------------------------------------------------------------
function list_count(our_list.listC)
	return our_list\elements
end function

; ------------------------------------------------------------------------------
;= ITERATORS
; ------------------------------------------------------------------------------

; sample usage

;	; for item.type = each list
;	it.listC_iter = list_iterator_begin(list)
;	while list_iterator_next(it)
;		item.type = object.type(list_iterator_get(it))
;	wend

; ------------------------------------------------------------------------------
function list_iterator_begin.listC_iter(our_list.listC)
; create a new iterator for traversing the list forwards

	it.listC_iter = new listC_iter
	if our_list <> null then
		it\list = our_list
		it\forwards = true
		it\current_node = null
		it\next_node = our_list\first_node
	endif
	return it
end function

; ------------------------------------------------------------------------------
function list_iterator_begin_reverse.listC_iter(our_list.listC)
; create a new iterator for traversing the list backwards

	it.listC_iter = new listC_iter
	if our_list <> null then
		it\list = our_list
		it\forwards = false
		it\current_node = null
		it\next_node = our_list\last_node
	endif
	return it
end function

; ------------------------------------------------------------------------------
function list_iterator_next(it.listC_iter)
; advance the iterator to the next item in the list

	; drop out immediately if this iterator is void
	if it\list = null then return false
	; if there's a next node, advance to it and return true
	if it\next_node <> null then
		it\current_node = it\next_node
		if it\forwards then
			it\next_node = it\current_node\next_node
		else
			it\next_node = it\current_node\prev_node
		endif
		return true
	; otherwise, destroy the iterator and return false
	else
		delete it
		return false
	endif
end function

; ------------------------------------------------------------------------------
function list_iterator_get(it.listC_iter)
; return the value of the element the iterator is currently on

	return it\current_node\value
end function

; ------------------------------------------------------------------------------
;= CONVENIENCE
; ------------------------------------------------------------------------------
function list_find_node_by_value.listC_node(our_list.listC, value)
; find the first node in a list matching the value given

	if our_list = null then runtimeerror("not a valid listC: null")

	this_node.listC_node = our_list\first_node
	while (this_node <> null)
		if this_node\value = value then return this_node
		; advance
		this_node = this_node\next_node
	wend
	return null
end function

; ------------------------------------------------------------------------------
function list_find_node_by_position.listC_node(our_list.listC, index)
; find the Nth node in a list

	if our_list = null then runtimeerror("not a valid listC: null")

	if index < 0 then index = our_list\elements - index

	; make sure there are enough nodes
	if index < 0 or index > our_list\elements-1 then return null

	this_node.listC_node = our_list\first_node
	for i = 0+1 to index
		this_node = this_node\next_node
	next
	return this_node
end function

; ------------------------------------------------------------------------------
;= TESTING
; ------------------------------------------------------------------------------
type list_sample_type_1
	field value$
end type
type list_sample_type_2
	field list_node.listC_node ; keep track of the node in sample_list which points to us
	field value$
end type
function list_test()
	print "list_test()"

	; ---
	print "example 1: the basics"

	; create some objects
	a.list_sample_type_1 = new list_sample_type_1 : a\value$ = "bar"
	b.list_sample_type_1 = new list_sample_type_1 : b\value$ = "baaz"
	c.list_sample_type_1 = new list_sample_type_1 : c\value$ = "quux"

	; create a list and add our objects
	sample_list_1.listC = new listC
	list_push(sample_list_1, handle(a))
	list_push(sample_list_1, handle(b))
	list_push(sample_list_1, handle(c))

	; display the objects in the list
	print "listing all elements..."
	it.listC_iter = list_iterator_begin(sample_list_1)
	while list_iterator_next(it)
		item_1.list_sample_type_1 = object.list_sample_type_1(list_iterator_get(it))
		print "  " + item_1\value$
	wend
	
	; destroy the objects
	; for item_1.list_sample_type_1 = each sample_list_1
	it.listC_iter = list_iterator_begin(sample_list_1)
	while list_iterator_next(it)
		item_1.list_sample_type_1 = object.list_sample_type_1(list_iterator_get(it))
		delete item_1
	wend
	
	; destroy the list (and its nodes)
	list_destroy(sample_list_1)
	
	; ---
	print "example 2: keeping track of nodes"

	; create some objects
	x.list_sample_type_2 = new list_sample_type_2 : x\value$ = "fish"
	y.list_sample_type_2 = new list_sample_type_2 : y\value$ = "percolator"
	z.list_sample_type_2 = new list_sample_type_2 : z\value$ = "cheese"

	; create a list and add our objects (keeping track of the list nodes)
	sample_list_2.listC = new listC
	x\list_node = list_push(sample_list_2, handle(x))
	y\list_node = list_push(sample_list_2, handle(y))
	z\list_node = list_insert_after(x\list_node, handle(z))

	; display the objects in the list
	print "listing all elements..."
	it.listC_iter = list_iterator_begin(sample_list_2)
	while list_iterator_next(it)
		item_2.list_sample_type_2 = object.list_sample_type_2(list_iterator_get(it))
		print "  " + item_2\value$
	wend

	; remove an object from the list
	list_remove_node(z\list_node) : z\list_node = null

	; shift the remaining elements off the list, displaying their \value$s
	print "remaining elements..."
	while (sample_list_2\elements > 0)
		item_2.list_sample_type_2 = object.list_sample_type_2(list_shift(sample_list_2))
		print "  " + item_2\value$
	wend

	; destroy the list (and its nodes)
	list_destroy(sample_list_2)

	; delete the objects
	delete x
	delete y
	delete z
	
	; ---
	print "press any key to exit..."
	waitkey
	end

end function
