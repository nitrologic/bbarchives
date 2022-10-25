; ID: 857
; Author: Jeppe Nielsen
; Date: 2003-12-15 14:48:46
; Title: Wheel rolling example
; Description: Shows how to roll a wheel, unlike a ball it cannot go in any direction.

;wheel rolling physics example by Jeppe Nielsen 2003
;email: nielsen_jeppe@hotmail.com

Const gravity#=-0.04;-0.02 ;gravity constant

Graphics3D 800,600,16,2

Const wheel_col=1
Const world_col=2
Collisions wheel_col,world_col,2,3


;create old amiga texture :-)
Global texture

texture=CreateTexture(16,16)

SetBuffer TextureBuffer(texture)

Color 255,255,255
Rect 0,0,16,16,1

Color 255,0,0
Rect 0,0,8,8,1
Rect 8,8,8,8,1

ScaleTexture texture,0.5,0.5

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



light=CreateLight(1)
RotateEntity light,30,20,0


obj=CreateCube()
EntityType obj,world_col
PositionEntity obj,0,-20,-175
ScaleEntity obj,10,140,40
RotateEntity obj,75,0,0
EntityTexture obj,texture


sphere=CreateSphere(32)
EntityType sphere,world_col
ScaleEntity sphere,40,40,40
EntityTexture sphere,texture


Dim VertArray(0)
sphere1=AdjustTriangles(sphere,0.5,False)
sphere2=AdjustTriangles(Sphere,0.5,True)
FlipMesh sphere2

PositionEntity sphere1,0,35,0
RotateEntity sphere1,180,0,0
ScaleEntity sphere1,40,40,40
EntityTexture sphere1,texture
EntityType sphere1,world_col
RotateEntity sphere2,180,0,0
PositionEntity sphere2,0,35,0
ScaleEntity sphere2,40,40,40
EntityTexture sphere2,texture
EntityType sphere2,world_col


camera=CreateCamera()
CameraClsColor camera,0,0,255
PositionEntity camera,0,600,-40

wheel.wheel=wheelnew(0,4,0,2)

zoom#=14

Repeat

