; ID: 99
; Author: David Bird(Birdie)
; Date: 2001-10-14 04:51:47
; Title: Wireframe Overlay 
; Description: Combine wireframe with solid entities

;
; Wireframe for Mixed Modes 3d n 2d
; David Bird.  dave@birdie72.freeserve.co.uk
; Warning this is slow at high polys
;
Graphics3D 640,480
SetBuffer BackBuffer()

cam=CreateCamera()
PositionEntity cam,0,0,-4

mesh=CreateCylinder(10)
ScaleMesh mesh,1,2,1

light=CreateLight(2)
PositionEntity light,-4,4,-4
sphere=CreateSphere()
PositionEntity sphere,-1,0,2

While Not KeyDown(1)
	TurnEntity mesh,1.2,1,0
	UpdateWorld
	RenderWorld
	Draw_Wire_2D(mesh,cam)
	Color 255,255,255
	Flip
Wend

FreeEntity cam
EndGraphics
End

; Slow method to draw all polys in a mesh
; Wireframe
; (c)2001 David Bird
Function Draw_Wire_2D(mesh,cam,red=0,grn=0,blu=0)
	Color red,grn,blu
	If EntityInView(mesh,cam)=False Then Return
	cnt=CountSurfaces(mesh)
	For a=1 To cnt
		surf=GetSurface(mesh,a)
		For tri=0 To CountTriangles(surf)-1
			TFormPoint VertexX(surf,TriangleVertex(surf,tri,0)),VertexY(surf,TriangleVertex(surf,tri,0)),VertexZ(surf,TriangleVertex(surf,tri,0)),mesh,0
			CameraProject(cam,TFormedX(),TFormedY(),TFormedZ())
			sx1=ProjectedX()
			sy1=ProjectedY()
			TFormPoint VertexX(surf,TriangleVertex(surf,tri,1)),VertexY(surf,TriangleVertex(surf,tri,1)),VertexZ(surf,TriangleVertex(surf,tri,1)),mesh,0
			CameraProject(cam,TFormedX(),TFormedY(),TFormedZ())
			sx2=ProjectedX()
			sy2=ProjectedY()
			TFormPoint VertexX(surf,TriangleVertex(surf,tri,2)),VertexY(surf,TriangleVertex(surf,tri,2)),VertexZ(surf,TriangleVertex(surf,tri,2)),mesh,0
			CameraProject(cam,TFormedX(),TFormedY(),TFormedZ())
			sx3=ProjectedX()
			sy3=ProjectedY()
			If Check_Cull_2D(sx1,sy1,sx2,sy2,sx3,sy3)<0
				Line sx1,sy1,sx2,sy2
				Line sx2,sy2,sx3,sy3
				Line sx3,sy3,sx1,sy1	
			End If
		Next			
	Next
End Function

;Check for cw poly
;returns <0 for clockwise
;(c)2001 David Bird
Function Check_Cull_2D(x1,y1,x2,y2,x3,y3)
	 Return (x1-x2)*(y3-y2)-(y1-y2)*(x3-x2)
End Function
