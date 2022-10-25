; ID: 144
; Author: Neo Genesis10
; Date: 2001-11-19 03:44:05
; Title: CreateExplosion()
; Description: Two simple routines to make simplistic explosions.

Type explosion
	Field max#
	Field mesh
	Field opa#
End Type

Global boom.explosion
Global exptex = LoadTexture("boom.jpg", 8)

Function CreateExplosion( x#, y#, z#, size, polys )
	
	If polys =< 0 RuntimeError "Negative poly count in CreateExplosion"
	If polys > 100 RuntimeError "Poly count too high for CreateExplosion"
	boom.explosion = New explosion
	boom\mesh = CreateSphere( polys )

	Select size
		Case 1
			ScaleMesh boom\mesh,.3,.3,.3
			boom\max = 1.9
			w = MeshWidth(boom\mesh)
		Case 2
			ScaleMesh boom\mesh,.7,.7,.7
			boom\max = 4.0
			w = MeshWidth(boom\mesh)
		Default
			ScaleMesh boom\mesh,.9,.9,.9
			boom\max = 5.5
			w = MeshWidth(boom\mesh)
	End Select
	EntityAlpha boom\mesh, .7
	EntityTexture boom\mesh, exptex
	boom\opa# = 0.9
	PositionEntity boom\mesh, x#, y#, z#
	
End Function

Function UpdateExplosions()

	For boom.explosion = Each explosion
		ScaleMesh boom\mesh, 1.01, 1.01, 1.01
		boom\opa# = boom\opa# - 0.01
		EntityAlpha boom\mesh, boom\opa#
		TurnEntity boom\mesh, 0,1,0
		
		If MeshWidth(boom\mesh) > boom\max
			FreeEntity boom\mesh
			Delete boom.explosion
		EndIf
	Next

End Function
