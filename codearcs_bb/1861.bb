; ID: 1861
; Author: Jeppe Nielsen
; Date: 2006-11-15 00:31:45
; Title: Pool Game
; Description: Just a simple pool game done in 2 hours

Graphics3D 800,600,32,2

AppTitle "Pool Game done in two hours by Jeppe Nielsen"

SeedRnd MilliSecs()

Repeat 

	gametype=MenuNew()

	If gametype=0
		End
	Else
		GameNew(gametype-1)
		GameDelete
	EndIf

Forever


End

Function MenuNew()

gametype=2
FlushKeys

st=30
up=200
up2=100

Color 255,255,255

Repeat	
	Cls

	
	If KeyHit(200)
		gametype=gametype+1
		If gametype>2
			gametype=0
		EndIf
	ElseIf KeyHit(208)
		gametype=gametype-1
		If gametype<0
			gametype=2
		EndIf
	EndIf
	

	do=0
		
	Text GraphicsWidth()/2,GraphicsHeight()/2-up+do,"Pool Game By Jeppe Nielsen",1,1 : do=do+st
	Text GraphicsWidth()/2,GraphicsHeight()/2-up+do,"Done in two hours",1,1: do=do+st
	Text GraphicsWidth()/2,GraphicsHeight()/2-up2+do,"Single player",1,1: do=do+st
	Text GraphicsWidth()/2,GraphicsHeight()/2-up2+do,"Two player",1,1: do=do+st
	Text GraphicsWidth()/2,GraphicsHeight()/2-up2+do,"Exit",1,1: do=do+st

	Rect GraphicsWidth()/2-250,GraphicsHeight()/2-up2+50+(2-gametype)*st,500,20,0

	Flip
	
	If KeyDown(1)

		gametype=0

	EndIf

Until KeyDown(28)

Return gametype
End Function



Function GameNew(gametype=0)

BallInit()

cueball.Ball=BallNew(250,300,10,8,0)

table.Table=TableNew(400-250,300-150,500,300)

turn=0
break=True
gamestate=0
player1balls=-1
fault=False

Repeat

Cls

If gamestate=0 Or gamestate=5 Or gamestate=10
	
	If gametype=0
		Text GraphicsWidth()/2,20,"Player "+Str(turn+1),1,1
	Else
		If turn=0
			Text GraphicsWidth()/2,20,"Player 1",1,1
		Else
			Text GraphicsWidth()/2,20,"Computer",1,1
		EndIf
			
	EndIf
	
EndIf

If gamestate<20
	
	TableDraw table
	BallDraw
	BallUpdate table
	
EndIf

