; ID: 1645
; Author: Malice
; Date: 2006-03-17 10:14:32
; Title: Smoke Particle Engine
; Description: Simple placement of Smoke Emitters

;************************
;GALACTIC ALLEGIANCE v3.2
;************************

;******* FUNCTIONS *****

;Coded By PJ Chowdhury 2006
;For the Galactic Allegiance Open-Source Project

;These Functions Released as Full Public Domain


;The functions are used to create a particle emitter which will
;Then continue To emit smoke-like particles of the chosen specifications

;************************************************************************************************
;                                         EXAMPLE
;************************************************************************************************


Graphics3D 800,600,32,2
SetBuffer BackBuffer()

cam=CreateCamera()
CameraViewport cam,0,0,800,600
CameraRange cam,1,100

MoveEntity cam,0,0,-10

tex=CreateTexture(128,128,3)

SetBuffer TextureBuffer(tex)

	
	
	ClsColor 0,0,0
	Cls
		Color 255,255,255
		Oval 64,64,64,64,1
SetBuffer BackBuffer()

Global SmokeSprite=CreateSprite() ;*YOU WILL NEED A GLOBAL SPRITE WITH THE HANDLE SmokeSprite FOR THE FUNCTIONS TO WORK
EntityFX SmokeSprite,1

EntityTexture SmokeSprite,tex

EntityAlpha SmokeSprite,0		
		
AmbientLight 180,180,180
sun=CreateLight(1)
PositionEntity sun,40,20,60

PointEntity sun,cam

cube=CreateCube()
EntityColor cube,180,0,0
MoveEntity cube,-5,0,2
EntityPickMode cube,2

sphere=CreateSphere(20)
EntityColor sphere,0,180,0
MoveEntity sphere,5,0,2
EntityPickMode sphere,2

cylinder=CreateCylinder(20,1)
MoveEntity cylinder,0,4,10
RotateMesh cylinder,90,0,45
EntityColor cylinder,0,0,180
EntityPickMode cylinder,2

piv=CreatePivot()

pyra=CreateCone(4,1)
EntityColor pyra,180,180,0
MoveEntity pyra,0,-2,5
EntityPickMode pyra,2

EntityParent pyra,piv

