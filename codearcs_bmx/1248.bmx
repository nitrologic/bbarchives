; ID: 1248
; Author: Filax
; Date: 2004-12-29 06:21:25
; Title: Simple Type
; Description: This is a simple method to learn Type

Graphics 800,600,0

' ---------------------------------
' Init a new type list
' ---------------------------------
Global MyList:TList=CreateList() 
Global MyID

' ----------------
' Type creation
' ----------------
Type MonTest
	Field ID
	Field Px#
	Field Py#
	Field MySpeed#=0.2
	Field MyDirection%=Rand(0,360)
	
	' ------------------------------
	' Redraw
	' ------------------------------
	Method Refresh() 
		SetColor 255,255,255
		DrawRect PX#,PY#,30,30 
		DrawText ID,Px#,Py#-15
	End Method 
	
	' --------------------------------
	' Move rectangle
	' --------------------------------
	Method Go()
		Px#:+MySpeed#*Cos(MyDirection%)
		Py#:+MySpeed#*Sin(MyDirection%)
		
		
		' -----------------------------------------------------------
		' When mousedown i delete type number 5
		' -----------------------------------------------------------
		If Mousedown(1) Then
			If ID=5 Then
				MyList.remove(Self)
			EndIf
		EndIf
	End Method 
End Type

' -----------------------
' Creation des rectangles
' -----------------------
Create()

' -----------------
' Main Loop
' -----------------
While Not KeyDown(Key_Escape)
	For T:MonTest= EachIn MyList
		T.Refresh()
		T.Go()
	next

	Flip
	Cls
Wend 

' -----------------------
' Rect creation
' -----------------------
Function Create()
	For i=1 To 5
		MyID=MyID+1
		
		NewProto:MonTest= New MonTest
		NewProto.ID=MyID
		NewProto.Px#=Rand(5,700)
		NewProto.Py#=Rand(5,500)	
		
		' ---------------------------------
		' Add this entry in the type list
		' ---------------------------------
		ListAddLast MyList,NewProto
	next
End Function
