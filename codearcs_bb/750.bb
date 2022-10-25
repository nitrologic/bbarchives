; ID: 750
; Author: Ken Lynch
; Date: 2003-07-22 05:39:42
; Title: Using LinePick to do sphere-to-sphere collisions
; Description: Using LinePick to do sphere-to-sphere collisions

;
; LinePick sphere-to-sphere collision
;

Graphics3D 800, 600

light = CreateLight()

pivot = CreatePivot()

camera = CreateCamera(pivot)
PositionEntity camera, 0, 10, 0
RotateEntity camera, 90, 0, 0

;
; Player
;
player = CreateSphere()
player_speed# = 0.1
PositionEntity player, 0, 0, 5
EntityPickMode player, 1

;
; Sphere
;
sphere = CreateSphere()
velocity# = 2
radius# = 1
sph = CreateCylinder()
EntityParent sph, sphere
ScaleEntity sph, 0.1, 1, 0.1
EntityColor sph, 255, 0, 0
PositionEntity sph, 0, 0, 1
AlignToVector sph, 0, 0, 1, 2

;
; Sphere to show where collision occurs
;
collision = CreateSphere()
PositionEntity collision, 0, 0, 2
EntityColor collision, 0, 0, 255
EntityAlpha collision, 0.5
EntityFX collision, 1

;
; Cylinder to show normal vector
;
normal = CreateCylinder()
ScaleEntity normal, 0.1, 1, 0.1
EntityColor normal, 255, 0, 0
HideEntity normal

Repeat
	;
	; Up key
	;
	If KeyDown(200) Then MoveEntity player, 0, 0, player_speed

	;
	; Down key
	;
	If KeyDown(208) Then MoveEntity player, 0, 0, -player_speed

	;
	; Left key
	;
	If KeyDown(203) Then MoveEntity player, -player_speed, 0, 0

	;
	; Right key
	;
	If KeyDown(205) Then MoveEntity player, player_speed, 0, 0
	
	;
	; Rotate sphere
	;
	If KeyDown(44) Then TurnEntity sphere, 0, 1, 0
	If KeyDown(45) Then TurnEntity sphere, 0, -1, 0
	
	;
	; Increase/decrease velocity
	;
	If KeyHit(12) Then velocity = velocity - 0.1
	If KeyHit(13) Then velocity = velocity + 0.1
	
	;
	; Increase/decrease radius
	;
	If KeyHit(26) Then radius = radius - 0.1
	If KeyHit(27) Then radius = radius + 0.1
	ScaleEntity sphere, radius, radius, radius
	
	;
	; Transform velocity to global coordinates
	;
	TFormVector 0, 0, velocity, sphere, 0
	
	;
	; Do the line pick
	;
	If LinePick(0, 0, 0, TFormedX(), TFormedY(), TFormedZ(), radius) > 0 Then
		;
		; Show normal vector
		;
		ShowEntity normal
		;
		; Move it to the picked position
		;
		PositionEntity normal, PickedX(), PickedY(), PickedZ()
		;
		; Align it to the picked vector
		;
		AlignToVector normal, PickedNX(), PickedNY(), PickedNZ(), 2
		;
		; Show collision sphere
		;
		ShowEntity collision
		;
		; Move collision sphere
		;
		PositionEntity collision, PickedX() + radius * PickedNX(), PickedY() + radius * PickedNY(), PickedZ() + radius * PickedNZ()
		;
		; Scale to radius
		;
		ScaleEntity collision, radius, radius, radius
	Else
		;
		; Nothing was picked
		;
		HideEntity normal
		HideEntity collision
	End If

	RenderWorld
	
	Locate 0, 0
	Print "Velocity = "+velocity
	Print "Radius   = "+radius
	Print
	Print "Use cursor keys to move player"
	Print "Use z/x to rotate sphere left/right
	Print "Use -/= to decrease/increase velocity"
	Print "Use [/] to decrease/increase radius"
	
	Flip
Until KeyHit(1)
