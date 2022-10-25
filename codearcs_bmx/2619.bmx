; ID: 2619
; Author: Ked
; Date: 2009-11-30 18:06:30
; Title: TSelectionBox thing
; Description: Made this and decided to add it here so if I need it again, I'd know where it was!

SuperStrict

Framework BRL.GLMax2D
Import BRL.Retro

Graphics 800,600

Global selectionbox:TSelectionBox

Repeat
	If KeyHit(KEY_ESCAPE) End
	If AppTerminate() End
	
	Cls
	
	SetBlend ALPHABLEND
	SetScale 1,1
	SetAlpha 1.0
	SetRotation 0
	SetColor 255,255,255
	
	If selectionbox
		If MouseDown(MOUSE_LEFT)=True
			selectionbox.Draw(MouseX(),MouseY())
		Else
			Local rect:Int[]=selectionbox.GetRect()
			
			Print "X: "+rect[0]
			Print "Y: "+rect[1]
			Print "W: "+rect[2]
			Print "H: "+rect[3]
			
			selectionbox=Null
		EndIf
	Else
		If MouseDown(MOUSE_LEFT)=True
			selectionbox=New TSelectionBox.Create(MouseX(),MouseY())
		EndIf
	EndIf			
	
	Flip()
Forever

Type TSelectionBox
	Field startx:Int,starty:Int
	Field width:Int,height:Int
	Field finalx:Int,finaly:Int
	
	Method Create:TSelectionBox(x:Int,y:Int)
		startx=x
		starty=y
		
		Return Self
	EndMethod
	
	Method Draw(x:Int,y:Int)
		SetColor 255,255,255
		SetLineWidth(1)
		
		DrawLine(startx,starty,x,starty)
		DrawLine(startx,starty,startx,y)
		DrawLine(x,starty,x,y)
		DrawLine(startx,y,x,y)
		
		finalx=x
		finaly=y
		
		width=Abs(startx-x)
		height=Abs(starty-y)
	EndMethod
	
	Method GetRect:Int[]()
		Local retval:Int[4]
		If finalx<startx startx=finalx
		If finaly<starty starty=finaly
		retval=[startx,starty,width,height]
		
		Return retval
	EndMethod
EndType
