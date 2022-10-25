; ID: 2316
; Author: Nate the Great
; Date: 2008-09-17 22:34:20
; Title: Verlet Physics (incomplete)
; Description: Verlet Physics for simple low poly effects in a game

Graphics3D 640,480,0,2
SeedRnd(MilliSecs())




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;Temporary camera stuff;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piv = CreatePivot()
cam = CreateCamera(piv)
;CameraRange cam,.01,50
CameraZoom cam,2
MoveEntity cam,0,3,-30
lit = CreateLight()
TurnEntity lit,90,0,0

TurnEntity piv,20,0,0

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;




Global VerletType = 1					;Collision Types
Global RBodyType = 2
Global RigidBodyNum = 0
Global groundtype = 3

;Collisions VerletType,RBodyType,2,2  	;Sets Collision Types
;Collisions VerletType,VerletType,1,2
Collisions verletType,GroundType,2,2

Type Verlet								;Verlet type Contains:
	Field Active						;Determines if the verlet is an active verlet or a verlet used for orientation
	Field Mass#							;Gives the verlet a mass
	Field x#,y#,z#						;Gives the verlet an x,y,z cooridnate
	Field vx#,vy#,vz#					;Gives the verlet a velocity in 3d
	Field ox#,oy#,oz#					;Stores the old x,y,z coordinates to figure out the velocity of the verlet
	Field piv,ent						;Gives the verlet a pivot point and names the verlet's entity
	Field Col,ID,piv2,radius#			;Col tells if the verlet has collided yet and ID tells what entity and verlet group the verlet belongs to
End Type	

Type Constraint							;Constraints constrain the verlets to certain distances from eachother
	Field v1.verlet						;First verlet in constraint
	Field v2.verlet						;Second verlet in constraint
	Field length#						;Length of the constraint
End Type

Type Rigidbody										;Rigidbody is used as a reference for all of the verlets that belong to a mesh
	Field Ent										;Ent is the entity that is acting as the rigid body
	Field ID										;ID is the ID that all of the verlets in this mesh are attatched to
	Field x#,y#,z#									;X,Y,Z coordinates of the mesh
	Field Yaw#,pitch#,Roll#							;Yaw,Pitch,Roll coordinates of the mesh
	Field lf.verlet,lb.verlet,rf.verlet,rb.verlet	;The verlets that are inactive and are used to orient the mesh
	Field lfd.verlet,lbd.verlet,rfd.verlet,rbd.verlet;The verlets that are inactive and are used to orient the mesh	
	Field c.verlet,idl								;The central Verlet
	Field Verl.verlet[50],verlnum
End Type





SetBuffer BackBuffer()

ground = CreatePlane()
EntityColor ground,32,32,64
EntityAlpha ground,.9
EntityType ground,GroundType



rwall = CreateCube()
ScaleEntity rwall,.1,.8,10
MoveEntity rwall,5,.1,0

EntityType rwall,GroundType

lwall = CreateCube()
ScaleEntity lwall,.1,.8,10
MoveEntity lwall,-5,.1,0

EntityType lwall,GroundType

bwall = CreateCube()
ScaleEntity bwall,5,.8,.1
MoveEntity bwall,0,.1,-10

EntityType bwall,GroundType

fwall = CreateCube()
ScaleEntity fwall,5,.8,.1
MoveEntity fwall,0,.1,10

EntityType fwall,GroundType


obstacle = CreateCylinder(10)
MoveEntity obstacle,0,0,5

EntityType obstacle,GroundType


puck = CreateCylinder(8)
MoveEntity puck,0,.8,0
ScaleEntity puck,.3,.1,.3

applyphysics(puck,10,False,.2)



pl1 = CreateCylinder(10)
MoveEntity pl1,0,.8,-4
ScaleEntity pl1,.5,.2,.5
EntityColor pl1,255,0,0

applyphysics(pl1,10,False,.2)

CreateMirror()





timer = MilliSecs()

cnter = 0
While Not KeyDown(1)
Cls


If KeyDown(205) Then PApplyForce(pl1,.015,0,0)
If KeyDown(203) Then PApplyForce(pl1,-.015,0,0)
If KeyDown(200) Then PApplyForce(pl1,0,0,.015)
If KeyDown(208) Then PApplyForce(pl1,0,0,-.015)



UpdateVerlets()

UpdateConstraints()

DrawVerlets()

UpdateWorld()
detectcollisions()

drawverlets()

positionPhysicsEntity()

RenderWorld()

cnt = 0
For v.verlet = Each verlet
	cnt = cnt + 1
Next

Text 1,1,cnt

cnter = cnter + 1
Text 1,20,"FPS: "+1000/((MilliSecs()-timer)/cnter)

