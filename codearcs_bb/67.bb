; ID: 67
; Author: MadJack
; Date: 2001-10-02 08:24:21
; Title: Vehicle pitch and roll without child/pivot entities
; Description: Looks at a mesh as a single object on a terrain and calculates the pitch and roll

; my car test - Phil Jones
; a look at calculating vehicle pitch and roll (on a terrain) without 
; child/pivot entities

;set up screen,light & camera
Graphics3D 800,600,16
SetBuffer BackBuffer()
light = CreateLight(1)

;create terrain
terrain = LoadTerrain("heightmap_256.bmp")
TerrainDetail terrain, 3000, True

ScaleEntity terrain, 50,1000,50
PositionEntity terrain,-200,0,-200
terraintex = LoadTexture("coolfloor2.bmp")

ScaleTexture terraintex,.4,.4
TerrainShading terrain,1
EntityTexture terrain,terraintex

;create car/plane/tank whatever and centre it local co-ords
car = LoadMesh("747.x")
ScaleMesh car,100,100,100

carlength# = MeshDepth#(car)/2 ; get length from centre to front of car
carwidth# = MeshWidth#(car)/2 ; get length from centre to left or right of car
carheight# = MeshHeight#(car)/2: ; get height from centre To top Or bottom of car
meshh = MeshHeight#(car)/2
FitMesh car, 0-carwidth,0,0-carlength,carwidth*2,carheight*2,carlength*2; just some general recentring to have car sit on plane

pitch# = 0
x# = 200
y# = 600
z# = 480
speed# = 0

frontheight#=0 ; height of terrain at midpoint front of vehicle
backheight#=0 ; height of terrain at midpoint back of vehicle
leftheight#=0 ; height of terrain at midpoint left side of vehicle
rightheight#=0 ; height of terrain at midpoint right side of vehicle
th# = 0 ; height of terrain at centre of vehicle (this will averaged from front/back/left/right)

PositionEntity car,x,y,z ; set up initial pos of car and camera
;RotateEntity car,0,180,0,1

;these are just for visual ref - not needed in calculation of rotations
pop = CreateCube()
ScaleEntity pop,5,400,5
pfront = CreateCube()
ScaleEntity pfront,5,5,5
pback = CreateCube()
ScaleEntity pback,5,5,5
pleft = CreateCube()
ScaleEntity pleft,5,5,5
pright = CreateCube()
ScaleEntity pright,5,5,5

;setup camera
camera = CreateCamera()
PositionEntity camera ,300,350,100
PointEntity camera ,car
CameraRange camera,1,900000


;MAIN LOOP +++++++++++++++++++++++++++++++++

While Not KeyHit(1)

; get current location and overall height of car
x# = EntityX (car)
z# = EntityZ (car)
y# = EntityY (car)

; check for keypress to alter car speed (A,Z)
speed = speed -.5*(KeyDown(208) And speed > -10 ) + .5*(KeyDown(200) And speed < 10 )

; check for keypress to turn car left/right
turn = (KeyDown(203)) - (KeyDown(205))
TurnEntity car,0,turn,0
yaw# = EntityYaw(car)



; CALCULATE PITCH/ROLL ++++++++++++++++++++++++++++++++
;work out terrainheights at front/back/left/right of car
anglenew# = yaw -90  ; what must be added to yaw to calculate front of vehicle's global x,z co-ords
xnew# = x+Cos(anglenew)*carlength
znew# = z+Sin(anglenew)*carlength
frontheight# = TerrainY(terrain,xnew,y,znew); get the height at new x,y,z
PositionEntity pfront,xnew,frontheight,znew ; a visual marker only - not neccessary for calculations

anglenew# = yaw +90 ; what must be added to yaw to calculate back of vehicle's global x,z co-ords
xnew# = x+Cos(anglenew)*carlength
znew# = z+Sin(anglenew)*carlength
backheight = TerrainY(terrain,xnew,y,znew)
PositionEntity pback,xnew,backheight,znew

anglenew# = yaw  ; what must be added to yaw to calculate midleft of vehicle's global x,z co-ords
xnew# = x+Cos(anglenew)*carwidth
znew# = z+Sin(anglenew)*carwidth
Leftheight = TerrainY(terrain,xnew,y,znew)
PositionEntity pleft,xnew,leftheight,znew

anglenew# = yaw + 180 ; what must be added to yaw to calculate midright of vehicle's global x,z co-ords
xnew# = x+Cos(anglenew)*carwidth
znew# = z+Sin(anglenew)*carwidth
rightheight = TerrainY(terrain,xnew,y,znew)
PositionEntity pright,xnew,rightheight,znew

pitchx# = carlength*2 ; need complete length of car for x
pitchy# = (backheight - frontheight); the height dif between the front and rear of car
pitch = ATan2(pitchx,pitchy); atan2 will return the angle from 0,0 to x,y

rollx# = carwidth*2
rolly# = (rightheight - leftheight) ; the height dif between the left and right of car
roll# = ATan2(rollx,rolly); atan2 will return the angle from 0,0 to x,y

RotateEntity car, pitch-90,yaw,roll-90 ; rotate car with offsets to adjust Atan2's calculation

th=(frontheight+backheight+leftheight+rightheight)/4; get average of terrain heights for cars overall height
PositionEntity car,x,th,z ; adjust car height 
MoveEntity car,0,0,speed

PositionEntity pop,x,th,z ; this is just a central axis 'pole' to help see rotation angles 
RotateEntity pop,pitch-90,yaw,roll-90

PositionEntity camera , x+400,y+300,z+200
PointEntity camera,car

UpdateWorld
RenderWorld
Flip

Wend
Stop
