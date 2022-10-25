; ID: 2704
; Author: Yasha
; Date: 2010-04-22 17:06:59
; Title: String types
; Description: Use strings to store composite data

;StringTypes
;===========

; This mini-lib shows how to use the basic B3D string type as a composite data
; type, allowing you to store multiple pieces of information while making use of
; B3D's very limited garbage collector.

; Use
;-----

; Declare a new string type with the Type_ST function. This returns a pointer to
; the new type object. You can then add fields to this type with Field_ST.

; You can instantiate the type with New_ST. This will return an empty data string.

; Enter data with the SetField function. Because strings are value types, you'll
; need to use the form:  myString = SetField(myString,"myField",myData).
; Access data with the GetField function.

; Once you have finished with the type, you can free it with FreeStringType. Note
; that after a type has been freed, or if you add more fields to it, you will not
; be able to access the data in any remaining data strings.

; Pros
;------

; Strings are versatile; they can be loaded, saved, sent; they can store ints and
; floats without loss of data; any Type can be cast to Str to store its value, or
; to Int with Handle(). Strings can also hold any kinds of binary data, cast byte
; by byte with Chr(). It is also safe to store one data string inside another.

; As a consequence, the stringtypes are all untyped and can store any kind of data
; in any field, even another data string.

; Strings are value-types; when assigned to a new variable the data is copied and
; can be modified without affecting the original.

; Strings are automatically garbage-collected; this is the only way to use the GC
; in Blitz3D without ugly workarounds.

; Because the string types are created at runtime, they don't need to be hardcoded
; and allow types to be loaded from a data file and modified at runtime.

; Cons
;------

; There is no way to handle strings as reference types without also losing the GC
; advantage (ie. locking them inside a type object, or converting them to a bank).
; This means that storing one data string inside another creates a complete new
; copy of its data and modifying it will not affect the original.

; Strings are easily the slowest datatype in Blitz3D, orders of magnitude slower
; than banks.

; How it works
;--------------

; Each datastring consists of a header and a body. The first four bytes are the 
; handle of its stringtype. After this is a four-byte pointer for each data field
; in the type, giving the data's position in the string. The final four bytes give
; the pointer to the end of the string; thus accessing a field uses two pointers,
; to get its position and size. The body of the string consists of the raw data
; of each field.


;=================================================================================
; Library
;=================================================================================


;Types
;=====

Type StringType
	Field name$		;Type name
	Field noFields	;Number of fields
	Field fList.StringTypeField
End Type

Type StringTypeField	;Field entry
	Field name$					;Field name
	Field nx.StringTypeField	;Next in this type
End Type


;Functions
;=========

Function Type_ST.StringType(name$)	;Create a new string type with the given name
	Local newST.StringType
	newST=New StringType
	newST\name=name
	Return newST
End Function

Function Field_ST(st.StringType,name$)	;Add a field to a StringType
	Local f.StringTypeField
	
	If st\fList=Null
		st\fList=New StringTypeField
		st\fList\name=name
	Else
		f=st\fList
		While f\nx<>Null
			f=f\nx
		Wend
		f\nx=New StringTypeField
		f\nx\name=name
	EndIf
	
	st\noFields=st\noFields+1
End Function

Function New_ST$(st.StringType)		;Create a new instance of ST. Use like New
	Local obj$,dat$,i
	
	For i=1 To st\noFields+1		;Plus one to provide a cap
		obj=obj+IntToString((st\noFields+2)*4+i)
		dat=dat+Chr(0)
	Next
	
	Return IntToString(Handle(st))+obj+dat
End Function

Function GetField$(obj$,fName$)
	Local st.StringType,f.StringTypeField,i,ptr;,tail$
	
	st=Object.StringType(StringToInt(Mid(obj,1,4)))
	If st=Null Then Return ""	;Type apparently does not exist
	
	f=st\fList
	For i=1 To st\noFields		;i will thus give us the index of the field
		If f\name=fName Then Exit
		f=f\nx
	Next
	If f=Null Then Return ""	;Field apparently does not exist
	
	ptr=StringToInt(Mid(obj,(i*4)+1,4))		;Get the pointer to the start of the field in the string body
	i=StringToInt(Mid(obj,(i+1)*4+1,4))		;Get the pointer to the start of the next field (or end cap)
	
	Return Mid(obj,ptr,(i-1)-ptr)
