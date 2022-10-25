; ID: 655
; Author: AmazingJas
; Date: 2003-04-20 08:07:22
; Title: .b3d and with Skeletal Animation
; Description: Take a .b3d animated file, and play built in animations and skeletal deformations at the same time

Graphics3D 800,600
SetBuffer BackBuffer()
camera=CreateCamera()
CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()

;load in a .b3d and give it a handle
Global zombie=LoadAnimMesh("zombie.b3d")

;locate the bone in the model and give it a handle
Global waist=FindChild(zombie,"Joint8")

;a variable to keep track of the waistbones rotation
Global rotation

;position the model and start it running it's inbuild animation
MoveEntity zombie,0,0,5
ScaleEntity zombie, 0.5,0.5,0.5
Animate zombie,1,0.1,0,0

;mainloop
While Not KeyHit(1)

;move zombie toward or away from the camera for easy viewing
If KeyDown(31) Then MoveEntity zombie,0,0,1
If KeyDown(17) Then MoveEntity zombie,0,0,-1

;use a and d keys to keep track of the waist bone rotation
If KeyDown(30) Then rotation=rotation+1
If KeyDown(32) Then rotation=rotation-1

;updateworld
UpdateWorld

;turn the waistbone, this must be done AFTER UpdateWorld
TurnEntity waist,0,0,rotation

;Render/Flip/display the screen
RenderWorld
Flip

;and loop....
Wend
