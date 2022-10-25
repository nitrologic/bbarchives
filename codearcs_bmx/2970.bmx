; ID: 2970
; Author: Pineapple
; Date: 2012-08-17 09:54:38
; Title: Heap
; Description: Just a regular old heap type module

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


SuperStrict


Rem
bbdoc: Data structures/Heaps
End Rem
Module pine.heap

ModuleInfo "Version: 1.00"
ModuleInfo "Author: Sophie Kirschner : meapineapple@gmail.com"
ModuleInfo "License: Public domain"
ModuleInfo "Modserver: pine"


	Rem
	bbdoc: Create a new heap
	returns: A new heap
	End Rem
Function CreateHeap:THeap(descending:Int=True,dynamic:Int=True,depth:Int=4,comparisonfunc:Int(o1:Object,o2:Object)=CompareObjects)
	Return THeap.Create(depth,descending,dynamic,comparisonfunc)
End Function
	Rem
	bbdoc: Clear a heap
	End Rem
Function ClearHeap(heap:THeap)
	heap.clear
End Function
	Rem
	bbdoc: Create a copy of a heap
	returns: A new heap with contents and properties identical to the original
	End Rem
Function CopyHeap:THeap(heap:THeap)
	Return heap.copy()
End Function
	Rem
	bbdoc: Insert a new value into the heap
	returns: The index of the new value in the heap's array
	End Rem
Function HeapInsert:Int(heap:THeap,value:Object)
	Return heap.insert(value)
End Function
	Rem
	bbdoc: Insert an array of new values into the heap
	End Rem
Function HeapInsertArray(heap:THeap,array:Object[])
	heap.insertarray array
End Function
	Rem
	bbdoc: Get the value in a heap's array at the given index
	returns: The object in the heap at the given @index
	about: Note that the heap starts storing objects at 1, not 0
	End Rem
Function HeapValue:Object(heap:THeap,index:Int)
	Return heap.data[index]
End Function
	Rem
	bbdoc: Get the object at the top of a heap
	returns: The object at the top of the heap
	End Rem
Function HeapTop:Object(heap:THeap)
	Return heap.top()
End Function
	Rem
	bbdoc: Get and remove the object at the top of a heap
	returns: The object at the top of the heap
	End Rem
Function HeapRemove:Object(heap:THeap)
	Return heap.removetop()
End Function
	Rem
	bbdoc: Sort the contents of some array using heapsort
	End Rem
Function HeapSortArray(array:Object[],descending:Int=True,comparisonfunc:Int(o1:Object,o2:Object)=CompareObjects)
	THeap.sortarray(array,descending,comparisonfunc)
End Function
	Rem 
	bbdoc: Get a sorted array from a heap
	returns: An array containing the sorted contents of the heap
	End Rem
Function HeapToArray:Object[](heap:THeap,reverseorder:Int=False)
	Return heap.tosortedarray(reverseorder)
End Function
	Rem 
	bbdoc: Get a heapified array from a heap
	returns: An array containing the heapified contents of the heap
	End Rem
Function HeapToUnsortedArray:Object[](heap:THeap)
	Return heap.toarray()
End Function
	Rem
	bbdoc: Get the number of values in a heap
	returns: The number of values in the heap
	End Rem
Function CountHeap:Int(heap:THeap)
	Return heap.count()
End Function
	Rem
	bbdoc: Determine whether the heap contains any values
	returns: 1 if the heap is empty, 0 if it contains any values
	End Rem
Function HeapIsEmpty:Int(heap:THeap)
	Return heap.isempty()
End Function
	Rem
	bbdoc: Merge two heaps into one
	about: All the contents of @other are copied into @heap
	End Rem
Function HeapMerge(heap:THeap,other:THeap)
	heap.merge other
End Function






	Rem
	bbdoc: A heap object
	End Rem
