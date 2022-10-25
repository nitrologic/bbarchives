; ID: 526
; Author: sswift
; Date: 2002-12-11 19:14:47
; Title: Remove overlapping triangles
; Description: This function removes overlapping triangles in a mesh, even across surfaces.  For example, if you build a level out of cubes, this function will remove all faces sandwidched between cubes.

Type Triangle							; This type is for the Remove_Coincident_Tris() function.
		
		Field Surface						; Pointer to the surface this triangle is in.
		Field Index							; The index number for this triangle in the specified surface.
		
	End Type 	


; -------------------------------------------------------------------------------------------------------------------
; This function detects all front-to-front facing tris in a mesh, rebuilds the mesh without them, and returns a
; pointer to the new mesh.
;
; In other words, if two triangles face eachother in a mesh, and both triangles touch at 3 vertices, then both
; will be removed.  If the faces face in the same direction they will not be removed.
;
; Note that it is possible for some vertices to be left around orphaned with no triangle connected to them if all
; triangles that were connected to them were coincident with other triangles, but that shouldn't cause any problems,
; as they won't be visible.
; -------------------------------------------------------------------------------------------------------------------
Function Remove_Coincident_Tris(ThisMesh)

	; Find all the coincident tris.

		Surfaces = CountSurfaces(ThisMesh)

		; Loop through each surface of this mesh.
		For Surface_Index_1 = 1 To Surfaces

			Surface1 = GetSurface(ThisMesh, Surface_Index_1)
			Tris = CountTriangles(Surface1)

			; Loop through each triangle in this surface.
			For Tri_Index_1 = 0 To (Tris-1)
				
				; This triangle has not yet been found to be coincident with any other.
				Coincident = False
				
				; Loop through every triangle after this triangle in this surface.
				For Tri_Index_2 = (Tri_Index_1+1) To (Tris-1)
			
					; If these triangles are coincident...
					If TrisCoincident(Surface1, Tri_Index_1, Surface1, Tri_Index_2)
				
						; Mark this triangle as having been found to be coincident.
						Coincident = True
				
						; Mark both triangles for removal.
						ThisTriangle.Triangle = New Triangle
						ThisTriangle\Surface = Surface1
						ThisTriangle\Index 	 = Tri_Index_1
					
						ThisTriangle.Triangle = New Triangle
						ThisTriangle\Surface = Surface1
						ThisTriangle\Index 	 = Tri_Index_2
	
						; Exit the Tri_Index_2 loop.
						Exit
					
					EndIf
						
				Next

		
				; If we found the first triangle to be coincident with another already, don't look for any more
				; triangles coincident with this one.
				If Not Coincident

					; Otherwise, we failed to find a coincident triangle in the same surface as the triangle we're
					; testing, so look in all the other surfaces.

					; Loop through every triangle in every surface after this surface.
					For Surface_Index_2 = Surface_Index_1+1 To Surfaces

						Surface2 = GetSurface(ThisMesh, Surface_Index_2)
						Tris2 = CountTriangles(Surface2)

						For Tri_Index_2 = 0 To Tris2-1
	
							; If these triangles are coincident...
							If TrisCoincident(Surface1, Tri_Index_1, Surface2, Tri_Index_2)
				
								; Mark this triangle as having been found to be coincident.
								Coincident = True
				
								; Mark both triangles for removal.
								ThisTriangle.Triangle = New Triangle
								ThisTriangle\Surface = Surface1
								ThisTriangle\Index 	 = Tri_Index_1
					
								ThisTriangle.Triangle = New Triangle
								ThisTriangle\Surface = Surface2
								ThisTriangle\Index 	 = Tri_Index_2
	
								; Exit the Tri_Index_2 loop.
								Exit
					
							EndIf

						Next						

						; If we found the first triangle to be coincident with another already, exit Surface_Index_2 loop.
						If Coincident Then Exit
		
					Next

				EndIf

			Next 
		
		Next
	
	
	; Delete all the coincident tris by constructing a new mesh without those tris from the old mesh.

		; Create a new mesh.
		NewMesh = CreateMesh()	

		; Loop through each surface of the mesh.
		For Surface_Index = 1 To Surfaces

			; Get the pointer to the this surface and the number of vertices in it.
			SrcSurface = GetSurface(ThisMesh, Surface_Index)
			Tris = CountTriangles(SrcSurface)
	
			; Create a new surface in the destination mesh to hold the copy of this surface's data.
			DestSurface = CreateSurface(NewMesh)
			
			; Copy all the vertices from the source surface to the destination surface.
			SrcVerts = CountVertices(SrcSurface)
			For VertLoop = 0 To SrcVerts-1
		
				Vx#  = VertexX#(SrcSurface, VertLoop)
				Vy#  = VertexY#(SrcSurface, VertLoop)
				Vz#  = VertexZ#(SrcSurface, VertLoop)
				Vu#  = VertexU#(SrcSurface, VertLoop)
				Vv#  = VertexV#(SrcSurface, VertLoop)		
				Vw#  = VertexW#(SrcSurface, VertLoop)
				Vnx# = VertexNX#(SrcSurface, VertLoop)
				Vny# = VertexNY#(SrcSurface, VertLoop)
				Vnz# = VertexNZ#(SrcSurface, VertLoop)						
				Vr   = VertexRed(SrcSurface, VertLoop)
				Vg   = VertexGreen(SrcSurface, VertLoop)
				Vb   = VertexBlue(SrcSurface, VertLoop)
				AddVertex(DestSurface, Vx#, Vy#, Vz#, Vu#, Vv#, Vw#)
				VertexNormal(DestSurface, VertLoop, Vnx#, Vny#, Vnz#)
				VertexColor(DestSurface, VertLoop, Vr, Vg, Vb) 
	
			Next

			; Copy all triangles from the source surface to the destination surface.	
			SrcTris  = CountTriangles(SrcSurface)
			For TriLoop = 0 To SrcTris-1
	
				Copy_Tri = True
				
				For ThisTri.Triangle = Each Triangle
									
					; If this triangle is a coincident triangle that should be removed...
					If (ThisTri\Surface = SrcSurface) And (ThisTri\Index = TriLoop) 
						
						; Do not copy the triangle.
						Copy_Tri = False
						
						; Exit ThisTri loop early.
						Exit
						
					EndIf
						
				Next
	
	
				; If it's okay to copy this triangle...
				If Copy_Tri			
		
					V0 = TriangleVertex(SrcSurface, TriLoop, 0)
					V1 = TriangleVertex(SrcSurface, TriLoop, 1)
					V2 = TriangleVertex(SrcSurface, TriLoop, 2)
					AddTriangle(DestSurface, V0, V1, V2)
		
				EndIf
	
			Next
		
		Next


	; Delete the old mesh.
	FreeEntity ThisMesh
		
	; Delete all the temporary coincident triangle data.		
	Delete Each Triangle

	; Return the pointer to the new mesh.
	Return NewMesh
	
	
End Function 


; -------------------------------------------------------------------------------------------------------------------
; This function returns the squared distance between two vertices.
; -------------------------------------------------------------------------------------------------------------------
Function VertexDist#(Surface1, Vert1, Surface2, Vert2)

	V1x# = VertexX#(Surface1, Vert1)
	V1y# = VertexY#(Surface1, Vert1)
	V1z# = VertexZ#(Surface1, Vert1)
	
	V2x# = VertexX#(Surface2, Vert2)
	V2y# = VertexY#(Surface2, Vert2)
	V2z# = VertexZ#(Surface2, Vert2)
	
	Return (V1x#-V2x#)*(V1x#-V2x#) + (V1y#-V2y#)*(V1y#-V2y#) + (V1z#-V2z#)*(V1z#-V2z#)	
	
End Function	


; -------------------------------------------------------------------------------------------------------------------
; This function returns true if two triangles are coincident.  (Occupy the same space.)
;
; Epsilon is the distance by which the vertices of the triangles can be seperated and still be considered coincident.
; -------------------------------------------------------------------------------------------------------------------
Function TrisCoincident(Surface1, Tri_Index_1, Surface2, Tri_Index_2, Epsilon#=0.001)
		

	; Square the epsilon so that we can use squared distances for speed in comparisons.	
	Epsilon# = Epsilon#*Epsilon# 	
		
		
	; Store the indices of the vertices which make up the triangles.
	T1_Vert0 = TriangleVertex(Surface1, Tri_Index_1, 0)		
	T1_Vert1 = TriangleVertex(Surface1, Tri_Index_1, 1)
	T1_Vert2 = TriangleVertex(Surface1, Tri_Index_1, 2)
			
	T2_Vert0 = TriangleVertex(Surface2, Tri_Index_2, 0)		
	T2_Vert1 = TriangleVertex(Surface2, Tri_Index_2, 1)
	T2_Vert2 = TriangleVertex(Surface2, Tri_Index_2, 2)


	; Check to see if all three vertices of these triangles are coincident.

		Coincident = True
			
		; Check to see if vertex 0 of triangle 1 is coincident with any of the vertices in triangle 2.
		If VertexDist#(Surface1, T1_Vert0, Surface2, T2_Vert0) > Epsilon#
			If VertexDist#(Surface1, T1_Vert0, Surface2, T2_Vert1) > Epsilon#
				If VertexDist#(Surface1, T1_Vert0, Surface2, T2_Vert2) > Epsilon#
					Coincident = False
				EndIf
			EndIf
		EndIf
		
		; If the first test passed...
		If Coincident
				
			; Check to see if vertex 1 of triangle 1 is coincident with any of the vertices in triangle 2.
			If VertexDist#(Surface1, T1_Vert1, Surface2, T2_Vert0) > Epsilon#
				If VertexDist#(Surface1, T1_Vert1, Surface2, T2_Vert1) > Epsilon#
					If VertexDist#(Surface1, T1_Vert1, Surface2, T2_Vert2) > Epsilon#
						Coincident = False
					EndIf
				EndIf
			EndIf

			; If the second test passed...						
			If Coincident
					
				; Check to see if vertex 2 of triangle 1 is coincident with any of the vertices in triangle 2.
				If VertexDist#(Surface1, T1_Vert2, Surface2, T2_Vert0) > Epsilon#
					If VertexDist#(Surface1, T1_Vert2, Surface2, T2_Vert1) > Epsilon#
						If VertexDist#(Surface1, T1_Vert2, Surface2, T2_Vert2) > Epsilon#
							Coincident = False
						EndIf
					EndIf
				EndIf
				
			EndIf
														
		EndIf	

	
	; Return whether the triangles were coincident or not.
	Return Coincident

			
End Function
