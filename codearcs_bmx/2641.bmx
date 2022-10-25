; ID: 2641
; Author: Taron
; Date: 2010-01-17 19:17:04
; Title: Fireworks 2010
; Description: mouseclick fireworks

SuperStrict
Framework brl.GLMax2D
'Framework brl.d3d7Max2D
Import brl.random
Import brl.eventqueue
'-------------------------------	set graphics
SetGraphicsDriver GLMax2DDriver()
'SetGraphicsDriver D3D7Max2DDriver()
Global wintitle:String = "                                                                                                             *** HAPPY 2010 ***"
AppTitle=(wintitle)

Global scrx:Int = 800'1280
Global scry:Int = 800'800
Graphics(scrx,scry,0,60)
AutoMidHandle(1)

'==============================================================================================	
'GENERATING CONTENT
'==============================================================================================	
Global background:TImage = CreateImage(scrx,scry,1,FILTEREDIMAGE|DYNAMICIMAGE|MASKEDIMAGE)
Local pixmap:TPixmap = LockImage(background)
For Local x:Float = 0 Until scrx
	For Local y:Float = 0 Until scry
		Local xx:Float = x-scrx*0.5
		Local yy:Float = y-scry*0.3
		Local dot:Float = xx*xx+yy*yy
		If dot
			dot = 1.0- Sqr(dot)/scrx
			If dot<0 Then dot= 0
			If dot>1.0 Then dot=1
			
			dot:*255
		EndIf
		Local alpha:Int = (0.2+0.8*(y/scry)^10)*255
		WritePixel(pixmap,x,y,Int(dot)|alpha Shl 24)
	Next
Next
UnlockImage(background)

Global land_height:Int = 100
Global land:TImage = CreateImage(scrx,land_height,1,FILTEREDIMAGE|DYNAMICIMAGE|MASKEDIMAGE)
pixmap = LockImage(land)
For Local x:Float = 0 Until scrx
	For Local y:Float = 0 Until land_height
		
		Local col:Float = 0
		Local y_perc:Float = y/land_height
		Local alpha:Int = 0
		If y>0
			col = 0.8+0.2*Sin(x+(land_height-y*y_perc)*Cos(x*y_perc^2))
			col:* y_perc
			col:*255
			alpha=y_perc^2*255			
		EndIf
		
		WritePixel(pixmap,x,y,Int(alpha) Shl 24|Int(col) Shl 16|Int(col) Shl 8|Int(col))
	Next
Next
UnlockImage(land)

Local glowsize:Int = 32
Local glowhalf:Int = glowsize*0.5

Global glow:TImage = CreateImage(glowsize,glowsize,1,FILTEREDIMAGE|DYNAMICIMAGE|MASKEDIMAGE)
pixmap = LockImage(glow)
For Local x:Float = 0 Until glowsize
	For Local y:Float = 0 Until glowsize
		Local xx:Float = x-glowhalf
		Local yy:Float = y-glowhalf
		Local dot:Float = xx*xx+yy*yy
		If dot
			dot = 1.0- Sqr(dot)/glowhalf
			dot = dot*0.1+(dot*1.1)^8
			If dot<0 Then dot= 0
			If dot>1.0 Then dot=1
			
			dot:*255
		Else 
			dot = 255
		EndIf
		WritePixel(pixmap,x,y,Int(dot) Shl 24|$ffffff)
	Next
Next
UnlockImage(glow)

'==============================================================================================	
'TYPES
'==============================================================================================	
'----------------------- HELPERS
Type ms
Field x:Double
Field y:Double
Field scl_x:Float 
Field scl_y:Float 
Field speed:Double
	Function Create:ms()
		Return New ms
	End Function
 	Method normalize:Double()
		speed= x*x+y*y
		If speed
			speed= Sqr(speed)
			x:/speed
			y:/speed
		EndIf
		Return speed
	EndMethod
EndType

Type color
Field r:Double
Field g:Double
Field b:Double
Field a:Double
	Method Clear()
		r = 0
		g = 0
		b = 0
		a = 0		
	EndMethod
	
 	Method normalize()
		Local t:Double
		t = r*r+g*g+b*b
		If t 
			t = 1.0/Sqr(t)
			r:*t
			g:*t
			b:*t
		EndIf
	EndMethod
	
	Method up()
		r:*255
		g:*255
		b:*255
	EndMethod
	
	Function Create:color()
		Return New color
	End Function
EndType

'------------------------ ROCKET
Global spark_col:color = color.Create()
spark_col.r = 80'170
spark_col.g = 40'90
spark_col.b = 0

