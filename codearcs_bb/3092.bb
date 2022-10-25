; ID: 3092
; Author: big10p
; Date: 2013-12-09 09:21:20
; Title: Vent_lib. A B3D-like vector GFX system
; Description: Duplicates the B3D commandset for vector entities

;
; (vent_lib.bb)
;
; VentLib - 2D Vector Entity System Library.
;
;
;


	;
	; Constants.
	;

	Const VL_VERT_BUFF_SIZE% = 100
	Const VL_LINE_BUFF_SIZE% = 100
	
	Const VL_PICK_MODE_NONE%		= 0
	Const VL_PICK_MODE_CIRCLE%	= 1
	Const VL_PICK_MODE_POLY%		= 2
	Const VL_PICK_MODE_BOX%			= 3

	Const VL_CLASS_PIVOT%	= 0
	Const VL_CLASS_MESH%	= 1	

	;
	; Globals.
	;
	
	; Linked list of all pickable entities.
	Global vl_first_pickable.vl_elinkT = Null
	
	; Tform command results.
	Global vl_tformed_x#
	Global vl_tformed_y#

	; Screen scale metrics.
	Global vl_screen_units_x#		= 10.0
	Global vl_screen_units_y#
	Global vl_unit_size_pixels#
	
	; Default vertex render size.
	Global vl_vertex_size% = 0
	
	; Default vector line flicker intensity.
	Global vl_vector_flicker# = 0;.25
	
	; vl__lines_intersect_xy(), vl__line_intersects_circle() results.
	Global vl_intersect_x#
	Global vl_intersect_y#	
	
	; Latest pick status variables.
	Global vl_picked_entity.vl_entityT	= Null
	Global vl_picked_line%							= -1
	Global vl_picked_x#									= 0.0
	Global vl_picked_y#									= 0.0
	Global vl_picked_nx#								= 0.0
	Global vl_picked_ny#								= 0.0
	
	; Latest picked point and pick line deltas from pick origin - needed for vl_picked_time().
	Global vl_picked_dx#								= 0.0
	Global vl_picked_dy#								= 0.0
	Global vl_pick_line_dx#							= 0.0
	Global vl_pick_line_dy#							= 0.0

	
	;
	; Types.
	;
	
	; Main entity type.
		
	Type vl_entityT
		Field visuals.vl_visualsT					; Visual data required by mesh class entities.

		Field x#													; Local X coord of entity origin.
		Field y#													; Local Y coord of entity origin.
		Field scale_x#										; Local X scale of entity.
		Field scale_y#										; Local Y scale of entity.
		Field rot#												; Local rotation angle of entity.

		Field parent.vl_entityT						; Entity's parent entity.
		Field first_child.vl_elinkT				; Linked list of child entities.

		Field class%											; Entity class type.
		Field hidden%											; Visibility status (can be overidden if parent is hidden).
		Field name$												; Entity name.
		Field pick_mode%									; Entity pick mode.
		Field obscurer%										; Entity obscurer status used by vl_entity_visible().
		Field radius#											; Entity pick radius.
		Field box_x#											; Entity pick box X position.
		Field box_y#											; Entity pick box Y position.
		Field box_width#									; Entity pick box width.
		Field box_height#									; Entity pick box height.
	End Type

	; Mesh entity visual data type.
	
	Type vl_visualsT
		Field mesh.vl_meshT								; Entity mesh data.

		Field order%											; Draw order.
		Field r%, g%, b%									; Entity color.
		Field brightness#									; Entity brightness.
		
		Field draw_me%										; Flags to renderer whether entity is to be drawn, or not.
		Field vert_x#[VL_VERT_BUFF_SIZE]	; Transformed vertices buffer - X coords.
		Field vert_y#[VL_VERT_BUFF_SIZE]	; Transformed vertices buffer - Y coords
	End Type

	; Mesh type.
		
	Type vl_meshT
		Field first_user.vl_elinkT					; Linked list of entities using this mesh.
		
		Field last_vert_i%									; Index of last defined vertex.
		Field vert_x#[VL_VERT_BUFF_SIZE]		; Mesh vertices buffer - X coords.
		Field vert_y#[VL_VERT_BUFF_SIZE]		; Mesh vertices buffer - Y coords.
		
		Field last_line_i%									; Index of last defined line.
		Field line_v0_i%[VL_LINE_BUFF_SIZE]	; Mesh vector line buffer - start coords.
		Field line_v1_i%[VL_LINE_BUFF_SIZE]	; Mesh vector line buffer - end coords.
		Field line_hard%[VL_LINE_BUFF_SIZE] ; Flags indicating if line is hard(1) or soft(0).
	End Type


	; Entity linked list type.
	
	Type vl_elinkT
		Field entity.vl_entityT			; Entity being referenced.
		Field next_elink.vl_elinkT	; Next entity link in list.
	End Type








;
;
;
Function vl_graphics(width%, height%, depth% = 0, mode% = 0)

	Graphics width, height, depth, mode

	vl_scale_graphics(vl_screen_units_x)
	
End Function


