; ID: 2378
; Author: Vorderman
; Date: 2008-12-22 09:09:58
; Title: Silo3D .obj import for B3D
; Description: Imports .obj meshes from Silo3D, including full automatic material/brush setup

;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
;
; SILO .obj MESH IMPORTER
;
; Current state: 
;	Import mesh : working as expected, Blitz and Silo's -X and X coords are reversed, but the import matches the visual in Silo.
;	Texture coords : working as expected, including proper UV splitting along texture mapping seams
;	Normals : working as expected 
;	Smoothing groups : working if the object is split into multiple parts in Silo
;	Brush setup : working as expected
;	Loading textures : working as expected
;	Dummy child object : working as expected - use a 4-tri single quad object with the name "DUMMY name", positioned at average of the 4 vertices
;
;	Vertex colours : not yet implemented
;
;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------






;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
;MATERIAL NAME = texture filename.ext  followed by 4 numbers seperated by spaces
;	TEXTURE.PNG		BRUSH_BLEND			BRUSH_FX			TEXTURE_BLEND			TEXTURE_FLAGS
;
;DIFFUSE = BRUSH COLOUR RGB
;AMBIENT = BRUSH ALPHA (0 To 1)
;SPECULAR = unused
;EMISSIVE = unused
;SHININESS = BRUSH SHININESS (0 To 127 = 0 To 1)
;TEXTURE = texture filename
;
;
;
;BRUSH_BLEND			1 = alpha (Default)		
;					2 = multiply
;					3 = add
;
;BRUSH_FX				0 = nothing (Default)
;					1 = full-bright 
;					2 = use vertex colours instead of brush colour
;					4 = flatshaded
;					8 = disable fog
;					16 = disable backface culling
;
;TEXTURE_BLEND			0 = do Not blend
;					1 = no blend , Or alpha when texture loaded with alpha flag (2)
;					2 = multiply (Default)
;					3 = add
;					4 = dot3
;					5 = multiply2
;
;TEXTURE_FLAGS			1 = colour (Default)
;					2 = alpha
;					4 = masked
;					8 = mipmapped (Default)
;					16 = clamp U
;					32 = clamp V
;					64 = environment map
;					128 = cube map
;					256 = store texture in vram
;				    	512 = force high-colour texture
;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------









;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
;OBJ import arrays
Type TYPE_vertex
	Field ID
	Field pointer
	Field x# , y# , z#
	Field u# , v#
End Type

Type TYPE_normal
	Field ID
	Field nx# , ny# , nz#
End Type

Type TYPE_uvw
	Field ID
	Field u# , v#
End Type

Type TYPE_surface
	Field ID
	Field pointer
	Field materialID$
End Type

Type TYPE_face
	Field ID
	Field pointer
	Field materialID$
	
	Field number_of_vertices
	
	Field vertexID[ MAX_VERTS_PER_FACE ]
	Field normalID[ MAX_VERTS_PER_FACE ]
	Field uvwID[ MAX_VERTS_PER_FACE ]
End Type

Type TYPE_material
	Field ID
	Field all_flags$
	Field alpha#
	Field red , green , blue
	Field shininess#
	Field texture_filename$
	
	Field FLAG_brush_blend
	Field FLAGS_brush_FX
	Field FLAG_texture_blend
	Field FLAGS_texture_flags
End Type
	
Const MAX_VERTS_PER_FACE											= 10
Const MAX_VERTS_PER_MESH											= 10000
Const MAX_UVW_PER_MESH												= 20000
Const MAX_FACES_PER_MESH											= 5000
Const MAX_SURFACES_PER_MESH											= 100
Const MAX_MATERIALS_PER_MESH											= 10
Const MAX_FACES_PER_DUMMY											= 6
Const VERTEX_MERGE_TOLLERANCE#										= 0.01

