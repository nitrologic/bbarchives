; ID: 1769
; Author: grable
; Date: 2006-07-31 07:35:54
; Title: Simple Verlet (physics)
; Description: An example of a verlet with rotation

SuperStrict


Const S_TIMESTEP:Float = 0.1


Type TSPoint
	Field x:Float,y:Float ' current position
	Field oldx:Float,oldy:Float ' old position
	Field fx:Float,fy:Float ' impulse force
	Field mass:Float
	Field active:Int
	
	Function Create:TSPoint( x:Float,y:Float, mass:Float, active:Int = True)
		Local p:TSPoint = New TSPoint
		p.x = x
		p.y = y
		p.oldx = x
		p.oldy = y
		p.mass = mass
		p.active = active
		Return p
	EndFunction
	
	Method Update()
		If Not active Then Return
		
		Local tmpx1:Float = x
		Local tmpy1:Float = y		
		Local tmpx2:Float = fx * S_TIMESTEP * S_TIMESTEP
		Local tmpy2:Float = fy * S_TIMESTEP * S_TIMESTEP
		
		oldx :+ tmpx2
		oldy :+ tmpy2
		
		x :- oldx
		y :- oldy
		
		x :+ tmpx1
		y :+ tmpy1
		
		oldx = tmpx1
		oldy = tmpy1		
		
		fx = 0
		fy = 0
	EndMethod
	
	Method Render()
		SetColor 0,0,255
		DrawOval x-2,y-2, 5,5
	EndMethod
	
	Method Translate( x:Float,y:Float, reset:Int = False)
		Self.x :+ x
		Self.y :+ y		
		' reset movement
		If reset Then
			oldx = Self.x
			oldy = Self.y		
		EndIf
	EndMethod
	
	Method Rotate( dir:Float, center:Float[], reset:Int = False)
		Local xr:Float = x - center[0]
		Local yr:Float = y - center[1]
		x = xr * Cos(dir) - yr * Sin(dir)
		y = xr * Sin(dir) + yr * Cos(dir)		
		x :+ center[0]
		y :+ center[1]
		' reset movement
		If reset Then
			oldx = x
			oldy = y
		EndIf
	EndMethod
EndType


Type TSLink
	Field p1:TSPoint
	Field p2:TSPoint	
	Field restLength:Float
	Field k:Float	
	Field stress:Float
	
	Function Create:TSLink( p1:TSPoint, p2:TSPoint, k:Float)
		Local l:TSLink = New TSLink
		l.p1 = p1
		l.p2 = p2		
		l.k = k
		l.CalcRestLength()		
		Return l
	EndFunction
	
	Method Update()
		Local dx:Float = p1.x - p2.x
		Local dy:Float = p1.y - p2.y
		Local dist:Float = Sqr( dx*dx + dy*dy)
		Local w:Float = p1.mass + p2.mass
		
		If p1.active Then
			p1.x :- ((dx / dist) * ((dist - restLength) * k)) * (p1.mass / w)
			p1.y :- ((dy / dist) * ((dist - restLength) * k)) * (p1.mass / w)
		EndIf
		
		If p2.active Then
			p2.x :+ ((dx / dist) * ((dist - restLength) * k)) * (p2.mass / w)
			p2.y :+ ((dy / dist) * ((dist - restLength) * k)) * (p2.mass / w)
		EndIf
		
		stress = (dist - restLength) / restLength
	EndMethod	
	
	Method Render()
		SetColor 255,255,255
		DrawLine p1.x,p1.y, p2.x,p2.y
	EndMethod
	
	Method CalcRestLength()
		restLength = Sqr((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y))
	EndMethod	
EndType


