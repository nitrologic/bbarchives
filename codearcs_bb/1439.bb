; ID: 1439
; Author: octothorpe
; Date: 2005-08-08 00:44:07
; Title: Container: hash table
; Description: (aka map, dictionary) plug'n'play hash table containers which can be used to store objects - get(key$), set(key$, value), delete(key$), iterators

;test_hashC()

; ------------------------------------------------------------------------------
;= TO DO
; ------------------------------------------------------------------------------
; iterator sanity checking (try to catch problems which might be caused by deleting elements while iterating)
; replace hashC\bucket[] array with a vectorC so hashes aren't all forced to share the same number of buckets?

; ------------------------------------------------------------------------------
;= CHANGE LOG
; ------------------------------------------------------------------------------
; 12/11/2005 - iterator sample usage for quick copying and pasting
; 06/11/2005 - new() for forwards compatibility
; 06/11/2005 - dropped humpBack notation in favour of under_score
; 06/11/2005 - sexy new hash_get_list() function for hashes-of-lists
; 03/11/2005 - renamed class from hashType
; 02/11/2005 - iterators now treat null hash objects as empty hashes
; 02/11/2005 - added hash_destroy, iterators

; ------------------------------------------------------------------------------
;= INCLUDES
; ------------------------------------------------------------------------------
include "listC.bb" ; http://blitzbasic.com/codearcs/codearcs.php?code=1438

; ------------------------------------------------------------------------------
;= CONSTANTS
; ------------------------------------------------------------------------------
const HASH_MAX_BUCKETS = 512
	; more buckets will make lookups faster in crowded hashes, but will use more memory
	; theoretically, overhead memory usage is (buckets * 16 bytes)

; ------------------------------------------------------------------------------
;= TYPES
; ------------------------------------------------------------------------------
type hashC
	field bucket.listC[HASH_MAX_BUCKETS-1]          ; a list of hashC_elems
	field elements%
end type

type hashC_elem
	field key$
	field value
end type

type hashC_iter
	field hash.hashC
	field current_bucket%
	field current_node.listC_node
	field next_node.listC_node                      ; keep track of this so we can safely delete the current_node
end type

; ------------------------------------------------------------------------------
;= FUNDAMENTAL
; ------------------------------------------------------------------------------

; ------------------------------------------------------------------------------
function hash_new.hashC()
; create a new hash

	return new hashC
end function

; ------------------------------------------------------------------------------
function hash_destroy(our_hash.hashC)
; delete a hash and all of its elements

	if our_hash = null then runtimeerror("not a valid hashC: null")

	; loop through each bucket
	for bucket_index% = 0 to HASH_MAX_BUCKETS-1
		local this_element_list.listC = our_hash\bucket[bucket_index]

		if this_element_list <> null then
			; loop through each node in each bucket's list
			local this_node.listC_node = this_element_list\first_node
			while this_node <> null
				; destroy the element
				delete object.hashC_elem(this_node\value)
				; advance to next node in list
				this_node = this_node\next_node
			wend
			; destroy the bucket
			list_destroy(this_element_list)
		endif
	next
	; destroy the hash
	delete our_hash
end function

; ------------------------------------------------------------------------------
function hash_set(our_hash.hashC, key$, value)
; set a key-value pair in a hash

	if our_hash = null then runtimeerror("not a valid hashC: null")

	; find the appropriate bucket
	local our_element_list.listC = hash_choose_bucket(our_hash, key$)

	; look for an element with a matching key
	local this_node.listC_node = our_element_list\first_node
	while this_node <> null
		local this_element.hashC_elem = object.hashC_elem(this_node\value)
	
		; if the keys match, update the value and return from the function
		if (this_element\key$ = key$) then
			this_element\value = value
			return
		endif

		; look at the next node
		this_node = this_node\next_node
	wend

	; if we didn't find an element to update, add a new element
	local new_element.hashC_elem = new hashC_elem
	new_element\key$  = key$
	new_element\value = value
	list_push(our_element_list, handle(new_element))
	
	; update element count
	our_hash\elements = our_hash\elements + 1

end function

; ------------------------------------------------------------------------------
function hash_exists(our_hash.hashC, key$)
; check to see if a key exists in a hash

	if our_hash = null then runtimeerror("not a valid hashC: null")

	if (hash_get(our_hash, key$) > 0) then
		return true
	else
		return false
	endif

