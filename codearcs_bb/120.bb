; ID: 120
; Author: David Bird(Birdie)
; Date: 2001-10-31 16:41:35
; Title: Mesh Outlines
; Description: A slow method to create outlines on meshes

;Edge detection functions
;(c)David Bird 2001
;dave@birdie72.freeserve.co.uk
;Birdie
Graphics3D 640,480
SetBuffer BackBuffer()

cam=CreateCamera()
PositionEntity cam,0,0,-10
CameraClsColor cam,255,255,255

sphere=CreateSphere(10)
ScaleMesh sphere,5,3,2
EntityColor sphere,255,255,0

light1=CreateLight()
TurnEntity light1,45,45,0


While Not KeyDown(1)
	DeleteOutlines
	x#=Float(MouseYSpeed())/Float(2)
	y#=Float(MouseXSpeed())/Float(2)
	TurnEntity sphere,x,y,0,True

	OutlineMesh(sphere,cam)
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	UpdateWorld
	RenderWorld
	DrawOutlines()
	Color 0,0,0
	Text 320,0,"Edge Detection and Outline Function.(c)2001 David Bird",1
	Text 320,15,"Need Speeding up to be useful.",1
	Text 320,30,"dave@birdie72.freeserve.co.uk",1
	Text 320,47,"Move mouse to spin sphere.",1
	Flip
Wend

FreeEntity sphere
EndGraphics
End
;
;

Function Check_Cull_2D#(x1#,y1#,x2#,y2#,x3#,y3#)
	 Return (x1-x2)*(y3-y2)-(y1-y2)*(x3-x2)
End Function

;
;

Type outside
	Field sx1,sy1
	Field sx2,sy2
End Type

