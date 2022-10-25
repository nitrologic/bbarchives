; ID: 2578
; Author: Jesse
; Date: 2009-09-02 18:13:30
; Title: Isometric
; Description: Basic Isometric tile engine.

Framework BRL.GLMax2D
Import BRL.Math
Import BRL.Random
SuperStrict

Type Tcolor
	Field red		:Int
	Field green	:Int
	Field blue	:Int
End Type

Type Tiso
	Field _x			:Int
	Field _y			:Int
	
	Field xOrigin		:Int
	Field yOrigin		:Int

	Field posx		:Float
	Field posy		:Float
	
	Field Lcolor		:Tcolor
	Field Lwidth		:Int
	Field Lalpha		:Float
	
	Field xs2657		:Float
	Field ys2657		:Float
	
	Field xScreenSize 	:Int 
	Field yScreenSize 	:Int
	Field poly4		:Float[8]
	
	Method New()
	
		'2:1 ratio width to height
		xs2657 = Cos(26.57)
		ys2657 = Sin(26.57)
		Lcolor = New Tcolor
	End Method
	
	Method init()		 
		
		' --- initialization -------------------------------- 
		xScreenSize = GraphicsWidth() 
		yScreenSize = GraphicsHeight() 
		xOrigin = xScreenSize / 2 
		yOrigin = yScreenSize - 30
	
	End Method
	
	' transforms x,y,z coordinates into 2d x coordinate 
	Method xFla:Float (x:Float, y:Float, z:Float) 

		' cartesian coordinates 
		Local xCart:Float = (x - z) * xs2657 
		Return xCart+xOrigin 
		
	End Method 
  
	' transforms x,y,z coordinates into 2d y coordinate 
	Method yFla:Float (x:Float, y:Float, z:Float)
		
		' cartesian coordinates 
		Local yCart:Float = y + (x + z) * ys2657 
		Return -yCart + yOrigin 
				
	End Method

	Method lineStyle(w:Int,c:Int,A:Float)
		
		Lwidth = w
		Lalpha = a
		Lcolor.red = (c Shr 16) & $FF
		Lcolor.green = (c Shr 8) & $FF
		Lcolor.blue = c & $FF
		SetLineWidth Lwidth
		SetAlpha Lalpha
		SetColor Lcolor.red,Lcolor.green,Lcolor.blue
			
	End Method
	
	' --- lineToing methods -------------------------------- 

	Method style(w:Int, c:Int, a:Float)
		' w: line width 
		' c: line color 
		' a: line alpha 
		lineStyle(w, c, a)
		
	End Method
	
	Method moveTo(x:Float,y:Float)
	
		posx = x
		posy = y
		Plot(posx,posy)
	
	End Method
	
	Method Plott(x:Float, y:Float, z:Float)
	
		moveTo(xFla(x, y, z), yFla(x, y, z))
	
	End Method  
	
	Method lineTo(x:Float,y:Float,z:Float)
		Local x2:Float = xFla(x,y,z)
		Local y2:Float = yFla(x,y,z)
		DrawLine posx, posy, x2, y2
		posx = x2
		posy = y2
	End Method
	
	Method LineSegment(x:Float,y:Float,z:Float,x1:Float,y1:Float,z1:Float)
		
		plott(x,y,z)
		lineTo(x1,y1,z1)
		
	End Method
	
	Method rectangle(x:Float, y:Float, z:Float, a:Float, b:Float, c:Float, color:Int)
		If c = 0
			style(1, color, 1)
			poly4[0] = xFla(x, y, z)
			poly4[1] = yFla(x, y, z)
			poly4[2] = xFla(x+a, y, z)
			poly4[3] = yFla(x+a, y, z)
			poly4[4] = xFla(x+a, y+b, z)
			poly4[5] = yFla(x+a, y+b, z)
			poly4[6] = xFla(x, y+b, z)
			poly4[7] = yFla(x, y+b, z)
			DrawPoly poly4
		ElseIf b = 0
			style(1, color, 1)
			poly4[0] = xFla(x, y+b, z)
			poly4[1] = yFla(x, y+b, z)
			poly4[2] = xFla(x+a, y+b, z)
			poly4[3] = yFla(x+a, y+b, z)
			poly4[4] = xFla(x+a, y+b, z+c)
			poly4[5] = yFla(x+a, y+b, z+c)
			poly4[6] = xFla(x, y+b, z+c)
			poly4[7] = yFla(x, y+b, z+c)
			DrawPoly poly4
		Else
			style(1, color, 1)
			poly4[0] = xFla(x, y, z)
			poly4[1] = yFla(x, y, z)
			poly4[2] = xFla(x, y+b, z)
			poly4[3] = yFla(x, y+b, z)
			poly4[4] = xFla(x, y+b, z+c)
			poly4[5] = yFla(x, y+b, z+c)
			poly4[6] = xFla(x, y, z+c)
			poly4[7] = yFla(x, y, z+c)
			DrawPoly poly4
		EndIf			
	End Method 	
	
	Method box (x:Float, y:Float, z:Float, a:Float, b:Float, c:Float, color:Int)  
		
		style(1, color, 1) 
		
		Plott(x, y, z) 
		lineTo(x+a, y, z) 
		lineTo(x+a, y+b, z) 
		lineTo(x, y+b, z)
		lineTo(x, y, z) 
		
		Plott(x, y+b, z) 
		lineTo(x+a, y+b, z) 
		lineTo(x+a, y+b, z+c) 
		lineTo(x, y+b, z+c) 
		lineTo(x, y+b, z) 
		
		Plott(x, y, z) 
		lineTo(x, y+b, z)
		lineTo(x, y+b, z+c) 
		lineTo(x, y, z+c) 
		lineTo(x, y, z)
		
	End Method
	
	'draws a box 
	
	Method boxFilled(x:Float, y:Float, z:Float, a:Float, b:Float, c:Float, color:Int) 
		style(1, color, 1)
		poly4[0] = xFla(x, y, z)
		poly4[1] = yFla(x, y, z)
		poly4[2] = xFla(x+a, y, z)
		poly4[3] = yFla(x+a, y, z)
		poly4[4] = xFla(x+a, y+b, z)
		poly4[5] = yFla(x+a, y+b, z)
		poly4[6] = xFla(x, y+b, z)
		poly4[7] = yFla(x, y+b, z)
		DrawPoly poly4 'facing right
		poly4[0] = xFla(x, y+b, z)
		poly4[1] = yFla(x, y+b, z)
		poly4[2] = xFla(x+a, y+b, z)
		poly4[3] = yFla(x+a, y+b, z)
		poly4[4] = xFla(x+a, y+b, z+c)
		poly4[5] = yFla(x+a, y+b, z+c)
		poly4[6] = xFla(x, y+b, z+c)
		poly4[7] = yFla(x, y+b, z+c)
		DrawPoly poly4 'facing up
		poly4[0] = xFla(x, y, z)
		poly4[1] = yFla(x, y, z)
		poly4[2] = xFla(x, y+b, z)
		poly4[3] = yFla(x, y+b, z)
		poly4[4] = xFla(x, y+b, z+c)
		poly4[5] = yFla(x, y+b, z+c)
		poly4[6] = xFla(x, y, z+c)
		poly4[7] = yFla(x, y, z+c)
		DrawPoly poly4 ' facing left
		box(x,y,z,a,b,c,$ff000000)'draws the box outline
	End Method	
	' --- main ------------------------------------------ 


