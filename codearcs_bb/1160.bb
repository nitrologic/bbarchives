; ID: 1160
; Author: Nilium
; Date: 2004-09-12 20:10:39
; Title: Push/Pop Data Functions 2
; Description: Functions to push and pop data to and from stacks (created from types)

;#Region DESCRIPTION
	;; Stack sub-system used for organization of objects
;#End Region

;#Region CLASSES
	Type Stack
		Field F.StackObject
		Field L.StackObject
		Field Objects%
	End Type
	
	Type StackObject
		Field Content$
		Field Class$
		Field N.StackObject
		Field P.StackObject

		Field Parent.Stack
		Field z
	End Type
	
	Global STACK_Class$
	Global STACK_Content$
;#End Region

;#Region PROCEDURES
	Function CreateStack( )
		Local s.Stack = New Stack
		
		Return Handle( s )
	End Function
	
	Function PushObject( Stack , Content$ , Class$="", ToFront = 0 )
		Local s.Stack = Object.Stack (Stack )
		
		If s = Null Then Return 0
		
		Local Index = s\Objects
		
		Local i.StackObject = New StackObject
		
		i\Content = Content
		i\Class = Class
		i\Parent = s
		
		If ToFront <= 0 Then
			Local l.StackObject = s\L
			If l <> Null Then
				l\N = i
				i\P = l
			EndIf
			s\L = i
			If s\F = Null Then s\F = i
		Else
			Local f.StackObject = s\F
			If f <> Null Then
				f\P = i
				i\N = f
			EndIf
			s\F = i
			If s\L = Null Then s\L = i
		EndIf
		
		i\Z = s\Objects
		
		s\Objects = s\Objects + 1
		
		If DEVELOP And LOG_STACK Then DebugLog "Push "+Stack +" "+ Index +" "+ Content
		
		Return Index
	End Function
	
	Function PopObject$( Stack , FromFront = 0 )
		Local s.Stack = Object.Stack( Stack )
		
		Local Content$ = "",Class$ = ""
		
		If s = Null Then Return Content
		
		If FromFront <= 0 Then
			If s\L = Null Then Return Content
			
			Local l.StackObject = s\L
			
			If l\P <> Null Then
				l\P\N = Null
				s\L = l\P
			EndIf
			
			If s\L = Null Then s\L = s\F
			
			Content = l\Content
			Class = l\Class
			
			Delete l
		Else
			If s\F = Null Then Return Content
			
			Local f.StackObject = s\F
			
			If f\P <> Null Then
				f\N\P = Null
				s\F = f\N
			EndIf
			
			If s\F = Null Then s\F = s\L
			
			Content = f\Content
			Class = f\Class
			
			Delete f
		EndIf
		
		If DEVELOP And LOG_STACK Then DebugLog "Pop "+Stack+" "+Content
		STACK_Content = Content
		STACK_Class = Class
		Return Content
	End Function
	
	Function GetObject$( Stack, Index, RemoveData=0 )
		Local Content$ = "",Class$ = ""
		
		Local s.Stack = Object.Stack( Stack )
		
		If s = Null Then Return Content
		
		Local i
		Local f.StackObject = s\F
		
		If f = Null Then Return Contents
		
		For i = 0 To Index-1
			If f\N = Null Then Exit
			f = f\N
		Next
		
		Content = f\Content
		Class = f\Class
		
		If RemoveData > 0 Then
			If f\N <> Null Then f\N\P = f\P
			If f\P <> Null Then f\P\N = f\N
			
			If s\L = f Then s\L = f\P
			If s\F = f Then s\F = f\N
			
			s\Objects = s\Objects - 1
			
			Delete f
		EndIf
		
		If DEVELOP And LOG_STACK Then DebugLog "Get "+Stack+" "+Index+" "+Content+" "+RemoveData
		
		STACK_Class = Class
		STACK_Content = Content
		
		Return Content
	End Function
	
	Function GetObjectF#( Stack, Index, RemoveData=0 )
		Return Float(GetObject(Stack,Index,RemoveData))
	End Function
	
	Function GetObjectI%( Stack, Index, RemoveData=0 )
		Return Int(GetObject(Stack,Index,RemoveData))
	End Function
	
	Function InsertObject( Stack, At, Content$, Class$="" )
		Local s.Stack = Object.Stack( Stack )
		
		If s = Null Then Return -1
		
		Local i
		Local f.StackObject = s\F
		
		If f = Null Then Return PushObject( Stack, Content)
		
		For i = 0 To At-1
			If f\N = Null Then Exit
			f = f\N
		Next
		
		Local n.StackObject = New StackObject
		n\Content = Content
		n\Class = Class
		n\Parent = s
		
		If f <> Null Then
			n\N = f\N
			n\P = f
			f\N = n
		EndIf
		
		s\Objects = s\Objects + 1
		
		If DEVELOP And LOG_STACK Then DebugLog "Insert "+Stack+" "+At+" "+Content
		
		Return i
	End Function
	
	Function MoveObject( Stack, TakeFrom, MoveTo )
		Local cont$ = GetObject(Stack,TakeFrom,1)
		Return InsertObject(Stack, MoveTo-1,cont$,STACK_Class)
	End Function
	
	Function MoveObjectToFront( Stack, Index )
		Local cont$ = GetObject(Stack,Index,1)
		Return PushObject(Stack,cont$,1)
	End Function
	
	Function MoveObjectToBack( Stack, Index )
		Local cont$ = GetObject(Stack,Index,1)
		Return PushObject(Stack,cont$,0)
	End Function
	
	Function Objects( Stack )
		Local s.Stack = Object.Stack( Stack )
		
		If s = Null Then Return -1
		
		Return s\Objects
	End Function
	
	Function FreeStack( Stack )
		Local s.Stack = Object.Stack( Stack )
		
		If s = Null Then Return 0
		
		Delete s
		
		Local i.StackObject = Null
		
		For i = Each StackObject
			If i\Parent = Null Then
				Delete i
			EndIf
		Next
		
		If DEVELOP And LOG_STACK Then DebugLog "Free "+Stack
		
		Return 1
	End Function
	
	Function Debug_StackContents(stack)
		For i = 0 To Objects(stack)-1
			s$ = s$+" | "+GetObject(stack,i)
		Next
		
;		If Right(s$,2) = "| " Then s$ = Left(s$,Len(s)-3)
		DebugLog s$
	End Function
;#End Region
