; ID: 2001
; Author: Rob Farley
; Date: 2007-04-22 05:13:42
; Title: Drag and Drop objects on a grid
; Description: A simple example of moving objects in a type around on a grid

Graphics 1024,768,32,2

Global size = GraphicsHeight() / 32

SetBuffer BackBuffer()

Type Obj
	Field X,Y,R,G,B,Held
End Type

; create random objects
For n=1 To 100
	o.obj = New obj
	o\x = Rand(0,31)
	o\y = Rand(0,31)
	o\r = Rand(1,4) * 63
	o\g = Rand(1,4) * 63
	o\b = Rand(1,4) * 63
	o\held = False
Next

Global ObjHold = False

Function DrawGrid()
	; Draw Grid
	
	Color 100,100,100

	For n=0 To 31
		Rect n*size,0,size,32*size,False
		Rect 0,n*size,32*size,size,False
	Next
	
	px=-1
	py=-1
	
	; highlight square and get mouse location on grid
	If MouseX()<32*size And MouseY()<32*size Then
		px = Floor(MouseX()/size)
		py = Floor(MouseY()/size)
		
		Color 255,255,255
		Rect px*size,py*size,size,size,False
	EndIf
	
	; cycle through objects
	For o.obj = Each obj

		; pick up an object
		If o\held = False And MouseDown(1) And ObjHold = False Then
			If o\x = px And o\y = py Then o\held = True: objHold = True
		EndIf
		
		; drop and object
		If o\held = True And MouseDown(1) = False Then
			If px >= 0 And py >= 0 Then o\x = px:o\y = py
			o\held = False
			objHold = False
		EndIf
				
		; draw the object	
		If o\held Then
			DrawObj(o,MouseX(),MouseY())
		Else
			DrawObj(o,(o\x*size) + (size/2),(o\y*size) + (size/2)) 
		EndIf
	Next
End Function

Function DrawObj (o.obj,x,y)
	Color o\r,o\g,o\b
	Oval x-(size/2)+1,y-(size/2)+1,size-2,size-2
	
	If o\held Then
		Color 255,255,255
		Oval x-(size/2),y-(size/2),size,size,False
	EndIf
End Function

; Main Loop
Repeat
	Cls
	drawgrid
	Flip
Until KeyHit(1)
