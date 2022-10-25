; ID: 976
; Author: sswift
; Date: 2004-03-25 09:30:19
; Title: Calculate_FaceNormals
; Description: This function calculates the face normals for a mesh.

; Uses 25 megs of ram.  May replace with banks allocated temporarily.
; Assumes a max of 32 surfaces with 65536 vertices in each.
Dim Face_NX#(32, 65536)
Dim Face_NY#(32, 65536)
Dim Face_NZ#(32, 65536)

; -------------------------------------------------------------------------------------------------------------------
; This function calculates the face normals for a mesh.
;
; Should probably update this so that it can recursively loop through all of an entities children as well.
; -------------------------------------------------------------------------------------------------------------------
Function Calculate_FaceNormals(ThisMesh)
	
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

				; Compute their cross product.
				Nx# = Py#*Qz# - Pz#*Qy#
				Ny# = Pz#*Qx# - Px#*Qz#
				Nz# = Px#*Qy# - Py#*Qx#

				; Store the face normal.
				Face_NX#(LOOP_Surfaces, LOOP_Tris) = Nx#
				Face_NY#(LOOP_Surfaces, LOOP_Tris) = Ny#
				Face_NZ#(LOOP_Surfaces, LOOP_Tris) = Nz#

		Next

	Next

End Function