Global rocketlist:TList = CreateList()
Type rocket
	Field pos:ms = ms.Create()
	Field vel:ms = ms.Create()
	Field col:color = color.Create() 'default=30,44,55
	Field age:Float
	
	Method draw()
		pos.x:+vel.x
		pos.y:+vel.y
		vel.y:+0.1
		SetAlpha 0.7
		SetColor 155,103,25
		Plot(pos.x,pos.y)
		age:-0.01		
	End Method
	
	Function update()
		For Local ro:rocket = EachIn rocketlist
			ro.draw()	
			explode.Create(ro.pos.x,ro.pos.y,-0.5+RndFloat(),-0.3*ro.vel.y,spark_col,(ro.age*0.7)^4,0)
			If ro.age <= 0 
				Local vel:ms = ms.Create()
				Local power:Float = 0.5*RndFloat()+0.05
				Local count:Int = Rand(10,600)
				Local bage:Float = 0.2+RndFloat()*0.8
				Local typ:Byte = 3.001-(RndFloat()^4)*3
				If typ < 2
					For Local i:Int = 0 Until count
						Local circ:Float = i*360*(RndFloat()*0.5+0.5)
						Local push:Float = (2+RndFloat()*6)*power
						Local velx:Float = Sin(circ)*push+ro.vel.x*0.3
						Local vely:Float = Cos(circ)*push+(ro.vel.y*0.2-0.4)
						Local age:Float = bage+RndFloat()*bage
						If age>0.9 Then typ = 1 Else typ = 0
						explode.Create(ro.pos.x,ro.pos.y,velx,vely,ro.col,age*2,typ)
					Next
				Else
					If typ = 2
						For Local i:Int = 0 Until (count*power*0.3)
							Local circ:Float = i*360*(RndFloat()*0.5+0.5)
							Local push:Float = power*(3+RndFloat())
							Local velx:Float = Sin(circ)*push+ro.vel.x*0.3
							Local vely:Float = Cos(circ)*push+(ro.vel.y*0.2-0.4)
							explode.Create(ro.pos.x,ro.pos.y,velx,vely,ro.col,bage+RndFloat()*bage,typ)
						Next
					End If
					If typ = 3
						Local offset:Int = Rand(0,120)
						For Local i:Int = 0 Until (count*power*0.3)
							Local circ:Float = i*20*(RndFloat()*0.01+0.99)+offset
							Local push:Float = power*(circ*0.002)
							Local velx:Float = Sin(circ)*push+ro.vel.x*0.3
							Local vely:Float = Cos(circ)*push+(ro.vel.y*0.2-0.4)
							explode.Create(ro.pos.x,ro.pos.y,velx,vely,ro.col,bage+RndFloat()*bage,3)
						Next
					End If
				End If
				SetAlpha 1.0
				SetColor 233,233,255
				SetScale 6,3
				DrawImage(glow,ro.pos.x,ro.pos.y)
				drawstar(ro.pos.x, ro.pos.y,2)
				rocketlist.Remove(ro)
			EndIf
		Next		
	End Function
	
	Function Create:rocket(pos:ms,vel:ms, r:Int,g:Int,b:Int, age:Float)
		Local ro:rocket = New rocket
		ro.pos = pos
		ro.vel = vel
		
		ro.age = age
		ro.col.r = r
		ro.col.g = g
		ro.col.b = b
		ListAddLast rocketlist, ro
		Return ro
	EndFunction
End Type

'------------------------ EXPLODE
Function drawstar(posx:Float, posy:Float, scale:Float)
	SetScale scale*5.5,0.1
	SetRotation 45
	DrawImage(glow,posx,posy)'Plot(posx,posy)
	SetRotation -45
	DrawImage(glow,posx,posy)'Plot(posx,posy)
	SetRotation 0
End Function

Global explodelist:TList = CreateList()

Type explode
	Field posx:Float
	Field posy:Float
	Field velx:Float
	Field vely:Float
	Field colr:Int
	Field colg:Int
	Field colb:Int
	Field age:Float
	Field dim:Float
	Field typ:Byte
	
	Method draw()
		'velx:*((age/dim)*0.25+0.8)
		'vely:*((age/dim)*0.25+0.8)
		posx:+velx
		posy:+vely
		SetColor colr,colg,colb
		Select typ
			Case 0
				vely:+0.005
			Case 1
				Local alp:Float = (age/dim)*(age Mod 0.2)/0.2
				SetAlpha alp
				velx:*(0.8+RndFloat()*0.3)
				vely:*(0.8+RndFloat()*0.3)
				If age<0.9 And alp>0.2
					alp:*2
					SetScale alp,alp
					SetAlpha 0.1+0.25*alp
					DrawImage(glow,posx,posy)'Plot(posx,posy)
					SetAlpha 0.25*alp
					drawstar(posx,posy,1.0)
					SetScale 1,1
					SetColor 255,255,255
				End If
				vely:+0.03
			Case 2
				SetAlpha 0.05
					SetScale 5.5,0.1
					drawstar(posx,posy,1.0)
				SetColor 255,255,255				
				vely:+0.002
			Case 3
				SetAlpha 0.05
				DrawImage(glow,posx,posy)
				SetScale 0.5,0.5
				If age<0.1 And age>0.04 
					SetAlpha 1.0-(age-0.04)/0.06; 
					drawstar(posx,posy,1.0)
				EndIf
				SetColor 255,255,255
				vely:+0.002
		End Select
		
		SetAlpha (age/dim)^2
		SetScale 0.2,0.2
		DrawImage(glow,posx,posy)'Plot(posx,posy)
		
		If posx<0 Or posx>scrx Or posy>scry Then explodelist.Remove(Self)
		age:-0.01	
		SetAlpha 1.0	
		SetScale 1.0,1.0
	End Method
	
	Function update()
		For Local ex:explode = EachIn explodelist
			ex.draw()	
			If ex.age <= 0 
				
				explodelist.Remove(ex)
			EndIf
		Next		
	End Function
	
	Function Create:explode(posx:Float,posy:Float,velx:Float,vely:Float, col:color, age:Float,typ:Byte)
		Local ex:explode = New explode
		ex.posx = posx
		ex.posy = posy
		ex.velx = velx
		ex.vely = vely
		ex.colr = col.r+(-0.2+0.2*RndFloat())*255
		ex.colg = col.g+(-0.2+0.2*RndFloat())*255
		ex.colb = col.b+(-0.2+0.2*RndFloat())*255
		ex.age = age
		ex.dim = age
		ex.typ = typ
		
		ListAddLast explodelist, ex
		Return ex
	End Function
