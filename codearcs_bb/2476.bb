; ID: 2476
; Author: Nate the Great
; Date: 2009-05-09 17:33:58
; Title: redblue realistic3d code
; Description: redblue 3d glasses needed

Graphics3D 1024,768,0,1

Global cam = CreateCamera()
lit = CreateLight()
TurnEntity lit,60,0,0

Type cube
	Field ent
End Type

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Global tdcam = CreateCamera()
TurnEntity tdcam,90,0,0

plan = CreatePlane()
MoveEntity plan,0,-5,0
EntityPickMode plan,2
EntityAlpha plan,0

CameraPick tdcam,0,0
Global x1# = PickedX()
Global y1# = PickedY()
Global z1# = PickedZ()

CameraPick tdcam,0,GraphicsHeight()
Global x2# = PickedX()
Global y2# = PickedY()
Global z2# = PickedZ()

CameraPick tdcam,GraphicsWidth(),GraphicsHeight()
Global x3# = PickedX()
Global y3# = PickedY()
Global z3# = PickedZ()

CameraPick tdcam,GraphicsWidth(),0
Global x4# = PickedX()
Global y4# = PickedY()
Global z4# = PickedZ()

m1 = CreateMesh()
s = CreateSurface(m1)

fr# = GraphicsWidth()/1024.0
fl# = GraphicsHeight()/1024.0
v1 = AddVertex(s,x1,y1,z1,0,0)
v2 = AddVertex(s,x2,y2,z2,0,fl)
v3 = AddVertex(s,x3,y3,z3,fr,fl)
v4 = AddVertex(s,x4,y4,z4,fr,0)

t1 = AddTriangle(s,v2,v1,v3)
t2 = AddTriangle(s,v4,v3,v1)

EntityColor m1,0,50,255
EntityFX m1,1


m2 = CreateMesh()
s = CreateSurface(m2)

v1 = AddVertex(s,x1,y1,z1,0,0)
v2 = AddVertex(s,x2,y2,z2,0,fl)
v3 = AddVertex(s,x3,y3,z3,fr,fl)
v4 = AddVertex(s,x4,y4,z4,fr,0)

t1 = AddTriangle(s,v2,v1,v3)
t2 = AddTriangle(s,v4,v3,v1)

EntityColor m2,255,50,0
EntityFX m2,1

redtex = CreateTexture(1024,1024)
blutex = CreateTexture(1024,1024)

EntityTexture m1,blutex
EntityTexture m2,redtex

EntityBlend m1,3
EntityBlend m2,3

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

SetBuffer BackBuffer()
timer = 0
While Not KeyDown(1)
Cls

timer = timer + 1

If timer => 3 Then
	timer = 0
	c.cube = New cube
	c\ent = CreateCube()
	scl# = Rnd(.01,.05)
	ScaleEntity c\ent,scl,scl,scl
	EntityColor c\ent,Rnd(255),Rnd(255),Rnd(255)
	MoveEntity c\ent,Rnd(-1,1),Rnd(-1,1),8
EndIf

For c.cube = Each cube
	MoveEntity c\ent,0,0,-.08
	If EntityZ(c\ent) < 0 Then
		FreeEntity c\ent
		Delete c.cube
	EndIf
Next

;renderstuff
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
ShowEntity cam
HideEntity tdcam
MoveEntity cam,-.05,0,0
TurnEntity cam,0,2,0
RenderWorld()

CopyRect(0,0,GraphicsWidth()+1,GraphicsHeight()+1,0,0,BackBuffer(),TextureBuffer(redtex))

TurnEntity cam,0,-2,0
MoveEntity cam,.1,0,0
TurnEntity cam,0,-2,0
RenderWorld()

CopyRect(0,0,GraphicsWidth()+1,GraphicsHeight()+1,0,0,BackBuffer(),TextureBuffer(blutex))
TurnEntity cam,0,2,0
MoveEntity cam,-.05,0,0

ShowEntity tdcam
HideEntity cam
RenderWorld()
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Flip
Wend
