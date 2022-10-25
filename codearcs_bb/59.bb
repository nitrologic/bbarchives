; ID: 59
; Author: bradford6
; Date: 2001-09-26 20:03:58
; Title: Types and Entities
; Description: manipulating entites using TYPE comands

Graphics3D 640, 480

cam = CreateCamera()
MoveEntity cam ,32,32,-600 ; Move the camera back (-z)  "units"
light = CreateLight ()
MoveEntity light, -5, 5, -5

; A 'Box' type, which simply creates a list called Box,
; and has an entity handle for each 'Box' in the list...

Type Box
	Field entity
	Field spinspeed#,movespeed#,entitysize#
End Type

; Create a cube, then hide it -- we'll use CopyEntity to create our list of cubes
; from this, which means we're only using the memory needed for *one* cube...

model = CreateCube ()
HideEntity model

; Create 16X16 copies in our 'Box' list... the 'b.Box = New Box' line simply
; creates a new entry in the 'Box' list, and b.Box is just a pointer to the
; current entry, which is obviously changing as you add more cubes to the Box
; list. We then just place it in rows on screen...

For horiz = 1 To 16
	For vert = 1 To 16
		b.Box = New Box
		b\entity = CopyEntity (model)
		b\spinspeed# = Rnd(-5,5)
		b\entitysize# = Rnd(.1,1)
		PositionEntity b\entity, horiz*4, vert*4, zpos#
		EntityColor b\entity,Rnd (0,255), Rnd (0, 255), Rnd (0,255)
		;zpos#=zpos#+.1

	Next 
Next 
Repeat

	; Process the Box list, turning the copied cube entity
	; stored in each entry of the list...
camspeed#=.001
	For b.Box = Each Box
		TurnEntity b\entity, b\spinspeed#,0, b\spinspeed#
		MoveEntity b\entity,0,0,b\movespeed#
		If EntityZ#( cam )<-40 MoveEntity cam,0,0,camspeed# camspeed#=camspeed+.0001
		



;TurnEntity cam , 0,.001,Sin(radius#)
		radius# = radius# + .001
		If radius# = 360 Then radius# =1 
			ScaleEntity b\entity ,Sin(radius#),Sin(radius#),Sin(radius#)
		Next

	UpdateWorld
	RenderWorld

	Flip

Until KeyHit (1)

End
