; ID: 1833
; Author: SillyPutty
; Date: 2006-10-03 20:14:22
; Title: Shadows
; Description: How to draw 2D shadows

SuperStrict

Graphics 640,480

Global xpos:Float
Global ypos:Float
Global angle:Float
Global Falloff:Int = 1000

SetClsColor 125,125,125

Function DrawShadowCaster(x1:Float,y1:Float,x2:Float,y2:Float)
	
	Local shadowCaster:Float[8]
	Local lightAngle:Float
	
	lightAngle = ATan2(xpos-x2,ypos-y2)

	SetColor 0,0,0
	
	shadowCaster[0] = x1
	shadowCaster[1] = y1
	shadowCaster[2] = x2
	shadowCaster[3] = y2
	shadowCaster[4] = x2-Sin(lightAngle)*Falloff
	shadowCaster[5] = y2-Cos(lightAngle)*Falloff

	lightAngle = ATan2(xpos-x1,ypos-y1)

	shadowCaster[6] = x1-Sin(lightAngle)*Falloff
	shadowCaster[7] = y1-Cos(lightAngle)*Falloff
	
	DrawPoly ShadowCaster
	SetColor 255,255,255
	DrawLine x1,y1,x2,y2
	
End Function


While not KeyDown(KEY_ESCAPE)
	
	
	xpos = MouseX()
	ypos = MouseY()
	
	SetColor 255,255,0
	DrawOval xpos-5,ypos-5,10,10
	
	' box
	DrawShadowCaster 200,200,400,200
	DrawShadowCaster 200,200,200,100 
	DrawShadowCaster 200,100,400,100
	DrawShadowCaster 400,100,400,200
	
	'wall
	DrawShadowCaster 200,400,400,400

	DrawLine 200,200,400,200
	DrawLine 200,200,200,100
	DrawLine 200,100,400,100
	DrawLine 400,100,400,200
	
	Flip
	Cls
Wend
