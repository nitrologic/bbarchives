; ID: 983
; Author: Jeppe Nielsen
; Date: 2004-03-30 08:23:14
; Title: Enemy chasing with collision
; Description: Shows how enemies can chase a target, without sliding into same position. No blitz collisions used, only math.

Graphics3D 800,600,16,2

cam=CreateCamera()
RotateEntity cam,90,0,0
PositionEntity cam,0,50,0

;create player entity
player=CreateSphere()
EntityFX player,1

;create 10 randomly positioned creatures, with random velocity aswell
For n=1 To 10
	creaturenew(Rnd(-20,20),0,Rnd(-20,20),Rnd(0.1,0.5))
Next

Repeat
	
	;make the creatures home in on player
	creatureupdate(EntityX(player),EntityY(player),EntityZ(player))
	
	;control player with mouse
	PositionEntity player,MouseX()/10-30,0,-MouseY()/10+30
	
	RenderWorld
	
	Text 400,0,"Control player with mouse",1,0
	Text 400,20,"Creatures will not slide into each other",1,0
	
	
	
	Flip
	
	
Until KeyDown(1)
End

Type creature

Field e ;entity

Field vel# ;velocity

End Type

Function CreatureNew(x#,y#,z#,vel#)
	
	c.creature=New creature
	
	c\e=CreateCube()
	
	c\vel=vel
	
	PositionEntity c\e,x,y,z
	
End Function

Function CreatureUpdate(x#,y#,z#,size#=2) ;size is 2 here as a created cube is 2 units wide
	
	For c.creature=Each creature
	
		;vector to x,y,z
		
		dx#=(x-EntityX(c\e))
		dy#=(y-EntityY(c\e))
		dz#=(z-EntityZ(c\e))
		
		;length of vector
		
		l#=Sqr(dx*dx+dy*dy+dz*dz)
		
		;make the vector a unit vector, length = 1
		
		dx=dx/l
		dy=dy/l
		dz=dz/l
		
		TranslateEntity c\e,dx*c\vel,dy*c\vel,dz*c\vel
		
		For cc.creature=Each creature
			
			;do not test against it self
			If cc<>c
			
				;vector to another enemy
				dx#=(EntityX(cc\e)-EntityX(c\e))
				dy#=(EntityY(cc\e)-EntityY(c\e))
				dz#=(EntityZ(cc\e)-EntityZ(c\e))
				
				;length of vector
				
				l#=Sqr(dx*dx+dy*dy+dz*dz)
				
				
				;enemies collide if they er within range:
				
				If l<size
					
					;make the vector a unit vector, length = 1
					
					dx=dx/l
					dy=dy/l
					dz=dz/l
					
					TranslateEntity c\e,-dx*c\vel,-dy*c\vel,-dz*c\vel
					
				EndIf
					
			EndIf
			
		Next
		
	Next
	
End Function
