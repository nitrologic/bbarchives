; ID: 3014
; Author: Kryzon
; Date: 2013-01-06 13:43:22
; Title: Dolly-Zoom effect
; Description: Cinematographic camera zoom effect, also known as Push-Pull or Vertigo zoom.

;**************************************************
;
;  3 Recipes for the Dolly-Zoom effect
;  (also known as Push-Pull or Vertigo zoom).
;
;  Demo and Distance-Zoom method by Floyd.
;  Adaptation, FOV and Z-Scale methods by Kryzon.
;
;  ----------------------------------------------
;  Press SPACE to change the zoom method.
;  Hold A or Z to move the camera.
; 
;  Jan 6th 2013
;
;**************************************************

;Zoom methods:
;1 - Distance-Zoom
;2 - FOV
;3 - Z-Scale (messes up the lighting, here only for "academic" purposes.)

Local dollyMethod% = 1

AppTitle "Dolly-Zoom effect demo"
Graphics3D 800, 600, 0, 2
SetBuffer BackBuffer()
HidePointer

Local sphere% = CreateSphere()
Local camera% = CreateCamera() : PositionEntity camera, 0, 5, -5 
CameraRange camera, 0.5, 1000 : PointEntity camera, sphere
TurnEntity CreateLight(), 30, 70, 0
VariousOtherThings 30

;Setup for Distance-Zoom method:
Local zoom_over_distance# = 1.0 / EntityDistance( camera, sphere )   ; CameraZoom is 1.0

;Setup for FOV method:
Local subjectDistance# = EntityDistance( camera, sphere )
Local originalFOV# = 90.0 ;In Blitz3D, the equivalent of a CameraZoom of 1.0
Local originalTan# = Tan(originalFOV / 2.0)
Local currentFOV# = 90.0
Local distanceRatio# = 1.0

;Setup for Z-Scale method:
subjectDistance# = EntityDistance( camera, sphere)
Local cameraScale# = 1.0
distanceRatio# = 1.0

;Main Loop.
While Not KeyDown(1)
	
	;Move the camera.
	z# = EntityZ( camera )
	If KeyDown(44) Then z = z * 1.01
	If KeyDown(30) Then z = z / 1.01
	PositionEntity camera, 0, -z, z
	
	;Select a zoom method.
	If KeyHit(57) Then 
		dollyMethod = dollyMethod + 1
		If dollyMethod > 3 Then dollyMethod = 1
		
		If dollyMethod <> 3 Then 
			ScaleEntity camera, 1.0, 1.0, 1.0
		Else
			CameraZoom camera, 1.0
		EndIf 
	EndIf 
		
	Select dollyMethod
		Case 1 ;Distance-Zoom
			CameraZoom camera, zoom_over_distance * EntityDistance( camera, sphere )
			PointEntity camera, sphere
			
			RenderWorld
			Text 10, 10, "Method: Distance-Zoom"
			Text 10, 30, "Distance = " + EntityDistance( camera, sphere )
			Text 10, 50, "CameraZoom = " + zoom_over_distance * EntityDistance( camera, sphere )
		
		Case 2 ;FOV
			distanceRatio# = subjectDistance / EntityDistance( camera, sphere )
			
			;The FOV (field-of-view) is an angle value that several engines use for setting up a camera's zoom.
			currentFOV = 2.0 * ATan(distanceRatio * originalTan)
		
			;You apply that FOV angle in whatever way your engine does it (matrix, function, etc.). In case of Blitz3D...
			CameraZoom camera, ( 1.0 / Tan(currentFOV/2.0) )
			
			PointEntity camera, sphere
			
			RenderWorld
			Text 10, 10, "Method: FOV"
			Text 10, 30, "Distance = " + EntityDistance( camera, sphere )
			Text 10, 50, "CameraZoom = " + (1.0 / Tan(currentFOV/2.0 )) + " ("+currentFOV+" degrees)"
		
		Case 3 ;Z-Scale
			distanceRatio# = subjectDistance / EntityDistance( camera, sphere )
			ScaleEntity( camera, 1.0, 1.0, (1.0 / distanceRatio) )

			RenderWorld
			Text 10, 10, "Method: Z-Scale"
			Text 10, 30, "Distance = " + EntityDistance( camera, sphere )
			Text 10, 50, "CameraZoom = " + ( 1.0 / distanceRatio )	
	End Select
	
	Flip
Wend

;Aesthetics.
Function VariousOtherThings( quantity )
	For n = 1 To quantity
		Select Rand( 1, 4 )
			Case 1 : temp = CreateSphere()
			Case 2 : temp = CreateCone()
			Case 3 : temp = CreateCylinder()
			Case 4 : temp = CreateCube()
		End Select
		ScaleEntity temp, Rnd( 0.6, 1.5 ), Rnd( 0.6, 1.5 ), Rnd( 0.6, 1.5 )
		EntityColor temp, Rand( 100, 255 ), Rand( 100, 255 ), Rand( 100, 255 )
		RotateEntity temp, Rnd( -20, 20 ), Rnd( -50, 50 ), Rnd( -20, 20 )
		angle# = Rnd( -45, 225)
		dist#  = Rnd( 2.5, 6 )
		PositionEntity temp, dist * Cos( angle ), Rnd( - 3, 3 ), dist * Sin( angle )
	Next
End Function

End
