; ID: 1198
; Author: Jonathan Nguyen
; Date: 2004-11-14 15:28:17
; Title: Asteroids
; Description: A full (except death) asteroids game with a 2D particle system.

; // Initialize Graphics
Graphics 640,480,16,0
SeedRnd MilliSecs()

; // Asteroids
Global ASTEROIDS_NUM=6
Global ASTEROIDS_SIZE=4
Global GUN_SPEED=4 ; Lower is faster, actually delay.
Global GUN_NUM=1

; ////////////////////// FUNCTIONS

; // FPS Limiting
Global tfps#=30
Global spdfct#
Global cticks
Global frmdel=MilliSecs()

; // FPS Measuring
Global fps
Global fps_timer,fps_ptime
Global fps_count

; // Capturing
Global capture,tf,scr_tf$

; // FPS Limiting Update
Function fpslimit()
	If MilliSecs()>=fps_ptime+1000 Then fps_ptime=MilliSecs() : fps=fps_count : fps_count=0
	fps_count=fps_count+1
	cticks=MilliSecs()
	spdfct#=(cticks-frmdel)/(1000.0/tfps#)
	If spdfct#<=0 Then spdfct#=0.00000000001
	frmdel=cticks
	If spdfct#>2 Then spdfct#=2
End Function

; // Screen Shot
Function screenshot(Key)
	If KeyHit(Key)=True
		t=0
		Repeat
			t=t+1
			If t<10 Then scr_t$="00"+Str$(t)
			If t>=10 And t<100 Then scr_t$="0"+Str$(t)
			If t>=100 And t<1000 Then scr_t$=Str$(t)
		Until ReadFile("screen"+scr_t$+".bmp")=0 Or t=>1000
		SaveBuffer FrontBuffer(),"screen"+scr_t$+".bmp"
	EndIf
End Function

; // Distance Function
Function dist#(x1#,y1#,x2#,y2#)
	Return Sqr#((x1#-x2#)^2+(y1#-y2#)^2)
End Function

; // Limit
Function limit#(value#,low#,high#)
	If value#<low# Then Return low#
	If value#>high# Then Return high#
	Return value#
End Function

; ////////////////////// PLAYER

; // Player
Type player
	; Info
	Field x#,y#
	Field xv#,yv#
	Field xa#,ya#
	Field acc#
	Field drag#
	Field vel#
	Field ang#
	Field firedel#
	; Ship
	Field ship_angvel#
	Field ship_acc#
	Field ship_velmul#
	Field ship_firedel#
	; Player
	Field name$
	Field ID
End Type

