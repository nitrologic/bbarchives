; ID: 2023
; Author: stu
; Date: 2007-05-30 11:44:52
; Title: Orbital gravity
; Description: Simple orbital gravity

;
; Gravity demo
;

;
; Set up
; 

Graphics3D 1024,768

;
; Global Variables - needs some tidying
;
Global angle#, radius#
Global earthx#,earthy#,earthvelx#,earthvely#
Global earthangle#,earthradius#,earthrot#
Global earthforce#,eaccelx#,eaccely#
Global eradius#, eangle#

SetBuffer BackBuffer()
;
; camera
;
camera = CreateCamera()
CameraViewport camera,0,0,1024,768
CameraRange camera,2,2000
PositionEntity camera,0,-350,-100
RotateEntity camera,-40,0,0

AmbientLight(200,100,100)

light1=CreateLight(2)
light2=CreateLight(2)
light3=CreateLight(2)
light4=CreateLight(2)

LightColor light1,200,100,100
LightColor light3,200,100,100
LightColor light2,200,20,20
LightColor light4,200,20,20

PositionEntity light1,20,0,200
PositionEntity light2,0,-20,200
PositionEntity light3,-20,0,200
PositionEntity light4,0,20,200

SeedRnd MilliSecs()
;
; Particle type
;
Type Par
	Field xpos#,ypos#
	Field xvel#,yvel#
	Field xp
	Field mass#
End Type

;
; set up scenario initial conditions
;
Global sun = CreateSphere(32)
Global Earth = CreateSphere(32)

ScaleEntity sun,20,20,20
ScaleEntity Earth,5,5,5
EntityColor sun,255,255,0
EntityColor Earth,50,200,255
;suntex = LoadTexture("sun.jpg",1)
;earthtex = LoadTexture("earth.jpg",1)
sunangle# = 90.0
Const SUNMASS = 10000
Const EARTHMASS = 1000
Const Fieldx = 200
Const Fieldy = 200
; no of particles
Const Parcnt = 1000
Const G# = 0.000667
; Start limit distance from Sun
Const START = 80
earthx = 100
earthy = 100
earthvelx = 0.2
earthvely = -0.1

PositionEntity sun,0,0,200


PositionEntity Earth,earthx,earthy,200

;Create the Texture
width=1024
sptex = CreateTexture(width,width,1+8)
SetBuffer TextureBuffer(sptex)
For a = 1 To 200
	Plot Rand(0,width-1),Rand(0,width-1)
Next
SetBuffer BackBuffer()
TextureBlend sptex,5

;Create the Sphere
spbox = CreateSphere(5)
ScaleEntity spbox,1000,1000,1000
EntityTexture spbox,sptex
ScaleTexture sptex,.25,.5
EntityFX spbox,1
FlipMesh spbox
EntityOrder spbox,99999

;
; function to create particle
;


Function CreatePar()
	p.Par = New par
	p\xpos# = Rand(-Fieldx,Fieldx)
	p\ypos# = Rand(-Fieldy,Fieldy)
	If p\xpos > 0.0 And p\ypos > 0.0 And p\xpos < START And p\ypos < START 
		p\xpos = START
		p\ypos = START
	ElseIf p\xpos < 0.0 And p\ypos < 0.0 And p\xpos > -START And p\ypos > -START
		p\xpos = -START
		p\ypos = -START
	ElseIf p\xpos > 0.0 And p\ypos < 0.0 And p\xpos < START And p\ypos > -START
		p\xpos = START
		p\ypos = -START
	ElseIf p\xpos < 0.0 And p\ypos > 0.0 And p\xpos > -START And p\ypos < START
		p\xpos = -START
		p\ypos = START
	EndIf

	If p\xpos > 0.0 And p\ypos > 0.0 
		p\xvel = Rand(1,1.2)/10.0
		p\yvel = Rand(-1.2,-1)/10.0
	ElseIf p\xpos > 0.0 And p\ypos < 0.0
		p\xvel = Rand(-1.2,-1)/10.0
		p\yvel = Rand(-1.2,-1)/10.0
	ElseIf p\xpos < 0.0 And p\ypos < 0.0
		p\xvel = Rand(-1.2,-1)/10.0
		p\yvel = Rand(1,1.2)/10.0
	Else
		p\xvel = Rand(1,1.2)/10.0
		p\yvel = Rand(1,1.2)/10.0
	EndIf	
	p\mass = 5.0
	p\xp = CreateSphere()
	EntityColor p\xp,10,10,10
	EntityShininess p\xp,1.0
