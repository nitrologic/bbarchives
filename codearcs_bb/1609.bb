; ID: 1609
; Author: Ross C
; Date: 2006-02-05 07:57:53
; Title: Mesh LoadTerrain
; Description: Creates a mesh from a heightmap image you choose. Doesn't use blitz terrains.

; Y position is calculated by: scale * pixel colour on loaded image.

; PLEASE NOTE: You must clamp the texture your applying if it is spread across the whole mesh.
;              If you don't, it will bleed across to the opposite side. Clamp with flags 16 + 32
Function load_terrain(heightmap$,y_scale#=1,texture1$="",t_flag1 = 1,texture2$="",t_flag2 = 1,texture3$="",t_flag3 = 1)


	temp = LoadImage(heightmap$)
	If temp = 0 Then Return 0
	
	x = ImageWidth(temp)
	DebugLog(" image width = "+x)
	y = ImageHeight(temp)

	mesh = CreateMesh()
	surf = CreateSurface(mesh)

	For ly = 0 To y-1 ; your doing -1 because the image pixels start at 1 and the vertex
					  ; indices start at 0
		For lx = 0 To x-1
			AddVertex surf,lx , 0, -ly, Float(lx)/(x-1), Float(ly)/(y-1)
		Next
	Next
	RenderWorld
	
	AddTriangle surf, 0, 65,64
	AddTriangle surf, 0, 1 ,65
	For ly = 0 To y-2
		For lx = 0 To x-2 ; leave the vertex counting an extra unit short, as the code below reaches
						  ; reaches one unit ahead to create the triangles

			; below is to simply explain the creation process and order of the triangles.
			; Triangles must be created in clockwise order, facing the camera, to be seen.
			current_y = ly*y
			next_y = (ly+1)*y
			current_x = lx
			next_x = lx+1
			;create the quad
			AddTriangle surf, current_y+current_x, next_y+next_x    , next_y + current_x
			AddTriangle surf, current_y+current_x, current_y+next_x , next_y + next_x
		Next
	Next


	PositionMesh mesh, -x/2.0,0,y/2.0 ; centre the mesh on the world axis.

	SetBuffer ImageBuffer(temp)
	For ly = 0 To y-1
		For lx = 0 To x-1
			GetColor lx,ly
			index = (ly*y) + lx
			VertexCoords surf, index , VertexX(surf,index), ColorRed()*y_scale, VertexZ(surf,index)
		Next
	Next
	SetBuffer BackBuffer()
	
	UpdateNormals mesh
	
	If texture1 <> "" Then
		t1 = LoadTexture(texture1,t_flag1)
		If t1 = 0 Then RuntimeError(" Error in load_terrain() function: Texture1 not found from filename given")
		EntityTexture mesh,t1,0,0
	End If
	If texture2 <> "" Then
		t2 = LoadTexture(texture2,t_flag2)
		If t2 = 0 Then RuntimeError(" Error in load_terrain() function: Texture2 not found from filename given")
		EntityTexture mesh,t2,0,1
	End If
	If texture3 <> "" Then
		t3 = LoadTexture(texture3,t_flag3)
		If t3 = 0 Then RuntimeError(" Error in load_terrain() function: Texture3 not found from filename given")
		EntityTexture mesh,t3,0,2
	End If
		
	
	Return mesh
	
End Function