end function

; ------------------------------------------------------------------------------
function hash_delete(our_hash.hashC, key$)
; delete a key-value pair in a hash, returning the value

	if our_hash = null then runtimeerror("not a valid hashC: null")

	; find the appropriate bucket
	local our_element_list.listC = hash_choose_bucket(our_hash, key$)

	; look for an element with a matching key
	local this_node.listC_node = our_element_list\first_node
	while this_node <> null
		local this_element.hashC_elem = object.hashC_elem(this_node\value)
	
		; if the keys match, remove this element and return the value
		if (this_element\key$ = key$) then
			local value = this_element\value
			delete this_element
			list_remove_node(this_node)

			; update element count
			our_hash\elements = our_hash\elements - 1

			return value
		endif

		; look at the next node
		this_node = this_node\next_node
	wend

	; if we didn't find an element to update, return 0
	return 0


end function

; ------------------------------------------------------------------------------
function hash_get(our_hash.hashC, key$)
; get a value from a hash by its key$

	if our_hash = null then runtimeerror("not a valid hashC: null")

	; find the appropriate bucket
	local our_element_list.listC = hash_choose_bucket(our_hash, key$)

	; look for an element with a matching key
	local this_node.listC_node = our_element_list\first_node
	while this_node <> null
		local this_element.hashC_elem = object.hashC_elem(this_node\value)
	
		; if the keys match, update the value and return from the function
		if (this_element\key$ = key$) then
			return this_element\value
		endif

		; look at the next node
		this_node = this_node\next_node
	wend

	; we didn't find the key, so return 0
	return 0
end function

; ------------------------------------------------------------------------------
function hash_count(our_hash.hashC)
; return the number of elements in the hash

	if our_hash = null then runtimeerror("not a valid hashC: null")
	return our_hash\elements
end function

; ------------------------------------------------------------------------------
;= INTERNAL
; ------------------------------------------------------------------------------

; ------------------------------------------------------------------------------
function hash_choose_bucket.listC(our_hash.hashC, key$)
; return the linked list which corresponds with the key$

	; find the appropriate bucket index
	local bucket_index% = hash_key_to_bucket_index(key$, HASH_MAX_BUCKETS)

	; make sure bucket has been initialized
	if (our_hash\bucket[bucket_index%] = null) then
		our_hash\bucket[bucket_index%] = list_new()
	endif

	return our_hash\bucket[bucket_index%]

end function

; ------------------------------------------------------------------------------
function hash_key_to_bucket_index%(key$, modulus%)	
; come up with an index for any string, dependably coming up with the same index 
; for the same string, and attempting to distribute strings evenly over the 
; available indexes

	local bucket_index%

	local character_index
	for character_index = 1 to len(key$)
		local character$ = mid$(key$, character_index, 1)
		bucket_index% = (bucket_index% * 123) + asc(character)            ; arbitrary math, whee!
	next

	; force the index to be valid (overflows might make the number negative)
	bucket_index% = abs(bucket_index% mod modulus)

	return bucket_index%

end function

; ------------------------------------------------------------------------------
;= ITERATORS
; ------------------------------------------------------------------------------

; sample usage

;	; for item.type = each hash
;	it.hashC_iter = hash_iterator_begin(hash)
;	while hash_iterator_next(it)
;		key$      = hash_iterator_get_key$(it)
;		item.type = object.type(hash_iterator_get_value(it))
;	wend

; ------------------------------------------------------------------------------
function hash_iterator_begin.hashC_iter(our_hash.hashC)
; create a new iterator for traversing the hash
; note that elements will be returned in arbitrary order!

	it.hashC_iter = new hashC_iter
	if our_hash <> null then
		it\hash = our_hash
		it\current_bucket = -1
		it\current_node = null
		it\next_node = null
	endif
	return it
end function

