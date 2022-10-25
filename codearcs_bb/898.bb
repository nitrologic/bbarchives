; ID: 898
; Author: simonh
; Date: 2004-01-28 11:44:41
; Title: Dynamic Collision Lib
; Description: A small lib that allows for dynamic sphere -> dynamic poly collisions using Blitz's built-in collision system

; Dynamic Collisions Example
; --------------------------

;Include "dcol_lib.bb" ; must be used at start of program

Graphics3D 640,480
SetBuffer BackBuffer()

cam=CreateCamera() : PositionEntity cam,0,12,-10 : RotateEntity cam,30,0,0
light=CreateLight()

; Create source entity
sphere=CreateSphere() : EntityColor sphere,0,0,255 : PositionEntity sphere,0,5,0

; Give sphere a collision type for collisions with non-dynamic objects, i.e. the ground in this case
EntityType sphere,1 : EntityRadius sphere,1

; Create ground - this won't be moving and so will not be part of the dynamic collision system.
; It will be part of the normal collision system however so apply collision type to it and enable collisions with sphere.
plane=CreatePlane() : EntityColor plane,0,255,0
EntityType plane,2

; Set up normal sliding collisions between sphere and plane
Collisions 1,2,2,2

; NOW THE DYNAMIC STUFF - *** Important comments highlighted by stars ***

; Create first destination entity - platform 1
platform1=CreateCylinder(16) : ScaleEntity platform1,2,6,2 : EntityColor platform1,255,255,0
PositionEntity platform1,-10,-8,8

; Create second destination entity - platform 2
platform2=CreateCube() : ScaleEntity platform2,4,4,4 : EntityColor platform2,255,0,0
PositionEntity platform2,10,0,8

src_type=3 ; assign a collision type for source collision entity (sphere)
des_type=4 ; assign a collision type for destination collision entity (platforms)

; *** Set dynamic collision pairs ***
DCO_SetPair(sphere,platform1,src_type,des_type,1.0) ; parameters - src_entity,des_entity,src_type,des_type,src_radius=1.0
DCO_SetPair(sphere,platform2,src_type,des_type,1.0)

; *** Enable collisions for dynamic pairs ***
Collisions src_type,des_type,2,2

