; ID: 62
; Author: bradford6
; Date: 2001-09-28 19:18:11
; Title: simple ball physics
; Description: Types and simple ball physics

; this is an experiment in physics and types
; Bill Radford 2001
; from the outset I am attempting to attach physical properties to 3d objects
; using Blitz's TYPE command set. I am new to this (about 1 week) so if you find a better
; method of doing this or improve my code, please let me know b_radford@yahoo.com
;
; please comment  

; set up the display
; graphics

If Windowed3D ()  ; see if the Graphics card supports 3D in  a window, if not go to fullscreen
	Graphics3D 640, 480, 0, 2
Else
	Graphics3D 640, 480, 0, 1
EndIf

SetBuffer BackBuffer () ; point all drawing to the hidden back buffer which we will eventually FLIP 

light=CreateLight()
cam=CreateCamera()
MoveEntity cam,20,10,-20					 

; VARIABLE LIST
Gravity# = .01
Friction# = .99



; End OF VARIABLE LIST


Gosub create_objects 
Gosub define_types
y#=20

Repeat


For thing.ball = Each ball  ; cycle through all the "balls" and set the position

thing\posy#=thing\posy#+thing\yvel#  ;  yposition = yposition + Velocity
thing\yvel#=thing\yvel#-gravity#     ;  velocity = velocity - gravity

; if the ball hits the ground, convert the velocity value to a positive number with the ABS() function
; and multiply it by the balls elasticity value to dissipate some of the energy. .1=flab 1.1=flubber 
If thing\posy#<1 Then thing\yvel#=Abs(thing\yvel#) * thing\elasticity# 

PositionEntity thing\entity,thing\posx#,thing\posy#,thing\posz#

Next





RenderWorld
UpdateWorld
Text 0,0, y#
Flip

Until KeyHit(1)=1 ; keep looping (repeating) until the Escape key is hit


; *************************************************
.create_objects ; label for the gosub command to find this subroutine 
ballmodel=CreateSphere(6)
;HideEntity ballmodel
PositionEntity ballmodel,0,50,0
plane=CreatePlane() ; CreatePlane ( [sub_divs][,parent] ) 
EntityColor plane,240,5,5

Return ; 
; **************************************************

.define_types

Type ball
Field posx#,posy#,posz#
Field entity
Field mass,weight,size
Field xvel#,yvel#,zvel#
Field elasticity#,alpha#

End Type


For x=1 To 8				; number of balls (8x8=64)
For z=1 To 8
thing.ball = New ball
thing\entity = CopyEntity(ballmodel) 
thing\posx# = x*4                       ; set the x position
thing\posy# = 30 				; 
thing\posz# = z*4						; set the z positon
thing\elasticity#=Rnd(.7,.89)
EntityColor thing\entity,Rnd(1,255),Rnd(1,255),Rnd(1,255)
PositionEntity thing\entity,thing\posx#,thing\posy#,thing\posz#
thing\alpha# = .6
EntityAlpha thing\entity,thing\alpha#

Next
Next



Return
