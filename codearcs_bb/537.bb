; ID: 537
; Author: EdzUp[GD]
; Date: 2003-01-05 08:33:14
; Title: Thief style Luminance
; Description: see how visible a player via light sources in game

;
;	Luminance.bb - Copyright ©2002 EdzUp
;	Coded by Ed Upton
;

Global AmbientLightR#, AmbientLightG#, AmbientLightB#
Global LastLightR#, LastLightG#, LastLightB#

Function SetAmbientLight( R#, G#, B# )
	;set ambience to ambient level and store value
	AmbientLight R#, G#, B#
	
	AmbientLightR# = R#
	AmbientLightB# = B#
	AmbientLightG# = G#
End Function

Function ShowLuminance#( Luminance# )
	;this puts a brick on screen with the current luminance value
	Local R# = ( LastLightR# /100 )* (100-Luminance#)
	Local G# = ( LastLightG# /100 )* (100-Luminance#)
	Local B# = ( LastLightB# /100 )* (100-Luminance#)

	If R#<AmbientLightR# Then R# = AmbientLightR#
	If G#<AmbientLightG# Then G# = AmbientLightG#
	If B#<AmbientLightB# Then B# = AmbientLightB#
	Color R#, G#, B#

	Rect GraphicsWidth()-40, 60, 30, 20
End Function

Function EntityInSight( FromEntity, ToEntity, R#, G#, B#, Range# )
	;FromEntity: this is the light source to check from
	;ToEntity: this is the entity to check luminance of
	Local Luminance#=0.0
	Local NewLuminance#=0.0
	Local CheckCube = CreateCube()
	Local SourceCube = CreateCube()
	
	ScaleEntity SourceCube, .1, .1, .1
	ScaleEntity CheckCube, .1, .1, .1
	PositionEntity CheckCube, EntityX#( ToEntity ), EntityY#( ToEntity ), EntityZ#( ToEntity )
	EntityPickMode CheckCube, 2
	
	LastLightR# = 0.0
	LastLightG# = 0.0
	LastLightB# = 0.0
	
	PositionEntity SourceCube, EntityX#( FromEntity ), EntityY#( FromEntity ), EntityZ#( FromEntity )
	PointEntity SourceCube, CheckCube
									
	If EntityPick( SourceCube, 500.0 )=CheckCube	;if in view
		NewLuminance# = CalculateLuminance#( ToEntity, FromEntity, Range#*2 )
		If NewLuminance#>Luminance#
			Luminance# = NewLuminance#
			LastLightR# = R#
			LastLightB# = G#
			LastLightG# = B#
		EndIf
	EndIf
	
	FreeEntity CheckCube
	FreeEntity SourceCube
	
	Return Luminance#
End Function

Function CalculateLuminance#( Target, Source, Range# )
	Local Dist# = EntityDistance#( Target, Source )

	If Dist#<Range#
		Return Dist# / ( Range#/100 )
	Else
		Return -1
	EndIf
	
	Return -1
End Function
