; ID: 175
; Author: AngelEyes
; Date: 2001-12-31 05:46:40
; Title: Basic Stack structure
; Description: Create, Push, Pop, Free onto an Integer stack

Graphics3D 640,480,16,2
SetBuffer BackBuffer()

Type test
	Field c
End Type

Global mystack.stack = Stack_Create(5)

Global camera=CreateCamera()
Global sprite.test=New test
sprite\c = CreateCube()

PositionEntity camera,0,0,-10
PositionEntity sprite\c,0,0,2.5

While Not KeyHit(1)
	Cls
	Stack_Push(mystack,3421)
	Stack_Push(mystack,3431)
	Stack_Push(mystack,3441)
	Stack_Push(mystack,3451)
	
	Stack_Push(mystack,Handle(sprite))
	
	a.test = Object.test(Stack_Pop(mystack))
	TurnEntity a\c,1,2,3
	
	RenderWorld
	Text 10,10,Stack_Pop(mystack)
	Text 10,30,Stack_Pop(mystack)
	Text 10,50,Stack_Pop(mystack)
	Text 10,70,Stack_Pop(mystack)
	Text 10,90,mystack\size
	Flip
Wend

Stack_Destroy(mystack)
FreeEntity sprite\c

End

; The Stack code

Type stack
	Field pointer
	Field size
	Field stack
End Type

Function Stack_Create.stack(size%)  	; size is number of Integer elements, not bytes
	s.stack = New stack
	s\pointer = 0
	s\size = size
	s\stack = CreateBank(size*4)
	Return s
End Function

Function Stack_Push(s.stack, value%) ; pushes an integer onto the stack, returns true for success, false for failed
	If s\pointer < BankSize(s\stack) Then
		PokeInt s\stack,s\pointer,value
		s\pointer = s\pointer + 4
		Return True
	Else
		Return False
	End If
End Function

Function Stack_Pop%(s.stack) ; gets the top integer back, -1
	If s\pointer > 0 Then
		s\pointer = s\pointer - 4
		Return PeekInt(s\stack, s\pointer)	
	Else
		Return -1
	End If
End Function

Function Stack_Destroy(s.stack)
	FreeBank(s\stack)
	Delete s
End Function
