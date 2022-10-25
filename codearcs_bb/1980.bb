; ID: 1980
; Author: Mattizzle
; Date: 2007-04-03 21:56:08
; Title: 3rd-Person Camera
; Description: Rotates around a pivot

Function CurveValue#(newvalue#,oldvalue#,increments )
	If increments>1 oldvalue#=oldvalue#-(oldvalue#-newvalue#)/increments
	If increments<=1 oldvalue=newvalue
	Return oldvalue#
End Function

DebugLog "Initiating..."

;[Block] Graphics
Graphics3D 800,600,0,2
SetBuffer BackBuffer()
HidePointer()

DebugLog "Graphics Set"
;[End Block]

;[Block] Light
Global light=CreateLight()
RotateEntity light,25,0,0
AmbientLight 110,110,110

DebugLog "Lights Created"
;[End Block]

;[Block] Create Player
Global player, pz, px
player = CreateCube()

RotateEntity player, 0,180,0

tex = CreateTexture(16,16)
SetBuffer TextureBuffer(tex)
ClsColor 255,255,255
Cls
font = LoadFont("ariel",24)
SetFont font
Color 255,0,0
Text 4,0,"^"

EntityTexture player,tex
SetBuffer BackBuffer()

DebugLog "Player Created"
;[End Block]

;[Block] Camera
Global camPivot = CreatePivot()
PositionEntity camPivot, EntityX(player),EntityY(player),EntityZ(player)
RotateEntity camPivot, 0,180,0

Global cam = CreateCamera()
CameraRange cam,1,100000
CameraZoom cam,1.4
PositionEntity cam, EntityX(player),EntityY(player)+5,EntityZ(player)-20
EntityParent cam,camPivot

Global mousespeedx# = 0.4
Global mousespeedy# = 0.2
Global playerspeed# = 0.8
Global camerasmoothness# = 3
Global mx#, my#, pitch#, yaw#, roll#

DebugLog "Camera Has Loaded"
;[End Block]

;[Block] Random Cubes
For a=1 To 50
	cube = CreateCube()
	PositionEntity cube, Rnd(200)-100,Rnd(200)-100,Rnd(200)-100
	RotateEntity cube, Rnd(360),Rnd(360),Rnd(360)
	ScaleEntity cube, Rnd(5)+0.5,Rnd(5)+0.5,Rnd(5)+0.5
	EntityColor cube, Rnd(255),Rnd(255),Rnd(255)
Next	
;[End Block]



DebugLog "MainLoop Initiated"
;[Block] MAIN LOOP
While Not KeyHit(1)
	
	;[Block] Camera Controls
	mx#=CurveValue(MouseXSpeed()*mousespeedx,mx,camerasmoothness)
	my#=CurveValue(MouseYSpeed()*mousespeedy,my,camerasmoothness)
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	pitch#=EntityPitch(camPivot)
	yaw#=EntityYaw(camPivot)
	pitch=pitch+my
	yaw=yaw-mx
	If pitch > 9  pitch = 9
	If pitch < -49 pitch = -49

	RotateEntity camPivot, 0,yaw,0
	TurnEntity camPivot, pitch,0,0
	;[End Block]
	
	
	;[Block] Player Controls
	px=(KeyDown(32)-KeyDown(30))
	pz=(KeyDown(17)-KeyDown(31))

	If pz = 1 And px = 0 RotateEntity(player, 0,EntityYaw(camPivot)+180,0)
	If pz =-1 And px = 0 RotateEntity(player, 0,EntityYaw(camPivot),0)
	If px = 1 And pz = 0 RotateEntity(player, 0,EntityYaw(camPivot)+90,0)
	If px =-1 And pz = 0 RotateEntity(player, 0,EntityYaw(camPivot)-90,0)
		
	If pz = 1 And px = 1 RotateEntity(player, 0,EntityYaw(camPivot)+135,0)
	If pz = 1 And px =-1 RotateEntity(player, 0,EntityYaw(camPivot)-135,0)
	If pz =-1 And px = 1 RotateEntity(player, 0,EntityYaw(camPivot)+45,0)
	If pz =-1 And px =-1 RotateEntity(player, 0,EntityYaw(camPivot)-45,0)

	If pz <> 0 Or px <> 0 
		MoveEntity player, 0,0,playerspeed
	EndIf
	;[End Block]
	
	PositionEntity camPivot,EntityX(player),EntityY(player),EntityZ(player)

	RenderWorld()
	Flip
Wend
;[End Block]

ClearWorld()

DebugLog "Memory Freed. Exititing..."



End
