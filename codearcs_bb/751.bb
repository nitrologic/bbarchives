; ID: 751
; Author: Ken Lynch
; Date: 2003-07-22 14:15:28
; Title: Simple 3D sphere-to-sphere physics
; Description: Simple 3D sphere-to-sphere physics

;
; Simple 3D collision with physics
;

;
; Data types
;

Type PhysicsEntity
	Field entity%
	Field radius#, mass#, bounce#, friction#
	Field x#, y#, z#
	Field xv#, yv#, zv#
End Type

;
; The usual stuff
;

Graphics3D 800, 600

light = CreateLight()

camera = CreateCamera()
PositionEntity camera, 0, 0, -25

;
; Create our entities
;
For i = -8 To 8 Step 2
	For j = -8 To 8 Step 2
		ent = CreateSphere()
		ScaleEntity ent, 0.5, 0.5, 0.5
		PositionEntity ent, i, j, 0
		SetEntityPhysics ent, 0.5, 1, 0.90, 0.0005
		ApplyForce ent, Rnd(-0.2, 0.2), Rnd(-0.2, 0.2), 0
	Next
Next

;
; Main loop
;
Repeat
	UpdatePhysics
	RenderWorld
	Flip
Until KeyHit(1)

;
; Physics handling functions
;

;
; FindPhysicsEntity(entity)
;
; Quickly find PhysicsEntity data type from entity using the Handle stored in EntityName
;
Function FindPhysicsEntity.PhysicsEntity(entity)
	name = EntityName(entity)
	Return Object.PhysicsEntity(name)
End Function

;
; SetEntityPhysics entity,radius#,mass#
;
; Sets an entity's phisics properties
;
Function SetEntityPhysics(entity, radius#=1, mass#=1, bounce#=1, friction#=0)
	pe.PhysicsEntity = New PhysicsEntity
	pe\entity = entity
	pe\radius = radius
	pe\mass = mass
	pe\bounce = bounce
	pe\friction = friction
	EntityRadius entity, radius
	EntityPickMode entity, 1
	;
	; Magic to store the Handle of pe in the entity's name
	;
	NameEntity entity, Handle(pe)
End Function

;
; RemoveEntityPhysics entity
;
; Removes an entity's physics properties
;
Function RemoveEntityPhysics(entity)
	pe.PhysicsEntity = FindPhysicsEntity(entity)
	If pe <> Null Then
		NameEntity entity, ""
		Delete pe
	Else
		RuntimeError "Entity has no physics"
	End If
End Function

;
; ApplyForce entity,x#,y#,z#
;
; Applies an impulse force to physics entity
;
Function ApplyForce(entity, x#, y#, z#)
	pe.PhysicsEntity = FindPhysicsEntity(entity)
	If pe <> Null Then
		pe\xv = pe\xv + x
		pe\yv = pe\yv + y
		pe\zv = pe\zv + z
	Else
		RuntimeError "Entity has no physics"
	End If
End Function

;
; UpdatePhysics
;
; This will update all entities with physics
;

Function UpdatePhysics()
	For e1.PhysicsEntity = Each PhysicsEntity
		;
		; Check if the entity is moving
		;
		If e1\xv <> 0 Or e1\yv <> 0 Or e1\zv <> 0 Then
	
			;
			; Record current entity position
			;
			x# = EntityX(e1\entity, True)
			y# = EntityY(e1\entity, True)
			z# = EntityZ(e1\entity, True)	
	
			;
			; Reduce velocity due to friction
			;
			If e1\friction > 0 Then
				speed# = Sqr(e1\xv ^ 2 + e1\yv ^ 2 + e1\zv ^ 2)
				new_speed# = speed - e1\friction
				If new_speed <= 0 Then
					e1\xv = 0
					e1\yv = 0
					e1\zv = 0
				Else
					e1\xv = e1\xv / speed * new_speed
					e1\yv = e1\yv / speed * new_speed
					e1\zv = e1\zv / speed * new_speed
				End If
			End If

			;
			; Do a line pick to check for collisions
			;
			ent = LinePick(x, y, z, e1\xv, e1\yv, e1\zv, e1\radius)
			If ent > 0 Then e2.PhysicsEntity = FindPhysicsEntity(ent)
			If ent = 0 Or e2 = Null
				;
				; Add velocity vector to current position
				;
				x = x + e1\xv
				y = y + e1\yv
				z = z + e1\zv
			Else
				;
				; Get the point of collision
				;
				Px# = PickedX()
				Py# = PickedY()
				Pz# = PickedZ()
			
				;
				; Get the collision normal vector
				;
				Nx# = PickedNX()
				Ny# = PickedNY()
				Nz# = PickedNZ()
			
				;
				; Back up a little to prevent collision errors
				;
				TFormNormal e1\xv, e1\yv, e1\zv, 0, 0
				dx# = TFormedX() * 0.05
				dy# = TFormedY() * 0.05
				dz# = TFormedZ() * 0.05
						
				;
				; Calculate the new position
				;
				x = Px + (e1\radius) * Nx - dx
				y = Py + (e1\radius) * Ny - dy
				z = Pz + (e1\radius) * Nz - dz
			
				;
				; Conservation of momentum
				;
				a1# = e1\xv * Nx + e1\yv * Ny + e1\zv * Nz
				a2# = e2\xv * Nx + e2\yv * Ny + e2\zv * Nz
				OptP# = 2 * (a1 - a2) / (e1\mass + e2\mass)
			
				e1\xv = (e1\xv - (OptP * e2\mass * Nx)) * e1\bounce
				e1\yv = (e1\yv - (OptP * e2\mass * Ny)) * e1\bounce
				e1\zv = (e1\zv - (OptP * e2\mass * Nz)) * e1\bounce

				e2\xv = (e2\xv + (OptP * e1\mass * Nx)) * e2\bounce
				e2\yv = (e2\yv + (OptP * e1\mass * Ny)) * e2\bounce
				e2\zv = (e2\zv + (OptP * e1\mass * Nz)) * e2\bounce
			End If
			;
			; Reposition entity
			;
			PositionEntity e1\entity, x, y, z, True
		End If
	Next
End Function