End Type


Type tbox
	Field x		:Int
	Field y		:Int
	Field z		:Int
	Field dirx	:Int
	Field diry	:Int
	Field dirz	:Int
	Field color	:Int

	Field stp	:Int
	Field box	:Tiso

	Global boxs:Tbox[]	

	Method New()	
		box = New Tiso
		box.init()
		stp = 2
		boxs = boxs[..boxs.length+1]
		boxs[boxs.length-1] = Self
		color = $FFFFFFFF 
	End Method
	
	Method initcolor(c:Int)
		color = c
	End Method

	Method getAxis()
		dirx = 0
		diry = 0
		dirz = 0
		If KeyDown(KEY_LEFT)
			dirx = -1 * stp
			
		ElseIf KeyDown(KEY_RIGHT)
			dirx = 1 * stp
		EndIf
		If KeyDown(KEY_UP)
			dirz = 1 * stp
		ElseIf KeyDown(KEY_DOWN)
			dirz = -1 * stp
		EndIf
		If KeyDown(KEY_D)
			diry = -1 * stp
		ElseIf KeyDown(KEY_U)
			diry = 1 * stp
		EndIf
		Local lt:Int = x+dirx
		Local rt:Int = x+dirx+32
		Local tp:Int = z+dirz
		Local bt:Int = z+dirz+32
		Local collided:Int = False
		If y < 32
			For Local i:Int = 0 Until boxs.length
				If boxs[i] = Self Continue
				Local nlt:Int = boxs[i].x
				Local nrt:Int = boxs[i].x+32
				Local ntp:Int = boxs[i].z
				Local nbt:Int = boxs[i].z+32
				If lt >= nlt And lt =< nrt 
					If tp >= ntp And tp =< nbt collided = True
					If bt >= ntp And bt =< nbt collided = True
				ElseIf rt >= nlt And rt =< nrt 
					If tp >= ntp And tp =< nbt collided = True
					If bt >= ntp And tp =< nbt collided = True
				EndIf 
			Next
		End If
		If collided  dirx = 0; dirz = 0
		x:+ dirx 
		y:+ diry 
		z:+ dirz 
		
		If (x<0) x = 0
		If (y<0) y = 0
		If (z<0) z = 0
		
		If x > 400-32 x = 400-32
		If y > 400-32 y = 400-32
		If z > 400-32 z = 400-32
		
	End Method
	
	Method sort()
		Local temp:TBox
		If boxs.length < 2 Then Return
		For Local i:Int = 0 Until boxs.length-1
			If boxs[i].y >= 32 Then 
				temp = boxs[i]
				boxs[i] = boxs[i+1]
				boxs[i+1] = temp
			EndIf
			If boxs[i].x < boxs[i+1].x-32 Then
				temp = boxs[i]
				boxs[i] = boxs[i+1]
				boxs[i+1] = temp
			EndIf
			If boxs[i].z < boxs[i+1].z-32 Then
				temp = boxs[i]
				boxs[i] = boxs[i+1]
				boxs[i+1] = temp
			EndIf
			 
		Next
						 
	End Method
	
	Method DrawBox()
		
		box.style(1, $FF0000, 1) 
		box.Plott(400, 0, 0) 
		box.lineTo(0, 0, 0) 
		box.lineTo(0, 0, 400)
		box.lineTo(400,0,400)
		box.lineto(400,0,0)
		For Local i:Int = 0 Until boxs.length
			boxs[i].box.boxfilled(boxs[i].x,boxs[i].y,boxs[i].z,32,32,32,boxs[i].Color)
		Next
		' red line
		SetColor 255,255,255 
		DrawText "use arrows, U and D",20,10
		DrawText "ESC to exit",20,25
	End Method
