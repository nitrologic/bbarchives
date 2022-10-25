; ID: 446
; Author: sswift
; Date: 2002-10-02 00:26:51
; Title: Cel Shader
; Description: Creates black outlines around a mesh so it looks like a cartoon.

; -------------------------------------------------------------------------------------------------------------------
; This function sets an entity to be cel shaded.
; If you need to scale your entity before applying cel shading then you should use scalemesh, not scaleentity.
; -------------------------------------------------------------------------------------------------------------------
Function Cel_Shade(Shaded_Entity, OutlineScale#=0.025)


	; Copy the entity's mesh.		
	Outline_Entity = CopyMesh(Shaded_Entity) 		
						
	; Update the vertex normals so that all surfaces are smoothed between.  We can't have normals at corners
	; which point in diffrent directions for each face or the outline will have cracks.
	UpdateNormals Outline_Entity 
			
	; Disable lighting & fog.
	EntityFX Outline_Entity, 1+8
	
	
	; Loop through all surfaces.
	Surfaces = CountSurfaces(Outline_Entity)
	For LOOP_Surface = 1 To Surfaces

		Surface_Handle = GetSurface(Outline_Entity, LOOP_Surface)
	
		; Loop through all vertices in this surface.				
		Verts = CountVertices(Surface_Handle) - 1
		For LOOP_Verts = 0 To Verts-1
			
			; Move the vertices out in the direction of the vertex normals, which now point inwards because
			; the mesh was inverted.  Move them by the distance specified in the shader this object uses.

			Vx#  = VertexX#(Surface_Handle, LOOP_Verts)
			Vy#  = VertexY#(Surface_Handle, LOOP_Verts)
			Vz#  = VertexZ#(Surface_Handle, LOOP_Verts)

			VNx# = VertexNX#(Surface_Handle, LOOP_Verts)
			VNy# = VertexNY#(Surface_Handle, LOOP_Verts)
			VNz# = VertexNZ#(Surface_Handle, LOOP_Verts)
										
			VertexCoords Surface_Handle, LOOP_Verts, Vx#+(VNx#*OutlineScale#), Vy#+(VNy#*OutlineScale#), Vz#+(VNz#*OutlineScale#)
				
		Next

	Next


	; Turn the mesh inside out.
	FlipMesh Outline_Entity
	
	; Set the entity to be black.
	EntityColor Outline_Entity, 0, 0, 0

	; Place the entity at the same location as the original entity.
	PositionEntity Outline_Entity, EntityX#(Shaded_Entity, True), EntityY#(Shaded_Entity, True), EntityZ#(Shaded_Entity, True)
	
	; Parent the outline to the original entity so it moves with it automatically.
	EntityParent Outline_Entity, Shaded_Entity


End Function
