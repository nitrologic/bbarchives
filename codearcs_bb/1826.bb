; ID: 1826
; Author: kevin8084
; Date: 2006-09-28 10:45:00
; Title: Radar
; Description: Another Radar System

; **************************************************************************************************
; A simple radar system by Kevin Lee Legge (kevin8084@gmail.com)
; **************************************************************************************************


; set up the 3d graphics
Graphics3D 800,600,32,1
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Const enemy_type  = 2
Const cube_type   = 3

Const radarX=660 ; x-coordinate of the radar on the screen
Const radarY=10  ; y-coordinate of the radar on the screen
Const centerX=radarX+65 ; center (x-coordinate) of the radar
Const centerY=radarY+65 ; center (y-coordinate) of the radar

Global sweep=0 ; this is used for the radar's sweep

Type enemy
	Field entity
	Field x#,y#,z#
End Type

; create an enemy to track
Global e.enemy = New enemy
e\entity=CreateSphere()
EntityRadius e\entity,1
EntityColor e\entity,255,0,0
EntityShininess e\entity,1
PositionEntity e\entity,Rand(-10,10),0,Rand(-15,15)
EntityType e\entity,enemy_type

; we need to see things and be able to move around
Global player=CreateCamera()
PositionEntity player,0,0,0

light=CreateLight()
PositionEntity light,25,100,25
RotateEntity light,90,0,0

plane=CreatePlane()
EntityColor plane,0,50,0
PositionEntity plane,0,-1,0

; let's populate the world with some cubes for visual reference
For c=1 To 30
	cube = CreateCube()
	EntityType cube,cube_type
	EntityColor cube,Rand(255),Rand(255),Rand(255)
	PositionEntity cube,Rand(-125,125),0,Rand(-125,125)
Next

; don't want the enemy to pass through the cubes
Collisions enemy_type,cube_type,2,2

While Not KeyHit(1)
If KeyDown(200) Then MoveEntity player,0,0,.2
If KeyDown(208) Then MoveEntity player,0,0,-.2
If KeyDown(203) Then TurnEntity player,0,1,0
If KeyDown(205) Then TurnEntity player,0,-1,0

; give the enemy some movement to track on the radar
moveEnemy(e)

UpdateWorld
RenderWorld
; draw the radar
drawRadar()
; plot the enemy on the radar in relation to the player
radar(e,player)
Flip
Delay 10
Wend
; free up some memory
FreeEntity e\entity
Delete e
End

Function moveEnemy(enemy.enemy)
; this function just allows the enemy to move at random so that we can track its progress
; on the radar
MoveEntity enemy\entity,0,0,.1
If Rand(55)=12 Then RotateEntity enemy\entity,0,EntityYaw#(enemy\entity)+Rand(-15,15),0
End Function

Function drawRadar()
; this function just draws your basic radar screen
Color 85,255,85 ; a fairly light green color
Oval radarX,radarY,130,130,0 ; first circle of the radar - 130 pixels in diameter
Oval radarX+20,radarY+20,90,90,0 ; second circle - 90 pixels in diameter
Oval radarX+40,radarY+40,50,50,0 ; third circle - 50 pixels in diameter
Color 120,195,120
; separate the upper and lower half of the radar into quadrants
Line centerX,radarY+40,centerX,10
Line centerX,radarY+90,centerX,radarY+130
Line centerX+25,centerY,centerX+65,centerY
Line centerX-65,centerY,centerX-25,centerY
Line centerX+18,centerY-18,centerX+Cos(45)*65,centerY-Sin(45)*65
Line centerX-18,centerY-18,centerX-Cos(45)*65,centerY-Sin(45)*65
Line centerX+18,centerY+18,centerX+Cos(45)*65,centerY+Sin(45)*65
Line centerX-18,centerY+18,centerX-Cos(45)*65,centerY+Sin(45)*65

; this line command draws the radar's sweep. The Cos and Sin enable the sweep to traverse a
; 360 degree arc from the centerpoint
Line centerX,centerY,centerX+Cos(sweep)*65,centerY+Sin(sweep)*65
sweep=sweep+1
; this resets the variable back to 0 if it exceeds 360 degrees
If sweep>360 Then sweep=0
; set color back to white in case you need to draw text, etc...
Color 255,255,255
End Function


Function radar(e.enemy,center)
; this function plots the enemy's location relative to the player
; note: "center" in this function call is the player's location
For e.enemy=Each enemy
	dist#=EntityDistance#(center,e\entity) ; first we need to get the distance of the enemy from the player
	dx#=EntityX(center)-EntityX(e\entity) ; how far to the side of the player is the enemy?
	dz#=EntityZ(center)-EntityZ(e\entity) ; how far in front or behind the player is the enemy?
	; what's the angle to turn to face the enemy - compensating for the player's turning?
	deltay#=ATan2(dx,dz)-180+EntityYaw#(center)
	; just basic trigonometry to find the point x,y (enemy's location) given the angle deltay#
	x#=dist*Cos(deltay#-90)
	y#=dist*Sin(deltay#-90) 
	x#=x#/2 ; scales down the x-coordinate by half so that the plot stays within our radar
	y#=y#/2 ; scales down the y-coordinate by half so that the plot stays within our radar
	Color 255,255,255
	If dist#<=130 Then ; this is the diameter of our largest radar circle
		Plot centerX+x,centerY+y ; plot the pixel representing our enemy
	End If
Next
End Function ; radar()
