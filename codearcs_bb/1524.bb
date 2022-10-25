; ID: 1524
; Author: Andy
; Date: 2005-11-08 22:59:07
; Title: Fake mirrors
; Description: How to make mirrors in varous shapes and sizes

; fakemirrors 

width=800
height=600
fovsphere#=65.0
fovmirror#=70.0
texsize=512
testfl=1


Graphics3D width,height 

SetBuffer BackBuffer() 

; create textures
tex=CreateTexture( texsize,texsize,48+256)
tex2=CreateTexture( texsize,texsize,48+256)

;setup cube texture
SetBuffer TextureBuffer(tex2) 
ClsColor 255,255,255 
Cls
For q=1 To 200
Color Rnd(255),Rnd(255),Rnd(255)
Rect Rnd(640),Rnd(480),Rnd(32),Rnd(32)
Next
SetBuffer BackBuffer() 

; brushes
brush1=CreateBrush() 
BrushTexture brush1,tex 
BrushShininess brush1,1 
BrushFX brush1,1

brush2=CreateBrush() 
BrushTexture brush2,tex2

brush3=CreateBrush(255,0,0) 





; objects

sphere_object=CreateSphere()
sphere_pivot=CreatePivot(sphere_object)
sphere_camera=CreateCamera(sphere_object) 
light=CreateLight() 

cube2=CreateCube()
ScaleMesh cube2,200,200,200
FlipMesh cube2
PaintMesh cube2,brush2


mirror_object=CreateMesh() 
surf=CreateSurface(mirror_object) 
v0=AddVertex(surf,-5, 5, 0, 1.0, 0.0)  ; upper left 
v1=AddVertex(surf, 5, 5, 0, 0.0, 0.0)   ; upper right 
v2=AddVertex(surf, 5,-5, 0, 0.0, 1.0)   ; lower right 
v3=AddVertex(surf,-5,-5, 0, 1.0, 1.0)  ; lower left 
t0=AddTriangle(surf,v0,v1,v2) ; triangle 1 
t1=AddTriangle(surf,v2,v3,v0) ; triangle 2 

mirror_pivot_left=CreatePivot(mirror_object)
mirror_pivot_right=CreatePivot(mirror_object)
PositionEntity mirror_pivot_left,-10,10,0 
PositionEntity mirror_pivot_right,10,10,0 

mirror_camera=CreateCamera(mirror_object)

; paint objects
PaintMesh mirror_object,brush1 
PaintMesh sphere_object,brush3 


; posititon and orient objects
PositionEntity mirror_object,0,0,100 
RotateEntity light,45,0,0 
RotateEntity mirror_camera,0.0,180.0,0.0 


While Not KeyDown(1) 

Gosub movesphere
Gosub rmirror

Cls
HideEntity(mirror_camera)
ShowEntity(sphere_camera)

CameraZoom sphere_camera,(1.0 / Tan(fovsphere#/2.0))
CameraClsColor sphere_camera,0,0,0
RenderWorld 

frames=frames+1
If fpstimer < (MilliSecs() - 1000)
	fps=frames
	frames=0
	fpstimer=MilliSecs()
EndIf
Text 10,10, fps
Text 10,30, fovmirror#
Text 10,50, mirror_distance#


Flip 

Wend 

End 


.movesphere
	If KeyDown( 203) Then TurnEntity sphere_object,0,0.5,0
	If KeyDown( 205 ) Then TurnEntity sphere_object,0,-0.5,0
	If KeyDown( 200 ) Then MoveEntity sphere_object,0,0,0.5
	If KeyDown( 208 ) Then MoveEntity sphere_object,0,0,-0.5
	If KeyDown( 30 ) Then MoveEntity sphere_object,0,0.5,0
	If KeyDown( 44 ) Then MoveEntity sphere_object,0,-0.5,0
	If KeyHit(17) Then wire= Not wire

	WireFrame wire
Return


.rmirror
    HideEntity(sphere_camera)
    ShowEntity(mirror_camera)

	CameraClsColor mirror_camera,0,0,255
	PointEntity(sphere_pivot, mirror_object) 

        mirror_distance#=Abs(EntityDistance ( sphere_object, mirror_object))
        PositionEntity mirror_camera,0.0,0.0,mirror_distance#
	RotateEntity mirror_camera,EntityPitch(mirror_object)+(EntityPitch(sphere_pivot,1)*1.0),EntityYaw(mirror_object)+180+(EntityYaw(sphere_pivot,1)*-1.0),EntityRoll(mirror_object) 

	PointEntity(sphere_pivot, mirror_pivot_left) 
	fov1#=EntityYaw(sphere_pivot);*1.75
	PointEntity(sphere_pivot, mirror_pivot_right) 
	fov2#=EntityYaw(sphere_pivot);*1.75

	fovmirror#=Abs(fov2#-fov1#)

	CameraZoom mirror_camera,(1.0 / Tan(fovmirror#/2.0))

	Cls
	RenderWorld 
    CopyRect (width/2)-(texsize/2),(height/2)-(texsize/2),texsize,texsize,0,0,0,TextureBuffer(tex)
Return
