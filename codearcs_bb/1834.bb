; ID: 1834
; Author: Jeppe Nielsen
; Date: 2006-10-03 21:01:41
; Title: 2D Particle &amp; Constraints physics with mouse buildmode/interactmode
; Description: a 2D physics simulation with an interactive demonstration

;2D Physics by Jeppe Nielsen 2006

Graphics3D 800,600,32,2

font1=LoadFont("Arial",11)
font2=LoadFont("Courier",10)

CreateScene()

iterations=10

showtext=True

Repeat
Cls


If KeyHit(57)

	If mode=0
		
		PointRemember()
		
	Else
	
		PointRestore()
		
		For c.Constraint=Each Constraint
			ConstraintDisable c,False
		Next
				
	EndIf
	
	state=0

	hit.Point=Null
	grab.Point=Null
	FlushMouse 
	FlushKeys
	
	mode=1-mode

EndIf


mx=MouseX()
my=MouseY()


Select mode

Case 0

	Select state
		
		Case 0
			
			If MouseHit(1)
				
				hit.Point=PointHit.Point(mx,my)
				
				grab.Point=hit
				
				remx=mx
				remy=my
				
				If grab=Null
				
					point1.Point=PointNew(mx,my)
					
					grab=point1
					
					state=1
											
				Else
				
					state=1
								
				EndIf
									
			EndIf
			
			If MouseHit(2)
			
				hit.Point=PointHit.Point(mx,my)
			
				If hit<>Null
				
					PointLock hit,1-hit\lock
				
				Else
				
					grab=Null
					
					hit=Null
					
					state=0
				
				
				EndIf
			
			
			EndIf
			
						
		Case 1
		
			
		
		
			If MouseHit(1)
			
				hit.Point=PointHit.Point(mx,my)
			
				If grab<>Null And hit<>Null And (hit<>grab)
								
					constraint.Constraint=ConstraintNew(grab,hit)
					
				Else
				
					If hit=Null
					
						hit.Point=PointNew(mx,my)
						
						constraint.Constraint=ConstraintNew(grab,hit)
					
					EndIf
					
						
				EndIf
				
				grab=Null
				
				state=0
				
				If hit<>Null And (hit<>grab)
				
					grab=hit
					
					state=1
				
				EndIf
					
				
			EndIf
			
			If MouseHit(2)
			
			
				grab=Null
				
				hit=Null
				
				state=0
			
			
			
			EndIf
			
			If KeyHit(14) Or KeyHit(211)
			
				If grab<>Null
				
					PointDelete grab
					
				EndIf
			
				state=0
			
			EndIf

			
	
	End Select

Case 1
	
	Select state
		
		Case 0
			
			hit.Point=PointHit.Point(mx,my)
			
			If hit<>Null 
			
				If MouseDown(1)
				
					grab.Point=hit
					
					MoveMouse grab\x,grab\y
					
					state=1
				
				EndIf
			
			EndIf
			
		Case 1
		
			If MouseDown(1)
			
				If grab<>Null
			
		
					ix#=Float(MouseXSpeed())*0.2
					iy#=Float(MouseYSpeed())*0.2
				
					PointImpulse grab,ix,iy
					
					MoveMouse grab\x,grab\y
			
				EndIf
				
			Else
			
				grab=Null
				
				state=0
				
			EndIf
	
	End Select
	
	
	time=MilliSecs()
	PointUpdate
	ConstraintUpdate iterations
	physicstime=MilliSecs()-time
	
End Select



Select drawmode

	Case 0

		SetFont font1

		ConstraintDraw
		ConstraintTensionDraw
		PointDraw
		
	Case 1
	
		ConstraintDraw
		PointDraw

	Case 2
	
		SetFont font1
		ConstraintDraw
		ConstraintTensionDraw
		
	Case 3
			
		ConstraintDraw	
				
	Case 4
	
		PointDraw

End Select	

Color 255,255,255

angle#=Float(MilliSecs())/1.0

size#=4+Sin(angle)*3

Select mode

	Case 0
	
		If grab<>Null 
		
			Oval grab\x-size,grab\y-size,size*2+1,size*2+1,0
		
		EndIf
	
	Case 1

		If hit<>Null 
		
			Oval hit\x-size,hit\y-size,size*2+1,size*2+1,0
		
		EndIf
		
End Select

SetFont font2

wh=GraphicsWidth()/2

If showtext=True

Text wh,0,"Blitz 2D Physics simulation",1
Text wh,10,"Space to toggle edit/simulation mode",1
Text wh,20,"1, 2, 3, 4, 5 - to set drawing mode :"+(drawmode+1),1
Text wh,30,"Up/down arrow keys - to set simulations iterations :"+iterations,1
Text wh,40,"Physicstime :"+physicstime,1
Text wh,50,"Use mouse to create/edit points and constraints :",1
Text wh,60,"Rightclick a point to make it static, shown in red",1
Text wh,70,"Return to toggle this text",1