Type TSGroup
	Field points:TList = New TList
	Field links:TList = New TLIst	
	Field gravity:Float
	Field active:Int
	Field bbox:Float[4]
	Field center:Float[2]
	
	Function Create:TSGroup( gravity:Float = 0.0, active:Int = True)
		Local g:TSGroup = New TSGroup
		g.gravity = gravity
		Return g
	EndFunction
	
	Method AddPoint( p:TSPoint)
		If p Then points.AddLast( p)
	EndMethod
	
	Method AddLink( l:TSLink)
		If l Then links.AddLast( l)
	EndMethod	
	
	Method Update()
		If Not active Then Return
		
		For Local p:TSPoint = EachIn points
			p.fy = gravity
			p.Update()
		Next
		
		For Local l:TSLink = EachIn links
			l.Update()
		Next
		
		CalcBoundingBox()
		CalcCenterPoint()		
	EndMethod
	
	Method Render()
		For Local l:TSLink = EachIn links
			l.Render()
		Next
		
		For Local p:TSPoint = EachIn points
			p.Render()
		Next
		
		SetColor 0,192,0
		DrawFrame( bbox[0], bbox[1], bbox[2], bbox[3])
		
		SetColor 255,0,0
		DrawOval center[0]-2,center[1]-2,4,4
	EndMethod	
	
	Method Translate( x:Float,y:Float, reset:Int = False)
		For Local p:TSPoint = EachIn points
			p.Translate( x,y, reset)		
		Next
		CalcBoundingBox()
		CalcCenterPoint()
	EndMethod	
	
	Method Rotate( dir:Float, reset:Int = False)
		For Local p:TSPoint = EachIn points
			p.Rotate( dir, center, reset)
		Next
		CalcBoundingBox()
		CalcCenterPoint()
	EndMethod	
	
	Method CalcBoundingBox()
		bbox[0] = $FFFFFFF
		bbox[1] = $FFFFFFF
		bbox[2] = 0
		bbox[3] = 0
		For Local p:TSPoint = EachIn points
			bbox[0] = Min( bbox[0], p.x)
			bbox[1] = Min( bbox[1], p.y)
			bbox[2] = Max( bbox[2], p.x)
			bbox[3] = Max( bbox[3], p.y)
		Next
		bbox[2] :- bbox[0]
		bbox[3] :- bbox[1]
	EndMethod
	
	Method CalcCenterPoint()
		Local xtmp:Float,ytmp:Float, sz:Int = points.Count()
		For Local p:TSPoint = EachIn points
			xtmp :+ p.x
			ytmp :+ p.y
		Next
		center[0] = xtmp / sz
		center[1] = ytmp / sz		
	EndMethod
EndType



Function DrawFrame( x:Float,y:Float, w:Float,h:Float)	
	DrawLine x,y, x+w,y		' top
	DrawLine x,y+h, x+w,y+h	' bottom
	DrawLine x,y, x,y+h		' left
	DrawLine x+w,y, x+w,y+h	' right	
EndFunction

Function PointInRect:Int( px:Int,py:Int, rect:Int[])
	Return (px >= rect[0]) And (py >= rect[1]) And (px < rect[0] + rect[2]) And (py < rect[1] + rect[3])
EndFunction



'
' TEST
'
Graphics 640,480, 0

Const BOX_COEF:Float = 0.4
Const BOX_MASS:Float = 30

Local obj:TSGroup = TSGroup.Create( -5, False)

Local p1:TSPoint = TSPoint.Create( 0,0,	 BOX_MASS, True)
Local p2:TSPoint = TSPoint.Create( 64,0,  BOX_MASS, True)
Local p3:TSPoint = TSPoint.Create( 0,64,  BOX_MASS, True)
Local p4:TSPoint = TSPoint.Create( 64,64, BOX_MASS, True)

obj.AddPoint( p1)
obj.AddPoint( p2)
obj.AddPoint( p3)
obj.AddPoint( p4)

obj.AddLink( TSLink.Create( p1, p2, BOX_COEF)) ' top
obj.AddLink( TSLink.Create( p2, p4, BOX_COEF)) ' right
obj.AddLink( TSLink.Create( p4, p3, BOX_COEF)) ' bottom
obj.AddLink( TSLink.Create( p3, p1, BOX_COEF)) ' left
obj.AddLink( TSLink.Create( p3, p2, BOX_COEF)) ' cross 1
obj.AddLink( TSLink.Create( p1, p4, BOX_COEF)) ' cross 2