;
;
;
Function vl_scale_graphics(scale#)

	vl_screen_units_x		= scale
	vl_screen_units_y		= vl_screen_units_x * (Float(GraphicsHeight()) / GraphicsWidth())
	vl_unit_size_pixels	= (GraphicsWidth() - 1.0) / vl_screen_units_x

End Function


;
;
;
Function vl_graphics_width#()

	Return vl_screen_units_x

End Function


;
;
;
Function vl_graphics_height#()

	Return vl_screen_units_y

End Function


;
; Renders all visible entities to the currently set drawing buffer.
;
Function vl_render_world(clear% = True)

	;
	; Reset all entity render flags.
	;
	
	For visuals.vl_visualsT = Each vl_visualsT

		visuals\draw_me = False

	Next
	

	;
	; Transform all visible entities into actual screen coords.
	;
	
	For this.vl_entityT = Each vl_entityT

		If (this\parent = Null) And (Not this\hidden)

			If this\class = VL_CLASS_MESH
				; This entity has no parent so it's local coord system IS the global coord system.
				vl__tform_entity(this, this\x, this\y, this\rot, this\scale_x, this\scale_y)
			EndIf
						
			; Recursively tranform all this entity's children.
			If this\first_child <> Null
				vl__tform_children(this, this\x, this\y, this\rot, this\scale_x, this\scale_y)
			EndIf

		EndIf

	Next

	
	;
	; Draw all visible entities.
	;
	
	red		= ColorRed() 
	green	= ColorGreen()
	blue	= ColorBlue()

	If clear Then Cls
	
	LockBuffer GraphicsBuffer()

	If vl_vertex_size ; Draw vector lines AND verts...

		For visuals.vl_visualsT = Each vl_visualsT
	
			If visuals\draw_me
		
				mesh.vl_meshT = visuals\mesh
				
				brightness# = visuals\brightness
				flicker# = 1.0 - Rnd(0.0, vl_vector_flicker)
				r = (visuals\r * brightness) * flicker
				g = (visuals\g * brightness) * flicker
				b = (visuals\b * brightness) * flicker
		
				Color r, g, b
				
				;
				; Draw all lines defined in this mesh.
				;
				
				For i = 0 To mesh\last_line_i
		
					; Get line start and end vertex indices.
					v0_i = mesh\line_v0_i[i]
					v1_i = mesh\line_v1_i[i]
					
					; Draw vector line.
					Line visuals\vert_x[v0_i], visuals\vert_y[v0_i], visuals\vert_x[v1_i], visuals\vert_y[v1_i]
		
				Next
		
				;				
				; Draw verts at double brightness.
				;
				
				If r >= 128 Then r = 255 Else r = (r Shl 1)
				If g >= 128 Then g = 255 Else g = (g Shl 1)
				If b >= 128 Then b = 255 Else b = (b Shl 1)

				Color r, g, b
	
				If vl_vertex_size = 1 ; Draw simple 1x1 verts...
				
					For i = 0 To mesh\last_vert_i
			
						vx# = visuals\vert_x[i]
						vy# = visuals\vert_y[i]
						
						Line vx, vy, vx, vy
			
					Next

				Else ; Draw 3x3 verts...

					For i = 0 To mesh\last_vert_i
			
						vx# = visuals\vert_x[i]
						vy# = visuals\vert_y[i]
						
						Line vx-1, vy, vx+1, vy
						Line vx, vy-1, vx, vy+1
			
					Next

				EndIf
										
			EndIf

		Next

	Else ; Draw vector lines only...
	
		For visuals.vl_visualsT = Each vl_visualsT
	
			If visuals\draw_me
			
				mesh.vl_meshT = visuals\mesh
				
				brightness# = visuals\brightness
				flicker# = 1.0 - Rnd(0.0, vl_vector_flicker)
				r = (visuals\r * brightness) * flicker
				g = (visuals\g * brightness) * flicker
				b = (visuals\b * brightness) * flicker

				Color r, g, b
		
				;
				; Draw all lines defined in this mesh.
				;
				
				For i = 0 To mesh\last_line_i
		
					; Get line start and end vertex indices.
					v0_i = mesh\line_v0_i[i]
					v1_i = mesh\line_v1_i[i]
					
					; Draw vector line.
					Line visuals\vert_x[v0_i], visuals\vert_y[v0_i], visuals\vert_x[v1_i], visuals\vert_y[v1_i]
		
				Next
		
				;x# = vl_screen(vl__entity_x(this, True))
				;y# = vl_screen(vl__entity_y(this, True))
				;Line x, y, x, y

			EndIf

		Next

	EndIf
	
	UnlockBuffer GraphicsBuffer()
	
	; Reinstate original color.
	Color red, green, blue
	 
End Function


;
; Sets the flicker intensity to use when drawing vector lines.
;
; Params:
; value - Level of flicker to use. 0 = extreme flicker, 1 = no flicker.
;
Function vl_vector_flicker(value#)

	If value < 0.0

		value = 0.0

	ElseIf value > 1.0

		value = 1.0

	EndIf
	
	vl_vector_flicker = value

End Function


;
; Sets the size vertices should be drawn at.
;
; params:
; size - Size level all vertices are to be drawn at.
;        0 = Vertices are not drawn.
;        1 = Vertices are drawn using a single pixel.
;        2 = Vertices are drawn using a 3x3 pixel cross.
;
Function vl_vertex_size(size%)

	If size =< 0

		vl_vertex_size = 0

	ElseIf size >= 2

		vl_vertex_size = 2

	Else

		vl_vertex_size = 1

	EndIf

End Function


;
; Creates a blank mesh entity. Geometry can then be added to this mesh
; by using the vl_add_vertex and vl_add_line commands.
;
; Params:
; parent_ID - Handle of entity to be made the new mesh entity's parent.
;             The new mesh entity will then adopt the parent's position/rotation/scale.
;
; Returns:
; The handle of the newly created mesh entity.
;
Function vl_create_mesh(parent_ID% = 0)

	entity.vl_entityT		= New vl_entityT
	visuals.vl_visualsT	= New vl_visualsT
	mesh.vl_meshT				= New vl_meshT

	entity_ID = Handle(entity)

	; Initialize new entity.
	
	entity\visuals			= visuals

	If parent_ID Then vl__add_child(Object.vl_entityT(parent_ID), entity)

	entity\first_child	= Null

	entity\x						= 0.0
	entity\y						= 0.0
	entity\scale_x			= 1.0
	entity\scale_y			= 1.0
	entity\rot					= 0.0

	entity\class 				= VL_CLASS_MESH
	entity\hidden				= False
	entity\name					= ""
	entity\pick_mode		= VL_PICK_MODE_NONE
	entity\obscurer			= False
	entity\radius				= 1.0
	entity\box_x				= -1.0
	entity\box_y				= -1.0
	entity\box_width		= 2.0
	entity\box_height		= 2.0

	; Initialize new entity visual data.	

	visuals\mesh				= mesh
	
	vl__entity_order(entity, 0)

	visuals\r						= 255
	visuals\g						= 255
	visuals\b						= 255
	visuals\brightness	= 0.5
	
	; Initialize new mesh.
		
	vl__add_user(mesh, entity)
	
	mesh\last_vert_i	= -1
	mesh\last_line_i	= -1

	Return entity_ID

End Function


;
; Creates a polygon mesh entity.
;
; Params:
; sides     - Number of sides the polygon should have.
; parent_ID - Handle of entity to be made the new mesh entity's parent.
;             The new mesh entity will then adopt the parent's position/rotation/scale.
;
; Returns:
; The handle of the newly created polygon mesh entity.
;
Function vl_create_polygon(sides% = 3, parent_ID% = 0)

	mesh = vl_create_mesh(Parent_ID)
	
	total_verts	= sides
	vert_ang#		= 0.0
	delta_ang#	= 360.0 / sides

	; Add verts for all corners of polygon.
	For n = 0 To total_verts - 1

		vl_add_vertex(mesh, Cos(vert_ang), Sin(vert_ang))

		vert_ang = vert_ang + delta_ang
	Next	

	; Add vector lines.
	For n = 0 To total_verts - 2

		vl_add_line(mesh, n, n + 1)

	Next

	vl_add_line(mesh, total_verts - 1, 0)
	
	Return mesh
	
End Function


;
; Creates a box/square mesh entity.
;
; Params:
; parent_ID - Handle of entity to be made the new mesh entity's parent.
;             The new mesh entity will then adopt the parent's position/rotation/scale.
;
; Returns:
; The handle of the newly created box mesh entity.
;
Function vl_create_box(parent_ID% = 0)

	mesh = vl_create_mesh(Parent_ID)
	
	vl_add_vertex(mesh,  1, -1)
	vl_add_vertex(mesh,  1,  1)
	vl_add_vertex(mesh, -1,  1)
	vl_add_vertex(mesh, -1, -1)

	vl_add_line(mesh, 0, 1)
	vl_add_line(mesh, 1, 2)
	vl_add_line(mesh, 2, 3)
	vl_add_line(mesh, 3, 0)
	
	Return mesh
	
End Function


;
; Creates a star-shaped mesh entity.
;
; Params:
; points    - Number of points the star should have.
; indent    - Sets the 'depth' of the indent between the points of the star.
; parent_ID - Handle of entity to be made the new mesh entity's parent.
;             The new mesh entity will then adopt the parent's position/rotation/scale.
;
; Returns:
; The handle of the newly created star mesh entity.
;
Function vl_create_star(points% = 5, indent# = 0.5, parent_ID% = 0)

	mesh = vl_create_mesh(Parent_ID)
	
	total_verts	= points * 2
	vert_ang#		= 0.0
	delta_ang#	= 360.0 / total_verts

	; Add verts for all star points & indents.
	For n = 0 To total_verts - 1

		If (n And 1) Then size# = indent Else size# = 1.0

		vl_add_vertex(mesh, Cos(vert_ang) * size, Sin(vert_ang) * size)

		vert_ang = vert_ang + delta_ang

	Next

	; Add vector lines.
	For n = 0 To total_verts - 2

		vl_add_line(mesh, n, n + 1)

	Next

	vl_add_line(mesh, total_verts - 1, 0)

	Return mesh
	
End Function


;
; Creates a pivot entity.
;
; Params:
; parent_ID - Handle of entity to be made the new pivot entity's parent.
;             The new pivot will then adopt the parent's position/rotation/scale.
;
; Returns:
; The handle of the newly created pivot entity.
;
Function vl_create_pivot(parent_ID% = 0)

	entity.vl_entityT		= New vl_entityT

	entity_ID = Handle(entity)

	; Initialize new entity.
	
	entity\visuals			= Null 

	If parent_ID Then vl__add_child(Object.vl_entityT(parent_ID), entity)

	entity\first_child	= Null

	entity\x						= 0.0
	entity\y						= 0.0
	entity\scale_x			= 1.0
	entity\scale_y			= 1.0
	entity\rot					= 0.0

	entity\class 				= VL_CLASS_PIVOT
	entity\hidden				= False
	entity\name					= ""
	entity\pick_mode		= VL_PICK_MODE_NONE
	entity\obscurer			= False
	entity\radius				= 1.0
	entity\box_x				= -1.0
	entity\box_y				= -1.0
	entity\box_width		= 2.0
	entity\box_height		= 2.0

	Return entity_ID

End Function


;
; Creates a point entity. Point entities disply as a single pixel when rendered.
;
; Params:
; parent_ID - Handle of entity to be made the new point entity's parent.
;             The new point entity will then adopt the parent's position/rotation/scale.
;
; Returns:
; The handle of the newly created point entity.
;
Function vl_create_point(parent_ID% = 0)

	mesh = vl_create_mesh(Parent_ID)
	
	vl_add_vertex(mesh, 0, 0)

	vl_add_line(mesh, 0, 0)
	
	Return mesh

End Function


;
; Deletes an entity and frees all it's resources. All the entity's children are also freed.
;
; Params:
; entity_ID  - Handle of entity to be freed.
;
Function vl_free_entity(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	
	; Recursively free any child entities.
	If entity\first_child <> Null Then vl__free_children(entity)

	If entity\class = VL_CLASS_MESH	

		mesh.vl_meshT = entity\visuals\mesh
		vl__delete_user(mesh, entity)
		
		; Delete this mesh if no other entities are using it.
		If mesh\first_user = Null Then Delete mesh
	
		Delete entity\visuals

	EndIf

	If entity\pick_mode Then vl__delete_pickable(entity)

	If entity\parent <> Null Then vl__delete_child(entity)
	
	Delete entity
			
End Function


;
; Rotates all the vertices of a mesh by the specified rotation angle.
;
; Params:
; entity_ID  - Handle of entity referencing the mesh to be rotated.
; rot        - Angle to rotate the mesh by.
;
Function vl_rotate_mesh(entity_ID%, rot#)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	
	mesh.vl_meshT = entity\visuals\mesh

	cos_rot# = Cos(rot)
	sin_rot# = Sin(rot)

	For i = 0 To mesh\last_vert_i

		vert_x# = mesh\vert_x[i]
		vert_y# = mesh\vert_y[i]

		mesh\vert_x[i] = (vert_x * cos_rot) - (vert_y * sin_rot)
		mesh\vert_y[i] = (vert_y * cos_rot) + (vert_x * sin_rot)
		
	Next

End Function


;
; Moves/Offsets all vertices of a mesh by the specified x,y amounts.
;
; Params:
; entity_ID  - Handle of entity referencing the mesh to be positioned.
; x          - X amount to move all vertices by.
; y          - Y amount to move all vertices by.
;
Function vl_position_mesh(entity_ID%, x#, y#)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	mesh.vl_meshT = entity\visuals\mesh

	For i = 0 To mesh\last_vert_i

		mesh\vert_x[i] = mesh\vert_x[i] + x
		mesh\vert_y[i] = mesh\vert_y[i] + y
		
	Next

End Function


;
; Adds a copy of one mesh to another mesh.
;
; Params:
; source_entity_ID - Handle of entity referencing the mesh to be copied.
; dest_entity_ID   - Handle of entity referencing the mesh to be added to.
; hardness         - Indicates what hardness the copied mesh lines should be given
;                    when added to the destination mesh:
;                    0 = Set all lines to be soft (unpickable).
;                    1 = Set all lines to be hard (pickable).
;                    2 = (default) Keep lines same as defined in source mesh.
;
Function vl_add_mesh(source_entity_ID%, dest_entity_ID%, hardness% = 2)

	If hardness < 0 Then hardness = 0
	
	source_entity.vl_entityT = Object.vl_entityT(source_entity_ID)
	source_mesh.vl_meshT = source_entity\visuals\mesh

	dest_entity.vl_entityT = Object.vl_entityT(dest_entity_ID)
	dest_mesh.vl_meshT = dest_entity\visuals\mesh

	; Copy all vertices from source mesh to destination mesh.
	
	vert_i_offset = dest_mesh\last_vert_i + 1
	dest_mesh\last_vert_i = dest_mesh\last_vert_i + source_mesh\last_vert_i + 1
	
	For i = 0 To source_mesh\last_vert_i

		dest_mesh\vert_x[vert_i_offset + i] = source_mesh\vert_x[i]
		dest_mesh\vert_y[vert_i_offset + i] = source_mesh\vert_y[i]
		
	Next

	; Copy all vector lines from source mesh to destination mesh.
	
	line_i_offset = dest_mesh\last_line_i + 1
	dest_mesh\last_line_i = dest_mesh\last_line_i + source_mesh\last_line_i + 1
	
	If hardness < 2 ; Set lines to specified hardness...

		For i = 0 To source_mesh\last_line_i
	
			dest_mesh\line_v0_i[line_i_offset + i] = source_mesh\line_v0_i[i] + vert_i_offset
			dest_mesh\line_v1_i[line_i_offset + i] = source_mesh\line_v1_i[i] + vert_i_offset
			dest_mesh\line_hard[line_i_offset + i] = hardness
	
		Next

	Else ; Keep source mesh's line hardness...
	
		For i = 0 To source_mesh\last_line_i
	
			dest_mesh\line_v0_i[line_i_offset + i] = source_mesh\line_v0_i[i] + vert_i_offset
			dest_mesh\line_v1_i[line_i_offset + i] = source_mesh\line_v1_i[i] + vert_i_offset
			dest_mesh\line_hard[line_i_offset + i] = source_mesh\line_hard[i]
	
		Next

	EndIf
			
End Function


;
; Creates a copy of an existing mesh.
;
; Params:
; entity_ID - Handle of entity whose mesh is to be copied.
; parent_ID - Handle of entity to be made the new mesh entity's parent.
;             The new mesh entity will then adopt the parent's position/rotation/scale.
;
; Returns:
; The handle of the newly created mesh entity.
;
Function vl_copy_mesh(entity_ID%, parent_ID% = 0)

	new_mesh_ID = vl_create_mesh(parent_ID)
	
	vl_add_mesh(entity_ID, new_mesh_ID)
	
	Return new_mesh_ID
	
End Function


;
; Scales all vertices of a mesh by the specified x,y factors.
;
; Params:
; entity_ID  - Handle of entity referencing the mesh to be scaled.
; x          - X factor to scale all vertices by.
; y          - Y factor to scale all vertices by.
;
Function vl_scale_mesh(entity_ID%, scale_x#, scale_y#)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	mesh.vl_meshT = entity\visuals\mesh

	For i = 0 To mesh\last_vert_i

		mesh\vert_x[i] = mesh\vert_x[i] * scale_x
		mesh\vert_y[i] = mesh\vert_y[i] * scale_y
		
	Next

End Function


;
; Flips all vector lines of a mesh so they face the opposite way.
;
; Params:
; entity_ID  - Handle of entity referencing the mesh to be flipped.
;
Function vl_flip_mesh(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	mesh.vl_meshT = entity\visuals\mesh

	For i = 0 To mesh\last_line_i

		temp_v0_i = mesh\line_v0_i[i]
		
		mesh\line_v0_i[i] = mesh\line_v1_i[i]
		mesh\line_v1_i[i] = temp_v0_i
		
	Next

End Function


;
; Scales and translates all vertices of a mesh so that the mesh occupies the specified box.
;
; Params:
; entity_ID  - Handle of entity referencing the mesh to be made to fit the box.
; x          - X coord of the corner of the fit box.
; y          - Y coord of the corner of the fit box.
; width      - Width of the fit box.
; height     - Height of the fit box.
; uniform    - False (default) to fit the mesh exactly inside the fit box.
;              True to scale verts uniformly so as to retain the mesh's aspect ratio.
;
Function vl_fit_mesh(entity_ID%, x#, y#, width#, height#, uniform% = False)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	mesh.vl_meshT = entity\visuals\mesh

	If mesh\last_vert_i = -1 Then Return


	;
	; Find mesh width/height and x,y offset needed to centre the mesh inside the defined box (this is
	; required for meshes whose origin isn't at the dead-centre of the bounding box of it's verts).
	;
		
	biggest_x#	= mesh\vert_x[0]
	smallest_x#	= biggest_x
	biggest_y#	= mesh\vert_y[0]
	smallest_y#	= biggest_y

	For i = 1 To mesh\last_vert_i

		vert_x# = mesh\vert_x[i]

		If vert_x > biggest_x
			biggest_x = vert_x
		ElseIf vert_x < smallest_x
			smallest_x = vert_x
		EndIf

		vert_y# = mesh\vert_y[i]

		If vert_y > biggest_y
			biggest_y = vert_y
		ElseIf vert_y < smallest_y
			smallest_y = vert_y
		EndIf
		
	Next

	mesh_width# = biggest_x - smallest_x
	offset_x# = (biggest_x - mesh_width) + (mesh_width / 2.0)

	mesh_height# = biggest_y - smallest_y
	offset_y# = (biggest_y - mesh_height) + (mesh_height / 2.0)


	;
	; Change all mesh verts to fit inside the defined box.
	;

	; Find centre of defined box.
	origin_x# = x + (width / 2.0)
	origin_y# = y + (height / 2.0)

	If uniform ; Keep mesh aspect ratio...

		If mesh_width > mesh_height
			height = (width * (mesh_height / mesh_width)) * Sgn(height)
		Else
			width = (height * (mesh_width / mesh_height)) * Sgn(width)
		EndIf

	EndIf

	scale_x# = width / mesh_width
	scale_y# = height / mesh_height
		
	For i = 0 To mesh\last_vert_i

		mesh\vert_x[i] = origin_x + ((mesh\vert_x[i] - offset_x) * scale_x)
		mesh\vert_y[i] = origin_y + ((mesh\vert_y[i] - offset_y) * scale_y)
		
	Next

End Function


;
; Removes all vertices and/or vector lines from a mesh.
;
; Params:
; entity_ID   - Handle of entity referencing the mesh to be cleared.
; clear_verts - True (default) to remove al vertices. False not to.
; clear_lines - True (default) to remove al vector lines. False not to.
;
Function vl_clear_mesh(entity_ID%, clear_verts% = True, clear_lines% = True)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	mesh.vl_meshT = entity\visuals\mesh

	If clear_verts Then mesh\last_vert_i = -1

	If clear_lines Then mesh\last_line_i = -1

End Function


;
; Determines whether the specified meshes are intersecting.
;
; Params:
; entity_a_ID - Handle of first mesh to test for intersection.
; entity_b_ID - Handle of second mesh to test for intersection.
;
; Returns:
; True if the two meshes are currently intersecting, False otherwise.
;
Function vl_meshes_intersect(entity_a_ID%, entity_b_ID%)

	; Transform mesh A to world space.
	entity_a.vl_entityT = Object.vl_entityT(entity_a_ID)
	visuals_a.vl_visualsT = entity_a\visuals
	mesh_a.vl_meshT = visuals_a\mesh
	vl__tform_entity_world(entity_a, vl__entity_x(entity_a, True), vl__entity_y(entity_a, True), vl__entity_rotation(entity_a, True), vl__entity_scale_x(entity_a, True), vl__entity_scale_y(entity_a, True))

	; Transform mesh B to world space.
	entity_b.vl_entityT = Object.vl_entityT(entity_b_ID)
	visuals_b.vl_visualsT = entity_b\visuals
	mesh_b.vl_meshT = visuals_b\mesh
	vl__tform_entity_world(entity_b, vl__entity_x(entity_b, True), vl__entity_y(entity_b, True), vl__entity_rotation(entity_b, True), vl__entity_scale_x(entity_b, True), vl__entity_scale_y(entity_b, True))

	If (mesh_a\last_line_i + mesh_b\last_line_i) > 10
		check_meshes = vl__vert_boxes_intersect(entity_a, entity_b)
	Else
		check_meshes = True
	EndIf
	
	If check_meshes ; See if any lines in mesh A intersect with any lines in mesh B...
	
		For a_i = 0 To mesh_a\last_line_i
	
			; Get this mesh A line start and delta.
			v0_i = mesh_a\line_v0_i[a_i]
			v1_i = mesh_a\line_v1_i[a_i]
	
			x1# = visuals_a\vert_x[v0_i]
			y1# = visuals_a\vert_y[v0_i]
	
			dx1# = visuals_a\vert_x[v1_i] - x1
			dy1# = visuals_a\vert_y[v1_i] - y1
	
			For b_i = 0 To mesh_b\last_line_i
	
				; Get this mesh B line start and delta.
				v0_i = mesh_b\line_v0_i[b_i]
				v1_i = mesh_b\line_v1_i[b_i]
		
				x2# = visuals_b\vert_x[v0_i]
				y2# = visuals_b\vert_y[v0_i]
		
				dx2# = visuals_b\vert_x[v1_i] - x2
				dy2# = visuals_b\vert_y[v1_i] - y2
	
				If vl__lines_intersect(x1, y1, dx1, dy1,  x2, y2, dx2, dy2) Then Return True
	
			Next
			
		Next
	
	EndIf
		
	Return False
	
End Function


;
; Returns the width of a mesh.
;
; Params:
; entity_ID - Handle of entity whose width is to be returned.
;
; Returns:
; The given entity's mesh width.
;
Function vl_mesh_width#(entity_ID%)

	Return vl__mesh_width(Object.vl_entityT(entity_ID))

End Function


;
; Returns the height of a mesh.
;
; Params:
; entity_ID - Handle of entity whose height is to be returned.
;
; Returns:
; The given entity's mesh height.
;
Function vl_mesh_height#(entity_ID%)

	Return vl__mesh_height(Object.vl_entityT(entity_ID))

End Function


;
; Returns the parent of an entity.
;
; Params:
; entity_ID - Handle of entity whose parent is to be returned.
;
; Returns:
; The handle of the given entity's parent.
;
Function vl_get_parent(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	Return Handle(entity\parent)

End Function


;
; Returns the child of an entity.
;
; Params:
; entity_ID - Handle of entity whose child is to be returned.
; child_num - Number of child to get. Range is 1 to vl_count_children().
;
; Returns:
; The handle of the given entity's child.
;
Function vl_get_child(entity_ID%, child_num)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	child_link.vl_elinkT = entity\first_child
	child.vl_entityT = Null
	count = 1
	
	While child_link <> Null
	
		If count = child_num Then Return Handle(child_link\entity)
		
		count = count + 1
		child_link = child_link\next_elink

	Wend

End Function


;
; Returns the first child of an entity with the given name. All the entity's 
; children and sub-children are searched, via recursion.
;
; Params:
; entity_ID - Handle of entity whose named child is to be returned.
; name      - The name of the child to find (case sensitive).
;
; Returns:
; The handle of the given entity's first child with the specified name.
;
Function vl_find_child(entity_ID%, name$)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	If entity\first_child <> Null Then Return Handle(vl__find_child(entity, name$))

End Function


;
; Returns the number of children an entity has.
;
; Params:
; entity_ID - Handle of entity whose children are to be counted.
;
; Returns:
; The total number of immediate children an entity has.
;
Function vl_count_children(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	child_link.vl_elinkT = entity\first_child

	total = 0
	
	While child_link <> Null

		total = total + 1			
		child_link = child_link\next_elink

	Wend

	Return total

End Function


;
; Sets the color of an entity.
;
; Params:
; entity_ID - Handle of entity whose color is to be set.
; r         - Red component of color.
; g         - Green component of color.
; b         - Blue component of color.
;
Function vl_entity_color(entity_ID%, r%, g%, b%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	entity\visuals\r = r
	entity\visuals\g = g
	entity\visuals\b = b

End Function


;
; Sets an entity's brightness.
;
; Params:
; entity_ID - Handle of entity whose brightness is to be set.
; value     - Level of brightness to give the entity. 0 = invisible, 1 = fullbright.
;
Function vl_entity_brightness(entity_ID%, value#)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	
	If value < 0.0

		value = 0.0

	ElseIf value > 1.0

		value = 1.0

	EndIf
	
	entity\visuals\brightness = value

End Function


;
; Returns the name of an entity.
;
; Params:
; entity_ID - Handle of entity whose name is to be returned.
;
; Returns:
; The given entity's name.
;
Function vl_entity_name$(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	Return entity\name$

End Function


;
; Returns the class of an entity.
;
; Params:
; entity_ID - Handle of entity whose class is to be returned.
;
; Returns:
; A string describing the class of the given entity.
;
Function vl_entity_class$(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	If entity\class
	
		Return "Mesh"
		
	Else
	
		Return "Pivot"
		
	EndIf

End Function


;
; Sets the name of an entity.
;
; Params:
; entity_ID - Handle of entity whose name is to be set.
; name      - Name to give the entity.
;
Function vl_name_entity(entity_ID%, name$)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	entity\name$ = name$

End Function


;
; Sets the drawing order of an entity.
;
; Params:
; entity_ID - Handle of entity whose draw order is to be set.
; order     - Order the entity will be drawn in.
;
Function vl_entity_order(entity_ID%, order%)

	vl__entity_order(Object.vl_entityT(entity_ID), order)
	
End Function


;
; Sets an entity's pick mode.
;
; Params:
; entity_ID - Handle of entity whose pick mode is to be set.
; mode      - Mode of geometry to be used when picking the entity:
;             0 = None (unpickable)
;             1 = Circle (vl_entity_radius is used)
;             2 = Polygon (uses the polygon defined by the mesh's vector lines)
;             3 = Box (vl_entity_box is used)
; obscurer  - True (default) to determine the entity 'obscures' other entities
;             during a vl_entity_visible() call. 
;
Function vl_entity_pick_mode(entity_ID%, mode%, obscurer% = True)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	If mode < 0
	
		mode = 0
		
	ElseIf mode > 3
	
		mode = 3
		
	EndIf
	
	vl__entity_pick_mode(entity, mode, obscurer)
	
End Function


;
; Returns the nearest pickable entity ahead of the specified entity.
;
; Params:
; entity_ID - Handle of entity to pick ahead of.
; range#    - Distance to pick ahead of entity by.
;
Function vl_entity_pick(entity_ID%, range#)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	; Temporarily assert entity is hidden to ensure it isn't included in the pick.
	hidden = entity\hidden
	entity\hidden = True
	
	rot# = vl__entity_rotation(entity, True)
	vl_line_pick(vl__entity_x(entity, True), vl__entity_y(entity, True), (Cos(rot) * range), (Sin(rot) * range))

	entity\hidden = hidden

	Return Handle(vl_picked_entity)

End Function


;
; Sets the dimensions on an entity's pick box.
;
; Params:
; entity_ID - Handle of entity whose pick box is to be set.
; x         - X position of entity's pick box.
; y         - Y position of entity's pick box.
; width     - Width of entity's pick box.
; height    - Height of entity's pick box.
;
Function vl_entity_box(entity_ID%, x#, y#, width#, height#)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	;
	; Ensure box 'winds' clockwise from top-left corner to ensure the normal of a picked
	; box edge can be calculated correctly (i.e. normals need to point outward).
	;
	
	If width < 0.0

		x = x + width
		width = -width

	EndIf

	If height < 0.0

		y = y + height
		height = -height

	EndIf
	
	entity\box_x			= x
	entity\box_y			= y
	entity\box_width	= width
	entity\box_height	= height

End Function


;
; Sets the radius on an entity's pick circle.
;
; Params:
; entity_ID - Handle of entity whose pick circle is to be set.
; radius    - Radius of entity's pick circle.
;
Function vl_entity_radius(entity_ID%, radius#)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	entity\radius = radius

End Function


;
; Parents (attaches) one entity to another.
;
; Params:
; entity_ID - Handle of entity to be made a child of parent.
; parent_ID - Handle of entity to be made the parent of entity. 0 sets the entity to have no parent.
; glob      - True (default) for the entity to retain it's global position/rotation/scale.
;             False for the entity to retain it's local position/rotation/scale.
;
Function vl_entity_parent(entity_ID%, parent_ID%, glob% = True)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	If parent_ID ; Parent entity...

		parent.vl_entityT = Object.vl_entityT(parent_ID)

		If (entity = parent) Or (entity\parent = parent) Then Return

		If glob ; Retain entity's global position/rotation/scale...

			pg_scale_x# = vl__entity_scale_x(parent, True)
			pg_scale_y# = vl__entity_scale_y(parent, True)

			; Find global coords of entity, relative to parent.
			dx# = vl__entity_x(entity, True) - vl__entity_x(parent, True)
			dy# = vl__entity_y(entity, True) - vl__entity_y(parent, True)
			
			; Rotate coords into parent's coord system.
			inv_pg_rot# = -vl__entity_rotation(parent, True)

			cos_rot# = Cos(inv_pg_rot)
			sin_rot# = Sin(inv_pg_rot)
			
			x# = (dx * cos_rot) - (dy * sin_rot)
			y# = (dy * cos_rot) + (dx * sin_rot)

			; Scale global coords into parent's global scale.
			entity\x = x / pg_scale_x
			entity\y = y / pg_scale_y

			; Convert global entity rotation to equivalent in parent's coord system.
			entity\rot = vl__clamp_rot(vl__entity_rotation(entity, True) + inv_pg_rot)
			
			; Convert global entity scale to equivalent in parent's scale.
			entity\scale_x = vl__entity_scale_x(entity, True) / pg_scale_x
			entity\scale_y = vl__entity_scale_y(entity, True) / pg_scale_y

		EndIf

		If entity\parent <> Null Then vl__delete_child(entity)

		vl__add_child(parent, entity)
			
	Else ; Un-parent entity...

		If entity\parent <> Null ; Entity does have a parent...
		
			If glob ; Retain entity's global position/rotation/scale...
	
				global_x#				= vl__entity_x(entity, True)
				global_y#				= vl__entity_y(entity, True)
				global_rot#			= vl__entity_rotation(entity, True)
				global_scale_x#	= vl__entity_scale_x(entity, True)
				global_scale_y#	= vl__entity_scale_y(entity, True)
				
				entity\x				= global_x
				entity\y				= global_y
				entity\rot			= global_rot
				entity\scale_x	= global_scale_x
				entity\scale_y	= global_scale_y
			
			EndIf
	
			vl__delete_child(entity)
		
		EndIf
		
	EndIf
		
End Function


;
; Returns an entity's X position coordinate.
;
; Params:
; entity_ID - Handle of entity whose X coord is to be returned.
; glob      - True returns the entity's global X coord.
;             False (default) returns the entity's local X coord.
;
; Returns:
; The given entity's current X coord. 
;
Function vl_entity_x#(entity_ID%, glob% = False)

	Return vl__entity_x(Object.vl_entityT(entity_ID), glob)

End Function


;
; Returns an entity's Y position coordinate.
;
; Params:
; entity_ID - Handle of entity whose Y coord is to be returned.
; glob      - True returns the entity's global Y coord.
;             False (default) returns the entity's local Y coord.
;
; Returns:
; The given entity's current Y coord. 
;
Function vl_entity_y#(entity_ID%, glob% = False)

	Return vl__entity_y(Object.vl_entityT(entity_ID), glob)

End Function


;
; Returns an entity's width.
;
; Params:
; entity_ID - Handle of entity whose width is to be returned.
; glob      - True returns the entity's global width.
;             False (default) returns the entity's local width.
;
; Returns:
; The given entity's current width. 
;
Function vl_entity_width#(entity_ID%, glob% = False)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	
	If glob
	
		vl__tform_entity_world(entity, vl__entity_x(entity, True), vl__entity_y(entity, True), vl__entity_rotation(entity, True), vl__entity_scale_x(entity, True), vl__entity_scale_y(entity, True))
	
		visuals.vl_visualsT = entity\visuals
		mesh.vl_meshT = visuals\mesh
	
		If mesh\last_vert_i = -1 Then Return 0.0
		
		biggest#	= visuals\vert_x[0]
		smallest#	= biggest
	
		For i = 1 To mesh\last_vert_i
	
			vert_x# = visuals\vert_x[i]
	
			If vert_x > biggest
			
				biggest = vert_x
			
			ElseIf vert_x < smallest
			
				smallest = vert_x
				
			EndIf
			
		Next
	
		Return (biggest - smallest)

	Else
	
		Return Abs(vl__mesh_width(entity) * vl__entity_scale_x(entity, True))
	
	EndIf
	
End Function


;
; Returns an entity's height.
;
; Params:
; entity_ID - Handle of entity whose height is to be returned.
; glob      - True returns the entity's global height.
;             False (default) returns the entity's local height.
;
; Returns:
; The given entity's current height. 
;
Function vl_entity_height#(entity_ID%, glob% = False)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	
	If glob
	
		vl__tform_entity_world(entity, vl__entity_x(entity, True), vl__entity_y(entity, True), vl__entity_rotation(entity, True), vl__entity_scale_x(entity, True), vl__entity_scale_y(entity, True))
	
		visuals.vl_visualsT = entity\visuals
		mesh.vl_meshT = visuals\mesh
	
		If mesh\last_vert_i = -1 Then Return 0.0
		
		biggest#	= visuals\vert_y[0]
		smallest#	= biggest
	
		For i = 1 To mesh\last_vert_i
	
			vert_y# = visuals\vert_y[i]
	
			If vert_y > biggest
			
				biggest = vert_y
			
			ElseIf vert_y < smallest
			
				smallest = vert_y
				
			EndIf
			
		Next
	
		Return (biggest - smallest)

	Else
	
		Return Abs(vl__mesh_height(entity) * vl__entity_scale_y(entity, True))
	
	EndIf
	
End Function


;
; Returns an entity's angle of rotation.
;
; Params:
; entity_ID - Handle of entity whose rotation is to be returned.
; glob      - True returns the entity's global rotation.
;             False (default) returns the entity's local rotation.
;
; Returns:
; The given entity's current angle of rotation. 
;
Function vl_entity_rotation#(entity_ID%, glob% = False)

	Return vl__entity_rotation(Object.vl_entityT(entity_ID), glob)

End Function


;
; Returns an entity's X axis scale.
;
; Params:
; entity - Entity whose X scale is to be returned.
; glob   - True returns the entity's global X scale.
;          False (default) returns the entity's local X scale.
;
; Returns:
; The given entity's current X axis scale. 
;
Function vl_entity_scale_x#(entity_ID%, glob% = False)

	Return vl__entity_scale_x(Object.vl_entityT(entity_ID), glob)

End Function


;
; Returns an entity's Y axis scale.
;
; Params:
; entity - Entity whose Y scale is to be returned.
; glob   - True returns the entity's global Y scale.
;          False (default) returns the entity's local Y scale.
;
; Returns:
; The given entity's current Y axis scale. 
;
Function vl_entity_scale_y#(entity_ID%, glob% = False)

	Return vl__entity_scale_y(Object.vl_entityT(entity_ID), glob)

End Function


;
; Returns the distance between two entities.
;
; Params:
; entity_a_ID - Handle of first entity.
; entity_b_ID - Handle of second entity.
;
; Returns:
; The absolute distance between the two given entities.
;
Function vl_entity_distance#(entity_a_ID%, entity_b_ID%)

	entity_a.vl_entityT = Object.vl_entityT(entity_a_ID)
	entity_b.vl_entityT = Object.vl_entityT(entity_b_ID)

	dx# = vl__entity_x(entity_a, True) - vl__entity_x(entity_b, True)
	dy# = vl__entity_y(entity_a, True) - vl__entity_y(entity_b, True)

	Return Sqr(dx * dx + dy * dy)

End Function


;
; Determines whether two entities can 'see' each other i.e. there are no pickable, obscurer
; entities between the line-of-sight of the two entities.
;
; Params:
; entity_a_ID - Handle of first entity.
; entity_b_ID - Handle of second entity.
;
; Returns:
; True if both entities can see each other, false otherwise.
;
Function vl_entity_visible(entity_a_ID%, entity_b_ID%)

	entity_a.vl_entityT = Object.vl_entityT(entity_a_ID)
	entity_b.vl_entityT = Object.vl_entityT(entity_b_ID)

	sx# = vl__entity_x(entity_a, True)
	sy# = vl__entity_y(entity_a, True)
	dx# = vl__entity_x(entity_b, True) - sx
	dy# = vl__entity_y(entity_b, True) - sy

	;
	; See if there's any pickable, visible, obscurer entities between the two entities.
	;
	
	pickable.vl_elinkT = vl_first_pickable
	
	While pickable <> Null
	
		this.vl_entityT = pickable\entity
		
		If (this <> entity_a) And (this <> entity_b) And vl__entity_visible(this) And this\obscurer

			;
			; See if this entity is picked, using it's defined pick method.
			;
			
			Select this\pick_mode
			
			Case VL_PICK_MODE_CIRCLE
			
				cx# = vl__entity_x(this, True)
				cy# = vl__entity_y(this, True)
				
				If vl__line_intersects_circle(sx, sy, dx, dy,  cx, cy, this\radius) Then Return False

			Case VL_PICK_MODE_BOX

				rot# = vl__entity_rotation(this, True)
				cos_rot# = Cos(rot)
				sin_rot# = Sin(rot)
		
				trans_x# = (this\box_x * cos_rot) - (this\box_y * sin_rot)
				trans_y# = (this\box_y * cos_rot) + (this\box_x * sin_rot)

				edge_x# = vl__entity_x(this, True) + trans_x
				edge_y# = vl__entity_y(this, True) + trans_y
		
				width_dx#		= cos_rot * this\box_width
				width_dy#		= sin_rot * this\box_height
				height_dx#	= Cos(rot + 90) * this\box_width
				height_dy#	= Sin(rot + 90) * this\box_height
		
				For n = 1 To 4
				
					; Set correct delta for current edge being tested.
					Select n
					Case 1 ; Top.
						edge_dx# = width_dx
						edge_dy# = width_dy
					Case 2 ; Right.
						edge_dx# = height_dx
						edge_dy# = height_dy
					Case 3 ; Bottom.
						edge_dx# = -width_dx
						edge_dy# = -width_dy
					Case 4 ; Left.
						edge_dx# = -height_dx
						edge_dy# = -height_dy
					End Select
		
					If vl__lines_intersect(sx, sy, dx, dy,  edge_x, edge_y, edge_dx, edge_dy)
		
						Return False
								
					EndIf
					
					edge_x = edge_x + edge_dx
					edge_y = edge_y + edge_dy
				
				Next

			Case VL_PICK_MODE_POLY
			
				; Transform this entity's verts to world coords so we can check if it's vector
				; lines intersect with the pick line.
				vl__tform_entity_world(this, vl__entity_x(this, True), vl__entity_y(this, True), vl__entity_rotation(this, True), vl__entity_scale_x(this, True), vl__entity_scale_y(this, True))

				visuals.vl_visualsT = this\visuals
				mesh.vl_meshT = visuals\mesh
				
				If mesh\last_line_i > 6
					check_for_pick = vl__line_intersects_vert_box(this, sx, sy, dx, dy)
				Else
					check_for_pick = True
				EndIf
				
				If check_for_pick
				
					For i = 0 To mesh\last_line_i
			
						; Get this mesh line start and delta.
						v0_i = mesh\line_v0_i[i]
						v1_i = mesh\line_v1_i[i]
				
						mx# = visuals\vert_x[v0_i]
						my# = visuals\vert_y[v0_i]
				
						mdx# = visuals\vert_x[v1_i] - mx
						mdy# = visuals\vert_y[v1_i] - my
		
						If vl__lines_intersect(sx, sy, dx, dy,  mx, my, mdx, mdy)
					
							Return False
															
						EndIf
		
					Next
			
				EndIf
			
			End Select
			
		EndIf
	
		pickable = pickable\next_elink
		
	Wend

	Return True
	
End Function


;
; Determines whether an entity is within the bounds of the screen.
;
; Params:
; entityID - Handle of entity to check.
;
; Returns:
; True if the given entity is visible, false otherwise.
;
Function vl_entity_in_view(entityID%)

	entity.vl_entityT = Object.vl_entityT(entityID)

	If entity\class = VL_CLASS_MESH
	
	Else
	
		x# = vl__entity_x(entity, True)
		y# = vl__entity_y(entity, True)

		If (x < 0) Or (x > vl_screen_units_x) Then Return False
		If (y < 0) Or (y > vl_screen_units_y) Then Return False
	
		Return True

	EndIf
	
End Function


;
; Converts a world coordinate to the corresponding screen coordinate.
;
; Params:
; world - The world coord to be converted to screen coord.
;
; Returns:
; The screen coordinate that corresponds to the given world coordinate.
;
Function vl_screen#(world#)

	Return (world * vl_unit_size_pixels)

End Function


;
; Converts a screen coordinate to the corresponding world coordinate.
;
; Params:
; screen - The screen coord to be converted to world coord.
;
; Returns:
; The world coordinate that corresponds to the given screen coordinate.
;
Function vl_world#(screen#)

	Return (screen / vl_unit_size_pixels)

End Function


;
; Adds a new vertex to the mesh used by the given entity.
;
; Params:
; entity_ID - Handle of entity whose mesh is to receive the new vertex.
; x         - X coord of new vertex.
; y         - Y coord of new vertex.
;
; Returns:
; The index number of the newly created vertex.
;
Function vl_add_vertex(entity_ID%, x#, y#)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	mesh.vl_meshT = entity\visuals\mesh
	
	i = mesh\last_vert_i + 1
	mesh\last_vert_i = i
	
	mesh\vert_x[i] = x
	mesh\vert_y[i] = y

	Return i

End Function


;
; Adds a new vector line to the mesh used by the given entity.
;
; Params:
; entity_ID - Handle of entity whose mesh is to receive the new vector line.
; v0        - Index of vertex representing the start point of the vector line.
; v1        - Index of vertex representing the end point of the vector line.
; hard      - Flag indicating if line is hard or not.
;             False = Soft line (not pickable).
;             True (default) = Hard line (pickable).
;
; Returns:
; The index number of the newly created vector line.
;
Function vl_add_line(entity_ID%, v0%, v1%, hard% = True)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	mesh.vl_meshT = entity\visuals\mesh

	i = mesh\last_line_i + 1
	mesh\last_line_i = i
	
	mesh\line_v0_i[i] = v0
	mesh\line_v1_i[i] = v1
	mesh\line_hard[i] = (hard <> 0)
		
	Return i

End Function


;
; Sets the hard state of an existing mesh vector line.
;
; Params:
; entity_ID - Handle of entity whose mesh holds the affected line.
; line_i    - Index of vector line to set the hard state of.
; hard      - Hard state to set the line to:
;             0 - Line is soft (unpickable).
;             1 - Line is hard (pickable).
;
Function vl_line_hard(entity_ID%, line_i%, hard%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	entity\visuals\mesh\line_hard[line_i] = (hard <> 0)
		
End Function


;
; Returns the requested vertex index of the specified mesh vector line.
;
; Params:
; entity_ID - Handle of entity whose mesh holds the vector line vertex requested.
; line_i    - Index of vector line to get the vertex index of.
; vert_i    - Index of line vertex to be returned.
;             0 = Vector line's start vertex.
;             1 = Vector line's end vertex.
;
; Returns:
; The requested vertex index of the specified mesh vector line.
;
Function vl_line_vertex(entity_ID%, line_i%, vert_i%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	If vert_i

		Return entity\visuals\mesh\line_v1_i[line_i]

	Else

		Return entity\visuals\mesh\line_v0_i[line_i]

	EndIf
	
End Function


;
; Sets the coordinates of an existing mesh vertex.
;
; Params:
; entity_ID - Handle of entity whose mesh holds the affected vertex.
; vert_i    - Index of vertex to be set.
; x         - X coord to set vertex to.
; y         - Y coord to set vertex to.
;
Function vl_vertex_coords(entity_ID%, vert_i%, x#, y#)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	mesh.vl_meshT = entity\visuals\mesh
	
	mesh\vert_x[vert_i] = x
	mesh\vert_y[vert_i] = y

End Function


;
; Returns the X coordinate of a vertex.
;
; Params:
; entity_ID - Handle of entity whose mesh holds the specified vertex.
; vert_i    - Index of vertex to return the X coord of.
;
; Returns:
; The X coordinate of the specified vertex.
;
Function vl_vertex_x#(entity_ID%, vert_i%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	
	Return entity\visuals\mesh\vert_x[vert_i]

End Function


;
; Returns the Y coordinate of a vertex.
;
; Params:
; entity_ID - Handle of entity whose mesh holds the specified vertex.
; vert_i    - Index of vertex to return the Y coord of.
;
; Returns:
; The Y coordinate of the specified vertex.
;
Function vl_vertex_y#(entity_ID%, vert_i%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	
	Return entity\visuals\mesh\vert_y[vert_i]

End Function


;
; Returns the number of vertices in a mesh.
;
; Params:
; entity_ID - Handle of entity whose mesh vertices are to be counted.
;
; Returns:
; The total number of vertices defined in the specified entity's mesh.
;
Function vl_count_vertices(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	Return (entity\visuals\mesh\last_vert_i + 1)
	
End Function


;
; Returns the number of vector lines in a mesh.
;
; Params:
; entity_ID - Handle of entity whose mesh lines are to be counted.
;
; Returns:
; The total number of vector lines defined in the specified entity's mesh.
;
Function vl_count_lines(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	Return (entity\visuals\mesh\last_line_i + 1)
	
End Function


;
; Creates a copy of an existing entity. All children are also copied.
;
; Params:
; entity_ID - Handle of entity to be copied.
; parent_ID - Handle of entity to be made the new entity's parent.
;             The new entity will then adopt the parent's position/rotation/scale.
;
; Returns:
; The handle of the newly created entity copy.
;
Function vl_copy_entity(entity_ID%, parent_ID% = 0)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	;
	; Create and initialize new entity copy.
	;

	new_entity.vl_entityT = New vl_entityT
	new_entity_ID = Handle(new_entity)
	
	new_entity\visuals			= Null
	new_entity\scale_x			= 1.0
	new_entity\scale_y			= 1.0
	new_entity\rot					= 0.0
	new_entity\x						= 0.0
	new_entity\y						= 0.0
	new_entity\parent				= Null
	new_entity\first_child	= Null

	new_entity\class				= entity\class
	new_entity\hidden				= False
	new_entity\name					= entity\name
	new_entity\radius				= entity\radius
	new_entity\box_x				= entity\box_x
	new_entity\box_y				= entity\box_y
	new_entity\box_width		= entity\box_width
	new_entity\box_height		= entity\box_height

	vl__entity_pick_mode(new_entity, entity\pick_mode, entity\obscurer)

	If entity\class = VL_CLASS_MESH

		visuals.vl_visualsT			= entity\visuals
		new_visuals.vl_visualsT	= New vl_visualsT
		new_entity\visuals			= new_visuals

		vl__add_user(visuals\mesh, new_entity)
		vl__entity_order(new_entity, visuals\order)

		new_visuals\mesh				= visuals\mesh
		new_visuals\r						= visuals\r
		new_visuals\g						= visuals\g
		new_visuals\b						= visuals\b
		new_visuals\brightness	= visuals\brightness

	EndIf
	
	; Recursively copy any child entities.
	If entity\first_child <> Null Then vl__copy_children(entity, new_entity)

	; Keep this AFTER the recursion call, else an infinite loop will be entered
	; if the entity to be parented to is also the entity being copied!
	If parent_ID Then vl_entity_parent(new_entity_ID, parent_ID, False)

	Return new_entity_ID

End Function


;
; Hides an entity so that it's no longer drawn. All children are also hidden.
;
; Params:
; entity_ID - Handle of entity to be hidden.
;
Function vl_hide_entity(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	entity\hidden = True

End Function


;
; Shows an entity.
;
; Params:
; entity_ID - Handle of entity to be shown.
;
Function vl_show_entity(entity_ID%)

	entity.vl_entityT = Object.vl_entityT(entity_ID)

	entity\hidden = False

End Function


;
; Positions an entity at an absolute position.
;
; Params:
; entity_ID - Handle of entity to be positioned.
; x         - X coord entity will be positioned at.
; y         - Y coord entity will be positioned at.
; glob      - True if the position is global.
;             False (default) if the position is local.
;
Function vl_position_entity(entity_ID%, x#, y#, glob% = False)

	entity.vl_entityT = Object.vl_entityT(entity_ID)
	parent.vl_entityT = entity\parent
	
	If glob And (parent <> Null)

		; Find global coords of x,y point, relative to parent.
		dx# = x - vl__entity_x(parent, True)
		dy# = y - vl__entity_y(parent, True)

		; Rotate coords into parent's coord system.
		inv_pg_rot# = -vl__entity_rotation(parent, True)

		cos_rot# = Cos(inv_pg_rot)
		sin_rot# = Sin(inv_pg_rot)
		
		x = (dx * cos_rot) - (dy * sin_rot)
		y = (dy * cos_rot) + (dx * sin_rot)

		; Scale coords to parent's global scale.
		entity\x = x / vl__entity_scale_x(parent, True)
		entity\y = y / vl__entity_scale_y(parent, True)

	Else
	
		entity\x = x
		entity\y = y
	
	EndIf

End Function
