; ID: 393
; Author: Filax
; Date: 2002-08-12 07:30:45
; Title: Types for newbies
; Description: How make simple windows using types

Graphics3D 640,480,32,2
SetBuffer BackBuffer()

Global Name$

Type OpenWindow
	Field Px
	Field Py
	Field Tx
	Field Ty
End Type

Procedure_OpenWindow(50,50,300,200)
Procedure_OpenWindow(150,300,200,150)
Procedure_OpenWindow(250,200,200,50)

While KeyDown(1)<>1
	Procedure_RefreshWindow()
	Flip
Wend


Function Procedure_OpenWindow(Px,Py,Tx,Ty)
	Gui.OpenWindow=New OpenWindow
	Gui\Px=Px
	Gui\Py=Py
	Gui\Tx=Tx
	Gui\Ty=Ty

	Procedure_DrawWindow(Gui\Px,Gui\Py,Gui\Tx,Gui\Ty)
End Function

Function Procedure_DrawWindow(Px,Py,Tx,Ty)
	Color 100,100,100
	Rect Px,Py,Tx,Ty,1
	
	Color 19,66,117
	Rect Px,Py,Tx,20,1
	
	Procedure_DrawCloseGadget(Px+Tx-17,Py+3,0)
	
	Color 200,200,200
	Line Px,Py,Px+Tx,Py
	Line Px,Py,Px,Py+Ty
	
	Color 50,50,50
	Line Px+Tx,Py,Px+Tx,Py+Ty
	Line Px,Py+Ty,Px+Tx,Py+Ty
End Function

Function Procedure_DrawCloseGadget(Px,Py,Flag)
	If Flag=0 Then
		Color 100,10,10
		Rect Px,Py,15,15,1
	EndIf
	
	If Flag=1 Then
		Color 10,10,100
		Rect Px,Py,15,15,1
	EndIf
End Function

Function Procedure_RefreshWindow()
	For Gui.OpenWindow = Each OpenWindow
		Procedure_DrawWindow(Gui\Px,Gui\Py,Gui\Tx,Gui\Ty)
		
		If MouseX()>Gui\Px+Gui\Tx-17 And MouseX()<Gui\Px +Gui\Tx-2 And MouseY()>Gui\Py+3 And MouseY()<Gui\Py+18 And MouseDown(1)=1		
			Procedure_DrawCloseGadget(Gui\Px+Gui\Tx-17,Gui\Py+3,1):Flip :Cls
			
			While MouseDown(1)=1
			Wend
			
			 Delete Gui 
		EndIf
	Next
End Function
