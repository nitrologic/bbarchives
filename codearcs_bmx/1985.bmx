; ID: 1985
; Author: bradford6
; Date: 2007-04-07 16:31:32
; Title: miniB3D cubes
; Description: miniB3D version of my very first Blitz3D entry

Import sidesign.minib3d
AppTitle =  "miniB3D cubes"
Graphics3D 800,600

cam = CreateCamera()
MoveEntity cam ,32,32,-600 ' Move the camera back (-z)  "units"
light = CreateLight ()
MoveEntity light, -5, 5, -5

' A 'Box' type, which simply creates a list called Box,
' And has an entity handle For each 'Box' in the list...

Type Box
	Global List:TList = CreateList()
	Method New()
		ListAddLast(List , Self)
	End Method

	Field entity:Tentity
	Field spinspeed#,movespeed#,entitysize#
End Type

' Create a cube, Then hide it -- we'll use CopyEntity to create our list of cubes
' from this, which means we're only using the memory needed for *one* cube...

model:Tentity = CreateCube()
HideEntity model

' Create 16X16 copies in our 'Box' list... the 'b.Box = New Box' line simply
' creates a New entry in the 'Box' list, and b.Box is just a pointer to the
' current entry, which is obviously changing as you add more cubes To the Box
' list. We Then just place it in rows on screen...

For horiz = 1 To 16
	For vert = 1 To 16
		b:Box = New Box
		b.entity = CreateCube()
		b.spinspeed# = Rnd(-5,5)
		b.entitysize# = Rnd(.1,1)
		PositionEntity b.entity, horiz*4, vert*4, zpos#
		EntityColor b.entity,Rnd (0,255), Rnd (0, 255), Rnd (0,255)
		'zpos#=zpos#+.1

	Next 
Next 
Repeat

	' Process the Box list, turning the copied cube entity
	' stored in each entry of the list...
camspeed#=.001
	For b:Box = EachIn Box.List
		TurnEntity b.entity, b.spinspeed#,0, b.spinspeed#
		MoveEntity b.entity,0,0,b.movespeed#
		If EntityZ#( cam )<-40 MoveEntity cam,0,0,camspeed# camspeed#=camspeed+.0001
		

'TurnEntity cam , 0,.001,Sin(radius#)
		radius# = radius# + .001
		If radius# = 360 Then radius# =1 
			ScaleEntity b.entity ,Sin(radius#),Sin(radius#),Sin(radius#)
		Next

	UpdateWorld
	RenderWorld

	Flip

Until KeyHit (KEY_ESCAPE)

End
