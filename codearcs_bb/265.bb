; ID: 265
; Author: Vorderman
; Date: 2002-03-12 03:53:52
; Title: Carve through the fog
; Description: Wispy fog that a moving entity can carve a trail through

AppTitle "Wispy Fog"
Graphics3D 800,600,32,2
SetBuffer BackBuffer()
Color 255,255,255
SeedRnd MilliSecs()
HidePointer


;-------------- CONSTANTS ---------------------------
Const gameFPS 		= 50				;frame limiting
Const MAX_FOG		= 50
Const SPREAD#		= 5
Const GLOBAL_MUSH#  = 1.0


;-------------- GLOBALS -----------------------------
Global CAM_yaw#		= 0.0
Global CAM_zoom#	= 20
Global NULLPIVOT 	= CreatePivot()


;-------------- TYPES -------------------------------
Type TYPE_FOG
	Field x#,y#,z#
	Field xs#,zs#
	Field sprite
	Field ax#,az#
	Field mushiness#
End Type

Dim FOG.TYPE_FOG(MAX_FOG)
For a=1 To MAX_FOG
	FOG.TYPE_FOG(a) = New TYPE_FOG
	FOG(a)\sprite = LoadSprite("fog particle.bmp",1) ;load a 16x16 pixel greyscale sprite
	ScaleSprite FOG(a)\sprite,2.5,2.5
	FOG(a)\x# = Rnd#(-SPREAD#,SPREAD#)
	FOG(a)\y# = 1
	FOG(a)\z# = Rnd#(-SPREAD#,SPREAD#)
	FOG(a)\xs# = 0.0
	FOG(a)\zs# = 0.0
	FOG(a)\ax# = FOG(a)\x#
	FOG(a)\az# = FOG(a)\z#
	PositionEntity FOG(a)\sprite,FOG(a)\x#,FOG(a)\y#,FOG(a)\z#
	FOG(a)\mushiness# = 0.0
Next


;-------------- ENTITIES ------------------------------
Global player = CreateSphere()
PositionEntity player,0,1.0,20
EntityColor player,255,100,0

Global camera1=CreateCamera()
CameraViewport camera1,0,0,800,600
PositionEntity camera1,0,20,-20
CameraClsColor camera1,50,50,100
RotateEntity camera1,45,0,0

AmbientLight 150,150,150
light1 = CreateLight(2)
PositionEntity light1,500,500,-500


;-------------- PRE-GAME SETUP ---------------------------
framePeriod = 1000 / gameFPS
frameTime = MilliSecs () - framePeriod
MoveMouse 400,300


;-------------- M A I N  L O O P ---------------------------
While Not KeyHit(1)

	;frame limiting
    Repeat
        frameElapsed = MilliSecs () - frameTime
    Until frameElapsed
    frameTicks = frameElapsed / framePeriod
    frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod)

	;game logic loop
    For frameLimit = 1 To frameTicks

		;frame limiting    
        If frameLimit = frameTicks Then CaptureWorld
        frameTime = frameTime + framePeriod

		;mouse to move blob
		MoveEntity player,(MouseXSpeed() / 10.0),0,-(MouseYSpeed() / 10.0)
		MoveMouse 400,300
		
		;update the fog
		FUNC_process_fog()

		;update game world logic
        UpdateWorld

    Next


	;render
	RenderWorld frameTween


	;calculate FPS
	If (frameTicks>0) 
		FPS = gameFPS/frameTicks
	Else
		FPS = gameFPS
	EndIf


	;on-screen FPS counter
	Color 255,255,255
	Text 700,10,"FPS:"+FPS
	Text 350,10,"WISPY FOG TEST"
	
	Text 290,550,"USE MOUSE TO MOVE ORANGE BLOB"
	Color 255,0,0
	Text 295,562,"MOVE SLOWLY FOR BEST RESULTS"
	
	;flip buffers	
	Flip

Wend

End






Function FUNC_process_fog()

	For f.TYPE_FOG = Each TYPE_FOG

		dist# = EntityDistance(f\sprite , player)
		PositionEntity NULLPIVOT,f\ax#,1.0,f\az#
		dist2# = EntityDistance(f\sprite , NULLPIVOT)
				
		EntityAlpha f\sprite,((1.0/2.0)*(1.0-(dist2#/2.0)))

		If ( dist# < 2.0 ) And (dist2# < 2.0)
				
			PointEntity f\sprite , player
			TurnEntity f\sprite,0,180,0
			MoveEntity f\sprite ,0,0,(dist# / 5.0);0.1
			f\x# = EntityX#(f\sprite)
			f\z# = EntityZ#(f\sprite)
		
			f\mushiness# = GLOBAL_MUSH#
		Else

			If (f\mushiness# =< 0.0)
				PointEntity f\sprite,NULLPIVOT
				dist# = EntityDistance(f\sprite , NULLPIVOT)
				MoveEntity f\sprite,0,0,(dist#/30.0)
			Else
				f\mushiness# = f\mushiness - 0.1
			EndIf

			f\x# = EntityX#(f\sprite)
			f\z# = EntityZ#(f\sprite)
	
		EndIf	
	Next
	
End Function