zoom#=zoom#-MouseZSpeed()
wheelcamera(camera,wheel,0,zoom#,-zoom#,0,5,0)


wheelcontrol(wheel)
wheelupdate()

RenderWorld()

Color 255,255,255

Text 10,10,"Arrow keys to control wheel"
Text 10,20,"Space to lift wheel"
Text 10,30,"Scroll on mouse to zoom"


Flip


Until KeyDown(1)
End


Type wheel

Field e ;entity

Field cylinder

Field pivot

Field x#,y#,z# ; position in 3d-space

Field vx#,vy#,vz# ; velocity

Field ax#,ay#,az# ; acceleration

Field size#

Field vel#

Field vx2#,vy2#,vz2# ; temp velocity

Field collided

End Type


Function wheelnew.wheel(x#,y#,z#,size#=1)
	
	c.wheel=New wheel
	
	c\x#=x#
	c\y#=y#
	c\z#=z#
	
	c\size=size
	
	c\e=CreatePivot()
			
	c\cylinder=CreateCylinder(64)
	RotateMesh c\cylinder,0,0,90
	
	c\pivot=CreatePivot()
	
	EntityType c\e,wheel_col
	EntityRadius c\e,c\size
	
	PositionEntity c\e,c\x,c\y,c\z
	ScaleEntity c\cylinder,c\size,c\size,c\size
	
	EntityTexture c\cylinder,texture
	
	Return c
	
End Function

Function wheelupdate()
	
	For c.wheel=Each wheel
	
			
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
		
	For c.wheel=Each wheel
	
		;correct velocity if collided
		c\vx2=(EntityX(c\e)-c\x)
		c\vy2=(EntityY(c\e)-c\y)
		c\vz2=(EntityZ(c\e)-c\z)
		
		c\x#=EntityX(c\e)
		c\y#=EntityY(c\e)
		c\z#=EntityZ(c\e)
		
		PositionEntity c\cylinder,c\x,c\y,c\z
		PositionEntity c\pivot,c\x,c\y,c\z
		
		TFormVector 1,0,0,c\e,0
		AlignToVector c\cylinder,TFormedX(),TFormedY(),TFormedZ(),1,0.6
		
		c\collided=0
				
		If EntityCollided(c\e,world_col)
		
			c\collided=1
				
			avnx#=0
			avny#=0
			avnz#=0
				
			count=CountCollisions(c\e)	
				
			For i = 1 To count
				; Get the normal of the surface which the entity collided with. 
				Nx# = CollisionNX(c\e, i) 
				Ny# = CollisionNY(c\e, i) 
				Nz# = CollisionNZ(c\e, i) 
				
				avnx#=avnx+Nx
				avny#=avny+Ny
				avnz#=avnz+Nz
				
				; Compute the dot product of the entity's motion vector and the normal of the surface collided with. 
				VdotN# = c\vx#*Nx# + c\vy#*Ny# + c\vz#*Nz# 
				
				; Calculate the normal force. 
				NFx# = -2.0 * Nx# * VdotN# 
				NFy# = -2.0 * Ny# * VdotN# 
				NFz# = -2.0 * Nz# * VdotN# 
				
				; Add the normal force to the direction vector. 
				c\vx# = c\vx# + NFx# 
				c\vy# = c\vy# + NFy# 
				c\vz# = c\vz# + NFz# 
								
			Next
				
			If count>0 
							
				;average collision normals
				avnx#=avnx/count
				avny#=avny/count
				avnz#=avnz/count

				;align controlpivot to averaged collision normals
				AlignToVector c\e,avnx,avny,avnz,2
				
				;calculate length of motion vector, or total velocity.
				l#=Sqr(c\vx*c\vx+c\vy*c\vy+c\vz*c\vz)

				;get normalized direction of motion, the vector will have a length of one.
				;the +0.00001 is there to aviod NaN (not a number) numbers. This happens
				;if the length of the vector is 0.
				TFormNormal c\vx,c\vy+0.00001,c\vz,0,0
				vx#=TFormedX()
				vy#=TFormedY()
				vz#=TFormedZ()
											
				;get direction of wheel
				TFormNormal 0,0,1,c\e,0
				
				;calculate a dot product, a number between -1 and 1 to determine angle
				;calculate angle like this: angle#=Acos(dot#)		
				dot#=TFormedX()*vx+TFormedY()*vy+TFormedZ()*vz
			
				;correct velocities, by multiplying the controlpivot´s direction with
				;the dot product, followed by the original velocity vector length:
				c\vx=TFormedX()*dot*l
				c\vy=TFormedY()*dot*l
				c\vz=TFormedZ()*dot*l
							
				RotateEntity c\pivot,EntityPitch(c\e),EntityYaw(c\e),EntityRoll(c\e)
																					
			EndIf
						
			c\vel#=Sqr(c\vx2*c\vx2+c\vy2*c\vy2+c\vz2*c\vz2)
						
			;slow down due To friction
			c\vx#=c\vx*0.98
			c\vy#=c\vy*0.98
			c\vz#=c\vz*0.98
			
		EndIf
		
		EntityParent c\cylinder,c\pivot
		
		TFormVector c\vx,c\vy,c\vz,0,c\e
		;moving forward
		If TFormedZ()>0
		
			TurnEntity c\pivot,c\vel#*(180/Pi)/c\size#,0,0
		
		Else
		;moving backwards
		
			TurnEntity c\pivot,-c\vel#*(180/Pi)/c\size#,0,0
			
		EndIf

		EntityParent c\cylinder,0

		;reset acceleration
		c\ax#=0
		c\ay#=0
		c\az#=0	
		
	Next
	
End Function


Function wheelcontrol(c.wheel)

		If c\collided=1
		
			If KeyDown(200)
				
				TFormVector 0,0,0.05,c\e,0
				
				c\ax#=TFormedX()
				c\ay#=TFormedY()
				c\az#=TFormedZ()
				
			EndIf
		
			If KeyDown(208)
				
				c\vx=c\vx*0.94
				c\vy=c\vy*0.94
				c\vz=c\vz*0.94
				
			EndIf
		
		EndIf
		
		If KeyDown(57)
			
			c\ay#=c\ay+0.05
														
		EndIf
		
		If KeyDown(203)
			
			TurnEntity c\e,0,2,0
			
		EndIf
		
		If KeyDown(205)
			
			TurnEntity c\e,0,-2,0
			
		EndIf
		
		
	
End Function

Function wheelcamera(camera,b.wheel,camx#,camy#,camz#,aimx#=0,aimy#=0,aimz#=0,smoothcam#=0.1,roll#=0)
	
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

;function by Graythe
Function AdjustTriangles%(EntityNo%,Triangles#,FreeSource%=True,FX%=False)
Dim VertArray(2^16)
Copied=CreateMesh(GetParent(EntityNo))
For SurfLoop=1 To CountSurfaces(EntityNo)
	
	NewSurface=CreateSurface(Copied)

	OldSurface=GetSurface(EntityNo,SurfLoop)
	
	NoVertices=CountVertices(OldSurface)-1
	TotTriangles=Int(CountTriangles(OldSurface)*Triangles)-1
		
	For TriLoop=False To TotTriangles
		For CornerLoop=False To 2
			tri=TriangleVertex(OldSurface,TriLoop,CornerLoop)
			VertArray(tri)=True
		Next
	Next
	
	For VertLoop = False To NoVertices
		If VertArray(VertLoop) Then
			NewVertex=AddVertex(NewSurface,VertexX(OldSurface,VertLoop),VertexY(OldSurface,VertLoop),VertexZ(OldSurface,VertLoop),VertexU(OldSurface,VertLoop),VertexV(OldSurface,VertLoop),VertexW(OldSurface,VertLoop))
		End If
	Next		
		
	For TriLoop=False To TotTriangles
		AddTriangle NewSurface, TriangleVertex(OldSurface,TriLoop,False),TriangleVertex(OldSurface,TriLoop,1),TriangleVertex(OldSurface,TriLoop,2)
	Next
		
Next
Dim VertArray(0)
UpdateNormals Copied
If FX Then EntityFX Copied,FX
If FreeSource Then FreeEntity EntityNo
Return Copied
End Function
