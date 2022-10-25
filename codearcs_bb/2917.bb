; ID: 2917
; Author: Captain Wicker
; Date: 2012-02-04 12:09:13
; Title: Easy Collisions Example
; Description: An easy example of collision detection by me

AppTitle("Collisions Example")
Graphics3D 800,600,32,2
SetBuffer BackBuffer()

camera=CreateCamera()
light=CreateLight()

Const entity_sphere=1
Const entity_sphere2=2

sphere=CreateSphere(32)
PositionEntity sphere,-.85,0,5

sphere2=CreateSphere(32)
PositionEntity sphere2,.85,0,5

EntityType sphere,entity_sphere
EntityType sphere2,entity_sphere2

While Not KeyDown(1)


EntityColor sphere,Rnd(255),Rnd(255),Rnd(255)
MoveEntity sphere,0,.1,0
TurnEntity sphere,1,0,1

EntityColor sphere2,Rnd(255),Rnd(255),Rnd(255)
MoveEntity sphere2,0,.1,0
TurnEntity sphere2,1,0,-1

Collisions entity_sphere,entity_sphere2,1,2
Collisions entity_sphere2,entity_sphere,1,2

If EntityCollided(sphere,entity_sphere2) Then RuntimeError("Collisions Detected!!!")

If EntityCollided(sphere2,entity_sphere) Then RuntimeError("Collisions Detected!!!")

UpdateWorld
RenderWorld
Flip
Wend
