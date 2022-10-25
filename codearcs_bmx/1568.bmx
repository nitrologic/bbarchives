; ID: 1568
; Author: bradford6
; Date: 2005-12-18 17:13:21
; Title: particles
; Description: very simple particles in Blitzmax (WIP)

' particle example

Rem 
This is a demonstration of one way to do particles in Blitzmax.  There are several 
ways to create particle systems. I will attempt to document this as heavily as possible

End Rem

' set up graphics

Graphics 800,600

' create a particle (programmatically, you can uncomment the loadimage line if you have a better particle image
	SetBlend ALPHABLEND
	Local sz:Int =32
	Global particle_image:timage = CreateImage(sz,sz)
	Local alpha:Float = 1.0
		For i = 1 To sz/2
			DrawOval (sz/2)-i,(sz/2)-i,i*2,i*2
			alpha:* 0.75
			SetColor 150+(i*i),150+(i*i),i
			SetAlpha alpha
		Next
	GrabImage(particle_image,0,0) ; Cls

'Global particle_image:Timage = LoadImage("particle.png")

Const FRICTION:Float = 000.999
Const GRAVITY:Float = 000.01

' Temitter is a particle emitter. it gets placed on-screen somewhere and it's job is to spit out particles

Type Temitter
	Field particlelist:tlist
	Field x_position,y_position,z_position
	Field spawn_rate,spawn_timer
	Field x_spread:Float,y_spread:Float
	Field part_life
	Field link:TLink
	Field emitter_life
	Field scale_start:Float,scale_end:Float
	Field initial_xvel:Float,initial_yvel:Float,initial_zvel:Float
	
	Function create:Temitter(x,y,z=0,xv=4,yv=4,zv=0,xspread=4.0,yspread=4.0,zspread=0,rate=5,partlife=200,emitter_life=50)
		Local temp:Temitter = New Temitter
		temp.x_position=x
		temp.y_position=y
		temp.z_position=z
		temp.initial_xvel = Rnd(2.1,xv)
		temp.initial_yvel = Rnd(2.1,yv)
		temp.initial_zvel = zv
		temp.x_spread = xspread
		temp.y_spread = yspread
		temp.spawn_rate = rate
		temp.part_life = partlife
		temp.emitter_life = emitter_life
		temp.particlelist = CreateList()
		Return temp
	End Function
	
	Method spawn()
		'Print "SPAWN"
		Local p:Tparticle = New Tparticle
		p.x_position = Rnd(x_position-(x_spread/2),x_position+(x_spread/2))
		p.y_position = Rnd(y_position-(y_spread/2),y_position+(y_spread/2))
		p.z_position = z_position
		p.xvel = initial_xvel
		p.yvel = initial_yvel
		p.zvel = initial_zvel
		p.angle = Rnd(0,360)
		p.Pimage = particle_image
		p.life = part_life
		p.link = ListAddLast(particlelist,p)
		p.vel = xvel ' temp
		p.Pred = Rnd(0,255)
		p.Pblue = Rnd(0,255)
		p.Pgreen = Rnd(0,255)
		p.scale = Rnd(1,3)
		p.alpha = 1
	End Method

	Method update()
		emitter_life:-1
		If emitter_life < 0
			' kill emitter
			spawn_rate = 20000
			
		EndIf
		If emitter_life < -part_life
			RemoveLink self.link
		
		EndIf
		
		
		
		
		
		
		spawn_timer:+1
		'Print "Spawn:"+spawn_timer
		If spawn_timer > spawn_rate
			self.spawn()
			spawn_timer=0
		EndIf	
		
		
		For Local ptemp:Tparticle = EachIn particlelist
			ptemp.update
			If ptemp.life<0 Then RemoveLink ptemp.link
		
		Next
	End Method
	
	Method kill()
	
	End Method
End Type

Type Tparticle
	Field x_position:Float,y_position:Float,z_position:Float
	Field life:Int
	Field angle:Float, angle_count:Float, angle_dir:Float
	Field vel:Float
	Field xvel:Float
	Field yvel:Float
	Field zvel:Float
	Field scale:Float
	Field alpha:Float
	Field link:TLink
	Field Pimage:Timage ' a reference to the Global Particle image
	Field Pred:Int
	Field Pblue:Int
	Field Pgreen:Int
	
 		
	Method update()
		life:-1
		If life = 0
			self.kill()
		EndIf
		vel = vel * FRICTION
		xvel = xvel * FRICTION
		yvel = yvel * FRICTION
		'yvel = yvel - GRAVITY
		
		'speed:*.99
		'rotation_speed:*.95
		'rotation:+rotation_speed
		
		' wave
		
		If angle_dir = 0
			angle_count:+.1
			angle:+angle_count
			If angle_count > 300 angle_dir = 1 
		Else
			angle_count:-.1
			angle:-angle_count
			If angle_count < 0 angle_dir = 0 
		EndIf
		
		
		
		
		x_position:+(xvel*Sin(angle-90))
		y_position:+(yvel*Cos(angle-90))
		'yvel:-GRAVITY
		'y_position:-yvel
		'Sin(angle)
		'x_position = x_position + xvel
		'y_position = y_position + yvel
		' z
		'scale = scale * 1.01
		SetScale scale,scale
		SetColor Pred,Pblue,Pgreen
		SetBlend  LIGHTBLEND 'ALPHABLEND
		SetAlpha alpha
		alpha:*0.99
		SetRotation(angle)
		DrawImage Pimage,x_position,y_position
		
		
		
	End Method

	Method kill()
	
	End Method


End Type


'	Temitter(x,y,z=0,xv=100,yv=100,zv=0,xspread=5,yspread=5,zspread=0,rate=100)

'Local my_emitter:Temitter = Temitter.create(200,200)
Local emitterlist:Tlist = CreateList()
Local createdelay:Int

Repeat
	Cls
	createdelay:-1
	If MouseDown(1) And createdelay < 0
		Local temp:Temitter = Temitter.create(MouseX(),MouseY())
		temp.link = ListAddLast(emitterlist,temp)	
		createdelay = 3
	EndIf
	
	For Local tem:Temitter = EachIn emitterlist
		tem.update()
	Next
	
	SetColor 255,0,0
	SetRotation 0
	SetScale 1,1
	SetAlpha 1
	'DrawText("memory: "+GCMemAlloced() ,10,10)
	
	
	Flip
Until KeyDown(KEY_ESCAPE)
End
