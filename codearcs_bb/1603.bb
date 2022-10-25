; ID: 1603
; Author: Beaker
; Date: 2006-01-27 13:49:27
; Title: Quad functions
; Description: Similar to those you have for Triangles/Vertices - AddQuad(), CountQuads(), QuadVertex() etc

Graphics3D 640,480,0,2

cam = CreateCamera()
MoveEntity cam,0,0,-6

lit = CreateLight()
TurnEntity lit,30,45,0

ball = CreateSphere(10)
PositionEntity ball,3,0,0
EntityPickMode ball,2

cube = CreateCube()
EntityPickMode cube,2

cone = CreateCylinder(14)
PositionEntity cone,-3,0,0
EntityPickMode cone,2

Local tfx#[4]
Local tfy#[4]
Local tfz#[4]

cursor = CreateMesh()
cursorsurf = CreateSurface(cursor)
EntityColor cursor,255,0,0
EntityOrder cursor,-9

While Not KeyDown(1)

	RenderWorld
	If PickedEntity()
		Text 5,5,"picked quad = "+PickedQuad()+"/"+CountQuads(PickedSurface())
		Text 5,35,"quad verts:"
		Text 15,50,QuadVertex(PickedSurface(),PickedQuad(),0)
		Text 15,65,QuadVertex(PickedSurface(),PickedQuad(),1)
		Text 15,80,QuadVertex(PickedSurface(),PickedQuad(),2)
		Text 15,95,QuadVertex(PickedSurface(),PickedQuad(),3)
	EndIf

	Text 320,440,"Use mouse to click on objects",True

	Flip
	
	If MouseDown(1)
		CameraPick cam,MouseX(),MouseY()
		If PickedEntity()
			surf = PickedSurface()
		
			For f = 0 To 3
				qv = QuadVertex(surf,PickedQuad(),f)
				TFormPoint VertexX(surf,qv),VertexY(surf,qv),VertexZ(surf,qv),PickedEntity(),0
				tfx[f] = TFormedX()
				tfy[f] = TFormedY()
				tfz[f] = TFormedZ()
			Next
			
			ClearSurface cursorsurf
			AddQuad2(cursorsurf, tfx[0],tfy[0],tfz[0], tfx[1],tfy[1],tfz[1], tfx[2],tfy[2],tfz[2], tfx[3],tfy[3],tfz[3])
			UpdateNormals cursor

		EndIf
	Else
		ClearSurface cursorsurf
	EndIf
	
	TurnEntity cube,0.3,0.4,0
Wend
End


Function AddQuad(surf,v0,v1,v2,v3)	; similar to AddTriangle()
	Local tri = AddTriangle(surf,v0,v1,v2)
	AddTriangle(surf,v0,v2,v3)
	Return tri / 2
End Function

Function AddQuad2(surf, x0#,y0#,z0#, x1#,y1#,z1#, x2#,y2#,z2#, x3#,y3#,z3#)	; alternative to above func
	Local v0 = AddVertex(surf,x0,y0,z0, 0,0)
	Local v1 = AddVertex(surf,x1,y1,z1, 1,0)
	Local v2 = AddVertex(surf,x2,y2,z2, 1,1)
	Local v3 = AddVertex(surf,x3,y3,z3, 0,1)
	Local tri = AddTriangle(surf,v0,v1,v2)
	AddTriangle(surf,v0,v2,v3)
	Return tri / 2
End Function


Function CountQuads(surf)	; similar to CountTriangles()
	Return CountTriangles(surf) / 2
End Function

Function QuadVertex(surf,index,vertex) ; similar to TriangleVertex()
	If vertex < 3
		Return TriangleVertex(surf,index*2,vertex)
	EndIf
	Return TriangleVertex(surf,index*2+1,2)
End Function


Function PickedQuad()	; similar to PickedTriangle()
	Return PickedTriangle() / 2
End Function



Function PickedVertex()	; similar to PickedTriangle()/PickedSurface() etc
	Local f, pv
	Local dx#,dy#,dz#
	Local vert, dist#, nearest# = -1.0
	Local surf = PickedSurface()
	
	TFormPoint PickedX(),PickedY(),PickedZ(),0,PickedEntity()
	For f = 0 To 2
		vert = TriangleVertex(surf,PickedTriangle(),f)
		dx = TFormedX()-VertexX(surf,vert)
		dy = TFormedY()-VertexY(surf,vert)
		dz = TFormedZ()-VertexZ(surf,vert)
		dist = dx*dx + dy*dy + dz*dz
		If dist < nearest Or nearest < 0.0
			pv = vert
			nearest = dist
		EndIf
	Next
	Return pv
End Function
