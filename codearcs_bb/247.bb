; ID: 247
; Author: EdzUp[GD]
; Date: 2002-02-23 13:22:43
; Title: Gamma demo
; Description: Simple Gamma adjustment demo

;
;	Gamma.bb - Copyright ©2002 EdzUp
;	Coded by Ed Upton
;

Graphics3D 640,480,16,2
SetBuffer FrontBuffer()

AppTitle "Gamma test"

AmbientLight 255,255,255

Global GammaUp = CreateCube()
EntityColor GammaUp,255,255,255
ScaleEntity GammaUp,2,1,.001
Global GammaDown = CreateCube()
EntityColor GammaDown,0,0,0
ScaleEntity Gammadown,2,1,.001
EntityAlpha GammaUp,0
EntityAlpha GammaDown,0

Global Camera = CreateCamera()
Global Sphere = CreateSphere()

EntityColor sphere,255,0,0

Global CurrentGamma#=0.0

MoveEntity sphere,0,0,5

While Not KeyDown(1)
	PositionEntity GammaUp,EntityX#(camera),EntityY#(camera),EntityZ#(camera)
	PositionEntity GammaDown,EntityX#(camera),EntityY#(camera),EntityZ#(camera)
	MoveEntity GammaUp,0,0,1.1
	MoveEntity GammaDown,0,0,1.1
	If CurrentGamma#>0.0 Then EntityAlpha GammaUp,CurrentGamma#
	If CurrentGamma#<0.0 Then EntityAlpha GammaDown,Abs(CurrentGamma#)
	UpdateWorld
	RenderWorld
	If KeyDown(77)=1
		CurrentGamma = CurrentGamma + .05
		If CurrentGamma>1.0 Then CurrentGamma = 1.0
	EndIf
	If KeyDown(75)=1
		CurrentGamma = CurrentGamma - .05
		If CurrentGamma<-1.0 Then CurrentGamma = -1.0
	EndIf
	Flip
Wend
End
