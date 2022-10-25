; ID: 329
; Author: skn3[ac]
; Date: 2002-05-24 15:19:55
; Title: DragSelect 3D objects
; Description: Simple method of selecting a 3d point in space

Graphics3D 640,480
SetBuffer BackBuffer()

Global camera=CreateCamera()
PositionEntity camera,0,2,-10

light=CreateLight()
RotateEntity light,90,0,0

Type cube
	Field status
	Field cube
	Field cube2
End Type

brush=CreateBrush(0,255,0)

obj.cube=New cube
obj\cube=CreateCube()
PositionEntity obj\cube,0,1,0
obj\cube2=CreateCube()
PositionEntity obj\cube2,0,1,0
ScaleEntity obj\cube2,1.1,1.1,1.1
EntityAlpha obj\cube2,0.4
HideEntity obj\cube2
PaintMesh obj\cube2,brush
obj\status=False

obj.cube=New cube
obj\cube=CreateCube()
PositionEntity obj\cube,-5,1,0
obj\cube2=CreateCube()
PositionEntity obj\cube2,-5,1,0
ScaleEntity obj\cube2,1.1,1.1,1.1
EntityAlpha obj\cube2,0.4
HideEntity obj\cube2
PaintMesh obj\cube2,brush
obj\status=False

obj.cube=New cube
obj\cube=CreateCube()
PositionEntity obj\cube,5,1,0
obj\cube2=CreateCube()
PositionEntity obj\cube2,5,1,0
ScaleEntity obj\cube2,1.1,1.1,1.1
EntityAlpha obj\cube2,0.4
HideEntity obj\cube2
PaintMesh obj\cube2,brush
obj\status=False

obj.cube=New cube
obj\cube=CreateCube()
PositionEntity obj\cube,0,5,0
obj\cube2=CreateCube()
PositionEntity obj\cube2,0,5,0
ScaleEntity obj\cube2,1.1,1.1,1.1
EntityAlpha obj\cube2,0.4
HideEntity obj\cube2
PaintMesh obj\cube2,brush
obj\status=False

obj.cube=New cube
obj\cube=CreateCube()
PositionEntity obj\cube,-5,5,0
obj\cube2=CreateCube()
PositionEntity obj\cube2,-5,5,0
ScaleEntity obj\cube2,1.1,1.1,1.1
EntityAlpha obj\cube2,0.4
HideEntity obj\cube2
PaintMesh obj\cube2,brush
obj\status=False

obj.cube=New cube
obj\cube=CreateCube()
PositionEntity obj\cube,5,5,0
obj\cube2=CreateCube()
PositionEntity obj\cube2,5,5,0
ScaleEntity obj\cube2,1.1,1.1,1.1
EntityAlpha obj\cube2,0.4
HideEntity obj\cube2
PaintMesh obj\cube2,brush
obj\status=False

obj.cube=New cube
obj\cube=CreateCube()
PositionEntity obj\cube,0,-3,0
obj\cube2=CreateCube()
PositionEntity obj\cube2,0,-3,0
ScaleEntity obj\cube2,1.1,1.1,1.1
EntityAlpha obj\cube2,0.4
HideEntity obj\cube2
PaintMesh obj\cube2,brush
obj\status=False

obj.cube=New cube
obj\cube=CreateCube()
PositionEntity obj\cube,-5,-3,0
obj\cube2=CreateCube()
PositionEntity obj\cube2,-5,-3,0
ScaleEntity obj\cube2,1.1,1.1,1.1
EntityAlpha obj\cube2,0.4
HideEntity obj\cube2
PaintMesh obj\cube2,brush
obj\status=False

obj.cube=New cube
obj\cube=CreateCube()
PositionEntity obj\cube,5,-3,0
obj\cube2=CreateCube()
PositionEntity obj\cube2,5,-3,0
ScaleEntity obj\cube2,1.1,1.1,1.1
EntityAlpha obj\cube2,0.4
HideEntity obj\cube2
PaintMesh obj\cube2,brush
obj\status=False

Global mouse=False,x_s=0,y_s=0,x_e=0,y_e=0

Repeat
	SetBuffer BackBuffer()
	Cls
		If mouse=False Then
			If MouseDown(1)=True Then
				mouse=True
				x_s=MouseX()
				y_s=MouseY()
				x_e=MouseX()
				y_e=MouseY()
			End If
		Else
			x_e=MouseX()
			y_e=MouseY()
			If MouseDown(1)=False Then
				select_cube()
				mouse=False
			End If
		End If
	For obj.cube = Each cube
		If obj\status=True Then
			TurnEntity obj\cube,1,1,1
			TurnEntity obj\cube2,1,1,1
		End If
	Next
	UpdateWorld()
	RenderWorld()
	If mouse=True Then
		Color 0,255,0
		Line x_s,y_s,x_e,y_s
		Line x_s,y_s,x_s,y_e
		Line x_e,y_s,x_e,y_e
		Line x_e,y_e,x_s,y_e
	End If
	Flip
Until KeyDown(1)=True

Function select_cube()
	;Setup x,y's
	If x_e<x_s Then
		x=x_e
		w=x_s-x_e
	Else
		x=x_s
		w=x_e-x_s
	End If
	If y_e<y_s Then
		y=y_e
		h=y_s-y_e
	Else
		y=y_s
		h=y_e-y_s
	End If
	For obj.cube = Each cube
		CameraProject(camera,EntityX(obj\cube),EntityY(obj\cube),EntityZ(obj\cube))
		If RectsOverlap(ProjectedX()-5,ProjectedY()-5,10,10,x,y,w,h)=True Then
			obj\status=True
			ShowEntity obj\cube2
		Else
			obj\status=False
			HideEntity obj\cube2
		End If
	Next
End Function
