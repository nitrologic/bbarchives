; ID: 865
; Author: Jeppe Nielsen
; Date: 2003-12-23 09:40:19
; Title: Draw3DMouse
; Description: Draw a 3d quad as a pointer. Works with any zoom value, as it uses a second camera.

HidePointer
Graphics3D 800,600,16,2



Init3DMouse("Pointer.png")


Repeat
Cls


Draw3DMouse(MouseX(),MouseY())

RenderWorld

Flip

Until KeyDown(1)
End

Global mousecamera3d
Global mousepivot3d
Global mouseentity3d,mousetexture3d

Function Init3DMouse(file$,flags=1+2+16+32,order=-99,x#=10000,y#=10000,z#=10000)
Clear3DMouse()

mousecamera3d=CreateCamera()
PositionEntity mousecamera3d,x,y,z
CameraClsMode mousecamera3d,0,1
CameraRange mousecamera3d,1,2
EntityOrder mousecamera3d,order

mousepivot3d=CreatePivot(mousecamera3d)

PositionEntity mousepivot3d,-1,Float(GraphicsHeight())/GraphicsWidth(),1
scale#=2.0/GraphicsWidth()
ScaleEntity mousepivot3d,scale,-scale,1

mousetexture3d=LoadTexture(file$,flags)
mouseentity3d=CreateMesh(mousepivot3d)
EntityFX mouseentity3d,1+8

surf=CreateSurface(mouseentity3d)

w#=Float(TextureWidth(mousetexture3d))
h#=Float(TextureHeight(mousetexture3d))

v=AddVertex(surf,0,0,0 ,0,0)
AddVertex(surf,w,0,0 ,1,0)
AddVertex(surf,w,h,0 ,1,1)
AddVertex(surf,0,h,0 ,0,1)

AddTriangle(surf,v,v+1,v+2)
AddTriangle(surf,v,v+2,v+3)

EntityTexture mouseentity3d,mousetexture3d

End Function

Function Clear3DMouse()

If mousetexture3d<>0 Then FreeTexture mousetexture3d: mousetexture3d=0
If mouseentity3d<>0 Then FreeEntity mouseentity3d: mouseentity3d=0
If mousepivot3d<>0 Then FreeEntity mousepivot3d: mousepivot3d=0
If mousecamera3d<>0 Then FreeEntity mousecamera3d: mousecamera3d=0

End Function

Function Draw3DMouse(x,y)

	PositionEntity mouseentity3d,x,y,0

End Function
