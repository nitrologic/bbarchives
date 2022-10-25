; ID: 1632
; Author: Diablo
; Date: 2006-03-04 07:12:21
; Title: Simple Rect
; Description: A VERY simple rect struct

Type TRect

	Rem
		The rect
	EndRem
	Field x%, y%, W%, h%
	
	Rem
		Gets the resulting rect if you try and fit _other into it.
		Basicaly resizes _other to fit into this rect
	EndRem
	Method InterRect:TRect(_other:TRect)
	
		Local rect:TRect = New TRect
		
		rect.x = _other.x
		rect.y = _other.y
		rect.w = _other.w
		rect.h = _other.h
		
		If rect.x < x Then 
			rect.x = x
			rect.w:- (x - _other.x)
		EndIf
		If rect.y < y Then 
			rect.y = y
			rect.h:- (y - _other.y)
		EndIf
		
		If rect.x > x + w Then rect.x = x + w
		If rect.y > y + h Then rect.h = y + h
		
		If rect.x + rect.w > x + w Then rect.w:- (rect.x + rect.w) - (x + w)
		If rect.y + rect.h > y + h Then rect.h:- (rect.y + rect.h) - (y + h)
		
		If rect.x + rect.w < x Then rect.w = 0
		If rect.y + rect.h < y Then rect.h = 0
		
		Return rect
		
	End Method
	
	Rem
		Simple method to set the values
	EndRem
	Method Set(_x%, _y%, _w%, _h%)
	
		x = _x
		y = _y
		w = _w
		h = _h
		
	End Method
	
	Rem
		Check if _x AND _y is in the rect
	EndRem
	Method IsIn@(_x%, _y%)
	
		If _x >= x And _y >= y And _x <= x + w And _y <= y + h Then Return True
		
	End Method
	
	Rem 
		Simple draw method
	EndRem
	Method Draw()
	
		DrawRect x, y, w, h
		
	End Method
	
End Type

Rem 
	TEST CODE
EndRem
Graphics 800, 600

SetBlend ALPHABLEND

Global someRects:TRect[3]

someRects[0] = New TRect
someRects[0].x = 100
someRects[0].y = 100
someRects[0].w = 600
someRects[0].h = 400

someRects[1] = New TRect
someRects[1].x = 0
someRects[1].y = 0
someRects[1].w = 600
someRects[1].h = 400

While Not KeyHit(KEY_ESCAPE)

	Cls
	
	SetColor 255, 0, 0
	If someRects[0].IsIn(MouseX(), MouseY()) Then SetColor 255, 255, 0
	someRects[0].draw()
	
	SetAlpha .6
	SetColor 0, 255, 0
	someRects[1].x = MouseX() - 300
	someRects[1].y = MouseY() - 200
	someRects[1].draw()
	
	SetColor 0, 0, 255
	someRects[2] = someRects[0].InterRect(someRects[1])
	someRects[2].draw()
	
	SetColor 255, 255, 255
	SetAlpha 1
	
	Flip

Wend
