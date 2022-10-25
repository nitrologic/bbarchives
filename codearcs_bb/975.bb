; ID: 975
; Author: sswift
; Date: 2004-03-25 09:17:35
; Title: CalculateNormals
; Description: This function calculates face and vertex normals for a mesh using a method which works better for levels (and many other types of meshes) than the built in function.

Dim Face_NX#(32768)
Dim Face_NY#(32768)
Dim Face_NZ#(32768)
		
Dim Vertex_ConnectedTris(32768)
Dim Vertex_TriList(32768, 32)


; -------------------------------------------------------------------------------------------------------------------
; This function calculates and sets the normals for a mesh.
;
; Should probably update this so that it can recursively loop through all of an entities children as well.
; -------------------------------------------------------------------------------------------------------------------
Function Calculate_Normals(ThisMesh)
	
	; Loop through all surfaces of the mesh.
	Surfaces = CountSurfaces(ThisMesh)
	For LOOP_Surface = 1 To Surfaces

		Surface_Handle = GetSurface(ThisMesh, LOOP_Surface)

		; Reset the number of connected polygons for each vertex.
		For LoopV = 0 To 32767	
			Vertex_ConnectedTris(LoopV) = 0
		Next	
	
		; Loop through all triangles in this surface of the mesh.
		Tris = CountTriangles(Surface_Handle)
		For LOOP_Tris = 0 To Tris-1

			; Get the vertices that make up this triangle.
				Vertex_0 = TriangleVertex(Surface_Handle, LOOP_Tris, 0)
				Vertex_1 = TriangleVertex(Surface_Handle, LOOP_Tris, 1)
				Vertex_2 = TriangleVertex(Surface_Handle, LOOP_Tris, 2)
	
			; Adjust the number of triangles each vertex is connected to and
			; store this triangle in each vertex's list of triangles it is connected to.
				ConnectedTris = Vertex_ConnectedTris(Vertex_0)
				Vertex_TriList(Vertex_0, ConnectedTris) = LOOP_Tris
				Vertex_ConnectedTris(Vertex_0) = ConnectedTris + 1

				ConnectedTris = Vertex_ConnectedTris(Vertex_1)
				Vertex_TriList(Vertex_1, ConnectedTris) = LOOP_Tris
				Vertex_ConnectedTris(Vertex_1) = ConnectedTris + 1

				ConnectedTris = Vertex_ConnectedTris(Vertex_2)
				Vertex_TriList(Vertex_2, ConnectedTris) = LOOP_Tris
				Vertex_ConnectedTris(Vertex_2) = ConnectedTris + 1

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

				; Compute their cross product.
				Nx# = Py#*Qz# - Pz#*Qy#
				Ny# = Pz#*Qx# - Px#*Qz#
				Nz# = Px#*Qy# - Py#*Qx#

				; Store the face normal.
				Face_NX#(LOOP_Tris) = Nx#
				Face_NY#(LOOP_Tris) = Ny#
				Face_NZ#(LOOP_Tris) = Nz#

		Next

		; Now that all the face normals for this surface have been calculated, calculate the vertex normals.
		Vertices = CountVertices(Surface_Handle)
		For LOOP_Vertices = 0 To Vertices-1

			; Reset this normal.
			Nx# = 0
			Ny# = 0
			Nz# = 0

			; Add the normals of all polygons which are connected to this vertex.
			Polys = Vertex_ConnectedTris(LOOP_Vertices)
				
			For LOOP_Polys = 0 To Polys-1

				ThisPoly = Vertex_TriList(LOOP_Vertices, LOOP_Polys)

				Nx# = Nx# + Face_NX#(ThisPoly)
				Ny# = Ny# + Face_NY#(ThisPoly)
				Nz# = Nz# + Face_NZ#(ThisPoly)			
				
			Next	
				
			; Normalize the new vertex normal.
			; (Normalizing is scaling the vertex normal down so that it's length = 1)

				Nl# = Sqr(Nx#^2 + Ny#^2 + Nz#^2)

				; Avoid a divide by zero error if by some freak accident, the vectors add up to 0.
				; If Nl# = 0 Then Nl# = 0.1

				Nx# = Nx# / Nl#
				Ny# = Ny# / Nl#
				Nz# = Nz# / Nl#

			; Set the vertex normal.
			
				VertexNormal Surface_Handle, LOOP_Vertices, Nx#, Ny#, Nz#
				;VertexColor Surface_Handle, LOOP_Vertices, polys*127, polys*127, polys*127
		
		Next

	Next

End Function
