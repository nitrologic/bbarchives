; ID: 2060
; Author: Azathoth
; Date: 2007-07-10 01:54:15
; Title: Lambda
; Description: Lambda Types/Functions

Strict

Type Lambda

	Field args:TList=CreateList()

	Method PushArg(a:LType)
		args.AddLast(a)
	EndMethod
	
	Method GetArg:LType(i)
		Return LType(args.ValueAtIndex(i))
	EndMethod

	Method Invoke:LType()
	EndMethod
EndType

Type LAdd Extends Lambda

	Function Add:LAdd(x:LType,y:LType)
		Local n:LAdd=New LAdd
		
		n.PushArg(x); n.PushArg(y)
		Return n
	EndFunction
	
	Method Invoke:LType()
		Local r:LType=LType.Create(String(GetArg(0).ToDouble()+GetArg(1).ToDouble()),LType.Num)
		
		Return r
	EndMethod
EndType

Type LMul Extends Lambda

	Function Mul:LMul(x:LType,y:LType)
		Local n:LMul=New LMul
		
		n.PushArg(x); n.PushArg(y)
		Return n
	EndFunction
	
	Method Invoke:LType()
		Local r:LType=LType.Create(String(GetArg(0).ToDouble()*GetArg(1).ToDouble()),LType.Num)
		
		Return r
	EndMethod
EndType

Type LType
	Const None=0
	Const Lambda=1
	Const Num=2
	Const Str=3

	Field _data:Object
	Field _type:Int
	
	Function Create:LType(value:Object=Null,t=None)
		Local n:LType=New LType
		n.Set(value,t)
		Return n
	EndFunction
	
	Method Set(value:Object=Null,t=None)
		If t=None And value<>Null		' Guess type
			If .Lambda(value)
				_type=LType.Lambda
			ElseIf String(value)
				_type=Str
			EndIf
		Else
			_type=t
		EndIf
		_data=value
	EndMethod
	
	Method ToString:String()
		If _type=Num Or _type=Str
			Return String(_data)
		ElseIf _type=LType.Lambda
			Return .Lambda(_data).Invoke().ToString()
		EndIf
	EndMethod
	
	Method ToDouble:Double()
		Return ToString().ToDouble()
	EndMethod
	
EndType


' *** Test

Local test:Lambda=LMul.Mul(.. 
	LType.Create("10"),LType.Create(LAdd.Add( LType.Create("1"),LType.Create("1") ))..
	)

Print "10*(1+1)"								
Print test.Invoke().ToString()
Print "--~n"

' *** Test with 'variables'


Local UserArg1:LType=New LType

test=LMul.Mul(.. 
	UserArg1,LType.Create(LAdd.Add( LType.Create("1"),LType.Create("1") ))..
	)
								

UserArg1.Set("100")
Print "UserArg1=100"
Print "UserArg1*(1+1)"
Print test.Invoke().ToString()
