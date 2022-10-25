; ID: 2799
; Author: Galaxy613
; Date: 2010-12-18 10:41:34
; Title: Simple TStack
; Description: A simple implimentation of First-in-last-out Stacks

Type TStack
	Field Top:TStackElement
	Field Bottom:TStackElement
	Field Depth% = 0
	
	Method Pop:Object()
		If Top = Null Then Return Null
		Local tmp:TStackElement = Top
		If Top <> Bottom Then
			Top = Top.Parent
		Else
			Top = Null
			Bottom = Null
		EndIf
		Depth:-1
		Return Tmp.Data
	End Method
	
	Method Push(Data:Object)
		Local tmp:TStackElement = New TStackElement
		tmp.data = data
		tmp.parent = Top
		Top = tmp
		Depth:+1
		If Bottom = Null Then Bottom = tmp
	End Method
End Type

Type TStackElement
	Field Parent:TStackElement
	Field Data:Object
End Type

''' TStack Test

Global testStack:TStack = New TStack

testStack.Push "1"
Print (String testStack.Top.data)+" "+(String testStack.Bottom.data)

testStack.Push "2"
Print (String testStack.Top.data)+" "+(String testStack.Bottom.data)

testStack.Push "3"
Print (String testStack.Top.data)+" "+(String testStack.Bottom.data)

Local tmpStr$ = String testStack.Pop()
Print (String testStack.Top.data)+" "+(String testStack.Bottom.data)+" "+tmpStr

tmpStr$ = String testStack.Pop()
Print (String testStack.Top.data)+" "+(String testStack.Bottom.data)+" "+tmpStr

tmpStr$ = String testStack.Pop()
Print tmpStr

tmpStr$ = String testStack.Pop()
