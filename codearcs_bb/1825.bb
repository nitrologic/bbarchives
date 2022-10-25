; ID: 1825
; Author: kevin8084
; Date: 2006-09-27 21:54:45
; Title: Waypoints
; Description: Another Waypoint System

; **************************************************************************************************
; Simple waypoint system by Kevin Lee Legge (kevin8084@gmail.com)
; This program demonstrates a very simple 3d waypoint system using randomly placed waypoints
; In a game you can place the generated waypoints yourself, or leave them random
; **************************************************************************************************


Graphics3D 800,600,32,1
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Const MAX_POINTS = 25 ; set this to however many waypoints you want

; **************************************************************************************************
; TYPES
; **************************************************************************************************
Type waypoint
	Field entity
	Field number
End Type

Type player
	Field entity
	Field x#,y#,z#
	Field nextpoint
End Type

; Create the "player" - the one who will walk the waypoints
Global p.player=New player
p\entity=CreateSphere()
ScaleEntity p\entity,2,2,2
EntityColor p\entity,100,255,255
EntityShininess p\entity,1
; set the player's next waypoint at 1...or whatever number you like
p\nextpoint = 1 

; since we need to see what's going on, create a camera
Global camera = CreateCamera()
CameraZoom camera,1.6
PositionEntity camera,0,1,-200

; ...let there be light!
light=CreateLight()
PositionEntity light,0,90,0
RotateEntity light,90,0,0

; Create the waypoints
createWaypoints()

; move the mouse to the center of the screen before the main program runs
; this way we can be sure that we are not going to be facing some odd direction
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

While Not KeyHit(1)
If KeyDown(200) Then MoveEntity camera,0,0,.1  ; up arrow
If KeyDown(208) Then MoveEntity camera,0,0,-.1 ; down arrow
If KeyDown(203) Then TurnEntity camera,0,1,0   ; left arrow
If KeyDown(205) Then TurnEntity camera,0,-1,0  ; right arrow

mxs#=-MouseXSpeed()*.25
mys#=MouseYSpeed()*.25
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
TurnEntity camera,mys#,mxs#,0
RotateEntity camera,EntityPitch#(camera),EntityYaw#(camera),0

; alright...now walk that path
walkPath(p)

UpdateWorld
RenderWorld
Text 0,10,"Next Point:"+p\nextpoint
Flip
Delay 10 ; helps prevent laptops from overheating...lets the OS do its thing
Wend
killall() ; free up the memory used
End


Function createWaypoints()
; this function creates the waypoints and places them randomly
For w=1 To MAX_POINTS
	this.waypoint = New waypoint
	this\entity = CreateSphere()
	ScaleEntity this\entity,2,2,2
	EntityColor this\entity,Rand(255),Rand(255),Rand(255)
	EntityShininess this\entity,1
	this\number = w ; we want to know which waypoint this is
	PositionEntity this\entity,Rand(-75,75),Rand(-70,70),Rand(-75,75)
Next
End Function

Function walkPath(p.player)
; this function moves the player from waypoint to waypoint
For this.waypoint = Each waypoint
	If this\number = p\nextpoint Then
		; we have a match
		; you can use any routine here that will point p\entity towards this\entity
		PointEntity p\entity,this\entity
		MoveEntity p\entity,0,0,.7
		If EntityDistance(p\entity,this\entity)<3 Then
			; this determines how close we let p\entity get to the waypoint
			; p\entity is close enough (3 units) to its destination waypoint.
			; time for the next waypoint
			p\nextpoint = p\nextpoint+1
			; if we ran out of waypoints, then go back to the first one
			If p\nextpoint > MAX_POINTS Then p\nextpoint = 1
		End If
	End If
Next
End Function

Function killall()
; kill everything...overkill probably :)
FreeEntity p\entity
Delete p
For w.waypoint = Each waypoint
		FreeEntity w\entity
		Delete w
Next
FreeEntity camera
FreeEntity light
End Function
