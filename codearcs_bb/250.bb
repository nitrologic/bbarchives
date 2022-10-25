; ID: 250
; Author: Anthony
; Date: 2002-02-26 06:16:29
; Title: Import STL...
; Description: A set of functions for importing and exporting '.stl' files for Blitz3D.

; ------------------------------------------------------------------------------
; STL library (v0.1) // by Anthony Blackshaw // ant@getme.co.uk 
; ------------------------------------------------------------------------------

; A set of functions for importing and exporting '.stl' files for Blitz3D. The
; functions currently support for:

; - import ascii and binary '.stl' files
; - mesh optimization by welding duplicate vertices
; - base geometry uvw mapping (not fully implemented)

; Note:
; - no normals support is currently implemented, all normals are generated using
;   the right-hand rule
; - using STL_IMPORT_OPTIMIZED as the parser flag can take a VERY long time, for
;   little benifit other than fewer vertices.

; Next version:
; - export ascii and binary '.stl' files
; - add normals support
; - implement uvw mapping properly
; - store error information within the stl

; Credits:
; - dave@birdie72.freeserve.co.uk for the uvw mapping routine, though I still
;   don't fully understand whats going on. ( Please comment more, I don't have
;   alot of experience with uvw mapping, and found it very hard going).
; - Paul Bourke for information on the structure and implementaion of the stl
;   file format. Available online at 
;   
;   http://astronomy.swin.edu.au/~pbourke/3dformats/stl/


; defines

; file formats
Const STL_ASCII		= 0
Const STL_BINARY	= 1

; file parsing methods
Const STL_IMPORT_BASIC 		= 0 
Const STL_IMPORT_OPTIMIZED 	= 1

; base geometry uvw mapping ( currently only plane is supported) 
Const STL_UVW_NONE		= 0
Const STL_UVW_PLANE		= 1
Const STL_UVW_CUBE		= 2
Const STL_UVW_CYLINDER	= 3
Const STL_UVW_SPHERE	= 4
Const STL_UVW_FACE		= 5


; types

; stl file type
Type t_STL
	Field format			; file format (ascii/binary)
	Field parser			; basic (do nothing), optimized (remove duplicate vertices)
	Field weld_radius#		; radius used to determine if vertices are duplicates
	Field uvw				; type of uvw mapping
	Field header$			; information from the file header (comments)
	Field facets%			; number of facets in the file
	Field mesh				; handle to the mesh
End Type


; functions

