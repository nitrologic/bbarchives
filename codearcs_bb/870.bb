; ID: 870
; Author: EdzUp[GD]
; Date: 2004-01-02 03:35:26
; Title: Replace Texture
; Description: Replace a texture on a multi-textured mesh

Function ReplaceTexture( Mesh, OldTexture$, NewTexture, Frame=0, LightMapped=1 )
	;this function changes the lightmap on a mesh to a selected lightmap
	Local SurfS=CountSurfaces( Mesh ) 
	Local SurfI=0
	Local BlankBrush = CreateTexture( 64, 64 )
	
	SetBuffer TextureBuffer( BlankBrush )
	ClsColor 255, 255, 255
	Cls
	ClsColor 0, 0, 0
	SetBuffer BackBuffer()
	
	For SurfI=1 To Surfs
		s=GetSurface( Mesh, SurfI ) 
		b=GetSurfaceBrush(s) 
		t0=GetBrushTexture( b, 0 ) 
		t1=GetBrushTexture( b, 1 )
		
		If Upper$( StripPath$( TextureName$( t0 ) ) ) = Upper$( OldTexture$ )
			BrushTexture b, NewTexture, Frame, 0
			If LightMapped=0
				BrushTexture b, BlankBrush, 0, 1
			Else
				BrushTexture b, t1, 0, 1
			EndIf
			PaintSurface s, b
		EndIf 
		
		If t0<>0 Then FreeBrush t0
		If t1<>0 Then FreeBrush t1
		If b<>0 Then FreeBrush b
	Next 
	
	FreeTexture BlankBrush
End Function
