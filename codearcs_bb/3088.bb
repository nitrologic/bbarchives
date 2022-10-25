; ID: 3088
; Author: zoqfotpik
; Date: 2013-11-16 09:29:11
; Title: RTS Drag Select
; Description: Demonstrates Drag Select for RTS games

' RTS Drag Select
' By ZoqFotPik


Type dragrect ' a mousedrag rect
	Field upperleft:Int, upperright:Int, lowerleft:Int, lowerright:Int
End Type

Global objects:TList = New TList

Type selectobject
	Field x:Int
	Field y:Int
	Field selected:Int
	Method draw()
		SetColor 255,255,255
		DrawRect x-5,y-5,10,10
		
		If selected = 1 
			SetColor 0,255,0
			DrawRect x-10,y-10,15,15
		EndIf
	End Method
End Type
		
Graphics 640, 480 

For i = 1 To 100
	Local tempobj:selectobject = New selectobject
	tempobj.x = Rand(640)
	tempobj.y = Rand(480)
	objects.addlast(tempobj)
Next

Global selectx:Int
Global selecty:Int ' these are the x and y origins of a select box
Global selectflag:Int ' is a selectbox active?

Print leastof(5,10)
Print greaterof(5,10)
While Not KeyHit(KEY_ESCAPE)

Cls

If MouseDown(1) 
	If Selectflag = 0
		selectflag = 1
		selectx = MouseX()
		selecty = MouseY()
	EndIf
EndIf
	

Local tempobj2:selectobject = New selectobject

For tempobj2 = EachIn objects
	tempobj2.draw()
Next

If selectflag = 1 
	SetColor 0,255,0
	DrawLine selectx, selecty, MouseX(), selecty
	DrawLine MouseX(),selecty, MouseX(),MouseY()
	DrawLine MouseX(),MouseY(), selectx,MouseY()
	DrawLine selectx,MouseY(),selectx,selecty
	
	If Not MouseDown(1)
		selectflag = 0
		Local ux:Int = leastof (MouseX(),selectx)
		Local uy:Int = leastof(MouseY(),selecty)
		Local lx:Int = greaterof(MouseX(),selectx)
		Local ly:Int = greaterof(MouseY(),selecty)
		For tempobj2 = EachIn objects
		If tempobj2.x > ux And tempobj2.y > uy And tempobj2.x<lx And tempobj2.y <ly
			tempobj2.selected = 1
		Else
			tempobj2.selected = 0
		EndIf
		Next
	EndIf
		
EndIf

Flip
Wend
'leastof and greaterof are necessary in case of a leftward or upward drag of the select box

Function leastof(n1:Int, n2:Int)  
	Local least:Int
	If n1>n2 least = n2
	If n2>n1 least = n1
	Return least
End Function

Function greaterof(n1:Int, n2:Int)
	Local greater:Int
	If n1>n2 greater= n1
	If n2>n1 greater= n2
	Return greater
End Function
