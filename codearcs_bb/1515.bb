; ID: 1515
; Author: octothorpe
; Date: 2005-11-02 05:17:08
; Title: Container: vector
; Description: plug'n'play vector containers which can be used to store objects

;vector_test()

; ------------------------------------------------------------------------------
;= TO DO
; ------------------------------------------------------------------------------
; iterator sanity checking (try to catch problems which might be caused by deleting elements while iterating)

; ------------------------------------------------------------------------------
;= CHANGE LOG
; ------------------------------------------------------------------------------
; 12/11/2005 - iterator sample usage for quick copying and pasting
; 12/11/2005 - negative index values now represent elements in reverse order,
;              added remove_element(), insert_element_*(), shift(), unshift(),
;              and iterators
; 12/11/2005 - calling new() is now required
; 06/11/2005 - new() for forwards compatibility
; 03/11/2005 - renamed class from vectorType

; ------------------------------------------------------------------------------
;= CONSTANTS
; ------------------------------------------------------------------------------
const VECTORS_EXPAND_BY = 20
	; instead of resizing our bank every push() operation, grab some extra space

; ------------------------------------------------------------------------------
;= TYPES
; ------------------------------------------------------------------------------
type vectorC
	field last_element%                   ; size of the container - 1
	field elements_allocated%             ; size of the bank (not in bytes)
	field bank
end type

type vectorC_iter
	field vector.vectorC
	field forwards                        ; bool to keep track of which direction we're going
	field current_index%
end type

; ------------------------------------------------------------------------------
;= FUNDAMENTAL
; ------------------------------------------------------------------------------

; ------------------------------------------------------------------------------
function vector_new.vectorC(elements% = 0)
; create a new vector

	our_vector.vectorC = new vectorC
	our_vector\bank = createbank(0)
	vector_resize(our_vector, elements)   ; also sets \last_element to elements-1
	return our_vector
end function

; ------------------------------------------------------------------------------
function vector_destroy(our_vector.vectorC)
; delete a vector and all of its elements

	freebank(our_vector\bank)
	delete our_vector
end function

; ------------------------------------------------------------------------------
function vector_set(our_vector.vectorC, index%, value)
; set value at specified index

	if our_vector = null then runtimeerror("not a valid vectorC: null")
	if (index < 0) then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	if (index < 0 or index > our_vector\last_element) then runtimeerror("element out of range: "+index)
	pokeint(our_vector\bank, 4 * index, value)
end function

; ------------------------------------------------------------------------------
function vector_get(our_vector.vectorC, index%)
; get value at specified index

	if our_vector = null then runtimeerror("not a valid vectorC: null")
	if (index < 0) then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	if (index < 0 or index > our_vector\last_element) then runtimeerror("element out of range: "+index)
	return peekint(our_vector\bank, 4 * index)
end function

; ------------------------------------------------------------------------------
function vector_resize(our_vector.vectorC, elements%)
; set the size of the vector

	if our_vector = null then runtimeerror("not a valid vectorC: null")
	resizebank(our_vector\bank, 4 * elements)
	our_vector\elements_allocated = elements
	our_vector\last_element = elements - 1
end function

; ------------------------------------------------------------------------------
function vector_count(our_vector.vectorC)
; return the number of elements in the vector

	if our_vector = null then runtimeerror("not a valid vectorC: null")
	return our_vector\last_element + 1
end function

; ------------------------------------------------------------------------------
;= STACK OPERATIONS
; ------------------------------------------------------------------------------
function vector_push(our_vector.vectorC, value)
; add a new element to the end of the vector

	if our_vector = null then runtimeerror("not a valid vectorC: null")
	; allocate more space if required
	if our_vector\elements_allocated-1 = our_vector\last_element then
		our_vector\elements_allocated = our_vector\elements_allocated + VECTORS_EXPAND_BY
		resizebank(our_vector\bank, 4 * our_vector\elements_allocated)
	endif
	; store the new value
	our_vector\last_element = our_vector\last_element + 1
	pokeint(our_vector\bank, 4 * our_vector\last_element, value)
end function

; ------------------------------------------------------------------------------
function vector_pop(our_vector.vectorC)
; remove an element from the end of the vector

	if our_vector = null then runtimeerror("not a valid vectorC: null")
	if our_vector\last_element = -1 then return 0
	our_vector\last_element = our_vector\last_element - 1
	return peekint(our_vector\bank, 4 * (our_vector\last_element + 1))
end function

; ------------------------------------------------------------------------------
function vector_alloc(our_vector.vectorC, elements%)
; preallocate a bunch of space for push() operations

	if our_vector = null then runtimeerror("not a valid vectorC: null")
	if elements < our_vector\last_element + 1 then runtimeerror("cannot alloc less space than is currently being used")
	our_vector\elements_allocated = elements
	resizebank(our_vector\bank, 4 * our_vector\elements_allocated)
