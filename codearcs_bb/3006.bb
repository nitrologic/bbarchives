; ID: 3006
; Author: Yasha
; Date: 2012-11-29 23:16:17
; Title: Garbage collector for Blitz3D/+
; Description: FULLY automated memory management for use with BB custom type objects

; Garbage collection for Blitz3D/+
;==================================

; Requires FastPointer DLL (free): http://www.fastlibs.com/index.php

Const GC_REFCOUNT_OFFSET = -4, GC_OPTR_OFFSET = -20, GC_CELLSIZE = 12, GC_PTR_OFS = 0, GC_LC_OFS = 4, GC_DTOR_OFS = 8
Const GC_SHRINK_THRESH = 25, GC_LISTSIZE = 128 * GC_CELLSIZE, GC_DEFAULT_AGGRO = 25
Global GC_private_Aggro_, GC_private_ACount_, GC_private_ObjList_, GC_private_ListTop_, GC_private_Active_


; Initialize the garbage collector system
Function GC_Init()
	If GC_private_ObjList_ Then RuntimeError "GC already initialized!"
	GC_private_ObjList_ = CreateBank(GC_LISTSIZE * GC_CELLSIZE)
	GC_private_ListTop_ = 0
	GC_private_Aggro_ = GC_DEFAULT_AGGRO
	GC_private_Active_ = True
End Function

; Set an object to be tracked by the collector. This may trigger a GC run
Function GC_Track(ptr, dtor)
	If Not GC_private_ObjList_ Then RuntimeError "GC not initialized!"
	If Not dtor Then RuntimeError "Destructor function must not be null!"
	
	PokeInt GC_private_ObjList_, GC_private_ListTop_ + GC_PTR_OFS, ptr
	PokeInt GC_private_ObjList_, GC_private_ListTop_ + GC_DTOR_OFS, dtor
	PokeInt GC_private_ObjList_, GC_private_ListTop_ + GC_LC_OFS, 0
	
	GC_private_ListTop_ = GC_private_ListTop_ + GC_CELLSIZE		;Extend the list if necessary
	If GC_private_ListTop_ >= BankSize(GC_private_ObjList_) Then ResizeBank	GC_private_ObjList_, GC_private_ListTop_ * 2
	
	GC_private_ACount_ = GC_private_ACount_ + 1
	If GC_private_Active_
		If GC_private_ACount_ >= GC_private_Aggro_ And GC_private_Aggro_ > 0 Then GC_CollectNow
	EndIf
End Function

; Set the number of objects allowed to be created between GC runs
Function GC_SetAggressiveness(aggro)
	If Not GC_private_ObjList_ Then RuntimeError "GC not initialized!"
	GC_private_Aggro_ = aggro
End Function

; Force a GC run: called automatically for the most part
Function GC_CollectNow()
	If GC_private_Active_
		
		Local o : For o = GC_private_ListTop_ - GC_CELLSIZE To 0 Step -GC_CELLSIZE	;Start with most recent
			If PeekInt(GC_private_ObjList_, o + GC_LC_OFS) < 1
				Local rc = MemoryFastPeekInt(PeekInt(GC_private_ObjList_, o + GC_PTR_OFS) + GC_REFCOUNT_OFFSET)
				If rc < 2
					If rc = 1	;Only the type list: collect
						Local dtor = PeekInt(GC_private_ObjList_, o + GC_DTOR_OFS)
						CallFunctionVarInt dtor, PeekInt(GC_private_ObjList_, o + GC_PTR_OFS) + GC_OPTR_OFFSET
					EndIf		;Could also have been deleted earlier, in which case just remove
					GC_private_ListTop_ = GC_private_ListTop_ - GC_CELLSIZE
					If GC_private_ListTop_ >= 0
						CopyBank GC_private_ObjList_, GC_private_ListTop_, GC_private_ObjList_, o, GC_CELLSIZE
					EndIf
				EndIf
			EndIf
		Next
		
		Local sz = BankSize(GC_private_ObjList_)	;Shrink the list if it's largely empty
		If sz > GC_LISTSIZE * GC_CELLSIZE
			If (GC_private_ListTop_ * 100) / sz < GC_SHRINK_THRESH Then ResizeBank GC_private_ObjList_, sz / 2
		EndIf
		
		GC_private_ACount_ = 0
	EndIf
End Function

; Pause garbage collection until GC_Resume is called
Function GC_Suspend()
	If Not GC_private_ObjList_ Then RuntimeError "GC not initialized!"
	GC_private_Active_ = False
End Function

; Resume garbage collection
Function GC_Resume()
	If Not GC_private_ObjList_ Then RuntimeError "GC not initialized!"
	GC_private_Active_ = True
	If GC_private_ACount_ >= GC_private_Aggro_ And GC_private_Aggro_ > 0 Then GC_CollectNow
End Function

; Increment the lock count of an object, protecting it from GC
Function GC_LockObject(ptr)
	Local o = GC_GetOffset_(ptr) : If o >= 0
		PokeInt GC_private_ObjList_, o + GC_LC_OFS, PeekInt(GC_private_ObjList_, o + GC_LC_OFS) + 1
	Else
		RuntimeError "Invalid object pointer for GC to lock: 0x" + Hex(ptr)
	EndIf
End Function

; Decrement the lock count of an object, potentially relinquishing it for GC
Function GC_UnlockObject(ptr)
	Local o = GC_GetOffset_(ptr) : If o >= 0
		PokeInt GC_private_ObjList_, o + GC_LC_OFS, PeekInt(GC_private_ObjList_, o + GC_LC_OFS) - 1
	Else
		RuntimeError "Invalid object pointer for GC to unlock: 0x" + Hex(ptr)
	EndIf
End Function

; (Internal) Get the position in the object list of the given pointer
Function GC_GetOffset_(ptr)
	Local o : For o = GC_private_ListTop_ - GC_CELLSIZE To 0 Step -GC_CELLSIZE
		If PeekInt(GC_private_ObjList_, o) = ptr Then Return o
	Next
	Return -1
End Function


;~IDEal Editor Parameters:
;~C#Blitz3D