; // Create Player
Function createplayer(name$,ID,x#,y#,ang#,ship_angvel#,ship_acc#,ship_velmul#,ship_firedel#)
	p.player=New player
	p\name$=name$
	p\ID=ID
	p\x#=x#
	p\y#=y#
	p\ang#=ang#
	p\ship_angvel#=ship_angvel#
	p\ship_acc#=ship_acc#
	p\ship_velmul#=ship_velmul#
	p\ship_firedel#=ship_firedel#
End Function

; // Update Players
Function updateplayers()
	For p.player=Each player
	
	; // USER
	If p\id=0

	; // Controls
	If KeyDown(200)=True
		p\acc#=p\ship_acc#
		p\drag#=p\vel#*p\ship_velmul#
		Else
		p\acc#=0
		p\drag#=0
	EndIf
	If KeyDown(208)=True
		p\drag#=p\vel#*p\ship_velmul#*50
	EndIf
	If KeyDown(203)=True
		p\ang#=p\ang#+p\ship_angvel#*spdfct#
	EndIf
	If KeyDown(205)=True
		p\ang#=p\ang#-p\ship_angvel#*spdfct#
	EndIf
	
	; // Fire
	If KeyDown(57)=True And p\firedel#=<0
		For t=1 To GUN_NUM
			tang#=Rnd(-4,4)
			createbullet(p\x#-(Sin(p\ang#)*8),p\y#-(Cos(p\ang#)*8),p\xv#-(Sin(p\ang#+tang#)*12),p\yv#-(Cos(p\ang#+tang#)*12),45,255-Rand(4),192+Rand(-4,4),64+Rand(-4,4))
		Next
		p\firedel#=p\ship_firedel#
	EndIf
	p\firedel#=p\firedel#-spdfct#
	
	; // PLAYER
	Else
	
	; // END USER/PLAYER
	EndIf
	
	; // Update
	p\xa#=(p\drag#*p\xv#)-(Sin(p\ang#)*p\acc#)
	p\ya#=(p\drag#*p\yv#)-(Cos(p\ang#)*p\acc#)
	p\xv#=p\xv#+p\xa#*spdfct#
	p\yv#=p\yv#+p\ya#*spdfct#
	p\x#=p\x#+p\xv#*spdfct#
	p\y#=p\y#+p\yv#*spdfct#
	p\vel#=dist#(0,0,p\xv#,p\yv#)
	
	; // Boundaries
	If p\x#<0 Then p\x#=p\x#+640
	If p\x#>640 Then p\x#=p\x#-640
	If p\y#<0 Then p\y#=p\y#+480
	If p\y#>480 Then p\y#=p\y#-480
	
	; // Draw Ship
	x1=p\x#-(Sin(p\ang#)*8)
	y1=p\y#-(Cos(p\ang#)*8)
	x2=p\x#-(Sin(p\ang#+140)*6)
	y2=p\y#-(Cos(p\ang#+140)*6)
	x3=p\x#-(Sin(p\ang#-140)*6)
	y3=p\y#-(Cos(p\ang#-140)*6)
	Color 255,255,255
	Line x1,y1,x2,y2
	Line x2,y2,x3,y3
	Line x3,y3,x1,y1
	
	Next
End Function

; ////////////////////// ASTEROIDS

; // Asteroid
Type asteroid
	Field x#,y#
	Field xv#,yv#
	Field ang#,angvel#
	Field rad#[8]
	Field avgrad#
	Field size
	Field life
	Field cr,cg,cb
End Type

; // Create Asteroid
Function createasteroid(x#,y#,xv#,yv#,size)
	a.asteroid=New asteroid
	a\x#=x#
	a\y#=y#
	a\xv#=xv#
	a\yv#=yv#
	a\ang#=Rnd(360)
	a\angvel#=Rnd(-6,6)
	a\size=size
	a\life=size
	tcol=Rand(-48,48)
	a\cr=128+tcol
	a\cg=128+tcol
	a\cb=128+tcol
	; // Create "Rockiness"
	a\avgrad#=0
	For t=0 To 7
		a\rad#[t]=size*8.0+Rnd(-size*4.0,size*4.0)
		a\avgrad#=a\avgrad#+a\rad#[t]
	Next
	a\avgrad#=a\avgrad#/6.0
	a\rad#[8]=a\rad#[0]
End Function

; // Fill Asteroids
Function fillasteroids(num,size)
	For t=1 To num
		Repeat
			tx#=Rnd(640)
			ty#=Rnd(480)
		Until ( tx#<280 Or tx#>360 ) And ( ty#<200 Or ty#>280 )
		createasteroid(tx#,ty#,Rnd(-3,3),Rnd(-3,3),size+Rand(1))
	Next
End Function

; // Update Asteroids
Function updateasteroids()
	For a.asteroid=Each asteroid
		
		; // Update
		a\x#=a\x#+a\xv#*spdfct#
		a\y#=a\y#+a\yv#*spdfct#
		a\ang#=a\ang#+a\angvel#*spdfct#
		
		; // Boundaries
		If a\x#<-a\avgrad# Then a\x#=a\x#+640+a\avgrad#*2
		If a\x#>640+a\avgrad# Then a\x#=a\x#-640-a\avgrad#*2
		If a\y#<-a\avgrad# Then a\y#=a\y#+480+a\avgrad#*2
		If a\y#>480+a\avgrad# Then a\y#=a\y#-480-a\avgrad#*2
		
		; // Draw
		tmul#=360.0/8.0
		Color a\cr,a\cg,a\cb
		For t=0 To 7
			Line a\x#-(Sin(a\ang#+(t)*tmul#)*a\rad#[t]),a\y#-(Cos(a\ang#+(t)*tmul#)*a\rad#[t]),a\x#-(Sin(a\ang#+(t+1)*tmul#)*a\rad#[t+1]),a\y#-(Cos(a\ang#+(t+1)*tmul#)*a\rad#[t+1])
		Next
	Next
End Function

; ////////////////////// BULLETS

; // Bullet
Type bullet
	Field x#,y#
	Field xv#,yv#
	Field life#
	Field cr,cg,cb
End Type

; // Create Bullet
Function createbullet(x#,y#,xv#,yv#,life#,cr,cg,cb)
	b.bullet=New bullet
	b\x#=x#
	b\y#=y#
	b\xv#=xv#
	b\yv#=yv#
	b\life#=life#
	b\cr=cr
	b\cg=cg
	b\cb=cb
End Function

; // Update Bullets
Function updatebullets()
	For b.bullet=Each bullet
		
		; // Update
		b\x#=b\x#+b\xv#*spdfct#
		b\y#=b\y#+b\yv#*spdfct#
		
		; // Boundaries
		If b\x#<0 Then b\x#=b\x#+640
		If b\x#>640 Then b\x#=b\x#-640
		If b\y#<0 Then b\y#=b\y#+480
		If b\y#>480 Then b\y#=b\y#-480
		
		; // Life
		b\life#=b\life#-spdfct#
		If b\life#<0 Then b\life#=0
		
		; // Draw
		If b\life#=<15.0
			tmul#=b\life#/15.0
			Else
			tmul#=1.0
		EndIf
		Color b\cr*tmul#,b\cg*tmul#,b\cb*tmul#
		Line b\x#,b\y#,b\x#+b\xv#,b\y#+b\yv#
		
		; // Collision
		For a.asteroid=Each asteroid
			If dist#(b\x#,b\y#,a\x#,a\y#)=<a\avgrad#
				a\life=a\life-1
				b\life#=0
				For t=1 To 4
					createparticle(b\x#,b\y#,Rnd(-8,8),Rnd(-8,8),0.95,30,PDM_spark,255,192,64,16)
				Next
				For t=1 To 4
					createparticle(b\x#,b\y#,Rnd(-4,4),Rnd(-4,4),0.95,60,PDM_smlparticle,160,160,160,0)
				Next
				If a\life=<0
				
					; // Particle Effects
					For t=1 To 8
						createparticle(a\x#,a\y#,Rnd(-10,10),Rnd(-10,10),0.95,30,PDM_spark,255,192,64,64)
					Next
					For t=1 To 6
						createparticle(a\x#,a\y#,Rnd(-6,6),Rnd(-6,6),0.95,30,PDM_medparticle,255,192,64,128)
					Next
					For t=1 To 6
						createparticle(a\x#,a\y#,Rnd(-8,8),Rnd(-8,8),0.99,60,PDM_smlparticle,160,160,160,0)
					Next
					For t=1 To 5
						createparticle(a\x#,a\y#,Rnd(-6,6),Rnd(-6,6),0.99,60,PDM_medparticle,160,160,160,0)
					Next
					For t=1 To 4
						createparticle(a\x#,a\y#,Rnd(-4,4),Rnd(-4,4),0.99,60,PDM_bigparticle,160,160,160,0)
					Next
					
					; // Split Asteroid
					If a\size>1
						For t=1 To 2
							createasteroid(a\x#,a\y#,Rnd(-5,5),Rnd(-5,5),a\size-1)
						Next
					EndIf
					Delete a.asteroid
				EndIf
			EndIf
		Next
		
		; // Kill
		If b\life#=0
			Delete b.bullet
		EndIf
	Next
End Function

; ////////////////////// PARTICLES

; // Particle
Type particle
	Field x#,y#
	Field xv#,yv#
	Field vm#
	Field life#,mlife#
	Field drawmode
	Field cr,cg,cb
	Field cflash
End Type
Const PDM_smlparticle=0
Const PDM_medparticle=1
Const PDM_bigparticle=2
Const PDM_spark=3

; // Create Particle
Function createparticle(x#,y#,xv#,yv#,vm#,life#,drawmode,cr,cg,cb,cflash)
	c.particle=New particle
	c\x#=x#
	c\y#=y#
	c\xv#=xv#
	c\yv#=yv#
	c\vm#=vm#
	c\life#=life#
	c\mlife#=life#
	c\drawmode=drawmode
	c\cr=cr
	c\cg=cg
	c\cb=cb
	c\cflash=cflash
End Function

; // Update Particles
Function updateparticles()
	For c.particle=Each particle
		
		; // Update
		c\x#=c\x#+c\xv#*spdfct#
		c\y#=c\y#+c\yv#*spdfct#
		c\xv#=c\xv#*(1.0-(1.0-c\vm#)*spdfct#)
		c\yv#=c\yv#*(1.0-(1.0-c\vm#)*spdfct#)
		c\life#=c\life#-spdfct#
		If c\life#<0 Then c\life#=0
		
		; // Draw
		tmul#=c\life#/c\mlife#
		tfls=Rand(-c\cflash,c\cflash)
		Color limit#(c\cr*tmul#+tfls,0,255),limit#(c\cg*tmul#+tfls,0,255),limit#(c\cb#*tmul+tfls,0,255)
		Select c\drawmode
			Case PDM_smlparticle
				Plot c\x#,c\y#
			Case PDM_medparticle
				Oval c\x#-1,c\y#-1,3,3
			Case PDM_bigparticle
				Oval c\x#-2,c\y#-2,5,5
			Case PDM_spark
				Line c\x#,c\y#,c\x#+c\xv#,c\y#+c\yv#
		End Select
		
		; // Kill
		If c\life#=0
			Delete c.particle
		EndIf
	Next
End Function

; ////////////////////// MAIN

; // Create User
createplayer("Player",0,320,240,0,6,0.16,-0.0005,GUN_SPEED)

; // Create Asteroids
fillasteroids(ASTEROIDS_NUM,ASTEROIDS_SIZE)

; // Create Background
Global background=CreateImage(GraphicsWidth(),GraphicsHeight())
	SetBuffer ImageBuffer(background)
	For t=1 To 300*(640.0/GraphicsWidth())
		tcol=Rand(64,192)
		Color tcol,tcol,tcol
		Plot Rand(GraphicsWidth()),Rand(GraphicsHeight())
	Next

; // Main Loop
SetBuffer BackBuffer()
While Not KeyDown(1)

	; // Clear Screen
	Cls
	
	; // Draw Background
	DrawImage background,0,0
	
	; // FPS Limit
	fpslimit()
	
	; // Update Particles
	updateparticles()
	
	; // Update Players
	updateplayers()
	
	; // Update Bullets
	updatebullets()
	
	; // Update Asteroids
	updateasteroids()

	; // Flip Buffers
	Flip
	
	; // Screenshot
	screenshot(88)

; // End Main Loop
Wend

; // Clear and End Program
For p.player=Each player
	Delete p.player
Next
For a.asteroid=Each asteroid
	Delete a.asteroid
Next
For b.bullet=Each bullet
	Delete b.bullet
Next
For c.particle=Each particle
	Delete c.particle
Next
End
