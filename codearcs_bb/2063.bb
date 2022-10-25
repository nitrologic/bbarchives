; ID: 2063
; Author: Ben(t)
; Date: 2007-07-12 13:58:29
; Title: fade in/out
; Description: a simple sprite that can be used to fade in and out

Graphics3D 640,480,32,2

pivot=CreatePivot()
cam=CreateCamera(pivot)
PositionEntity cam,0,5,-10,1

fade=CreateSprite(cam)
ScaleSprite fade,2,2
PositionEntity fade,0,0,1.1
EntityColor fade,0,0,0

cube=CreateCube()
ScaleEntity cube,5,.1,5
EntityColor cube,255,0,0

ball=CreateSphere(32)
MoveEntity ball,1,0,1
EntityColor ball,0,0,255

light=CreateLight()
MoveEntity light,5,8,5

While Not KeyHit(1)
PointEntity cam,cube
TurnEntity pivot,0,1,0

EntityAlpha fade,alpha#
If KeyDown(13) Then alpha#=alpha#+.01
If KeyDown(12) Then alpha#=alpha#-.01
If alpha#<0 Then alpha#=0
If alpha#>1 Then alpha#=1

RenderWorld
Text 0,0,alpha#
Text 0,10,"+ to increase dark and - to decrease"

Flip
Wend
End
