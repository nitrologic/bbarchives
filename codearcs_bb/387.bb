; ID: 387
; Author: Vorderman
; Date: 2002-08-07 04:00:49
; Title: Rigid Body Physics System
; Description: A simple particle-based rigid body physics system.

AppTitle "Rigid Body Physics Test"
Graphics3D 800,600,32,2
SetBuffer BackBuffer()


;-------------- CONSTANTS ---------------------------
Const gameFPS = 50 ;frame limiting
Const GRAVITY# = -0.0098 ;obviously gravity
Const DAMPING# = 0.98 ;global damping factor
Const MAX_PARTICLES = 10 ;max number of particles allowed
Const MAX_SPRINGS = 100 ;max number of springs allowed
Const TENSION_MUTIPLIER# = 160 ;multiplier to obtain corretc tension colours


;-------------- GLOBALS ---------------------------
Global SHAPE = 1 ;which shape to use
Global SPRINGSTRENGTH# = 0.3 ;stretchy length strength of springs
Global camang# = 0.0 ;camera y axis angle
Global number_of_springs = 0 ;number of springs in use
Global number_of_particles = 0 ;number of patricles in use
Global FLAG_show_particle_numbers = False ;shall particle ID numbers be shown?
Global FLAG_show_spring_numbers = False ;shall spring ID numbers be shown?
Global FLAG_show_all_info = False ;shall all info be shown in a table form?
Global camera1 
Global floormesh
Global light1



;-------------- FONTS ---------------------------
Global FONT_arial20 = LoadFont("Arial",20,True,False,False)
Global FONT_arial12 = LoadFont("Arial",12,False,False,False)


;-------------- CUSTOM TYPES ---------------------------
;data type for particle
Type TYPE_particle
Field mesh ;physical object
Field x#,y#,z# ;coords
Field xs#,ys#,zs# ;velocities
Field locked ;lets you lock particle position
End Type

;data type for spring
Type TYPE_spring
Field connect1 ;connection ID 1
Field connect2 ;connection ID 2
Field length# ;resting length of the spring
Field currentlength# ;actual length of the spring at any time
Field peaktension# ;max tension the spring has managed to achieve
End Type



;-------------- GLOBAL ARRAYS ---------------------------
Dim particles.TYPE_particle(MAX_PARTICLES)
Dim springs.TYPE_spring(MAX_SPRINGS)

For p=1 To MAX_PARTICLES
particles(p) = New TYPE_particle
Next

For s=1 To MAX_SPRINGS
springs(s) = New TYPE_spring
Next



;-------------- INITIALISE WORLD ---------------------------
FUNC_reset()
FUNC_make_floor()
framePeriod = 1000 / gameFPS
frameTime = MilliSecs () - framePeriod
MoveMouse 400,300



;-------------- M A I N L O O P ---------------------------
While Not KeyHit(1)

;frame limiting
Repeat
frameElapsed = MilliSecs () - frameTime
Until frameElapsed
frameTicks = frameElapsed / framePeriod
frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod)


;game logic loop
For frameLimit = 1 To frameTicks

If frameLimit = frameTicks Then CaptureWorld
frameTime = frameTime + framePeriod


;for testing - cursor keys add forces to certain particles
If KeyDown(200) particles(1)\ys# = particles(1)\ys# + 0.1
If KeyDown(208) particles(1)\ys# = particles(1)\ys# - 0.1
If KeyDown(203) particles(1)\xs# = particles(1)\xs# - 0.1
If KeyDown(205) particles(1)\xs# = particles(1)\xs# + 0.1

;flags for displaying spring and particle ID numbers
; a = all info
; s = springs info
; p = particle info
; l = lock particle 4
If KeyHit(25) FLAG_show_particle_numbers = Not(FLAG_show_particle_numbers)
If KeyHit(31) FLAG_show_spring_numbers = Not(FLAG_show_spring_numbers)
If KeyHit(30) FLAG_show_all_info = Not(FLAG_show_all_info)
If KeyHit(38) particles(4)\locked = Not(particles(4)\locked)

;R key for reset
If KeyHit(19) 
FUNC_reset()
FUNC_make_floor()
EndIf

