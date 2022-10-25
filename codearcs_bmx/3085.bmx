; ID: 3085
; Author: Pineapple
; Date: 2013-09-27 21:10:11
; Title: Condense objects in a list or array
; Description: Cause references to equivalent but separate objects to all point to a single instance

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


SuperStrict

Import brl.linkedlist

' Example code

Rem

Global list:TList=CreateList()

list.addlast num.Create(1)
list.addlast num.Create(2)
list.addlast num.Create(3)
list.addlast num.Create(3)
list.addlast num.Create(3)
list.addlast num.Create(4)
list.addlast num.Create(4)

Print "Before condensing:"
For Local n:num=EachIn list
	Print n.tostring()
Next

CondenseList list

Print "After condensing:"
For Local n:num=EachIn list
	Print n.tostring()
Next

Type num
	Field value%
	Function Create:num(value%)
		Local n:num=New num
		n.value=value
		Return n
	End Function
	Method tostring$()
		Return "Value: "+value+" Pointer: "+(Int(Byte Ptr(Self)))
	End Method
	Method compare%(o:Object)
		Local ovalue%=num(o).value
		If value>ovalue
			Return 1
		ElseIf value<ovalue
			Return -1
		Else
			Return 0
		EndIf
	End Method
End Type

EndRem

' Functions search for objects which get "0" (equivalency) when compared, and makes all instances refer to the first occurrence.

Function CondenseList(list:TList,compare%(o1:Object,o2:Object)=CompareObjects)
	Local link:TLink=list._head._succ
	While link<>list._head
		If link._value
			Local olink:TLink=link._succ
			While olink<>list._head
				If olink._value And link._value<>olink._value And compare(link._value,olink._value)=0 Then olink._value=link._value
				olink=olink._succ
			Wend
		EndIf
		link=link._succ
	Wend
End Function

Function CondenseArray(array:Object[],compare%(o1:Object,o2:Object)=CompareObjects)
	For Local i%=0 Until array.length
		If Not array[i] Continue
		For Local j%=i+1 Until array.length
			If array[j] And array[i]<>array[j] And compare(array[i],array[j])=0 Then array[j]=array[i]
		Next
	Next
End Function

Function Condense2DArray(array:Object[,],compare%(o1:Object,o2:Object)=CompareObjects)
	Local size%[]=array.Dimensions(),total%=size[0]*size[1]
	Local x0%=0,y0%=0,x1%=0,y1%=0
	For Local i%=0 Until total
		If array[x0,y0] Then
			x1=x0;y1=y0
			For Local j%=i+1 Until total
				x1:+1
				If x1>=size[0] Then x1=0;y1:+1
				If array[x1,y1] And array[x0,y0]<>array[x1,y1] And compare(array[x0,y0],array[x1,y1])=0 Then array[x0,y0]=array[x1,y1]
			Next
		EndIf
		x0:+1
		If x0>=size[0] Then x0=0;y0:+1
	Next
End Function
