; ID: 2003
; Author: Rob Farley
; Date: 2007-04-28 05:33:31
; Title: Drag and Drop on a Hexagon Grid
; Description: Drag and Drop tokens on a Hexagon grid

Graphics 800,600,32,2

SetBuffer BackBuffer()

; Load Media
Global counter = LoadAnimImage("counter.png",32,32,0,2)
Global piece = LoadAnimImage("pieces.png",16,16,0,4)
MaskImage piece,255,0,255

; Set up Obj Type
Type Obj
	Field X,Y,T,Held
End Type

; create random objects
For n=1 To 20
	o.obj = New obj
	o\x = Rand(0,15)
	o\y = Rand(0,31)
	o\t = Rand(0,3)

	o\held = False
Next

Global ObjHold = False


; Main Loop
Repeat
	Cls
	DrawGrid
	Flip
Until KeyHit(1)

Function DrawGrid()

	px=-1
	py=-1
	
	For x=0 To 15
	For y=0 To 31
	
		xp = x * 48
		yp = y * 16
		
		If y Mod 2 = 0 Then xp = xp + 24
		
		over = False
		
		; Middle Bit
		If RectsOverlap(xp+7,yp,16,31,MouseX(),MouseY(),1,1) Then over = True
		; Edges
		If over = False And InTriangle(MouseX(),MouseY(),xp+8,yp,xp+8,yp+31,xp,yp+15) Then over = True
		If over = False And InTriangle(MouseX(),MouseY(),xp+22,yp,xp+22,yp+31,xp+31,yp+15) Then over = True
		
		DrawImage counter,xp,yp,over
		
		If over Then px=x:py=y
	Next
	Next

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
		
		xp = o\x * 48
		yp = o\y * 16
	
		If o\y Mod 2 = 0 Then xp = xp + 24	
						
		; draw the object	
		If o\held Then
			DrawObj(o,MouseX(),MouseY())
		Else
			DrawObj(o,xp+16,yp+16) 
		EndIf
	Next
End Function

Function DrawObj (o.obj,x,y)
	DrawImage piece,x-8,y-8,o\t
End Function

Function InTriangle(x0,y0,x1,y1,x2,y2,x3,y3)
	b0# =  (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)
	b1# = ((x2 - x0) * (y3 - y0) - (x3 - x0) * (y2 - y0)) / b0 
	b2# = ((x3 - x0) * (y1 - y0) - (x1 - x0) * (y3 - y0)) / b0
	b3# = ((x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0)) / b0 
	
	If b1>0 And b2>0 And b3>0 Then Return True Else Return False
End Function
