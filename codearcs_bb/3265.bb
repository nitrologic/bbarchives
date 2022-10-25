; ID: 3265
; Author: _PJ_
; Date: 2016-04-20 09:26:19
; Title: Convert BlitzTerrain To Mesh
; Description: Convert Blitz Terrain To Mesh

Function h3d_MESH_GenerateMeshFromTerrain(Terrain,StartX=0,StartZ=0,MeshSize=0)
	;TERRAIN MUST NOT BE ROTATED (Might do this later but it's really complicated... Maybe require TFormPoint or something with the Entitypitch and Yaw values)
	; No need to know SCALE factor - Mesh is generated to current actual scaled size of terrain
	
	;Establish Parameters
	Local Size=TerrainSize(Terrain)
	
	Local XOffset#=EntityX(Terrain,True)
	Local YOffset#=EntityY(Terrain,True)
	Local ZOffset#=EntityZ(Terrain,True)
	
	Local UVRatio#=1.0/Size
	
	;Declare locals
	
	Local U#
	Local V#
	Local W#
	
	Local X#
	Local Y#
	Local Z#
	
	Local TX#
	Local TY#
	Local TZ#
	
	Local v0
	Local v1
	Local v2
	Local v3
	
	Local Mesh=CreateMesh()
	Local Surface=CreateSurface(Mesh)
	
	For Z=0 To MeshSize-1
		For X=0 To MeshSize-1
			
			;Adjust for position
			TX=StartX+X+XOffset
			TZ=StartZ+Z+ZOffset
			
			;Vertex0
			Y#=GetRelativeTerrainHeight(Terrain,TX+1,TZ,YOffset)
			
			U#=(X+1)*UVRatio
			V#=1-(Z*UVRatio)
			W=(Z*UVRatio)
			
			v0=AddVertex(Surface,X+1,Y,Z,U,V,W)
			
			;Vertex1
			Y#=GetRelativeTerrainHeight(Terrain,TX,TZ,YOffset)
			
			U#=X*UVRatio
			V#=1-(Z*UVRatio)
			W=Z*UVRatio
			
			v1=AddVertex(Surface,X,Y,Z,U,V,W)
			
			;Vertex2
			Y#=GetRelativeTerrainHeight(Terrain,TX+1,TZ+1,YOffset)
			
			U#=(X+1)*UVRatio
			V#=1-((Z+1)*UVRatio)
			W=(Z+1)*UVRatio
			
			v2=AddVertex(Surface,X+1,Y,Z+1,U,V,W)
			
			;Vertex3
			Y#=GetRelativeTerrainHeight(Terrain,TX,TZ+1,YOffset)
			
			U#=X*UVRatio
			V#=1-((Z+1)*UVRatio)
			W=(Z+1)*UVRatio
			
			v3=AddVertex(Surface,X,Y,Z+1,U,V,W)
			
			;Create Surface Polys
			
			AddTriangle(Surface,v0,v1,v2)
			AddTriangle(Surface,v1,v3,v2)
			
		Next
	Next
	
	UpdateNormals Mesh
	
	PositionEntity Mesh,XOffset+StartX,YOffset,ZOffset+StartZ,True
	
	Return Mesh
	
End Function

Function GetRelativeTerrainHeight#(Terrain,X#,Z#,YOffset#)
	;Converts absolute value to relative
	Local AbsoluteHeight#=TerrainY(Terrain,X,0,Z)
	Return AbsoluteHeight#-YOffset
End Function
