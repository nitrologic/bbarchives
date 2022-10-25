; ID: 93
; Author: bradford6
; Date: 2001-10-06 17:10:09
; Title: Particle Emitter
; Description: free simple particle emitter



Graphics3D 800,600
; LOAD YOUR OWN PARTICle here
p=LoadSprite("particle.bmp",8)


SetBuffer BackBuffer ()
cam = CreateCamera ()
MoveEntity cam,0,0,-50

Type emitter
	Field xpos#,ypos#,zpos# 			; position of the emitter
	Field duration						; duration that the emitter emits in milliseconds
	Field winddirection					; 			
	Field windspeed#					;
	Field xvel#,yvel#,zvel#				; initial velocities of the particles
	Field emissionspeed#				;
	Field xrange#,yrange#,zrange#		; the width of the emitters "emission zone"
	Field partweight#					; the weight of each particle
	Field partelasticity#				; particles "bounciness"
	Field entity						; a TYPE handle to attach the entity to (sprite)
	Field partlife						;
	Field alpha#
	Field spangle#
End Type


box=CreateCube()
ScaleEntity(box,5,5,5)



For x = 1 To 100
b.emitter = New emitter
b\entity = CopyEntity(p)
b\alpha# = Rnd(.3,.9)
EntityAlpha(b\entity,b\alpha#)
Next 


; play with these variables to change the emitter
psx#= 100
psy#= 0
psz#= 100
vlx#= .1
vly#= .5
vlz#= .1
xrnge#= 5
yrnge#= 5
zrnge#= 5
dur= 4
widspdx#= 0
widspdz#= 0
wddir= 3
prtwt#= 3
elast#=  3
plife= 200

; LOOP


w#=-.01 ; temp windspeed changes???
Repeat

If KeyDown (203) TurnEntity cam,0,1,0 

If KeyDown (205) TurnEntity cam,0,-1,0 
If KeyDown(200) Then MoveEntity cam,0,0,.5
If KeyDown(201) Then MoveEntity cam,0,0,-.5

;emit(posx#,posy#,posz#,velx#,vely#,velz#,xrange#,yrange#,zrange#,duration,windspeed#,winddir,partwt#,elastic#, partlife)
;emit(100,0,100,.1,.5,.1,15,15,15,5,5,12,.01,.9,60)
If dir=0 Then w#=W#+.0001
If W#>.01 Then dir=1
If dir=1 Then w#=w#-.0001
If w#<-.01 Then dir=0


r=r+1
If r>360 Then r=0
psx#=Sin(r)*10
psz#=Cos(r)*10
PositionEntity(box,psx#,psy#,psz#)

emit(psx#,psy#,psz#,vlx#,vly#,vlz#,xrnge#,yrnge#,zrnge#,dur,w#,w#,wddir,prtwt#,elast#, plife)

;emit(100,0,100,.1,-1.3,.1,315,315,305,5,5,12,.01,.9,120) ; snow
;emit(100,0,100,-3,.3,.3,20,5,5,1,500,12,.01,.9,120)
;emit(100,0,100,.3,.3,.3,5,5,5,1,0,5,.01,.9,120)

UpdateWorld
RenderWorld

Flip

Until KeyHit (1)


Function emit(posx#,posy#,posz#,velx#,vely#,velz#,xrange#,yrange#,zrange#,duration,windspeedx#,windspeedz#,winddir,partwt#,elastic#, partlife)
For b.emitter = Each emitter
If b\partlife<0 
	b\xpos#=Rnd(posx#,posx#+xrange#)
	b\ypos#=Rnd(posy#,posy#+yrange#)
	b\zpos#=Rnd(posz#,posz#+zrange#)
	b\xvel#=Rnd(-velx#,velx#) b\yvel#=vely# b\zvel#=Rnd(-velx#,velz#)
	b\partweight#=partwt#
	b\partelasticity#=elastic#
	b\partlife=Rnd(partlife-40,partlife+40)
	sc#=Rnd(.1,3)
	ScaleSprite  (b\entity,sc#,sc#)
EndIf


b\xpos#=b\xpos#+b\xvel# b\xvel#=b\xvel#-.001+windspeedx# ; windresistance and vel
b\ypos#=b\ypos#+b\yvel# b\yvel#=b\yvel#-.01; gravity
b\zpos#=b\zpos#+b\zvel# b\zvel#=b\zvel#-.001+windspeedz#

b\partlife = b\partlife - 1

PositionEntity b\entity,b\xpos#,b\ypos#,b\zpos#
b\spangle#=b\spangle#+2 If b\spangle#>360 Then b\spangle#=1
RotateSprite b\entity,b\spangle#


Next


End Function

