; ID: 1456
; Author: Delerna
; Date: 2005-09-05 03:32:47
; Title: Random Tileable Terrain Generator
; Description: Generate terrain meshes that can be tiled side by side

;Changeable Parameters
GridSize%=9; Must be a 2^n+1
Landform#=10;The bigger the number the greater the difference in heights between highspots and lowspots
Smoothness#=6;The bigger the number the more smoothly the transition between highspots and low spots will occur


;Setup the 3D environment
Graphics3D 1024,700,16,2
SetBuffer BackBuffer()
Dim vert(GridSize,GridSize)
WireFrame True
AmbientLight(255,255,255)
cam=CreateCamera()
MoveEntity cam, 0, 50, -140 ; Our camera...
CameraRange cam,1,10000

;Create 4 copies of the same terrain and position them to show that they are tileable
RandomTerrain=TileableRandomDiamondSquareTerrain(GridSize,Landform,Smoothness)
RandomTerrain2=CopyMesh(RandomTerrain,RandomTerrain)
PositionEntity(RandomTerrain2,-45,0,0)
RandomTerrain3=CopyMesh(RandomTerrain,RandomTerrain)
PositionEntity(RandomTerrain3,-45,0,45)
RandomTerrain4=CopyMesh(RandomTerrain,RandomTerrain)
PositionEntity(RandomTerrain4,0,0,45)


While Not KeyDown(1)
	Cls
	TurnEntity RandomTerrain,0,0.5,0
	RenderWorld 
	Flip 
Wend
End
Function TileableRandomDiamondSquareTerrain(GridSize,Range#,Smoothness#)
	NumSquares=1; We start with 1 square that is the size of the mesh
	XS=1
	Iterations#=Log2(GridSize-1) ;In the loops we work with progressively smaller and smaller squares
											  ;This is the number of times to execute the loops to get to the smallest
											  ;possible square defined by the vertices.
	Terrain=CreateGrid(GridSize,GridSize)
	surf=GetSurface(Terrain,1)
	SeedRnd (MilliSecs()) 
	For i= 1 To iterations
		s=((GridSize-1)/XS)
		For z=1 To NumSquares/XS
		For x=1 To NumSquares/XS
			;Get the corner vertices of the square defined by z,x
			v1=0+(s*(x-1))+s*(z-1)*GridSize  :  v2=v1+s  :  v3=v1+s*GridSize  :  v4=v3+s
			;Now get the centre vertice of the z,x square and raise or lower it by a random amount
			vc=v1+s/2+(s/2)* GridSize
			AvgHeight=(VertexY(surf,v1)+VertexY(surf,v2)+VertexY(surf,v3)+VertexY(surf,v4))/4
			VertexCoords surf,vc,VertexX(surf,vc),Rand(AvgHeight-Range,AvgHeight+Range),VertexZ(surf,vc)
			
			;next we get the centre vertice of the left edge of the z.x square and raise or lower it by a random amount
			v5=vc-s/2  :  v15=v5-(s/2)*GridSize  :  v25=v15+(s/2)  :  v35=v5+(s/2)
			AvgHeight=(VertexY(surf,v15)+VertexY(surf,v25)+VertexY(surf,v35))/3
			VertexCoords surf,v5,VertexX(surf,v5),Rand(AvgHeight-Range,AvgHeight+Range),VertexZ(surf,v5)
			;if the vertex is on the left edge of the mesh then set the vertex on the right edge to the same height to ensure tileability
			If v5*1.0/gridsize*1.0=Int(v5/gridsize) Then
				ve=v5+gridsize-1:VertexCoords surf,ve,VertexX(surf,ve),VertexY(surf,v5),VertexZ(surf,ve)
			End If
			
			;next we get the centre vertice of the top edge of the z.x square and raise or lower it by a random amount
			v6=vc-(s/2)*GridSize  :  v16=v6+(s/2)   :  v26=v16+(s/2)*GridSize  :  v36=v26-(s/2)			
			AvgHeight=(VertexY(surf,v16)+VertexY(surf,v26)+VertexY(surf,v36))/3
			VertexCoords surf,v6,VertexX(surf,v6),Rand(AvgHeight-Range,AvgHeight+Range),VertexZ(surf,v6)
			;if the vertex is on the top edge of the mesh then set the vertex on the bottom edge to the same height to ensure tileability
			If v6<GridSize Then
				ve=v6+gridsize*(gridsize-1):VertexCoords surf,ve,VertexX(surf,ve),VertexY(surf,v6),VertexZ(surf,ve)
			End If

			;next we get the centre vertice of the right edge of the z.x square and raise or lower it by a random amount
			v7=vc+s/2  :  v17=v7-(s/2)   :  v27=v17+(s/2)*GridSize  :  v37=v27+(s/2)
			AvgHeight=(VertexY(surf,v17)+VertexY(surf,v27)+VertexY(surf,v37))/3
			VertexCoords surf,v7,VertexX(surf,v7),Rand(AvgHeight-Range,AvgHeight+Range),VertexZ(surf,v7)
			;if the vertex is on the right edge of the mesh then set the vertex on the left edge to the same height to ensure tileability
			If ((v7-gridsize+1)*1.0)/(gridsize*1.0)=Int((v7-gridsize+1)/(gridsize-1))  Then
				ve=v7-gridsize+1:VertexCoords surf,ve,VertexX(surf,ve),VertexY(surf,v7),VertexZ(surf,ve)
			End If
			
			;next we get the centre vertice of the bottom edge of the z.x square and raise or lower it by a random amount
			v8=vc+(s/2)*GridSize  :  v18=v8-(s/2)*GridSize  :  v28=v18+(s/2)  :  v38=v8+(s/2)
			AvgHeight=(VertexY(surf,v17)+VertexY(surf,v27)+VertexY(surf,v37))/3
			VertexCoords surf,v8,VertexX(surf,v8),Rand(AvgHeight-Range,AvgHeight+Range),VertexZ(surf,v8)
			;if the vertex is on the top edge of the mesh then set the vertex on the bottom edge to the same height to ensure tileability
			If v8>GridSize*(gridsize-1) Then
				ve=v8-gridsize*(gridsize-1):VertexCoords surf,ve,VertexX(surf,ve),VertexY(surf,v8),VertexZ(surf,ve)
			End If
			Next
		Next
		NumSquares=NumSquares*4
		xs=xs*2
		Print numsquares
		range=range/smoothness
	Next
	Return terrain
End Function
Function CreateGrid(x=9,z=9,w=5,h=5)
	mesh=CreateMesh()
	surf=CreateSurface(mesh)
	
	For xl=0 To x-1
		v=AddVertex(surf,xl*w,0,0)
		vert(xl,0)=v
	Next
	
	For zl=1 To z-1
		For xl=0 To x-1
			v=AddVertex(surf,xl*w,0,zl*-h)
			vert(xl,zl)=v
			If xl>0 Then
				AddTriangle surf,vert(xl,zl),vert(xl-1,zl),vert(xl,zl-1)
				AddTriangle surf,vert(xl,zl-1),vert(xl-1,zl),vert(xl-1,zl-1)					
			End If
			
		Next
	Next
	Return mesh
End Function
Function Log2# ( x# )
	Return Log( x ) / Log( 2 ) 
End Function