End Function

;
; Update particle position
;
Function UpdatePar(p.Par)
	Local sunforce#,accelx#,accely#
	Local eforce#,accelex#,acceley#
	AngleSun p
	If radius < 20.0 Or radius > 500.0
		p\xvel = 0.0
		p\yvel = 0.0
		PositionEntity p\xp,0,0,200	
		Delete p
	Else
		Sunforce = (G * SUNMASS * p\mass) / (radius*radius)
		AngleEarth p
		If eradius < 5.0
			p\xvel = 0.0
			p\yvel = 0.0
			PositionEntity p\xp,0,0,200
			Delete p
		Else 	
			eforce = (G * EARTHMASS * p\mass) / (eradius*eradius)
			accelx=-(sunforce/p\mass) * Cos(angle) 
			accely=-(sunforce/p\mass) * Sin(angle)
			accelex =  -(eforce/p\mass) * Cos(eangle)
			acceley =  -(eforce/p\mass) * Sin(eangle)
			accelx = accelx + accelex
			accely = accely + acceley
			p\xvel = p\xvel+accelx
			p\yvel = p\yvel+accely 
			p\xpos=p\xpos+p\xvel
			p\ypos=p\ypos+p\yvel
			PositionEntity p\xp,p\xpos,p\ypos,200
		EndIf
	EndIf
End Function

;
; Get angle + radius
; 
Function AngleSun(p.Par)
	angle = ATan2(p\ypos,p\xpos)
	radius = EntityDistance(sun,p\xp) 
End Function

Function AngleEarth(p.Par)
	eangle = ATan2(p\ypos-earthy,p\xpos-earthx)
	eradius = EntityDistance(Earth,p\xp)
End Function


	

	
;
; Create some particles
;
For i = 1 To Parcnt
	CreatePar
Next

;
; Main loop
;

While Not KeyHit(1)

;EntityTexture sun,suntex
;EntityTexture Earth,earthtex
;sunangle = sunangle + 0.1
;earthrot = earthrot+ 0.001
;RotateTexture suntex,sunangle
;TurnEntity Earth,0,earthrot,0

;
; Move Earth
;
	earthangle = ATan2(earthy,earthx)
	earthradius = Sqr(earthx*earthx+earthy*earthy)
	earthforce = (G * SUNMASS * EARTHMASS) / (earthradius*earthradius)
	eaccelx=-(earthforce/EARTHMASS) * Cos(earthangle)
	eaccely=-(earthforce/EARTHMASS) * Sin(earthangle)
	earthvelx = earthvelx+eaccelx
	earthvely = earthvely+eaccely 
	earthx=earthx+earthvelx
	earthy=earthy+earthvely
	PositionEntity Earth,earthx,earthy,200

;
; Move particles
;

For p.Par = Each Par	
	UpdatePar p
Next
	
;
; Move Camera
; Cursor keys + A = zoom in , Z = zoom out
; , roll left . roll right
;
	
If KeyDown(200) Then
MoveEntity camera,0,3.6,0
TurnEntity camera,1,0,0
EndIf

If KeyDown(208) Then
MoveEntity camera,0,-3.6,0
TurnEntity camera,-1,0,0
EndIf

If KeyDown(203) Then
MoveEntity camera,3.6,0,0 
TurnEntity camera,0,1.0,0
EndIf

If KeyDown(205) Then
MoveEntity camera,-3.6,0,0
TurnEntity camera,0,-1.0,0
EndIf

If KeyDown(30) Then
MoveEntity camera,0,0,1.0
EndIf

If KeyDown(44) Then
MoveEntity camera,0,0,-1.0
EndIf

If KeyDown(51) Then
TurnEntity camera,0,0,1
EndIf

If KeyDown(52) Then
TurnEntity camera,0,0,-1
EndIf

UpdateWorld
RenderWorld

Flip

Wend
End
