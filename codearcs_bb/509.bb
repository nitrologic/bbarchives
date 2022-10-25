; ID: 509
; Author: EdzUp[GD]
; Date: 2002-11-25 02:49:07
; Title: Vertex Lighting
; Description: Vertex lighting give you directX like lighting without the 8 light restrictions (Thanks to Markus Rauch for assistance)

Graphics3D 640,480,16,2

Global Camera =CreateCamera()
PositionEntity Camera,0,0,-200

Global Cube=CreateCube()
ScaleMesh Cube,50,50,50
EntityFX Cube,2 ;VertexLight

Global Light=CreateSphere(8)

PositionEntity Light,0,40,-100 

While Not KeyHit(1)

	TurnEntity Cube,0.5,1,0.2

	SetVertexLightAmbience Cube,32,32,32

	VertexLightMesh( Cube, 1, EntityX(Light),EntityY(Light),EntityZ(Light),255,255,255,500,1 )

	UpdateWorld
	RenderWorld
	Flip
 
Wend
End

;####################################################################################################

;
;	EFPSVertexLighting.bb - Copyright ©2002 EdzUp
;	Coded by Ed Upton
;	Idea by HunterSD (thanks m8 =) )
;

;**********************************************************************
;NOTE: REMEMBER TO SET THE ENTITYFX OF THE MESH SO IT USES VERTEX LIGHT
;**********************************************************************

Type LightType							;this stores basic lighting info
	Field LightID						;integer lighting ID
	Field R#, G#, B#					;Light color
	Field Range#						;light range
	Field Class							;what sort of light it is
	Field X#, Y#, Z#					;position
	Field XR#, YR#, ZR#					;Light rotation
End Type

Type VertexLightType					;this is the light data for vertices
	Field MeshID						;what mesh it belongs to
	Field LightID						;what light illuminates the Mesh
	Field R#, G#, B#					;the amount this light illuminates this vertex
	Field SurfaceID						;Surface of Mesh the light relates to
	Field VertexID						;Vertex the light relates to
End Type

Global LPick=0
Global VertexLightAmbienceR# = 0		;makes sure light never goes below ambience
Global VertexLightAmbienceG# = 0
Global VertexLightAmbienceB# = 0

