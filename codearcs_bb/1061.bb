; ID: 1061
; Author: Neochrome
; Date: 2004-05-30 18:24:48
; Title: Pushing Objects
; Description: Real Collision Pushing

(I let this code free)

[codebox]
If lpEnvpushable=True
	;translateentity lpEnvmodel,0,-.1,0		
	z = EntityCollided(ThisActor, coll_objects) 
	If z = lpEnvmodel
		i=1
		; Calculate bounce:
		; Get the normal of the surface which the entity collided with.
		Nx# = CollisionNX(ThisActor%, i)
		Ny# = CollisionNY(ThisActor%, i)
		Nz# = CollisionNZ(ThisActor%, i)
					
		vy# = VectorYaw(-nx, -ny, -nz)

		tfm_z# = Cos(vy#)/5
		tfm_x# = -Sin(vy#)/5
		tfm_y# = VectorPitch(-nx, -ny, -nz)
		If Abs(tfm_y)<60 Then TranslateEntity lpEnvmodel,tfm_x,0,tfm_z
	End If
End If
[/codebox]
