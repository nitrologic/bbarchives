; ID: 321
; Author: jfk EO-11110
; Date: 2002-05-13 16:55:29
; Title: Handle 3D Sprites as 2D Sprites
; Description: Position Sprites to Screencoordinate system.

WIDTH = 800
HEIGHT = 600
halfWIDTH = WIDTH/2
halfHEIGHT = HEIGHT/2

Graphics3D WIDTH,HEIGHT,16,2
SetBuffer BackBuffer()

camera = CreateCamera()
camzoom#=1.0 ; this is default. Other values work as well.
CameraZoom camera,camzoom#
CameraRange camera,1,(halfWIDTH * camzoom#)+1000

sprite=CreateSprite()
; if you want the sprite to be 32*32 Pixels:
ScaleSprite sprite,32/2,32/2

EntityColor sprite,255,0,0

While KeyDown(1)=0
    PositionEntity sprite,MouseX()-halfWIDTH,halfHEIGHT-MouseY(),halfWIDTH * camzoom#
	UpdateWorld
	RenderWorld
	Flip
Wend
EndGraphics
End
