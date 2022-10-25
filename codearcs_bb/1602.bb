; ID: 1602
; Author: Beaker
; Date: 2006-01-27 06:48:16
; Title: CopyPartMesh(mesh,start,end,par=0)
; Description: Copies part of a mesh

Graphics3D 640,480

cam = CreateCamera()
MoveEntity cam,0,1,-6

lit = CreateLight()
TurnEntity lit,30,45,0

ball = CreateSphere(10)
PositionEntity ball,3,3,0
cube = CreateCube()
PositionEntity cube,0,3,0
cone = CreateCylinder(14)
PositionEntity cone,-3,3,0

flipper=1

cnt# = 0.0
cnt2# = 0.0

While Not KeyDown(1)

	RenderWorld
	Flip
	
	If flipper 
		cnt = cnt + 0.003
		If cnt> 1.0
			cnt = 1.0
			cnt2 = 0.0
			flipper = 1-flipper
		EndIf
	Else
		cnt2 = cnt2 + 0.003
		If cnt2 > 1.0
			cnt = 1.0
			cnt2 = 0.0
			flipper = 1-flipper
		EndIf
	EndIf


	FreeEntity pivot
	pivot = CreatePivot()
	
	ballpart = copyPartMesh(ball,cnt2,cnt)
	PositionEntity ballpart,3,0,0
	EntityFX ballpart,16
	EntityParent ballpart,pivot
	
	cubepart = copyPartMesh(cube,cnt2,cnt)
	EntityFX cubepart,16
	EntityParent cubepart,pivot
	
	
	conepart = copyPartMesh(cone,cnt2,cnt)
	PositionEntity conepart,-3,0,0
	EntityFX conepart,16
	EntityParent conepart,pivot
	
Wend
End



Function copyPartMesh(meshFrom,startamt#=0.0,endamt#=1.0,par=0)	
	Local surfFrom = GetSurface(meshFrom,1)
	startvert = startamt*(CountVertices(surfFrom)-1)
	endvert = endamt*(CountVertices(surfFrom)-1)

	Local meshTo = CreateMesh(par)	
	Local surfTo = CreateSurface(meshTo)
	Local vert,newvert,vert1
	Local v0,v1,v2,tri
	
	For vert = startvert To endvert
		newvert = AddVertex(surfTo,VertexX(surfFrom,vert),VertexY(surfFrom,vert),VertexZ(surfFrom,vert), VertexU(surfFrom,vert),VertexV(surfFrom,vert))
		VertexNormal surfTo,newvert,VertexNX(surfFrom,vert),VertexNY(surfFrom,vert),VertexNZ(surfFrom,vert)
		If vert = startvert
			vert1 = newvert
		EndIf
	Next
	
	For tri = 0 To CountTriangles(surfFrom)-1
		v0 = TriangleVertex(surfFrom,tri,0)
		v1 = TriangleVertex(surfFrom,tri,1)
		v2 = TriangleVertex(surfFrom,tri,2)
		If v0 => startvert And v0 <= endvert
			If v1 => startvert And v1 <= endvert
				If v2 => startvert And v2 <= endvert
					v0 = vert1+v0-startvert
					v1 = vert1+v1-startvert
					v2 = vert1+v2-startvert
					AddTriangle(surfTo,v0,v1,v2)
				EndIf
			EndIf
		EndIf
	Next
	Return meshTo
End Function