End Type 

Graphics 800,600

Local iso:Tiso = New Tiso

iso.init()
Cls
' red line 
iso.style(1, $FF0000, 1) 'line width, color, alpha
iso.Plott(0, 0, 0)  ' position start of line
iso.lineTo(200, 0, 0) 'continue line to 
' green line 
iso.style(1, $00FF00, 1) 
iso.Plott(0, 0, 0) 
iso.lineTo(0, 200, 0) 
' blue line 
iso.style(1, $0000FF, 1) 
iso.Plott(0, 0, 0) 
iso.lineTo(0, 0, 200)
Flip()
WaitKey()
' --- main ------------------------------------------
Cls 
iso.box(0, 0, 100, 100, 50, 100, $00FF00); 
iso.boxFilled(100, 0, 0, 100, 50, 100, $FF0000);
Flip()
WaitKey()
Cls

' --- House ------------------------------------------ 
' Left wall 
iso.boxFilled(0, 0, 200, 200, 80, 0, $EE0000) 
' Right wall 
iso.boxFilled(200, 0, 0, 0, 80, 200, $EE0000) 
' Floor 
iso.boxFilled(0, 0, 0, 200, 0, 200, $00BB00) 
' Left door 
iso.boxFilled(80, 0, 200, 40, 60, 0, $CCCCCC) 
' Right door 
iso.boxFilled(200, 0, 80, 0, 60, 40, $CCCCCC) 
' blue box 
iso.boxFilled(100, 0, 130, 30, 60, 30,$0000FF) 
' grey box 
iso.boxFilled(80, 0, 80, 30, 30, 30, $AAAAAA) 
' yellow box 
iso.boxFilled(60, 0, 70, 20, 20, 20, $FFFF00) 
' purple box 
iso.boxFilled(60, 0, 20, 30, 20, 40, $FF00FF)

Flip()
WaitKey()

Cls
Local random_colors:Int[] = [$FF0000, $00FF00, $0000FF, $FFFF00, $00FFFF, $FF00FF, $FFFFFF] 
For Local j:Int = 6 To 0 Step -1 
	Local random_color:Int = random_colors[j] 
	For Local i:Int = 6 To 0 Step -1 
		iso.boxFilled(j*32, 0, i*32, 32, Rand(10)*10+10, 32, random_color); 
	Next
Next

Flip()
WaitKey()

Local box:tbox = New Tbox
box.x = 100
box.z = 100
box.initcolor($FFFF0000)
box:tbox = New Tbox
box.x = 200
box.z = 150
box.initcolor($FF00FF00)

box = New Tbox
box.initcolor($FF0000FF)

Repeat
	Cls()
	box.GetAxis()
	box.sort()
	box.DrawBox()
	
Flip()

Until KeyDown(key_escape)
