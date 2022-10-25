; ID: 2964
; Author: Madk
; Date: 2012-07-27 15:21:20
; Title: Stack
; Description: Fully featured array-based stack type with options for both static and dynamic sizing.

' This code originally written by Sophie Kirschner (meapineapple@gmail.com) and released as public domain.



' Example code
Rem
Local stack:Astack=CreateStack()
StackPush stack,"First"
StackPush stack,"Second"
StackPush stack,"Third"
Print String(StackPop(stack))
Print String(StackPop(stack))
Print String(StackPop(stack))
EndRem



' Types

Rem
bbdoc: Array-based stack type.
EndRem
Type AStack Abstract
	Rem 
	bbdoc: Initializes and returns a new AStack object.
	about: Dynamic stacks are slightly slower, but their size will automatically adjust as they become filled or empty.
	EndRem
	Function Create:AStack(size%=64,dynamic%=True)
		If dynamic Then Return AStackDynamic.make(size)
		Return AStackStatic.make(size)
	End Function
	Function make:AStack(size%) Abstract
	
	Field data:Object[] ' AStack data
	Field size% ' Current stack size
	Rem
	bbdoc: Resize an AStack.
	about: Preserves existing values.
	EndRem
	Method resize(newsize%)
		data=data[..newsize] 
		size=Min(size,newsize)
	End Method
	Rem
	bbdoc: Resize an AStack.
	about: Does not preserve existing values.
	EndRem
	Method setsize(newsize%)
		data=New Object[newsize]
		size=0
	End Method
	Rem
	bbdoc: Get the maximum size of the stack.
	returns: The current length of the data array.
	EndRem
	Method maxsize%()
		Return data.length
	End Method
	Rem
	bbdoc: Pushes a new value onto the AStack.
	EndRem
	Method push(value:Object) Abstract
	Rem
	bbdoc: Pops and returns the top value of the AStack.
	EndRem
	Method pop:Object() Abstract
	Rem
	bbdoc: Returns the top value of the AStack.
	EndRem
	Method peek:Object()
		If size=<0 Then Return Null
		Return data[size-1]
	End Method
	Rem
	bbdoc: Returns the bottom value of the AStack.
	EndRem
	Method peekbottom:Object()
		If size=<0 Then Return Null
		Return data[0]
	End Method
	Rem
	bbdoc: Returns the number of objects currently in the AStack.
	EndRem
	Method count%()
		Return size
	End Method
	Rem
	bbdoc: Returns an array containing all elements in the AStack.
	EndRem
	Method toarray:Object[]()
		Return data[..size]
	End Method
	Rem
	bbdoc: Object iterator for EachIn support.
	EndRem
	Method ObjectEnumerator:AStackEnum()
		Local n:AStackEnum=New AStackEnum ' Create a new enumerator object
		n.stack=Self
		Return n
	End Method
End Type

Rem
bbdoc: Static AStack type.
EndRem
Type AStackStatic Extends AStack
	Function make:AStackStatic(size%)
		Local this:AStackStatic=New AStackStatic
		this.setsize size
		Return this
	End Function
	Method push(value:Object)
		Assert size<data.length,"Can't push object: stack is full." ' Guard against stack overflow
		data[size]=value
		size:+1
	End Method
	Method pop:Object()
		If size=<0 Then Return Null
		size:-1
		Return data[size]
	End Method
End Type

Rem
bbdoc: Dynamic AStack type.
EndRem
Type AStackDynamic Extends AStack
	Function make:AStackDynamic(size%)
		Local this:AStackDynamic=New AStackDynamic
		this.setsize size
		Return this
	End Function
	Method push(value:Object)
		If dynamic And size=>data.length Then
			resize(data.length*2)
		EndIf
		data[size]=value
		size:+1
	End Method
	Method pop:Object()
		If size=<0 Then Return Null
		If size>64 And (size Shr 2)<data.length Then resize(data.length/2)
		size:-1
		Return data[size]
	End Method
End Type

Rem
bbdoc: Object iterator for EachIn support.
EndRem
Type AStackEnum
	Field stack:AStack
	Field index%=0
	Method NextObject:Object()
		Local i%=index
		index:+1
		Return stack.data[i]
	End Method
	Method HasNext%()
		Return index<stack.size
	End Method
End Type

' Wrapper functions

Rem 
bbdoc: Initializes and returns a new AStack object.
about: Dynamic stacks are slightly slower, but their size will automatically adjust as they become filled or empty.
EndRem
Function CreateStack:AStack(size:Int=64,dynamic:Int=True)
	Return AStack.Create(size,dynamic)
End Function
Rem
bbdoc: Resize an AStack.
about: Preserves existing values.
EndRem
Function StackResize(stack:AStack,size:Int)
	stack.resize size
End Function
Rem
bbdoc: Resize an AStack.
about: Does not preserve existing values.
EndRem
Function SetStackSize(stack:AStack,size:Int)
	stack.setsize size
End Function
Rem
bbdoc: Get the maximum size of the stack.
returns: The current length of the data array.
EndRem
Function StackSize:Int(stack:AStack)
	Return stack.maxsize()
End Function
Rem
bbdoc: Returns the number of objects currently in the AStack.
EndRem
Function CountStack:Int(stack:AStack)
	Return stack.count()
End Function
Rem
bbdoc: Pushes a new value onto the AStack.
EndRem
Function StackPush(stack:AStack,value:Object)
	stack.push value
End Function
Rem
bbdoc: Returns the top value of the AStack.
EndRem
Function StackPop:Object(stack:AStack)
	Return stack.pop()
End Function
Rem
bbdoc: Returns the top value of the AStack.
EndRem
Function StackPeek:Object(stack:AStack)
	Return stack.peek()
End Function
Rem
bbdoc: Returns the bottom value of the AStack.
EndRem
Function StackPeekBottom:Object(stack:AStack)
	Return stack.peekbottom()
End Function
Rem
bbdoc: Returns an array containing all elements in the AStack.
EndRem
Function StackToArray:Object[](stack:AStack)
	Return stack.toarray()
End Function
