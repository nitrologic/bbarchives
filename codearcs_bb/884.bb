; ID: 884
; Author: Jeppe Nielsen
; Date: 2004-01-11 17:37:08
; Title: On a collision course
; Description: How to calculate if two entities are on a collision course in a plane

;How to predict if two entities will collide, not taking their speed into consideration
;By Jeppe Nielsen 2004
;Lines_intersect function by sswift

; Values returned by the Lines_Intersect() function.
Global Intersection_X#									
Global Intersection_Y#
Global Intersection_AB#
Global Intersection_CD#

Graphics3D 800,600,16,2

SeedRnd MilliSecs()
cam=CreateCamera()

PositionEntity cam,0,400,0
RotateEntity cam,90,0,0
CameraZoom cam,5

Global ent1=CreateCube() : EntityFX ent1,1
Global ent2=CreateCube() : EntityFX ent2,1

Global intersectsphere=CreateSphere()
EntityColor intersectsphere,255,255,0
EntityFX intersectsphere,1
HideEntity intersectsphere

PositionEntity ent1,20,0,10
RotateEntity ent1,0,45,0
PositionEntity ent2,0,0,-20
RotateEntity ent2,0,0,0

	Repeat
	
	MoveEntity ent1,0,0,0.1
	MoveEntity ent2,0,0,0.1
	
	If KeyHit(57)
	
		resetentities
	
	EndIf
	
	RenderWorld
	
	Text 400,10,"Space to reset entities",1
	Text 400,20,"Flashing yellow sphere indicates intersection point",1
	
	If CollisionCourse(ent1,ent2)
	
		Text 400,300,"ENTITIES ARE ON A COLLISION COURSE!",1,1
		
		If Sin(MilliSecs()*5)>0
			ShowEntity intersectsphere
		Else
			HideEntity intersectsphere
		EndIf
		
		PositionEntity intersectsphere,Intersection_X#,0,Intersection_Y#
		
	Else
	
		HideEntity intersectsphere
	
	EndIf
	
	Flip
	
Until KeyDown(1)
End

Function resetentities()
	
	PositionEntity ent1,Rnd(-20,20),0,Rnd(-20,20)
	RotateEntity ent1,0,Rnd(360),0
	PositionEntity ent2,Rnd(-20,20),0,Rnd(-20,20)
	RotateEntity ent2,0,Rnd(360),0
	
End Function

Function CollisionCourse(e1,e2)
	
	x1#=EntityX(e1,1)
	z1#=EntityZ(e1,1)
	
	TFormVector 0,0,1,e1,0
	
	dx1#=TFormedX()
	dz1#=TFormedZ()
	
	x2#=EntityX(e2,1)
	z2#=EntityZ(e2,1)
	
	TFormVector 0,0,1,e2,0
	
	dx2#=TFormedX()
	dz2#=TFormedZ()
	
	
	;if trajectories are parallel
	If Lines_Intersect(x1,z1,x1+dx1,z1+dz1,x2,z2,x2+dx2,z2+dz2)=False
	
		Return False
	
	Else
	
	Return Intersection_AB#>=0 And Intersection_CD#>=0
	
	EndIf
	
End Function


;function by sswift
Function Lines_Intersect(Ax#, Ay#, Bx#, By#, Cx#, Cy#, Dx#, Dy#)
  

	Rn# = (Ay#-Cy#)*(Dx#-Cx#) - (Ax#-Cx#)*(Dy#-Cy#)
        Rd# = (Bx#-Ax#)*(Dy#-Cy#) - (By#-Ay#)*(Dx#-Cx#)

	If Rd# = 0 
		
		; Lines are parralel.

		; If Rn# is also 0 then lines are coincident.  All points intersect. 
		; Otherwise, there is no intersection point.
	
		Return False
	
	Else
	
		; The lines intersect at some point.  Calculate the intersection point.
	
                Sn# = (Ay#-Cy#)*(Bx#-Ax#) - (Ax#-Cx#)*(By#-Ay#)

		Intersection_AB# = Rn# / Rd#
		Intersection_CD# = Sn# / Rd#

		Intersection_X# = Ax# + Intersection_AB#*(Bx#-Ax#)
         	Intersection_Y# = Ay# + Intersection_AB#*(By#-Ay#)
			
		Return True
		
	EndIf


End Function
