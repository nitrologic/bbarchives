; ID: 944
; Author: RiverRatt
; Date: 2004-02-24 07:19:20
; Title: sphere_and_box
; Description: sphere pushing a box around

;sphere moveing a box around by Matt Anthony 
;This code was made with the help of people on the blitz
;forum and is free to all

Graphics3D 640,480,16 
SetBuffer BackBuffer() 

Const cube_col=1 
Const sphere_col=2 


light= CreateLight() 


camera=CreateCamera() 
PositionEntity camera,0,40,0 

Type cubedata 
Field x#,y# 
End Type 

Type circledata 
Field x#,y# 
End Type 

AutoMidHandle =True 
 
box.cubedata = New cubedata 
box\x#=-10 
box\y#=40 
cube= CreateCube() 
ScaleEntity cube,3,3,3 
PositionEntity cube,box\x#,box\y#,20 
EntityColor cube,255,0,0 
EntityType cube,cube_col;collisionccccccccccccccccccccccccccccc 

;player 
circle.circledata = New circledata 
circle\x#=10 
circle\y#=40 
sphere= CreateSphere() 
EntityColor sphere,0,0,255 

PositionEntity sphere,circle\x#,circle\y#,20 
EntityType sphere,sphere_col;collisionccccccccccccccccccccccccccccc 

Collisions sphere_col,cube_col,2,3 

Global rsx#= .25  ;right movment value
Global lsx#=-.25  ;left movment value
Global usy#=-.25  ;up movement value
Global dsy#= .25  ;down movement value
;Const bx#=1
While Not KeyHit(1);Main MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM 

If KeyDown(30) MoveEntity sphere,lsx#,0,0 
If KeyDown(32) MoveEntity sphere,rsx#,0,0 
If KeyDown(17) MoveEntity sphere,0,dsy#,0 
If KeyDown(31) MoveEntity sphere,0,usy#,0 
UpdateWorld() 
RenderWorld() 

Color 255,255,0
Text 335,500,"Collision Detection"
Text 10,20,circle\x + circle\y 
If EntityCollided(sphere,cube_col);collision 
	Text 370,80,"Collided !!!" 

	If EntityX(sphere)+1.5 > EntityX(cube) Then ; Check on wich side the sphere is

		TranslateEntity cube,-.25,0,0 

	EndIf

	If EntityX(sphere)-1.5 < EntityX(cube) Then ; Check on wich side the sphere is

		TranslateEntity cube,.25,0,0 
	EndIf
EndIf

	
If EntityCollided(sphere,cube_col);collision
	Text 370,80,"Collided !!!" 
 	
	If EntityY(sphere)+1.5 < EntityY(cube) Then ; Check on wich side the sphere is

		TranslateEntity cube,0,.25,0 
	EndIf

	If EntityY(sphere)-1.5 > EntityY(cube) Then ; Check on wich side the sphere is

		TranslateEntity cube,0,-.25,0 
	EndIf



	

EndIf
 
 
Flip 
Wend ;End of Main MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
End
