; ID: 866
; Author: jfk EO-11110
; Date: 2003-12-24 01:23:50
; Title: SaveB3D
; Description: Save multisurface textured Mesh as .B3D

Graphics3D 640,480,32,2
SetBuffer BackBuffer()

meshname$="testsurf.3ds" ; Mesh to save... (one that is using textures)

; BTW: you should run this from inside the folder where the Mesh resides, unless you edit the
; Part labeled by "<<<<<<<<<<<<"

Include "b3dfile.bb"
; this file can be found here: http://www.blitzbasic.com/sdkspecs/sdkspecs/b3dfile_utils.zip
cam=CreateCamera()
TranslateEntity cam,0,0,-10
mesh=LoadMesh(meshname$)
Global c_surfs=CountSurfaces(mesh)

Print "Mesh "+meshname$+" has "+ c_surfs+" Surfaces, using the following textures:"

Dim c_surf(c_surfs)
Dim c_brush(c_surfs)
Dim c_tex(c_surfs)
Dim c_tex_name$(c_surfs)

; track down used textures (thanks Mark!)
For i=1 To c_surfs
 c_surf(i)= GetSurface(mesh,i)
 c_brush(i)=GetSurfaceBrush( c_surf(i) )
 c_tex(i)=GetBrushTexture( c_brush(i) )
 c_tex_name$(i)=Lower$(TextureName$( c_tex(i))) ; Full (!) Texture Path
 curdir$=Lower$(CurrentDir$()) 
 c_tex_name$(i)= Replace$(c_tex_name$(i),curdir$,"") ;<<<<<<<<<<<<<<<<<<<
 Print c_tex_name$(i)
 If c_tex_name$(i)="" Then Print "Error: Surface No."+i+" has no Texture"
 If FileType(c_tex_name$(i))<>1 Then Print "Warning: Surface No."+i+" uses nonexistant Texture ("+c_tex_name$(i)+")."
Next

Print "Press any key to save this Mesh as TEMP.B3D"


WaitKey()

; end

WriteBB3D( "temp.b3d",mesh )

For i=1 To c_surfs
 FreeBrush c_brush(i); release memory
 FreeTexture c_tex(i)
Next


; test if it worked...
FreeEntity mesh
mesh2=LoadMesh("temp.b3d")
While Not KeyDown(1)
 TurnEntity mesh2,1,2,3
 RenderWorld()
 Text 0,0,"TEMP.B3D"
 Flip
Wend
End


Function WriteBB3D( f_name$,mesh )

	file=WriteFile( f_name$ )

	b3dSetFile( file )
	
	b3dBeginChunk( "BB3D" )
		b3dWriteInt( 1 )	;version

		b3dBeginChunk( "TEXS" ) ; list all textures used by the mesh
		For i=1 To c_surfs
			b3dWriteString( c_tex_name$(i) ) 	;texture file
			b3dWriteInt( 1 )					;flags
			b3dWriteInt( 2 )					;blend
			b3dWriteFloat( 0 )					;x in tex 0 (hu?)
			b3dWriteFloat( 0 )					;y in tex 0
			b3dWriteFloat( 1 )					;x scale 1
			b3dWriteFloat( 1 )					;y scale 1
			b3dWriteFloat( 0 )					;rotation 0
			
		Next
		b3dEndChunk()	;end of TEXS chunk

		
		For i=1 To c_surfs
			b3dBeginChunk( "BRUS" ) ; describe all brushes used by the mesh
		
			b3dWriteInt( 1 )					;number of textures per brush ; (eg 2 with lightmap)
			b3dWriteString( "brush"+(i-1) )		;brushname
			b3dWriteFloat( 1 )					;red
			b3dWriteFloat( 1 )					;green
			b3dWriteFloat( 1 )					;blue
			b3dWriteFloat( 1 )					;alpha
			b3dWriteFloat( 0 )					;shininess
			b3dWriteInt( 1 )					;blendmode
			b3dWriteInt( 0 )					;FX
			b3dWriteInt( i-1 )					;used texture index 
;			b3dWriteInt( ? )					;additional texture index (eg lightmap), but here we only use 1 (see above)

			b3dEndChunk()	;end of BRUS chunk
		Next
		
		b3dBeginChunk( "NODE" )
			b3dWriteString( "entity_name_here!" )
			b3dWriteFloat( 0 )	;x_pos
			b3dWriteFloat( 0 )	;y_pos
			b3dWriteFloat( 0 )	;z_pos
			b3dWriteFloat( 1 )	;x_scale
			b3dWriteFloat( 1 )	;y_scale
			b3dWriteFloat( 1 )	;z_scale
			b3dWriteFloat( 1 )	;rot_w
			b3dWriteFloat( 0 )	;rot_x
			b3dWriteFloat( 0 )	;rot_y
			b3dWriteFloat( 0 )	;rot_z
			WriteMESH( mesh )
		b3dEndChunk()	;end of NODE chunk
		
	b3dEndChunk()	;end of BB3D chunk
	
	CloseFile file
End Function

Function WriteMESH( mesh )

	n_surfs=CountSurfaces( mesh )
	
	b3dBeginChunk( "MESH" )
		b3dWriteInt( -1 )				;no 'entity' brush -1
		
		b3dBeginChunk( "VRTS" )
			b3dWriteInt( 0 )			;flags - 0=no normal/color
			b3dWriteInt( 1 )			;number of tex_coord sets (eg: 2 with lightmap)
			b3dWriteInt( 2 )			;coords per set (u,v,w?) 2 with uv, 3 with uvw
			
			For k=1 To n_surfs
				surf=GetSurface( mesh,k )
				n_verts=CountVertices( surf )-1
				
				For j=0 To n_verts
					b3dWriteFloat( VertexX( surf,j ) )
					b3dWriteFloat( VertexY( surf,j ) )
					b3dWriteFloat( VertexZ( surf,j ) )
					b3dWriteFloat( VertexU#( surf,j,0 ) )
					b3dWriteFloat( VertexV#( surf,j,0 ) )
;					b3dWriteFloat( VertexW#( surf,j,0 ) )
;;					b3dWriteFloat( VertexU#( surf,j,1 ) ) ; lightmap uv
;;					b3dWriteFloat( VertexV#( surf,j,1 ) ) ; lightmap uv
;					b3dWriteFloat( VertexW#( surf,j,1 ) )
				Next
			Next
		b3dEndChunk()	;end of VRTS chunk
		
		first_vert=0
		For k=1 To n_surfs
			surf=GetSurface( mesh,k )
			n_tris=CountTriangles( surf )-1
			
			b3dBeginChunk( "TRIS" )
				b3dWriteInt( k-1 )		;brush for these triangles (surf -1 !!!)
				
				For j=0 To n_tris
					b3dWriteInt( first_vert+TriangleVertex( surf,j,0 ) )
					b3dWriteInt( first_vert+TriangleVertex( surf,j,1 ) )
					b3dWriteInt( first_vert+TriangleVertex( surf,j,2 ) )
				Next
				
			b3dEndChunk()	;end of TRIS chunk
			
			first_vert=first_vert+CountVertices( surf )
			
		Next
		
	b3dEndChunk()	;end of MESH chunk
	
End Function


;-------------------------------------------------------------------------------------------------
