; ID: 2323
; Author: Jesse
; Date: 2008-09-29 22:17:12
; Title: dragging and sliding
; Description: Object dragging and sliding

Include "mouse.bmx"


Type TDrag
	Field x				:Float			
	Field y				:Float
	Field oldx			:Float
	Field oldy			:Float

	Field dx			:Float
	Field dy			:Float

	Field startx		:Float
	Field starty		:Float
	
	Field minx			:Float
	Field miny			:Float
	Field maxx			:Float
	Field maxy			:Float
	Field LimitWidth	:Float
	Field LimitHeight	:Float
				
	Field Width			:Float		
	Field Height		:Float	
	Field dragging		:Int	'mouse dragging object flag
	Field MaxErrorX		:Int = 50
	Field MaxErrorY		:Int = 50
'	create a drag(square) object

	Function Create:TDrag(x:Float,y:Float,Width:Int,Height:Int) 

		Local drag:TDrag = New TDrag
		drag.x = x
		drag.y = y
		drag.Width = width
		drag.Height = Height
		drag.minx = 0
		drag.miny = 0
		drag.maxx = GraphicsWidth()-width
		drag.maxy = GraphicsHeight()-height
		drag.LimitWidth = drag.maxx
		drag.LimitHeight = drag.maxy
		Return drag

	End Function
	
	Method setObjectLimits(x1:Float,y1:Float,W:Float Var,H:Float Var)
		x:+ x1 - minx
		y:+ y1 - miny
		minx = x1
		miny = y1
		If W < width  Then W = width
		If H < Height Then H = Height
		maxx = x1+W-width
		maxy = y1+H-height
		LimitWidth = W
		LimitHeight = H
		If x < minx  Then x = minx 
		If x > maxx - width Then x = maxx
		If y < miny Then y = miny
		If y > maxy - height Then y = maxy

	End Method
	
	Method OWidth(w:Float) 
		Width = w
	End Method
	
	Method Oheight(h:Float) 
		Height = h
	End Method

'	move object to new position

	Method shift()
		shiftx()
		shifty()
	End Method

'	move to x

	Method shiftx()
	
		Self.x = mouse.x - dx
		If x<minx Then x = minx
		If x>maxx Then x = maxx 

	End Method	

'	move to y

	Method shifty()
	
		Self.y = mouse.y - dy
		If y<miny Then y = miny
		If y>maxy Then y = maxy	
	End Method		

'	relay box location.

	Method setxy(x:Float, y:Float)
		Self.x = x
		Self.y = y
		If Self.x < minx Then Self.x = minx
		If Self.x > maxx Then Self.x = maxx
		If Self.y < miny Then Self.y = miny
		If Self.y > maxy Then Self.y = maxy
	End Method

'	get x and y

	Method getxy(h:Float Var,v:Float Var)
		If Mouse.ButtonActivated() 
			dragging  =  Mouse.InArea(x, y, width, height)
			If dragging
			 
				startx = mouse.x
				starty = mouse.y
				dx = startx-x
				dy = starty-y
				startx :-dx
				starty :-dy
				
			EndIf
		ElseIf mouse.ButtonInUse() 
			If dragging
				If mouse.moved() 
					shift()
					If Not mouse.InArea(minx,miny,LimitWidth,LimitHeight) 
						If mouse.x < (minx-MaxErrorX) Or mouse.x > (minx+LimitWidth+MaxErrorX) Or ..  
						   mouse.y < (miny-MaxErrorY) Or mouse.y > (miny+LimitHeight+MaxErrorY) Then 
							x = startx
							y = starty
						EndIf 
					EndIf
				EndIf
			EndIf
		Else							'mouse button released
			dragging = False 
		End If
		h = x
		v = y
	End Method

	
End Type
Rem

Local obj:tdrag 

Graphics 800,600
Local x:Float = 100
Local y:Float = 100
Local w:Int = 100
Local h:Int = 100
Local lx:Int = 100
Local ly:Int = 100
Local lw:Float = 50
Local lh:Float = 400
obj = Tdrag.Create(x,y,w,h)
obj.SetObjectLimits(lx,ly,lw,lh)

Repeat
	Cls()
	DrawText Mouse.X+"  "+Mouse.Y,200,20
	obj.getxy(x,y)
	DrawText x+"  "+y,400,20
	DrawLine(lx   ,ly   ,lx+lw,ly   )
	DrawLine(lx   ,ly   ,lx   ,ly+lh)
	DrawLine(lx   ,ly+lh,lx+lw,ly+lh)
	DrawLine(lx+lw,ly   ,lx+lw,ly+lh)
	DrawRect(x,y,w,h)
	If KeyHit(key_space)
		lx = Rand(0,400)
		ly = Rand(0,200)
		obj.SetObjectLimits(lx,ly,lw,lh)
	EndIf
	Flip()
	
Until KeyDown(key_escape) Or AppTerminate()
EndRem
