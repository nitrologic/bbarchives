; ID: 2584
; Author: skidracer
; Date: 2009-09-18 16:29:56
; Title: basic max2d window event system
; Description: a desktop manager system with balls!

' ancient code from old bmx test folder
' hit f1 to add layer
' draw window frames

Strict

Global System:TSystem

Type TQuad
	Field	x#,y#
	Field	width,height
	Field	image:TImage
	
	Method Draw()
		DrawImage image,x,y
	End Method
End Type

Type TView Extends TQuad
	Field	parent:TView
	Field	task:TTask
	Field	spritelist:TList
	Field	originx,originy
	Field	background[]

	Method FindView:TView(mx,my)
		Local v:TView,p:TView	
		Local t:TList		
		If mx>=x And my>=y And mx<x+width And my<y+height
			spritelist=spritelist.reversed()
			For v=EachIn spritelist
				p=v.FindView(mx,my)
				If p Exit
			Next
			spritelist=spritelist.reversed()
			If p Return p			
			Return Self
		EndIf
	End Method

	Method Move(dx#,dy#)
		Local v:TView
		x:+dx
		y:+dy
		For v=EachIn spritelist
			v.Move(dx,dy)
		Next
	End Method
		
	Method CreateView:TView(t:TTask,x,y,w,h)
		Local	v:TView
		Assert t
		v=New TView
		v.parent=Self
		v.task=t
		v.x=x
		v.y=y
		v.width=w
		v.height=h
		v.spritelist=New TList
		spritelist.addlast v
		Return v
	End Method
	
	Method CreateFrameView:TView(t:TTask,x,y,w,h)
		Local	frame:TView
		Local	view:TView
		frame=CreateView(System,x-4,y-24,w+8,h+28)
		frame.background=[0,255,0]
		view=frame.CreateView(t,x,y,w,h)
		view.background=[0,0,0]
		Return view		
	End Method		
		
	Method Draw()
		Local s:TQuad
		Local vx,vy,vw,vh
		
		vw=width;vh=height
		vx=x;If vx<0 vw:+vx;vx=0
		vy=y;If vy<0 vh:+vy;vy=0
		SetViewport vx,vy,vw,vh
		SetOrigin x+originx,y+originy
		If background
			SetColor background[0],background[1],background[2]
			DrawRect 0,0,width,height
			SetColor 255,255,255
		EndIf
		For s=EachIn SpriteList
			s.Draw()
		Next	
	End Method
			
	Method CreateSprite:TQuad(image:TImage,x#=0,y#=0)
		Local	s:TQuad
		s=New TQuad
		s.x=x
		s.y=y
		s.width=image.width
		s.height=image.height
		s.image=image
		spritelist.addlast s
		Return s
	End Method
End Type

Type TDisplay Extends TView
		
	Method Draw()
		SetViewport 0,0,width,height
		Cls
		Super.Draw()
		Flip
	End Method
	
	Function CreateDisplay:TDisplay(t:TTask,w,h)
		Local	d:TDisplay
		Graphics w,h',32
		d=New TDisplay
		d.task=t
		d.width=w
		d.height=h
		d.spritelist=New TList
		Return d
	End Function
End Type

Const MOUSELCLICK=1
Const MOUSERCLICK=2
Const MOUSELDRAG=3
Const MOUSERDRAG=4
Const MOUSELRELEASE=5
Const MOUSERRELEASE=6
Const CHARKEY=7

Type TMessage
	Field	link:TMessage
	Field	id
	Field	MouseX,MouseY
	Field	MouseXSpeed,MouseYSpeed
	Field	view:TView
	Field	CHARKEY
End Type

Type TTask
	Field	messages:TMessage

	Method Post(MSG:TMessage) 
		Local	m:TMessage
		m=messages
		If m
			While m.link
				m=m.link
			Wend
			m.link=MSG
		Else
			messages=MSG
		EndIf
	End Method
	
	Method GetMessage:TMessage()
		Local	m:TMessage
		m=messages
		If m messages=m.link
		Return m
	End Method
			
	Method Update() Abstract
End Type

Type TSystem Extends TTask
	Field	tasklist:TList
	Field	display:TDisplay
	Field	shutdown
	Field	oldmx,oldmy,oldml,oldmr
	Field	mousefocus:TView
	Field	keyboardfocus:TView
		
	Method Update()
		Local	m:TMessage
		Local	v:TView
		Local	t:TTask
		Local	mx,my,ml,mr,mouseevent,c
				
		mx=MouseX()
		my=MouseY()
		
		ml=MouseDown(1)
		mr=MouseDown(2)	
		
		If mx<>oldmx Or my<>oldmy 
			If ml mouseevent=MOUSELDRAG
			If mr mouseevent=MOUSERDRAG
		EndIf
		
		If ml And (Not oldml) mouseevent=MOUSELCLICK
		If mr And (Not oldmr) mouseevent=MOUSERCLICK
		
		If (Not ml) And oldml mouseevent=MOUSELRELEASE
		If (Not mr) And oldmr mouseevent=MOUSERRELEASE
						
		If mouseevent
			v=mousefocus
			If v=Null Or (mouseevent=MOUSELCLICK Or mouseevent=MOUSERCLICK)
				v=display.FindView(mx,my)
				mousefocus=v
				keyboardfocus=v
			EndIf
			If v
				m=New TMessage
				m.id=mouseevent
				m.MouseX=mx
				m.MouseY=my
				m.MouseXSpeed=mx-oldmx
				m.MouseYSpeed=my-oldmy
				m.view=v
				v.task.Post m
			EndIf
			If mouseevent=MOUSELRELEASE Or mouseevent=MOUSERRELEASE
				mousefocus=Null
			EndIf
		EndIf
		
		oldmx=mx;oldmy=my;oldml=ml;oldmr=mr

		t=Self
		If keyboardfocus t=keyboardfocus.task
		If v
			While True
				c=GetChar()
				If c=0 Exit
				m=New TMessage
				m.id=CHARKEY
				m.MouseX=mx
				m.MouseY=my
				m.CHARKEY=c
				t.Post m
			Wend
		EndIf
				
		m=GetMessage()
		While m
			DebugLog "message says:"+m.ToString()
			If m.id=MOUSELDRAG
				m.view.Move(m.MouseXSpeed,m.MouseYSpeed)
			EndIf
			m=GetMessage()
		Wend
	
		For t=EachIn tasklist
			t.Update()
		Next
				
		If KeyHit(KEY_ESCAPE) shutdown=True
	
	End Method
	
	Method AddTask(t:TTask)
		tasklist.addlast t
	End Method
	
	Method Run()
		While Not shutdown
			Update()
			display.Draw()		
		Wend
	End Method
	
	Function CreateSystem:TSystem(w,h)
		Local s:TSystem
		s=New TSystem
		s.tasklist=New TList	
		s.display=TDisplay.CreateDisplay(s,w,h)
		Return s
	End Function
End Type



System=TSystem.CreateSystem(1024,768)
System.AddTask TBallTask.Create(1,100,100,200,200)

SetBlend ALPHABLEND

System.Run

End

Function Normalize(x#Var,y#Var,z#Var)
	Local	l#
	l=x*x+y*y+z*z
	If l
		l=1.0/Sqr(l)
		x:*l;y:*l;z:*l
	EndIf
End Function		

Function CreateSphere:TImage(d)
	Local	image:TImage,pixmap:TPixmap
	Local	pix[],x,y,r#,f#,a,pf
	Local	dx#,dy#,dz#,l
	Local	lx#,ly#,lz#
	
	pf=PF_RGBA8888
	pixmap=CreatePixmap(d,d,pf)
	pix=New Int[d]
	r=0.5*d
	lx=0.5;ly=-0.5;lz=1.5;Normalize lx,ly,lz	
	For y=0 Until d
		For x=0 Until d
			dx=x+.5-r
			dy=y+.5-r
			f=dx*dx+dy*dy		'calc 3d vector for point on sphere
			dx=dx/r
			dy=dy/r
			dz=Sqr(1.0-(dx*dx+dy*dy))			
			l=16+255*(lx*dx+ly*dy+lz*dz)	'calc light from dot product
			l=Max(0,l)
			l=Min(255,l)
			l=l | (l Shl 8) | (l Shl 16)
			f=Sqr(f)
			a=0	
			If f<r
				a=255*(r-f)
				If a>255 a=255
			EndIf
			pix[x]=(a Shl 24)|l
?MACOS	
			pix[x]=(a)|(l Shl 8)
?
		Next
		CopyPixels pix,pixmap.pixelptr(0,y),pf,d
	Next
	image=LoadImage(pixmap)
	Return image
End Function

Function CreateCircle:TImage(d)
	Local	image:TImage,pixmap:TPixmap
	Local	pix[],x,y,r#,rr#,f#,a,pf
	pf=PF_RGBA8888
	pixmap=CreatePixmap(d,d,pf)
	pix=New Int[d]
	r=0.5*d
	rr=r*r
	For y=0 Until d
		For x=0 Until d
			f=Sqr((x+.5-r)*(x+.5-r)+(y+.5-r)*(y+.5-r))
			a=0
			If f<r
				a=255*(r-f)
				If a>255 a=255
			EndIf
			pix[x]=(a Shl 24)|$ffffff
?MACOS	
			pix[x]=(a)|$ffffff00
?
		Next
		CopyPixels pix,pixmap.pixelptr(0,y),pf,d
	Next
	image=LoadImage(pixmap)
	Return image
End Function

Type TBall
	Field	parent:TBallTask
	Field	sprite:TQuad	
	Field	x#,y#,vx#,vy#
	
	Method Update()
		Local	w,h
		w=parent.view.width-sprite.width
		h=parent.view.height-sprite.height
		x:+vx
		y:+vy
		If x<0 x=0;vx=Abs(vx)
		If y<0 y=0;vy=Abs(vy)
		If x>w x=w;vx=-Abs(vx)
		If y>h y=h;vy=-Abs(vy)
		sprite.x=x
		sprite.y=y
	End Method
End Type

Type TBallTask Extends TTask
	Field 	view:TView
	Field	image:TImage
	Field	balls:TList

	Function Create:TBallTask(n,x,y,w,h)
		Local	b:TBallTask,i
		b=New TBallTask		
		b.view=System.display.CreateFrameView(b,x,y,w,h)
		b.image=CreateSphere(32)
'		b.image=CreateCircle(32)
		b.balls=New TList
		For i=1 To n
			b.AddBall()
		Next
		Return b
	End Function

	Method AddBall()
		Local	b:TBall
		b=New TBall
		b.parent=Self
		b.x=Rnd(view.width)
		b.y=Rnd(view.height)
		b.vx=Rnd(-1.0,1.0) 
		b.vy=Rnd(-1.0,1.0)
		b.sprite=view.CreateSprite(image)
		balls.addlast b
	End Method
	
	Method Update()
		Local	b:TBall
		Local	m:TMessage
				
		m=GetMessage()
		While m
			m=GetMessage()
		Wend
		
		If KeyHit(KEY_F1)
			System.AddTask TBallTask.Create(256,100,100,400,300)
		EndIf
		
		For b=EachIn balls
			b.Update()
		Next		
	End Method
End Type
