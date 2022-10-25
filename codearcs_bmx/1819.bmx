; ID: 1819
; Author: skn3[ac]
; Date: 2006-09-20 10:03:41
; Title: Calculate bounding box
; Description: Calculates the bounding box (x,y,width,height) of rect that has x,y,width,height,angle,scalex,scaley,handlex,handley

Type entity
	Field x:Float
	Field y:Float
	Field width:Float
	Field height:Float
	Field scalex:Float
	Field scaley:Float
	Field angle:Float
	Field handlex:Float
	Field handley:Float
	
	Field boundingx:Float
	Field boundingy:Float
	Field boundingwidth:Float
	Field boundingheight:Float
	
	Method UpdateBounding()	
		'calculate 4 points
		Local oldx1:Float = x
		Local oldy1:Float = y
		Local oldx2:Float = x + width * scalex
		Local oldy2:Float = y
		Local oldx3:Float = x + width * scalex
		Local oldy3:Float = y + height * scaley
		Local oldx4:Float = x
		Local oldy4:Float = y + height * scaley
		
		'calculate center point
		Local centerx:Float = x + (handlex*scalex)
		Local centery:Float = y + (handley*scaley)
		
		'calculate rotated points
		Local x1:Float = x + Cos(angle) * (oldx1 - centerx) - Sin(angle) * (oldy1 - centery)
		Local y1:Float = y + Sin(angle) * (oldx1 - centerx) + Cos(angle) * (oldy1 - centery)
		Local x2:Float = x + Cos(angle) * (oldx2 - centerx) - Sin(angle) * (oldy2 - centery)
		Local y2:Float = y + Sin(angle) * (oldx2 - centerx) + Cos(angle) * (oldy2 - centery)
		Local x3:Float = x + Cos(angle) * (oldx3 - centerx) - Sin(angle) * (oldy3 - centery)
		Local y3:Float = y + Sin(angle) * (oldx3 - centerx) + Cos(angle) * (oldy3 - centery)
		Local x4:Float = x + Cos(angle) * (oldx4 - centerx) - Sin(angle) * (oldy4 - centery)
		Local y4:Float = y + Sin(angle) * (oldx4 - centerx) + Cos(angle) * (oldy4 - centery)
		
		'find order of points
		Local minx:Float = x1
		If x2 < minx minx = x2
		If x3 < minx minx = x3
		If x4 < minx minx = x4
		
		Local miny:Float = y1
		If y2 < miny miny = y2
		If y3 < miny miny = y3
		If y4 < miny miny = y4
		
		Local maxx:Float = x1
		If x2 > maxx maxx = x2
		If x3 > maxx maxx = x3
		If x4 > maxx maxx = x4
		
		Local maxy:Float = y1
		If y2 > maxy maxy = y2
		If y3 > maxy maxy = y3
		If y4 > maxy maxy = y4
		
		'calculate bounding box
		boundingx = minx
		boundingy = miny
		boundingwidth = maxx - minx
		boundingheight = maxy - miny
	End Method
	
	Method Render()
		'draw bounding box
		SetHandle(0,0)
		SetAlpha(0.5)
		SetScale(1,1)
		SetRotation(0)
		SetColor(255,100,100)
		DrawRect(boundingx-3,boundingy-3,boundingwidth+6,boundingheight+6)
		
		'draw box
		SetAlpha(0.5)
		SetHandle(handlex,handley)
		SetScale(scalex,scaley)
		SetRotation(angle)
		SetColor(0,155,0)
		DrawRect(x,y,width,height)
		SetHandle(0,0)
	End Method
End Type

Graphics(640,480,0,0)

Local e:entity = New entity
e.x = 320
e.y = 240
e.width = 40
e.height = 60
e.scalex = 1
e.scaley = 1
e.handlex = 20
e.handley = 30
e.angle = 0

SetBlend(alphablend)
SetClsColor(255,255,255)
Repeat
	Cls
	If KeyDown(KEY_SPACE) e.angle:+0.01
	e.scaley = 1 + MouseY() / 100.0
	e.updatebounding()
	e.render()
	Flip
Until KeyDown(KEY_ESCAPE)
