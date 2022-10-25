; ID: 787
; Author: skn3[ac]
; Date: 2003-09-04 14:28:49
; Title: Recursive child search
; Description: Will search for an entity in children recursively. Using only 1 function call, no function calling itself over and over to search.

;### name   : 1-call recursive child search ###
;### by     : jonathan pittock (skn3)       ###
;### contact: skn3@acsv.net                 ###
;### www    : www.acsv.net                  ###

;This value is used to size the buffer bank below. If the data needs more space,
;it will resize the bank in blocks of the amount below. (in bytes, 1k = 1024 bytes)
Const recursive_resize=1024

;This bank is used in each call to the search function. It is outside the function,
;as creating and deleting over and over from memory, can cause fragmentation, not...
;to mention slow downs.
Global recursive_bank=CreateBank(recursive_resize),recursive_size=recursive_resize

;These are misc values, having them defined as global speeds up the function as...
;they don't need to be created/destroyed each time the function is called
Global recursive_entity,recursive_parent,recursive_id,recursive_start,recursive_total,recursive_offset

;The function
;It will return the entity if found, or 0 if not.
;MyChild=findchildentity(entity,"child name")
Function findchildentity(entity,name$)
	name$=Lower$(name$)
	recursive_parent=entity
	recursive_start=1
	recursive_offset=0
	.recursive_label
		recursive_total=CountChildren(recursive_parent)
		For recursive_id=recursive_start To recursive_total
			recursive_entity=GetChild(recursive_parent,recursive_id)
			If name$=Lower$(EntityName$(recursive_entity))
				Return recursive_entity
			Else
				If recursive_offset+8 > recursive_size-1
					ResizeBank(recursive_bank,recursive_size+recursive_resize)
					recursive_size=recursive_size+recursive_resize
				End If
				PokeInt(recursive_bank,recursive_offset,recursive_id+1)
				PokeInt(recursive_bank,recursive_offset+4,recursive_parent)
				recursive_offset=recursive_offset+8
				recursive_start=1
				recursive_parent=recursive_entity
				Goto recursive_label
			End If
		Next
		If recursive_offset=0
			Return 0
		Else
			recursive_start=PeekInt(recursive_bank,recursive_offset-8)
			recursive_parent=PeekInt(recursive_bank,recursive_offset-4)
			recursive_offset=recursive_offset-8
			Goto recursive_label
		End If
End Function
