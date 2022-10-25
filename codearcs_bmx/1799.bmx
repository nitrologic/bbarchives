; ID: 1799
; Author: Dreamora
; Date: 2006-08-31 09:13:50
; Title: THeap
; Description: Lightweight THeap datastructure for priority queues etc

Strict
Rem
    bbdoc: Heap
    about: Heap class. Can be used for sorting as well as "get always the smallest / largest"
End Rem
Rem
Module dreamora.ds_heap

ModuleInfo "Version: 1.0"
ModuleInfo "Author: Marc 'Dreamora' Schaerer"
ModuleInfo "License: Public domain"
ModuleInfo "Copyright: 2006  Marc 'Dreamora' Schaerer"
ModuleInfo "Modserver: dreamora"

ModuleInfo "History:"
moduleinfo "-   1.0 Release"
End Rem
Import brl.retro

Rem
	bbdoc: THeap
	about: Heap class
End Rem
Type THeap
	Field _data:Object[]
	Field _ascending:Int
	Field _length:Int = 1
	Field _sortingFunction:Int(one:Object,two:Object)
	
	Rem
		bbdoc: Create
		about: Creates a new THeap and returns the reference to it.<br>
		<b>elements:</b> Number of elements you want the heap to initialize with.<br>
		The heap will dynamically resize if more elements are added than needed and that in a very performant way.<br>
		This is only if you now that you will push x object directly onto it.<br>
		Defaults to 1.<br>
		<b>maximum:</b> Defines if the heap has maximum on top (true) or minimum.<br>
		Defaults to maximum.<br>
		<b>ComparisionFunction:int( one:Object,two:Object):</b> Lets you define an own comparision function.<br>
		The example shows how to use it and for what type of stuff it might be usefull after all.<br>
		Defaults to BMs internal compare Objects functionality.
		returns: A valid reference to a THeap object
	End Rem
	Function Create:THeap(elements:Int = 16, maximum:Int = True, ComparisionFunction:Int( one:Object,two:Object)=CompareObjects)
		Local result:THeap	= New THeap
		result._data		= New Object[elements]
		result._ascending	= maximum
		result._sortingFunction	= ComparisionFunction
		Return result
	End Function
	
	Rem
		bbdoc: Add
		about: Adds a new object to the heap.<br>
		<b>obj:</b> Object you want to add to the heap.
	End Rem
	Method Add(obj:Object)
		If _length = _data.length		_data	= _data[.._data.length*2]
		_data[_length] = obj
		Local ret:Int = _bubbleUp(_length)
		If ret > 0		_siftDown(ret)
		_length	:+ 1
	End Method
	
	Rem
		bbdoc: Top
		about: Returns the top element on the heap, without removing it.
		returns: Top element on the heap
	End Rem
	Method Top:Object()
		If isEmpty()			Throw "THeap.Top: Not supported on an empty heap!"
		Return _data[1]
	End Method
	
	Rem
		bbdoc: GetTop
		about: Returns the top element on the heap, but with removing it!
		returns: Top element on the heap
	End Rem
	Method GetTop:Object()
		If isEmpty()		Throw "THeap.GetTop: Not supported on an empty heap!"
		Local tmp:Object	= _data[1]
		_data[1]	= _data[_length-1]
		_length	:- 1
		_siftDown(1)
		If _data.length <= 0.35 * _length		_data	= _data[.._data.length/2]
		Return tmp
	End Method
	
	Rem
		bbdoc: Count
		about: Returns the number of elements on the heap.
		returns: Number of elements on the heap.
	End Rem
	Method Count:Int()
		Return _length-1
	End Method
	
	Rem
		bbdoc: isEmpty
		about: Returns if the heap is empty.
		returns: True if it is empty, otherwise false.
	End Rem
	Method isEmpty:Int()
		Return _length = 1
	End Method
	
	Method _bubbleUp:Int(index:Int)
		Local tmp:Object		= _data[index]
		If _ascending
			While index > 1 And _sortingFunction(tmp,_data[index/2]) > 0
				_data[index]	= _data[index/2]
				index			:/ 2
			Wend
		Else
			While index > 1 And _sortingFunction(tmp,_data[index/2]) < 0
				_data[index]	= _data[index/2]
				index			:/ 2
			Wend
		EndIf
		_data[index]		= tmp
		Return index
	End Method
	
	Method _siftDown(index:Int)
		Local tmp:Object			= _data[index]
		Local j:Int
		Local N:Int					= _length - 1
		If _ascending
			While index <= N/2
				j	= index+index
				If _data[j+1] And _sortingFunction(_data[j + 1],_data[j]) > 0	j :+ 1
				If _sortingFunction(tmp,_data[j]) >  0					Exit
				_data[index]		= _data[j]
				index				= j
			Wend
		Else
			While index <= N/2
				j	= index+index
				If _data[j+1] And _sortingFunction(_data[j + 1],_data[j]) < 0	j :+ 1
				If _sortingFunction(tmp,_data[j]) <  0					Exit
				_data[index]		= _data[j]
				index				= j
			Wend
		EndIf
		_data[index]			= tmp
	End Method
End Type



Function CompareObjects:Int(o1:Object, o2:Object)
	Return o1.compare(o2)
End Function
