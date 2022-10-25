; ID: 2964
; Author: Pineapple
; Date: 2012-07-27 15:21:20
; Title: Stack
; Description: Fully featured array-based stack type with options for both static and dynamic sizing and including EachIn support.

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict


' Example code
Rem
Local stack:AStack=CreateStack()
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
		If size=>data.length Then
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