; ------------------------------------------------------------------------------
function hash_iterator_next(it.hashC_iter)
; advance the iterator to the next item in the hash

	; drop out immediately if this iterator is void
	if it\hash = null then return false
	; if we aren't already going through a bucket's list, advance until we find one
	while (it\next_node = null)
		it\current_bucket = it\current_bucket + 1
		; if we've run out of buckets, delete the iterator and return false
		if it\current_bucket >= HASH_MAX_BUCKETS then
			delete it
			return false
		endif
		
		local bucket_list.listC = it\hash\bucket[it\current_bucket]
		if (bucket_list <> null) then
			; try the first node of this bucket
			; (if the bucket list is empty, this will be null and the next bucket will be tried,
			;  otherwise the last part of this function will advance us onto this node)
			it\next_node = bucket_list\first_node
		endif
	wend
	; while we're in a bucket list, simply advance to the next node
	it\current_node = it\next_node
	it\next_node = it\current_node\next_node
	return true
end function

; ------------------------------------------------------------------------------
function hash_iterator_get_key$(it.hashC_iter)
; return the key of the element the iterator is currently on

	local this_element.hashC_elem = object.hashC_elem(it\current_node\value)
	return this_element\key$
end function

; ------------------------------------------------------------------------------
function hash_iterator_get_value(it.hashC_iter)
; return the key of the element the iterator is currently on

	local this_element.hashC_elem = object.hashC_elem(it\current_node\value)
	return this_element\value
end function

; ------------------------------------------------------------------------------
;= CONVENIENCE
; ------------------------------------------------------------------------------

; ------------------------------------------------------------------------------
function hash_get_list.listC(our_hash.hashC, key$)
; get a list from a hash by its key$, autovivifying (for hashes-of-lists)

	if our_hash = null then runtimeerror("not a valid hashC: null")

	list.listC = object.listC(hash_get(our_hash, key$))
	if list = null then
		list = list_new()
		hash_set(our_hash, key$, handle(list))
	endif
	return list
end function

; ------------------------------------------------------------------------------
;= TESTING
; ------------------------------------------------------------------------------
type hash_sample_type
	field value$
end type
function hash_sample_type_new.hash_sample_type(value$)
	i.hash_sample_type = new hash_sample_type
	i\value$ = value$
	return i
end function
function test_hashC()
	print "test_hashC()"

	local our_hash.hashC = new hashC

	hash_set(our_hash, "apples", 1)
	hash_set(our_hash, "oranges", 2)
	hash_set(our_hash, "bananas", 8)

	print "displaying all elements..."
	it.hashC_iter = hash_iterator_begin(our_hash)
	while hash_iterator_next(it)
		key$ = hash_iterator_get_key$(it)
		value = hash_iterator_get_value(it)
		print key$ + " => " + value
	wend
	
	hash_delete(our_hash, "apples")
	hash_delete(our_hash, "bananas")
	hash_delete(our_hash, "grapefruit")

	print hash_get(our_hash, "oranges")
	print hash_exists(our_hash, "oranges")
	print hash_delete(our_hash, "oranges")
	print hash_get(our_hash, "oranges")
	print hash_exists(our_hash, "oranges")

	print "displaying all elements..."
	it.hashC_iter = hash_iterator_begin(our_hash)
	while hash_iterator_next(it)
		key$ = hash_iterator_get_key$(it)
		value = hash_iterator_get_value(it)
		print key$ + " => " + value
	wend
	
	hash_destroy(our_hash)

	; test new hash_get_list() function
	print "test new hash_get_list() function..."
	
	local HoL.hashC = new hashC

	list_push(hash_get_list(HoL, "milk"), handle(hash_sample_type_new("cow")))
	list_push(hash_get_list(HoL, "milk"), handle(hash_sample_type_new("goat")))
	list_push(hash_get_list(HoL, "cheese"), handle(hash_sample_type_new("gouda")))
	list_push(hash_get_list(HoL, "cheese"), handle(hash_sample_type_new("edam")))
	list_push(hash_get_list(HoL, "cheese"), handle(hash_sample_type_new("cheddar")))

	it.hashC_iter = hash_iterator_begin(HoL)
	while hash_iterator_next(it)
		key$ = hash_iterator_get_key$(it)
		value = hash_iterator_get_value(it)

		print key$ + ":"

		list_it.listC_iter = list_iterator_begin(object.listC(value))
		while list_iterator_next(list_it)
			i.hash_sample_type = object.hash_sample_type(list_iterator_get(list_it))

			print "  " + i\value$

		wend
	wend

	print "press any key to exit..."
	waitkey
	end

end function
