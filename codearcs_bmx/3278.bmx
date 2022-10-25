; ID: 3278
; Author: Cocopino
; Date: 2016-06-20 04:47:22
; Title: TExportMesh
; Description: Export mesh to b3d file from code

'TExportMesh.bmx
'Conversion of http://www.blitzbasic.com/codearcs/codearcs.php?code=866

Type TExportMesh

	Field mesh:TMesh
	Field b3d_stack:Int[100 + 1]
	Field b3d_file:TStream
	Field b3d_tos:Int

	Field c_surfs:Int
	Field c_surf:TSurface[]
	Field c_brush:TBrush[]
	Field c_tex:TTexture[]
	Field c_tex_name:String[]

	Method Init()

		c_surfs = CountSurfaces(mesh)

		c_surf = New TSurface[c_surfs + 1]
		c_brush = New TBrush[c_surfs + 1]
		c_tex = New TTexture[c_surfs + 1]
		c_tex_name = New String[c_surfs + 1]

		' track down used textures (thanks Mark!)
		For Local i:Int = 1 To c_surfs

			c_surf[i] = GetSurface(mesh, i)
			c_brush[i] = GetSurfaceBrush(c_surf[i])
			c_tex[i] = GetBrushTexture(c_brush[i])

			If c_tex[i] <> Null
				c_tex_name[i] = Lower(TextureName:String(c_tex[i])) ' Full (!) Texture Path
				Local curdir:String = Lower(CurrentDir())
				c_tex_name[i] = Replace(c_tex_name[i], curdir, "") '<<<<<<<<<<<<<<<<<<<
			End If

			Print c_tex_name[i]
			If c_tex_name[i] = "" Then Print "Error: Surface No." + i + " has no Texture"
			If FileType(c_tex_name[i]) <> 1 Then Print "Warning: Surface No." + i + " uses nonexistant Texture (" + c_tex_name[i] + ")."

		Next			
	
	End Method
	
	Method SaveMesh(f_name:String)
	
		Init()

		Local file:TStream = WriteFile(f_name:String)
		Local i:Int
	
		b3dSetFile( file )
		
		b3dBeginChunk( "BB3D" )
			b3dWriteInt( 1 )	'version
	
			b3dBeginChunk( "TEXS" ) ' list all textures used by the mesh
			For i = 1 To c_surfs
				b3dWriteString(c_tex_name[i]) 	'texture file
				b3dWriteInt( 1 )					'flags
				b3dWriteInt( 2 )					'blend
				b3dWriteFloat( 0 )					'x in tex 0 (hu?)
				b3dWriteFloat(0)					'y in tex 0
				b3dWriteFloat( 1 )					'x scale 1
				b3dWriteFloat( 1 )					'y scale 1
				b3dWriteFloat( 0 )					'rotation 0
				
			Next
			b3dEndChunk()	'End of TEXS chunk
	
			
			For i = 1 To c_surfs
				b3dBeginChunk( "BRUS" ) ' describe all brushes used by the mesh
			
				b3dWriteInt( 1 )					'number of textures per brush ; (eg 2 with lightmap)
				b3dWriteString( "brush"+(i-1) )		'brushname
				b3dWriteFloat( 1 )					'red
				b3dWriteFloat( 1 )					'green
				b3dWriteFloat( 1 )					'blue
				b3dWriteFloat( 1 )					'alpha
				b3dWriteFloat( 0 )					'shininess
				b3dWriteInt( 1 )					'blendmode
				b3dWriteInt( 0 )					'FX
				b3dWriteInt( i-1 )					'used texture index 
	'			b3dWriteInt( ? )					;additional texture index (eg lightmap), but here we only use 1 (see above)
	
				b3dEndChunk()	'End of BRUS chunk
			Next
			
			b3dBeginChunk( "NODE" )
				b3dWriteString("entity_name")
				b3dWriteFloat( 0 )	'x_pos
				b3dWriteFloat( 0 )	'y_pos
				b3dWriteFloat( 0 )	'z_pos
				b3dWriteFloat( 1 )	'x_scale
				b3dWriteFloat( 1 )	'y_scale
				b3dWriteFloat( 1 )	'z_scale
				b3dWriteFloat( 1 )	'rot_w
				b3dWriteFloat( 0 )	'rot_x
				b3dWriteFloat( 0 )	'rot_y
				b3dWriteFloat( 0 )	'rot_z
				WriteMESH( mesh )
			b3dEndChunk()	'End of NODE chunk
			
		b3dEndChunk()	'End of BB3D chunk
		
		CloseFile file
		
		
	End Method
	
	Method WriteMESH(mesh:TMesh)
	
		Local n_surfs:Int = CountSurfaces(mesh)
		Local k:Int
		Local surf:TSurface
		
		b3dBeginChunk( "MESH" )
			b3dWriteInt( -1 )				'no 'entity' brush -1
			
			b3dBeginChunk( "VRTS" )
				b3dWriteInt( 0 )			'flags - 0=no normal/color
				b3dWriteInt( 1 )			'number of tex_coord sets (eg: 2 with lightmap)
				b3dWriteInt( 2 )			'coords per set (u,v,w?) 2 with uv, 3 with uvw
				
				For k = 1 To n_surfs
					surf = GetSurface(mesh, k)
					Local n_verts:Int = CountVertices(surf) - 1
					
					For Local j:Int = 0 To n_verts
						b3dWriteFloat( VertexX( surf,j ) )
						b3dWriteFloat( VertexY( surf,j ) )
						b3dWriteFloat( VertexZ( surf,j ) )
						b3dWriteFloat( VertexU#( surf,j,0 ) )
						b3dWriteFloat( VertexV#( surf,j,0 ) )
	'					b3dWriteFloat( VertexW#( surf,j,0 ) )
	';					b3dWriteFloat( VertexU#( surf,j,1 ) ) ; lightmap uv
	';					b3dWriteFloat( VertexV#( surf,j,1 ) ) ; lightmap uv
	'					b3dWriteFloat( VertexW#( surf,j,1 ) )
					Next
				Next
			b3dEndChunk()	'End of VRTS chunk
			
			Local first_vert:Int = 0
			For k = 1 To n_surfs
				surf = GetSurface(mesh, k)
				Local n_tris:Int = CountTriangles(surf) - 1
				
				b3dBeginChunk( "TRIS" )
					b3dWriteInt( k-1 )		'brush For these triangles (surf -1 !!!)
					
					For Local j:Int = 0 To n_tris
						b3dWriteInt( first_vert+TriangleVertex( surf,j,0 ) )
						b3dWriteInt( first_vert+TriangleVertex( surf,j,1 ) )
						b3dWriteInt( first_vert+TriangleVertex( surf,j,2 ) )
					Next
					
				b3dEndChunk()	'End of TRIS chunk
				
				first_vert=first_vert+CountVertices( surf )
				
			Next
			
		b3dEndChunk()	'End of MESH chunk
		
	End Method
	
	
	'-------------------------------------------------------------------------------------------------
	
	
	
	
	'
	'b3d file utils to be included
	'
	
	
	Method b3dSetFile(file:TStream)
		b3d_tos:Int = 0
		b3d_file:TStream = file
	End Method
	
	'***** Methods for reading from B3D files *****
	
	Method b3dReadByte:Byte()
		Return ReadByte( b3d_file )
	End Method
	
	Method b3dReadInt:Int()
		Return ReadInt(b3d_file)
	End Method
	
	Method b3dReadFloat#()
		Return ReadFloat( b3d_file )
	End Method
	
	Method b3dReadString:String()
		Local t:String
		Repeat
			Local ch:Byte = b3dReadByte()
			If ch = 0 Return t:String
			t:String = t:String + Chr:String(ch)
		Forever
	End Method
	
	Method b3dReadChunk$()
		Local tag:String
		For Local k:Int = 1 To 4
			tag = tag + Chr(b3dReadByte())
		Next
		Local sz:Int = ReadInt(b3d_file)
		b3d_tos = b3d_tos + 1
		b3d_stack[b3d_tos] = StreamPos(b3d_file) + sz
		Return tag
		
	End Method
	
	Method b3dExitChunk()
		SeekStream b3d_file, b3d_stack[b3d_tos]
		b3d_tos=b3d_tos-1
	End Method
	
	Method b3dChunkSize:Int()
		Return b3d_stack[b3d_tos] - StreamPos(b3d_file)
	End Method
	
	'***** Methods for writing to B3D files *****
	
	Method b3dWriteByte(n:Int)
		WriteByte(b3d_file, n)
	End Method
	
	Method b3dWriteInt(n:Int)
		WriteInt(b3d_file, n)
	End Method
	
	Method b3dWriteFloat( n# )
		WriteFloat( b3d_file,n )
	End Method
	
	Method b3dWriteString( t$ )
		For Local k:Int = 1 To Len(t:String)
			Local ch:Int = Asc(Mid:String(t:String, k, 1))
			b3dWriteByte(ch)
			If ch=0 Return
		Next
		b3dWriteByte( 0 )
	End Method
	
	Method b3dBeginChunk( tag$ )
		b3d_tos=b3d_tos+1
		For Local k:Int = 1 To 4
			b3dWriteByte(Asc(Mid$( tag$,k,1 )))
		Next
		b3dWriteInt( 0 )
		b3d_stack[b3d_tos] = StreamPos(b3d_file)
	End Method
	
	Method b3dEndChunk()
		Local n:Int = StreamPos(b3d_file)
		SeekStream b3d_file, b3d_stack[b3d_tos] - 4
		b3dWriteInt(n - b3d_stack[b3d_tos])
		SeekStream b3d_file,n
		b3d_tos=b3d_tos-1
	End Method
	

End Type
