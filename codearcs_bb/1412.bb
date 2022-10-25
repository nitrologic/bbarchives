; ID: 1412
; Author: Erroneouss
; Date: 2005-06-28 11:58:16
; Title: Random 3D/2D Dungeon Generator
; Description: A random 2d/3d dungeon generator, as the title says

;controls: mouse look + up/down/left/right
Graphics3D 640,480,32,2
AppTitle "A Auto-Dungeon Generator","Exit? NOOOO!!!!!!"
SeedRnd MilliSecs()




Type cube
 Field x#,y#,z#
 Field mesh
End Type 




Dim level$(20,20)  ;our level
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	;go from left to right
	y=Rand(5,14)
	level$(x,y)="O"
	 For x=1 To 19
		level$(x,y)="O"
		y=y+Rand(-1,1)
		If y<0 y=0
		If y>20 y=20
		level$(x,y)="O"
 	Next 
	y=Rand(5,14)
	x=0
	level$(x,y)="O"
 	For x=1 To 19
		level$(x,y)="O"
		y=y+Rand(-1,1)
		If y<0 y=0
		If y>20 y=20
		level$(x,y)="O"
 	Next 
	;go from top to bottom
	y=0
	x=Rand(0,19)
	level$(x,y)="O"
 	For y=1 To 19
		level$(x,y)="O"
		x=x+Rand(-1,1)
		If x<0 x=0
		If x>20 x=20
		level$(x,y)="O"
	Next 
	y=0
	x=Rand(0,19)
	level$(x,y)="O"
 	For y=1 To 19
		level$(x,y)="O"
		x=x+Rand(-1,1)
		If x<0 x=0
		If x>20 x=20
		level$(x,y)="O"
 	Next 
	;fill in remaining places
	For i=0 To 19
 		For p=0 To 19
			If level$(i,p)<>"O" level$(i,p)="X"
 		Next
	Next 






;make 3d level
For i=0 To 19
 For p=0 To 19
    If level$(i,p)="O"
	 ;nothing because its open 
	Else
	 c.cube=New cube
	 c\x#=i*3
	 c\y#=0
	 c\z#=p*3
	 c\mesh=CreateCube()
	 PositionEntity c\mesh,c\x#,c\y#,c\z#
	 EntityColor c\mesh,Rand(115,128),Rand(51,64),0 ;obviously take this away and replace
	 ScaleEntity c\mesh,1.5,1,1.5                     ;with random texturing for coolness....
	EndIf 	
 Next
 Print 
Next 







camera=CreateCamera()
l=CreateLight()



While Not KeyDown(1)

	;mouse look taken from something i don't remember... :/ i was in a hurry...
	mxs#=MouseXSpeed()*0.25                                                                   
	mys#=MouseYSpeed()*0.25                                                            
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	campitch=campitch+mys
	If campitch<-85 Then campitch=-85
	If campitch>85 Then campitch=85
	RotateEntity camera,campitch,EntityYaw(camera)-mxs,0
	mvx=mvx/1.2
	mvy=mvy/1.2
	mvz=mvz/1.2
	MoveEntity camera,mvx,0,mvz
	TranslateEntity camera,0,mvy,0
	If EntityY(camera)>60 Then PositionEntity camera,EntityX(camera),60,EntityZ(camera) 
	
	 
	
	 If KeyDown(200) MoveEntity camera,0,0,0.1
	 If KeyDown(208) MoveEntity camera,0,0,-.1
	 If KeyDown(205) MoveEntity camera,0.1,0,0
	 If KeyDown(203) MoveEntity camera,-.1,0,0
	 If EntityY(camera)<>0 PositionEntity camera,EntityX(camera),0,EntityZ(camera)
	RenderWorld 
	Flip 
	Cls
Wend