EndIf

If KeyHit(2) Then drawmode=0
If KeyHit(3) Then drawmode=1
If KeyHit(4) Then drawmode=2
If KeyHit(5) Then drawmode=3
If KeyHit(6) Then drawmode=4

If KeyDown(208) 

	iterations=iterations-1
	
	If iterations<1
	
		iterations=1
	
	EndIf
	
ElseIf KeyDown(200) 

	iterations=iterations+1
	
	
EndIf

If KeyHit(28) Then showtext=1-showtext


Flip

Until KeyDown(1)
End


Function CreateScene()
	
	i=250
	wh=GraphicsWidth()/2
	
	p1.Point=PointNew(wh-50,100+i)
	p2.Point=PointNew(wh+50,100+i)
	
	p3.Point=PointNew(wh-50,200+i)
	p4.Point=PointNew(wh+50,200+i)
	
	c1.Constraint=ConstraintNew(p1,p2)
	c2.Constraint=ConstraintNew(p2,p3)
	c3.Constraint=ConstraintNew(p3,p4)
	c4.Constraint=ConstraintNew(p4,p1)
	c5.Constraint=ConstraintNew(p1,p3)
	c6.Constraint=ConstraintNew(p2,p4)
	
	f=False
	
	For y#=100 To 300 Step 25
		
		pp.Point=PointNew(GraphicsWidth()/2,y)
		If f=False
			
			PointLock pp,True
		
		EndIf
		If ppp.Point<>Null
		
			ConstraintNew pp,ppp
		
		EndIf
		
		ppp.Point=pp
		
		f=True
		
	Next
	
	ConstraintNew p1,ppp
	
	CreateWheel(100,200,100,100,12,4)
	CreateWheel(600,200,100,100,5,2)
	CreateWheel(300,200,70,100,10,2)
	
End Function

Function CreateWheel(cx,cy,rad1,rad2,points,constraintstep=4)

	Local point.Point[100]
	
	
	For po=1 To points
	
		num=num+1
	
		a=(360/points)*po
	
		px=cx+Sin(a)*rad1
		py=cy+Cos(a)*rad2
		
		point[num]=PointNew(px,py)
	
	Next	
		
	For n=1 To num
	
		point1=n
		point2=n+1
		If point2>num
		
			point2=point2-num
		
		EndIf
		
		constraintnew(point[point1],point[point2])
	
		point1=n
		point2=n+constraintstep
		If point2>num
		
			point2=point2-num
		
		EndIf
		
		constraintnew(point[point1],point[point2])
	
	Next
	



End Function



Type Point

	Field x#,y#
	Field vx#,vy#
	Field mass#
	Field rx#,ry#
	Field lock
	Field rememberx#,remembery#
	
End Type