Global ARRAY_line_elements$[100]
Global ARRAY_vertex.TYPE_vertex[MAX_VERTS_PER_MESH]
Global ARRAY_normal.TYPE_normal[MAX_VERTS_PER_MESH]
Global ARRAY_uvw.TYPE_uvw[MAX_UVW_PER_MESH]
Global ARRAY_face.TYPE_face[MAX_FACES_PER_MESH]
Global ARRAY_surface.TYPE_surface[MAX_SURFACES_PER_MESH]
Global ARRAY_material.TYPE_material[MAX_MATERIALS_PER_MESH]
Global ARRAY_dummy_face.TYPE_face[MAX_FACES_PER_DUMMY]
;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------








;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Function FUNC_Import_Silo_OBJ( filename$ )

	mesh = 0
	material_library$ = ""
	object_name$ = ""
	last_material$ = ""
	
	vertex_ID = 1
	normal_ID = 1
	uvw_ID = 1
	face_ID = 1
	dummy_face_ID = 1
	surface_ID = 1

	;process the filename to obtain the directoy and mesh name	
	pos = Len(filename$)
	Repeat
		char$ = Mid$( filename$ , pos , 1)
		pos = pos - 1
	Until pos=0 Or char$="\"
	If (pos>0)
		stripped_meshname$ = Right$( filename$ , Len(filename$) - pos - 1)
		directory$ = Left$( filename$ , pos+1)
	Else
		stripped_meshname$ = filename$
		directory$ = ""
	EndIf

	;open the file for reading
	file = OpenFile( filename$ )
	
	;check open success
	If file

		DebugLog "---- OBJ LOADER - "+filename$+" ------------------------------------------------------------"
		
		;create base mesh
		mesh = CreateMesh()	
		PositionEntity mesh,0,0,0
		RotateEntity mesh,0,0,0		

		;reset to start of file
		SeekFile( file , 0)

		group_ignore = False
		
		;cycle through lines and parse each one
		Repeat
			;parse line to obtain the individual elements
			txt$ = ReadLine$( file )
			FUNC_Parse_Line( txt$ , " ")
			
			;process the stored words and store them
			Select ARRAY_line_elements[1]
			Case ""
;				DebugLog "EMPTY ELEMENT"

			Case "mtllib"
;				DebugLog "MATERIAL LIBRARY"
				material_library$ = ARRAY_line_elements[2]
			Case "o"
;				DebugLog "OBJECT NAME"
				object_name$ = ARRAY_line_elements[2]

			Case "v"
;				DebugLog "VERTEX COORDS"
				ARRAY_vertex[vertex_ID] = New TYPE_vertex
				ARRAY_vertex[vertex_ID]\ID = vertex_ID
				ARRAY_vertex[vertex_ID]\x# = -Float#(ARRAY_line_elements[2])
				ARRAY_vertex[vertex_ID]\y# = Float#(ARRAY_line_elements[3])
				ARRAY_vertex[vertex_ID]\z# = Float#(ARRAY_line_elements[4])
				ARRAY_vertex[vertex_ID]\pointer = -1
				vertex_ID = vertex_ID + 1
				
			Case "vn"
;				DebugLog "VERTEX NORMAL"
				ARRAY_normal[normal_ID] = New TYPE_normal
				ARRAY_normal[normal_ID]\ID = normal_ID
				ARRAY_normal[normal_ID]\nx# = -Float#(ARRAY_line_elements[2])
				ARRAY_normal[normal_ID]\ny# = Float#(ARRAY_line_elements[3])
				ARRAY_normal[normal_ID]\nz# = Float#(ARRAY_line_elements[4])

				normal_ID = normal_ID + 1
				
			Case "vt"
;				DebugLog "VERTEX TEXTURE COORDS"
				ARRAY_uvw[uvw_ID] = New TYPE_uvw
				ARRAY_uvw[uvw_ID]\ID = uvw_ID
				ARRAY_uvw[uvw_ID]\u# = Float#(ARRAY_line_elements[2])
				ARRAY_uvw[uvw_ID]\v# = -Float#(ARRAY_line_elements[3])

				uvw_ID = uvw_ID + 1
				
			Case "g"
;				DebugLog "GROUP"
				groupname$ = ARRAY_line_elements[2]
				If groupname$ = "DUMMY"
					group_ignore = True
					;create a new child dummy object here
					child = CreateSphere(4,mesh);CreatePivot(mesh)
					ScaleMesh child,0.05,0.05,0.05
					EntityColor child,255,0,0
					NameEntity child , ARRAY_line_elements[3]
					DebugLog "Child object "+EntityName$(child)+" created."
				Else
					group_ignore = False
				EndIf

			Case "usemtl" 
				If (group_ignore=False)
