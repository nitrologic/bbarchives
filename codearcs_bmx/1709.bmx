; ID: 1709
; Author: IPete2
; Date: 2006-05-12 12:29:52
; Title: Very Simple Gravity
; Description: Implement 2d gravity in a very simple manner

Global  ymov = 0
Global  grav = 2
Global    y =10

Graphics 800,600,2

While Not KeyDown(key_ESCAPE)
 
 	ymov = ymov+ grav
	y=y+ymov

	If y>=355 Then 
	y = 355
	ymov=ymov*-1
	EndIf 
	
	
DrawOval 320,y,20,20

Flip 1
Cls
 
Wend
End