' move it some to the right
obj.Translate( 405,32, True) 
' rotate it and give it some speed
obj.Rotate( 10)
obj.Translate( 4,0)

' globals
Global mb1:Int,mb2:Int
Global mx:Int,my:Int
Global mpoint:TSPoint


Repeat

	If KeyHit( KEY_SPACE) Then obj.active = Not obj.active

' create points / links
	If KeyDown( KEY_LCONTROL) And (Not obj.active) Then
		mx = MouseX()
		my = MouseY()	
		If MouseHit(1) Then
			' create point
			obj.AddPoint( TSPoint.Create( mx,my, BOX_MASS, True))
		ElseIf MouseHit(2)
			' create link
			Local rect:Int[4]
			If mpoint = Null Then
				' select first point
				For Local p:TSPoint = EachIn obj.points
					rect[0] = p.x - 4
					rect[1] = p.y - 4
					rect[2] = 8
					rect[3] = 8
					If PointInRect( mx,my, rect) Then
						mpoint = p
						Exit
					EndIf
				Next
			Else
				' select second point 
				For Local p:TSPoint = EachIn obj.points
					rect[0] = p.x - 4
					rect[1] = p.y - 4
					rect[2] = 8
					rect[3] = 8
					If PointInRect( mx,my, rect) Then
						obj.AddLink( TSLink.Create( mpoint, p, BOX_COEF))
						Exit
					EndIf
				Next
				mpoint = Null
			EndIf
		EndIf
		FlushMouse()		
	Else
' move single point / modify link
		If MouseDown(1) Then
			mx = MouseX()
			my = MouseY()
			If Not mb1 Then
				' select point
				Local rect:Int[4]
				For Local p:TSPoint = EachIn obj.points
					rect[0] = p.x - 4
					rect[1] = p.y - 4
					rect[2] = 8
					rect[3] = 8
					If PointInRect( mx,my, rect) Then
						mpoint = p					
						Exit
					EndIf
				Next
				mb1 = True
			EndIf
			' modify point
			If mpoint Then
				mpoint.x = mx
				mpoint.y = my
				' modify connected links
				If Not obj.active Then
					' search for links with this point
					For Local l:TSLink = EachIn obj.links
						If (l.p1 = mpoint) Or (l.p2 = mpoint) Then
							l.CalcRestLength()
						EndIf
					Next
					' cancel allow movement
					mpoint.oldx = mpoint.x
					mpoint.oldy = mpoint.y
				EndIf
			EndIf
		Else
			If mb1 Then
				' reset
				mb1 = False
				mpoint = Null
				FlushMouse()
			EndIf
		EndIf
	EndIf

' turn point on/off	
	If KeyHit( KEY_A) Then
		If mpoint <> Null Then
			mpoint.active = Not mpoint.active
		EndIf
	EndIf
	
' rotate box
	If KeyDown( KEY_1) Then
		obj.Rotate( -0.5, False)
	ElseIf KeyDown( KEY_2) Then
		obj.Rotate( 0.5, False)
	EndIf

	obj.Update()
	
' constrain all points to screen edges
	For Local p:TSPoint = EachIn obj.points
		' bottom
		If p.y > GraphicsHeight() Then 
			p.y = GraphicsHeight()
			' full friction
			p.oldx = p.x
		EndIf
		' left, right
		If p.x < 0 Then
			p.x = 0
		ElseIf p.x > GraphicsWidth() Then
			p.x = GraphicsWidth()
		EndIf
	Next

	obj.Render()

' some help
	SetColor 255,255,255
	DrawText "HELP:", 0,0
	DrawText "  Pause Simulation/Edit mode: SPACE", 0,15	
	DrawText "  Rotate Left/Right:  1 / 2", 0,30
	DrawText "  Modify Point: MB1 + DRAG", 0,45
	DrawText "  Create Point: CTRL + MB1", 0,60
	DrawText "  Create Link: CTRL + MB2 (select 2 points)", 0,75
	DrawText "  Turn point On/Off: A (on selected point)", 0,90

	Flip
	Cls

Until KeyHit( KEY_ESCAPE)
End