end function

; ------------------------------------------------------------------------------
;= SLOW OPERATIONS
; ------------------------------------------------------------------------------
function vector_remove_element(our_vector.vectorC, index%)
; remove an element from anywhere in a vector

	if our_vector = null then runtimeerror("not a valid vectorC: null")
	if (index < 0) then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	if (index < 0 or index > our_vector\last_element) then runtimeerror("element out of range: "+index)
	value = peekint(our_vector\bank, 4 * index)
	our_vector\last_element = our_vector\last_element - 1
	; move elements backward to cover our hole
	copybank(our_vector\bank, 4*(index+1), our_vector\bank, 4*index, 4*(our_vector\last_element+1 - index))
	return value
end function

; ------------------------------------------------------------------------------
function vector_insert_before(our_vector.vectorC, index%, value)
; add a new element before an existing one
; note that we allow a reference index of our_vector\last_element+1 (which is dispatched to vector_push() anyways)


	if our_vector = null then runtimeerror("not a valid vectorC: null")
	if (index < 0) then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	if index = our_vector\last_element+1 then return vector_push(our_vector, value) ; much faster alternative!
	if (index < 0 or index > our_vector\last_element) then runtimeerror("element out of range: "+index)
	; allocate more space if required
	if our_vector\elements_allocated-1 = our_vector\last_element then
		our_vector\elements_allocated = our_vector\elements_allocated + VECTORS_EXPAND_BY
		resizebank(our_vector\bank, 4 * our_vector\elements_allocated)
	endif
	; move elements forward
	copybank(our_vector\bank, 4*(index), our_vector\bank, 4*(index+1), 4*(our_vector\last_element+1 - index))
	our_vector\last_element = our_vector\last_element + 1
	; set our new value
	pokeint(our_vector\bank, 4 * index, value)
end function

; ------------------------------------------------------------------------------
function vector_insert_after(our_vector.vectorC, index%, value)
; add a new element after an existing one
	if (index < 0) then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	return vector_insert_before(our_vector, index+1, value)
end function

; ------------------------------------------------------------------------------
function vector_shift(our_vector.vectorC)
; add a new element after an existing one
	return vector_remove_element(our_vector, 0)
end function

; ------------------------------------------------------------------------------
function vector_unshift(our_vector.vectorC, value)
; add a new element to the front of the vector
	vector_insert_before(our_vector, 0, value)
end function

; ------------------------------------------------------------------------------
;= ITERATORS
; ------------------------------------------------------------------------------

; sample usage

; ; for item.type = each vector
;	it.vectorC_iter = vector_iterator_begin(vector)
;	while vector_iterator_next(it)
;		item.type = object.type(vector_iterator_get(it))
;	wend


; ------------------------------------------------------------------------------
function vector_iterator_begin.vectorC_iter(our_vector.vectorC)
; create a new iterator for traversing the vector forwards

	it.vectorC_iter = new vectorC_iter
	if our_vector <> null then
		it\vector = our_vector
		it\forwards = true
		it\current_index = 0-1
	endif
	return it
end function

; ------------------------------------------------------------------------------
function vector_iterator_begin_reverse.vectorC_iter(our_vector.vectorC)
; create a new iterator for traversing the vector backwards

	it.vectorC_iter = new vectorC_iter
	if our_vector <> null then
		it\vector = our_vector
		it\forwards = false
		it\current_index = our_vector\last_element+1
	endif
	return it
end function

; ------------------------------------------------------------------------------
function vector_iterator_next(it.vectorC_iter)
; advance the iterator to the next element in the vector

	; drop out immediately if this iterator is void
	if it\vector = null then return false
	
	if it\forwards = true then
		it\current_index = it\current_index + 1
		if it\current_index > it\vector\last_element then delete it : return false
	else
		it\current_index = it\current_index - 1
		if it\current_index < 0 then delete it : return false
	endif
	return true
end function

; ------------------------------------------------------------------------------
function vector_iterator_get(it.vectorC_iter)
; return the value of the element the iterator is currently on

	return peekint(it\vector\bank, 4 * it\current_index)
end function

; ------------------------------------------------------------------------------
;= TESTING
; ------------------------------------------------------------------------------
function vector_test()
	print "vector_test()"

	sample_vector.vectorC = vector_new(2)

	vector_set(sample_vector, 0, 123)
	vector_set(sample_vector, 1, 456)
	vector_push(sample_vector, 789)
	vector_unshift(sample_vector, 321)
	vector_insert_after(sample_vector, -1, -100)

	it.vectorC_iter = vector_iterator_begin(sample_vector)
	while vector_iterator_next(it)
		value = vector_iterator_get(it)
		print value
	wend
	

	print "press any key to exit..."
	waitkey
	end

end function
