; ID: 977
; Author: sswift
; Date: 2004-03-25 10:45:36
; Title: Calculate_FrontFacingVertices
; Description: This function calculates all vertices in a mesh which are visible to a camera.

Const MAX_SURFACES_PER_MESH    = 32
Const MAX_VERTICES_PER_SURFACE = 65536

Dim Vertex_FrontFacing(MAX_SURFACES_PER_MESH, MAX_VERTICES_PER_SURFACE)


; -------------------------------------------------------------------------------------------------------------------
; This function calculates which vertcies in a mesh are visible to the camera.
;
; Should probably update this so that it can recursively loop through all of an entities children as well.
; -------------------------------------------------------------------------------------------------------------------
Function Calculate_FrontFacingVertices(ThisMesh, Camera)
	
	; Calculate the normal which indicates the direction the camera is pointing.	
	TFormNormal 0,0,1, Camera, 0	
	CNx# = TFormedX#()
	CNy# = TFormedY#()
	CNz# = TFormedZ#()
	
	; Loop through all surfaces of the mesh.
	Surfaces = CountSurfaces(ThisMesh)
	For LOOP_Surfaces = 1 To Surfaces

		Surface_Handle = GetSurface(ThisMesh, LOOP_Surfaces)
	
		; Loop through all triangles in this surface of the mesh.
		Tris = CountTriangles(Surface_Handle)
		For LOOP_Tris = 0 To Tris-1

			; Get the vertices that make up this triangle.
				Vertex_0 = TriangleVertex(Surface_Handle, LOOP_Tris, 0)
				Vertex_1 = TriangleVertex(Surface_Handle, LOOP_Tris, 1)
				Vertex_2 = TriangleVertex(Surface_Handle, LOOP_Tris, 2)

			; Calculate the normal for this face.

				; Get the corners of this face:
				Ax# = VertexX#(Surface_Handle, Vertex_0)
				Ay# = VertexY#(Surface_Handle, Vertex_0)
				Az# = VertexZ#(Surface_Handle, Vertex_0)

				Bx# = VertexX#(Surface_Handle, Vertex_1)
				By# = VertexY#(Surface_Handle, Vertex_1)
				Bz# = VertexZ#(Surface_Handle, Vertex_1)

				Cx# = VertexX#(Surface_Handle, Vertex_2)
				Cy# = VertexY#(Surface_Handle, Vertex_2)
				Cz# = VertexZ#(Surface_Handle, Vertex_2)

				; Triangle 1
				; Get the vectors for two edges of the triangle.
				Px# = Ax#-Bx#
				Py# = Ay#-By#
				Pz# = Az#-Bz#

				Qx# = Bx#-Cx#
				Qy# = By#-Cy#
				Qz# = Bz#-Cz#

				; Compute their cross product to get the face normal.
				FNx# = Py#*Qz# - Pz#*Qy#
				FNy# = Pz#*Qx# - Px#*Qz#
				FNz# = Px#*Qy# - Py#*Qx#

			; Compute the dot product of the face and camera normal.
				DP# = FNx#*CNx# + FNy#*CNy# + FNz#*CNz#

			; Is this face facing towards the camera?
				If DP# > 0
				
					; Set all vertcies connected to this triangle to front-facing.
					Vertex_FrontFacing(LOOP_Surfaces, Vertex_0) = True
					Vertex_FrontFacing(LOOP_Surfaces, Vertex_1) = True
					Vertex_FrontFacing(LOOP_Surfaces, Vertex_2) = True
					
				EndIf	

		Next

	Next

End Function
