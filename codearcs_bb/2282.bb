; ID: 2282
; Author: Nate the Great
; Date: 2008-07-02 20:26:38
; Title: Avoid_entity function
; Description: cone avoids red cube

; The number of logic updates per second.
Const C_LOGIC_FREQUENCY = 50

; The interval (in milliseconds) between logic updates.
Const C_LOGIC_INTERVAL# = 1000.0 / C_LOGIC_FREQUENCY ; Unused.

Const C_SMART_MISSILE_TEST_DISTANCE# = 10.0
Const C_SMART_MISSILE_TEST_ANGLE# = 60.0
Const C_SMART_MISSILE_NO_TURN_DISTANCE# = 15.0

Global timer = CreateTimer( C_LOGIC_FREQUENCY )

Graphics3D 800, 600, 0, 2
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Global cam = CreateCamera()
CameraZoom cam, 1.6
MoveEntity cam, 0.0, 60.0, 0.0
TurnEntity cam, 90.0, 0.0, 0.0

Global light = CreateLight()

Global cone = CreateCone()
RotateMesh cone, 90.0, 0.0, 0.0
UpdateNormals cone
PositionEntity cone, 0.0, 0.0, -20.0

Global cube = CreateCube()
Global bcube = CreateCube()
EntityColor bcube,255,0,0
UpdateNormals cube
;PositionEntity cube, 40.0, 0.0, 20.0
PositionEntity cube, Rnd( -5.0, 5.0 ), 0.0, Rnd( -5.0, 5.0 )
Repeat
	PositionEntity bcube, Rnd( -15.0, 15.0 ), 0.0, Rnd( -15.0, 15.0 )
Until(EntityDistance(cube,bcube) > 8)


Global dist#

While Not KeyHit( 1 )
	
	dist# = EntityDistance( cone, cube )
	
	If allow_turn
		If dist# < C_SMART_MISSILE_TEST_DISTANCE#
			If Abs( DeltaYaw( cone, cube ) ) > C_SMART_MISSILE_TEST_ANGLE#
				allow_turn = False
			EndIf
		EndIf
	Else
		If dist# > C_SMART_MISSILE_NO_TURN_DISTANCE# Then allow_turn = True
	EndIf
	
	If Not avoid_entity( cone, bcube, 3.0, 0.5 ) Then
		If allow_turn Then YawToEntity cone, cube, 3.0
	EndIf
	
	MoveEntity cone, 0.0, 0.0, 0.5
	
	If dist# < 2.0
		PositionEntity cube, Rnd( -15.0, 15.0 ), 0.0, Rnd( -15.0, 15.0 )
		Repeat
			PositionEntity bcube, Rnd( -15.0, 15.0 ), 0.0, Rnd( -15.0, 15.0 )
		Until(EntityDistance(cube,bcube) > 8)
	EndIf
	
	UpdateWorld
	RenderWorld
	Flip
	
	WaitTimer( timer )
	
Wend

End


Function YawToEntity( src_entity, dest_entity, rate# )
; Turns 'src_entity' to point at 'dest_entity' at the rotation rate specified by 'rate#'.

	Local target_yaw# = DeltaYaw( src_entity, dest_entity )
	
	; If the required correction amount is less than the correction amount to be applied...
	If Abs( target_yaw# ) < rate#
		; Point 'src_entity' directly at 'dest_entity' to prevent jittering.
		TurnEntity src_entity, 0.0, target_yaw#, 0.0
	Else
		; Turn 'src_entity' gradually towards 'dest_entity'.
		TurnEntity src_entity, 0.0, rate# * Sgn( target_yaw# ), 0.0
	EndIf
End Function


Function avoid_entity( scr_entity, dest_entity, rate#, spd# )

	If EntityDistance(scr_entity,dest_entity) < 8 Then
		MoveEntity scr_entity,0,0,spd#
		dist1# = EntityDistance(scr_entity,dest_entity)
		MoveEntity scr_entity,0,0,-spd#
		TurnEntity scr_entity,0,rate#,0
		MoveEntity scr_entity,0,0,spd#
		dist2# = EntityDistance(scr_entity,dest_entity)
		MoveEntity scr_entity,0,0,-spd#
		TurnEntity scr_entity,0,-2*rate#,0
		MoveEntity scr_entity,0,0,spd#
		dist3# = EntityDistance(scr_entity,dest_entity)
		MoveEntity scr_entity,0,0,-spd#
		
		If dist1# > dist2# And dist1# > dist3# Then TurnEntity scr_entity,0,rate#,0
		If dist2# > dist3# And dist2# > dist1# Then TurnEntity scr_entity,0,2*rate#,0
		
		Return True
	Else
		Return False
	EndIf

End Function
