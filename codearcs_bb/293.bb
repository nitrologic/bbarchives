; ID: 293
; Author: Rob
; Date: 2002-04-15 05:43:41
; Title: Particle Systems
; Description: Simple particles showing how tiny code can achieve much

; Particle Test No.1 : using spray
; by Rob Cummings (rob@redflame.net)

; If there is sufficient demand I'll make more
; Each spawn returns the entity so you're able To texture And create a particle in one call!

Include "partyinclude.bb"

Graphics3D 640,480,16,2
camera=CreateCamera()
light=CreateLight()
MoveEntity camera,0,0,-100

;SYNTAX: spawn(x,y,z,size,scalefactor,life#,fadebias#,vx#,vy#,vz#,roll#,gravity#)

fire=LoadTexture("fire.png",2)
smoke=LoadTexture("smoke.jpg",2)


While Not KeyHit(1)
	
	;controlled spawn rate
	If time<0
		time=2
		EntityTexture spawn(-60,-60,0,1,0.1,120,0.02,Rnd(-0.1),Rnd(0.5)+0.5,Rnd(-1,1),Rnd(-5,5),0),smoke
		EntityTexture spawn(0,-60,0,2+Rnd(2),0,100,0.01,Rnd(-.2,.2),Rnd(1)+1,Rnd(-.2,.2),Rnd(-10,10),-.025),fire
		EntityTexture spawn(60,-60,0,Rnd(2)+0.5,-0.01,100,0.02,Cos(MilliSecs()/8),Rnd(1)+1,Sin(MilliSecs()/8),Rnd(-10,10),-.025),smoke
		EntityTexture spawn(-80,60,0,Rnd(1)+2,0.3,80,0.01,Rnd(1)+2,Sin(MilliSecs()/16)-1,Sin(MilliSecs()/8),-15,.05),fire
	Else
		time=time-1
	EndIf
	
	
	updateparty
	UpdateWorld
	RenderWorld
	Text 0,0,"numparticles:"+pno
	Flip 
	
Wend
End

;And HERE is the include file:

; PARTICLE PARTY INCLUDE
;
Global pno

Type party
	Field x#,y#,z#,ax#,ay#,az,vx#,vy#,vz#,gravity#,roll#,vroll#
	Field life#,delaytime#,fadebias#,scalefactor#,scale#,blendmode
	Field ent
End Type

Function spawn(x#,y#,z#,scale#,scalefactor#,life#,fadebias#,vx#,vy#,vz#,vroll#,gravity#)
	p.party=New party
	p\ent=CreateSprite()
	EntityBlend p\ent,2
	p\life=life
	p\vx=vx
	p\vy=vy
	p\gravity=gravity
	p\fadebias=fadebias
	p\vroll=vroll
	p\scalefactor=scalefactor
	p\scale=scale
	p\blendmode=blendmode
	PositionEntity p\ent,x,y,z
	ScaleSprite p\ent,scale,scale
	Return p\ent
End Function

Function updateparty()
	pno=0
	For p.party=Each party
		If p\life=0
			FreeEntity p\ent
			Delete p
		Else
			pno=pno+1
			p\life=p\life-1
			p\vy=p\vy+p\gravity
			p\roll=p\roll+p\vroll
			p\scale=p\scale+p\scalefactor
			If p\scale<0 Then p\scale=0

			ScaleSprite p\ent,p\scale,p\scale
			EntityAlpha p\ent,p\life*p\fadebias
			RotateSprite p\ent,p\roll
			MoveEntity p\ent,p\vx,p\vy,p\vz

		EndIf
	Next
End Function