Function PointNew.Point(x#,y#,mass#=1)

	p.Point=New Point
	
	PointMove p,x,y

	Return p
End Function

Function PointDelete(p.Point)

	Delete p
	
	ConstraintRefresh
	
End Function

Function PointMove(p.Point,x#,y#)

	p\x=x
	p\y=y

End Function 

Function PointDraw()

	For p.Point=Each Point
	
		If p\lock=False
		
			Color 255,255,255
		
		Else
		
			Color 255,0,0
		
		EndIf

		Rect p\x-2,p\y-2,5,5,True
	
	Next

End Function

Function PointImpulse(p.Point,ix#,iy#)

	p\vx=p\vx+ix
	p\vy=p\vy+iy

End Function

Function PointUpdate(gravity#=0.5)

	For p.Point=Each Point
	
		PointImpulse p,0,gravity
		
		p\vx=p\vx*0.99
		p\vy=p\vy*0.99
	
		If p\lock=False
	
			p\x=p\x+p\vx
			p\y=p\y+p\vy
			
		EndIf
	
		PointLimits p

	
	Next




End Function

Function PointLimits(p.Point)

	If p\x<0

		p\vx=-p\vx*0.5
		p\x=0

	EndIf
	
	If p\x>GraphicsWidth()-1

		p\vx=-p\vx*0.5
		p\x=GraphicsWidth()-1

	EndIf

	If p\y<0

		p\vy=-p\vy*0.5
		p\y=0

	EndIf
	
	If p\y>GraphicsHeight()-1

		p\vy=-p\vy*0.5
		p\y=GraphicsHeight()-1

	EndIf
		
End Function

Function PointVelocity(p.Point,vx#,vy#)

	p\vx=vx
	p\vy=vy

End Function

Function PointLock(p.Point,lock)

	p\lock=lock
	PointVelocity p,0,0
	
End Function

Function PointHit.Point(x#,y#,size#=10)
	
	sizesq#=size*size
	
	For p.Point=Each Point
	
		dx#=x-p\x
		dy#=y-p\y
		
		d#=dx*dx+dy*dy
		
		If d<sizesq
	
			Return p
	
		EndIf
	
	Next
	
End Function

Function PointRemember()
	
	For p.Point=Each Point
	
		p\rememberx=p\x
		p\remembery=p\y
		
	Next
	
End Function

Function PointRestore()
	
	For p.Point=Each Point
	
		PointVelocity p,0,0
		PointMove p,p\rememberx,p\remembery
					
	Next
	
End Function


Type Constraint

	Field p1.Point
	Field p2.Point
	Field length#
	Field tension#
	Field maxtension#
	Field disable
		
End Type

Function ConstraintNew.Constraint(p1.Point,p2.Point,maxtension#=100.0,length#=-1)

	If ConstraintSameTest(p1,p2)=True
	
		Return Null
	
	EndIf

	c.Constraint=New Constraint
	
	c\p1=p1
	c\p2=p2
	
	ConstraintLength(c,length)
	ConstraintTension(c,maxtension)

	Return c
End Function

Function ConstraintDelete(c.Constraint)

	Delete c

End Function

Function ConstraintSameTest(p1.Point,p2.Point)

	
	For c.Constraint=Each Constraint
	
		If (c\p1=p1 And c\p2=p2) Or (c\p1=p2 And c\p2=p1)
		
			Return True
	
		EndIf
		
	Next



End Function

Function ConstraintLength(c.Constraint,length#=-1)

	If length#<=0

		dx#=c\p2\x-c\p1\x
		dy#=c\p2\y-c\p1\y
		
		c\length=Sqr(dx*dx+dy*dy)
		
	Else	

		c\length=length

	EndIf

End Function

Function ConstraintTension(c.Constraint,maxtension#)

	c\maxtension=maxtension

End Function

Function ConstraintRefresh()

	For c.Constraint=Each Constraint
	
		If c\p1=Null Or c\p2=Null
		
			ConstraintDelete c
	
		EndIf
		
	Next

End Function

Function ConstraintDraw()
	
	
	For c.Constraint=Each Constraint
		If c\disable=False
			tension#=c\tension/c\maxtension
			
			If tension>0
			
				green=255-tension*255
			
			Else
			
				green=255+tension*255
			
			EndIf
			
			If green<0
				
				green=0
				
			EndIf
			
			Color 255,green,green
				
			Line c\p1\x,c\p1\y,c\p2\x,c\p2\y
		
		EndIf
	Next

End Function
	
Function ConstraintTensionDraw()

	For c.Constraint=Each Constraint
		If c\disable=False
			tension#=c\tension/c\maxtension
				
			mx#=(c\p1\x+c\p2\x)*0.5
			my#=(c\p1\y+c\p2\y)*0.5
			
			wid=StringWidth(Int(tension*100)+"%")
			
			Color 0,0,0
			Rect mx-wid*0.5-2,my-5,wid+4,10,True
			
			Color 0,0,255
			Text mx,my,Int(tension*100)+"%",True,True
			
		EndIf
	Next
	
End Function

Function ConstraintDisable(c.Constraint,disable)

	c\disable=disable

End Function

Function ConstraintUpdate(iterations=20)

	For p.Point=Each Point
	
		p\rx=p\x
		p\ry=p\y
	
	Next
	
	For c.Constraint=Each Constraint
	
		c\tension#=0
	
	Next
	
	For n=1 To iterations
		
		For c.Constraint=Each Constraint
			If c\disable=False
							
				dx#=c\p2\x-c\p1\x
				dy#=c\p2\y-c\p1\y
				
				l#=Sqr(dx*dx+dy*dy)
				
				nx#=dx/l
				ny#=dy/l
						
				dl#=((l-c\length))*0.5
				
				c\tension#=c\tension#+dl*2
								
				If c\p1\lock=False
					c\p1\x=c\p1\x+dl*nx
					c\p1\y=c\p1\y+dl*ny
					PointLimits c\p1
				EndIf
				
				If c\p2\lock=False
					c\p2\x=c\p2\x-dl*nx
					c\p2\y=c\p2\y-dl*ny
					PointLimits c\p2
				EndIf
				
				
							
			EndIf
		Next
				
	Next
	
	For c.Constraint=Each Constraint
	
		;c\tension#=c\tension/iterations
		
		If c\tension>c\maxtension
				
			ConstraintDisable c,True
			
		EndIf

	Next
	
	For p.Point=Each Point
	
		p\vx=p\vx+(p\x-p\rx)
		p\vy=p\vy+(p\y-p\ry)
	
	Next


End Function
