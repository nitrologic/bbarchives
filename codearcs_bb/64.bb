; ID: 64
; Author: skidracer
; Date: 2001-09-30 09:33:29
; Title: overlay
; Description: 2d overlay code

; overlay.bb

displaywidth=800
displayheight=600

Graphics3D displaywidth,displayheight

cam=CreateCamera()
CameraClsColor cam,100,120,200
CameraRange cam,.1,1000

overlay=CreatePivot()
aspect#=Float(displayheight)/displaywidth
PositionEntity overlay,-1,aspect,1
scale#=2.0/displaywidth

ScaleEntity overlay,scale,-scale,-scale

cube=CreateCube(overlay)
FitMesh cube,1,1,0,displaywidth-2,displayheight-2,0

sp=LoadSprite("simon.bmp")
EntityParent sp,overlay
ScaleSprite sp,.1,.1
SpriteViewMode sp,2

While Not KeyHit(1)
	PositionEntity sp,MouseX(),MouseY(),1
	RenderWorld
	UpdateWorld
	Flip
Wend

End