Type THeap
	Field data:Object[]
	Field sortfunc%(o1:Object,o2:Object)=CompareObjects
	Field dir%=True,dynamic%=True
	Field length%=1
	Field minsize%=%11111 'don't dynamically shrink if it's already this small or smaller
	Field maxsize%=$7fffffff 'don't dynamically grow if it's already this small or smaller
	Rem
	bbdoc: Sort the contents of some array using heapsort
	End Rem
	Function sortarray(array:Object[],descending:Int=False,comparisonfunc:Int(o1:Object,o2:Object)=CompareObjects)
		Local h:THeap=New THeap
		h.dynamic=False
		h.dir=descending
		h.sortfunc=comparisonfunc
		h.data=New Object[array.length+1]
		For Local x%=0 To array.length-1
			h.insert array[x]
		Next
		For Local x%=0 To array.length-1
			array[x]=h.removetop()
		Next
	End Function
	Rem
	bbdoc: Create a new heap
	returns: A new heap
	End Rem
	Function Create:THeap(depth:Int=4,maximum:Int=True,dynamic:Int=True,comparisonfunc:Int(o1:Object,o2:Object)=CompareObjects)
		Local h:THeap=New THeap
		h.dir=maximum
		h.dynamic=dynamic
		h.sortfunc=comparisonfunc
		If depth>31 Then depth=31
		If depth>=0 h.data=New Object[($ffffffff Shr (32-depth))+1] Else h.data=New Object[2]
		Return h
	End Function
	Rem
	bbdoc: Clear a heap
	End Rem
	Method clear()
		length=1
		If dynamic Then data=New Object[minsize]
	End Method
	Rem
	bbdoc: Insert a new value into the heap
	returns: The index of the new value in the heap's array
	End Rem
	Method insert%(value:Object)
		If dynamic And data.length<=maxsize Then
			While length>=data.length
				data=data[..data.length*2]
			Wend
		ElseIf length>=data.length
			Return -1
		EndIf
		data[length]=value
		Local r%=up(length)
		If r>0 r=down(r)
		length:+1
		Return r
	End Method
	Rem 
	bbdoc: Get a heapified array from a heap
	returns: An array containing the heapified contents of the heap
	End Rem
	Method toarray:Object[]()
		Return data[1..length]
	End Method
	Rem 
	bbdoc: Get a sorted array from a heap
	returns: An array containing the sorted contents of the heap
	End Rem
	Method tosortedarray:Object[](reverse%=False)
		Local tl%=length,t:Object[]=data[0..]
		Local r:Object[]=New Object[length-1]
		For Local x%=0 To length-2
			If reverse
				r[r.length-1-x]=removetop()
			Else
				r[x]=removetop()
			EndIf
		Next
		length=tl;data=t
		Return r
	End Method
	Rem
	bbdoc: Insert an array of new values into the heap
	End Rem
	Method insertarray%(array:Object[])
		If dynamic
			If length+array.length>maxsize Return 0
			If length+array.length>data.length Then data=data[..length+array.length]
		ElseIf length+array.length>=data.length
			Return 0
		EndIf
		Local r%
		For Local x%=0 To array.length-1
			data[length]=array[x]
			r=up(length)
			If r>0 down r
			length:+1
		Next
		Return 1
	End Method
	Rem
	bbdoc: Merge two heaps into one
	about: All the contents of @other are copied into @heap
	End Rem
	Method merge%(other:THeap)
		If dynamic
			If length+other.length>maxsize Return 0
			If length+other.length>data.length Then data=data[..length+other.length]
		ElseIf length+other.length>=data.length
			Return 0
		EndIf
		Local r%
		For Local x%=1 To other.length-1
			data[length]=other.data[x]
			r=up(length)
			If r>0 down r
			length:+1
		Next
		Return 1
	End Method
	Rem
	bbdoc: Create a copy of a heap
	returns: A new heap with contents and properties identical to the original
	End Rem
	Method copy:THeap()
		Local n:THeap=New THeap
		n.dir=dir
		n.length=length
		n.dynamic=dynamic
		n.minsize=minsize
		n.maxsize=maxsize
		n.sortfunc=sortfunc
		n.data=data[0..]
		Return n
	End Method
	Rem
	bbdoc: Get the object at the top of a heap
	returns: The object at the top of the heap
	End Rem
	Method top:Object()
		If length=<1 Return Null
		Return data[1]
	End Method
	Rem
	bbdoc: Get and remove the object at the top of a heap
	returns: The object at the top of the heap
	End Rem
	Method removetop:Object()
		If length<=1 Return Null
		Local t:Object=data[1]
		data[1]=data[length-1]
		length:-1
		down(1)
		If dynamic And data.length>minsize Then
			While data.length<=length/3
				data=data[..data.length/2]
			Wend
		EndIf
		Return t
	End Method
	Rem
	bbdoc: Get the number of values in a heap
	returns: The number of values in the heap
	End Rem
	Method count%()
		Return length-1
	End Method
	Rem
	bbdoc: Determine whether the heap contains any values
	returns: 1 if the heap is empty, 0 if it contains any values
	End Rem
	Method isempty%()
		Return length<=1
	End Method
	Rem
	bbdoc: Move an object up in the heap toward its proper place via its index
	returns: The new index of the same object
	End Rem
	Method up%(i%)
		Local t:Object=data[i],v%=(dir Shl 1)-1
		While i>1 And Sgn(sortfunc(t,data[i/2]))=v
			data[i]=data[i/2]
			i:/2
		Wend
		data[i]=t
		Return i
	End Method
	Rem
	bbdoc: Move an object down in the heap toward its proper place via its index
	returns: The new index of the same object
	End Rem
	Method down%(i%)
		Local t:Object=data[i],j%,n%=(length-1)/2,v%=(dir Shl 1)-1
		While i<=n
			j=i+i
			If data[j+1] And Sgn(sortfunc(data[j+1],data[j]))=v Then j:+1
			If Sgn(sortfunc(t,data[j]))=v Exit
			data[i]=data[j]
			i=j
		Wend
		data[i]=t
		Return i
	End Method
	Rem
	bbdoc: Method implements #EachIn support
	returns: An iterator object
	about: Iterates through the objects in the heap
	End Rem
	Method ObjectEnumerator:THeapEnum()
		Local e:THeapEnum=New THeapEnum
		e.data=data
		e.length=length
		Return e
	End Method
End Type

Type THeapEnum
	Field i%=0,data:Object[],length%=0
	Method HasNext%()
		Return (i+1)<length
	End Method
	Method NextObject:Object()
		i:+1
		Return data[i]
	End Method
End Type



Function CompareObjects:Int(o1:Object, o2:Object)
	Return o1.compare(o2)
End Function
