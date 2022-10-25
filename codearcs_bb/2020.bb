; ID: 2020
; Author: puki
; Date: 2007-05-26 08:25:47
; Title: 'Lightmesh' fixed-point lighting
; Description: Locks the position of the light

Graphics3D 640,480,16,2 
camera=CreateCamera() 

light=CreateSphere()
ScaleEntity light,.1,.1,.1
PositionEntity light,-4,-4,-4; I've used these coords to keep the light in view for this example

ent=CreateSphere() 
EntityFX ent,2 ; enable vertex colors 
LightMesh ent,-255,-255,-255 ; reset vertex colors from 255,255,255 (default) to 0,0,0 
LightMesh ent,255,255,0,50,-4,-4,-4 ; apply fake lighting - I've changed the X,Y,Z to reflect the 'visual' position of the light entity

MoveEntity camera,0,2,-10 
PointEntity camera,ent 

While Not KeyDown(1)

TurnEntity ent,0,0,.5

LightMesh ent,-255,-255,-255 ; reset vertex colors from 255,255,255 (default) to 0,0,0 
TFormPoint 0,0,0,light,ent ; bypass the limits of LightMesh's fake light coordinates
LightMesh ent,255,255,0,50,TFormedX(),TFormedY(),TFormedZ(); re-apply fake lighting

RenderWorld 
Flip 
Wend 
End
