; ID: 856
; Author: Jeppe Nielsen
; Date: 2003-12-14 14:49:30
; Title: Ball rolling example
; Description: Shows how you can make a ball roll

;Ball rolling physics example by Jeppe Nielsen 2003

Const gravity#=-0.04;-0.02 ;gravity constant

Graphics3D 800,600,16,1

Const ball_col=1
Const world_col=2

Collisions ball_col,world_col,2,2

Global texture

texture=CreateTexture(16,16)

SetBuffer TextureBuffer(texture)

Color 255,255,255
Rect 0,0,16,16,1

Color 255,0,0
Rect 0,0,8,8,1
Rect 8,8,8,8,1

ScaleTexture texture,0.1,0.1


Global groundtexture=CreateTexture(32,32,1+8)

SetBuffer TextureBuffer(groundtexture)

Color 0,0,0
Rect 0,0,32,32,1

Color 0,0,255
Rect 0,0,32,32,0

ScaleTexture groundtexture,2,2

SetBuffer BackBuffer()
Color 255,255,255

plane=CreatePlane()
EntityTexture plane,groundtexture
EntityType plane,world_col
EntityAlpha plane,0.5

mirror=CreateMirror()




light=CreateLight(1)
RotateEntity light,30,20,0


obj=CreateCube()

EntityType obj,world_col
PositionEntity obj,0,0,0
ScaleEntity obj,10,10,10
RotateEntity obj,45,0,0



camera=CreateCamera()
CameraClsColor camera,0,0,255


ball.ball=ballnew(0,4,-120,2,0.8)


zoom#=14

Repeat

zoom#=zoom#-MouseZSpeed()
ballcamera(camera,ball,0,zoom#,-zoom#,0,5,0)


ballcontrol()
ballupdate()

RenderWorld()

Text 10,10,"Arrow keys to control ball"
Text 10,30,"Scroll on mouse to zoom"


Flip


Until KeyDown(1)
End


Type ball

Field e ;entity

Field sphere

Field pivot

Field x#,y#,z# ; position in 3d-space

Field vx#,vy#,vz# ; velocity

Field ax#,ay#,az# ; acceleration

Field size#

Field bounce# ; bounce factor

Field vel#

Field vx2#,vy2#,vz2# ; temp velocity

End Type


Function ballnew.ball(x#,y#,z#,size#=1,bounce#=0.9)
	
	c.ball=New ball
	
	c\x#=x#
	c\y#=y#
	c\z#=z#
	
	c\size=size
	
	c\bounce#=bounce#
	
	c\e=CreatePivot()
		
	c\sphere=CreateSphere(64)
	
	c\pivot=CreatePivot()
	
	EntityType c\e,ball_col
	EntityRadius c\e,c\size
	
	PositionEntity c\e,c\x,c\y,c\z
	ScaleEntity c\sphere,c\size,c\size,c\size
	
	EntityTexture c\sphere,texture
	
	Return c
	
End Function

Function ballupdate()
	
	For c.ball=Each ball
	
			
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
		
	For c.ball=Each ball
	
		;correct velocity if collided
		c\vx2=(EntityX(c\e)-c\x)
		c\vy2=(EntityY(c\e)-c\y)
		c\vz2=(EntityZ(c\e)-c\z)
		
		c\x#=EntityX(c\e)
		c\y#=EntityY(c\e)
		c\z#=EntityZ(c\e)
		
		PositionEntity c\sphere,c\x,c\y,c\z
		PositionEntity c\pivot,c\x,c\y,c\z
		
		If EntityCollided(c\e,world_col)
				
			For i = 1 To CountCollisions(c\e)
				; Get the normal of the surface which the entity collided with. 
				Nx# = CollisionNX(c\e, i) 
				Ny# = CollisionNY(c\e, i) 
				Nz# = CollisionNZ(c\e, i) 
				
				; Compute the dot product of the entity's motion vector and the normal of the surface collided with. 
				VdotN# = c\vx#*Nx# + c\vy#*Ny# + c\vz#*Nz# 
				
				; Calculate the normal force. 
				NFx# = -2.0 * Nx# * VdotN# 
				NFy# = -2.0 * Ny# * VdotN# 
				NFz# = -2.0 * Nz# * VdotN# 
				
				; Add the normal force to the direction vector. 
				c\vx# = c\vx# + NFx# * c\bounce#
				c\vy# = c\vy# + NFy# * c\bounce#
				c\vz# = c\vz# + NFz# * c\bounce#

				avx#=EntityPitch(c\sphere)
				avy#=EntityYaw(c\sphere)
				avz#=EntityRoll(c\sphere)

				;Rotate stuff
				;Get vector from center to collision
				dx1#=(CollisionX(c\e,i)-c\x)
				dy1#=(CollisionY(c\e,i)-c\y)
				dz1#=(CollisionZ(c\e,i)-c\z)
				
				dx2#=c\vx
				dy2#=c\vy
				dz2#=c\vz
				
				;Cross product
				cx# = ( dy1 * dz2 ) - ( dz1 * dy2 ) 
				cy# = ( dz1 * dx2 ) - ( dx1 * dz2 ) 
				cz# = ( dx1 * dy2 ) - ( dy1 * dx2 ) 				
															
				AlignToVector c\pivot,cx,cy,cz,1
																									
			Next
			
			Nx# = CollisionNX(c\e, 1) 
			Ny# = CollisionNY(c\e, 1) 
			Nz# = CollisionNZ(c\e, 1) 				
					
			AlignToVector c\e,Nx#,Ny#,Nz#,2,0.5
			
			c\vel#=Sqr(c\vx2*c\vx2+c\vy2*c\vy2+c\vz2*c\vz2)
						
			;slow down due to friction
			c\vx#=c\vx*0.98
			c\vy#=c\vy*0.98
			c\vz#=c\vz*0.98
					

		EndIf

			
		EntityParent c\sphere,c\pivot
								
		TurnEntity c\pivot,-c\vel#*(180/Pi)/c\size#,0,0

		EntityParent c\sphere,0

		c\ax#=0
		c\ay#=0
		c\az#=0	
		
	Next
	
End Function


Function ballcontrol()
	
	For c.ball=Each ball
		
		If KeyDown(200)
			
			TFormVector 0,0,0.03,c\e,0
			
			c\ax#=TFormedX()
			c\ay#=TFormedY()
			c\az#=TFormedZ()
			
		EndIf
		
		If KeyDown(208)
			
			c\vx=c\vx*0.94
			c\vy=c\vy*0.94
			c\vz=c\vz*0.94
			
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

Function ballcamera(camera,b.ball,camx#,camy#,camz#,aimx#=0,aimy#=0,aimz#=0,smoothcam#=0.1,roll#=0)
	
	TFormPoint camx#,camy#,camz#,b\e,0
	
	dx#=(TFormedX()-EntityX(camera))*smoothcam#
	dy#=(TFormedY()-EntityY(camera))*smoothcam#
	dz#=(TFormedZ()-EntityZ(camera))*smoothcam#
	
	TranslateEntity camera,dx,dy,dz
	
	TFormPoint aimx#,aimy#,aimz#,b\e,0
	
	dx# = EntityX(camera)-TFormedX()
	dy# = EntityY(camera)-TFormedY()
	dz# = EntityZ(camera)-TFormedZ()
	dist#=Sqr#((dx#*dx#)+(dz#*dz#))
	pitch#=ATan2(dy#,dist#)
	yaw#=ATan2(dx#,-dz#)
	
	RotateEntity camera,pitch#,yaw#,roll#
	
End Function
