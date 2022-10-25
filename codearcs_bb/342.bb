; ID: 342
; Author: elias_t
; Date: 2002-06-12 11:49:45
; Title: Batch Convert .3ds &amp; .x to .b3d
; Description: Batch convert .3ds & .x files in a dir to .b3d

;batch convert .3ds and .x files in a directory to .b3d files
;modified Mark's code

;---------------------
Dim b3d_stack(100)
Global b3d_file,b3d_tos

;---------------------

Graphics3D 640,480,32,2


;/////////////////////////////////
rd=ReadDir(CurrentDir$())

tm$=NextFile$(rd)

While Not tm$=""

If Right$(tm$,4)=".3ds" Or Right$(tm$,4)=".3DS" Or Right$(tm$,2)=".x" Or Right$(tm$,2)=".X"

mesh = LoadMesh  (tm$) 

If Right$(tm$,4)=".3ds" Or Right$(tm$,4)=".3DS" Then tm$=Left$(tm$,Len(tm$)-4)
If Right$(tm$,2)=".x" Or Right$(tm$,2)=".X" Then tm$=Left$(tm$,Len(tm$)-2)

WriteBB3D( tm$+".b3d",mesh )

FreeEntity mesh

Print tm$ + "  Ok"

EndIf

tm$=NextFile$(rd)
Wend


CloseDir rd

End


;/////////////////////////////////////////////////////////////////////////
;////////////////////////////////////////////////////////////////////////


Function WriteBB3D( f_name$,mesh )

	file=WriteFile( f_name$ )

	b3dSetFile( file )
	
	b3dBeginChunk( "BB3D" )
		b3dWriteInt( 1 )	;version
		
		b3dBeginChunk( "BRUS" )
			b3dWriteInt( 0 )					;0 textures per brush
			b3dWriteString( "Brush" )			;brush name
			b3dWriteFloat( 1 )					;red
			b3dWriteFloat( 1 )					;green
			b3dWriteFloat( 1 )					;blue
			b3dWriteFloat( 1 )					;alpha
			b3dWriteFloat( 0 )					;shininess
			b3dWriteInt( 1 )					;blend
			b3dWriteInt( 0 )					;FX
		b3dEndChunk()	;end of BRUS chunk
		
		b3dBeginChunk( "NODE" )
			b3dWriteString( "_" );entity name
			b3dWriteFloat( 0 )	;x_pos
			b3dWriteFloat( 0 )	;y_pos
			b3dWriteFloat( 0 )	;y_pos
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
		b3dWriteInt( -1 )				;no 'entity' brush
		
		b3dBeginChunk( "VRTS" )
			b3dWriteInt( 0 )			;flags - 0=no normal/color
			b3dWriteInt( 1 )			;1 tex_coord sets
			b3dWriteInt( 2 )			;2 coords per set
			
			For k=1 To n_surfs
				surf=GetSurface( mesh,k )
				n_verts=CountVertices( surf )-1
				
				For j=0 To n_verts
					b3dWriteFloat( VertexX( surf,j ) )
					b3dWriteFloat( VertexY( surf,j ) )
					b3dWriteFloat( VertexZ( surf,j ) )
					
					b3dWriteFloat( VertexU#( surf,j ) )
					b3dWriteFloat( VertexV#( surf,j ) )
					
				Next
			Next
		b3dEndChunk()	;end of VRTS chunk
		
		first_vert=0
		For k=1 To n_surfs
			surf=GetSurface( mesh,k )
			n_tris=CountTriangles( surf )-1
			
			b3dBeginChunk( "TRIS" )
				b3dWriteInt( 0 )		;brush for these triangles
				
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


;////////////////////////////////////////////////////////////////////////

;***** Functions for writing to B3D files *****

Function b3dWriteByte( n )
	WriteByte( b3d_file,n )
End Function

Function b3dWriteInt( n )
	WriteInt( b3d_file,n )
End Function

Function b3dWriteFloat( n# )
	WriteFloat( b3d_file,n )
End Function

Function b3dWriteString( t$ )
	For k=1 To Len( t$ )
		ch=Asc(Mid$(t$,k,1))
		b3dWriteByte(ch)
		If ch=0 Return
	Next
	b3dWriteByte( 0 )
End Function

Function b3dBeginChunk( tag$ )
	b3d_tos=b3d_tos+1
	For k=1 To 4
		b3dWriteByte(Asc(Mid$( tag$,k,1 )))
	Next
	b3dWriteInt( 0 )
	b3d_stack(b3d_tos)=FilePos( b3d_file )
End Function

Function b3dEndChunk()
	n=FilePos( b3d_file )
	SeekFile b3d_file,b3d_stack(b3d_tos)-4
	b3dWriteInt( n-b3d_stack(b3d_tos) )
	SeekFile b3d_file,n
	b3d_tos=b3d_tos-1
End Function

;------------------
Function b3dSetFile( file )
	b3d_tos=0
	b3d_file=file
End Function
;----------------------------------
