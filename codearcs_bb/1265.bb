; ID: 1265
; Author: daaan
; Date: 2005-01-22 19:12:42
; Title: Easy Shadows (flat surfaces only)
; Description: a must for noobs!

; lazy mans shadows ;
;      by tank      ;
Graphics3D 640,480,32,2
SetBuffer BackBuffer()

campiv=CreatePivot()
camera=CreateCamera(campiv)
MoveEntity camera,0,5,-10
TurnEntity camera,25,0,0

cone=CreateCone()
MoveEntity cone,0,5,0

shadow=CreateCone()
EntityFX shadow,1
EntityColor shadow,10,10,10
ScaleEntity shadow,1,0.001,1
MoveEntity shadow,0,0.11,0
EntityAlpha shadow,0.5

platform=CreateCube()
ScaleEntity platform,5,0.1,5
EntityColor platform,100,100,100

light=CreateLight()
MoveEntity light,0,20,0
TurnEntity light,90,90,0

scale#=1.0

; M A I N  L O O P ;
While Not KeyHit(1)
		
	TurnEntity campiv,0,0.1,0
		
	RotateMesh cone,1,1,0
	RotateMesh shadow,1,1,0
	
	If KeyDown(200) Then scale# = scale# + 0.01
	If KeyDown(208) Then scale# = scale# - 0.01
	ScaleEntity cone, scale#, scale#, scale#
	ScaleEntity shadow, scale#+(scale#*0.4), 0.001, scale#+(scale#*0.4)
		
	UpdateWorld
	RenderWorld
	
	Text 10,10,"Use the UP ARROW and DOWN ARROW keys to change the scale of the cone."
	
	Flip
	
Wend
