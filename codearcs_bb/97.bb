; ID: 97
; Author: bradford6
; Date: 2001-10-12 00:58:31
; Title: 3rd person camera (simple)
; Description: 3rd person camera  with collisions 

; Smooth 3rd person Camera Example
; ----------------

camdistance=10

Graphics3D 640,480 			;rem   setup the Graphics mode

light=CreateLight()			;rem create a light for our scene

Global barney=CreateCube()		;rem create a cube and call it barney	
Global obpiv=CreatePivot(barney) ; attach a pivot to barney
MoveEntity obpiv,0,0,-camdistance		; move the pivot back a little adjust this for effect
				
Global cam=CreateCamera()	;rem create a camera and make barney the cube it's parent entity
	

me=1
building=2
EntityType barney,me

Type brick					;rem create a type called brick with 1 field in it
Field entity				;rem create a field in type brick to hold the entity number
End Type

For x = 1 To 10		;rem	create 100 "bricks" To place in the world
	For y = 1 To 10		

			b.brick = New brick 		;rem add an entry into the type list b 

			b\entity = CopyEntity(barney) ;rem make a copy of barney and call it b\entity
			EntityType b\entity,building
			PositionEntity b\entity,x*8,0,y*8 ;rem make a grid

			EntityColor b\entity,Rnd(0,255),Rnd(0,255),Rnd(0,255) ;rem random red,green,blue colors

Next
Next

Collisions me,building,3,1 ; set up collisions between me and the world 

; main loop
While Not KeyDown( 1 )

	If KeyDown (203) Then TurnEntity barney,0,1,0
	If KeyDown(205) Then TurnEntity barney,0,-1,0
	If KeyDown(200) Then speed#=speed#+.003
	If KeyDown(208) Then speed#=speed#-.003

speed#=speed#*.99
MoveEntity barney,0,0,speed#

smoothcam(obpiv,barney,50)

; check for collisions and bounce if necessary
If CountCollisions ( barney ) 
	hitentity=CollisionEntity ( barney,1 ) 
	;change the color of the entity we hit
	EntityColor hitentity,Rnd(0,255),Rnd(0,255),Rnd(0,255)
EndIf

If EntityCollided ( barney,building ) 
	If speed#<0 Then speed#=speed# +.1
	If speed#>0 Then speed#=speed# -.1
EndIf

	UpdateWorld
	RenderWorld  ;rem render the world
	Flip ; flip from the back buffer to the front buffer 

Wend

End

Function smoothcam(pivot,target,camspeed)


curx#=EntityX(cam)
curz#=EntityZ(cam)
destx#=EntityX(pivot,True)
destz#=EntityZ(pivot,True)

curx#=curx#+((destx#-curx#)/camspeed)
curz#=curz#+((destz#-curz#)/camspeed)
cury#=3

PositionEntity cam,curx#,cury#,curz#

PointEntity cam,target
End Function