While Not KeyDown(1)
 
	; Control sphere
	sph_x#=0
	sph_z#=0
	If KeyDown(203)=True Then sph_x=-.4
	If KeyDown(205)=True Then sph_x=.4
	If KeyDown(208)=True Then sph_z=-.4
	If KeyDown(200)=True Then sph_z=.4

	; Apply gravity to sphere
	MoveEntity sphere,0,-.5,0

	; Make our platforms dynamic - move them!
	MoveEntity platform1,0,Sin(angle#)*0.1,0 : angle#=angle#+1
	TurnEntity platform2,0,1,0
	TranslateEntity platform2,0,0,Sin(angle#)*0.1
	MoveEntity sphere,sph_x,sph_y,sph_z

	; *** Call DCO_Update functions, with UpdateWorld inbetween them ***
	DCO_UpdateA()
	UpdateWorld 0
	DCO_UpdateB()
	
	; *** Usual UpdateWorld call ***
	UpdateWorld
	
	; *** For best results, try experimenting with the order of the above four function calls ***
	
	RenderWorld
	
	; Output dynamic collision information - the dynamic collision system works best if you use these values to implement
	; proper bouncing physics between source entity and destination entity
	Text 0,0,"EntityCollided: "+DCO_EntityCollided()
	Text 0,20,"CollisionNX: "+DCO_CollisionNX#()
	Text 0,40,"CollisionNY: "+DCO_CollisionNY#()
	Text 0,60,"CollisionNZ: "+DCO_CollisionNZ#()
	
	Flip

Wend

End










; Dynamic collision lib by Simon Harrison (si@si-design.co.uk)

; You are free to use this source code how you like, however a credit in any programs that use it would be appreciated.

; Limitations:
; Only works with spheres as source entities
; Only works if the source entity is colliding with one dynamic entity at once

; Functions:
; DCO_SetPair(src_ent,des_ent,src_type,des_type,src_rad=1.0)
; DCO_UpdateA()
; DCO_UpdateB()
; DCO_EntityCollided()
; DCO_CollisionNX()
; DCO_CollisionNY()
; DCO_CollisionNZ()

; * See included example for demonstration of system.

Global DCO_pair_pos
Global DCO_ent
Global DCO_nx#
Global DCO_ny#
Global DCO_nz#

Type DCO_pair

	Field src_ent,src_col,src_type,src_rad
	Field des_ent,des_col,des_type
	
End Type

Function DCO_SetPair(src_ent,des_ent,src_type,des_type,src_rad=1.0)

	c.DCO_pair=New DCO_pair
	
	c\src_ent=src_ent
	c\src_type=src_type
	c\src_rad=src_rad
	
	c\des_ent=des_ent
	c\des_type=des_type
	
	c\src_col=CreatePivot() : EntityRadius c\src_col,src_rad
	c\des_col=CopyEntity(des_ent)
	EntityAlpha c\des_col,0
	
	DCO_pair_pos=DCO_pair_pos+(MeshWidth(c\des_col)*2)
	DCO_pair_pos=DCO_pair_pos+1000
	PositionEntity c\des_col,DCO_pair_pos,DCO_pair_pos,0
	
	EntityType c\src_col,src_type
	EntityType c\des_col,des_type
	
	ResetEntity c\src_col
	ResetEntity c\des_col
	
End Function

Function DCO_UpdateA()

	Local dx#,dy#,dz#,pivo,pivv

	For c.DCO_pair=Each DCO_pair

		dx#=EntityX#(c\src_ent)-EntityX#(c\des_ent)
		dy#=EntityY#(c\src_ent)-EntityY#(c\des_ent)
		dz#=EntityZ#(c\src_ent)-EntityZ#(c\des_ent)

		pivo=CreatePivot()
		pivv=CreatePivot(pivo)
		
		PositionEntity pivo,EntityX#(c\des_col),EntityY#(c\des_col),EntityZ#(c\des_col)
		
		RotateEntity pivo,0,0,0
		
		PositionEntity pivv,dx#,dy#,dz#,False

		RotateEntity pivo,-EntityPitch#(c\des_ent),-EntityYaw#(c\des_ent),-EntityRoll#(c\des_ent)

		PositionEntity c\src_col,EntityX#(pivv,True),EntityY#(pivv,True),EntityZ#(pivv,True)
		
		FreeEntity pivo
		
	Next

End Function

Function DCO_UpdateB()

	Local col,dx#,dy#,dz#,dx2#,dy2#,dz2#,ddx#=0,ddy#=0,ddz#=0,pivo,pivv,pivn
	
	DCO_ent=0
	For c.DCO_pair=Each DCO_pair
				
		col=EntityCollided(c\src_col,c\des_type)
		
		If col=c\des_col
	
			DCO_ent=c\des_ent
	
			dx#=EntityX#(c\src_ent)-EntityX#(c\des_ent)
			dy#=EntityY#(c\src_ent)-EntityY#(c\des_ent)
			dz#=EntityZ#(c\src_ent)-EntityZ#(c\des_ent)
		
			dx2#=EntityX#(c\src_col)-EntityX#(c\des_col)
			dy2#=EntityY#(c\src_col)-EntityY#(c\des_col)
			dz2#=EntityZ#(c\src_col)-EntityZ#(c\des_col)
	
			pivo=CreatePivot()
			pivv=CreatePivot(pivo)
			pivn=CreatePivot(pivo) ; piv used to translate collsion normals
			
			PositionEntity pivo,0,0,0
	
			RotateEntity pivo,-EntityPitch#(c\des_ent),-EntityYaw#(c\des_ent),-EntityRoll#(c\des_ent)
			
			PositionEntity pivv,dx2#,dy2#,dz2#,True
			PositionEntity pivn,CollisionNX#(c\src_col,1),CollisionNY#(c\src_col,1),CollisionNZ#(c\src_col,1),True
			
			RotateEntity pivo,0,0,0
			
			; platform normals
			DCO_nx#=EntityX#(pivn)
			DCO_ny#=EntityY#(pivn)
			DCO_nz#=EntityZ#(pivn)
			
			dx2#=EntityX#(pivv)
			dy2#=EntityY#(pivv)
			dz2#=EntityZ#(pivv)
	
			ddx#=dx2#-dx#
			ddy#=dy2#-dy#
			ddz#=dz2#-dz#
							
			FreeEntity pivo
		
			TranslateEntity c\src_ent,ddx#,ddy#,ddz#
		
			;Exit
		
		EndIf
				
	Next

End Function

Function DCO_EntityCollided()

	Return DCO_ent

End Function

Function DCO_CollisionNX#()

	Return DCO_nx#

End Function

Function DCO_CollisionNY#()

	Return DCO_ny#

End Function

Function DCO_CollisionNZ#()

	Return DCO_nz#

End Function