;(= and -) or (+ and -) keys for adjusting spring strengths
If KeyDown(13) Or KeyDown(78) SPRINGSTRENGTH# = SPRINGSTRENGTH# + 0.01
If KeyDown(12) Or KeyDown(74) SPRINGSTRENGTH# = SPRINGSTRENGTH# - 0.01
If (SPRINGSTRENGTH# < 0.02) SPRINGSTRENGTH# = 0.02
If (SPRINGSTRENGTH# > 2.0) SPRINGSTRENGTH# = 2.0

;F1 to F4 keys to change shapes
If KeyHit(59) SHAPE = 1 : FUNC_reset : FUNC_make_floor()
If KeyHit(60) SHAPE = 2 : FUNC_reset : FUNC_make_floor()
If KeyHit(61) SHAPE = 3 : FUNC_reset : FUNC_make_floor()
If KeyHit(62) SHAPE = 4 : FUNC_reset : FUNC_make_floor()

;cycle through all particles, check for ground plane collision and rebound
For pp.TYPE_particle = Each TYPE_particle
If (pp\y# <= 0.0) And (pp\ys# < 0.0) 
pp\ys# = - (pp\ys# * DAMPING#)
pp\y# = 0.0
EndIf
Next


;cycle through the springs, calculate and apply required forces
For s=1 To number_of_springs

;obtain linked particle IDs
p1 = springs(s)\connect1
p2 = springs(s)\connect2

;work out distance between particles
svx# = particles(p2)\x# - particles(p1)\x#
svy# = particles(p2)\y# - particles(p1)\y#
svz# = particles(p2)\z# - particles(p1)\z#
length# = Sqr#((svx# * svx#) + (svy# * svy#) + (svz# * svz#))

;store the current spring extension for use later
springs(s)\currentlength# = length#

;work out spring force acting in on the first particle
normallength# = springs(s)\length#
forcescaler# = (length# - normallength#) / normallength#

svx# = svx# * (1.0 / length#)
svy# = svy# * (1.0 / length#)
svz# = svz# * (1.0 / length#)

fvx# = svx# * forcescaler#
fvy# = svy# * forcescaler#
fvz# = svz# * forcescaler#

fvx# = fvx# * SPRINGSTRENGTH#
fvy# = fvy# * SPRINGSTRENGTH#
fvz# = fvz# * SPRINGSTRENGTH#

;negate force to act in opposite direction on the second particle
fvx2# = -fvx#
fvy2# = -fvy#
fvz2# = -fvz#

;apply force to particle 1
If Not (particles(p1)\locked)
particles(p1)\xs# = particles(p1)\xs# + fvx#
particles(p1)\ys# = particles(p1)\ys# + fvy#
particles(p1)\zs# = particles(p1)\zs# + fvz#
EndIf

;apply force to particle 2
If Not (particles(p2)\locked)
particles(p2)\xs# = particles(p2)\xs# + fvx2#
particles(p2)\ys# = particles(p2)\ys# + fvy2#
particles(p2)\zs# = particles(p2)\zs# + fvz2#
EndIf

Next


;apply gravity, damp particle velocities and update positions
For p=1 To number_of_particles

;check for locked particle
If Not (particles(p)\locked)
particles(p)\ys# = particles(p)\ys# + GRAVITY#

;some damping to slow particles down over time 
particles(p)\xs# = particles(p)\xs# * DAMPING#
particles(p)\ys# = particles(p)\ys# * DAMPING#
particles(p)\zs# = particles(p)\zs# * DAMPING#

particles(p)\x# = particles(p)\x# + particles(p)\xs#
particles(p)\y# = particles(p)\y# + particles(p)\ys#
particles(p)\z# = particles(p)\z# + particles(p)\zs#
EndIf

Next


;update the particle positions 
For p=1 To number_of_particles
PositionEntity particles(p)\mesh , particles(p)\x#, particles(p)\y#, particles(p)\z#
Next 


;allow user to rotate camera
camang# = camang# + MouseXSpeed()
PositionEntity camera1,0,0,0
RotateEntity camera1,0,camang#,0
MoveEntity camera1,0,10,-20
TurnEntity camera1,30,0,0
MoveMouse 400,300


;update game world logic
UpdateWorld

Next


;render
RenderWorld frameTween


;display particle IDs if required
SetFont FONT_arial12
If FLAG_show_particle_numbers
For p=1 To number_of_particles
x1# = particles(p)\x#
y1# = particles(p)\y#
z1# = particles(p)\z#
CameraProject camera1,x1#,y1#,z1#
c1x# = ProjectedX()
c1y# = ProjectedY()
Color 255,255,255
Text c1x#+5,c1y#,p
Next
EndIf


;display spring shadows
For s=1 To number_of_springs
x1# = particles(springs(s)\connect1)\x#
y1# = particles(springs(s)\connect1)\y#
z1# = particles(springs(s)\connect1)\z#
x2# = particles(springs(s)\connect2)\x#
y2# = particles(springs(s)\connect2)\y#
z2# = particles(springs(s)\connect2)\z#
CameraProject camera1,x1#,0,z1#
shad1x# = ProjectedX()
shad1y# = ProjectedY()
CameraProject camera1,x2#,0,z2#
shad2x# = ProjectedX()
shad2y# = ProjectedY()
Color 0,0,0
Line shad1x#,shad1y#,shad2x#,shad2y#
Next


;display the springs, taking into account their tension and colouring correctly
For s=1 To number_of_springs

x1# = particles(springs(s)\connect1)\x#
y1# = particles(springs(s)\connect1)\y#
z1# = particles(springs(s)\connect1)\z#

x2# = particles(springs(s)\connect2)\x#
y2# = particles(springs(s)\connect2)\y#
z2# = particles(springs(s)\connect2)\z#

CameraProject camera1,x1#,y1#,z1#
c1x# = ProjectedX()
c1y# = ProjectedY()

CameraProject camera1,x2#,y2#,z2#
c2x# = ProjectedX()
c2y# = ProjectedY()

;find the desired length of this spring
lnorm# = Abs(springs(s)\length#)

;find out the current length
lcurr# = Abs(springs(s)\currentlength#)

colour_r = 0
colour_g = 255

;calculate and display the tension in the spring
If (lcurr# > lnorm#)
tension# = (lcurr# - lnorm#)

colour_r = (tension# * TENSION_MUTIPLIER# )
If (colour_r > 255) colour_r = 255

colour_g = 255 - (tension# * TENSION_MUTIPLIER#)
If (colour_g < 0) colour_g = 0

EndIf

;update peak tension if required
If ( (tension# * TENSION_MUTIPLIER#) > (springs(s)\peaktension#) )
springs(s)\peaktension# = (tension# * TENSION_MUTIPLIER#)
EndIf

;draw the spring as a line 
Color colour_r , colour_g , 0 
Line c1x#,c1y#,c2x#,c2y#

;do we need to show global info for this spring? 
If FLAG_show_all_info
Text 10,(10*s)+10,"SPRING:"+s
Text 70,(10*s)+10,"TENSION:"+Int(tension# * TENSION_MUTIPLIER#)
Text 140,(10*s)+10,"PEAK:"+Int(springs(s)\peaktension#)
EndIf

;display spring IDs if required
If FLAG_show_spring_numbers
centrex# = (x1# + x2#) / 2.0
centrey# = (y1# + y2#) / 2.0
centrez# = (z1# + z2#) / 2.0
CameraProject camera1,centrex#,centrey#,centrez#
centrex# = ProjectedX()
centrey# = ProjectedY()
Text centrex#+5,centrey#,s
EndIf

Next


;calculate FPS
If (frameTicks>0) 
FPS = gameFPS/frameTicks
Else
FPS = gameFPS
EndIf


;on-screen text display including key commands and FPS counter
Color 255,255,255
SetFont FONT_arial20
Text 650,10,"FPS:"+FPS
SetFont FONT_arial12
Text 650,40,"A : show all info"
Text 650,55,"P : show particle IDs"
Text 650,70,"S : show spring IDs"
Text 650,85,"L : lock particle 4"
Text 650,100,"R : Reset object"
Text 650,125,"Spring Strength : "+SPRINGSTRENGTH#
Text 650,140,"+ : increase spring strength"
Text 650,155,"- : decrease spring strength"
Text 650,180,"Cursors apply force to Particle 1"
Text 650,195,"Rotate camera with mouse"

Color 255,255,255
SetFont FONT_arial20
Text 280,10,"SELECT SHAPE WITH F1 F2 F3 F4"
Select SHAPE
Case 1
Text 300,30,"SHAPE 1 : Soft Wobbly Cube"
Case 2
Text 300,30,"SHAPE 2 : Rigid Cube"
Case 3
Text 300,30,"SHAPE 3 : Small Diamond"
Case 4
Text 300,30,"SHAPE 4 : String"
End Select 

;flip buffers 
Flip

Wend

End








Function FUNC_make_floor()

floormesh=CreateMesh()
floorsurf=CreateSurface(floormesh)
EntityFX floormesh,2

vert = 0
SCALE# = 4.0
col=True

For z=-3 To 2
For x=-3 To 2

AddVertex floorsurf,x*SCALE#,0,z*SCALE#
AddVertex floorsurf,(x+1)*SCALE#,0,z*SCALE#
AddVertex floorsurf,(x+1)*SCALE#,0,(z+1)*SCALE#
AddVertex floorsurf,x*SCALE#,0,(z+1)*SCALE#

AddTriangle floorsurf,vert,vert+2,vert+1
AddTriangle floorsurf,vert,vert+3,vert+2

If (col) 
colour=100
Else
colour=0
EndIf

VertexColor floorsurf,vert,colour,colour,colour
VertexColor floorsurf,vert+1,colour,colour,colour
VertexColor floorsurf,vert+2,colour,colour,colour
VertexColor floorsurf,vert+3,colour,colour,colour

vert = vert + 4
col = Not(col)
Next

col = Not(col)
Next

UpdateNormals floormesh 
PositionEntity floormesh,0,0,0

End Function








Function FUNC_reset()

ClearWorld()

Select SHAPE
Case 1
Restore DATA_TEST1
Case 2
Restore DATA_TEST2
Case 3
Restore DATA_TEST3
Case 4 
Restore DATA_TEST4
End Select


Read number_of_particles
For p=1 To number_of_particles
Read particles(p)\x#
Read particles(p)\y#
Read particles(p)\z#
Read particles(p)\locked
particles(p)\xs# = 0.0
particles(p)\ys# = 0.0
particles(p)\zs# = 0.0
particles(p)\mesh = CreateSphere(4)
EntityColor particles(p)\mesh,255,255,255
r# = 0.5
FitMesh particles(p)\mesh,-(r#/2),-(r#/2),-(r#/2),r#,r#,r#
PositionEntity particles(p)\mesh ,particles(p)\x#,particles(p)\y#,particles(p)\z#,True
Next

Read number_of_springs
For s=1 To number_of_springs
Read springs(s)\connect1
Read springs(s)\connect2
dx# = particles(springs(s)\connect1)\x# - particles(springs(s)\connect2)\x#
dy# = particles(springs(s)\connect1)\y# - particles(springs(s)\connect2)\y#
dz# = particles(springs(s)\connect1)\z# - particles(springs(s)\connect2)\z#
springs(s)\length# = Sqr#((dx# * dx#) + (dy# * dy#) + (dz# * dz#))
Next

;game camera
camera1=CreateCamera()
CameraViewport camera1,0,0,800,600
PositionEntity camera1,0,10,-20
CameraClsColor camera1,50,50,100
RotateEntity camera1,30,0,0

;lighting
AmbientLight 150,150,150
light1 = CreateLight(1)
PositionEntity light1,500,500,-500
RotateEntity light1,45,45,0

End Function



;a soft cube, prone to twisting and flexing
.DATA_TEST1
;number_of_particles
Data 8 

;particle coords locked flag
Data -2,10,2 ,0
Data 2,10,2 ,0
Data 2,10,-2 ,0
Data -2,10,-2 ,0
Data -2,14,2 ,0
Data 2,14,2 ,0
Data 2,14,-2 ,0
Data -2,14,-2 ,0

;number of springs
Data 20

;spring connections
Data 1,2 
Data 2,3
Data 3,4
Data 4,1
Data 5,6
Data 6,7
Data 7,8
Data 8,5
Data 1,5
Data 2,6
Data 3,7
Data 4,8
Data 5,3
Data 8,2
Data 6,4
Data 7,1
Data 1,3
Data 2,4
Data 5,7
Data 6,8

;second cube, very rigid and unbreakable (I think)
.DATA_TEST2
;number_of_particles
Data 8

;particle coords locked flag
Data -2,10,2 ,0
Data 2,10,2 ,0
Data 2,10,-2 ,0
Data -2,10,-2 ,0
Data -2,14,2 ,0
Data 2,14,2 ,0
Data 2,14,-2 ,0
Data -2,14,-2 ,0

;number of springs
Data 24

;spring connections
Data 1,2 
Data 2,3
Data 3,4
Data 4,1
Data 5,6
Data 6,7
Data 7,8
Data 8,5
Data 5,1
Data 6,2
Data 7,3
Data 8,4
Data 1,3
Data 2,4
Data 5,7
Data 6,8
Data 4,5
Data 1,8
Data 5,2
Data 6,1
Data 6,3
Data 2,7
Data 8,3
Data 7,4

;a nice diamond shape
.DATA_TEST3
Data 6
Data 0,8,0 ,0
Data -1,10,0 ,0
Data 0,10,-1 ,0
Data 1,10,0 ,0
Data 0,10,1 ,0
Data 0,12,0 ,0

Data 11
Data 1,2
Data 1,3
Data 1,4
Data 1,5
Data 6,2
Data 6,3
Data 6,4
Data 6,5
Data 1,6
Data 2,4
Data 3,5

;next some string, fixed at the top and long enough to dangle to the ground
.DATA_TEST4
Data 9
Data -16,10,0 ,0
Data -14,10,0 ,0
Data -12,10,0 ,0
Data -10,10,0 ,0
Data -8,10,0 ,0
Data -6,10,0 ,0
Data -4,10,0 ,0
Data -2,10,0 ,0
Data 0,10,0 ,1

Data 8
Data 1,2
Data 2,3
Data 3,4
Data 4,5
Data 5,6
Data 6,7
Data 7,8
Data 8,9
