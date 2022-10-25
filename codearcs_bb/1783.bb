; ID: 1783
; Author: MCP
; Date: 2006-08-11 15:22:24
; Title: Simple Wheel Physics
; Description: Using Circumference to calculate wheel rotation based on velocity

;*** wheel circumference calculation
;*** R Ferriby 2006

Graphics3D 800,600,32
SetBuffer BackBuffer()
light%=CreateLight() : PositionEntity light,50,50,-50
camera%=CreateCamera()

wheel_radius#=10.0   ;<-- enter your own values here

;*** create some suitable ground
plane%=CreatePlane()
EntityColor plane,0,255,0
EntityAlpha plane,0.5
PositionEntity plane,0,-wheel_radius,0
mirror%=CreateMirror()
PositionEntity mirror,0,-wheel_radius,0

;*** create a simple wheel object
wheel%=CreateCylinder(12) : ScaleMesh wheel,1,0.05,1 : RotateMesh wheel,0,0,90
ScaleEntity wheel,wheel_radius,wheel_radius,wheel_radius

wheel_diameter#=wheel_radius+wheel_radius

circumference#=wheel_diameter*Pi

axel%=CreatePivot() : EntityParent wheel,axel : PositionEntity axel,0,0,100,1
TurnEntity axel,0,-90,0

velocity#=0.0
While Not KeyHit(1)
	If KeyDown(203)
		If velocity>-1.0
			velocity=velocity-0.02
		EndIf
	EndIf
	If KeyDown(205)
		If velocity<1.0
			velocity=velocity+0.02
		EndIf
	EndIf
	If velocity<0.0
		velocity=velocity+0.01
		If velocity>0.0
			velocity=0.0
		Else
			If velocity<-1.0
				velocity=-1.0
			EndIf
		EndIf
	EndIf
	If velocity>0.0
		velocity=velocity-0.01
		If velocity<0.0
			velocity=0.0
		Else
			If velocity>1.0
				velocity=1.0
			EndIf
		EndIf
	EndIf
	;*** simply move the axel the specified distance
	MoveEntity axel,0,0,velocity
	
	;*** magic bit to calculate wheel rotation based on distance traveled
	rotation#=360.0*(velocity/circumference)
	TurnEntity wheel,rotation,0,0
	
	RenderWorld
	Text 0,0,"wheel radius="+wheel_radius
	Text 0,15,"wheel circumference="+circumference
	Text 0,30,"velocity="+velocity
	Text 0,50,"Left cursor to Roll left"
	Text 0,65,"Right cursor to Roll right"
	Text 0,80,"Esc - Quit"
	Flip
Wend
End
