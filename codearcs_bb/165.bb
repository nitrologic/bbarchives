; ID: 165
; Author: bradford6
; Date: 2001-12-17 18:41:34
; Title: Jumping 
; Description: 3D Jumping 

; Jumping with Smooth 3rd person Camera Example
; ----------------

camdistance=10

Graphics3D 800,600,3 ;rem setup the Graphics mode

light=CreateLight() ;rem create a light for our scene

Global barney=CreateCube() ;rem create a cube and call it barney 
PositionEntity barney,0,1,0
Global obpiv=CreatePivot(barney) ; attach a pivot to barney
MoveEntity obpiv,0,0,-camdistance ; move the pivot back a little adjust this for effect

Global cam=CreateCamera() ;rem create a camera and make barney the cube it's parent entity

plane= CreatePlane()
planetex=CreateTexture(64,64)

mirror=CreateMirror()
EntityTexture plane,planetex
EntityAlpha plane,.5
SetBuffer TextureBuffer(planetex)
Color 200,0,0
Rect 10,10,50,50
SetBuffer BackBuffer()


gravity#=.01
jumpvel#=.3
; main loop
While Not KeyDown( 1 )

If KeyDown (203) Then TurnEntity barney,0,1,0
If KeyDown(205) Then TurnEntity barney,0,-1,0
If KeyDown(200) Then speed#=speed#+.03
If KeyDown(208) Then speed#=speed#-.03
; only jump if the JUMPING flag is set to zero
If jumping=0
	If KeyDown(57) Then yvel#=jumpvel# jumping=1
EndIf
If jumping=1
	yvel#=yvel#-gravity#
	by#=by#+yvel#
EndIf

If by#<1 Then by#=1  yvel#=0 jumping=0


speed#=speed#*.99
MoveEntity barney,0,yvel#,speed#

smoothcam(obpiv,barney,20)


UpdateWorld
RenderWorld ;rem render the world
Flip ; flip from the back buffer to the front buffer 

Wend

End

Function smoothcam(pivot,target,camspeed)


curx#=EntityX(cam)
curz#=EntityZ(cam)
destx#=EntityX(pivot,True)
destz#=EntityZ(pivot,True)

curx#=curx#+((destx#-curx#)/camspeed)
curz#=curz#+((destz#-curz#)/camspeed)
cury#=EntityY(target) + 5

PositionEntity cam,curx#,cury#,curz#

PointEntity cam,target
End Function
