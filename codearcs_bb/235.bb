; ID: 235
; Author: bradford6
; Date: 2002-02-12 14:14:56
; Title: draw 3d from 2d
; Description: draw on a 2d bitmap and add a cube to the 3d world

Graphics3D 600,400,16,2


camera2 = CreateCamera()
camera3 = CreateCamera()
camera4 = CreateCamera()


;CameraViewport(camera1,0,0,300,200) ; top view
CameraViewport(camera2,300,0,300,200) ; left
CameraViewport(camera3,0,200,300,200) ; angle
CameraViewport(camera4,300,200,300,200) ; texture window

;world is 100X100 units (in the x and z directions)
center = CreatePivot()
PositionEntity center,50,1,50


PositionEntity camera2,50,10,0
PositionEntity camera3,0,25,0
PositionEntity camera4,50,100,50 ; up 50 units


CameraClsColor camera2,200,0,0
CameraClsColor camera3,00,0,220
CameraClsColor camera4,200,80,200


worldboxtex = CreateTexture(256,256)
SetBuffer TextureBuffer(worldboxtex)
ClsColor 200,200,200
Cls
Color 3,3,3
Rect 0,0,128,128,1
Rect 128,128,128,128


worldbox = CreateCube()
ScaleEntity worldbox,100,10,100
FlipMesh worldbox
EntityTexture worldbox,worldboxtex
ScaleTexture worldboxtex,.3,.3
;EntityColor worldbox,0,230,0


tex = CreateImage(200,200) ; twice as big so we can see it
SetBuffer ImageBuffer(tex)
ClsColor 200,200,24
Cls


light=CreateLight(camera1)
AmbientLight 100,100,100

cube=CreateCube()

Type world_object
    Field entity
End Type



Repeat
plottimer=plottimer -1
If plottimer<0 Then plottimer = -1
;PointEntity camera1,cube
PointEntity camera2,center
PointEntity camera3,center
PointEntity camera4,center

RotateMesh cube,1,2,1


MoveEntity camera3,.3,0,0


SetBuffer ImageBuffer(tex)
Color 0,0,255

If MouseDown(2)=1 
    ypos = ypos + MouseYSpeed()/10
    If ypos>30 Then ypos=30
    If ypos<1 Then ypos=1

EndIf

If MouseDown(1)=1 And plottimer<0 
    MX = MouseX()
    MY = MouseY()
        If mx<200 And my<200 
             
            Rect mx,my,2,2,1
            b.world_object = New world_object
            b\entity = CopyEntity(cube)
            PositionEntity b\entity,mx/2,ypos,my/2
            EntityColor b\entity,Rnd(0,255),Rnd(0,255),Rnd(0,255)
            plottimer=10
        EndIf
EndIf    



UpdateWorld
RenderWorld
SetBuffer BackBuffer()
DrawImage tex,0,0
Color 0,0,30
Text 0,0,"mousex ="+MouseX()+" mousey ="+MouseY()
Text 0,11,"3d_x = "+MouseX()/2
Text 0,25,"3d_y (HEIGHT)="+ypos
Text 0,39,"3d_Z = "+MouseY()/2
Flip

Until KeyHit(1) =1
