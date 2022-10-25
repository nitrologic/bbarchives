; ID: 1718
; Author: bradford6
; Date: 2006-05-24 16:04:17
; Title: asteroids movement
; Description: thrust and turn like in classic asteroids

Strict
Graphics 1024,768
AutoMidHandle(True)


Local GW = GraphicsWidth()
Local GH = GraphicsHeight()
Local midw=GW/2
Local midh=GH/2
Local angle:Float
Local Thrust:Float  
Local ThrustX:Float 
Local ThrustY:Float
Local Angular_Velocity:Float
Local Friction:Float = 0.99
Local Xpos:Float = midw
Local Ypos:Float = midh
Local Velocity:Float  
Local Xvelocity:Float  
Local Yvelocity:Float

Local ship:timage = CreateImage(64,64)
	' draw the ship...
	SetColor 0,255,0
	DrawLine 0,0,64,32
	DrawLine 64,32,0,64
	SetColor 50,50,50
	DrawRect 0,16,32,32
	SetColor 255,255,0
	DrawOval 36,24,22,16
	GrabImage(ship,0,0)
Cls


Repeat
	' Calculate the Heading of the ship (the Angle)
		Angular_Velocity:*Friction	' slow the spin
		angle:+Angular_Velocity		' apply the angular velocity to the current Angle 
		
	' Calculate the Speed 
		
		XVelocity:*Friction			' slow the speed down (I know there is no friction in space but It looks better :) 
		Yvelocity:*Friction
		Thrust:*0.95				' this reduces the thrust amount each frame
	
	' Here we are dividing the Thrust into its X and Y components
	
		ThrustX = Thrust*Cos(angle)
		ThrustY = Thrust*Sin(angle)
	' now we add the resulting Thrust to the Velocities
		
		Xvelocity:+ Thrustx
		Yvelocity:+ Thrusty
	' AND update the ships current X,Y Cartesian Coordinates
		
		Xpos:+XVelocity
		Ypos:+YVelocity
		
	
	' Get Key Input
	If KeyDown(KEY_UP) Then Thrust:+0.01
		
	
	
	If KeyDown(KEY_LEFT) Then Angular_Velocity:-0.1
	If KeyDown(KEY_RIGHT) Then Angular_Velocity:+0.1
	
		
	' WRAP to SCREEN
	If xpos > GW Then Xpos = 0
	If ypos > GH Then Ypos = 0
	If Xpos < 0 Then Xpos = GW
	If Ypos < 0 Then Ypos = GH
		
	' Draw the stuff to the screen	
		SetRotation angle
		DrawImage ship, Xpos , Ypos
		SetRotation 0
		SetColor 255,0,0
		DrawRect midw-20,midh-20,40,40
		SetColor 255,255,255
		
	Flip
	Cls
Until KeyDown(KEY_ESCAPE)
