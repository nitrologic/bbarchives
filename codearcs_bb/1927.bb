; ID: 1927
; Author: Stevie G
; Date: 2007-02-15 03:54:44
; Title: DeltaRoll
; Description: As below

Graphics3D 640,480,16,1

Global Camera = CreateCamera() : PositionEntity Camera, 0, 0, -30
Global Ship = CreateCone(): ScaleMesh Ship, 1, 2, 1 : EntityColor Ship, 0,0,255
Global Target = CreateCube() : EntityColor Target,255,0,0

Repeat

	If KeyHit( 57 )
		PositionEntity target, Rand(-20,20 ), Rand(-20,20 ), 10
	EndIf
	
	DR# = DELTAroll( Ship, Target )	
	TurnEntity ship, 0, 0, DR * .01
	
	RenderWorld()

	Text 0,0,DR
	
	Flip

Until KeyDown(1)

;=================================================================================
;=================================================================================
;=================================================================================

Function DELTAroll#( Source , Target )

	TFormPoint 0,0,0 , Target, Source
	Return VectorYaw ( TFormedX() , 0 , TFormedY() )

End Function