Function ImportSTL.t_stl( filename$, parser, weld_radius#, uvw )

	; attempt to open file
	If ( FileType( filename ) )
		file = ReadFile( filename$ )
	Else
		Return Null
	EndIf 
	
	; create new stl
	stl.t_stl = New t_stl
	
	; read the header information ( 80 bytes )
	For i% = 0 To 79
		stl\header$ = stl\header$ + Chr( ReadByte( file ) )
	Next
	
	; read number of facets (4 bytes)
	stl\facets% = ReadInt( file )	

	; close the stl file
	CloseFile( file )
	
	; determine the stl format (ascii/binary)
	If ( ( ( stl\facets% * 50 ) + 84 ) = FileSize( filename$ ) )
	
		; NOTE: Only binary format contains information on the number of facets,
		; therfore we can only calculate the correct file size from a binary file.
	
		stl\format = STL_BINARY
		
	Else
		stl\format = STL_ASCII
	EndIf
	
	; extra stl information
	stl\parser = parser
	stl\weld_radius# = weld_radius#	
	stl\uvw = uvw
	
	; create the mesh from file
	If ( stl\format = STL_ASCII )
		
		; load as ascii
		stl\mesh = ImportASCIISTL( filename$ )
		If Not ( stl\mesh )
			Return Null 
		EndIf		
		
		; count the number of facets (this information is not avialable in ascii file)
		surface = GetSurface( stl\mesh, 1 )
		stl\facets% = CountTriangles( surface )
		
	Else
		
		; load as binary
		stl\mesh = ImportBinarySTL( filename$ )
		If Not ( stl\mesh )
			Return Null 
		EndIf
		
	EndIf
	
	; optimize mesh
	If ( stl\parser = STL_IMPORT_OPTIMIZED )
		stl\mesh = OptimizeMesh( stl\mesh, stl\weld_radius# )
	EndIf
	
	; uvw map mesh
	If Not ( stl\uvw = STL_UVW_NONE )
		stl\mesh = UVWMapMesh( stl\mesh, stl\uvw )
	EndIf	
		
	; re-orientate the mesh for
	RotateEntity( stl\mesh, -90, 0, 0 )
		
	Return stl

End Function

Function ImportASCIISTL( filename$ )
	
	; attempt to open file
	If ( FileType( filename ) )
		file = ReadFile( filename$ )
	Else
		Return False
	EndIf
	
	; create blank mesh
	mesh = CreateMesh()
	surface = CreateSurface( mesh )
	
	; read header (ignored)
	ReadLine( file )
	
	end_of_stl$ = ReadLine( file )
	
	; triangle index
	index%  = 0
	
	; read the file data into a mesh
	Repeat
	
		; facet (1 line)
		If Not ( Instr( end_of_stl$, "facet", 1 ) ) Then Return False
		
		; outer loop (1 line)
		If Not ( Instr( ReadLine( file ), "outer loop", 1 ) ) Then Return False
		
		; add the vertices (3 lines)
		For i% = 0 To 2
			
			; get each vertices line, trim spaces from start and en
			v$  = Trim( ReadLine( file ) )
			
			; vaidate the line contains vertices data
			If Not ( Instr( v$, "vertex", 1 ) ) Then Return False
			
			; space separate the float values for x, y, and z of each vertice
			vx# = Float( SeparateString$( v$, " ", 1 ) )
			vy# = Float( SeparateString$( v$, " ", 2 ) )
			vz# = Float( SeparateString$( v$, " ", 3 ) )
			
			AddVertex( surface, vx#, vy#, vz# )
			
		Next
		
		; add the triangle
		AddTriangle( surface, index%, ( index% + 1 ), ( index% + 2 ) )
		
		; step triangle index
		index% = index% + 3
		
		; endloop (1 line) 
		If Not ( Instr( ReadLine( file ), "endloop", 1 ) ) Then Return False	
			
		; endfacet (1 line) 
		If Not ( Instr( ReadLine( file ), "endfacet", 1 ) ) Then Return False				
		
		end_of_stl$ = ReadLine( file ) 
		
	Until ( Instr( end_of_stl$, "endsolid", 1 ) )
	
	; close the file
	CloseFile( file )

	UpdateNormals( mesh )
	
	Return mesh
	
End Function

Function ImportBinarySTL( filename$ )

	; attempt to open file
	If ( FileType( filename ) )
		file = ReadFile( filename$ )
	Else
		Return False
	EndIf

	; create a memory bank to store the file in memory
	bank = CreateBank( FileSize( filename$ ) )
	
	; store file in memory
	ReadBytes( bank, file, 0, ( FileSize( filename$ ) - 1 ) )

	; create blank mesh
	mesh = CreateMesh()
	surface = CreateSurface( mesh )
	
	; read number of facets (4 bytes)
	facets% = PeekInt( bank, 80 )
	
	; triangle index
	index%  = 0
	
	; header offset
	offset% = 84
	
	; read the file data into a mesh
	For i% = 0 To ( facets% - 1 )
	
		; normals (12 bytes) (ignored)
		offset% = ( offset% + 12 )
	
		; add the vertices (36 byes)
		AddVertex( surface, PeekFloat( bank, offset% ), PeekFloat( bank, offset% + 4 ), PeekFloat( bank, offset% + 8 ) )
		AddVertex( surface, PeekFloat( bank, offset% + 12 ), PeekFloat( bank, offset% + 16 ), PeekFloat( bank, offset% + 20 ) )
		AddVertex( surface, PeekFloat( bank, offset% + 24 ), PeekFloat( bank, offset% + 28 ), PeekFloat( bank, offset% + 32 ) )		
		offset% = ( offset% + 36 )
	
		; spacer (2 bytes) (ignored)
		offset% = ( offset% + 2 )
	
		; add the triangle
		AddTriangle( surface, index%, ( index% + 1 ), ( index% + 2 ) )
		
		; step triangle index
		index% = index% + 3
	
	Next

	UpdateNormals( mesh )

	; close the file
	CloseFile( file )
	
	Return mesh

End Function

Function OptimizeMesh( mesh, weld_radius# )

	; get mesh as surface
	surface = GetSurface( mesh, 1 )

	; create a bank to contain all the duplicates
	bank = CreateBank( CountVertices( surface ) * 4 )
	
	; fill our bank with vertices indexes
	For i% = 0 To ( CountVertices( surface ) - 1 )
		PokeInt( bank, ( i% * 4 ), i% )
	Next	

	; go through every vertex in the mesh and find duplicates
	For i% = 0 To ( CountVertices( surface ) - 1 )	
				
		For j% = ( i% + 1 ) To ( CountVertices( surface ) - 1 )
		
			If ( ( VertexX( surface, i% ) < ( VertexX( surface, j% ) + weld_radius# ) ) And ( VertexX( surface, i% ) > ( VertexX( surface, j% ) - weld_radius# ) ) )
			
				If ( ( VertexY( surface, i% ) < ( VertexY( surface, j% ) + weld_radius# ) ) And ( VertexY( surface, i% ) > ( VertexY( surface, j% ) - weld_radius# ) ) )
				
					If ( ( VertexZ( surface, i% ) < ( VertexZ( surface, j% ) + weld_radius# ) ) And ( VertexZ( surface, i% ) > ( VertexZ( surface, j% ) - weld_radius# ) ) )
						
						; store the duplicate data
						PokeInt( bank, ( j% * 4 ), i% )
					
					EndIf
					
				EndIf
			
			EndIf	
					
		Next
	
	Next	
	
	; create blank mesh for optimized
	optimized_mesh = CreateMesh()
	optimized_surface = CreateSurface( optimized_mesh )	
	
	; vertices index
	index% = 0
	
	; add optimized vertices
	For i% = 0 To ( CountVertices( surface ) - 1 )
		If ( i% = PeekInt( bank, ( i% * 4 ) ) )
			AddVertex( optimized_surface, VertexX( surface, i% ), VertexY( surface, i% ), VertexZ( surface, i% ) )
			
			; re-index the vertex
			PokeInt( bank, ( i% * 4 ), index% )

			index% = index% + 1
		Else	
			; re-index the vertex
			k% = PeekInt( bank, ( i% * 4 ) )
			
			PokeInt( bank, ( i% * 4 ), PeekInt( bank, ( k% * 4 ) ) )
			
		EndIf		
	Next	
	
	; add optimized triangles
	For i% = 0 To ( CountTriangles( surface ) - 1 )
		AddTriangle( optimized_surface, PeekInt( bank, ( TriangleVertex( surface, i%, 0 ) * 4 ) ), PeekInt( bank, ( TriangleVertex( surface, i%, 1 ) * 4 ) ), PeekInt( bank, ( TriangleVertex( surface, i%, 2 ) * 4 ) ) )					
	Next
	
	; remove any old mesh data
	ClearSurface( surface )
	FreeEntity( mesh )
	
	UpdateNormals( optimized_mesh )
	
	Return optimized_mesh

End Function

Function UVWMapMesh( mesh, uvw )

	; get mesh as surface
	surface = GetSurface( mesh, 1 )
	
	; select the base geomerty for uvw mapping
	Select uvw
	
		Case STL_UVW_PLANE ; flat plane
		
			; set the uv coords at each vertice
			For vertice% = 0 To CountVertices( surface ) - 1
				
				origin_u# = 0
				origin_v# = 0
				
				x# = VertexX( surface, vertice% )
				y# = VertexY( surface, vertice% )
				
				; find the ratio for the face uv coords 
				If ( MeshWidth( mesh ) > MeshHeight( mesh ) )
					hr# = 1.0
					wr# = 1 / ( MeshWidth( mesh ) / MeshHeight( mesh ) )
				Else
					wr# = 1.0
					hr# = 1 / ( MeshHeight( mesh ) / MeshWidth( mesh ) )				
				EndIf
				
				; calculate the uv coords
				u# = ( wr# * x# ) - origin_u#
				v# = ( hr# * y# ) - origin_v#
				
				; set the uv coords
				VertexTexCoords surface, vertice%, u, v
				
			Next

		Case STL_UVW_CUBE
			; todo
			
		Case STL_UVW_CYLINDER
			; todo
			
		Case STL_UVW_SPHERE
			; todo
			
		Case STL_UVW_FACE
			; todo
			
	End Select

	Return mesh

End Function

Function SeparateString$( whole_string$, character$, index% )
	
	; current occurance count
	count% = 0
	
	; go through the whole string char by char
	For i% = 1 To Len( whole_string$ )
	
		; if an occurance of the separator occurs increase the count
		If ( Mid( whole_string$, i%, 1 ) = character$ )
			count% = count% + 1
		EndIf
	
		; if count and index match (i.e. found the sub-string of intrest)
		If count% = index%
		
			; check if this is the first sub-string
			If Mid( whole_string$, i%, 1 ) = character$
				; if not the first sub-string move mid pointer on 1 char to remove separator
				j% = i% + 1
			Else
				j% = 1	
			EndIf
			
			; sub-string
			sub_string$ = ""
			
			; build the sub-string until another occurance of separator or the string end
			Repeat
				sub_string$ = sub_string$ + Mid( whole_string$, j%, 1 )
				j% = j% + 1
			Until ( Mid( whole_string$, j%, 1 ) = character$ Or j% > Len( whole_string$ ) )
			
			Return sub_string$
		
		EndIf
	Next
End Function