End Type

'==============================================================================================	
'GLOBALS
'==============================================================================================	
	Global wait:Int = 10

	Global cpos:ms = ms.Create()
	Global cvel:ms = ms.Create()
	
	cpos.x = 400
	cpos.y = 800
	cvel.x = 0.3
	cvel.y = -10
	rocket.Create(cpos,cvel,255,200,50,1.0)
	HideMouse
	
'************************************************************************************************
' GAME 
'************************************************************************************************
'------------------------------------------------------------------------------------------------
'------------------------------------------------------------------------------------------------	
Function fireworks()
	Flip 1
	SetBlend ALPHABLEND
	SetAlpha 1.0
	SetColor 25,45,95
	SetScale 1,1
	DrawImage(background,scrx*0.5,scry*0.5)
	DrawImage(land,scrx*0.5,scry-50)
	
	SetBlend LIGHTBLEND
	SetAlpha 0.25
	If wait <-200 Then SetAlpha 0.25-(201+wait)*0.001
	SetColor 10,10,90-wait
	DrawImage(glow,MouseX(),MouseY())
	SetAlpha 1.0
	Plot(MouseX(),MouseY())
	
	If MouseDown(1) Or KeyDown(KEY_SPACE)Or KeyDown(KEY_R)Or KeyDown(KEY_G)Or KeyDown(KEY_B)Or KeyDown(KEY_Y)
		If wait < 0 
			Local age:Float = Float(scry-MouseY())/scry 
			cpos:ms = ms.Create()
			cvel:ms = ms.Create()
			cpos.x = Rand(390,410)
			cpos.y = scry-20
			cvel.x = (MouseX()-cpos.x)/150
			cvel.y = -(5+age*8)
			
			If Not KeyDown(KEY_Z) Then cvel.x:+(-0.5+RndFloat()); age:*RndFloat();age:+1.0 Else age:+ Abs(Float(MouseX()-scrx*0.5)/(scrx*0.5))
			
			Local csin:Float = 0.5+0.5*Sin(MilliSecs()*0.1)
			Local cr:Float = csin/0.5
			If cr<0 Then cr = 0
			If cr>1.0 Then cr = 1.0
			
			Local cg:Float = (csin-0.5)/0.5
			If cg<0 Then cg=0
			If cg>1.0 Then cg=1.0
			
			Local cb:Float = (1.0-csin)
			Local dot:Float = cr*cr+cg*cg+cb*cb
			If dot
				dot = 1.0/Sqr(dot)
				cr:*dot
				cg:*dot
				cb:*dot
			End If
			cr=0.5+0.5*cr
			cg=0.7+0.3*cg
			If KeyDown(KEY_R) Then cr=1;cg=0.3;cb=0.1
			If KeyDown(KEY_G) Then cr=0.1;cg=1;cb=0.3
			If KeyDown(KEY_Y) Then cr=0.9;cg=1;cb=0.1
			If KeyDown(KEY_B) Then cr=0.3;cg=0.1;cb=1
			rocket.Create(cpos,cvel,cr*255,cg*255,cb*255,age)
			SetAlpha 1.0
			SetColor 125,60,20
			SetScale 8,2
			DrawImage(glow,cpos.x,cpos.y)
			SetScale 1,0.5
			DrawImage(glow,cpos.x,cpos.y)
			wait = Rand(0,6)
		Else
			wait:-1
		End If
	End If
	rocket.update()
	explode.update()
End Function

'=============================================================================
'EVENT POLLS
'=============================================================================
Local close:Byte = 0
Repeat
	close = 0
	PollEvent()
	If EventID() = 259 Then close = 1
	fireworks()
Until KeyHit(KEY_ESCAPE) Or close