End Function

Function SetField$(obj$,fName$,value$)
	Local uid$,st.StringType,f.StringTypeField,i
	Local ptr,lDiff,head$,body$,tail$,div
	
	st=Object.StringType(StringToInt(Mid(obj,1,4)))
	If st=Null Then Return obj		;The data is inaccessible without the type, but let's not discard it without asking
	
	f=st\fList
	For i=1 To st\noFields		;i will thus give us the index of the field
		If f\name=fName Then Exit
		f=f\nx
	Next
	If f=Null Then Return obj	;Field apparently does not exist
	
	ptr=StringToInt(Mid(obj,(i*4)+1,4))	;Get the pointer to the start of the field in the string body
	
	tail=Mid(obj,ptr)
	div=StringToInt(Mid(obj,(i+1)*4+1,4))
	lDiff=Len(value)-(div-1-ptr)	;Get the difference in size between old and new data blocks
	obj=Left(obj,ptr-1)+value+Mid(obj,div-1)
	head=Left(obj,i*4+4)
	body=Mid(obj,st\noFields*4+9)
	
	For ptr=i+1 To st\noFields+1
		head=head+IntToString(StringToInt(Mid(obj,ptr*4+1,4))+lDiff)
	Next
	
	Return head+body
End Function

Function GetStringType.StringType(name$)	;Get an StringType by its name
	Local st.StringType
	For st=Each StringType
		If st\name=name Then Return st
	Next
	Return Null
End Function

Function FreeStringType(st.StringType)		;Delete a StringType once done with it
	Local f.StringTypeField,n.StringTypeField
	f=st\fList
	While f<>Null
		n=f
		f=f\nx
		Delete n
	Wend
	Delete st
End Function

Function IntToString$(i)	;Convert an int to the binary-equivalent string
	Return Chr(i And $FF)+Chr((i And $FF00) Shr 8)+Chr((i And $FF0000) Shr 16)+Chr((i And $FF000000) Shr 24)
End Function

Function StringToInt(s$)	;Convert a string to the binary-equivalent int
	Return Asc(Mid(s,1,1)) Or (Asc(Mid(s,2,1)) Shl 8) Or (Asc(Mid(s,3,1)) Shl 16) Or (Asc(Mid(s,4,1)) Shl 24)
End Function


;=================================================================================


;=================================================================================
;Example
;=================================================================================

Graphics 900,500,32,6

Local mytype.StringType=Type_ST("mytest")
Field_ST(mytype,"a")
Field_ST(mytype,"b")
Field_ST(mytype,"c")
Field_ST(mytype,"d")
Field_ST(mytype,"e")

Local obj$=New_ST(mytype)

Print GetField(obj,"a")	;Just to show that these are empty
Print GetField(obj,"b")
Print GetField(obj,"c")
Print GetField(obj,"d")
Print GetField(obj,"e")

obj=SetField(obj,"a","Hello World!")
obj=SetField(obj,"b","Electric Boogaloo")
obj=SetField(obj,"c","Neomorphic Geshelphobe")
obj=SetField(obj,"d","She moves in mysterious ways")
obj=SetField(obj,"e","Days a Stranger")

Print GetField(obj,"a")
Print GetField(obj,"b")
Print GetField(obj,"c")
Print GetField(obj,"d")
Print GetField(obj,"e")

obj=SetField(obj,"b","Now is the time for all good men to come to the aid of THIS IS A REALLY LONG STRING OK")
obj=SetField(obj,"b","..no more")
obj=SetField(obj,"d","short")

Print ""	;Need a separator onscreen
Print GetField(obj,"a")
Print GetField(obj,"b")
Print GetField(obj,"c")
Print GetField(obj,"d")
Print GetField(obj,"e")

Print ""	;You can safely store one data string inside another
obj=SetField(obj,"c",obj)
Print GetField(obj,"c")


WaitKey
End

;=================================================================================