While Not KeyDown(1)
	TurnEntity piv,0,0.5,0
	TurnEntity cylinder,1,5,0
	If MouseHit(1)
		msh=CameraPick(cam,MouseX(),MouseY())
		
		If msh>0 
			p=msh
			s#=Rnd#(0.001,0.5)
			d=Rand(1,5)
			c=Rand(64,192)
			f#=Rnd#(0.01,0.1)
			
			Emit_Smoke(p,s#,d,c,c,c,f#,Rand(0,20))
		EndIf
		
	EndIf

Update_Smoke()




	
UpdateWorld

RenderWorld

Text 0,0,"CLICK ON A PRIMITIVE MESH TO APPLY A RANDOMISED SMOKE EMITTER"
Text 0,100,"LAST EMITTER: "+msh

Flip

Wend




;*************************************************************************************************
;                                   FUNCTIONS AND TYPES                                           
;*************************************************************************************************



Type Smoke_Particles
	Field Sprite
	Field Alpha#
	Field Fade#
End Type

Type Smoke_Emitter
	Field Pivot
	Field SP_Red
	Field SP_Green
	Field SP_Blue
	Field SP_Dens
	Field SP_Scale#
	Field SP_Fade#
	Field SP_Var
End Type





;The main user-defining function is Emit_Smoke,
;Call this Function Whenever you wish To Add an emitter To your world

;Parent_Entity is the entity to which you wish to attach the emitter
;Sm_Scale# is the size of both the particles and the spread of the smoke effect
;Sm_Density changes the number of particles created per frame. Integers only
;Smoke_Red, Green and Blue determine the colour of smoke emitted by that emitter.
;Sm_fade# is the rate at which the particles fade out (Floats between 0.005 and 0.1 are best)		
;Sm_Colour_Var is an amount (0 to 50 is a good range) to vary the colour of the smoke by

Function Emit_Smoke(Parent_Entity,Sm_Scale#,Sm_Density,Smoke_Red,Smoke_Green,Smoke_Blue,Sm_Fade#,Sm_Colour_Var)

	Sm_emit.Smoke_Emitter= New Smoke_Emitter
		Sm_emit\pivot=CreatePivot()	;This pivot is in effect The particle emitter

			PositionEntity Sm_emit\pivot,EntityX#(Parent_Entity,1),EntityY#(Parent_Entity,1),EntityZ#(Parent_Entity,1),1
			EntityParent Sm_emit\pivot,Parent_Entity ;Attach pivot to Parent Entity


			Sm_emit\SP_Dens=Sm_Density			;These lines feed
			Sm_emit\SP_Red=Smoke_Red			;the relevant data
			Sm_emit\SP_Green=Smoke_Green		;to the emitter
			Sm_emit\SP_Blue=Smoke_Blue			;
			Sm_emit\SP_Scale#=Sm_Scale#			;ensuring a record
			Sm_emit\SP_Fade#=Sm_Fade#			;is kept of the 
			Sm_emit\SP_Var=Sm_Colour_Var 		;particle criteria

End Function



Function Update_smoke() 	;This function should be called each loop.
	
	For upd_Smoke.Smoke_Emitter=Each Smoke_Emitter

		For f=1 To upd_Smoke\SP_Dens		;sets a small loop to generate more particles for higher densities

			SmPart.Smoke_Particles = New Smoke_Particles	;Generates New Smoke Particles
				SmPart\Sprite=CopyEntity (SmokeSprite)	;Copies the generic Smoke Sprite
				SmPart\Alpha#=1						;Sets the particles to be initially visible
				SmPart\Fade#=upd_Smoke\SP_Fade#			;Gives this Particle a fade index

				part_ScaleSmall#=((upd_smoke\SP_Scale#)/10)						;
				scl#=Rnd#(part_Scale#,upd_smoke\SP_Scale#)						;										;
				ScaleSprite SmPart\Sprite,scl#,scl#								;Scales the particle
	
				EntityColor SmPart\Sprite,upd_smoke\SP_Red+(Rand(0-upd_smoke\SP_var,upd_smoke\SP_var)),upd_smoke\SP_Blue+(Rand(0-upd_smoke\SP_var,upd_smoke\SP_var)),upd_smoke\SP_Green+(Rand(0-upd_smoke\SP_var,upd_smoke\SP_var))
				;Colours the particle
				PositionEntity SmPart\Sprite,EntityX#(upd_Smoke\Pivot,1),EntityY#(upd_Smoke\Pivot,1),EntityZ#(upd_Smoke\Pivot,1),1
				
				part_ScaleSmall#=Part_ScaleSmall#*5
	
				MoveEntity SmPart\Sprite,Rnd#(0-Part_ScaleSmall#,Part_ScaleSmall#),Rnd#(0-Part_ScaleSmall#,Part_ScaleSmall#),Rnd#(0-Part_ScaleSmall#,Part_ScaleSmall#)
				;positions the particle
			
			
		Next
		
	Next
	
	
	For Upd_Parts.Smoke_Particles = Each Smoke_Particles ;This should also be called each loop

				EntityAlpha Upd_Parts\Sprite,Upd_Parts\Alpha#			;	
				Upd_Parts\Alpha#=Upd_Parts\Alpha# - Upd_Parts\Fade#		; Fades the particle
				
				TranslateEntity Upd_Parts\Sprite,0,0.1,0				;Makes the particles rise
				

				
					If Upd_Parts\Alpha#<=0 Then Delete Upd_Parts		;Destroy fully-faded articles
								
	Next
	
End Function
