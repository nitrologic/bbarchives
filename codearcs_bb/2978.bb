; ID: 2978
; Author: Yasha
; Date: 2012-09-15 13:49:55
; Title: Semiautomatic reference counting
; Description: Objective-C 1.0 style memory management for B3D (needs FastPointer)

; Retain/release/autorelease style memory management
; Requires FastPointer

Type RefCounted
	Field rc, free, obj
End Type

Type AutoReleasePool
	Field objs, count, capacity
End Type


Const AUTORELEASE_POOL_START = 1024, RELEASABLE_OFFSET = 20


Function Retain(this.RefCounted)
	this\rc = this\rc + 1
End Function

Function Release(this.RefCounted)
	DebugLog "Called Release on 0x" + Hex(this\obj) + " with refcount " + Hex(this\rc)
	this\rc = this\rc - 1
	If this\rc < 1 Then CallFunctionVarInt this\free, this\obj : Delete this
End Function

Function AutoRelease(this.RefCounted)
	Local p.AutoReleasePool = Last AutoReleasePool
	If p\count = p\capacity Then p\capacity = p\capacity * 2 : ResizeBank p\objs, p\capacity * 4
	PokeInt p\objs, p\count * 4, TypePointer(this) - RELEASABLE_OFFSET
	DebugLog "Autoreleased 0x" + Hex(this\obj)
	p\count = p\count + 1 : Retain this
End Function

Function NewRefCounted.RefCounted(free, obj)
	Local rc.RefCounted = New RefCounted
	rc\free = free : rc\obj = obj - RELEASABLE_OFFSET
	Return rc
End Function

Function NewAutoReleasePool.AutoReleasePool(capacity = AUTORELEASE_POOL_START)
	Local p.AutoReleasePool = New AutoReleasePool
	p\objs = CreateBank(capacity * 4)
	p\capacity = capacity
	Return p
End Function

Function ClearAutoReleasePool(p.AutoReleasePool)
	Local r.RefCounted, rPtr = FunctionPointer() : Goto skip : Release r
	.skip
	Local o : For o = 0 To p\count - 1
		CallFunctionVarInt rPtr, PeekInt(p\objs, o * 4)
	Next
	FreeBank p\objs : Delete p
End Function


;==============================================================================
; Example
;==============================================================================


Type Foo
	Field rc.RefCounted
	Field a, b#, c$
End Type

Function NewFoo.Foo(a_, b_#, c_$)
	Local f.Foo = New Foo
	f\a = a_ : f\b = b_ : f\c = c_
	
	Local freePtr = FunctionPointer() : Goto skip : FreeFoo f
	.skip
	
	f\rc = NewRefCounted(freePtr, TypePointer(f))
	DebugLog "Created new Foo at 0x" + Hex(TypePointer(f))
	Return f
End Function

Function FreeFoo(this.Foo)
	DebugLog "Ran FreeFoo on 0x" + Hex(TypePointer(this))
	;...
	Delete this
End Function

Graphics 800, 400, 32, 6
Local p.AutoReleasePool = NewAutoReleasePool()

Local i : For i = 1 To 10
	Local f.Foo = NewFoo(1, 2.0, "three")
	AutoRelease f\rc
	If i > 4 Then Retain f\rc
Next

ClearAutoReleasePool p


WaitKey
End


;~IDEal Editor Parameters:
;~C#Blitz3D