Flip
Wend
End




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;Creates a verlet bounding box & creates verlets;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


Function ApplyPhysics(ent,mass#,stationary,verlsize# = .3)

rigidbodynum = rigidbodynum + 1


;Creates the Rigidbody that all of the verlets are linked to

r.rigidbody = New rigidbody
r\id = rigidbodynum
r\ent = ent
r\x# = EntityX(r\ent)
r\y# = EntityY(r\ent)
r\z# = EntityZ(r\ent)
r\yaw# = EntityYaw(r\ent)
r\pitch# = EntityPitch(r\ent)
r\roll# = EntityRoll(r\ent)
EntityType r\ent,RBodyType
r\idl = stationary

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;






;Loops through all surfaces and verticies
For k = 1 To CountSurfaces(ent)
	surf = GetSurface(ent,k)
	For index = 0 To CountVertices(surf)-1
		TFormPoint VertexX(surf,index), VertexY(surf, index),VertexZ(surf, index), ent, 0
		CreateVerlet(TFormedX(),TFormedY(),TFormedZ(),mass#,ent,r\ID,True,verlsize#)   ;Creates a verlet for every vertice  Later it deletes duplicate verlets for the sake of stability.
	Next
Next

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;Creates the bounding box verlets that don't react with anything but are used to orient the mesh

r\lf.verlet = CreateVerlet(r\x# - .5 , r\y# - .5, r\z# + .5, 1 , ent , r\ID , False)

r\lb.verlet = CreateVerlet(r\x# - .5 , r\y# - .5, r\z# - .5, 1 , ent , r\ID , False)

r\rf.verlet = CreateVerlet(r\x# + .5 , r\y# - .5, r\z# + .5, 1 , ent , r\ID , False)

r\rb.verlet = CreateVerlet(r\x# + .5 , r\y# - .5, r\z# - .5, 1 , ent , r\ID , False)

r\lfd.verlet = CreateVerlet(r\x# - .5 , r\y# + .5, r\z# + .5, 1 , ent , r\ID , False)

r\lbd.verlet = CreateVerlet(r\x# - .5 , r\y# + .5, r\z# - .5, 1 , ent , r\ID , False)

r\rfd.verlet = CreateVerlet(r\x# + .5 , r\y# + .5, r\z# + .5, 1 , ent , r\ID , False)

r\rbd.verlet = CreateVerlet(r\x# + .5 , r\y# + .5, r\z# - .5, 1 , ent , r\ID , False)

r\c.verlet = CreateVerlet(r\x# , r\y# , r\z#, 1 , ent , r\ID , False)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;Deletes Duplicate verlets so that the meshes are more stable

For v.verlet = Each verlet
	For vv.verlet = Each verlet
		If vv\ID = v\ID Then
			If vv\piv <> v\piv Then
				If v\x# = vv\x# And v\y# = vv\y# And v\z# = vv\z# And vv\mass <> 0 And v\mass <> 0 Then
					FreeEntity vv\piv
					Delete vv.verlet
				EndIf
			EndIf
		EndIf
	Next
Next

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;




cnt = 0
For v.verlet = Each verlet
	If V\ent = ent And V\Active = True Then
		R\Verl.verlet[cnt] = v.verlet
		cnt = cnt + 1
	EndIf
Next

r\Verlnum = cnt - 1


;This code makes constraints which it links every inside verlet to all eight of the outside verlets but no others

For v.verlet = Each verlet
	If v\ID = rigidbodynum Then
		If r\idl = False Then
			If v\active = True Then
				Createconstraint(v.verlet,r\rf.verlet)              ;Creates constraint
				Createconstraint(v.verlet,r\rb.verlet)
				Createconstraint(v.verlet,r\lf.verlet)
				Createconstraint(v.verlet,r\lb.verlet)
				Createconstraint(v.verlet,r\rfd.verlet)              ;Creates constraint
				Createconstraint(v.verlet,r\rbd.verlet)
				Createconstraint(v.verlet,r\lfd.verlet)
				Createconstraint(v.verlet,r\lbd.verlet)
				Createconstraint(v.verlet,r\c.verlet)
				
			EndIf
		EndIf
	EndIf
Next
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;




createconstraint(r\rf.verlet,r\c.verlet)
createconstraint(r\rb.verlet,r\c.verlet)
createconstraint(r\lf.verlet,r\c.verlet)
createconstraint(r\lb.verlet,r\c.verlet)
createconstraint(r\rfd.verlet,r\c.verlet)
createconstraint(r\rbd.verlet,r\c.verlet)
createconstraint(r\lfd.verlet,r\c.verlet)
createconstraint(r\lbd.verlet,r\c.verlet)

createconstraint(r\rf.verlet,r\rb.verlet)
createconstraint(r\rf.verlet,r\lf.verlet)
createconstraint(r\rf.verlet,r\lb.verlet)

createconstraint(r\rb.verlet,r\lb.verlet)
createconstraint(r\rb.verlet,r\lf.verlet)

createconstraint(r\lf.verlet,r\lb.verlet)


createconstraint(r\rfd.verlet,r\rbd.verlet)
createconstraint(r\rfd.verlet,r\lfd.verlet)
createconstraint(r\rfd.verlet,r\lbd.verlet)

createconstraint(r\rbd.verlet,r\lbd.verlet)
createconstraint(r\rbd.verlet,r\lfd.verlet)

createconstraint(r\lfd.verlet,r\lbd.verlet)


createconstraint(r\rf.verlet,r\rfd.verlet)
createconstraint(r\lf.verlet,r\lfd.verlet)
createconstraint(r\rb.verlet,r\rbd.verlet)
createconstraint(r\lb.verlet,r\lbd.verlet)

;Deletes duplicate Or reversed constraints  This speeds up the constraint loops very much

For c.constraint = Each constraint
	For cc.constraint = Each constraint
		If c\v1\piv = cc\v1\piv And c\v2\piv = c\v1\piv Then
			Delete cc.constraint
		EndIf
	Next
Next


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;





End Function














;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;Creates a verlet at the given x,y,z coordinate;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


Function createverlet.verlet(x#,y#,z#,mass#,ent,ID,Active,radius# = .1)

	v.Verlet = New Verlet
	v\x# = x#
	v\y# = y#
	v\z# = z#
	v\ox# = v\x#
	v\oy# = v\y#
	v\oz# = v\z#
	v\vx# = 0
	v\vy# = 0
	v\vz# = 0
	v\ent = ent
	v\ID = ID
	v\active = Active
	v\mass# = mass#
	v\radius# = radius#
	
	v\piv = CreatePivot()
	v\piv2 = CreatePivot()
	ScaleEntity v\piv,.2,.2,.2
	PositionEntity v\piv,v\x#,v\y#,v\z#
	
	If active = True Then
		EntityType v\piv,VerletType
		EntityRadius v\piv,radius#
	EndIf
	
	Return v
End Function

















;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;Constrains two verlets together;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


Function CreateConstraint(v1.verlet,v2.verlet)

	c.constraint = New constraint
	c\v1.verlet = v1.verlet
	c\v2.verlet = v2.verlet
	c\length# = Sqr((c\v1\x#-c\v2\x#)^2 + (c\v1\y#-c\v2\y#)^2 + (c\v1\z#-c\v2\z#)^2)

End Function
























;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;goes through every verlet and updates it;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function Updateverlets()

For v.verlet = Each verlet
	
	If v\col = True Then
		v\col = False
		fric# = .95
	Else
		fric# = 1
	EndIf
	
	v\vx# = (v\x# - v\ox#)*fric#
	v\vy# = (v\y# - v\oy#)*fric#
	v\vz# = (v\z# - v\oz#)*fric#
	
	v\ox# = v\x#
	v\oy# = v\y#
	v\oz# = v\z#
	
	v\x# = v\x# + v\vx#
	v\y# = v\y# + v\vy# - .004
	v\z# = v\z# + v\vz#
	
	
	For vv.verlet = Each verlet
			If v <> vv And v\id <> vv\id; if not the same verlet or group
				dx# = v\x# - vv\x#
				dy# = v\y# - vv\y#
				dz# = v\z# - vv\z#
				dist# = Sqr ( dx#*dx# + dy#*dy# + dz#*dz# )		
				totalr# = v\radius# + vv\radius#
				If dist# < totalr# Then
				
					
					Diffx# = ( dist# - totalr# ) * ( dx# / dist# )
					Diffy# = ( dist# - totalr# ) * ( dy# / dist# )
					Diffz# = ( dist# - totalr# ) * ( dz# / dist# )

					v\x# = v\x# - Diffx# ;* .5
					v\y# = v\y# - Diffy# ;* .5
					v\z# = v\z# - Diffz# ;* .5

					vv\x# = vv\x# + Diffx# ;* .5
					vv\y# = vv\y# + Diffy# ;* .5
					vv\z# = vv\z# + Diffz# ;* .5
				EndIf 				
			EndIf
		Next 

;	If v\y# < 0 Then
;		v\y# = 0
;		v\col = True
;	EndIf
	
Next

End Function






















;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;goes through every constraint and updates it;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function UpdateConstraints()

For i = 1 To 3

	For c.constraint = Each constraint
		mx# = ( c\v1\x# - c\v2\x# )
		my# = ( c\v1\y# - c\v2\y# )
		mz# = ( c\v1\z# - c\v2\z# )
		
		dist# = Sqr( (mx)^2 + (my)^2 + (mz)^2 )
		
		mx# = mx# / 2
		my# = my# / 2
		mz# = mz# / 2
		
		If dist# <> 0  Then
			dif# = (dist# - c\length#) / dist# * .7
		EndIf
		
	;	If c\v1\col = False Or i > 5 Then
			c\v1\x# = c\v1\x# - dif# * mx#
			c\v1\y# = c\v1\y# - dif# * my#
			c\v1\z# = c\v1\z# - dif# * mz#
	;	EndIf
	;	If c\v2\col = False Or i > 5 Then
			c\v2\x# = c\v2\x# + dif# * mx#
			c\v2\y# = c\v2\y# + dif# * my#
			c\v2\z# = c\v2\z# + dif# * mz#
	;	EndIf
	Next

Next

End Function



















;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;positions all verlets;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function Drawverlets()

For v.verlet = Each verlet
	
	PositionEntity v\piv,v\x#,v\y#,v\z#
	
Next

End Function


















;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;positions all meshes;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function PositionPhysicsEntity()

For r.rigidbody = Each rigidbody
	PositionEntity r\ent,EntityX(r\c\piv),EntityY(r\c\piv),EntityZ(r\c\piv)
	
	;align mesh to verlet cage
	x# = EntityX( r\rf\piv ) - EntityX( r\lf\piv ) + EntityX( r\rb\piv ) - EntityX( r\lb\piv )
	y# = EntityY( r\rf\piv ) - EntityY( r\lf\piv ) + EntityY( r\rb\piv ) - EntityY( r\lb\piv )
	z# = EntityZ( r\rf\piv ) - EntityZ( r\lf\piv ) + EntityZ( r\rb\piv ) - EntityZ( r\lb\piv )
	AlignToVector r\ent, x#,y#,z#,1  
	x# = EntityX( r\rf\piv ) - EntityX( r\rb\piv ) + EntityX( r\lf\piv ) - EntityX( r\lb\piv )
	y# = EntityY( r\rf\piv ) - EntityY( r\rb\piv ) + EntityY( r\lf\piv ) - EntityY( r\lb\piv )
	z# = EntityZ( r\rf\piv ) - EntityZ( r\rb\piv ) + EntityZ( r\lf\piv ) - EntityZ( r\lb\piv )
	AlignToVector r\ent, x#,y#,z#, 3
Next

End Function





















;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;tests all verlets for collisions;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function Detectcollisions()

For v.verlet = Each verlet
	If EntityX(v\piv) <> v\x# Then
		If EntityCollided(v\piv,3) Then
			v\col = True
		EndIf
		v\x# = EntityX(v\piv)
	EndIf
	If EntityY(v\piv) <> v\y# Then
		If EntityCollided(v\piv,3) Then
			v\col = True
		EndIf
		v\y# = EntityY(v\piv)
	EndIf
	If EntityZ(v\piv) <> v\z# Then
		If EntityCollided(v\piv,3) Then
			v\col = True
		EndIf
		v\z# = EntityZ(v\piv)
	EndIf
Next

End Function






;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;applies a force to given object;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


Function PApplyForce(ent,x#,y#,z#)

For r.rigidbody = Each rigidbody
	If r\ent = ent Then
		For i = 0 To r\Verlnum
			r\verl[i]\ox# = r\verl[i]\ox# - x#
			r\verl[i]\oy# = r\verl[i]\oy# - y#
			r\verl[i]\oz# = r\verl[i]\oz# - z#
		Next
	EndIf
Next

End Function



Function PMoveEntity(ent,x#,y#,z#)

For r.rigidbody = Each rigidbody
	If r\ent = ent Then
		For i = 0 To r\verlnum
			r\verl[cnt]\ox# = r\verl[cnt]\ox# + x#
			r\verl[cnt]\oy# = r\verl[cnt]\oy# + y#
			r\verl[cnt]\oz# = r\verl[cnt]\oz# + z#
			r\verl[cnt]\x# = r\verl[cnt]\x# + x#
			r\verl[cnt]\y# = r\verl[cnt]\y# + y#
			r\verl[cnt]\z# = r\verl[cnt]\z# + z#
		Next
	EndIf
Next

End Function



Function PPositionEntity(ent,x#,y#,z#)

For r.rigidbody = Each rigidbody
	If r\ent = ent Then
		For i = 0 To r\verlnum
			r\verl[cnt]\ox# = x#
			r\verl[cnt]\oy# = y#
			r\verl[cnt]\oz# = z#
			r\verl[cnt]\x# = x#
			r\verl[cnt]\y# = y#
			r\verl[cnt]\z# = z#
		Next
	EndIf
Next

End Function
