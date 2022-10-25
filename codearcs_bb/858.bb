; ID: 858
; Author: Jeppe Nielsen
; Date: 2003-12-15 15:46:33
; Title: Vector Example
; Description: Graphically shows the use of aligntovector, dot and cross products

;small vector example by Jeppe Nielsen 2003
;nielsen_jeppe@hotmail.com
;
;showing dot and cross products in action!
;and aligntovector.

Graphics3D 800,600,16,2

SeedRnd MilliSecs()

p1=CreatePivot()
p2=CreatePivot()
p3=CreatePivot()

RotateEntity p1,Rnd(360),Rnd(360),Rnd(360)
RotateEntity p2,Rnd(360),Rnd(360),Rnd(360)

cube=CreateCube()
ScaleEntity cube,0.5,0.5,0.5
EntityAlpha cube,0.5

campivot=CreatePivot()
camera=CreateCamera(campivot)

PositionEntity camera,0,2,-3
PointEntity camera,campivot

Repeat

TurnEntity campivot,0,2,0

TFormVector 0,0,1,p1,0

If KeyHit(57)=True

align=1-align

turnangle1#=Rnd(-2,2)
turnangle2#=Rnd(-2,2)

EndIf


If align=1

;change the last parameter for varied speeds
AlignToVector p2,TFormedX(),TFormedY(),TFormedZ(),3,0.04

Else

TurnEntity p2,turnangle1#,turnangle2#,0

EndIf


TFormNormal 0,0,1,p1,0
nx1#=TFormedX()
ny1#=TFormedY()
nz1#=TFormedZ()

TFormNormal 0,0,1,p2,0
nx2#=TFormedX()
ny2#=TFormedY()
nz2#=TFormedZ()

dot#=nx1*nx2+ny1*ny2+nz1*nz2

angle#=ACos(dot#)

;Make a third vector perpendicular to two other vectors
cx# = ( ny1 * nz2 ) - ( nz1 * ny2 ) 
cy# = ( nz1 * nx2 ) - ( nx1 * nz2 ) 
cz# = ( nx1 * ny2 ) - ( ny1 * nx2 ) 				
											
AlignToVector p3,cx,cy,cz,3


RenderWorld()

drawworldaxes(camera,1)
drawvector(p1,camera,1.5,255,255,255)
drawvector(p2,camera,1.5,0,0,255)
drawvector(p3,camera,1.5,255,0,0)

Color 0,255,0
Text 10,10,"Angle between blue and white vectors: "+angle
Text 10,30,"Red vector is perpendicular to the two others: "
Text 10,50,"Space to toggle between align/rotate the blue vector"
Text 10,70,"White vector coordinates:"
Text 10,80,"X: "+nx1#
Text 10,90,"Y: "+ny1#
Text 10,100,"Z: "+nz1#

Flip
Until KeyDown(1)
End

Function drawvector(e,camera,length,r,g,b)

;Get initial position
CameraProject camera,EntityX(e,1),EntityY(e,1),EntityZ(e,1)
x=ProjectedX()
y=ProjectedY()

;Draw X axis
;Color 255,0,0
;MoveEntity e,length,0,0
;CameraProject camera,EntityX(e,1),EntityY(e,1),EntityZ(e,1)
;Line x,y,ProjectedX(),ProjectedY()
;MoveEntity e,-length,0,0

;Draw Y axis
;Color 0,255,0
;MoveEntity e,0,length,0
;CameraProject camera,EntityX(e,1),EntityY(e,1),EntityZ(e,1)
;Line x,y,ProjectedX(),ProjectedY()
;MoveEntity e,0,-length,0

;Draw Z axis
Color r,g,b ;,0,255
MoveEntity e,0,0,length
CameraProject camera,EntityX(e,1),EntityY(e,1),EntityZ(e,1)
Line x,y,ProjectedX(),ProjectedY()
MoveEntity e,0,0,-length
Oval ProjectedX()-4,ProjectedY()-4,8,8,1

End Function

Function drawworldaxes(camera,length)

;Get initial position
CameraProject camera,0,0,0
x=ProjectedX()
y=ProjectedY()

;Draw X axis
Color 255,0,0
CameraProject camera,length,0,0
Line x,y,ProjectedX(),ProjectedY()
CameraProject camera,length*1.1,0,0
Oval ProjectedX()-12,ProjectedY()-12,24,24,0
Text ProjectedX(),ProjectedY(),"X",1,1

;Draw Y axis
Color 0,255,0
CameraProject camera,0,length,0
Line x,y,ProjectedX(),ProjectedY()
CameraProject camera,0,length*1.1,0
Oval ProjectedX()-12,ProjectedY()-12,24,24,0
Text ProjectedX(),ProjectedY(),"Y",1,1

;Draw Z axis
Color 0,0,255
CameraProject camera,0,0,length
Line x,y,ProjectedX(),ProjectedY()
CameraProject camera,0,0,length*1.1
Oval ProjectedX()-12,ProjectedY()-12,24,24,0
Text ProjectedX(),ProjectedY(),"Z",1,1

End Function
