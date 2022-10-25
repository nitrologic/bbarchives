; ID: 3227
; Author: Head
; Date: 2015-10-06 07:29:56
; Title: Circle Rotation Thing
; Description: Circle Art

SuperStrict

SetGraphicsDriver GLMax2DDriver()
Graphics 1024,768

Const DRAW_POINT:Int = GL_POINT
Const DRAW_LINE:Int = GL_LINE
Const DRAW_FILL:Int = GL_FILL

Global radius:Float = 150.0
Global speed:Float = 2.0
Global offset_x:Float = GraphicsWidth()/2, offset_y:Float = GraphicsHeight()/2
Global a:Float, x:Float, y:Float

Global OrbitList:TList=CreateList()
Global OvalList:TList=CreateList()

Type TOrbits
	Field radius:Float, x:Float, y:Float
	Field r:Int, g:Int, b:Int

	Function Create:TOrbits(_r:Float, _x:Float, _y:Float, _cr:Int, _cg:Int, _cb:Int)
		Local n:TOrbits = New TOrbits
		n.radius = _r
		n.x = _x
		n.y = _y
		n.r = _cr
		n.g = _cg
		n.b = _cb
		ListAddLast OrbitList, n
		Return n
	End Function
	
	Function Draw()
		SetDrawMode GL_LINE
		For Local n:TOrbits=EachIn OrbitList
			SetColor n.r,n.g,n.b
			DrawOval n.x-n.radius, n.y-n.radius,n.radius*2,n.radius*2	
		Next
		SetColor 255,255,255
		SetDrawMode GL_FILL
	End Function	
End Type
	
Type TOvals
	Field radius:Float, oradius:Float, speed:Float
	Field a:Float, x:Float, y:Float
	Field typ:Byte, orbit:TOrbits

	Function Create:TOvals(_a:Float, _r:Float, _s:Float, _t:Byte, _o:TOrbits)
		Local n:TOvals = New TOvals
		n.radius = _r
		n.speed = _s
		n.typ = _t
		n.orbit = _o
		n.oradius = _o.radius
		n.a = _a
		If n.typ = 1 Then
			n.oradius = (n.orbit.radius - n.radius) - n.radius
		End If
		ListAddLast OvalList, n
	End Function	
	
	Function Draw()
		SetDrawMode GL_LINE	
		For Local n:TOvals=EachIn OvalList
			SetColor 255,255,255
			n.a = (n.a+n.speed) Mod 360
			n.x = Cos(n.a) * n.oradius + offset_x-(n.radius/2)
			n.y = Sin(n.a) * n.oradius + offset_y-(n.radius/2)
			DrawOval n.x, n.y, n.radius, n.radius

			If n.typ=0 Then
				For Local o:TOvals=EachIn OvalList
					If o.typ = 1 Then
						Local d:Float = Sqr((n.x - o.x)^2 + (n.y - o.y)^2)
						If d < 120 Then 
							If d > 0 Then SetColor 240-d*2,240-d*2,240-d*2
							DrawLine n.x+n.radius/2, n.y+n.radius/2, o.x+o.radius/2, o.y+o.radius/2
							SetDrawMode GL_FILL	
							Local count:Float = 120/n.radius
							Local rad:Float = d / count
							DrawOval n.x+rad/2, n.y+rad/2, n.radius-rad, n.radius-rad
							SetDrawMode GL_LINE
							SetColor 255,255,255
						End If
					End If
				Next
			End If			
		Next
		
		SetColor 255,255,255
		SetDrawMode GL_FILL	
	End Function
End Type

Function SetDrawMode(mode:Int)
    glPolygonMode(GL_FRONT_AND_BACK, mode)
EndFunction

Local orbit:TOrbits = TOrbits.Create(250,offset_x,offset_y,50,50,100)
Local orbit1:TOrbits = TOrbits.Create(150,offset_x,offset_y,50,50,100)

For Local x:Int = 0 To 360 Step 18
	TOvals.Create(x,25,-.01,0,orbit)
Next

For x = 0 To 360 Step 18
	TOvals.Create(x,10,.01,0,orbit1)
Next

For x = 0 To 360 Step 72
	TOvals.Create(x,25,.5,1,orbit)
Next

For x = 0 To 360 Step 72
	TOvals.Create(x,10,-.5,1,orbit1)
Next

Repeat
	Flip
	Cls
	TOrbits.Draw()	
	TOvals.Draw()
Until KeyHit(KEY_ESCAPE)