Function SetVertexLightAmbience( Mesh, R#, G#, B# )
	;this act light ambient lighting
	;this must be called with the AmbientLight command in you game
	For i=1 To CountSurfaces( Mesh )
		s = GetSurface( Mesh, i )
		For v=0 To CountVertices( s )-1
			VertexColor s, v, R#, G#, B#
		Next
	Next
	
	VertexLightAmbienceR# = R#
	VertexLightAmbienceG# = G#
	VertexLightAmbienceB# = B#
End Function

Function CreateVertexLight( LightID, R#, G#, B#, Range#, Class, X#, Y#, Z#, XR#, YR#, ZR# )
	;create a new light
	VLight.LightType = New LightType
	VLight\LightID = LightID
	VLight\R# = R#
	VLight\G# = G#
	VLight\B# = B#
	VLight\Range# = Range#
	VLight\Class = Class
	VLight\X# = X#
	VLight\Y# = Y#
	VLight\Z# = Z#
	VLight\XR# = XR#
	VLight\YR# = YR#
	VLight\ZR# = ZR#
End Function

Function AdjustVertexLight( LightID, MeshID, R#, G#, B#, Active = True, AmbienceLocked = True )
	;allows quick adjustment of lights
	For VertexLight.VertexLightType = Each VertexLightType
		If VertexLight<>Null
			If VertexLight\LightID = LightID
				If VertexLight\MeshID = MeshID
					s = GetSurface( MeshID, VertexLight\SurfaceID )	
					;remove light old color
					VertexColor s, VertexLight\VertexID, VertexRed#( s, VertexLight\VertexID )-VertexLight\R#, VertexGreen#( s, VertexLight\VertexID )-VertexLight\G#, VertexBlue#( s, VertexLight\VertexID )-VertexLight\B#
					
					If Active=True
						;the light is still on so update vertex with new color
						If AmbienceLocked=True
							If R#<VertexLightAmbienceR# Then R# = VertexLightAmbienceR#
							If G#<VertexLightAmbienceG# Then G# = VertexLightAmbienceG#
							If B#<VertexLightAmbienceB# Then B# = VertexLightAmbienceB#						
						EndIf
						VertexLight\R# = R#
						VertexLight\G# = G#
						VertexLight\B# = B#
						VertexColor s, VertexLight\VertexID, VertexRed#( s, VertexLight\VertexID )+VertexLight\R#, VertexGreen#( s, VertexLight\VertexID )+VertexLight\G#, VertexBlue#( s, VertexLight\VertexID )+VertexLight\B#
					EndIf
				EndIf
			EndIf
		EndIf
	Next
End Function

Function VertexLightMesh( Mesh, LightID, LightX#, LightY#, LightZ#, R#, G#, B#, Range#, AmbientLocked =1 )
	;this does the pre-rendered lighting for all lights on the level
	;Mesh is the model to add lighting to
	;LightX#, LightY#, LightZ# is the position of the light
	;R#, G#, B# is the rgb value of the light
	;Range# is the range of the light
	;AmbientLocked if set to 0 means you can have darklight and 1 means it never falls below ambient vertex light
	Local dist#
	Local DummyLight = CreateCube()
	Local DummyTarget = CreateCube()
	
	If R#<VertexLightAmbienceR# Then R# = VertexLightAmbienceR#
	If G#<VertexLightAmbienceG# Then G# = VertexLightAmbienceG#
	If B#<VertexLightAmbienceB# Then B# = VertexLightAmbienceB#

	Local RPer# = R# / 255
	Local GPer# = G# / 255
	Local BPer# = B# / 255

	RotateMesh Mesh, EntityPitch(Mesh,True),EntityYaw(Mesh,True),EntityRoll(Mesh,True)          ;<- must include in VertexLightMesh

	ScaleEntity DummyTarget, .01, .01, .01
	EntityPickMode DummyTarget, 2
	
	ScaleEntity DummyLight, .01, .01, .01
	PositionEntity DummyLight, LightX#, Lighty#, LightZ#

	For i=1 To CountSurfaces( Mesh )
		s = GetSurface( Mesh, i )
		For v=0 To CountVertices( s )-1
			dist# = Distance#( VertexX#( s, v ), VertexY#( s, v ), VertexZ#( s, v ), Lightx#, Lighty#, Lightz# )
			If dist# <= Range#
				PositionEntity DummyTarget, VertexX#( s, v ), VertexY#( s, v ), VertexZ#( s, v )
				PointEntity DummyLight, DummyTarget

				LPick = EntityPick( DummyLight, 10000 )

				If LPick = DummyTarget
					VertexLight.VertexLightType = New VertexLightType
					VertexLight\MeshID = Mesh
					VertexLight\LightID = LightID
					VertexLight\R# = R#-( RPer# * Dist# )
					VertexLight\G# = G#-( RPer# * Dist# )
					VertexLight\B# = B#-( RPer# * Dist# )
					VertexLight\SurfaceID = i
					VertexLight\VertexID = v
					
					VertexColor s, v, r#- (RPer# * Dist#), g# - (RPer# * Dist#), b#-(RPer * Dist#)
				EndIf
			EndIf
		Next
	Next
	
	UpdateNormals Mesh
	
	FreeEntity DummyLight
	FreeEntity DummyTarget

	RotateMesh Mesh, 0, -EntityYaw(Mesh,True), 0    ;<- must include in VertexLightMesh
	RotateMesh Mesh, -EntityPitch(Mesh,True), 0, 0  ;<- must include in VertexLightMesh
	RotateMesh Mesh, 0, 0, -EntityRoll(Mesh,True)   ;<- must include in VertexLightMesh
	
End Function

Function Distance#( x#, y#, z#, x2#, y2#, z2# )
	;distance function a very useful function indeed.

	value#=Sqr((x#-x2#)*(x#-x2#)+(y#-y2#)*(y#-y2#)+(z#-z2#)*(z#-z2#))
	
	Return value#
End Function
