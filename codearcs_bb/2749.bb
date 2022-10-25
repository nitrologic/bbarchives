; ID: 2749
; Author: stayne
; Date: 2010-08-03 14:26:20
; Title: Compass
; Description: another way to include a compass in your game

Graphics3D 640,480,0,2
SetBuffer BackBuffer() 

camera=CreateCamera() 

light=CreateLight() 
RotateEntity light,90,0,0 

cube=CreateCube() 
PositionEntity cube,0,0,5 

compass = CreateSprite()
ScaleSprite compass,1,.1
PositionEntity compass,0,2,3

tex=CreateTexture(128,18) 
SetBuffer TextureBuffer(tex) 
ClsColor 50,50,50 
Cls
font=LoadFont("verdana",19) 
SetFont font 
Color 255,255,255
Text 0,0,"N | E | S | W |"

EntityTexture compass,tex 
SetBuffer BackBuffer()

While Not KeyDown(1)
	
	compassrot# = EntityYaw(cube)
		
	If compassrot# < 0 Then compassrot# = compassrot# + 360
		
	PositionTexture tex,compassrot#/360,0
		
	TurnEntity cube,0,-MouseXSpeed(),0
		
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	RenderWorld()
		
	Flip
	
Wend 

End