;					DebugLog "MATERIAL"
					temp$ = (ARRAY_line_elements[2])

					;seperate the texture filename from any flags / info
					pos = 1
					Repeat
						char$ = Mid$( temp$ , pos , 1)
						pos = pos + 1
					Until (char$ = "_") Or (pos>Len(temp$))
					last_material$ = Left$( temp$ , pos-2 )
;					DebugLog "last material : "+last_material$
				EndIf

			Case "f" 
				If (group_ignore=False)
;					DebugLog "FACE "+face_ID
					ARRAY_face[face_ID] = New TYPE_face
					ARRAY_face[face_ID]\ID = face_ID
					ARRAY_face[face_ID]\materialID$ = last_material$
					
					num = 2
					vertex_number = 1
					
					While ARRAY_line_elements[num] <> ""
	;					DebugLog "   Face element:"+ARRAY_line_elements[num]
						
						;extract vertex ID
						pos = 1
						char$ = ""
						number$ = ""
						Repeat
							char$ = Mid$(ARRAY_line_elements[num] , pos , 1)
							If (char$<>"/") number$ = number$ + char$
							pos = pos + 1
						Until char$="/"
						ARRAY_face[face_ID]\vertexID[ num-1 ] = Int(number$)
	;					DebugLog "   Vertex ID:"+ARRAY_face[face_ID]\vertexID[ num-1 ]
	
						;extract UVW id
						char$ = ""
						number$ = ""
						Repeat
							char$ = Mid$(ARRAY_line_elements[num] , pos , 1)
							If (char$<>"/") number$ = number$ + char$
							pos = pos + 1
						Until char$="/"
						ARRAY_face[face_ID]\uvwID[ num-1 ] = Int(number$)
	;					DebugLog "   UVW ID:"+ARRAY_face[face_ID]\uvwID[ num-1 ]
	
						;extract normal id
						char$ = ""
						number$ = ""
						Repeat
							char$ = Mid$(ARRAY_line_elements[num] , pos , 1)
							If (char$<>"/") number$ = number$ + char$
							pos = pos + 1
						Until char$="/" Or char$=""
						ARRAY_face[face_ID]\normalID[ num-1 ] = Int(number$)
	;					DebugLog "   Normal ID:"+ARRAY_face[face_ID]\normalID[ num-1 ] 
	
						num = num + 1
					Wend
									
					ARRAY_face[face_ID]\number_of_vertices = (num - 2)
	;				DebugLog "Face has "+ARRAY_face[face_ID]\number_of_vertices+" vertices."
	
					face_ID = face_ID + 1
				
				Else
				
				;scan the face listsings to obtain an average centre position for the child marker
					DebugLog "DUMMY OBJECT FACE FOUND"
					ARRAY_dummy_face[1] = New TYPE_face
					
					num = 2
					
					While ARRAY_line_elements[num] <> ""
						;extract vertex number
						pos = 1
						char$ = ""
						number$ = ""
						Repeat
							char$ = Mid$(ARRAY_line_elements[num] , pos , 1)
							If (char$<>"/") number$ = number$ + char$
							pos = pos + 1
						Until char$="/"
						ARRAY_dummy_face[1]\vertexID[ num-1 ] = Int(number$)
		
						num = num + 1
					Wend
					
					;average the four vertices
					dummy_x# = 0.0
					dummy_y# = 0.0
					dummy_z# = 0.0
					For a=1 To 4
						dummy_x# = dummy_x# + ARRAY_vertex[ ARRAY_dummy_face[1]\vertexID[ a ] ]\x#
						dummy_y# = dummy_y# + ARRAY_vertex[ ARRAY_dummy_face[1]\vertexID[ a ] ]\y#
						dummy_z# = dummy_z# + ARRAY_vertex[ ARRAY_dummy_face[1]\vertexID[ a ] ]\z#
					Next
					dummy_x# = dummy_x# / 4.0
					dummy_y# = dummy_y# / 4.0
					dummy_z# = dummy_z# / 4.0

					PositionEntity child , dummy_x# , dummy_y# , dummy_z# , True
					DebugLog "dummy object position : "+dummy_x#+" , "+dummy_y#+" , "+dummy_z#
				EndIf
									
			Default
				DebugLog "ERROR : MESH FILE : LINE NOT RECOGNIZED : "+txt$

			End Select
			
		Until Eof( file )



		;cycle through faces and build vertices and polygons
		For f=1 To (face_ID-1)

			;reset current surf to zero
			surf = 0

			;check if surface using this material ID$ already exists
			For s.TYPE_surface = Each TYPE_surface
				If s\materialID$ = ARRAY_face[f]\materialID$
					surf = s\pointer
				EndIf
			Next
			
			;if surface not found create new
			If surf=0
				;create new surface and store the material ID$ used
				ARRAY_surface[surface_ID] = New TYPE_surface
				ARRAY_surface[surface_ID]\ID = surface_ID
				ARRAY_surface[surface_ID]\pointer = CreateSurface( mesh )
				ARRAY_surface[surface_ID]\materialID$ = ARRAY_face[f]\materialID$
				surf = ARRAY_surface[surface_ID]\pointer
				surface_ID = surface_ID + 1
			EndIf

			;cycle through this faces verts and create them if necessary
			For v=1 To ARRAY_face[f]\number_of_vertices
			
				;obtain relevant details
				vert_number = ARRAY_face[f]\vertexID[v]
				normal_number = ARRAY_face[f]\normalID[v]
				texcoord_number = ARRAY_face[f]\uvwID[v]
				
				;if vert doesn't exists then create new
				If ARRAY_vertex[vert_number]\pointer = -1
					ARRAY_vertex[vert_number]\pointer = AddVertex( surf , ARRAY_vertex[vert_number]\x# , ARRAY_vertex[vert_number]\y# , ARRAY_vertex[vert_number]\z# )
					VertexNormal( surf , ARRAY_vertex[vert_number]\pointer , ARRAY_normal[normal_number]\nx# , ARRAY_normal[normal_number]\ny# , ARRAY_normal[normal_number]\nz#)
					VertexTexCoords( surf , ARRAY_vertex[vert_number]\pointer , ARRAY_uvw[texcoord_number]\u# , ARRAY_uvw[texcoord_number]\v# )
					ARRAY_vertex[vert_number]\u# = ARRAY_uvw[texcoord_number]\u#
					ARRAY_vertex[vert_number]\v# = ARRAY_uvw[texcoord_number]\v#
				Else
					;if it does exist, check that the UV coords match
					new_U# = ARRAY_uvw[texcoord_number]\u#
					new_V# = ARRAY_uvw[texcoord_number]\v#
;					DebugLog "New vertex UVs : "+new_U#+" , "+new_V#
					;existing UVs
					existing_U# = ARRAY_vertex[vert_number]\u#
					existing_V# = ARRAY_vertex[vert_number]\v#
;					DebugLog "Existing veretx UVs : "+existing_U#+" , "+existing_V#
					
					diff_U# = Abs(existing_U# - new_U#)
					diff_V# = Abs(existing_V# - new_V#)
					If (diff_U# > VERTEX_MERGE_TOLLERANCE#) Or (diff_V# > VERTEX_MERGE_TOLLERANCE#)
						;create a new vertex here to avoid distorting texture coords
;						DebugLog "CREATE NEW VERTEX : "+vertex_ID

						ARRAY_vertex[vertex_ID] = New TYPE_vertex
						ARRAY_vertex[vertex_ID]\ID = vertex_ID
						ARRAY_vertex[vertex_ID]\x# = ARRAY_vertex[vert_number]\x#
						ARRAY_vertex[vertex_ID]\y# = ARRAY_vertex[vert_number]\y#
						ARRAY_vertex[vertex_ID]\z# = ARRAY_vertex[vert_number]\z#
						ARRAY_vertex[vertex_ID]\pointer = AddVertex( surf , ARRAY_vertex[vertex_ID]\x# , ARRAY_vertex[vertex_ID]\y# , ARRAY_vertex[vertex_ID]\z# )
						VertexNormal( surf , ARRAY_vertex[vertex_ID]\pointer , ARRAY_normal[normal_number]\nx# , ARRAY_normal[normal_number]\ny# , ARRAY_normal[normal_number]\nz#)
						VertexTexCoords( surf , ARRAY_vertex[vertex_ID]\pointer , ARRAY_uvw[texcoord_number]\u# , ARRAY_uvw[texcoord_number]\v# )
						ARRAY_vertex[vertex_ID]\u# = ARRAY_uvw[texcoord_number]\u#
						ARRAY_vertex[vertex_ID]\v# = ARRAY_uvw[texcoord_number]\v#
						
						ARRAY_face[f]\vertexID[v] = vertex_ID
						vertex_ID = vertex_ID + 1
					EndIf
				EndIf
			Next
			
			;create tris if 3-sided polygon
			If (ARRAY_face[f]\number_of_vertices = 3)
				v0 = ARRAY_face[f]\vertexID[3]
				v1 = ARRAY_face[f]\vertexID[2]
				v2 = ARRAY_face[f]\vertexID[1]
				tri1 = AddTriangle( surf , ARRAY_vertex[v0]\pointer , ARRAY_vertex[v1]\pointer , ARRAY_vertex[v2]\pointer )
			EndIf

			;create tris if 4-sided polygon
			If (ARRAY_face[f]\number_of_vertices = 4)
				v0 = ARRAY_face[f]\vertexID[4]
				v1 = ARRAY_face[f]\vertexID[3]
				v2 = ARRAY_face[f]\vertexID[2]
				v3 = ARRAY_face[f]\vertexID[1]
				tri1 = AddTriangle( surf , ARRAY_vertex[v0]\pointer , ARRAY_vertex[v1]\pointer , ARRAY_vertex[v2]\pointer )
				tr21 = AddTriangle( surf , ARRAY_vertex[v0]\pointer , ARRAY_vertex[v2]\pointer , ARRAY_vertex[v3]\pointer )
			EndIf
	
		Next		

		;output object stats
		DebugLog "   Mesh : surface count : "+CountSurfaces(mesh)
		For a=1 To CountSurfaces(mesh)
			DebugLog "      Mesh : Surface "+a+" - vertex count : "+CountVertices(GetSurface(mesh,a))+"    triangle count : "+CountTriangles(GetSurface(mesh,a))
		Next


		
		;process the material file and setup brushes and textures		
		file = ReadFile(directory$+material_library$)
		material_ID = 1

		If file
			DebugLog "   Material file "+material_library$+" found."
			
			;set to start of file
			SeekFile( file , 0)

			;cycle through lines and parse each one
			Repeat
				;parse line to obtain the individual elements
				txt$ = ReadLine$( file )
				FUNC_Parse_Line( txt$ , " ")

				;process the stored words and store them
				Select ARRAY_line_elements[1]
				Case "newmtl"
;					DebugLog "	NEW MATERIAL"
					ARRAY_material[material_ID] = New TYPE_material
					ARRAY_material[material_ID]\ID = material_ID
					ARRAY_material[material_ID]\all_flags$ = ARRAY_line_elements[2]
				Case "Ka"
;					DebugLog "	Ka"
					ARRAY_material[material_ID]\alpha# = Float#(ARRAY_line_elements[2])
				Case "Kd"
;					DebugLog "	Kd"
					ARRAY_material[material_ID]\red = 255 * Float#(ARRAY_line_elements[2])
					ARRAY_material[material_ID]\green = 255 * Float#(ARRAY_line_elements[3])
					ARRAY_material[material_ID]\blue = 255 * Float#(ARRAY_line_elements[4])
				Case "Ks"
;					DebugLog "	Ks"
				Case "Ns"
;					DebugLog "	Ns"
					ARRAY_material[material_ID]\shininess# = (1.0 / 127.0) * Int(ARRAY_line_elements[2])
					If (ARRAY_material[material_ID]\shininess# > 1.0) ARRAY_material[material_ID]\shininess# = 1.0
				Case "map_Kd"
;					DebugLog "	map_Kd"
					ARRAY_material[material_ID]\texture_filename$ = ARRAY_line_elements[2]
				Default
					DebugLog "ERROR : MATERIAL FILE : LINE NOT RECOGNIZED : "+txt$
				End Select

				;parse and store the flags segction				
				FUNC_Parse_Line( ARRAY_material[material_ID]\all_flags$ , "_")
				top_texture_filename$ = Replace$(ARRAY_line_elements[1],"_","")
				ARRAY_material[material_ID]\FLAG_brush_blend = Int(ARRAY_line_elements[2])
				ARRAY_material[material_ID]\FLAGS_brush_FX = Int(ARRAY_line_elements[3])
				ARRAY_material[material_ID]\FLAG_texture_blend = Int(ARRAY_line_elements[4])
				ARRAY_material[material_ID]\FLAGS_texture_flags = Int(ARRAY_line_elements[5])

				;check for .DDS override
				If (top_texture_filename$ <> ARRAY_material[material_ID]\texture_filename$)			
					ARRAY_material[material_ID]\texture_filename$ = top_texture_filename$
					DebugLog "TEXTURE NAME OVERRIDE : Setting texture to "+ARRAY_material[material_ID]\texture_filename$
				EndIf
				
			Until Eof(file)
			
;			DebugLog "MATERIAL PROCESSING COMPLETE."
		Else
			DebugLog "ERROR : MATERIAL FILE : "+material_library$+" NOT FOUND."
		EndIf
		
		;create and assign brushes
		For m.TYPE_material = Each TYPE_material
			brush = CreateBrush( m\red , m\green , m\blue )
			BrushBlend brush , m\FLAG_brush_blend
			BrushFX brush , m\FLAGS_brush_FX
			BrushAlpha brush , m\alpha#
			BrushShininess brush , m\shininess#
			BrushColor brush , m\red , m\green , m\blue
			
			;load texture
			texture = LoadTexture(directory$ + m\texture_filename$ , m\FLAGS_texture_flags)
			TextureBlend texture , m\FLAG_texture_blend
			If texture
				DebugLog "   Texture "+m\texture_filename$+" loaded : flags "+m\FLAGS_texture_flags
				BrushTexture brush , texture
			EndIf
			
			;cycle through syrfaces and assign this brush
			For s.TYPE_surface = Each TYPE_surface
;				DebugLog "Surface "+s\ID+" material="+s\materialID$
;				DebugLog "Brush "+m\texture_filename$
				If m\texture_filename$ = s\materialID$
					PaintSurface s\pointer , brush
				EndIf
			Next
		Next
		


		FUNC_CleanUp()
		
		Return mesh		
	Else
		;file open failed, return -1
		Return -1
	EndIf

End Function
;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------







;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Function FUNC_CleanUp()
	For v.TYPE_vertex = Each TYPE_vertex
		Delete v
	Next
	For n.TYPE_normal = Each TYPE_normal
		Delete n
	Next
	For u.TYPE_uvw = Each TYPE_uvw
		Delete u
	Next
	For f.TYPE_face = Each TYPE_face
		Delete f
	Next
	For s.TYPE_surface = Each TYPE_surface
		Delete s
	Next
	For m.TYPE_material = Each TYPE_material
		Delete m
	Next
End Function
;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------






;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Function FUNC_Parse_Line( txt$ , break$ )

	;clear the storage array
	For a=1 To 100
		ARRAY_line_elements[a] = ""
	Next

;	DebugLog "FUNCTION : FUNC_Parse_Obtain_String( '"+txt$+"' )"

	;cycle to correct word in line
	startpos = 1
	num = 1
	
	Repeat
		char$ = ""
		word$ = ""
		Repeat
			;read line until space is found
			char$ = Mid$( txt$ , startpos , 1)
			startpos = startpos + 1
			If (char$<>" ") word$ = word$ + char$
		Until (char$ = break$) Or startpos>Len(txt$)
		
		;store the word
		ARRAY_line_elements[num] = word$
		num = num + 1
;		DebugLog "word extracted:"+word$+"."

	Until (startpos>Len(txt$))
	
End Function
;---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
