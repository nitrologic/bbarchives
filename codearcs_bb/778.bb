; ID: 778
; Author: Jeppe Nielsen
; Date: 2003-08-23 08:26:13
; Title: Simple car physics
; Description: Drive a cube around in a spherical world

; Simple car physics by Jeppe Nielsen 2003


Const gravity#=-0.01 ;gravity constant


Graphics3D 640,480,16,2

Const car_col=1
Const world_col=2

Collisions car_col,world_col,2,2


light=CreateLight(1)

RotateEntity light,30,20,0

plane=CreatePlane()

EntityType plane,world_col
EntityColor plane,255,0,0

For n=1 To 100

If Rnd(10)<5

sphere=CreateSphere(16)

Else

sphere=CreateCube()

EndIf

EntityType sphere,world_col
PositionEntity sphere,Rnd(-40,40),Rnd(2),Rnd(-40,40)
EntityColor sphere,Rnd(255),Rnd(255),Rnd(255)

Next

sp=CreateSphere()
ScaleEntity sp,100,100,100
FlipMesh sp

camera=CreateCamera()
CameraClsColor camera,0,0,255


car.car=carnew(0,5,0)

Repeat
TFormPoint 0,3,-5,car\e,0

dx#=(TFormedX()-EntityX(camera))*.1
dy#=(TFormedY()-EntityY(camera))*.1
dz#=(TFormedZ()-EntityZ(camera))*.1

TranslateEntity camera,dx,dy,dz

PointEntity camera,car\e

carcontrol()
carupdate()

RenderWorld()

Flip


Until KeyDown(1)
End


Type car

Field e ;entity

Field x#,y#,z# ; position in 3d-space

Field vx#,vy#,vz# ; velocity

Field ax#,ay#,az# ; acceleration

End Type


Function carnew.car(x#,y#,z#)
	
	c.car=New car
	
	c\x#=x#
	c\y#=y#
	c\z#=z#
	
	c\e=CreateCube()
	cube=CreateCube()
	ScaleEntity cube,0.3,0.3,0.3
	PositionEntity cube,0,0,1
	EntityParent cube,c\e
	
	EntityType c\e,car_col
	EntityRadius c\e,1
	
	PositionEntity c\e,c\x,c\y,c\z
	
	Return c
	
End Function

Function carupdate()
	
	For c.car=Each car
	
		c\vy#=c\vy#+gravity#
		
		c\vx#=c\vx#+c\ax#
		c\vy#=c\vy#+c\ay#
		c\vz#=c\vz#+c\az#
		
		c\x#=EntityX(c\e)
		c\y#=EntityY(c\e)
		c\z#=EntityZ(c\e)
		
		TranslateEntity c\e,c\vx,c\vy,c\vz
	
	Next
	
	UpdateWorld()
	
	For c.car=Each car
		
		;correct velocity if collided
		c\vx=(EntityX(c\e)-c\x)
		c\vy=(EntityY(c\e)-c\y)
		c\vz=(EntityZ(c\e)-c\z)
		
		
		;slow down due to friction
		If EntityCollided(c\e,world_col)
			
			c\vx#=c\vx*0.98
			c\vy#=c\vy*0.98
			c\vz#=c\vz*0.98
		
		EndIf
		
		c\ax#=0
		c\ay#=0
		c\az#=0
		
	Next
	
End Function


Function carcontrol()
	
	For c.car=Each car
		
		If KeyDown(200)
			
			TFormVector 0,0,0.02,c\e,0
			
			c\ax#=TFormedX()
			c\ay#=TFormedY()
			c\az#=TFormedZ()
			
		EndIf
		
		If KeyDown(208)
			
			c\vx=c\vx*0.99
			c\vy=c\vy*0.99
			c\vz=c\vz*0.99
			
		EndIf
		
		If KeyDown(57)
			
			TFormVector 0,0.05,0,c\e,0
			
			c\ax#=c\ax+TFormedX()
			c\ay#=c\ay+TFormedY()
			c\az#=c\az+TFormedZ()
			
									
		EndIf
		
		If KeyDown(203)
			
			TurnEntity c\e,0,2,0
			
		EndIf
		
		If KeyDown(205)
			
			TurnEntity c\e,0,-2,0
			
		EndIf
		
		
	Next
	
	
	
End Function
