; ID: 1629
; Author: Tin-cat
; Date: 2006-02-27 00:35:28
; Title: using Object and Handle
; Description: how to effect a field of the entity that was cliped!

; how to effect a field of the entity that was cliped!
; by Tin-cat  Trinnexx@hotmail.com

Graphics3D 640,480

Type player
	Field entity
	Field xvel#,yvel#,zvel#
End Type

Type block
	Field entity
	Field state%
End Type

Const COL_BOX = 1
Const COL_PLAYER = 2

Collisions COL_PLAYER,COL_BOX, 2, 2

; light and cam
light = CreateLight()
RotateEntity light ,20 ,45 ,0
cam = CreateCamera()
PositionEntity cam ,0 ,12 ,-30

; make a box
box = CreateCube()
FlipMesh box
UpdateNormals box
ScaleEntity box, 10, 10, 10
PositionEntity box, 0, 10, 0 
EntityType box,COL_BOX
HideEntity box
;give it a type
b.block = New block
b\entity = CopyEntity (box)
b\state = False
NameEntity b\entity, Handle(b) ; IMPORTANT! names the entity so you can access it later

;make a sphere
aball= CreateSphere(16)
EntityType aball,COL_PLAYER
HideEntity aball
;give it a type
p.player = New player
p\entity = CopyEntity(aball)
p\xvel = 0.1
p\yvel = 0.2
p\zvel = 0
PositionEntity p\entity, 0, 10, 0

; main loop
While Not KeyDown(1)
 	
	If EntityCollided(p\entity,COL_BOX) Then
	
		Mesh = CollisionEntity(p\entity,1)
		
		c.block = Object.block(EntityName(mesh))
		; recalls the type of the collided entity
		
		If c\state Then c\state = False Else c\state = True
		; flip the state so we can see it working
		
		Nx# = CollisionNX(p\entity, COL_BOX) ; got this from the forum
		Ny# = CollisionNY(p\entity, COL_BOX) ; but cant find it again!
		Nz# = CollisionNZ(p\entity, COL_BOX) ; please lemme know so i can give cred
		
		VdotN# = (p\xvel*Nx + p\yvel*Ny + p\zvel*Nz	)*2
		; note '*2'   1 = no energy return   2= 100%
		
		NFx# = -Nx# * VdotN 
		NFy# = -Ny# * VdotN 
		NFz# = -Nz# * VdotN 

		p\xvel = p\xvel + NFx
		p\yvel = p\yvel + NFy
		p\zvel = p\zvel + NFz
		
	EndIf
	
	p\yvel = p\yvel - 0.02 ; gravity, gotta do this after col or else energy builds up
		
	TranslateEntity p\entity,p\xvel,p\yvel,p\zvel ; move it
	
	UpdateWorld	: RenderWorld

	Text 0,0,"state of b\state: "+ Str(b\state)

	Flip

Wend
End