Function OutLineMesh(mesh,cam,rr=0,gg=0,bb=0)
	For surfcnt=1 To CountSurfaces(mesh)
		;
		;Find matching tris
		;
		surf=GetSurface(mesh,surfcnt)
		tricnt=CountTriangles(surf)
		For a=0 To tricnt-1
			TFormPoint VertexX(surf,TriangleVertex(surf,a,0)),VertexY(surf,TriangleVertex(surf,a,0)),VertexZ(surf,TriangleVertex(surf,a,0)),mesh,0
			CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
			sx1=ProjectedX()
			sy1=ProjectedY()
			
			TFormPoint VertexX(surf,TriangleVertex(surf,a,1)),VertexY(surf,TriangleVertex(surf,a,1)),VertexZ(surf,TriangleVertex(surf,a,1)),mesh,0
			CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
			sx2=ProjectedX()
			sy2=ProjectedY()

			TFormPoint VertexX(surf,TriangleVertex(surf,a,2)),VertexY(surf,TriangleVertex(surf,a,2)),VertexZ(surf,TriangleVertex(surf,a,2)),mesh,0
			CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
			sx3=ProjectedX()
			sy3=ProjectedY()
			cull1#=Check_Cull_2d(sx1,sy1,sx2,sy2,sx3,sy3)

			;Check edge 1 0-1
			edge=FindSecondTri(surf,a,TriangleVertex(surf,a,0),TriangleVertex(surf,a,1))
			If edge>0 Then	;check culling of both tri's
				;get screen coords of edge tri
				TFormPoint VertexX(surf,TriangleVertex(surf,edge,0)),VertexY(surf,TriangleVertex(surf,edge,0)),VertexZ(surf,TriangleVertex(surf,edge,0)),mesh,0
				CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
				ex1=ProjectedX()
				ey1=ProjectedY()
			
				TFormPoint VertexX(surf,TriangleVertex(surf,edge,1)),VertexY(surf,TriangleVertex(surf,edge,1)),VertexZ(surf,TriangleVertex(surf,edge,1)),mesh,0
				CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
				ex2=ProjectedX()
				ey2=ProjectedY()

				TFormPoint VertexX(surf,TriangleVertex(surf,edge,2)),VertexY(surf,TriangleVertex(surf,edge,2)),VertexZ(surf,TriangleVertex(surf,edge,2)),mesh,0
				CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
				ex3=ProjectedX()
				ey3=ProjectedY()
				cull2#=Check_Cull_2d(ex1,ey1,ex2,ey2,ex3,ey3)
				If cull1<=0 And cull2>0 Then ;this is an edge
					edge2d.outside=New outside
					edge2d\sx1=sx1
					edge2d\sy1=sy1
					edge2d\sx2=sx2
					edge2d\sy2=sy2
				End If
			End If
			;Check edge 2 1-2
			edge=FindSecondTri(surf,a,TriangleVertex(surf,a,1),TriangleVertex(surf,a,2))
			If edge>0 Then	;check culling of both tri's
				;get screen coords of edge tri
				TFormPoint VertexX(surf,TriangleVertex(surf,edge,0)),VertexY(surf,TriangleVertex(surf,edge,0)),VertexZ(surf,TriangleVertex(surf,edge,0)),mesh,0
				CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
				ex1=ProjectedX()
				ey1=ProjectedY()
			
				TFormPoint VertexX(surf,TriangleVertex(surf,edge,1)),VertexY(surf,TriangleVertex(surf,edge,1)),VertexZ(surf,TriangleVertex(surf,edge,1)),mesh,0
				CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
				ex2=ProjectedX()
				ey2=ProjectedY()

				TFormPoint VertexX(surf,TriangleVertex(surf,edge,2)),VertexY(surf,TriangleVertex(surf,edge,2)),VertexZ(surf,TriangleVertex(surf,edge,2)),mesh,0
				CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
				ex3=ProjectedX()
				ey3=ProjectedY()
				cull2#=Check_Cull_2d(ex1,ey1,ex2,ey2,ex3,ey3)
				If cull1<=0 And cull2>0 Then ;this is an edge
					edge2d.outside=New outside
					edge2d\sx1=sx2
					edge2d\sy1=sy2
					edge2d\sx2=sx3
					edge2d\sy2=sy3
				End If
			End If
			;Check edge 3 2-0
			edge=FindSecondTri(surf,a,TriangleVertex(surf,a,2),TriangleVertex(surf,a,0))
			If edge>0 Then	;check culling of both tri's
				;get screen coords of edge tri
				TFormPoint VertexX(surf,TriangleVertex(surf,edge,0)),VertexY(surf,TriangleVertex(surf,edge,0)),VertexZ(surf,TriangleVertex(surf,edge,0)),mesh,0
				CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
				ex1=ProjectedX()
				ey1=ProjectedY()
			
				TFormPoint VertexX(surf,TriangleVertex(surf,edge,1)),VertexY(surf,TriangleVertex(surf,edge,1)),VertexZ(surf,TriangleVertex(surf,edge,1)),mesh,0
				CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
				ex2=ProjectedX()
				ey2=ProjectedY()

				TFormPoint VertexX(surf,TriangleVertex(surf,edge,2)),VertexY(surf,TriangleVertex(surf,edge,2)),VertexZ(surf,TriangleVertex(surf,edge,2)),mesh,0
				CameraProject cam,TFormedX(),TFormedY(),TFormedZ()
				ex3=ProjectedX()
				ey3=ProjectedY()
				cull2=Check_Cull_2d(ex1,ey1,ex2,ey2,ex3,ey3)
				If cull1<=0 And cull2>0 Then ;this is an edge
					edge2d.outside=New outside
					edge2d\sx1=sx3
					edge2d\sy1=sy3
					edge2d\sx2=sx1
					edge2d\sy2=sy1
				End If
			End If
		Next
	Next
	Return 
End Function

Function DrawOutlines(rr=0,gg=0,bb=0)
	Color rr,gg,bb
	For le.outside=Each outside
		Line le\sx1,le\sy1,le\sx2,le\sy2
	Next
End Function

Function DeleteOutlines()
	Delete Each outside
End Function

Function FindSecondtri(surf,this,ind1,ind2)
	For a=0 To CountTriangles(surf)-1
		If a<>this Then
			If TriangleVertex(surf,a,0)=ind2 And TriangleVertex(surf,a,1)=ind1 Then Return a
			If TriangleVertex(surf,a,1)=ind2 And TriangleVertex(surf,a,2)=ind1 Then Return a
			If TriangleVertex(surf,a,2)=ind2 And TriangleVertex(surf,a,0)=ind1 Then Return a
		End If
	Next
	Return -1
End Function
