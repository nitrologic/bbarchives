; ID: 500
; Author: Tracer
; Date: 2002-11-20 23:42:19
; Title: fast fading of 2d or 3d screen
; Description: quick screen fade, VERY fast yet very cheated :)

; ########################################
; # CHEATED FADING USING 3D ON TOP OF 2D #
; ########################################
; #   BY TRACER (tracer@darkeffect.com)  #
; ########################################

Graphics3D 640,480,0,2 				; 3D mode

; create something to fade to and from
Global logo = CreateImage(640,480)
SetBuffer ImageBuffer(logo)
For t = 1 To 10000
	Color Rnd(100,255),Rnd(100,255),Rnd(100,255)
	Rect Rnd(0,639),Rnd(0,479),Rnd(10,50),Rnd(10,50)
Next
SetBuffer BackBuffer()

camera = CreateCamera()
CameraClsMode camera,0,1 ; NEEDED! for 3d on top of 2D

Global fade_sprite = CreateSprite() ; create sprite

; color of sprite, this determines the color to fade from and to
EntityColor fade_sprite,0,0,0

MoveEntity fade_sprite,0,0,1 ; move to just before camera
EntityOrder fade_sprite,-100 ; make sure it gets drawn last

Delay 1000

intro()

Function intro()
	Local fade_in = False
	Local fade_out = False
	Local done = False
	Local fade_level# = 1
	Local timer = 0
	
	While done = False
		timer = timer + 1
		
		If timer = 100
			fade_in = True
		EndIf
		
		Cls
		; background 2d (included in fade)

		DrawImage logo,0,0
			
		; 3D (used for 3d sprite that does fade)
	
		If fade_in = True
			EntityAlpha fade_sprite,fade_level#
			fade_level# = fade_level# - .01
			If fade_level# <= 0
				fade_level# = 0
				fade_in = False
			EndIf
		EndIf
		
		If fade_out = True
			EntityAlpha fade_sprite,fade_level#
			fade_level# = fade_level# + .01
			If fade_level# => 1
				fade_level# = 1
				done = True
			EndIf
		EndIf		
		
		If GetKey() Or timer >= 500
			fade_in = False
			fade_out = True
		EndIf
			
		RenderWorld

		; foreground 2D (not included in fade)

		Color 255,255,255
		Text 250,16,"This will not fade!"

		Flip
	Wend	
End Function