Select gamestate

	Case 0
	
		If turn=0 Or gametype=0
		
			dx#=MouseX()-cueball\x
			dy#=MouseY()-cueball\y
			
			length#=Sqr(dx*dx+dy*dy)
			
			ang#=ATan2(dy,dx)+180
			
			If MouseHit(1)
			
				impulse#=Sqr(dx*dx+dy*dy)*0.06
				
				BallImpulse cueball,Cos(ang#)*impulse,Sin(ang#)*impulse
				
				gamestate=1
							
			EndIf
			
		Else
		
			If player1balls=-1
				ang#=AiFindAngle#(table,player1balls,cueball\x,cueball\y)
			Else
				ang#=AiFindAngle#(table,1-player1balls,cueball\x,cueball\y)
			EndIf
														
			impulse#=7
			
			BallImpulse cueball,Cos(ang#)*impulse,Sin(ang#)*impulse
			
			gamestate=1
		
			
		EndIf
		
	Case 1	
	
		If BallInMotionTest()=False
	
			turnrem=turn
				
			countrem=-1
			If player1balls>-1
				countrem=0
				For b.Ball=Each ball
				
					If b\number>=1+((turnrem=1)*(1-player1balls)+(turnrem=0)*player1balls)*8 And b\number<=7+((turnrem=1)*(1-player1balls)+(turnrem=0)*player1balls)*8
						
						countrem=countrem+1
					
					EndIf
				
				Next
			EndIf
							
			If break=True
			
				turn=1-turnrem
				For e.BallEvent=Each BallEvent
					If e\typ=ballevent_inhole
						turn=turnrem
						Exit
					EndIf
				Next
				
				fault=True
						
				For e.BallEvent=Each BallEvent
					If e\typ=ballevent_collision
						If e\ball1=0
							fault=False
							Exit
						EndIf
					EndIf
				Next

			
			Else
			
				turn=1-turnrem
								
				If player1balls=-1
				
					fault=True
						
					For e.BallEvent=Each BallEvent
						If e\typ=ballevent_collision
							If e\ball1=0
								fault=False
								Exit
							EndIf
						EndIf
					Next
												
					For e.BallEvent=Each BallEvent
				
						If e\typ=ballevent_inhole
																											
							If e\ball1>=1 And e\ball1<=7
								player1balls=0+(turnrem)
							ElseIf e\ball1>=9 And e\ball1<=15
								player1balls=1-(turnrem)
							EndIf
							turn=turnrem
							
							Exit
													
						EndIf
								
					Next
					
				Else
									
			
					For e.BallEvent=Each BallEvent
						If e\typ=ballevent_inhole
							If e\ball1>=1+((turnrem=1)*(1-player1balls)+(turnrem=0)*player1balls)*8 And e\ball1<=7+((turnrem=1)*(1-player1balls)+(turnrem=0)*player1balls)*8
								turn=turnrem
								Exit
							EndIf
						EndIf
					Next
					
					fault=True
					
					For e.BallEvent=Each BallEvent
						If e\typ=ballevent_collision
							If e\ball1=0
								If e\ball2>=1+((turnrem=1)*(1-player1balls)+(turnrem=0)*player1balls)*8 And e\ball2<=7+((turnrem=1)*(1-player1balls)+(turnrem=0)*player1balls)*8 Or (e\ball2=8 And countrem=0)
									fault=False
								EndIf
								Exit
							EndIf
						EndIf
					Next
					
				EndIf
						
			EndIf
			
			count=-1
			If player1balls>-1
				count=0
				For b.Ball=Each ball
				
					If b\number>=1+((turn=1)*(1-player1balls)+(turn=0)*player1balls)*8 And b\number<=7+((turn=1)*(1-player1balls)+(turn=0)*player1balls)*8
						
						count=count+1
					
					EndIf
				
				Next
			EndIf
						
			break=False
						
			gamestate=0
			
			If count=0
			
				gamestate=10
								
			EndIf
									
			If fault=True
			
				gamestate=5
				turn=1-turnrem
		
			EndIf
														
			For e.BallEvent=Each BallEvent
				If e\typ=ballevent_inhole
													
					If e\ball1=8;if eight ball shot in hole
											
						If countrem=0
							If e\hole=selectedhole And fault=False
						
								gamestate=30
								Exit
							
							Else
						
								gamestate=20
								Exit
							EndIf
																				
						Else
					
							gamestate=20
							Exit
							
						EndIf
												
					ElseIf e\ball1=0
						
						gamestate=5
						turn=1-turnrem
						
						test=False
						For b.Ball=Each ball
							If b\number=8								
								test=True
								Exit
							EndIf
						Next
						If test=False

							gamestate=20
						
						EndIf
						
						
						Exit
						
					EndIf
				EndIf
			Next
			
			
			
		
			BallEventClear
			
			If cueball=Null
		
				cueball.Ball=BallNew(250,300,10,10,0)
		
			EndIf
			
							
		EndIf
			
		
	Case 5 ;fault
	
		Text GraphicsWidth()/2,50,"Place cue ball",1,1
	
		BallInactive cueball
	
		cueball\x=MouseX()
		cueball\y=MouseY()
				
		If MouseHit(1)
		
			gamestate=0
			BallActive cueball
			
			If player1balls>-1
				count=0
				For b.Ball=Each ball
					If b\number>=1+((turn=1)*(1-player1balls)+(turn=0)*player1balls)*8 And b\number<=7+((turn=1)*(1-player1balls)+(turn=0)*player1balls)*8
						
						count=1
						Exit
					EndIf
				Next
				If count=0
				
					gamestate=10
				
				EndIf
			EndIf
					
						
		EndIf

	
	Case 10;select hole
		
		Text GraphicsWidth()/2,50,"Select hole",1,1
		
		hole=TableInHole(table,MouseX(),MouseY(),30)
		
		If hole<>0
		
			px#=TableHoleCoordX(table,hole)
			py#=TableHoleCoordY(table,hole)
		
			Color 255,255,255
			Rect px-16,py-16,32,32,0
			Color 0,0,0
			Rect px-15,py-15,30,30,0
		
			If MouseHit(1)
				
				selectedhole=hole
				gamestate=0		
							
			EndIf
		
		EndIf
		
	Case 20;win to turn player
	
		If gametype=1
			If turn=0
				Text GraphicsWidth()/2,GraphicsHeight()/2,"Player 1 wins",1,1
			Else
				Text GraphicsWidth()/2,GraphicsHeight()/2,"Computer wins",1,1
			EndIf
		Else
			Text GraphicsWidth()/2,GraphicsHeight()/2,"Player "+Str((turn)+1)+" wins",1,1
		EndIf
		
	
	Case 30 ;turn player loses
		If gametype=1
			If turn=1
				Text GraphicsWidth()/2,GraphicsHeight()/2,"Player 1 wins",1,1
			Else
				Text GraphicsWidth()/2,GraphicsHeight()/2,"Computer wins",1,1
			EndIf
		Else
			Text GraphicsWidth()/2,GraphicsHeight()/2,"Player "+Str((1-turn)+1)+" wins",1,1
		EndIf

End Select
	





If gamestate=0

	Color 255,255,255
	Line cueball\x,cueball\y,cueball\x+Cos(ang#)*length,cueball\y+Sin(ang#)*length
	
EndIf

If gamestate<20

Color 255,255,255
Rect GraphicsWidth()/3-(10)*7-20,550-20,180,40,0
Rect 2*GraphicsWidth()/3-(10)*7-20,550-20,180,40,0

Select player1balls
	
	Case -1
		Text GraphicsWidth()/3,550,"None seleted",True,True
		Text 2*GraphicsWidth()/3,550,"None seleted",True,True
	Case 0
		BallDrawStatic(0,GraphicsWidth()/3-(10)*7,550)
		BallDrawStatic(1,2*GraphicsWidth()/3-(10)*7,550)
	Case 1
		BallDrawStatic(1,GraphicsWidth()/3-(10)*7,550)
		BallDrawStatic(0,2*GraphicsWidth()/3-(10)*7,550)
End Select

If gametype=1

	Text GraphicsWidth()/3,500,"Player 1:",True,True
	Text 2*GraphicsWidth()/3,500,"Computer:",True,True

Else

	Text GraphicsWidth()/3,500,"Player 1:",True,True
	Text 2*GraphicsWidth()/3,500,"Player 2:",True,True

EndIf

EndIf

x=MouseX()
y=MouseY()

Color 255,255,255

Rect x-6,y,13,1
Rect x,y-6,1,13

Flip

Until KeyDown(1)

End Function

Function GameDelete()

	TableClear
	BallClear
	
End Function



Type Table

	Field x#,y#
	Field w#,h#
	Field frame#
	Field tr,tg,tb
	Field fr,fg,fb
	Field holer,holeg,holeb
	Field image
	
End Type

Function TableClear()

	For t.Table=Each Table
		TableDelete t
	Next
	
End Function

Function TableNew.Table(x#,y#,w#,h#,frame#=16,tr=0,tg=200,tb=0,fr=0,fg=230,fb=0,holer=0,holeg=0,holeb=0)

	t.Table=New Table
	t\x=x
	t\y=y
	t\w=w
	t\h=h
	t\tr=tr
	t\tg=tg
	t\tb=tb
	t\fr=fr
	t\fg=fg
	t\fb=fb
	t\holer=holer
	t\holeg=holeg
	t\holeb=holeb
	t\frame=frame
	t\image=CreateImage(t\w,t\h)
	
	SetBuffer ImageBuffer(t\image)
		Color t\fr,t\fg,t\fb
		Rect 0,0,t\w,t\h
		
		Color t\holer,t\holeg,t\holeb
		For x=0 To 2
			For y=0 To 1
				Oval x*t\w*0.5-x*0.5*t\frame*2,y*t\h-y*t\frame*2,t\frame*2,t\frame*2
			Next
		Next
		
		Color t\tr,t\tg,t\tb
		Rect t\frame,t\frame,t\w-t\frame*2,t\h-t\frame*2

		
		;Color t\holer,t\holeg,t\holeb
		;For x=0 To 2
		;	For y=0 To 1
		;		If x<>1
		;		Oval x*t\w*0.5-x*0.5*t\frame*2,y*t\h-y*t\frame*2,t\frame*2,t\frame*2
		;		EndIf
		;	Next
		;Next

			
	SetBuffer BackBuffer()

	Return t

End Function

Function TableDraw(t.Table)

	DrawImage t\image,t\x,t\y

End Function

Function TableDelete(t.Table)

	FreeImage t\image
	
	Delete t
	
End Function

Function TableCollide(t.Table,x#,y#,size#)

test=TableInHole(t,x#,y#,size#*2.5)

If x#-size#<t\x+t\frame
	If test=0
		Return 1
	EndIf
ElseIf x#+size#>t\x+t\w-t\frame
	If test=0
		Return 2
	EndIf
ElseIf y#-size#<t\y+t\frame
	If test=0
		Return 3
	EndIf
ElseIf y#+size#>t\y+t\h-t\frame
	If test=0
		Return 4
	EndIf
EndIf

End Function

Function TableInHole(t.Table,bx#,by#,size#)

		For x=0 To 2
			For y=0 To 1
			
				px#=t\x+(x*t\w*0.5-x*0.5*t\frame*2)+t\Frame*0.5*2
				py#=t\y+(y*t\h-y*t\frame*2)+t\Frame*0.5*2
			
				dx#=bx-px
				dy#=by-py
				
				dist#=Sqr(dx*dx+dy*dy)
				
				If dist#<size ;t\frame
			
					Return (x+1)+(y*3)
			
				EndIf
			
			Next
		Next

End Function

Function TableHoleCoordX(t.Table,hole)
x=((hole-1) Mod 3)
y=(Ceil(hole/4))
px#=t\x+(x*t\w*0.5-x*0.5*t\frame*2)+t\Frame*0.5*2
Return px
End Function

Function TableHoleCoordY(t.Table,hole)
x=((hole-1) Mod 3)
y=(Ceil(hole/4))
py#=t\y+(y*t\h-y*t\frame*2)+t\Frame*0.5*2
Return py
End Function



.ballcolor

Data 255,255,255 ;cue ball
Data 255,255,0 ;1
Data 0,0,255 ;2
Data 255,0,0 ;3
Data 128,0,128 ;4
Data 255,128,0 ;5
Data 0,255,0 ;6
Data 186,64,64 ;7
Data 0,0,0 ;8
Data 255,255,0 ;9
Data 0,0,255 ;10
Data 255,0,0 ;11
Data 128,0,128 ;12
Data 255,128,0 ;13
Data 0,255,0 ;14
Data 186,64,64 ;15

Type Ball
	
	Field x#,y#
	Field vx#,vy#
	
	Field size#
	
	Field mass#
	
	Field number
	
	Field inactive
	
End Type

Dim BallColor(0,0)
Dim BallPlaceTest(0)


Function BallInit(x#=500,y#=300,dist#=20,size#=20)

Dim BallColor(15,2)

Restore ballcolor

For n=0 To 15
	For c=0 To 2
		Read BallColor(n,c)
	Next
Next

Dim BallPlaceTest(15)

For n=1 To 5
	xpos#=x#+(n-1)*dist#*Cos(30)
	For i=1 To n
		ypos#=y#-(n-1)*dist#*Sin(30)+(i-1)*dist#*Sin(30)*2	
		
		If (n=3 And i=2)

			BallNew(xpos,ypos,size*0.5,8,8)
			
		Else
		
			Repeat
			
				typ=Rand(1,15)
						
			Until BallPlaceTest(typ)=False And typ<>8
			
			BallPlaceTest(typ)=True
		
			BallNew(xpos,ypos,size*0.5,8,typ)
		
		EndIf
			
	Next
Next

Dim BallPlaceTest(0)

End Function

Function BallClear()

	For b.Ball=Each Ball
		BallDelete b
	Next
	
End Function

Function BallNew.Ball(x#,y#,size#,mass#,number)

	b.Ball=New Ball
	b\x=x
	b\y=y
	b\size=size
	b\mass=mass
	b\number=number
	b\vx=0
	b\vy=0

	Return b
End Function

Function BallDelete(b.Ball)

	Delete b
	
End Function

Function BallInactive(b.Ball)

	b\inactive=True

End Function

Function BallActive(b.Ball)

	b\inactive=False

End Function


Function BallDrawStatic(typ,x,y)

	If typ=0
	
		For b.Ball=Each Ball
		
			If b\number>=1 And b\number<=7
			
				px=x+(b\number-1)*(b\size*2+3)
				py=y
			
				Color BallColor(b\number,0),BallColor(b\number,1),BallColor(b\number,2)
				
				sized#=b\size*2
				
				Oval px-b\size,py-b\size,sized,sized,True
				
			
				Color 0,0,0
			
				Text px,py,b\number,True,True
				
				Color 255,255,255
			
				Text px-1,py-1,b\number,True,True
					

			EndIf
			
			
			
		Next
		
	Else
	
		For b.Ball=Each Ball
		
			If b\number>=9 And b\number<=15
			
				px=x+(b\number-9)*(b\size*2+3)
				py=y
			
				Color 255,255,255
		
				sized#=b\size*2
				
				Oval px-b\size,py-b\size,sized,sized,True
			
				Color BallColor(b\number,0),BallColor(b\number,1),BallColor(b\number,2)
		
				Rect px-b\size,py-b\size*0.5,b\size*2,b\size,True
				
				Color 0,0,0
			
				Text px,py,b\number,True,True
				
				Color 255,255,255
			
				Text px-1,py-1,b\number,True,True
					

		
			EndIf
			
		Next

	EndIf
	
End Function
	


Function BallDraw()

	For b.Ball=Each Ball
	
		If b\number<9
	
			Color BallColor(b\number,0),BallColor(b\number,1),BallColor(b\number,2)
			
			sized#=b\size*2
			
			Oval b\x-b\size,b\y-b\size,sized,sized,True
					
		Else
		
			Color 255,255,255
		
			sized#=b\size*2
			
			Oval b\x-b\size,b\y-b\size,sized,sized,True
		
			Color BallColor(b\number,0),BallColor(b\number,1),BallColor(b\number,2)
	
			Rect b\x-b\size,b\y-b\size*0.5,b\size*2,b\size,True
							
		EndIf

		If b\number>0
		
			Color 0,0,0
		
			Text b\x,b\y,b\number,True,True
			
			Color 255,255,255
		
			Text b\x-1,b\y-1,b\number,True,True
			
		EndIf

	Next

End Function

Function BallImpulse(b.Ball,ix#,iy#)

	b\vx=b\vx+ix
	b\vy=b\vy+iy

End Function

Function BallUpdate(table.Table)

	For b.Ball=Each Ball
	If b\inactive=False
	
		vel#=Sqr(b\vx*b\vx+b\vy*b\vy)
		
		If vel#<0.1
			b\vx=0
			b\vy=0
		EndIf
				
		For n=1 To 1

		For bb.Ball=Each Ball
			
			If b<>bb
			
				dx#=bb\x-b\x
				dy#=bb\y-b\y
				
				dist#=Sqr(dx*dx+dy*dy)
				
				If dist=<(b\size+bb\size)
																			
					If vel#>0.001
						
						dx#=dx/dist
						dy#=dy/dist
						
					
						r1#=b\mass/(b\mass+bb\mass)
						r2#=1-r1 ;b\mass/(b\mass+bb\mass)
					
						b\vx=b\vx-(dx)*vel#*r2
						b\vy=b\vy-(dy)*vel#*r2
						bb\vx=bb\vx+(dx)*vel#*r1
						bb\vy=bb\vy+(dy)*vel#*r1
						
						BallEventNew(b,bb,ballevent_collision,0)
					
					EndIf
						
					
				EndIf
				
			EndIf
			

		Next
		
		Next
		
		
		

		b\vx=b\vx*0.98
		b\vy=b\vy*0.98
		
		b\x=b\x+b\vx
		b\y=b\y+b\vy

			
		
		
		wall=TableCollide(table,b\x,b\y,b\size)
		
			Select wall
			
				Case 1
					
					b\x=table\x+table\frame+b\size
					b\vx=-b\vx
					
				Case 2	
					b\x=table\x+table\w-table\frame-b\size						
					b\vx=-b\vx
				
				Case 3
				
					b\y=table\y+table\frame+b\size
					b\vy=-b\vy
				
				Case 4
					b\y=table\y+table\h-table\frame-b\size						
					b\vy=-b\vy
				
				
			End Select
		
		hole=TableInHole(table,b\x,b\y,b\size*2.0)
	
		If hole<>0
	
			BallEventNew(b,b,ballevent_inhole,hole)
			BallDelete b
				
		EndIf
		
	EndIf
	Next
		

End Function

Function BallInMotionTest()

	For b.Ball=Each ball

		vel#=b\vx*b\vx+b\vy*b\vy
		If vel#>0.001
			Return True
		EndIf

	Next

End Function

Const ballevent_collision=0
Const ballevent_inhole=1

Type BallEvent

	Field ball1
	Field ball2
	
	Field typ
	
	Field hole

End Type

Function BallEventNew.BallEvent(b1.Ball,b2.Ball,typ,hole)

	e.BallEvent=New BallEvent
	e\ball1=b1\number
	e\ball2=b2\number
	e\typ=typ
	e\hole=hole

	Return e
End Function

Function BallEventDelete(e.BallEvent)

	Delete e

End Function

Function BallEventClear()
	For e.BallEvent=Each BallEvent
		BallEventDelete e
	Next
End Function

Function AiFindClosestBall.Ball(typ,x#,y#)

	dist#=100000
	foundBall.Ball=Null

	For b.Ball=Each Ball
		If (b\number>=1 And b\number<>8 And typ=-1) Or (b\number>=1+(typ)*8 And b\number<=7+(typ)*8)
			dx#=b\x-x#
			dy#=b\y-y#
			
			d#=dx*dx+dy*dy
			If d#<dist#
			
				dist#=d#
				
				foundBall=b
			
			EndIf
			
		EndIf
	Next
	
	Return foundBall

End Function


Function AiFindBestHole(t.Table,b.Ball,x#,y#)

	foundhole=0
	ang#=0

	For hole=1 To 6

		hx#=TableHoleCoordX(t,hole)
		hy#=TableHoleCoordY(t,hole)
		
		
		;if line from ball to hole dosn't collide with any other balls
		If AiLineBallCollide(b,Null,b\x,b\y,hx,hy)=False
			
			;vector from ball to hole
			dx1#=hx#-b\x
			dy1#=hy#-b\y
			
			
			;is the line
			
			
			;normalize vector, length = 1
			l#=Sqr(dx1*dx1+dy1*dy1)
			dx1#=dx1#/l#
			dy1#=dy1#/l#
			
			;vector from ball to coords
			dx2#=x#-b\x
			dy2#=y#-b\y
			
			;normalize vector, length = 1
			l#=Sqr(dx2*dx2+dy2*dy2)
			dx2#=dx2#/l#
			dy2#=dy2#/l#
			
			;angle between vectors:
			
			angle#=ACos(dx1*dx2+dy1*dy2)
			
			If angle>ang
			
				ang=angle
				
				foundhole=hole
			
			EndIf
			
			
		
		EndIf

	Next
	
	Return foundhole

End Function


Function AiFindAngle#(t.Table,typ,x#,y#)

	foundBall.Ball=AiFindClosestBall(typ,x,y)
	
	If foundBall<>Null
		
		hole=AiFindBestHole(t,foundBall,x#,y#)
		
		hx#=TableHoleCoordX(t,hole)
		hy#=TableHoleCoordY(t,hole)
		
		dx#=hx-foundBall\x
		dy#=hy-foundBall\y
		
		l#=Sqr(dx*dx+dy*dy)
		dx=dx/l
		dy=dy/l
		
		px#=foundBall\x-dx*(foundBall\size)*2.0 ;1.95
		py#=foundBall\y-dy*(foundBall\size)*2.0 ;1.95
		
;		Oval hx-3,hy-3,6,6
;		Oval px,py,2,2
;		Flip
;		Stop
		
		
		dx#=px-x
		dy#=py-y
		
		Return ATan2(dy,dx)
			
	EndIf

End Function

;returns true if line collide with any balls, excluding b1 and b2
Function AiLineBallCollide(b1.Ball,b2.Ball,x1#,y1#,x2#,y2#)

	For b.Ball=Each Ball
		If b<>b1
			If b<>b2

				If AiLineCollide(x1,y1,x2,y2,b\x,b\y,b\size*2)=True
				
					Return True
		
				EndIf

			EndIf
		EndIf
	Next

End Function


;Returns the shortest distance from a point to a line
Function AiLineDistance#(x1#,y1#,x2#,y2#,x#,y#)

	dx#=x2-x1
	dy#=y2-y1
	
	d#=Sqr(dx*dx+dy*dy)
	
	px#=x1-x#
	py#=y1-y#
	
	Return Abs(dx*py-px*dy) / d
	
End Function

;Returns true if a point collides with a line within range r
Function AiLineCollide(x1#,y1#,x2#,y2#,x#,y#,r#)

	dx#=x2-x1
	dy#=y2-y1

	d#=Sqr(dx*dx+dy*dy)
	If d#<0.0001
		d#=0.0001
	EndIf
	
	ux=dx/d
	uy=dy/d
	
	dx1#=x-(x1-ux*r)
	dy1#=y-(y1-uy*r)
	
	d#=Sqr(dx1*dx1+dy1*dy1)
	
	dx1=dx1/d
	dy1=dy1/d
	
	dx2#=x-(x2+ux*r)
	dy2#=y-(y2+uy*r)
	
	d#=Sqr(dx2*dx2+dy2*dy2)
	
	dx2=dx2/d
	dy2=dy2/d
	
	dot1#=dx1*ux+dy1*uy
	dot2#=dx2*ux+dy2*uy
	
	Return ((dot1#>=0 And dot2#<=0) Or (dot1#<=0 And dot2#>=0)) And (AiLineDistance(x1,y1,x2,y2,x,y)<r)
		
End Function
