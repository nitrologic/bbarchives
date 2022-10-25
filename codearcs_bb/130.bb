; ID: 130
; Author: poopla
; Date: 2001-11-06 13:41:21
; Title: A tutorial in the basics of 3d.
; Description: This will get you started in the real of walking around in 3d.  Its just very well commented code.

Will make a textured terrain......     TERAIN AND MOVING ON TERRAIN..........

Graphics3D 640,480 ;this here sets the display
SetBuffer BackBuffer();sets the current buffer
light=CreateLight();creates a light DUH

ter=CreateTerrain(128) ; create a 128 x 128 
For x = 1 To 128;this will madify your terrain 
For y = 1 To 128;same here
    ;ModifyTerrain terrain,grid_x,grid_z,height#[,realtime]
ModifyTerrain ter,x,y,Rnd(0.0,1.0) 
Next
Next

ScaleEntity ter,20,50,20 ; make terrain bigger so we can see it


pivot=CreatePivot() ; pivot for camera rotations
camera=CreateCamera(pivot) ; camera with pivot as its parent entity


; make a texture ***
ptex=CreateTexture(16,16)
SetBuffer TextureBuffer(ptex)
Color 0,0,255
Rect 2,2,14,14
Color 0,255,0
Rect 8,8,10,10
ScaleTexture ptex,2,2
; ******************

EntityTexture ter,ptex ; paint the terrain with our texture


SetBuffer BackBuffer()


; main loop **********************************
While Not KeyDown( 1 )


If MouseDown(1)=1 Then speed#=speed#+.05
speed#=speed#*.99 ; friction

TurnEntity camera,MouseYSpeed()/6,0,0 ; turn camera up and down
TurnEntity pivot,0,-MouseXSpeed()/6,0 ; turn pivot left --right
MoveEntity pivot,0,0,speed#

xpos#=EntityX(pivot)
zpos#=EntityZ(pivot)
ypos#=TerrainY(ter,xpos,0,zpos)

PositionEntity pivot,xpos,ypos+10,zpos
MoveMouse 100,100

UpdateWorld 
RenderWorld
Flip
Wend
End


Learned all this from bradford.  So a big thamks to him :)
