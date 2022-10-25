; ID: 103
; Author: bradford6
; Date: 2001-10-14 20:44:23
; Title: Collision Normals demo
; Description: my first test  with Collision Normals

; Entity Collision Normals Example 2 (Edited)
; I am a newbie so be kind. this is my attempt at making things bounce off at specific angles
; let me know what you think. b_radford@yahoo.com

camdistance=10

Graphics3D 640,480 ;rem setup the Graphics mode

light=CreateLight() ;rem create a light for our scene

Global barney=CreateSphere() ;rem create a cube and call it barney 
PositionEntity barney,0,1,0
Global obpiv=CreatePivot(barney) ; attach a pivot to barney
MoveEntity obpiv,0,0,-camdistance ; move the pivot back a little adjust this for effect

Global cam=CreateCamera() ;rem create a camera and make barney the cube it's parent entity

mirror=CreateMirror()
plane=CreatePlane()
planetex=CreateTexture(64,64)
SetBuffer TextureBuffer(planetex)
Color 255,0,0
Rect 5,5,60,60
ScaleTexture planetex,8,8
EntityTexture plane,planetex
EntityAlpha plane,.3

SetBuffer BackBuffer()

me=1
balls=2
EntityType barney,me

Type brick ;rem create a type called brick with 1 field in it
Field entity ;rem create a field in type brick to hold the entity number
Field xvel#,yvel#,zvel#


End Type

For x = 1 To 5 ;rem create 100 "bricks" To place in the world
For y = 1 To 5 

b.brick = New brick ;rem add an entry into the type list b 

b\entity = CopyEntity(barney) ;rem make a copy of barney and call it b\entity
EntityType b\entity,balls
PositionEntity b\entity,x*16,1,y*16 ;rem make a grid

EntityColor b\entity,Rnd(0,255),Rnd(0,255),Rnd(0,255) ;rem random red,green,blue colors
;EntityAlpha b\entity,.9
Next
Next

Collisions me,balls,1,1 ; set up collisions between me and the world 
;Collisions balls,balls,1,3
; main loop
While Not KeyDown( 1 )

If KeyDown (203) Then bangle#=bangle#-1
If KeyDown(205) Then bangle#=bangle#+1
If KeyDown(200) Then speed#=speed#+.01
;If KeyDown(208) Then speed#=speed#-.005

speed#=speed#*.99
speeddiv#=speed#/2

If bangle#>180 Then bangle#=-180
If bangle#<-180 Then bangle#=180

RotateEntity barney,0,-bangle#,0

velx#=Sin(bangle#)*speed#
velz#=Cos(bangle#)*speed#

posx#=posx#+velx#
posz#=posz#+velz#



PositionEntity barney,posx#,1,posz#
;MoveEntity barney,0,0,speed#

smoothcam(obpiv,barney,50)

; check for collisions and bounce if necessary
If CountCollisions ( barney ) 
	hitentity=CollisionEntity ( barney,1 )
	xnorm#=CollisionNX(barney,1)
	ynorm#=CollisionNY(barney,1)
	znorm#=CollisionNZ(barney,1)
	
	For b.brick= Each brick
		If b\entity = hitentity
	  
		  	b\xvel#=b\xvel#+xnorm#*speeddiv#
			b\yvel#=b\yvel#+ynorm#*speeddiv#
			b\zvel#=b\zvel#+znorm#*speeddiv#
		EndIf
	Next
	
	
EndIf

For b.brick= Each brick
			If CountCollisions ( b\entity ) 
				hit=CollisionEntity ( b\entity,1 )
		
				xnorm#=CollisionNX(b\entity,1)
				ynorm#=CollisionNY(b\entity,1)
				znorm#=CollisionNZ(b\entity,1)
			EndIf
			
			
	
MoveEntity b\entity,-b\xvel#,-b\yvel#,-b\zvel#  ; update the balls positions 
b\xvel#=b\xvel# * .99
b\zvel#=b\zvel# *.99		; friction 

;gravity#=.001
;b\yvel#=b\yvel#-gravity#
;If EntityY(b\entity)<1 Then b\yvel#=b\yvel#+b\yvel#



Next



If EntityCollided ( barney,balls )   ; bounce the player back after hitting 

bangle#=ATan2(xnorm#,znorm#)
speed#=speed# * .97
EndIf

UpdateWorld
RenderWorld ;rem render the world
;Text 0,0,"xn = "+xnorm#+"   YN  = "+ynorm#+"  znorm   =  "+znorm#
Text 0,0, "arrows to move around-- hit the balls at an angle :) "
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
cury#=5

PositionEntity cam,curx#,cury#,curz#

PointEntity cam,target
End Function


