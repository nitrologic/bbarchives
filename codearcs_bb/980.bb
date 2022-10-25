; ID: 980
; Author: Koriolis
; Date: 2004-03-28 11:46:16
; Title: bank -&gt; pointer and object -&gt; pointer conversion
; Description: Get the pointer for a banks or object, and say bye bye to multiple declarations of DLL functions just for the sake of handling the "NULL pointer parameter case"

;.lib "kernel32.dll"
;ptr_helper__%(a*,b%,c%):"MulDiv"


; Converts a bank handle to the memory pointer. 
; Useful to call functions from external DLLs that expect pointers
; as parameters. Especially if the pointer is allowed to be NULL, because
; then the usual solution is to have 2 declarations of the same function,
; one with the said parameter declared with "*", the other with "%".
; OK that's maybe not very well explained, so if anyone wants to explain
; it properly, feel free :)
; WARNING: don't use it to pass a pointer to a funciton if you
;          need a very very fast call, as there is a slight
;          overhead (here ptr_helper__ is used a no op, but in
;          fact it does something and thus consumes takes
;          a little time to execute; in normal cases this overhead
;          is simply peanuts)
Function ptr%(b)
	Return ptr_helper__(b, 1, 1)
End Function




; Also the same can be done for types, but then you need a version of the function
; for each type:

Type MyType
	Field a%
	Field b#
End Type

Function MyType_ptr%(obj.MyType)
	Return ptr_helper__(obj, 1, 1)
End Function



; === Example
; === Not a common case usage, but it shows it works ===
; === For a real life usage, see this thread http://www.blitzbasic.com/Community/posts.php?topic=31927
Graphics 640, 480, 2
;.lib "kernel32.dll"
;RtlMoveMemory(Destination%, Source%, Length%) ; copy memory from [Source, Source+Length[ to [Destination, Destination+Length[


srcBank% = CreateBank(15)
For i = 0 To 14 ; we fill srcBank
	PokeByte(srcBank, i, 3*i)
Next

destBank% = CreateBank(15)

; The way we declared 'RtlMoveMemory', it directly expects pointers (as integers)
; We'll get them using 'ptr'
RtlMoveMemory(ptr(destBank), ptr(srcBank), 15)

; we check srcBank has been copied into destBank
For i = 0 To 14
	Print PeekByte(destBank, i)
Next



; OK, it works, now we'll check for types:

srcObj.MyType = New MyType
srcObj\a = 123
srcObj\b = 1.2345
destObj.MyType = New MyType

Print "srcObj=" + Str(srcObj) + ", destObj=" + Str(destObj)

; Now we copy srcObj int dstObj
RtlMoveMemory(MyType_ptr(destObj), MyType_ptr(srcObj), 8) ; Sizeof MyType = 8 bytes


; Check that now destObj = srcObj
Print "srcObj=" + Str(srcObj) + ", destObj=" + Str(destObj)
