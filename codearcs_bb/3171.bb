; ID: 3171
; Author: _PJ_
; Date: 2015-01-04 05:25:06
; Title: Single Byte Arrays
; Description: Memory Efficient Single-Byte Arrays Using Banks

;Single-Byte Array functions by PJ 2015


;Example:

a=DefineArray(16777215)
Print "Please check memory"
WaitKey()
FreeArray a

Dim b(16777215)
Print "Please check memory again"
WaitKey()
End





;Declarations

Type Array
	Field ArrayBankHandle
	Field ArrayX
	Field ArrayY
End Type

;Functions
Function DefineArray(x=1,y=1)
	y=Abs(y)
	x=Abs(x)
	
	If (Not (x Or y))
		RuntimeError "Array must have dimensions"
	End If
	
	Local NewArray.Array=New Array
	NewArray\ArrayX=x
	NewArray\ArrayY=y
	NewArray\ArrayBankHandle=CreateBank(x*y)
	Return Handle(NewArray)
End Function

Function SetArrayElement(Array, Value, x=1, y=1)
	Local Instance.Array=Object.Array(Array)
	Local Reference=ArrayReference(x,y,Instance\ArrayX)
	PokeByte Instance\ArrayBankHandle,Reference,Value
End Function

Function GetArrayElement(Array,x=1,y=1)
	Local Instance.Array=Object.Array(Array)
	Local Reference=ArrayReference(x,y,Instance\ArrayX)
	Local Element=PeekByte(Instance\ArrayBankHandle,Reference)
	Return Element
End Function

Function ResizeArray(Array,x,y=1)
	Local Instance.Array=Object.Array(Array)
	
	Local OldSize=BankSize(Instance\ArrayBankHandle)
	Local NewSize=(x*y)
	
	If (NewSize=OldSize)
		Return
	End If
	
	If (NewSize<OldSize)
		Local Byte
		For Byte=NewSize To OldSize-1
			PokeByte Instance\ArrayBankHandle,Byte,0
		Next
		OldSize=NewSize
	End If
	
	Local NewBank=CreateBank(NewSize)
	CopyBank Instance\ArrayBankHandle,0,NewBank,0,OldSize-1
	
	FreeBank Instance\ArrayBankHandle
	
	Instance\ArrayBankHandle=NewBank
	
	Instance\ArrayX=x
	Instance\ArrayY=y
	
End Function

Function FreeArray(Array)
	Local Instance.Array=Object.Array(Array)
	FreeBank Instance\ArrayBankHandle
	Instance\ArrayX=0
	Instance\ArrayY=0
	Delete Instance
End Function

Function ArrayReference(x,y,xmax)
	Local Ref=y-1
	Ref=Ref*xmax
	Ref=Ref+x
	Ref=Ref-1
	Return Ref
End Function
