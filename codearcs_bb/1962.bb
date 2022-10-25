; ID: 1962
; Author: Matt Merkulov
; Date: 2007-03-15 10:46:25
; Title: Motion blur FX
; Description: 14 real-time 3D motion blur FX presets

;14 real-time 3D motion blur FX presets by Matt Merkulov

; The size of a structure, number of effect, the order of a portrayal(-1 or 1)
Const texsize=1024, fx=7, o =-1

Graphics3D 800,600,32

cam=CreateCamera()
PositionEntity cam, 0,0,-6
RotateEntity CreateLight(), 45,0,0

; Creation of a stage
cube=CreateCube()
EntityColor cube, 255,128,0
cone1=CreateCone(20)
EntityColor cone1,0,255,255
PositionEntity cone1,-4,0,0
cone2=CreateCone(20)
EntityColor cone2,0,255,0
PositionEntity cone2,4,0,0
p=CreatePivot()
sph=CreateSphere(20, p)
PositionEntity sph, 0,0,-4

Select fx
 Case 1:bl=createblurlayer(cam, 1,0,1,1, .95,1, o)
 Case 2:bl=createblurlayer(cam, 1,0,1, .97,1,3, o)
 Case 3:bl=createblurlayer(cam, 1, .2,1.02, .97,1,3, o)
 Case 4:bl=createblurlayer(cam, 1,0,1.01,1, .95,1, o)
 Case 5
 bl=createblurlayer(cam, 1,0,1.01,1, .9,1, o)
 EntityColor bl, 240,255,225
 Case 6
 bl=createblurlayer(cam, 1.1,0,1,1, .95,1, o)
 RotateEntity bl, 1,1,0
 Case 7
 bl=createblurlayer(cam, 1.01,1,1,1, .9,1, o)
 bl2=createblurlayer(cam, 1.02,-1,1,1, .8,1, o)
End Select

SetBuffer BackBuffer()
While Not KeyHit(1)
 TurnEntity cube, .1, .2, .3
 TurnEntity p, .55, .35, .2
 RenderWorld
 bltex=updateblurlayer(bl, bltex)
 If bl2 Then EntityTexture bl2, bltex
 If fx=4 Then PositionEntity bl, Rnd(-.01, .01), Rnd(-.01, .01), 1
 Flip
Wend

; Function of creation of a layer of the degradation adhered to the chamber - returns the address of a layer
Function createblurlayer(cam, z#, ang#, mgn#, bright#, alpha#, bmode, ord)
Local xres=GraphicsWidth()
Local yres=GraphicsHeight()
layer=CreateMesh(cam)
s=CreateSurface(layer)
; Calculation of coordinates of a structure
vx#= 1.0*xres/texsize
vy#= 1.0*yres/texsize
AddVertex s,-1,-1,0,0,0
AddVertex s, 1,-1,0, vx#, 0
AddVertex s,-1,1,0,0, vy#
AddVertex s, 1,1,0, vx#, vy#
AddTriangle s, 0,1,2
AddTriangle s, 3,2,1
; Definition of sizes for installation of a rectangular directly in front of the chamber by
; Calculations of screen coordinates of a point of the three-dimensional world
PositionEntity layer, 1,1, z#
CameraProject cam, EntityX(layer, True), EntityY(layer, True), EntityZ(layer, True)
rx#= ProjectedX#()-.5*xres
ry#= ProjectedY#()-.5*yres
; Scaling a layer
ScaleMesh layer, .5*xres/rx#,.5*yres/ry#, 1
; Sdlvig a rectangular on polpiksela to the left-upwards that it(he) was in the center of the screen
PositionEntity layer,-.5/rx#,-.5/ry#, z#
RotateEntity layer, 0,0, ang#
; The task of effects of a layer
ScaleEntity layer, mgn#, mgn#, mgn#
EntityAlpha layer, alpha#
EntityBlend layer, bmode
col=255*bright#
EntityColor layer, col, col, col
EntityFX layer, 1
EntityOrder layer, ord
Return layer
End Function

; Function of updating of a layer(returns the address of a structure)
; It is necessary to cause each time after RenderWorld
Function updateblurlayer(layer, tex)
If tex=0 Then tex=CreateTexture(texsize, texsize)
EntityTexture layer, tex
CopyRect 0,0, GraphicsWidth(), GraphicsHeight(), 0,0, BackBuffer(), TextureBuffer(tex)
Return tex
End Function
